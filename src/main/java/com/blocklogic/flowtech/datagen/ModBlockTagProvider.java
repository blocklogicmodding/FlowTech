package com.blocklogic.flowtech.datagen;

import com.blocklogic.flowtech.FlowTech;
import com.blocklogic.flowtech.block.ModBlocks;
import com.blocklogic.flowtech.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, FlowTech.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.ATTACK_PAD.get())
                .add(ModBlocks.FAST_FLOW_PAD.get())
                .add(ModBlocks.FASTER_FLOW_PAD.get())
                .add(ModBlocks.FASTEST_FLOW_PAD.get())
                .add(ModBlocks.FLOWTECH_CONTROLLER.get())
                .add(ModBlocks.FLOWTECH_COLLECTOR.get())
                .add(ModBlocks.UMBRITE_BLOCK.get())
                .add(ModBlocks.UMBRITE_STONE_ORE_BLOCK.get())
                .add(ModBlocks.UMBRITE_DEEPSLATE_ORE_BLOCK.get())
                .add(ModBlocks.UMBRITE_NETHER_ORE_BLOCK.get())
                .add(ModBlocks.UMBRITE_END_ORE_BLOCK.get())
                .add(ModBlocks.RAW_UMBRITE_BLOCK.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.UMBRITE_STONE_ORE_BLOCK.get())
                .add(ModBlocks.UMBRITE_DEEPSLATE_ORE_BLOCK.get())
                .add(ModBlocks.UMBRITE_NETHER_ORE_BLOCK.get())
                .add(ModBlocks.UMBRITE_END_ORE_BLOCK.get());

        tag(ModTags.Blocks.NEEDS_UMBRITE_TOOL)
                .addTag(BlockTags.NEEDS_IRON_TOOL);

        tag(ModTags.Blocks.INCORRECT_FOR_UMBRITE_TOOL)
                .addTag(BlockTags.NEEDS_IRON_TOOL)
                .remove(ModTags.Blocks.NEEDS_UMBRITE_TOOL);

        tag(ModTags.Blocks.FLOWTECH_PADS)
                .add(ModBlocks.ATTACK_PAD.get())
                .add(ModBlocks.FAST_FLOW_PAD.get())
                .add(ModBlocks.FASTER_FLOW_PAD.get())
                .add(ModBlocks.FASTEST_FLOW_PAD.get());

        tag(ModTags.Blocks.ORE_BLOCKS_UMBRITE)
                .add(ModBlocks.UMBRITE_STONE_ORE_BLOCK.get())
                .add(ModBlocks.UMBRITE_DEEPSLATE_ORE_BLOCK.get())
                .add(ModBlocks.UMBRITE_NETHER_ORE_BLOCK.get())
                .add(ModBlocks.UMBRITE_END_ORE_BLOCK.get());

        tag(ModTags.Blocks.FLOWTECH_MACHINES)
                .add(ModBlocks.FLOWTECH_CONTROLLER.get())
                .add(ModBlocks.FLOWTECH_COLLECTOR.get());

        tag(ModTags.Blocks.RAW_BLOCKS_UMBRITE)
                .add(ModBlocks.RAW_UMBRITE_BLOCK.get());
    }
}
