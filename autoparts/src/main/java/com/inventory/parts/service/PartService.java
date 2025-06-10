package com.inventory.parts.service;

import com.inventory.parts.dto.*;
import com.inventory.parts.domain.ConstituentPart;
import com.inventory.parts.domain.Part;
import com.inventory.parts.domain.PartType;
import com.inventory.parts.repository.PartRepository;
import com.inventory.parts.shared.exceptions.BadRequestException;
import com.inventory.parts.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @Transactional
    public PartResponse createPart(CreatePartRequest request) {
        Part part;
        if (request.getType() == PartType.RAW) {
            part = new Part(request.getName(), PartType.RAW);
        } else {
            CreateAssembledPartRequest assembledRequest = (CreateAssembledPartRequest) request;
            part = new Part(assembledRequest.getName(), PartType.ASSEMBLED);

            Map<String, Integer> constituentPartsMap = new HashMap<>();
            for (ConstituentPartRequest constituentReq : assembledRequest.getParts()) {
                if (constituentPartsMap.containsKey(constituentReq.getId())) {
                    throw new BadRequestException("Duplicate constituent parts found in the request: " + constituentReq.getId());
                }
                constituentPartsMap.put(constituentReq.getId(), constituentReq.getQuantity());
            }

            List<ConstituentPart> constituentPartEntities = assembledRequest.getParts().stream()
                    .map(constituentReq -> {
                        Part foundConstituent = partRepository.findById(constituentReq.getId())
                                .orElseThrow(() -> new NotFoundException("Constituent part with ID '" + constituentReq.getId() + "' not found."));
                        return new ConstituentPart(part, foundConstituent, constituentReq.getQuantity());
                    })
                    .collect(Collectors.toList());

            part.getConstituentParts().addAll(constituentPartEntities);
            validateCircularDependency(part);
        }
        Part savedPart = partRepository.save(part);
        return mapToPartResponse(savedPart);
    }

    @Transactional
    public InventoryUpdateResponse addPartToInventory(String partId, int quantityToAdd) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new NotFoundException("Part with ID '" + partId + "' not found."));

        if (part.getType() == PartType.RAW) {
            return addRawPartToInventory(part, quantityToAdd);
        } else {
            return addAssembledPartToInventory(part, quantityToAdd);
        }
    }

    private InventoryUpdateResponse addRawPartToInventory(Part part, int quantityToAdd) {
        part.setQuantityInStock(part.getQuantityInStock() + quantityToAdd);
        partRepository.save(part);
        return new InventoryUpdateResponse("SUCCESS", null);
    }

    private InventoryUpdateResponse addAssembledPartToInventory(Part assembledPart, int quantityToAdd) {
        Map<Part, Integer> requiredConstituents = getRequiredConstituents(assembledPart, quantityToAdd);

        // Check if all constituent parts have sufficient quantity
        Set<String> insufficientParts = new HashSet<>();
        for (Map.Entry<Part, Integer> entry : requiredConstituents.entrySet()) {
            Part constituentPart = entry.getKey();
            Integer requiredQty = entry.getValue();
            if (constituentPart.getQuantityInStock() < requiredQty) {
                insufficientParts.add(constituentPart.getId());
            }
        }

        if (!insufficientParts.isEmpty()) {
            String message = "Insufficient quantity - " + String.join(", ", insufficientParts);
            return new InventoryUpdateResponse("FAILED", message);
        }

        for (Map.Entry<Part, Integer> entry : requiredConstituents.entrySet()) {
            Part constituentPart = entry.getKey();
            Integer requiredQty = entry.getValue();
            constituentPart.setQuantityInStock(constituentPart.getQuantityInStock() - requiredQty);
            partRepository.save(constituentPart); // Save updated constituent part
        }

        assembledPart.setQuantityInStock(assembledPart.getQuantityInStock() + quantityToAdd);
        partRepository.save(assembledPart);

        return new InventoryUpdateResponse("SUCCESS", null);
    }

    private Map<Part, Integer> getRequiredConstituents(Part assembledPart, int quantityToAssemble) {
        Map<Part, Integer> totalRequiredRawParts = new HashMap<>();
        Stack<Map.Entry<Part, Integer>> stack = new Stack<>();
        stack.push(Map.entry(assembledPart, quantityToAssemble));

        while (!stack.isEmpty()) {
            Map.Entry<Part, Integer> currentEntry = stack.pop();
            Part currentPart = currentEntry.getKey();
            Integer currentQuantity = currentEntry.getValue();

            if (currentPart.getType() == PartType.RAW) {
                totalRequiredRawParts.merge(currentPart, currentQuantity, Integer::sum);
            } else {
                partRepository.findById(currentPart.getId()).ifPresent(p -> {
                    p.getConstituentParts().forEach(constituent -> {
                        int nextQuantity = constituent.getRequiredQuantity() * currentQuantity;
                        stack.push(Map.entry(constituent.getConstituentPart(), nextQuantity));
                    });
                });
            }
        }
        return totalRequiredRawParts;
    }


    private void validateCircularDependency(Part assembledPart) {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        dfsCircularDependency(assembledPart, visited, recursionStack);
    }

    private void dfsCircularDependency(Part currentPart, Set<String> visited, Set<String> recursionStack) {
        visited.add(currentPart.getId());
        recursionStack.add(currentPart.getId());

        if (currentPart.getType() == PartType.ASSEMBLED) {
            Part freshPart = partRepository.findById(currentPart.getId())
                    .orElseThrow(() -> new NotFoundException("Part " + currentPart.getId() + " not found during circular dependency check."));

            for (ConstituentPart constituent : freshPart.getConstituentParts()) {
                Part nextPart = constituent.getConstituentPart(); // This should already be loaded if fetched via freshPart
                if (recursionStack.contains(nextPart.getId())) {
                    throw new BadRequestException("Circular dependency detected: " + nextPart.getName() + " is a constituent of " + currentPart.getName() + ", which leads to a cycle.");
                }
                if (!visited.contains(nextPart.getId())) {
                    dfsCircularDependency(nextPart, visited, recursionStack);
                }
            }
        }
        recursionStack.remove(currentPart.getId());
    }


    private PartResponse mapToPartResponse(Part part) {
        if (part.getType() == PartType.RAW) {
            return new RawPartResponse(part.getId(), part.getName(), part.getType());
        } else {
            List<ConstituentPartResponse> constituentResponses = part.getConstituentParts().stream()
                    .map(cp -> new ConstituentPartResponse(cp.getConstituentPart().getId(), cp.getRequiredQuantity()))
                    .collect(Collectors.toList());
            return new AssembledPartResponse(part.getId(), part.getName(), part.getType(), constituentResponses);
        }
    }
}