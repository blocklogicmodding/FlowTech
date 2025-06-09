package com.blocklogic.flowtech.network;

import com.blocklogic.flowtech.block.entity.FlowtechCollectorBlockEntity;
import com.blocklogic.flowtech.screen.custom.FlowtechCollectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CollectorInventorySyncPacket(
        BlockPos pos,
        CompoundTag inventoryData,
        int slotCapacity
) implements CustomPacketPayload {

    public static final Type<CollectorInventorySyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("flowtech", "collector_inventory_sync"));

    public static final StreamCodec<FriendlyByteBuf, CollectorInventorySyncPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, CollectorInventorySyncPacket::pos,
            ByteBufCodecs.COMPOUND_TAG, CollectorInventorySyncPacket::inventoryData,
            ByteBufCodecs.INT, CollectorInventorySyncPacket::slotCapacity,
            CollectorInventorySyncPacket::new
    );

    @Override
    public Type<CollectorInventorySyncPacket> type() {
        return TYPE;
    }

    public static void handle(CollectorInventorySyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getBlockEntity(packet.pos()) instanceof FlowtechCollectorBlockEntity collector) {
                try {
                    // Update client-side inventory
                    collector.outputInventory.deserializeNBT(context.player().level().registryAccess(), packet.inventoryData());

                    // Update any open menu
                    if (context.player().containerMenu instanceof FlowtechCollectorMenu menu &&
                            menu.blockEntity == collector) {
                        menu.broadcastChanges();
                    }
                } catch (Exception e) {
                    FlowtechCollectorBlockEntity.getLogger().error("Failed to sync collector inventory", e);
                }
            }
        });
    }
}