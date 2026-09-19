package com.slepa01.catr.datagen;

import com.slepa01.catr.items.ModItemsGen;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Перебираем все предметы из DeferredRegister
        for (var holder : ModItemsGen.ITEMS.getEntries()) {
            basicItem(holder.get());   // автоматически item/generated с layer0
        }
    }
}
