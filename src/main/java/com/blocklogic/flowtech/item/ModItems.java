package com.blocklogic.flowtech.item;

import com.blocklogic.flowtech.FlowTech;
import com.blocklogic.flowtech.item.custom.PadWrenchItem;
import com.blocklogic.flowtech.item.custom.VoidFilterItem;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FlowTech.MODID);

    public static final DeferredItem<Item> BOA_MODULE = ITEMS.register("bane_of_arthropods_module",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> FIRE_ASPECT_MODULE = ITEMS.register("fire_aspect_module",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> LOOTING_MODULE = ITEMS.register("looting_module",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SHARPNESS_MODULE = ITEMS.register("sharpness_module",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SMITE_MODULE = ITEMS.register("smite_module",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> COLLECTION_RADIUS_INCREASE_MODULE = ITEMS.register("collection_radius_increase_module",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> VOID_FILTER_MODULE = ITEMS.register("void_filter_module",
            () -> new VoidFilterItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> PAD_WRENCH = ITEMS.register("pad_wrench",
            () -> new PadWrenchItem(new Item.Properties()));

    public static final DeferredItem<Item> RAW_UMBRITE = ITEMS.register("raw_umbrite",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> UMBRITE_INGOT = ITEMS.register("umbrite_ingot",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> UMBRITE_NUGGET = ITEMS.register("umbrite_nugget",
            () -> new Item(new Item.Properties()));

    public static void register (IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
