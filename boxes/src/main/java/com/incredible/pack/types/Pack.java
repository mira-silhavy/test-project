package com.incredible.pack.types;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

/**
 * Pack class.
 *
 * Every pack keeps track of its current length, weight, items it contains and its current count.
 */
@Data
public class Pack {

    /**
     * Pack counter class field.
     */
    private static int packCounter = 1;

    /**
     * Instance fields.
     */
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

    /**
     * Pack insert function. Contains all the pack logic for inserting, splitting or deciding whether the item fits in the pack.
     *
     * @return returns null if item has been consumed or is too large for processing otherwise returns new copy of an item with the
     * updated quantity
     */
    public Item addItem(Item item) {
        if (item.getWeight().compareTo(maxWeight) > 0) {
            System.out.println("Item " + item + " too heavy to fit into pack, skipping...");
            return null;
        }

        final BigDecimal allowedWeight = maxWeight.subtract(currentWeight);
        final int allowedItems = Math.min(allowedWeight.divide(item.getWeight(), 1, BigDecimal.ROUND_FLOOR).intValue(),
            maxItems - currentItems);

        if (allowedItems == 0) {
            return item;
        }

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
