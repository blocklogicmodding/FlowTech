package com.blocklogic.flowtech.data;

import com.blocklogic.flowtech.FlowTech;
import com.blocklogic.flowtech.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

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
        basicItem(ModItems.COLLECTION_RADIUS_INCREASE_MODULE.get());
        basicItem(ModItems.VOID_FILTER_MODULE.get());
        basicItem(ModItems.PAD_WRENCH.get());
        basicItem(ModItems.RAW_UMBRITE.get());
        basicItem(ModItems.UMBRITE_INGOT.get());
        basicItem(ModItems.UMBRITE_NUGGET.get());
    }

    private ItemModelBuilder handheldItem (DeferredItem<?> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/handheld")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, "item/" + item.getId().getPath()));
    }
}
