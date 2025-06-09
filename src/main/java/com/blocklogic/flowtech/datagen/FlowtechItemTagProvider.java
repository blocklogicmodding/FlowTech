package com.blocklogic.flowtech.datagen;

import com.blocklogic.flowtech.FlowTech;
import com.blocklogic.flowtech.block.ModBlocks;
import com.blocklogic.flowtech.item.ModItems;
import com.blocklogic.flowtech.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FlowtechItemTagProvider extends ItemTagsProvider {
    public FlowtechItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, FlowTech.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(Tags.Items.ORES)
                .add(ModBlocks.UMBRITE_STONE_ORE_BLOCK.get().asItem())
                .add(ModBlocks.UMBRITE_DEEPSLATE_ORE_BLOCK.get().asItem())
                .add(ModBlocks.UMBRITE_NETHER_ORE_BLOCK.get().asItem())
                .add(ModBlocks.UMBRITE_END_ORE_BLOCK.get().asItem());

        tag(Tags.Items.INGOTS)
                .add(ModItems.UMBRITE_INGOT.get());

        tag(Tags.Items.RAW_MATERIALS)
                .add(ModItems.RAW_UMBRITE.get());

        tag(ModTags.Items.ORES_UMBRITE)
                .add(ModBlocks.UMBRITE_STONE_ORE_BLOCK.get().asItem())
                .add(ModBlocks.UMBRITE_DEEPSLATE_ORE_BLOCK.get().asItem())
                .add(ModBlocks.UMBRITE_NETHER_ORE_BLOCK.get().asItem())
                .add(ModBlocks.UMBRITE_END_ORE_BLOCK.get().asItem());

        tag(ModTags.Items.INGOTS_UMBRITE)
                .add(ModItems.UMBRITE_INGOT.get());

        tag(ModTags.Items.NUGGETS_UMBRITE)
                .add(ModItems.UMBRITE_NUGGET.get());

        tag(ModTags.Items.RAW_MATERIALS_UMBRITE)
                .add(ModItems.RAW_UMBRITE.get());

        tag(ModTags.Items.FLOWTECH_MODULES)
                .add(ModItems.BOA_MODULE.get())
                .add(ModItems.FIRE_ASPECT_MODULE.get())
                .add(ModItems.SHARPNESS_MODULE.get())
                .add(ModItems.SMITE_MODULE.get())
                .add(ModItems.LOOTING_MODULE.get())
                .add(ModItems.COLLECTION_RADIUS_INCREASE_MODULE.get())
                .add(ModItems.VOID_FILTER_MODULE.get());

        tag(ModTags.Items.FLOWTECH_TOOLS)
                .add(ModItems.PAD_WRENCH.get());
    }
}
