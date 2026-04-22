package com.hei.openapi_federation.controller;

import com.hei.openapi_federation.entity.CreateMember;
import com.hei.openapi_federation.entity.Member;
import com.hei.openapi_federation.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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