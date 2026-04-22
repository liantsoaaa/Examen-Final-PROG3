package com.openapi_federation.controller;

import com.openapi_federation.entity.CreateMember;
import com.openapi_federation.entity.Member;
import com.openapi_federation.service.MemberService;
import org.openapi_springframework.http.HttpStatus;
import org.openapi_springframework.http.ResponseEntity;
import org.openapi_springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<List<Member>> createMembers(
            @RequestBody List<CreateMember> requests) {
        List<Member> created = memberService.createAll(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}