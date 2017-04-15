package com.incredible;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.incredible.types.EItemOrdering;
import com.incredible.types.Item;
import com.incredible.types.Pack;

/** Template JavaDoc for Processor */
public class Processor {

    public void process(Scanner scan) {
        PackFactory packFactory = null;
        List<Item> items = new ArrayList<>();

        while (scan.hasNextLine()) {
            final String line = scan.nextLine();

            if (StringUtils.isEmpty(line)) {
                break;
            }

            //skip comments
            if (!line.startsWith("#")) {
                String[] params = line.split(",");
                //read planner parameters
                if (packFactory == null) {
                    if (params.length != 3) {
                        System.out.println("Wrong number of Pack planner input parameters.");
                        return;
                    }
                    packFactory = new PackFactory(EItemOrdering.valueOf(params[0].toUpperCase().trim()),
                        Integer.parseInt(params[1]),
                        new BigDecimal(params[2]));
                } else { //read item parameters
                    if (params.length != 4) {
                        System.out.println("Wrong number of Item input parameters.");
                    }

                    int id = Integer.parseInt(params[0]);
                    int length = Integer.parseInt(params[1]);
                    int quantity = Integer.parseInt(params[2]);
                    BigDecimal weight = new BigDecimal(params[3]);

                    items.add(new Item(id, length, quantity, weight));
                }
            }
        }

        if (packFactory != null) {
            List<Pack> packs = packFactory.processItems(items);

            packs.forEach(p -> System.out.println(p + "\n"));
        }
    }
}
