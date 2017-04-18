package com.incredible.pack;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.incredible.pack.types.Item;
import com.incredible.pack.types.Pack;
import com.incredible.pack.types.enums.EItemOrdering;

/**
 * PackFactory test.
 */
public class PackFactoryTest {

    @Test
    void processTooLargeItem() {
        final PackFactory packFactory = new PackFactory(EItemOrdering.NATURAL, 10, new BigDecimal(100));
        final Item item = new Item(1, 1000, 10, new BigDecimal(101));

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> packFactory.processItems(Collections.singletonList(item)),
            "Processing items took too long");
    }

    @Test
    void processItemsMaxCountHaveCorrectCount() {
        final PackFactory packFactory = new PackFactory(EItemOrdering.NATURAL, 2, new BigDecimal(100));
        final Item item = new Item(1, 50, 10, new BigDecimal(50));
        final Item item1 = new Item(2, 50, 10, new BigDecimal(50));

        final List<Pack> packs = packFactory.processItems(Arrays.asList(item, item1));

        assertThat(packs.size(), is(10));
        final int count = packs.stream().map(Pack::getItems).mapToInt(List::size).sum();
        assertThat(count, is(10));
    }

    @Test
    void processItemsAscendingSort() {
        final PackFactory packFactory = new PackFactory(EItemOrdering.SHORT_TO_LONG, 25, new BigDecimal(2000));
        final Item item = new Item(1, 10, 10, new BigDecimal(50));
        final Item item1 = new Item(2, 20, 10, new BigDecimal(50));
        final Item item2 = new Item(3, 80, 10, new BigDecimal(50));
        final Item item3 = new Item(4, 30, 10, new BigDecimal(50));
        final Item item4 = new Item(5, 50, 10, new BigDecimal(50));

        final List<Pack> packs = packFactory.processItems(Arrays.asList(item, item1, item2, item3, item4));

        assertThat(packs.size(), is(2));
        assertThat(packs.get(0).getCurrentLength(), is(30));
        assertThat(packs.get(1).getCurrentLength(), is(80));
        assertThat(packs.get(0).getCurrentWeight(), is(new BigDecimal(1250)));
        assertThat(packs.get(1).getCurrentWeight(), is(new BigDecimal(1250)));
    }

    @Test
    void processItemsDescendingSort() {
        final PackFactory packFactory = new PackFactory(EItemOrdering.LONG_TO_SHORT, 25, new BigDecimal(2000));
        final Item item = new Item(1, 10, 10, new BigDecimal(50));
        final Item item1 = new Item(2, 20, 10, new BigDecimal(50));
        final Item item2 = new Item(3, 80, 10, new BigDecimal(50));
        final Item item3 = new Item(4, 30, 10, new BigDecimal(50));
        final Item item4 = new Item(5, 50, 10, new BigDecimal(50));

        final List<Pack> packs = packFactory.processItems(Arrays.asList(item, item1, item2, item3, item4));

        assertThat(packs.size(), is(2));
        assertThat(packs.get(0).getCurrentLength(), is(80));
        assertThat(packs.get(1).getCurrentLength(), is(30));
        assertThat(packs.get(0).getCurrentWeight(), is(new BigDecimal(1250)));
        assertThat(packs.get(1).getCurrentWeight(), is(new BigDecimal(1250)));
    }

    @Test
    void processHitWeightLimit() {
        final PackFactory packFactory = new PackFactory(EItemOrdering.NATURAL, 25, new BigDecimal(1050));
        final Item item = new Item(1, 10, 10, new BigDecimal(50));
        final Item item1 = new Item(2, 20, 10, new BigDecimal(50));
        final Item item2 = new Item(3, 80, 10, new BigDecimal(50));
        final Item item3 = new Item(4, 30, 10, new BigDecimal(50));
        final Item item4 = new Item(5, 50, 10, new BigDecimal(50));

        final List<Pack> packs = packFactory.processItems(Arrays.asList(item, item1, item2, item3, item4));

        assertThat(packs.size(), is(3));
        assertThat(packs.get(0).getCurrentLength(), is(80));
        assertThat(packs.get(1).getCurrentLength(), is(80));
        assertThat(packs.get(0).getCurrentWeight(), is(new BigDecimal(1050)));
        assertThat(packs.get(1).getCurrentWeight(), is(new BigDecimal(1050)));

        assertThat(packs.get(2).getItems().get(0).getQuantity(), is(8));
        assertThat(packs.get(2).getCurrentLength(), is(50));
    }
}