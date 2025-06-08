package com.blocklogic.flowtech.datagen;

import com.blocklogic.flowtech.FlowTech;
import com.blocklogic.flowtech.block.ModBlocks;
import com.blocklogic.flowtech.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        List<ItemLike> UMBRITE_SMELTABLES = List.of(ModItems.RAW_UMBRITE, ModBlocks.UMBRITE_STONE_ORE_BLOCK, ModBlocks.UMBRITE_DEEPSLATE_ORE_BLOCK, ModBlocks.UMBRITE_NETHER_ORE_BLOCK, ModBlocks.UMBRITE_END_ORE_BLOCK);
        List<ItemLike> UMBRITE_RAW_BLOCK_SMELTABLES = List.of(ModBlocks.RAW_UMBRITE_BLOCK);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RAW_UMBRITE_BLOCK.get())
                .pattern("UUU")
                .pattern("UUU")
                .pattern("UUU")
                .define('U', ModItems.RAW_UMBRITE.get())
                .unlockedBy("has_raw_umbrite", has(ModItems.RAW_UMBRITE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.RAW_UMBRITE.get(), 9)
                .requires(ModBlocks.RAW_UMBRITE_BLOCK)
                .unlockedBy("has_raw_umbrite_block", has(ModBlocks.RAW_UMBRITE_BLOCK))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.UMBRITE_BLOCK.get())
                .pattern("UUU")
                .pattern("UUU")
                .pattern("UUU")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .unlockedBy("has_umbrite_ingot", has(ModItems.UMBRITE_INGOT))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.UMBRITE_INGOT.get(), 9)
                .requires(ModBlocks.UMBRITE_BLOCK)
                .unlockedBy("has_umbrite_block", has(ModBlocks.UMBRITE_BLOCK))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.UMBRITE_INGOT.get())
                .pattern("NNN")
                .pattern("NNN")
                .pattern("NNN")
                .define('N', ModItems.UMBRITE_NUGGET.get())
                .unlockedBy("has_umbrite_nugget", has(ModItems.UMBRITE_NUGGET))
                .save(recipeOutput, "umbrite_ingot_from_nuggets");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.UMBRITE_NUGGET.get(), 9)
                .requires(ModItems.UMBRITE_INGOT)
                .unlockedBy("has_umbrite_ingot", has(ModItems.UMBRITE_INGOT))
                .save(recipeOutput);


        oreSmelting(recipeOutput, UMBRITE_SMELTABLES, RecipeCategory.MISC, ModItems.UMBRITE_INGOT.get(), 0.25F, 200, "umbrite");
        oreBlasting(recipeOutput, UMBRITE_SMELTABLES, RecipeCategory.MISC, ModItems.UMBRITE_INGOT.get(), 0.25F, 100, "umbrite");

        oreSmelting(recipeOutput, UMBRITE_RAW_BLOCK_SMELTABLES, RecipeCategory.MISC, ModBlocks.UMBRITE_BLOCK.get(), 0.5F, 300, "umbrite");
        oreBlasting(recipeOutput, UMBRITE_SMELTABLES, RecipeCategory.MISC, ModBlocks.UMBRITE_BLOCK.get(), 0.5F, 150, "umbrite");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FLOWTECH_CONTROLLER.get())
                .pattern("UCU")
                .pattern("IBI")
                .pattern("UCU")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COMPARATOR)
                .define('B', Items.REDSTONE_BLOCK)
                .unlockedBy("has_umbrite_ingot", has(ModItems.UMBRITE_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FLOWTECH_COLLECTOR.get())
                .pattern("UEU")
                .pattern("PCP")
                .pattern("UHU")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('P', Items.ENDER_PEARL)
                .define('H', Items.HOPPER)
                .define('C', Tags.Items.CHESTS)
                .define('E', Items.ENDER_EYE)
                .unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ATTACK_PAD.get(), 4)
                .pattern("USU")
                .pattern("AQA")
                .pattern("USU")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('S', Items.DIAMOND_SWORD)
                .define('A', Items.DIAMOND_AXE)
                .define('Q', Items.QUARTZ_BLOCK)
                .unlockedBy("has_quartz", has(Items.QUARTZ))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FAST_FLOW_PAD.get(), 8)
                .pattern("IUI")
                .pattern("UQU")
                .pattern("SSS")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('S', Tags.Items.STONES)
                .define('Q', Items.QUARTZ_BLOCK)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_quartz", has(Items.QUARTZ))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FASTER_FLOW_PAD.get(), 8)
                .pattern("DUD")
                .pattern("UFU")
                .pattern("SSS")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('D', Items.DIAMOND)
                .define('S', Tags.Items.STONES)
                .define('F', ModBlocks.FAST_FLOW_PAD.get())
                .unlockedBy("has_fast_flow_pad", has(ModBlocks.FAST_FLOW_PAD))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FASTEST_FLOW_PAD.get(), 8)
                .pattern("DUD")
                .pattern("UFU")
                .pattern("SSS")
                .define('U', ModBlocks.UMBRITE_BLOCK.get())
                .define('D', Items.DIAMOND_BLOCK)
                .define('S', Tags.Items.STONES)
                .define('F', ModBlocks.FASTER_FLOW_PAD.get())
                .unlockedBy("has_faster_flow_pad", has(ModBlocks.FASTER_FLOW_PAD))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.PAD_WRENCH.get())
                .pattern("  U")
                .pattern(" S ")
                .pattern("U  ")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('S', Items.STICK)
                .unlockedBy("has_umbrite_ingot", has(ModItems.UMBRITE_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SHARPNESS_MODULE.get())
                .pattern("UIU")
                .pattern("ISI")
                .pattern("UIU")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('S', Items.DIAMOND_SWORD)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_diamond_sword", has(Items.DIAMOND_SWORD))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FIRE_ASPECT_MODULE.get())
                .pattern("UBU")
                .pattern("BFB")
                .pattern("UBU")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('B', Items.BLAZE_ROD)
                .define('F', Items.FLINT_AND_STEEL)
                .unlockedBy("has_blaze_rod", has(Items.BLAZE_ROD))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SMITE_MODULE.get())
                .pattern("URU")
                .pattern("RBR")
                .pattern("URU")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('R', Items.ROTTEN_FLESH)
                .define('B', Items.BONE_BLOCK)
                .unlockedBy("has_rotten_flesh", has(Items.ROTTEN_FLESH))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOA_MODULE.get())
                .pattern("USU")
                .pattern("TET")
                .pattern("USU")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('S', Items.SPIDER_EYE)
                .define('E', Items.FERMENTED_SPIDER_EYE)
                .define('T', Items.STRING)
                .unlockedBy("has_spider_eye", has(Items.SPIDER_EYE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LOOTING_MODULE.get())
                .pattern("ULU")
                .pattern("LGL")
                .pattern("ULU")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('L', Items.LAPIS_BLOCK)
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ITEM_FILTER_MODULE.get())
                .pattern("PPP")
                .pattern("IHI")
                .pattern("UIU")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('I', Items.IRON_BARS)
                .define('P', Items.PAPER)
                .define('H', Items.HOPPER)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STACK_SIZE_MODULE.get())
                .pattern("UBU")
                .pattern("BCB")
                .pattern("UBU")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('B', ModBlocks.UMBRITE_BLOCK.get())
                .define('C', Items.ENDER_CHEST)
                .unlockedBy("has_ender_chest", has(Items.ENDER_CHEST))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PICKUP_ZONE_SIZE_MODULE.get())
                .pattern("UEU")
                .pattern("ERE")
                .pattern("UEU")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('E', Items.ENDER_EYE)
                .define('R', Items.OBSERVER)
                .unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.UMBRITE_SWORD.get())
                .pattern("U")
                .pattern("U")
                .pattern("S")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('S', Items.STICK)
                .unlockedBy("has_umbrite_ingot", has(ModItems.UMBRITE_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.UMBRITE_PICKAXE.get())
                .pattern("UUU")
                .pattern(" S ")
                .pattern(" S ")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('S', Items.STICK)
                .unlockedBy("has_umbrite_ingot", has(ModItems.UMBRITE_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.UMBRITE_AXE.get())
                .pattern("UU")
                .pattern("US")
                .pattern(" S")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('S', Items.STICK)
                .unlockedBy("has_umbrite_ingot", has(ModItems.UMBRITE_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.UMBRITE_SHOVEL.get())
                .pattern("U")
                .pattern("S")
                .pattern("S")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('S', Items.STICK)
                .unlockedBy("has_umbrite_ingot", has(ModItems.UMBRITE_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.UMBRITE_HOE.get())
                .pattern("UU")
                .pattern(" S")
                .pattern(" S")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('S', Items.STICK)
                .unlockedBy("has_umbrite_ingot", has(ModItems.UMBRITE_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.UMBRITE_HAMMER.get())
                .pattern("UBU")
                .pattern("UBU")
                .pattern(" S ")
                .define('U', ModItems.UMBRITE_INGOT.get())
                .define('B', ModBlocks.UMBRITE_BLOCK.get())
                .define('S', Items.STICK)
                .unlockedBy("has_umbrite_block", has(ModBlocks.UMBRITE_BLOCK))
                .save(recipeOutput);
    }

    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result,
                                      float experience, int cookingTime, String group) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, ingredients, category, result,
                experience, cookingTime, group, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result,
                                      float experience, int cookingTime, String group) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, ingredients, category, result,
                experience, cookingTime, group, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, String recipeName) {
        for(ItemLike itemlike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), category, result, experience, cookingTime, pCookingSerializer, factory).group(group).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, FlowTech.MODID + ":" + getItemName(result) + recipeName + "_" + getItemName(itemlike));
        }
    }
}
