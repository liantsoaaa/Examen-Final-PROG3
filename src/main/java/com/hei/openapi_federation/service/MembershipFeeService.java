package com.hei.openapi_federation.service;

import com.hei.openapi_federation.entity.*;
import com.hei.openapi_federation.exception.BadRequestException;
import com.hei.openapi_federation.repository.CollectivityRepository;
import com.hei.openapi_federation.repository.MembershipFeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class MembershipFeeService {

    private final MembershipFeeRepository membershipFeeRepository;
    private final CollectivityRepository collectivityRepository;

    public MembershipFeeService(MembershipFeeRepository membershipFeeRepository,
                                CollectivityRepository collectivityRepository) {
        this.membershipFeeRepository = membershipFeeRepository;
        this.collectivityRepository  = collectivityRepository;
    }


    public List<MembershipFee> getByCollectivity(String collectivityId) {
        Long id = parseId(collectivityId);
        assertCollectivityExists(id);
        return membershipFeeRepository.findByCollectivityId(id);
    }


    public List<MembershipFee> create(String collectivityId, List<CreateMembershipFee> requests) {
        Long id = parseId(collectivityId);
        assertCollectivityExists(id);

        List<MembershipFee> results = new ArrayList<>();
        for (CreateMembershipFee req : requests) {

            if (req.getAmount() == null || req.getAmount() <= 0) {
                throw new BadRequestException("Membership fee amount must be greater than 0.");
            }

            if (null != req.getFrequency()) {
                Long feeId = membershipFeeRepository.insert(
                        id,
                        req.getLabel() != null ? req.getLabel() : String.valueOf(req.getFrequency()),
                        String.valueOf(req.getFrequency()),
                        req.getAmount(),
                        req.getEligibleFrom()
                );

                MembershipFee fee = new MembershipFee();
                fee.setId(Integer.parseInt(String.valueOf(feeId)));
                fee.setLabel(req.getLabel());
                fee.setFrequency(req.getFrequency());
                fee.setAmount(req.getAmount());
                fee.setEligibleFrom(String.valueOf(req.getEligibleFrom()));
                fee.setStatus(ActivityStatus.ACTIVE);
                results.add(fee);
            } else {
                throw new BadRequestException("Unrecognized or missing frequency value.");
            }

        }
        return results;
    }

    private void assertCollectivityExists(Long id) {
        collectivityRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        HttpStatus.NOT_FOUND, "Collectivity not found: " + id));
    }

    private Long parseId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException(HttpStatus.NOT_FOUND, "Invalid collectivity id: " + id);
        }
    }
}