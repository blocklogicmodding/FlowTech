package com.blocklogic.flowtech.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CollectorSyncPacket(
        BlockPos pos,
        int storedXP,
        boolean xpCollectionEnabled,
        int downUpOffset,
        int northSouthOffset,
        int eastWestOffset,
        boolean topSideActive,
        boolean eastSideActive,
        boolean frontSideActive,
        boolean westSideActive,
        boolean bottomSideActive,
        boolean backSideActive
) implements CustomPacketPayload {

    public static final Type<CollectorSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("flowtech", "collector_sync"));

    public static final StreamCodec<FriendlyByteBuf, CollectorSyncPacket> STREAM_CODEC = StreamCodec.of(
            CollectorSyncPacket::encode,
            CollectorSyncPacket::decode
    );

    private static void encode(FriendlyByteBuf buf, CollectorSyncPacket packet) {
        BlockPos.STREAM_CODEC.encode(buf, packet.pos());
        buf.writeInt(packet.storedXP());
        buf.writeBoolean(packet.xpCollectionEnabled());
        buf.writeInt(packet.downUpOffset());
        buf.writeInt(packet.northSouthOffset());
        buf.writeInt(packet.eastWestOffset());
        buf.writeBoolean(packet.topSideActive());
        buf.writeBoolean(packet.eastSideActive());
        buf.writeBoolean(packet.frontSideActive());
        buf.writeBoolean(packet.westSideActive());
        buf.writeBoolean(packet.bottomSideActive());
        buf.writeBoolean(packet.backSideActive());
    }

    private static CollectorSyncPacket decode(FriendlyByteBuf buf) {
        return new CollectorSyncPacket(
                BlockPos.STREAM_CODEC.decode(buf),
                buf.readInt(),
                buf.readBoolean(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    @Override
    public Type<CollectorSyncPacket> type() {
        return TYPE;
    }

    public static void handle(CollectorSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            // Handle client-side sync
            // This would update the GUI state on the client
            // Implementation depends on how you want to store client-side state
        });
    }
}