package com.hei.openapi_federation.service;

import com.hei.openapi_federation.dto.request.CreateCollectivityDto;
import com.hei.openapi_federation.dto.request.CreateCollectivityStructureDto;
import com.hei.openapi_federation.dto.response.CollectivityDto;
import com.hei.openapi_federation.entity.Collectivity;
import com.hei.openapi_federation.entity.CollectivityStructure;
import com.hei.openapi_federation.entity.Member;
import com.hei.openapi_federation.exception.BadRequestException;
import com.hei.openapi_federation.mapper.CollectivityMapper;
import com.hei.openapi_federation.repository.CollectivityRepository;
import com.hei.openapi_federation.repository.MemberRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CollectivityServiceImpl implements CollectivityService {

    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;

    public CollectivityServiceImpl(CollectivityRepository collectivityRepository,
                                   MemberRepository memberRepository) {
        this.collectivityRepository = collectivityRepository;
        this.memberRepository = memberRepository;
    }

    public List<CollectivityDto> createCollectivities(List<CreateCollectivityDto> requests) {
        List<CollectivityDto> results = new ArrayList<>();
        for (CreateCollectivityDto req : requests) {
            results.add(createOneCollectivity(req));
        }
        return results;
    }

    private CollectivityDto createOneCollectivity(CreateCollectivityDto req) {

        // Rule A: federation approval required
        if (!req.isFederationApproval()) {
            throw new BadRequestException("Federation approval is required.");
        }

        // Rule A: structure required
        if (req.getStructure() == null) {
            throw new BadRequestException(
                    "Collectivity structure (president, vice-president, treasurer, secretary) is required.");
        }

        // Resolve all members
        List<String> memberIds = req.getMembers() != null ? req.getMembers() : List.of();
        List<Member> members = memberIds.stream()
                .map(id -> memberRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Member not found: " + id)))
                .collect(Collectors.toList());

        // Rule A: at least 10 members
        if (members.size() < 10) {
            throw new BadRequestException("A collectivity requires at least 10 members.");
        }

        // Rule A: at least 5 members with 6+ months of membership
        long seniorEnough = members.stream()
                .filter(m -> m.getMembershipDate() != null
                        && m.getMembershipDate().isBefore(LocalDate.now().minusMonths(6)))
                .count();
        if (seniorEnough < 5) {
            throw new BadRequestException(
                    "At least 5 members must have at least 6 months of membership.");
        }

        // Resolve structure
        CollectivityStructure structure = resolveStructure(req.getStructure());

        // Save collectivity
        Collectivity collectivity = new Collectivity();
        collectivity.setId(UUID.randomUUID().toString());
        collectivity.setLocation(req.getLocation());
        collectivity.setFederationApproval(req.isFederationApproval());
        collectivity.setStructure(structure);
        collectivity.setMembers(members);

        collectivity = collectivityRepository.save(collectivity);

        // Update each member's collectivity_id
        String collectivityId = collectivity.getId();
        for (Member m : members) {
            memberRepository.updateCollectivityId(m.getId(), collectivityId);
        }

        collectivity.setMembers(members);
        return CollectivityMapper.toDto(collectivity);
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
                .orElseThrow(() -> new NotFoundException(role + " not found: " + id));
    }
}