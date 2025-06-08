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

        // Register collector config packet (client to server)
        registrar.playToServer(
                CollectorConfigPacket.TYPE,
                CollectorConfigPacket.STREAM_CODEC,
                CollectorConfigPacket::handle
        );

        // Register collector XP packet (client to server)
        registrar.playToServer(
                CollectorXpPacket.TYPE,
                CollectorXpPacket.STREAM_CODEC,
                CollectorXpPacket::handle
        );

        // Register collector sync packet (server to client)
        registrar.playToClient(
                CollectorSyncPacket.TYPE,
                CollectorSyncPacket.STREAM_CODEC,
                CollectorSyncPacket::handle
        );
    }
}