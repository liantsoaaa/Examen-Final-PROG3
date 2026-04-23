package com.hei.openapi_federation.controller;

import com.hei.openapi_federation.entity.CreateMemberPayment;
import com.hei.openapi_federation.entity.MemberPayment;
import com.hei.openapi_federation.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberPaymentController {

    private final PaymentService paymentService;

    public MemberPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<List<MemberPayment>> createPayments(
            @PathVariable String id,
            @RequestBody List<CreateMemberPayment> requests) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.createPayments(id, requests));
    }
}