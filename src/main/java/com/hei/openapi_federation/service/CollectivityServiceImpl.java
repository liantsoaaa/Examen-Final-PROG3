package com.hei.openapi_federation.service;

import com.hei.openapi_federation.entity.Collectivity;
import com.hei.openapi_federation.entity.CollectivityStructure;
import com.hei.openapi_federation.entity.Member;
import com.hei.openapi_federation.exception.BadRequestException;
import com.hei.openapi_federation.repository.CollectivityRepository;
import com.hei.openapi_federation.repository.MemberRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CollectivityServiceImpl extends CollectivityService {

    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;

    public CollectivityServiceImpl(CollectivityRepository collectivityRepository,
                                   MemberRepository memberRepository) {
        super();
        this.collectivityRepository = collectivityRepository;
        this.memberRepository = memberRepository;
    }

        public List<String> createCollectivities(List<CreateCollectivityDto> requests) {
        List<String> results = new ArrayList<>();
        for (CreateCollectivityDto req : requests) {
            results.add(createOneCollectivity(req));
        }
        return results;
    }

    private String createOneCollectivity(CreateCollectivityDto req) {


        if (!req.isFederationApproval()) {
            throw new BadRequestException("Federation approval is required.");
        }


        if (req.getStructure() == null) {
            throw new BadRequestException(
                    "Collectivity structure (president, vice-president, treasurer, secretary) is required.");
        }


        List<String> memberIds = req.getMembers() != null ? req.getMembers() : List.of();
        List<Member> members = memberIds.stream()
                .map(id -> memberRepository.findById(id)
                        .orElseThrow(() -> new BadRequestException("Member not found: " + id)))
                .collect(Collectors.toList());


        if (members.size() < 10) {
            throw new BadRequestException("A collectivity requires at least 10 members.");
        }


        long seniorEnough = members.stream()
                .filter(m -> m.getMembershipDate() != null
                        && m.getMembershipDate().isBefore(Instant.from(LocalDate.now().minusMonths(6))))
                .count();
        if (seniorEnough < 5) {
            throw new BadRequestException(
                    "At least 5 members must have at least 6 months of membership.");
        }


        CollectivityStructure structure = resolveStructure(req.getStructure());

        Collectivity collectivity = new Collectivity();
        collectivity.setId(UUID.randomUUID().toString());
        collectivity.setLocation(req.getLocation());
        collectivity.setFederationApproval(req.isFederationApproval());
        collectivity.setStructure(structure);
        collectivity.setMembers(members);

        collectivity = collectivityRepository.save(collectivity);


        String collectivityId = collectivity.getId();
        for (Member m : members) {
            memberRepository.updateCollectivityId(m.getId(), collectivityId);
        }

        collectivity.setMembers(members);
        Object CollectivityMapper = null;
        return CollectivityMapper.toString();
    }

    private CollectivityStructure resolveStructure(CreateCollectivityStructureDto dto) {
        CollectivityStructure structure = new CollectivityStructure();
        structure.setPresident(resolveMember(dto.getPresident(), "President"));
        structure.setVicePresident(resolveMember(dto.getVicePresident(), "Vice-president"));
        structure.setTreasurer(resolveMember(dto.getTreasurer(), "Treasurer"));
        structure.setSecretary(resolveMember(dto.getSecretary(), "Secretary"));
        return structure;
    }

    private Member resolveMember(String id, String role) {
        if (id == null) {
            throw new BadRequestException(role + " is required in structure.");
        }
        return memberRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(role + " not found: " + id));
    }
}