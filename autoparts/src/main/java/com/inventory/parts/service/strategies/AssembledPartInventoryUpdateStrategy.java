package com.inventory.parts.service.strategies;

import com.inventory.parts.domain.Part;
import com.inventory.parts.domain.PartType;
import com.inventory.parts.dto.InventoryUpdateResponse;
import com.inventory.parts.repository.PartRepository;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional; // Added for this strategy to ensure atomicity
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

@Component
public class AssembledPartInventoryUpdateStrategy implements InventoryUpdateStrategy {
    @Override
    @Transactional
    public InventoryUpdateResponse addQuantity(Part assembledPart, int quantityToAdd, PartRepository partRepository) {
        Map<Part, Integer> requiredConstituents = getRequiredConstituents(assembledPart, quantityToAdd, partRepository);

        Set<String> insufficientParts = new HashSet<>();
        for (Map.Entry<Part, Integer> entry : requiredConstituents.entrySet()) {
            Part constituentPart = entry.getKey();
            Integer requiredQty = entry.getValue();
            if (constituentPart.getQuantityInStock() < requiredQty) {
                insufficientParts.add(constituentPart.getName() + " (ID: " + constituentPart.getId() + ")");
            }
        }

        if (!insufficientParts.isEmpty()) {
            String message = "Insufficient quantity - " + String.join(", ", insufficientParts);
            return new InventoryUpdateResponse("FAILED", message);
        }

        // Deduct from constituent parts
        for (Map.Entry<Part, Integer> entry : requiredConstituents.entrySet()) {
            Part constituentPart = entry.getKey();
            Integer requiredQty = entry.getValue();
            constituentPart.setQuantityInStock(constituentPart.getQuantityInStock() - requiredQty);
            partRepository.save(constituentPart); // Save updated constituent part
        }

        // Add to assembled part
        assembledPart.setQuantityInStock(assembledPart.getQuantityInStock() + quantityToAdd);
        partRepository.save(assembledPart);

        return new InventoryUpdateResponse("SUCCESS", null);
    }

    // Helper method to get the total raw constituents needed for an assembled part,
    // handling nested assemblies.
    private Map<Part, Integer> getRequiredConstituents(Part assembledPart, int quantityToAssemble, PartRepository partRepository) {
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
                Part partWithConstituents = partRepository.findById(currentPart.getId())
                        .orElseThrow(() -> new RuntimeException("Constituent part not found during BOM calculation: " + currentPart.getId()));

                partWithConstituents.getConstituentParts().forEach(constituent -> {
                    int nextQuantity = constituent.getRequiredQuantity() * currentQuantity;
                    stack.push(Map.entry(constituent.getConstituentPart(), nextQuantity));
                });
            }
        }
        return totalRequiredRawParts;
    }
}