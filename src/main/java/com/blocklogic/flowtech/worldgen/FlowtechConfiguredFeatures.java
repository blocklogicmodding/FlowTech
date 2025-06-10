package com.blocklogic.flowtech.worldgen;

import com.blocklogic.flowtech.FlowTech;
import com.blocklogic.flowtech.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class FlowtechConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_UMBRITE_ORE_KEY = registerKey("overworld_umbrite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_UMBRITE_ORE_HIDDEN_KEY = registerKey("overworld_umbrite_ore_hidden");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_UMBRITE_ORE_KEY = registerKey("nether_umbrite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_UMBRITE_ORE_KEY = registerKey("end_umbrite_ore");

    public static void bootstrap (BootstrapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest netherrackReplaceables = new BlockMatchTest(Blocks.NETHERRACK);
        RuleTest endstoneReplaceables = new BlockMatchTest(Blocks.END_STONE);

        List<OreConfiguration.TargetBlockState> overworldUmbriteOres = List.of(
                OreConfiguration.target(stoneReplaceables, ModBlocks.UMBRITE_STONE_ORE_BLOCK.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, ModBlocks.UMBRITE_DEEPSLATE_ORE_BLOCK.get().defaultBlockState()));

        OreConfiguration hiddenUmbriteConfig = new OreConfiguration(overworldUmbriteOres, 12, 0.9f);

        register(context, OVERWORLD_UMBRITE_ORE_HIDDEN_KEY, Feature.ORE, hiddenUmbriteConfig);
        register(context, OVERWORLD_UMBRITE_ORE_KEY, Feature.ORE, new OreConfiguration(overworldUmbriteOres, 12));
        register(context, NETHER_UMBRITE_ORE_KEY, Feature.ORE, new OreConfiguration(netherrackReplaceables, ModBlocks.UMBRITE_NETHER_ORE_BLOCK.get().defaultBlockState(), 12));
        register(context, END_UMBRITE_ORE_KEY, Feature.ORE, new OreConfiguration(endstoneReplaceables, ModBlocks.UMBRITE_END_ORE_BLOCK.get().defaultBlockState(), 12));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey (String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(FlowTech.MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register (BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
