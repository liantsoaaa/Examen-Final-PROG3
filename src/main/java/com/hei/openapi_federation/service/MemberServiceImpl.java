package com.hei.openapi_federation.service;

import com.hei.openapi_federation.dto.request.CreateMemberDto;
import com.hei.openapi_federation.dto.response.MemberDto;
import com.hei.openapi_federation.entity.Member;
import com.hei.openapi_federation.entity.MemberOccupation;
import com.hei.openapi_federation.exception.BadRequestException;
import com.hei.openapi_federation.mapper.MemberMapper;
import com.hei.openapi_federation.repository.CollectivityRepository;
import com.hei.openapi_federation.repository.MemberRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final CollectivityRepository collectivityRepository;

    public MemberServiceImpl(MemberRepository memberRepository,
                             CollectivityRepository collectivityRepository) {
        this.memberRepository = memberRepository;
        this.collectivityRepository = collectivityRepository;
    }

    public List<MemberDto> createMembers(List<CreateMemberDto> requests) {
        List<MemberDto> results = new ArrayList<>();
        for (CreateMemberDto req : requests) {
            results.add(createOneMember(req));
        }
        return results;
    }

    private MemberDto createOneMember(CreateMemberDto req) {

        // Rule B-2: fees must be paid
        if (!req.isRegistrationFeePaid()) {
            throw new BadRequestException("Registration fee not paid.");
        }
        if (!req.isMembershipDuesPaid()) {
            throw new BadRequestException("Membership dues not paid.");
        }

        // Verify target collectivity exists
        String targetCollectivityId = req.getCollectivityIdentifier();
        if (targetCollectivityId != null) {
            collectivityRepository.findById(targetCollectivityId)
                    .orElseThrow(() -> new NotFoundException(
                            "Collectivity not found: " + targetCollectivityId));
        }

        // Rule B-2: at least 2 referees
        List<String> refereeIds = req.getReferees();
        if (refereeIds == null || refereeIds.size() < 2) {
            throw new BadRequestException("At least two confirmed referees are required.");
        }

        // Resolve referees
        List<Member> referees = refereeIds.stream()
                .map(id -> memberRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Referee not found: " + id)))
                .collect(Collectors.toList());

        // Rule B-2: all referees must be confirmed (not JUNIOR)
        boolean allConfirmed = referees.stream()
                .allMatch(r -> r.getOccupation() != MemberOccupation.JUNIOR);
        if (!allConfirmed) {
            throw new BadRequestException(
                    "All referees must be confirmed members (not JUNIOR).");
        }

        // Rule B-2: internal referees >= external referees
        if (targetCollectivityId != null) {
            long internal = referees.stream()
                    .filter(r -> targetCollectivityId.equals(r.getCollectivityId()))
                    .count();
            long external = referees.size() - internal;
            if (internal < external) {
                throw new BadRequestException(
                        "Number of referees from target collectivity must be >= referees from other collectivities.");
            }
        }

        // Build and save member
        Member member = new Member();
        member.setId(UUID.randomUUID().toString());
        member.setFirstName(req.getFirstName());
        member.setLastName(req.getLastName());
        member.setBirthDate(req.getBirthDate());
        member.setGender(req.getGender());
        member.setAddress(req.getAddress());
        member.setProfession(req.getProfession());
        member.setPhoneNumber(req.getPhoneNumber());
        member.setEmail(req.getEmail());
        member.setOccupation(MemberOccupation.JUNIOR);
        member.setCollectivityId(targetCollectivityId);
        member.setMembershipDate(LocalDate.now());
        member.setReferees(referees);

        member = memberRepository.save(member);
        return MemberMapper.toDto(member);
    }
}