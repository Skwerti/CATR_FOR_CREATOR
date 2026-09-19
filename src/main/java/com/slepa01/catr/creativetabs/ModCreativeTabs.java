package com.slepa01.catr.creativetabs;

import com.slepa01.catr.CatrMod;
import com.slepa01.catr.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CatrMod.MOD_ID);

    public static final Supplier<CreativeModeTab> CATR_TAB = CREATIVE_TABS.register("catr_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.catr"))
                    .icon(() -> new ItemStack(ModItems.INCOPLETE_AUG.get()))
                    .displayItems((parameters, output) -> {
                    })
                    .build()
    );

    public static final Supplier<CreativeModeTab> CORPUS_TAB = CREATIVE_TABS.register("corpus_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.catr.corpus"))
                    .icon(() -> new ItemStack(ModItems.CORPUS_AKM.get()))
                    .displayItems((parameters, output) -> {
                    })
                    .build()
    );

    public static final Supplier<CreativeModeTab> COMPONENTS_WEAPON_TAB = CREATIVE_TABS.register("components_weapon_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.catr.components_weapon"))
                    .icon(() -> new ItemStack(ModItems.STORE_WEAPON.get()))
                    .displayItems((parameters, output) -> {
                    })
                    .build()
    );
}