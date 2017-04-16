package com.incredible;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.incredible.types.Item;
import com.incredible.types.Pack;
import com.incredible.types.enums.EItemOrdering;

/**
 * PackFactory test.
 */
public class PackFactoryTest {

    @Test
    void processItemsTooLargeItem() {
        final PackFactory packFactory = new PackFactory(EItemOrdering.NATURAL, 10, new BigDecimal(100));
        final Item item = new Item(1, 1000, 10, new BigDecimal(101));

        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> packFactory.processItems(Collections.singletonList(item)),
            "Processing items took too long");
    }

    @Test
    void processItemsMaxCount() {
        final PackFactory packFactory = new PackFactory(EItemOrdering.NATURAL, 2, new BigDecimal(100));
        final Item item = new Item(1, 50, 10, new BigDecimal(50));
        final Item item2 = new Item(2, 50, 10, new BigDecimal(50));

        final List<Pack> packs = packFactory.processItems(Arrays.asList(item, item2));

        assertThat(packs.size(), is(10));
        final int count = packs.stream().map(Pack::getItems).mapToInt(List::size).sum();
        assertThat(count, is(10));
    }
}