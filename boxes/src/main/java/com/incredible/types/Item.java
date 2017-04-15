package com.incredible.types;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Template JavaDoc for Item */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private int id;
    private int length;
    private int quantity;
    private BigDecimal weight;

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
