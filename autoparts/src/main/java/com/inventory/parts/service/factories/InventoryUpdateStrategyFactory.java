package com.inventory.parts.service.factories;

import com.inventory.parts.domain.PartType;
import com.inventory.parts.service.strategies.AssembledPartInventoryUpdateStrategy;
import com.inventory.parts.service.strategies.InventoryUpdateStrategy;
import com.inventory.parts.service.strategies.RawPartInventoryUpdateStrategy;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;

@Component
public class InventoryUpdateStrategyFactory {
    private final RawPartInventoryUpdateStrategy rawPartInventoryUpdateStrategy;
    private final AssembledPartInventoryUpdateStrategy assembledPartInventoryUpdateStrategy;
    private final Map<PartType, InventoryUpdateStrategy> strategies = new EnumMap<>(PartType.class);
    public InventoryUpdateStrategyFactory(RawPartInventoryUpdateStrategy rawPartInventoryUpdateStrategy,
                                          AssembledPartInventoryUpdateStrategy assembledPartInventoryUpdateStrategy) {
        this.rawPartInventoryUpdateStrategy = rawPartInventoryUpdateStrategy;
        this.assembledPartInventoryUpdateStrategy = assembledPartInventoryUpdateStrategy;
    }

    @PostConstruct
    public void init() {
        strategies.put(PartType.RAW, rawPartInventoryUpdateStrategy);
        strategies.put(PartType.ASSEMBLED, assembledPartInventoryUpdateStrategy);
    }

    public InventoryUpdateStrategy getStrategy(PartType partType) {
        if (!strategies.containsKey(partType)) {
            throw new IllegalArgumentException("No inventory update strategy found:" + partType);
        }
        return strategies.get(partType);
    }
}