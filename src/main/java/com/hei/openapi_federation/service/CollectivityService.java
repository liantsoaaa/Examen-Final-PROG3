package com.hei.openapi_federation.service;

import com.hei.openapi_federation.entity.*;
import com.hei.openapi_federation.exception.BadRequestException;
import com.hei.openapi_federation.exception.NotFoundException;
import com.hei.openapi_federation.repository.CollectivityRepository;
import com.hei.openapi_federation.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CollectivityService {

    private static final int MIN_TOTAL_MEMBERS = 10;
    private static final int MIN_SENIOR_MEMBERS = 5;
    private static final int SENIORITY_MONTHS = 6;

    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;

    public CollectivityService(CollectivityRepository collectivityRepository,
                               MemberRepository memberRepository) {
        this.collectivityRepository = collectivityRepository;
        this.memberRepository = memberRepository;
    }

    public List<Collectivity> createAll(List<CreateCollectivity> requests) {
        List<Collectivity> results = new ArrayList<>();
        for (CreateCollectivity request : requests) {
            results.add(createOne(request));
        }
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

        List<Long> memberLongIds = allMemberIds.stream()
                .map(Long::parseLong)
                .toList();

        int seniorCount = collectivityRepository.countMembersWithSeniorityInFederation(
                memberLongIds, SENIORITY_MONTHS);
        if (seniorCount < MIN_SENIOR_MEMBERS) {
            throw new BadRequestException(
                    "At least %d members must have at least %d months of seniority in the federation; found %d."
                            .formatted(MIN_SENIOR_MEMBERS, SENIORITY_MONTHS, seniorCount));
        }

        Long federationId = collectivityRepository.getOrCreateFederationId();
        Long cityId = collectivityRepository.findOrCreateCity(request.getLocation());
        String number = collectivityRepository.generateCollectivityNumber();

        String name = "Collectivity " + number;
        String speciality = "General";

        Long collectivityId = collectivityRepository.insertCollectivity(
                number, name, speciality, federationId, cityId);

        assignStructurePost(collectivityId, structure.getPresident(), "PRESIDENT");
        assignStructurePost(collectivityId, structure.getVicePresident(), "DEPUTY_PRESIDENT");
        assignStructurePost(collectivityId, structure.getTreasurer(), "TREASURER");
        assignStructurePost(collectivityId, structure.getSecretary(), "SECRETARY");

        List<String> structureIds = List.of(
                structure.getPresident(),
                structure.getVicePresident(),
                structure.getTreasurer(),
                structure.getSecretary()
        );
        for (String memberId : allMemberIds) {
            if (!structureIds.contains(memberId)) {
                collectivityRepository.insertMemberCollectivity(
                        Long.parseLong(memberId), collectivityId, "JUNIOR");
            }
        }

        return buildResponse(String.valueOf(collectivityId), request.getLocation(),
                structure, resolvedMembers, structureIds);
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
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("unknown");
            throw new NotFoundException("Member(s) not found: " + missing);
        }
        return members;
    }

    private void assignStructurePost(Long collectivityId, String memberId, String postName) {
        collectivityRepository.insertMemberCollectivity(
                Long.parseLong(memberId), collectivityId, postName);
    }

    private Collectivity buildResponse(String collectivityId, String location,
                                       CreateCollectivityStructure createStructure,
                                       List<Member> allMembers,
                                       List<String> structureIds) {
        
        java.util.Map<String, Member> memberMap = new java.util.HashMap<>();
        for (Member m : allMembers) memberMap.put(m.getId(), m);

        CollectivityStructure structure = new CollectivityStructure();
        structure.setPresident(memberMap.get(createStructure.getPresident()));
        structure.setVicePresident(memberMap.get(createStructure.getVicePresident()));
        structure.setTreasurer(memberMap.get(createStructure.getTreasurer()));
        structure.setSecretary(memberMap.get(createStructure.getSecretary()));

        List<Member> memberList = allMembers.stream().toList();

        Collectivity collectivity = new Collectivity();
        collectivity.setId(collectivityId);
        collectivity.setLocation(location);
        collectivity.setStructure(structure);
        collectivity.setMembers(memberList);

        return collectivity;
    }
}



