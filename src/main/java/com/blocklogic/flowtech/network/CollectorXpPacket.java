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

public record CollectorXpPacket(
        BlockPos pos,
        XpAction action,
        int amount
) implements CustomPacketPayload {

    public static final Type<CollectorXpPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("flowtech", "collector_xp"));

    public static final StreamCodec<FriendlyByteBuf, CollectorXpPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, CollectorXpPacket::pos,
            XpAction.STREAM_CODEC, CollectorXpPacket::action,
            ByteBufCodecs.INT, CollectorXpPacket::amount,
            CollectorXpPacket::new
    );

    @Override
    public Type<CollectorXpPacket> type() {
        return TYPE;
    }

    public static void handle(CollectorXpPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                if (player.level().getBlockEntity(packet.pos()) instanceof FlowtechCollectorBlockEntity collector) {
                    switch (packet.action()) {
                        case WITHDRAW -> {
                            int storedXp = collector.getStoredXP();
                            int withdrawAmount = Math.min(packet.amount(), storedXp);
                            if (withdrawAmount > 0) {
                                collector.setStoredXP(storedXp - withdrawAmount);
                                player.giveExperiencePoints(withdrawAmount);
                            }
                        }
                        case DEPOSIT -> {
                            int playerXp = player.totalExperience;
                            int depositAmount = Math.min(packet.amount(), playerXp);
                            if (depositAmount > 0) {
                                player.giveExperiencePoints(-depositAmount);
                                collector.setStoredXP(collector.getStoredXP() + depositAmount);
                            }
                        }
                    }
                }
            }
        });
    }

    public enum XpAction {
        WITHDRAW,
        DEPOSIT;

        public static final StreamCodec<FriendlyByteBuf, XpAction> STREAM_CODEC = StreamCodec.of(
                (buf, action) -> buf.writeEnum(action),
                buf -> buf.readEnum(XpAction.class)
        );
    }
}