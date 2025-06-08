package com.blocklogic.flowtech.block.entity;

import com.blocklogic.flowtech.FlowTech;
import com.blocklogic.flowtech.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, FlowTech.MODID);

    public static final Supplier<BlockEntityType<FlowtechControllerBlockEntity>> CONTROLLER_BE =
            BLOCK_ENTITIES.register("controller_be", () -> BlockEntityType.Builder.of(
                    FlowtechControllerBlockEntity::new, ModBlocks.FLOWTECH_CONTROLLER.get()).build(null));

    public static final Supplier<BlockEntityType<FlowtechCollectorBlockEntity>> COLLECTOR_BE =
            BLOCK_ENTITIES.register("collector_be", () -> BlockEntityType.Builder.of(
                    FlowtechCollectorBlockEntity::new, ModBlocks.FLOWTECH_COLLECTOR.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
