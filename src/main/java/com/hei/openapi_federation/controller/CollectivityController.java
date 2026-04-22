package com.hei.openapi_federation.controller;


import com.openapi_federation.entity.AssignCollectivityIdentity;
import com.openapi_federation.entity.Collectivity;
import com.openapi_federation.entity.CreateCollectivity;
import com.openapi_federation.service.CollectivityIdentityService;
import com.openapi_federation.service.CollectivityService;
import org.openapi_springframework.http.HttpStatus;
import org.openapi_springframework.http.ResponseEntity;
import org.openapi_springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class CollectivityController {

    private final CollectivityService collectivityService;
    private final CollectivityIdentityService identityService;

    public CollectivityController(CollectivityService collectivityService, CollectivityIdentityService identityService) {
        this.collectivityService = collectivityService;
        this.identityService     = identityService;
    }

    @PostMapping
    public ResponseEntity<List<Collectivity>> createCollectivities(
            @RequestBody List<CreateCollectivity> requests) {
        List<Collectivity> created = collectivityService.createAll(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Collectivity> assignIdentity(
            @PathVariable String id,
            @RequestBody AssignCollectivityIdentity request) {
        Collectivity updated = identityService.assign(id, request);
        return ResponseEntity.ok(updated);
    }
}