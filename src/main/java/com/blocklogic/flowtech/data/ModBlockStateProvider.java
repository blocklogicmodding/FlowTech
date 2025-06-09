package com.blocklogic.flowtech.data;

import com.blocklogic.flowtech.FlowTech;
import com.blocklogic.flowtech.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, FlowTech.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.UMBRITE_BLOCK);
        blockWithItem(ModBlocks.UMBRITE_STONE_ORE_BLOCK);
        blockWithItem(ModBlocks.UMBRITE_DEEPSLATE_ORE_BLOCK);
        blockWithItem(ModBlocks.UMBRITE_NETHER_ORE_BLOCK);
        blockWithItem(ModBlocks.UMBRITE_END_ORE_BLOCK);
        blockWithItem(ModBlocks.RAW_UMBRITE_BLOCK);
    }

    private void blockWithItem (DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
