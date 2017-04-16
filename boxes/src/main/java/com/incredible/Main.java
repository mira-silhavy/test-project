package com.incredible;

import java.util.Scanner;

/**
 * Main class.
 */
public class Main {

    public static void main(String[] args) {
        final Processor processor = new Processor();
        processor.process(new Scanner(System.in));
    }
}
