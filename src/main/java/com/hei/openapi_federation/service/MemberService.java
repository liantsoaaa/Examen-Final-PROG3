package com.hei.openapi_federation.service;

import com.hei.openapi_federation.dto.request.CreateMemberDto;
import com.hei.openapi_federation.dto.response.MemberDto;

import java.util.List;

public interface MemberService {
    List<MemberDto> createMembers(List<CreateMemberDto> requests);
}
