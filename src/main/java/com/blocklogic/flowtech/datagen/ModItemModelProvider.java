package com.blocklogic.flowtech.datagen;

import com.blocklogic.flowtech.FlowTech;
import com.blocklogic.flowtech.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, FlowTech.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.BOA_MODULE.get());
        basicItem(ModItems.FIRE_ASPECT_MODULE.get());
        basicItem(ModItems.SHARPNESS_MODULE.get());
        basicItem(ModItems.SMITE_MODULE.get());
        basicItem(ModItems.LOOTING_MODULE.get());
        basicItem(ModItems.STACK_SIZE_MODULE.get());
        basicItem(ModItems.PICKUP_ZONE_SIZE_MODULE.get());
        basicItem(ModItems.ITEM_FILTER_MODULE.get());
        basicItem(ModItems.PAD_WRENCH.get());
        basicItem(ModItems.RAW_UMBRITE.get());
        basicItem(ModItems.UMBRITE_INGOT.get());
        basicItem(ModItems.UMBRITE_NUGGET.get());
    }
}
