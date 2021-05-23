package com.mind.map.api.controllers;

import com.mind.map.api.domain.AddLeafResponse;
import com.mind.map.api.domain.Leaf;
import com.mind.map.api.domain.CreateMapRequest;
import com.mind.map.api.domain.Map;
import com.mind.map.api.domain.ReadMapResponse;
import com.mind.map.api.services.MapService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/map")
@Validated
public class MapController {
    private final MapService service;

    public MapController(MapService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map> createMap(@Valid @NotNull @RequestBody final CreateMapRequest request) {
        return ResponseEntity.ok().body(service.createMap(request));
    }

    @GetMapping("/{map}")
    public ResponseEntity<ReadMapResponse> getMap(@PathVariable final String map) {
        return ResponseEntity.ok().body(service.getMap(map));
    }

    @GetMapping("/{map}/pretty")
    public ResponseEntity printMap(@PathVariable final String map) {
        return ResponseEntity.ok().body(service.printMap(map));
    }

    @PostMapping("/{map}/leaf")
    public ResponseEntity<AddLeafResponse> addLeaf(@PathVariable final String map, @Valid @NotNull @RequestBody final Leaf request) {
        return ResponseEntity.ok().body(service.addLeaf(map, request));
    }

    @GetMapping("/{map}/leaf/{leafId}")
    public ResponseEntity<Leaf> readLeaf(@PathVariable final String map, @PathVariable final String leafId) {
        return ResponseEntity.ok().body(service.readLeaf(map, leafId));
    }

}
