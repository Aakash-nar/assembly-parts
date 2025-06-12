package com.inventory.parts.service.strategies;

import com.inventory.parts.dto.ConstituentPartRequest;
import com.inventory.parts.dto.CreateAssembledPartRequest;
import com.inventory.parts.domain.ConstituentPart;
import com.inventory.parts.domain.Part;
import com.inventory.parts.domain.PartType;
import com.inventory.parts.repository.PartRepository;
import com.inventory.parts.shared.exceptions.BadRequestException;
import com.inventory.parts.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AssembledPartCreationStrategy implements PartCreationStrategy<CreateAssembledPartRequest> {
    @Override
    public Part create(CreateAssembledPartRequest request, PartRepository partRepository) {
        Part part = new Part(request.getName(), PartType.ASSEMBLED);

        Map<String, Integer> constituentPartsMap = new HashMap<>();
        for (ConstituentPartRequest constituentReq : request.getParts()) {
            if (constituentPartsMap.containsKey(constituentReq.getId())) {
                throw new BadRequestException("Duplicate constituent parts found: " + constituentReq.getId());
            }
            constituentPartsMap.put(constituentReq.getId(), constituentReq.getQuantity());
        }

        List<ConstituentPart> constituentPartEntities = request.getParts().stream()
                .map(constituentReq -> {
                    Part foundConstituent = partRepository.findById(constituentReq.getId())
                            .orElseThrow(() -> new NotFoundException("Constituent part with ID '" + constituentReq.getId() + "' not found."));
                    return new ConstituentPart(part, foundConstituent, constituentReq.getQuantity());
                })
                .collect(Collectors.toList());

        part.getConstituentParts().addAll(constituentPartEntities);
        return part;
    }
}