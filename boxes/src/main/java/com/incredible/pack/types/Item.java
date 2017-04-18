package com.incredible.pack.types;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Item class.
 */
@Data
@AllArgsConstructor
public class Item {

    /**
     * Instance fields.
     */
    private int id;
    private int length;
    private int quantity;
    private BigDecimal weight;

    /**
     * Copy constructor.
     */
    public Item(Item item) {
        this.id = item.id;
        this.length = item.length;
        this.quantity = item.quantity;
        this.weight = item.weight;
    }

    @Override
    public String toString() {
        return id + "," + length + "," + quantity + "," + weight;
    }
}
