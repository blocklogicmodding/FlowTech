package com.blocklogic.flowtech.util;

import com.blocklogic.flowtech.FlowTech;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> FLOWTECH_PADS = createTag("flowtech_pads");
        public static final TagKey<Block> FLOWTECH_MACHINES = createTag("flowtech_machines");
        public static final TagKey<Block> ORE_BLOCKS_UMBRITE = createTag("ores/umbrite");
        public static final TagKey<Block> RAW_BLOCKS_UMBRITE = createTag("raw_blocks/umbrite");

        public static final TagKey<Block> NEEDS_UMBRITE_TOOL = createTag("needs_umbrite_tool");
        public static final TagKey<Block> INCORRECT_FOR_UMBRITE_TOOL = createTag("incorrect_for_umbrite_tool");

        private static TagKey<Block> createTag (String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> ORES_UMBRITE = createTag("ores/umbrite");
        public static final TagKey<Item> INGOTS_UMBRITE = createTag("ingots/umbrite");
        public static final TagKey<Item> NUGGETS_UMBRITE = createTag("nuggets/umbrite");
        public static final TagKey<Item> RAW_MATERIALS_UMBRITE = createTag("raw_materials/umbrite");
        public static final TagKey<Item> FLOWTECH_TOOLS = createTag("flowtech_tools");
        public static final TagKey<Item> FLOWTECH_MODULES = createTag("flowtech_modules");

        private static TagKey<Item> createTag (String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, name));
        }
    }
}
