package com.hei.openapi_federation.controller;

import com.hei.openapi_federation.entity.*;
import com.hei.openapi_federation.service.CollectivityService;
import com.hei.openapi_federation.service.MembershipFeeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/collectivities")
public class CollectivityController {

    private final CollectivityService  collectivityService;
    private final MembershipFeeService membershipFeeService;

    public CollectivityController(CollectivityService collectivityService,
                                  MembershipFeeService membershipFeeService) {
        this.collectivityService  = collectivityService;
        this.membershipFeeService = membershipFeeService;
    }


    @PostMapping
    public ResponseEntity<List<Collectivity>> createCollectivities(
            @RequestBody List<CreateCollectivity> requests) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(collectivityService.createAll(requests));
    }


    @PutMapping("/{id}/informations")
    public ResponseEntity<Collectivity> updateInformations(
            @PathVariable String id,
            @RequestBody CollectivityInformation request) {
        return ResponseEntity.ok(collectivityService.updateInformations(id, request));
    }


    @GetMapping("/{id}/membershipFees")
    public ResponseEntity<List<MembershipFee>> getMembershipFees(
            @PathVariable String id) {
        return ResponseEntity.ok(membershipFeeService.getByCollectivity(id));
    }


    @PostMapping("/{id}/membershipFees")
    public ResponseEntity<List<MembershipFee>> createMembershipFees(
            @PathVariable String id,
            @RequestBody List<CreateMembershipFee> requests) {
        return ResponseEntity.ok(membershipFeeService.create(id, requests));
    }


    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<CollectivityTransaction>> getTransactions(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(collectivityService.getTransactions(id, from, to));
    }
}