package com.slepa01.catr.items;

import net.minecraft.world.item.Item;

import java.util.Set;

public class DrillBits {
    public static final Set<Item> ALL = Set.of(
            ModItems.IRON_DRILL_BIT.get(),
            ModItemsGen.DIAMOND_DRILL_BIT.get(),
            ModItemsGen.STEEL_DRILL_BIT.get()
    );

    public static boolean isDrill(Item item) {
        return item != null && ALL.contains(item);
    }
}
