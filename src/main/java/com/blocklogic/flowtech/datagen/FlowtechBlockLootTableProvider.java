package com.blocklogic.flowtech.datagen;

import com.blocklogic.flowtech.block.ModBlocks;
import com.blocklogic.flowtech.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class FlowtechBlockLootTableProvider extends BlockLootSubProvider {
    protected FlowtechBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.UMBRITE_BLOCK.get());
        dropSelf(ModBlocks.FLOWTECH_COLLECTOR.get());
        dropSelf(ModBlocks.FLOWTECH_CONTROLLER.get());
        dropSelf(ModBlocks.FAST_FLOW_PAD.get());
        dropSelf(ModBlocks.FASTER_FLOW_PAD.get());
        dropSelf(ModBlocks.FASTEST_FLOW_PAD.get());
        dropSelf(ModBlocks.ATTACK_PAD.get());
        dropSelf(ModBlocks.RAW_UMBRITE_BLOCK.get());

        add(ModBlocks.UMBRITE_STONE_ORE_BLOCK.get(), block -> createOreDrop(ModBlocks.UMBRITE_STONE_ORE_BLOCK.get(), ModItems.RAW_UMBRITE.get()));
        add(ModBlocks.UMBRITE_DEEPSLATE_ORE_BLOCK.get(), block -> createOreDrop(ModBlocks.UMBRITE_DEEPSLATE_ORE_BLOCK.get(), ModItems.RAW_UMBRITE.get()));
        add(ModBlocks.UMBRITE_NETHER_ORE_BLOCK.get(), block -> createOreDrop(ModBlocks.UMBRITE_NETHER_ORE_BLOCK.get(), ModItems.RAW_UMBRITE.get()));
        add(ModBlocks.UMBRITE_END_ORE_BLOCK.get(), block -> createOreDrop(ModBlocks.UMBRITE_END_ORE_BLOCK.get(), ModItems.RAW_UMBRITE.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
