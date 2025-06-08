package com.blocklogic.flowtech.network;

import com.blocklogic.flowtech.block.entity.FlowtechCollectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CollectorConfigPacket(
        BlockPos pos,
        ConfigType configType,
        int value,
        boolean boolValue
) implements CustomPacketPayload {

    public static final Type<CollectorConfigPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("flowtech", "collector_config"));

    public static final StreamCodec<FriendlyByteBuf, CollectorConfigPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, CollectorConfigPacket::pos,
            ConfigType.STREAM_CODEC, CollectorConfigPacket::configType,
            ByteBufCodecs.INT, CollectorConfigPacket::value,
            ByteBufCodecs.BOOL, CollectorConfigPacket::boolValue,
            CollectorConfigPacket::new
    );

    @Override
    public Type<CollectorConfigPacket> type() {
        return TYPE;
    }

    public static void handle(CollectorConfigPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                if (player.level().getBlockEntity(packet.pos()) instanceof FlowtechCollectorBlockEntity collector) {
                    switch (packet.configType()) {
                        case XP_COLLECTION_TOGGLE -> collector.setXpCollectionEnabled(packet.boolValue());
                        case DOWN_UP_OFFSET -> collector.setDownUpOffset(packet.value());
                        case NORTH_SOUTH_OFFSET -> collector.setNorthSouthOffset(packet.value());
                        case EAST_WEST_OFFSET -> collector.setEastWestOffset(packet.value());
                        case TOP_SIDE -> collector.setTopSideActive(packet.boolValue());
                        case EAST_SIDE -> collector.setEastSideActive(packet.boolValue());
                        case FRONT_SIDE -> collector.setFrontSideActive(packet.boolValue());
                        case WEST_SIDE -> collector.setWestSideActive(packet.boolValue());
                        case BOTTOM_SIDE -> collector.setBottomSideActive(packet.boolValue());
                        case BACK_SIDE -> collector.setBackSideActive(packet.boolValue());
                    }
                }
            }
        });
    }

    public enum ConfigType {
        XP_COLLECTION_TOGGLE,
        DOWN_UP_OFFSET,
        NORTH_SOUTH_OFFSET,
        EAST_WEST_OFFSET,
        TOP_SIDE,
        EAST_SIDE,
        FRONT_SIDE,
        WEST_SIDE,
        BOTTOM_SIDE,
        BACK_SIDE;

        public static final StreamCodec<FriendlyByteBuf, ConfigType> STREAM_CODEC = StreamCodec.of(
                (buf, type) -> buf.writeEnum(type),
                buf -> buf.readEnum(ConfigType.class)
        );
    }
}