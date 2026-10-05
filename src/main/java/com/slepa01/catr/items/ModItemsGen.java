package com.slepa01.catr.items;

import com.slepa01.catr.CatrMod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

public class ModItemsGen {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CatrMod.MOD_ID);
    public static final DeferredItem<Item> CORPUS_QBZ_191 = ITEMS.register("corpus_qbz_191",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_QBZ_191 = ITEMS.register("incoplete_qbz_191",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_QBZ_95 = ITEMS.register("corpus_qbz_95",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_QBZ_95 = ITEMS.register("incoplete_qbz_95",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PART_WOODEN_BUTT_RPG7 = ITEMS.register("part_wooden_butt_rpg7",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BUTT_RPG7 = ITEMS.register("butt_rpg7",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_PART_WOODEN_BUTT_RPG7 = ITEMS.register("incoplete_part_wooden_butt_rpg7",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_BUTT_RPG7 = ITEMS.register("incoplete_butt_rpg7",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ARMY_SPRING = ITEMS.register("army_spring",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_ARMY_SPRING = ITEMS.register("incoplete_army_spring",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_RPG7 = ITEMS.register("corpus_rpg7",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_RPG7 = ITEMS.register("incoplete_rpg7",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PRESSED_GOLD = ITEMS.register("pressed_gold",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_PRESSED_GOLD = ITEMS.register("incoplete_pressed_gold",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_DEAGLE_GOLDEN = ITEMS.register("corpus_deagle_golden",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_DEAGLE_GOLDEN = ITEMS.register("incoplete_deagle_golden",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLDEN_SHUTTER = ITEMS.register("golden_shutter",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_GOLDEN_SHUTTER = ITEMS.register("incoplete_golden_shutter",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_ROD = ITEMS.register("steel_rod",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_SHEET = ITEMS.register("steel_sheet",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SHUTTER_DEAGLE = ITEMS.register("shutter_deagle",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_SHUTTER_DEAGLE = ITEMS.register("incoplete_shutter_deagle",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_DEAGLE = ITEMS.register("incoplete_deagle",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DURABLE_STORE_WEAPON = ITEMS.register("durable_store_weapon",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_DURABLE_STORE_WEAPON = ITEMS.register("incoplete_durable_store_weapon",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> DIAMOND_DRILL_BIT = ITEMS.register("diamond_drill_bit",
            () -> new DiamondDrillBitItem(new Item.Properties()
                    .durability(38)
                    .setNoRepair()
            ));
    public static final DeferredItem<Item> CORPUS_DEAGLE = ITEMS.register("corpus_deagle",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BARREL_SHOTGUN = ITEMS.register("barrel_shotgun",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_BARREL_SHOTGUN = ITEMS.register("incoplete_barrel_shotgun",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_DB_SHORT = ITEMS.register("incoplete_db_short",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_DB_LONG = ITEMS.register("incoplete_db_long",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_M870 = ITEMS.register("incoplete_m870",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_M870 = ITEMS.register("corpus_m870",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BARREL_SHORT = ITEMS.register("barrel_short",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_BARREL_SHORT = ITEMS.register("incoplete_barrel_short",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FREE_SHUTTER = ITEMS.register("free_shutter",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_FREE_SHUTTER = ITEMS.register("incoplete_free_shutter",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_HK_MP5A5 = ITEMS.register("corpus_hk_mp5a5",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_HK_MP5A5 = ITEMS.register("incoplete_hk_mp5a5",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_UZI = ITEMS.register("corpus_uzi",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_UZI = ITEMS.register("incoplete_uzi",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_VECTOR45 = ITEMS.register("corpus_vector45",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_VECTOR45 = ITEMS.register("incoplete_vector45",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_P90 = ITEMS.register("corpus_p90",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_P90 = ITEMS.register("incoplete_p90",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DURABLE_SHUTTER = ITEMS.register("durable_shutter",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_DURABLE_SHUTTER = ITEMS.register("incoplete_durable_shutter",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_RPK = ITEMS.register("corpus_rpk",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_RPK = ITEMS.register("incoplete_rpk",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> STEEL_DRILL_BIT = ITEMS.register("steel_drill_bit",
            () -> new DiamondDrillBitItem(new Item.Properties()
                    .durability(16)
                    .setNoRepair()
            ));
    public static final DeferredItem<Item> STEEL_NUGGET = ITEMS.register("steel_nugget",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_INGOT = ITEMS.register("steel_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SPRING = ITEMS.register("spring",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_FN_FAL = ITEMS.register("corpus_fn_fal",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_FN_FAL = ITEMS.register("incoplete_fn_fal",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_SCAR_H = ITEMS.register("corpus_scar_h",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_SCAR_H = ITEMS.register("incoplete_scar_h",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_HK_MK23 = ITEMS.register("corpus_hk_mk23",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_HK_MK23 = ITEMS.register("incoplete_hk_mk23",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_M1911 = ITEMS.register("corpus_m1911",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_M1911 = ITEMS.register("incoplete_m1911",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_SPR15HB = ITEMS.register("corpus_spr15hb",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_SPR15HB = ITEMS.register("incoplete_spr15hb",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_SKS_TACTICAL = ITEMS.register("corpus_sks_tactical",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_SKS_TACTICAL = ITEMS.register("incoplete_sks_tactical",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> IRON_BLANK = ITEMS.register("iron_blank",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_M249 = ITEMS.register("corpus_m249",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_M249 = ITEMS.register("incoplete_m249",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MACHINE_GUN_MAGAZINE = ITEMS.register("machine_gun_magazine",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_MACHINE_GUN_MAGAZINE = ITEMS.register("incoplete_machine_gun_magazine",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_ENHANCED_RETURN_MECHANISM = ITEMS.register("incoplete_enhanced_return_mechanism",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ENHANCED_RETURN_MECHANISM = ITEMS.register("enhanced_return_mechanism",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_FN_EVOLYS = ITEMS.register("corpus_fn_evolys",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_FN_EVOLYS = ITEMS.register("incoplete_fn_evolys",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_BLANK = ITEMS.register("steel_blank",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_TAURUS943 = ITEMS.register("corpus_taurus943",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_TAURUS943 = ITEMS.register("incoplete_taurus943",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_KAR98 = ITEMS.register("corpus_kar98",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_KAR98 = ITEMS.register("incoplete_kar98",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_M700 = ITEMS.register("corpus_m700",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_M700 = ITEMS.register("incoplete_m700",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_TIMELESS50 = ITEMS.register("corpus_timeless50",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_TIMELESS50 = ITEMS.register("incoplete_timeless50",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_STEEL_BLANK = ITEMS.register("gold_steel_blank",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_MK14 = ITEMS.register("corpus_mk14",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_MK14 = ITEMS.register("incoplete_mk14",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_M1014 = ITEMS.register("corpus_m1014",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_M1014 = ITEMS.register("incoplete_m1014",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_M320 = ITEMS.register("corpus_m320",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_M320 = ITEMS.register("incoplete_m320",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_DIAMOND_COATING = ITEMS.register("steel_diamond_coating",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_DIAMOND_BLANK = ITEMS.register("steel_diamond_blank",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_SHEET = ITEMS.register("netherite_sheet",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_DIAMOND_BLANK = ITEMS.register("netherite_diamond_blank",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_SPAS_12 = ITEMS.register("corpus_spas_12",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_SPAS_12 = ITEMS.register("incoplete_spas_12",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_SPRINGFIELD1873 = ITEMS.register("corpus_springfield1873",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_SPRINGFIELD1873 = ITEMS.register("incoplete_springfield1873",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_AA12 = ITEMS.register("corpus_aa12",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_AA12 = ITEMS.register("incoplete_aa12",
            () -> new Item(new Item.Properties()));
}