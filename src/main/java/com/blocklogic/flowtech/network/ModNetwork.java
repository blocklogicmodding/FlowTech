package com.blocklogic.flowtech.network;

import com.blocklogic.flowtech.FlowTech;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetwork {

    public static void register(IEventBus eventBus) {
        eventBus.addListener(ModNetwork::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(FlowTech.MODID);

        registrar.playToServer(
                ModConfigPacket.TYPE,
                ModConfigPacket.STREAM_CODEC,
                ModConfigPacket::handle
        );

        registrar.playToServer(
                CollectorXpPacket.TYPE,
                CollectorXpPacket.STREAM_CODEC,
                CollectorXpPacket::handle
        );
    }
}