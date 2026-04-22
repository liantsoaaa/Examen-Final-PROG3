package com.hei.openapi_federation.service;

import com.hei.openapi_federation.dto.request.CreateCollectivityDto;
import com.hei.openapi_federation.dto.response.CollectivityDto;

import java.util.List;

public interface CollectivityService {
    List<CollectivityDto> createCollectivities(List<CreateCollectivityDto> requests);
}
