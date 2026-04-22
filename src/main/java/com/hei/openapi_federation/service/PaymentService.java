package com.hei.openapi_federation.service;

import com.hei.openapi_federation.entity.*;
import com.hei.openapi_federation.exception.BadRequestException;
import com.hei.openapi_federation.repository.MemberRepository;
import com.hei.openapi_federation.repository.MembershipFeeRepository;
import com.hei.openapi_federation.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final MembershipFeeRepository membershipFeeRepository;

    public PaymentService(PaymentRepository paymentRepository, MemberRepository memberRepository, MembershipFeeRepository membershipFeeRepository) {
        this.paymentRepository       = paymentRepository;
        this.memberRepository        = memberRepository;
        this.membershipFeeRepository = membershipFeeRepository;
    }

    public List<MemberPayment> createPayments(String memberId, List<CreateMemberPayment> requests) {
        Long mId = parseId(memberId, "member");
        memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(
                        HttpStatus.NOT_FOUND, "Member not found: " + memberId));

        Long collectivityId = paymentRepository.getCollectivityIdForMember(mId)
                .orElseThrow(() -> new BadRequestException(
                        "Member " + memberId + " does not belong to any active collectivity."));

        List<MemberPayment> results = new ArrayList<>();

        for (CreateMemberPayment req : requests) {
            if (req.getAmount() == null || req.getAmount() <= 0) {
                throw new BadRequestException("Payment amount must be more than 0.");
            }
            if (req.getPaymentMode() == null) {
                throw new BadRequestException("Payment mode is required.");
            }

            Long accountId = parseId(req.getAccountCreditedIdentifier(), "account");
            FinancialAccount account = paymentRepository.findAccountById(accountId)
                    .orElseThrow(() -> new BadRequestException(
                            HttpStatus.NOT_FOUND,
                            "Account not found: " + req.getAccountCreditedIdentifier()));

            if (!paymentRepository.accountBelongsToMemberCollectivity(accountId, mId)) {
                throw new BadRequestException(
                        "Account " + accountId + " does not belong to the member's collectivity.");
            }

            Long feeId = null;
            if (req.getMembershipFeeIdentifier() != null
                    && !req.getMembershipFeeIdentifier().isBlank()) {
                feeId = parseId(req.getMembershipFeeIdentifier(), "membership fee");
                membershipFeeRepository.findById(feeId)
                        .orElseThrow(() -> new BadRequestException(
                                HttpStatus.NOT_FOUND,
                                "Membership fee not found: " + req.getMembershipFeeIdentifier()));
            }

            Long paymentId = paymentRepository.insertPayment(
                    mId,
                    collectivityId,
                    feeId,
                    accountId,
                    req.getAmount(),
                    req.getPaymentMode().toDbPaymentMode(),
                    mId 
            );

            paymentRepository.insertAccountMovement(accountId, req.getAmount());

            MemberPayment payment = new MemberPayment();
            payment.setId(String.valueOf(paymentId));
            payment.setAmount(req.getAmount());
            payment.setPaymentMode(req.getPaymentMode());
            payment.setAccountCredited(account);
            payment.setCreationDate(LocalDate.now());

            results.add(payment);
        }

        return results;
    }

    private Long parseId(String id, String label) {
        if (id == null || id.isBlank()) {
            throw new BadRequestException("Missing " + label + " identifier.");
        }
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException(
                    HttpStatus.NOT_FOUND, "Invalid " + label + " id: " + id);
        }
    }
}