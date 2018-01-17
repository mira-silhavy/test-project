package com.incredible;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.Test;

/**
 * Generator test.
 */
public class GeneratorTest {

    private final Generator instance = Generator.getInstance();

    @Test
    public void testGenerator() throws Exception {
        final long currentEpochMinusYear = LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC);
        final List<Long> collect = LongStream.range(0, 4000).mapToObj(i -> instance.generateId(currentEpochMinusYear)).collect
            (Collectors.toList());

        assertThat(collect.stream().distinct().count(), is(not(4000L)));
    }
}