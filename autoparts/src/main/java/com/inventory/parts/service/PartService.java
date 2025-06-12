package com.inventory.parts.service;

import com.inventory.parts.dto.*;
import com.inventory.parts.domain.Part;
import com.inventory.parts.domain.PartType;
import com.inventory.parts.repository.PartRepository;
import com.inventory.parts.shared.exceptions.NotFoundException;
import com.inventory.parts.service.factories.PartCreationStrategyFactory;
import com.inventory.parts.service.factories.InventoryUpdateStrategyFactory;
import com.inventory.parts.service.strategies.PartCreationStrategy;
import com.inventory.parts.service.strategies.InventoryUpdateStrategy;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartService {

    private final PartRepository partRepository;
    private final PartCreationStrategyFactory partCreationStrategyFactory;
    private final InventoryUpdateStrategyFactory inventoryUpdateStrategyFactory;

    public PartService(PartRepository partRepository,
                       PartCreationStrategyFactory partCreationStrategyFactory,
                       InventoryUpdateStrategyFactory inventoryUpdateStrategyFactory) {
        this.partRepository = partRepository;
        this.partCreationStrategyFactory = partCreationStrategyFactory;
        this.inventoryUpdateStrategyFactory = inventoryUpdateStrategyFactory;
    }

    @Transactional
    public PartResponse createPart(CreatePartRequest request) {
        PartCreationStrategy strategy = partCreationStrategyFactory.getStrategy(request.getType());
        Part part = strategy.create(request, partRepository);

        Part savedPart = partRepository.save(part);
        return mapToPartResponse(savedPart);
    }

    @Transactional
    public InventoryUpdateResponse addPartToInventory(String partId, int quantityToAdd) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new NotFoundException("Part with ID '" + partId + "' not found."));

        InventoryUpdateStrategy strategy = inventoryUpdateStrategyFactory.getStrategy(part.getType());
        return strategy.addQuantity(part, quantityToAdd, partRepository);
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