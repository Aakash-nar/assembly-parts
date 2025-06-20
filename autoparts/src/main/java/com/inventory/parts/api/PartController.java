package com.inventory.parts.api;

import com.inventory.parts.dto.*;
import com.inventory.parts.service.PartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/part")
public class PartController {

    PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

//    @PostMapping
//    public ResponseEntity<PartResponse> createPart(@Valid @RequestBody CreatePartRequest request) {
//        PartResponse partResponse = partService.createPart(request);
//        return new ResponseEntity<>(partResponse, HttpStatus.CREATED);
//    }


    @PostMapping("/raw")
    public ResponseEntity<PartResponse> createRawPart(@RequestBody CreatePartRequest request) {
        request.setType(com.inventory.parts.domain.PartType.RAW);
        PartResponse response = partService.createPart(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/assembled")
    public ResponseEntity<PartResponse> createAssembledPart(@RequestBody CreateAssembledPartRequest request) {
        request.setType(com.inventory.parts.domain.PartType.ASSEMBLED);
        PartResponse response = partService.createPart(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{partId}")
    public ResponseEntity<InventoryUpdateResponse> addPartToInventory(
            @PathVariable String partId,
            @Valid @RequestBody AddToInventoryRequest request) {
        InventoryUpdateResponse response = partService.addPartToInventory(partId, request.getQuantity());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}