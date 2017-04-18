package com.incredible.pack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.incredible.pack.types.Item;
import com.incredible.pack.types.Pack;
import com.incredible.pack.types.enums.EItemOrdering;

/**
 * Pack factory and items processor.
 */
public class PackFactory {

    private final EItemOrdering order;
    private final int maxItems;
    private final BigDecimal maxWeight;

    public PackFactory(EItemOrdering order, int maxItems, BigDecimal maxWeight) {
        this.order = order;
        this.maxItems = maxItems;
        this.maxWeight = maxWeight;
    }

    /**
     * Main pack processing function handling items initial sort and pack creation.
     */
    public List<Pack> processItems(List<Item> items) {
        switch (order) {
            case LONG_TO_SHORT:
                items.sort(Comparator.comparing(Item::getLength).reversed());
                break;
            case SHORT_TO_LONG:
                items.sort(Comparator.comparing(Item::getLength));
                break;
            default:
                break;
        }

        final List<Pack> packs = new ArrayList<>();
        Pack pack = makePack();

        for (Item item : items) {
            while ((item = pack.addItem(item)) != null) {
                packs.add(pack);
                pack = makePack();
            }
        }

        if (!pack.isEmpty()) {
            packs.add(pack);
        }

        return packs;
    }

    public Pack makePack() {
        return new Pack(maxItems, maxWeight);
    }
}
