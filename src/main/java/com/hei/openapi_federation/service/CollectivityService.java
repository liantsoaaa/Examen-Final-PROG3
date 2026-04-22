package com.hei.openapi_federation.service;

import com.hei.openapi_federation.entity.*;
import com.hei.openapi_federation.exception.BadRequestException;
import com.hei.openapi_federation.exception.ConflictException;
import com.hei.openapi_federation.exception.NotFoundException;
import com.hei.openapi_federation.repository.CollectivityRepository;
import com.hei.openapi_federation.repository.CollectivityRepository.CollectivityRow;
import com.hei.openapi_federation.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class CollectivityService {

    private static final int MIN_TOTAL_MEMBERS = 10;
    private static final int MIN_SENIOR_MEMBERS = 5;
    private static final int SENIORITY_MONTHS = 6;


    private static final String PENDING_MARKER = "__PENDING__";

    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;

    public CollectivityService(CollectivityRepository collectivityRepository,
                               MemberRepository memberRepository) {
        this.collectivityRepository = collectivityRepository;
        this.memberRepository = memberRepository;
    }


    public List<Collectivity> createAll(List<CreateCollectivity> requests) {
        List<Collectivity> results = new ArrayList<>();
        for (CreateCollectivity request : requests) results.add(createOne(request));
        return results;
    }

    private Collectivity createOne(CreateCollectivity request) {

        if (!request.isFederationApproval()) {
            throw new BadRequestException("Federation approval is required to open a new collectivity.");
        }

        CreateCollectivityStructure structure = request.getStructure();
        if (structure == null
                || structure.getPresident() == null
                || structure.getVicePresident() == null
                || structure.getTreasurer() == null
                || structure.getSecretary() == null) {
            throw new BadRequestException(
                    "Collectivity structure is incomplete. President, vice-president, treasurer and secretary are all required.");
        }

        List<String> allMemberIds = buildAllMemberIds(request);

        if (allMemberIds.size() < MIN_TOTAL_MEMBERS) {
            throw new BadRequestException(
                    "A new collectivity requires at least %d members; only %d provided."
                            .formatted(MIN_TOTAL_MEMBERS, allMemberIds.size()));
        }

        List<Member> resolvedMembers = resolveMembers(allMemberIds);

        List<Long> memberLongIds = allMemberIds.stream().map(Long::parseLong).toList();

        int seniorCount = collectivityRepository.countMembersWithSeniorityInFederation(
                memberLongIds, SENIORITY_MONTHS);
        if (seniorCount < MIN_SENIOR_MEMBERS) {
            throw new BadRequestException(
                    "At least %d members must have at least %d months of seniority; found %d."
                            .formatted(MIN_SENIOR_MEMBERS, SENIORITY_MONTHS, seniorCount));
        }

        Long federationId = collectivityRepository.getOrCreateFederationId();
        Long cityId       = collectivityRepository.findOrCreateCity(request.getLocation());


        String tempNumber = PENDING_MARKER + System.currentTimeMillis();
        String tempName   = PENDING_MARKER + System.currentTimeMillis();

        Long collectivityId = collectivityRepository.insertCollectivity(
                tempNumber, tempName, "General", federationId, cityId);

        List<String> structureIds = List.of(
                structure.getPresident(),
                structure.getVicePresident(),
                structure.getTreasurer(),
                structure.getSecretary()
        );

        collectivityRepository.insertMemberCollectivity(
                Long.parseLong(structure.getPresident()),    collectivityId, "PRESIDENT");
        collectivityRepository.insertMemberCollectivity(
                Long.parseLong(structure.getVicePresident()), collectivityId, "DEPUTY_PRESIDENT");
        collectivityRepository.insertMemberCollectivity(
                Long.parseLong(structure.getTreasurer()),    collectivityId, "TREASURER");
        collectivityRepository.insertMemberCollectivity(
                Long.parseLong(structure.getSecretary()),    collectivityId, "SECRETARY");

        for (String memberId : allMemberIds) {
            if (!structureIds.contains(memberId)) {
                collectivityRepository.insertMemberCollectivity(
                        Long.parseLong(memberId), collectivityId, "JUNIOR");
            }
        }

        return buildResponse(String.valueOf(collectivityId), request.getLocation(),
                null, null, structure, resolvedMembers, structureIds);
    }



    public Collectivity assignIdentity(String collectivityId, AssignCollectivityIdentity request) {


        Long id = parseLongId(collectivityId);
        CollectivityRow row = collectivityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Collectivity not found: " + collectivityId));


        if (request.getNumber() == null || request.getNumber().isBlank()) {
            throw new BadRequestException("Field 'number' is required.");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Field 'name' is required.");
        }


        if (row.number != null && !row.number.startsWith(PENDING_MARKER)) {
            throw new ConflictException(
                    "This collectivity already has the number '%s' assigned. It cannot be changed."
                            .formatted(row.number));
        }


        if (row.name != null && !row.name.startsWith(PENDING_MARKER)) {
            throw new ConflictException(
                    "This collectivity already has the name '%s' assigned. It cannot be changed."
                            .formatted(row.name));
        }


        if (collectivityRepository.nameExistsForOther(request.getName(), id)) {
            throw new ConflictException(
                    "The name '%s' is already used by another collectivity."
                            .formatted(request.getName()));
        }

        // Rule 6 — number must be unique across all other collectivities
        if (collectivityRepository.numberExistsForOther(request.getNumber(), id)) {
            throw new ConflictException(
                    "The number '%s' is already used by another collectivity."
                            .formatted(request.getNumber()));
        }


        collectivityRepository.updateNumberAndName(id, request.getNumber(), request.getName());


        List<Member> members = collectivityRepository.findMembersByCollectivityId(id);


        Member president    = members.stream().filter(m -> m.getOccupation() == MemberOccupation.PRESIDENT).findFirst().orElse(null);
        Member vicePresident= members.stream().filter(m -> m.getOccupation() == MemberOccupation.VICE_PRESIDENT).findFirst().orElse(null);
        Member treasurer    = members.stream().filter(m -> m.getOccupation() == MemberOccupation.TREASURER).findFirst().orElse(null);
        Member secretary    = members.stream().filter(m -> m.getOccupation() == MemberOccupation.SECRETARY).findFirst().orElse(null);

        CollectivityStructure structure = new CollectivityStructure();
        structure.setPresident(president);
        structure.setVicePresident(vicePresident);
        structure.setTreasurer(treasurer);
        structure.setSecretary(secretary);

        Collectivity response = new Collectivity();
        response.setId(collectivityId);
        response.setNumber(request.getNumber());
        response.setName(request.getName());
        response.setLocation(row.cityName);
        response.setStructure(structure);
        response.setMembers(members);

        return response;
    }



    private List<String> buildAllMemberIds(CreateCollectivity request) {
        List<String> ids = new ArrayList<>();
        if (request.getMembers() != null) ids.addAll(request.getMembers());
        CreateCollectivityStructure s = request.getStructure();
        addIfAbsent(ids, s.getPresident());
        addIfAbsent(ids, s.getVicePresident());
        addIfAbsent(ids, s.getTreasurer());
        addIfAbsent(ids, s.getSecretary());
        return ids;
    }

    private void addIfAbsent(List<String> ids, String id) {
        if (id != null && !ids.contains(id)) ids.add(id);
    }

    private List<Member> resolveMembers(List<String> ids) {
        List<Member> members = memberRepository.findByIds(ids);
        if (members.size() != ids.size()) {
            List<String> foundIds = members.stream().map(Member::getId).toList();
            String missing = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .reduce((a, b) -> a + ", " + b).orElse("unknown");
            throw new NotFoundException("Member(s) not found: " + missing);
        }
        return members;
    }

    private Collectivity buildResponse(String collectivityId, String location,
                                       String number, String name,
                                       CreateCollectivityStructure createStructure,
                                       List<Member> allMembers,
                                       List<String> structureIds) {
        Map<String, Member> memberMap = new java.util.HashMap<>();
        for (Member m : allMembers) memberMap.put(m.getId(), m);

        CollectivityStructure structure = new CollectivityStructure();
        structure.setPresident(memberMap.get(createStructure.getPresident()));
        structure.setVicePresident(memberMap.get(createStructure.getVicePresident()));
        structure.setTreasurer(memberMap.get(createStructure.getTreasurer()));
        structure.setSecretary(memberMap.get(createStructure.getSecretary()));

        Collectivity collectivity = new Collectivity();
        collectivity.setId(collectivityId);
        collectivity.setNumber(number);
        collectivity.setName(name);
        collectivity.setLocation(location);
        collectivity.setStructure(structure);
        collectivity.setMembers(allMembers);

        return collectivity;
    }

    private Long parseLongId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new NotFoundException("Invalid collectivity id: " + id);
        }
    }
}