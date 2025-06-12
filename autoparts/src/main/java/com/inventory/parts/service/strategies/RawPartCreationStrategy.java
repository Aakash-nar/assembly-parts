package com.inventory.parts.service.strategies;

import com.inventory.parts.dto.CreatePartRequest;
import com.inventory.parts.domain.Part;
import com.inventory.parts.domain.PartType;
import com.inventory.parts.repository.PartRepository;
import org.springframework.stereotype.Component;

@Component
public class RawPartCreationStrategy implements PartCreationStrategy<CreatePartRequest> {
    @Override
    public Part create(CreatePartRequest request, PartRepository partRepository) {
        return new Part(request.getName(), PartType.RAW);
    }
}
