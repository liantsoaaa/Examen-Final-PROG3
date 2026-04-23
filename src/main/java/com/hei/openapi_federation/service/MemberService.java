package com.hei.openapi_federation.service;

import com.hei.openapi_federation.entity.CreateMember;
import com.hei.openapi_federation.entity.Member;
import com.hei.openapi_federation.entity.MemberOccupation;
import com.hei.openapi_federation.exception.BadRequestException;
import com.hei.openapi_federation.repository.MemberRepository;
import com.hei.openapi_federation.repository.SponsorshipRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class MemberService {

    private MemberRepository memberRepository = null;
    private SponsorshipRepository sponsorshipRepository = null;

    public MemberService() {
        this.memberRepository = memberRepository;
        this.sponsorshipRepository = sponsorshipRepository;
    }

    public List<Member> createAll(List<CreateMember> requests) {
        List<Member> results = new ArrayList<>();
        for (CreateMember request : requests) {
            results.add(createOne(request));
        }
        return results;
    }

    private Member createOne(CreateMember request) {

        if (!request.isRegistrationFeePaid()) {
            throw new BadRequestException(
                    "Registration fee of 50,000 MGA must be paid before admission.");
        }

        if (!request.isMembershipDuesPaid()) {
            throw new BadRequestException(
                    "Annual membership dues must be paid in full before admission.");
        }

        List<String> refereeIds = request.getReferees();
        if (refereeIds == null || refereeIds.size() < 2) {
            throw new BadRequestException(
                    "At least 2 confirmed member referees are required for admission.");
        }

        String collectivityId = request.getCollectivityIdentifier();
        if (collectivityId == null || collectivityId.isBlank()) {
            throw new BadRequestException("A target collectivity identifier is required.");
        }
        if (!memberRepository.collectivityExists(collectivityId)) {
            throw new BadRequestException("Collectivity not found: " + collectivityId);
        }

        List<Member> referees = resolveReferees(refereeIds);

        for (String refereeId : refereeIds) {
            if (!memberRepository.isConfirmedMember(refereeId)) {
                throw new BadRequestException(
                        "Referee with id=%s is not a confirmed member.".formatted(refereeId));
            }
        }

        long targetCount = countRefereesFromCollectivity(refereeIds, collectivityId);
        long otherCount = refereeIds.size() - targetCount;
        if (targetCount < otherCount) {
            throw new BadRequestException(
                    "The number of referees from the target collectivity (%d) must be >= referees from other collectivities (%d)."
                            .formatted(targetCount, otherCount));
        }

        Long newMemberId = memberRepository.insert(
                request.getFirstName(),
                request.getLastName(),
                request.getBirthDate(),
                request.getGender().name(),
                request.getAddress(),
                String.valueOf(request.getPhoneNumber()),
                request.getEmail(),
                request.getProfession()
        );

        insertMemberCollectivity(newMemberId, Long.parseLong(collectivityId));

        for (String refereeId : refereeIds) {
            sponsorshipRepository.insert(
                    newMemberId,
                    Long.parseLong(refereeId),
                    Long.parseLong(collectivityId),
                    "unspecified"
            );
        }

        Member response = new Member();
        response.setId(String.valueOf(newMemberId));
        response.setFirstName(request.getFirstName());
        response.setLastName(request.getLastName());
        response.setBirthDate(request.getBirthDate());
        response.setGender(request.getGender());
        response.setAddress(request.getAddress());
        response.setProfession(request.getProfession());
        response.setPhoneNumber(String.valueOf(Integer.parseInt(String.valueOf(request.getPhoneNumber()))));
        response.setEmail(request.getEmail());
        response.setOccupation(MemberOccupation.JUNIOR);
        response.setReferees(referees);

        return response;
    }

    private List<Member> resolveReferees(List<String> refereeIds) {
        List<Member> referees = memberRepository.findByIds(refereeIds);
        if (referees.size() != refereeIds.size()) {
            List<String> foundIds = new ArrayList<>();
            Function<? super Member, ? extends String> mapper = (Function<? super Member, ? extends String>)
                    member -> member.getId().toString();
            for (Member referee : referees) {
                String string = mapper.apply(referee);
                foundIds.add(string);
            }
            String missing = refereeIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("unknown");
            throw new BadRequestException("Referee member(s) not found: " + missing);
        }
        return referees;
    }

    private long countRefereesFromCollectivity(List<String> refereeIds, String collectivityId) {
        return refereeIds.stream()
                .filter(refereeId -> {
                    Optional<Long> currentCollId = memberRepository.getCurrentCollectivityId(refereeId);
                    return currentCollId.isPresent()
                            && currentCollId.get().equals(Long.parseLong(collectivityId));
                })
                .count();
    }

    private void insertMemberCollectivity(Long memberId, Long collectivityId) {
        memberRepository.insertAsJunior(memberId, collectivityId);
    }
}