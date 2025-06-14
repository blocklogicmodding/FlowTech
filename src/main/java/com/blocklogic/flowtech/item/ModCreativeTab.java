package com.blocklogic.flowtech.item;

import com.blocklogic.flowtech.FlowTech;
import com.blocklogic.flowtech.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FlowTech.MODID);

    public static final Supplier<CreativeModeTab> FLOWTECH = CREATIVE_MODE_TAB.register("flowtech",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.FAST_FLOW_PAD.get()))
                    .title(Component.translatable("creativetab.flowtech.flowtech"))
                    .displayItems((ItemDisplayParameters, output) -> {
                        output.accept(ModBlocks.FAST_FLOW_PAD);
                        output.accept(ModBlocks.FASTER_FLOW_PAD);
                        output.accept(ModBlocks.FASTEST_FLOW_PAD);
                        output.accept(ModBlocks.ATTACK_PAD);
                        output.accept(ModBlocks.FLOWTECH_COLLECTOR);
                        output.accept(ModBlocks.FLOWTECH_CONTROLLER);
                        output.accept(ModBlocks.UMBRITE_STONE_ORE_BLOCK);
                        output.accept(ModBlocks.UMBRITE_DEEPSLATE_ORE_BLOCK);
                        output.accept(ModBlocks.UMBRITE_NETHER_ORE_BLOCK);
                        output.accept(ModBlocks.UMBRITE_END_ORE_BLOCK);
                        output.accept(ModBlocks.UMBRITE_BLOCK);
                        output.accept(ModBlocks.RAW_UMBRITE_BLOCK);

                        output.accept(ModItems.BOA_MODULE);
                        output.accept(ModItems.FIRE_ASPECT_MODULE);
                        output.accept(ModItems.SHARPNESS_MODULE);
                        output.accept(ModItems.SMITE_MODULE);
                        output.accept(ModItems.LOOTING_MODULE);
                        output.accept(ModItems.COLLECTION_RADIUS_INCREASE_MODULE);
                        output.accept(ModItems.VOID_FILTER_MODULE);
                        output.accept(ModItems.PAD_WRENCH);
                        output.accept(ModItems.RAW_UMBRITE);
                        output.accept(ModItems.UMBRITE_INGOT);
                        output.accept(ModItems.UMBRITE_NUGGET);
                    }).build());

    public static void register (IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
