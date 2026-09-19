package com.slepa01.catr.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

public class DiamondDrillBitItem extends Item {

    public DiamondDrillBitItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        // Вычисляем прочность, делённую на 2
        int maxDamage = stack.getMaxDamage();          // например 6
        int currentDamage = stack.getDamageValue();
        int currentDurability = (maxDamage - currentDamage);   // остаток / 2

        // Добавляем серую строку
        tooltipComponents.add(Component.translatable("catr.item_info.durability",
                        currentDurability + " / " + maxDamage)
                .withStyle(ChatFormatting.GRAY));
    }
}