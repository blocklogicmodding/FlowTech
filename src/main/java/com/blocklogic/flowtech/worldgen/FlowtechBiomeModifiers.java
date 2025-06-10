package com.blocklogic.flowtech.worldgen;

import com.blocklogic.flowtech.FlowTech;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class FlowtechBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_OVERWORLD_UMBRITE_ORE = registerKey("add_overworld_umbrite_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_UMBRITE_ORE = registerKey("add_nether_umbrite_ore");
    public static final ResourceKey<BiomeModifier> ADD_END_UMBRITE_ORE = registerKey("add_end_umbrite_ore");

    public static void bootstrap (BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_OVERWORLD_UMBRITE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(
                        placedFeatures.getOrThrow(FlowtechPlacedFeatures.OVERWORLD_UMBRITE_ORE_PLACED_KEY),
                        placedFeatures.getOrThrow(FlowtechPlacedFeatures.OVERWORLD_UMBRITE_ORE_HIDDEN_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));

        context.register(ADD_NETHER_UMBRITE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_NETHER),
                HolderSet.direct(placedFeatures.getOrThrow(FlowtechPlacedFeatures.NETHER_UMBRITE_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));

        context.register(ADD_END_UMBRITE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_END),
                HolderSet.direct(placedFeatures.getOrThrow(FlowtechPlacedFeatures.END_UMBRITE_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));
    }

    private static ResourceKey<BiomeModifier> registerKey (String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, name));
    }
}
