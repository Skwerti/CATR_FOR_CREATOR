package com.slepa01.catr;

import com.slepa01.catr.creativetabs.ModCreativeTabs;
import com.slepa01.catr.datagen.ModItemModelProvider;
import com.slepa01.catr.datagen.ModRecipeProvider;
import com.slepa01.catr.items.DrillBits;
import com.slepa01.catr.items.ModItems;
import com.slepa01.catr.items.ModItemsGen;
import com.slepa01.catr.network.DeployerSwapPacket;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import catr.mixin.DeployerBlockEntityAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("catr")
public class CatrMod
{
    public static final String MOD_ID = "catr";
    public static final Logger LOGGER = LoggerFactory.getLogger("catr");

    public CatrMod(IEventBus eventBus, ModContainer container)
    {
        ModCreativeTabs.CREATIVE_TABS.register(eventBus);
        eventBus.addListener(this::addCreative);
        eventBus.addListener(this::onGatherData);
        ModItems.ITEMS.register(eventBus);
        ModItemsGen.ITEMS.register(eventBus);
        eventBus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.register(this);
        LOGGER.info("CatrMod загружен!");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModCreativeTabs.CORPUS_TAB.get()) {
            event.accept(ModItemsGen.CORPUS_RPG7.get());
            event.accept(ModItemsGen.CORPUS_HK_MP5A5.get());
            event.accept(ModItemsGen.CORPUS_UZI.get());
            event.accept(ModItemsGen.CORPUS_VECTOR45.get());
            event.accept(ModItemsGen.CORPUS_P90.get());
            event.accept(ModItemsGen.CORPUS_RPK.get());
            event.accept(ModItems.CORPUS_AKM.get());
            event.accept(ModItems.CORPUS_M4A1.get());
            event.accept(ModItems.CORPUS_HK_G3.get());
            event.accept(ModItems.CORPUS_GLOCK_17.get());
            event.accept(ModItems.CORPUS_RHINO_REVOLVER.get());
            event.accept(ModItems.CORPUS_CZ_75.get());
            event.accept(ModItems.CORPUS_P320.get());
            event.accept(ModItems.CORPUS_B93R.get());
            event.accept(ModItems.CORPUS_M9A4.get());
            event.accept(ModItems.CORPUS_SCAR_L.get());
            event.accept(ModItems.CORPUS_M16A4.get());
            event.accept(ModItems.CORPUS_M16A1.get());
            event.accept(ModItems.CORPUS_HK416D.get());
            event.accept(ModItems.CORPUS_AUG.get());
            event.accept(ModItems.CORPUS_TYPE_81.get());
            event.accept(ModItems.CORPUS_G36K.get());
            event.accept(ModItemsGen.CORPUS_QBZ_191.get());
            event.accept(ModItemsGen.CORPUS_QBZ_95.get());
            event.accept(ModItemsGen.CORPUS_DEAGLE.get());
            event.accept(ModItemsGen.CORPUS_DEAGLE_GOLDEN.get());
            event.accept(ModItemsGen.CORPUS_FN_FAL.get());
            event.accept(ModItemsGen.CORPUS_SPR15HB.get());
            event.accept(ModItemsGen.CORPUS_SCAR_H.get());
            event.accept(ModItemsGen.CORPUS_HK_MK23.get());
            event.accept(ModItemsGen.CORPUS_M1911.get());
            event.accept(ModItemsGen.CORPUS_SKS_TACTICAL.get());
            event.accept(ModItemsGen.CORPUS_M870.get());
        } else if (event.getTab() == ModCreativeTabs.COMPONENTS_WEAPON_TAB.get()) {
            event.accept(ModItems.THE_RETURN_MECHANISM.get());
            event.accept(ModItems.BUTT_WEAPON.get());
            event.accept(ModItems.WOODEN_HANDLE.get());
            event.accept(ModItems.IRON_HANDLE.get());
            event.accept(ModItems.IRON_BUTT_WEAPON.get());
            event.accept(ModItems.DRUM_REVOLVER.get());
            event.accept(ModItemsGen.PART_WOODEN_BUTT_RPG7.get());
            event.accept(ModItemsGen.BUTT_RPG7.get());
            event.accept(ModItemsGen.ARMY_SPRING.get());
            CreativeTabConditions.addIfModsNotLoaded(
                    event,
                    new ItemStack(ModItemsGen.SPRING.get()),
                    "simulated"
            );
            event.accept(ModItems.STORE_WEAPON.get());
            event.accept(ModItemsGen.DURABLE_STORE_WEAPON.get());
            event.accept(ModItems.BARREL_WEAPON.get());
            event.accept(ModItemsGen.BARREL_SHOTGUN.get());
            event.accept(ModItemsGen.BARREL_SHORT.get());
            event.accept(ModItems.SHUTTER_PISTOLS.get());
            event.accept(ModItemsGen.GOLDEN_SHUTTER.get());
            event.accept(ModItemsGen.SHUTTER_DEAGLE.get());
            event.accept(ModItemsGen.FREE_SHUTTER.get());
            event.accept(ModItems.SHUTTER_RIFLE.get());
            event.accept(ModItemsGen.DURABLE_SHUTTER.get());
            event.accept(ModItems.IRON_DRILL_BIT.get());
            event.accept(ModItemsGen.STEEL_DRILL_BIT.get());
            event.accept(ModItemsGen.DIAMOND_DRILL_BIT.get());
            event.accept(ModItemsGen.STEEL_ROD.get());
            event.accept(ModItemsGen.STEEL_SHEET.get());
            CreativeTabConditions.addIfModsNotLoaded(
                    event,
                    new ItemStack(ModItemsGen.STEEL_NUGGET.get()),
                    "createnuclear", "createbigcannons"
            );
            CreativeTabConditions.addIfModsNotLoaded(
                    event,
                    new ItemStack(ModItemsGen.STEEL_INGOT.get()),
                    "createnuclear", "createbigcannons"
            );
            event.accept(ModItemsGen.PRESSED_GOLD.get());
        }
    }
    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1.0")
                .playToServer(DeployerSwapPacket.TYPE, DeployerSwapPacket.STREAM_CODEC, DeployerSwapPacket::handle);
    }

    // Блокируем нативное взаимодействие с деплоером (Create кладёт/забирает предмет
    // из руки в деплоер), когда хотя бы один из предметов — бур. Иначе при клике по
    // деплоеру срабатывает нативная замена, а при клике по текстуре — наш пакет, и
    // предметы в руке/деплоере дважды меняются местами (багообразная замена).
    // Обмен бурами происходит только через DeployerSwapPacket (клик по текстуре).
    @net.neoforged.bus.api.SubscribeEvent
    public void onDeployerRightClick(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide()) return;
        BlockEntity be = level.getBlockEntity(event.getPos());
        if (!(be instanceof DeployerBlockEntity dbe)) return;
        ItemStack hand = event.getEntity().getItemInHand(event.getHand());
        ItemStack held = ((DeployerBlockEntityAccessor) dbe).getHeldItem();
        if (DrillBits.isDrill(hand.getItem()) || DrillBits.isDrill(held.getItem())) {
            event.setCanceled(true);
        }
    }

    private void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var existingFileHelper = event.getExistingFileHelper();
        var packOutput = generator.getPackOutput();

        // Клиентские провайдеры
        generator.addProvider(
                event.includeClient(),
                new ModItemModelProvider(packOutput, MOD_ID, existingFileHelper)
        );

        // Серверные провайдеры
        generator.addProvider(
                event.includeServer(),
                new ModRecipeProvider(packOutput, event.getLookupProvider())
        );
    }
}

