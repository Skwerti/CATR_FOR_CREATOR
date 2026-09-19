package com.slepa01.catr;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;


public class CreativeTabConditions {

    /**
     * Добавляет предмет в событие только если существует хотя бы один рецепт,
     * результатом которого является этот предмет.
     */
    public static void addIfRecipeExists(BuildCreativeModeTabContentsEvent event, ItemStack stack) {
        if (hasRecipeFor(stack)) {
            event.accept(stack);
        }
    }

    private static boolean hasRecipeFor(ItemStack stack) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return false;

        RecipeManager manager = minecraft.level.getRecipeManager();
        // Перебираем все рецепты и ищем те, что дают нужный предмет
        return manager.getRecipes().stream()
                .anyMatch(holder -> {
                    ItemStack result = holder.value().getResultItem(minecraft.level.registryAccess());
                    return ItemStack.isSameItemSameComponents(result, stack);
                });
    }
    public static void addIfModsNotLoaded(BuildCreativeModeTabContentsEvent event,
                                          ItemStack stack, String... modIds) {
        for (String id : modIds) {
            if (ModList.get().isLoaded(id)) {
                return; // хотя бы один мод загружен — не добавляем
            }
        }
        // все моды отсутствуют
        event.accept(stack);
    }
}
