package com.slepa01.catr.client.util;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;

/**
 * Хранит позицию блока деплоера и предмет, на который в данный момент смотрит
 * игрок (при этом высвечивается свечение/информация). Обновляется рендерером
 * каждый кадр, пока игрок действительно смотрит на текстуру предмета.
 */
public class DeployerLookState {
    private static BlockPos pos;
    private static Item item;
    private static long time;

    public static void set(BlockPos p, Item i) {
        pos = p;
        item = i;
        time = Util.getMillis();
    }

    private static boolean fresh() {
        return Util.getMillis() - time < 250L;
    }

    public static BlockPos getPos() {
        return fresh() ? pos : null;
    }

    public static Item getItem() {
        return fresh() ? item : null;
    }
}
