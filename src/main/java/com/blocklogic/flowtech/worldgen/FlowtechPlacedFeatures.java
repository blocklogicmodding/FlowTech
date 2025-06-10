package com.blocklogic.flowtech.worldgen;

import com.blocklogic.flowtech.FlowTech;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

public class FlowtechPlacedFeatures {
    public static final ResourceKey<PlacedFeature> OVERWORLD_UMBRITE_ORE_PLACED_KEY = registerKey("overworld_umbrite_ore_placed");
    public static final ResourceKey<PlacedFeature> OVERWORLD_UMBRITE_ORE_HIDDEN_KEY = registerKey("overworld_umbrite_ore_hidden");
    public static final ResourceKey<PlacedFeature> NETHER_UMBRITE_ORE_PLACED_KEY = registerKey("nether_umbrite_ore_placed");
    public static final ResourceKey<PlacedFeature> END_UMBRITE_ORE_PLACED_KEY = registerKey("end_umbrite_ore_placed");

    public static void bootstrap (BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, OVERWORLD_UMBRITE_ORE_PLACED_KEY, configuredFeatures.getOrThrow(FlowtechConfiguredFeatures.OVERWORLD_UMBRITE_ORE_KEY),
                FlowtechOrePlacement.commonOrePlacement(4,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64)
                ))
        );

        register(context, OVERWORLD_UMBRITE_ORE_HIDDEN_KEY, configuredFeatures.getOrThrow(FlowtechConfiguredFeatures.OVERWORLD_UMBRITE_ORE_HIDDEN_KEY),
                FlowtechOrePlacement.commonOrePlacement(2,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-32), VerticalAnchor.absolute(32))
                )
        );

        register(context, NETHER_UMBRITE_ORE_PLACED_KEY, configuredFeatures.getOrThrow(FlowtechConfiguredFeatures.NETHER_UMBRITE_ORE_KEY),
                FlowtechOrePlacement.commonOrePlacement(4, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, END_UMBRITE_ORE_PLACED_KEY, configuredFeatures.getOrThrow(FlowtechConfiguredFeatures.END_UMBRITE_ORE_KEY),
                FlowtechOrePlacement.commonOrePlacement(4, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));
    }

    private static ResourceKey<PlacedFeature> registerKey (String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, name));
    }

    private static void register (BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
