package com.incredible.types;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Template JavaDoc for Pack */
public class Pack {

    private static int packCounter = 1;
    private final int number;
    private final int maxItems;
    private final BigDecimal maxWeight;

    private List<Item> items = new ArrayList<>();
    private int currentLength;
    private int currentItems;
    private BigDecimal currentWeight = BigDecimal.ZERO;

    public Pack(int maxItems, BigDecimal maxWeight) {
        this.number = packCounter++;
        this.maxItems = maxItems;
        this.maxWeight = maxWeight;
    }

    public Item addItem(Item item) {
        BigDecimal allowedWeight = maxWeight.subtract(currentWeight);
        int allowedItems = Math.min(allowedWeight.divide(item.getWeight(), 1, BigDecimal.ROUND_FLOOR).intValue(), maxItems - currentItems);

        Item itemCopy = null;

        if (item.getQuantity() > allowedItems) {
            itemCopy = new Item(item);
            itemCopy.setQuantity(item.getQuantity() - allowedItems);
            item.setQuantity(allowedItems);
        }

        currentWeight = currentWeight.add(item.getWeight().multiply(new BigDecimal(item.getQuantity())));
        currentItems += item.getQuantity();
        currentLength = Math.max(currentLength, item.getLength());
        items.add(item);

        return itemCopy;
    }

    public boolean isEmpty() {
        return items.size() == 0;
    }

    @Override
    public String toString() {
        return "Pack number: " + number + "\n" + items.stream().map(Item::toString).collect(Collectors.joining("\n")) + "\n" +
               "Pack Length: " + currentLength + ", Pack Weight: " + currentWeight.stripTrailingZeros();
    }
}
