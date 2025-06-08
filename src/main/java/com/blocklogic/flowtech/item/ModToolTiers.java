package com.blocklogic.flowtech.item;

import com.blocklogic.flowtech.util.ModTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class ModToolTiers {
    public static final Tier UMBRITE = new SimpleTier(ModTags.Blocks.INCORRECT_FOR_UMBRITE_TOOL,
            1045, 7F, 2F, 15, () -> Ingredient.of(ModItems.UMBRITE_INGOT));
}
