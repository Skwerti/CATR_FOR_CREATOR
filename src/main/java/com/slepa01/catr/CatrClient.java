package com.slepa01.catr;

import com.slepa01.catr.client.util.DeployerLookState;
import com.slepa01.catr.network.DeployerSwapPacket;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.PacketDistributor;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CatrMod.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = CatrMod.MOD_ID, value = Dist.CLIENT)
public class CatrClient {
    public CatrClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        CatrMod.LOGGER.info("HELLO FROM CLIENT SETUP");
        CatrMod.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    // Правая кнопка мыши в области предмета (цилиндр, по которому высвечивается свечение/информация)
    // выполняет действие деплоера (обмен/забор бура), как если бы игрок взаимодействовал с самим блоком.
    @SubscribeEvent
    static void onUse(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isUseItem()) return;
        if (Minecraft.getInstance().screen != null) return;
        var pos = DeployerLookState.getPos();
        if (pos != null) {
            PacketDistributor.sendToServer(new DeployerSwapPacket(pos));
            event.setCanceled(true);
        }
    }
}
