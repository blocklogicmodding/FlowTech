package com.blocklogic.flowtech.item;

import com.blocklogic.flowtech.FlowTech;
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

    public static final DeferredItem<Item> STACK_SIZE_MODULE = ITEMS.register("stack_size_increase_module",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> PICKUP_ZONE_SIZE_MODULE = ITEMS.register("pickup_zone_size_increase_module",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> VOID_FILTER_MODULE = ITEMS.register("void_filter_module",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> PAD_WRENCH = ITEMS.register("pad_wrench",
            () -> new Item(new Item.Properties()));

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
