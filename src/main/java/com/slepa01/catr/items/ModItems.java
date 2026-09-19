package com.slepa01.catr.items;

import com.slepa01.catr.CatrMod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CatrMod.MOD_ID);
    public static final DeferredItem<Item> IRON_DRILL_BIT = ITEMS.register("iron_drill_bit",
            () -> new DiamondDrillBitItem(new Item.Properties()
                    .durability(2)
                    .setNoRepair()
            ));
    public static final DeferredItem<Item> STORE_WEAPON = ITEMS.register("store_weapon",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_STORE_WEAPON = ITEMS.register("incoplete_store_weapon",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BARREL_WEAPON = ITEMS.register("barrel_weapon",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_BARREL_WEAPON = ITEMS.register("incoplete_barrel_weapon",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> THE_RETURN_MECHANISM = ITEMS.register("the_return_mechanism",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_THE_RETURN_MECHANISM = ITEMS.register("incoplete_the_return_mechanism",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SHUTTER_RIFLE = ITEMS.register("shutter_rifle",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_SHUTTER_RIFLE = ITEMS.register("incoplete_shutter_rifle",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BUTT_WEAPON = ITEMS.register("butt_weapon",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_BUTT_WEAPON = ITEMS.register("incoplete_butt_weapon",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WOODEN_HANDLE = ITEMS.register("wooden_handle",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_WOODEN_HANDLE = ITEMS.register("incoplete_wooden_handle",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_AKM = ITEMS.register("corpus_akm",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_M4A1 = ITEMS.register("corpus_m4a1",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_HK_G3 = ITEMS.register("corpus_hk_g3",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_RHINO_REVOLVER = ITEMS.register("corpus_rhino_revolver",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_GLOCK_17 = ITEMS.register("corpus_glock_17",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_CZ_75 = ITEMS.register("corpus_cz_75",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_AKM = ITEMS.register("incoplete_akm",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> IRON_HANDLE = ITEMS.register("iron_handle",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_IRON_HANDLE = ITEMS.register("incoplete_iron_handle",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> IRON_BUTT_WEAPON = ITEMS.register("iron_butt_weapon",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_IRON_BUTT_WEAPON = ITEMS.register("incoplete_iron_butt_weapon",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_M4A1 = ITEMS.register("incoplete_m4a1",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_HK_G3 = ITEMS.register("incoplete_hk_g3",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DRUM_REVOLVER = ITEMS.register("drum_revolver",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_DRUM_REVOLVER = ITEMS.register("incoplete_drum_revolver",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_RHINO_REVOLVER = ITEMS.register("incoplete_rhino_revolver",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SHUTTER_PISTOLS = ITEMS.register("shutter_pistols",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_SHUTTER_PISTOLS = ITEMS.register("incoplete_shutter_pistols",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_GLOCK_17 = ITEMS.register("incoplete_glock_17",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_CZ_75 = ITEMS.register("incoplete_cz_75",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_P320 = ITEMS.register("corpus_p320",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_P320 = ITEMS.register("incoplete_p320",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_B93R = ITEMS.register("corpus_b93r",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_B93R = ITEMS.register("incoplete_b93r",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_M9A4 = ITEMS.register("corpus_m9a4",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_M9A4 = ITEMS.register("incoplete_m9a4",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_SCAR_L = ITEMS.register("corpus_scar_l",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_SCAR_L = ITEMS.register("incoplete_scar_l",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_M16A4 = ITEMS.register("corpus_m16a4",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_M16A4 = ITEMS.register("incoplete_m16a4",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_M16A1 = ITEMS.register("corpus_m16a1",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_M16A1 = ITEMS.register("incoplete_m16a1",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_HK416D = ITEMS.register("corpus_hk416d",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_HK416D = ITEMS.register("incoplete_hk416d",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_AUG = ITEMS.register("corpus_aug",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_AUG = ITEMS.register("incoplete_aug",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_TYPE_81 = ITEMS.register("corpus_type_81",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_TYPE_81 = ITEMS.register("incoplete_type_81",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CORPUS_G36K = ITEMS.register("corpus_g36k",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INCOPLETE_G36K = ITEMS.register("incoplete_g36k",
            () -> new Item(new Item.Properties()));
}
