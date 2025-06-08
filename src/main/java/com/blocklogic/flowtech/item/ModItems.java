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

    public static final DeferredItem<Item> ITEM_FILTER_MODULE = ITEMS.register("item_filter_module",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> PAD_WRENCH = ITEMS.register("pad_wrench",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> RAW_UMBRITE = ITEMS.register("raw_umbrite",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> UMBRITE_INGOT = ITEMS.register("umbrite_ingot",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> UMBRITE_NUGGET = ITEMS.register("umbrite_nugget",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<SwordItem> UMBRITE_SWORD = ITEMS.register("umbrite_sword",
            () -> new SwordItem(ModToolTiers.UMBRITE, new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.UMBRITE, 3F, -2.4F))));

    public static final DeferredItem<PickaxeItem> UMBRITE_PICKAXE = ITEMS.register("umbrite_pickaxe",
            () -> new PickaxeItem(ModToolTiers.UMBRITE, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(ModToolTiers.UMBRITE, 1F, -2.8F))));

    public static final DeferredItem<AxeItem> UMBRITE_AXE = ITEMS.register("umbrite_axe",
            () -> new AxeItem(ModToolTiers.UMBRITE, new Item.Properties()
                    .attributes(AxeItem.createAttributes(ModToolTiers.UMBRITE, 5F, -3.0F))));

    public static final DeferredItem<ShovelItem> UMBRITE_SHOVEL = ITEMS.register("umbrite_shovel",
            () -> new ShovelItem(ModToolTiers.UMBRITE, new Item.Properties()
                    .attributes(ShovelItem.createAttributes(ModToolTiers.UMBRITE, 1.5F, -3.0F))));

    public static final DeferredItem<HoeItem> UMBRITE_HOE = ITEMS.register("umbrite_hoe",
            () -> new HoeItem(ModToolTiers.UMBRITE, new Item.Properties()
                    .attributes(HoeItem.createAttributes(ModToolTiers.UMBRITE, 0F, -3.0F))));

    public static void register (IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
