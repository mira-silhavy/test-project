package com.incredible;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Unique ID generator. Generates 64 bit long value.
 */
public class Generator {

    private static final int LENGTH = 64;

    private static final long TIME_MAX = 0xFFFFFFFFL; // 32 bits
    private static final int TIME_OFFSET = 32;

    private static final long NODE_MAX = 0xFFFL; // 12 bits
    private static final int NODE_OFFSET = 12;

    private static final long COUNT_MAX = 0x7FFL; // 11 bits
    private static final int COUNT_OFFSET = 11;

    private static final int RESERVED = 9; // reserved bits for expansion

    private int count = 0;
    private long time = 0;
    private int nodeId = 0;

    private static Generator instance = null;

    public static Generator getInstance() {
        if (instance == null) {
            instance = new Generator(0);
        }
        return instance;
    }

    public Generator(int nodeId) {
        if (nodeId > NODE_MAX) {
            System.out.println("Max node limit exceeded, limit is " + NODE_MAX + " nodes");
            throw new IndexOutOfBoundsException();
        }
        this.nodeId = nodeId;
    }

    /**
     * This method generates an unique 64-bit long positive ID combining 3 separate values:
     * <p>
     * ID 64 bits = 32 bits (timestamp) + 12 bits (node) + 11 bits (count) + 8 bits (reserved)
     * <p>
     * First value is timestamp that has allocated 32 bits. Timestamp is a value in seconds that elapsed since startEpoch parameter.
     * Maximum allowed range is 136 years since startEpoch.
     * <p>
     * Second value is node ID that has allocated 12 bits. Node id can be used to uniquely identify separate instances that uses this
     * generator. The value must unique per running instance or table.
     * Maximum allowed value is 4095.
     * <p>
     * Third value is auto incremental counter with limit of 2047 values.
     * <p>
     * Maximum throughput is 2047 values per second per instance (node ID). Values began to repeat if more than 2047 in one second are
     * requested.
     *
     * @throws IllegalStateException if timestamp exceeds the allowed range
     * @return returns ID
     */
    public long generateId(long startEpoch) {
        final long currentEpochSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        final long secondsSinceStart = currentEpochSeconds - startEpoch;

        if (count > COUNT_MAX && time == currentEpochSeconds) {
            System.out.println("Max count value overflow, limit is " + COUNT_MAX + " values");
        } else if (secondsSinceStart > TIME_MAX) {
            System.out.println("Max time limit reached, limit is " + TIME_MAX + " seconds");
            throw new IllegalStateException("Time limit reached.");
        }

        if (time < currentEpochSeconds) {
            count = 0;
            time = currentEpochSeconds;
        }

        long id = secondsSinceStart << (LENGTH - TIME_OFFSET);
        id |= nodeId << (LENGTH - TIME_OFFSET - NODE_OFFSET);
        id |= (count++ % 2048) << (LENGTH - TIME_OFFSET - NODE_OFFSET - COUNT_OFFSET);

        return id;
    }
}
