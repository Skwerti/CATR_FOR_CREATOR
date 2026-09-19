package com.slepa01.catr.datagen;

import com.slepa01.catr.items.ModItemsGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.data.recipes.SingleItemRecipeBuilder.stonecutting;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        Optional<Item> ironSheet = getCreateIronSheet();
        if (ironSheet.isEmpty()) {
            return;
        }
        Ingredient ingredient = Ingredient.of(ironSheet.get());

        for (var holder : ModItemsGen.ITEMS.getEntries()) {
            Item item = holder.get();
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);

            if (id.getPath().contains("corpus")) {
                // Создаём рецепт и сразу добавляем критерий разблокировки
                stonecutting(ingredient, RecipeCategory.MISC, item, 1)
                        .unlockedBy("has_iron_sheet", has(ironSheet.get()))
                        .save(output, ResourceLocation.fromNamespaceAndPath(
                                id.getNamespace(), id.getPath() + "_from_stonecutting"));
            }
        }
    }

    private Optional<Item> getCreateIronSheet() {
        return BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse("create:iron_sheet"));
    }
}