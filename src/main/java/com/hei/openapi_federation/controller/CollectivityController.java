package com.hei.openapi_federation.controller;

import com.hei.openapi_federation.entity.AssignCollectivityIdentity;
import com.hei.openapi_federation.entity.Collectivity;
import com.hei.openapi_federation.entity.CreateCollectivity;
import com.hei.openapi_federation.service.CollectivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/collectivities")
public class CollectivityController {

    private final CollectivityService collectivityService;

    public CollectivityController(CollectivityService collectivityService) {
        this.collectivityService = collectivityService;
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
        Collectivity updated = collectivityService.assignIdentity(id, request);
        return ResponseEntity.ok(updated);
    }
}