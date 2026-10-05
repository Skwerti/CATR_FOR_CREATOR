package com.slepa01.catr.client.event;

import com.slepa01.catr.CatrMod;
import com.slepa01.catr.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.glfw.GLFW;

public class RecipeTooltipModifier {

    public RecipeTooltipModifier() {
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        if (stack.getItem() != ModItems.CORPUS_AKM.get()) return;

        // Всегда показываем подсказку
        event.getToolTip().add(Component.translatable("catr.tooltip.press_w"));

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getWindow() == null) return;
        long windowHandle = mc.getWindow().getWindow();
        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_W) != GLFW.GLFW_PRESS) return;

        // Переведённые названия предметов для читабельности
        Component inputName = Component.translatable("item.catr.corpus_akm");
        Component transitionName = Component.translatable("item.catr.incoplete_akm");

        // Переведённое название оружия из мода tacz
        Component gunName = Component.translatable("tacz.gun.ak47.name");

        // Формируем рецепт как в JEI: структурированно с цветами, стрелками и секциями
        event.getToolTip().add(Component.literal("§6§l[Recipe: " + inputName.getString() + " → " + gunName.getString() + "]"));
        event.getToolTip().add(Component.literal("§7Input: §f" + inputName.getString()));
        event.getToolTip().add(Component.literal("§7Transition: §f" + inputName.getString() + " → " + transitionName.getString()));

        // Шаги сборки с переведёнными названиями компонентов
        event.getToolTip().add(Component.literal("§8─── Sequence Steps ───"));
        event.getToolTip().add(Component.literal("§71) §f" + transitionName.getString() + " + §e" + Component.translatable("item.catr.butt_weapon").getString() + " §f→ " + transitionName.getString()));
        event.getToolTip().add(Component.literal("§72) §f" + transitionName.getString() + " + §e" + Component.translatable("item.catr.wooden_handle").getString() + " §f→ " + transitionName.getString()));
        event.getToolTip().add(Component.literal("§73) §f" + transitionName.getString() + " + §e" + Component.translatable("item.catr.store_weapon").getString() + " §f→ " + transitionName.getString()));
        event.getToolTip().add(Component.literal("§74) §f" + transitionName.getString() + " + §e" + Component.translatable("item.catr.barrel_weapon").getString() + " §f→ " + transitionName.getString()));
        event.getToolTip().add(Component.literal("§75) §f" + transitionName.getString() + " + §e" + Component.translatable("item.catr.the_return_mechanism").getString() + " §f→ " + transitionName.getString()));
        event.getToolTip().add(Component.literal("§76) §f" + transitionName.getString() + " + §e" + Component.translatable("item.catr.shutter_rifle").getString() + " §f→ " + transitionName.getString()));

        event.getToolTip().add(Component.literal("§8─── Final Output ───"));
        event.getToolTip().add(Component.literal("§eResult: §f§ltacz:modern_kinetic_gun§r (§6" + gunName.getString() + "§r)"));
        event.getToolTip().add(Component.literal("§8  • GunId: §6" + gunName.getString() + "§8  • Mode: AUTO"));
        event.getToolTip().add(Component.literal("§8  • Ammo: 30  • HasBulletInBarrel: false"));
    }
}
