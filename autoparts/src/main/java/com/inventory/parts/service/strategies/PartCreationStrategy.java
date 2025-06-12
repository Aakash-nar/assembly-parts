package com.inventory.parts.service.strategies;

import com.inventory.parts.dto.CreatePartRequest;
import com.inventory.parts.domain.Part;
import com.inventory.parts.repository.PartRepository;

public interface PartCreationStrategy<T extends CreatePartRequest> {
    Part create(T request, PartRepository partRepository);
}