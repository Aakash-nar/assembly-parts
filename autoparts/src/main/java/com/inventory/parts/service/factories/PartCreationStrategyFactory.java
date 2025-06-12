package com.inventory.parts.service.factories;
import com.inventory.parts.domain.PartType;
import com.inventory.parts.service.strategies.PartCreationStrategy;
import com.inventory.parts.service.strategies.RawPartCreationStrategy;
import com.inventory.parts.service.strategies.AssembledPartCreationStrategy;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import jakarta.annotation.PostConstruct;

@Component
public class PartCreationStrategyFactory {

    private final RawPartCreationStrategy rawPartCreationStrategy;
    private final AssembledPartCreationStrategy assembledPartCreationStrategy;

    private final Map<PartType, PartCreationStrategy<?>> strategies = new EnumMap<>(PartType.class);

    public PartCreationStrategyFactory(RawPartCreationStrategy rawPartCreationStrategy,
                                       AssembledPartCreationStrategy assembledPartCreationStrategy) {
        this.rawPartCreationStrategy = rawPartCreationStrategy;
        this.assembledPartCreationStrategy = assembledPartCreationStrategy;
    }

    @PostConstruct
    public void init() {
        strategies.put(PartType.RAW, rawPartCreationStrategy);
        strategies.put(PartType.ASSEMBLED, assembledPartCreationStrategy);
    }

    public PartCreationStrategy getStrategy(PartType partType) {
        if (!strategies.containsKey(partType)) {
            throw new IllegalArgumentException("No creation strategy: " + partType);
        }
        return strategies.get(partType);
    }
}