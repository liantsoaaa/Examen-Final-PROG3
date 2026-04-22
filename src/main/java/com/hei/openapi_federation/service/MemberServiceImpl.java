package com.hei.openapi_federation.service;


import com.hei.openapi_federation.entity.Member;
import com.hei.openapi_federation.entity.MemberOccupation;
import com.hei.openapi_federation.exception.BadRequestException;
import com.hei.openapi_federation.repository.CollectivityRepository;
import com.hei.openapi_federation.repository.MemberRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MemberServiceImpl extends MemberService {

    private final MemberRepository memberRepository;
    private final CollectivityRepository collectivityRepository;

    public MemberServiceImpl(MemberRepository memberRepository,
                             CollectivityRepository collectivityRepository) {
        super( );
        this.memberRepository = memberRepository;
        this.collectivityRepository = collectivityRepository;
    }

    public List<String> createMembers(List<CreateMemberDto> requests) {
        List<String> results = new ArrayList<>();
        for (CreateMemberDto req : requests) {
            results.add(createOneMember(req));
        }
        return results;
    }

    private String createOneMember(CreateMemberDto req) {


        if (!req.isRegistrationFeePaid()) {
            throw new BadRequestException("Registration fee not paid.");
        }
        if (!req.isMembershipDuesPaid()) {
            throw new BadRequestException("Membership dues not paid.");
        }


        String targetCollectivityId = req.getCollectivityIdentifier();
        if (targetCollectivityId != null) {
            collectivityRepository.findById(Long.valueOf(targetCollectivityId))
                    .orElseThrow(() -> new BadRequestException(
                            "Collectivity not found: " + targetCollectivityId));
        }


        List<String> refereeIds = req.getReferees();
        if (refereeIds == null || refereeIds.size() < 2) {
            throw new BadRequestException("At least two confirmed referees are required.");
        }


        List<Member> referees = refereeIds.stream()
                .map(id -> memberRepository.findById(id)
                        .orElseThrow(() -> new BadRequestException("Referee not found: " + id)))
                .collect(Collectors.toList());


        boolean allConfirmed = referees.stream()
                .allMatch(r -> r.getOccupation() != MemberOccupation.JUNIOR);
        if (!allConfirmed) {
            throw new BadRequestException(
                    "All referees must be confirmed members (not JUNIOR).");
        }


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
        Object MemberMapper = null;
        return MemberMapper.toString();
    }
}