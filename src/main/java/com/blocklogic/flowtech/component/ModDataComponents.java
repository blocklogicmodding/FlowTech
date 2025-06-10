package com.blocklogic.flowtech.component;

import com.blocklogic.flowtech.FlowTech;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, FlowTech.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<VoidFilterData>> VOID_FILTER_DATA =
            DATA_COMPONENT_TYPES.register("void_filter_data", () -> DataComponentType.<VoidFilterData>builder()
                    .persistent(VoidFilterData.CODEC)
                    .networkSynchronized(ByteBufCodecs.fromCodec(VoidFilterData.CODEC))
                    .cacheEncoding()
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PadWrenchData>> PAD_WRENCH_DATA =
            DATA_COMPONENT_TYPES.register("pad_wrench_data", () -> DataComponentType.<PadWrenchData>builder()
                    .persistent(PadWrenchData.CODEC)
                    .networkSynchronized(ByteBufCodecs.fromCodec(PadWrenchData.CODEC))
                    .cacheEncoding()
                    .build());

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}