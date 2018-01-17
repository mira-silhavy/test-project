package com.incredible.pack;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.incredible.pack.types.Item;
import com.incredible.pack.types.Pack;
import com.incredible.pack.types.enums.EItemOrdering;

/**
 * Main class.
 */
public class Main {

    public final URL resource;
    public final URL resource2;
    public final URL resource3;
    public final ClassLoader parent;
    public final ClassLoader current;
    private final Class thread;

    Main() {
        resource = Thread.currentThread().getContextClassLoader().getResource("test.yaml");
        resource2 = this.getClass().getResource("test.yaml");
        resource3 = this.getClass().getClassLoader().getResource("test.yaml");
        current = this.getClass().getClassLoader();
        parent = this.getClass().getClassLoader().getParent();
        thread = Thread.currentThread().getClass();
    }

    public static void main(String[] args) {
        final Main main = new Main();
        final Scanner scan = new Scanner(System.in);
        final List<Item> items = new ArrayList<>();

        PackFactory packFactory = null;

        while (scan.hasNextLine()) {
            final String line = scan.nextLin  e();

            if (StringUtils.isEmpty(line)) {
                break;
            }

            try {
                if (!line.startsWith("#")) { // skip comments
                    final String[] params = line.split(",");
                    if (packFactory == null) { // read configuration parameters
                        if (params.length != 3) {
                            System.out.println("Wrong configuration parameters.");
                            return;
                        }

                        packFactory = new PackFactory(EItemOrdering.valueOf(params[0].toUpperCase().trim()), Integer.parseInt(params[1]),
                            new BigDecimal(params[2]));
                    } else { //read items
                        if (params.length != 4) {
                            System.out.println("Wrong number of item input parameters.");
                        }

                        final int id = Integer.parseInt(params[0]);
                        final int length = Integer.parseInt(params[1]);
                        final int quantity = Integer.parseInt(params[2]);
                        final BigDecimal weight = new BigDecimal(params[3]);

                        items.add(new Item(id, length, quantity, weight));
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid parameter: " + e.getMessage());
            }
        }

        if (packFactory != null) {
            final List<Pack> packs = packFactory.processItems(items);
            if (CollectionUtils.isNotEmpty(packs)) {
                packs.forEach(p -> System.out.println(p + "\n"));
            } else {
                System.out.println("No packs created. Either no items provided or they don't fit the pack parameters.");
            }
        }
    }
}
