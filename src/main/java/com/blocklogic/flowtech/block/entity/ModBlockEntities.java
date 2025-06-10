package com.blocklogic.flowtech.block.entity;

import com.blocklogic.flowtech.FlowTech;
import com.blocklogic.flowtech.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
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

    public static final Supplier<BlockEntityType<AttackPadBlockEntity>> ATTACK_PAD_BE =
            BLOCK_ENTITIES.register("attack_pad_be", () -> BlockEntityType.Builder.of(
                    AttackPadBlockEntity::new, ModBlocks.ATTACK_PAD.get()).build(null));

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                COLLECTOR_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof FlowtechCollectorBlockEntity collector) {
                        return collector.getItemHandler(direction);
                    }
                    return null;
                }
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CONTROLLER_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof FlowtechControllerBlockEntity controller) {
                        return controller.inventory;
                    }
                    return null;
                }
        );
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
        eventBus.addListener(ModBlockEntities::registerCapabilities);
    }
}