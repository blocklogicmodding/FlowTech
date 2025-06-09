package com.blocklogic.flowtech.network;

import com.blocklogic.flowtech.block.entity.FlowtechCollectorBlockEntity;
import com.blocklogic.flowtech.component.ModDataComponents;
import com.blocklogic.flowtech.component.VoidFilterData;
import com.blocklogic.flowtech.item.custom.VoidFilterItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ModConfigPacket(
        ConfigTarget target,
        BlockPos pos,
        ConfigType configType,
        int intValue,
        boolean boolValue
) implements CustomPacketPayload {

    public static final Type<ModConfigPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("flowtech", "mod_config"));

    public static final StreamCodec<FriendlyByteBuf, ModConfigPacket> STREAM_CODEC = StreamCodec.composite(
            ConfigTarget.STREAM_CODEC, ModConfigPacket::target,
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs::optional), packet -> packet.pos() == null ? java.util.Optional.empty() : java.util.Optional.of(packet.pos()),
            ConfigType.STREAM_CODEC, ModConfigPacket::configType,
            ByteBufCodecs.INT, ModConfigPacket::intValue,
            ByteBufCodecs.BOOL, ModConfigPacket::boolValue,
            (target, posOpt, configType, intValue, boolValue) -> new ModConfigPacket(target, posOpt.orElse(null), configType, intValue, boolValue)
    );

    @Override
    public Type<ModConfigPacket> type() {
        return TYPE;
    }

    public static void handle(ModConfigPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                switch (packet.target()) {
                    case COLLECTOR_BLOCK -> handleCollectorConfig(packet, player);
                    case VOID_FILTER_ITEM -> handleVoidFilterConfig(packet, player);
                }
            }
        });
    }

    private static void handleCollectorConfig(ModConfigPacket packet, ServerPlayer player) {
        if (packet.pos() == null) return;

        ServerLevel level = player.serverLevel();

        if (player.distanceToSqr(packet.pos().getX() + 0.5, packet.pos().getY() + 0.5, packet.pos().getZ() + 0.5) > 64) {
            return;
        }

        if (level.getBlockEntity(packet.pos()) instanceof FlowtechCollectorBlockEntity collector) {
            switch (packet.configType()) {
                case COLLECTOR_XP_COLLECTION_TOGGLE -> {
                    collector.setXpCollectionEnabled(packet.boolValue());
                    level.sendBlockUpdated(packet.pos(), level.getBlockState(packet.pos()), level.getBlockState(packet.pos()), 3);
                }
                case COLLECTOR_DOWN_UP_OFFSET -> {
                    collector.setDownUpOffset(packet.intValue());
                    level.sendBlockUpdated(packet.pos(), level.getBlockState(packet.pos()), level.getBlockState(packet.pos()), 3);
                }
                case COLLECTOR_NORTH_SOUTH_OFFSET -> {
                    collector.setNorthSouthOffset(packet.intValue());
                    level.sendBlockUpdated(packet.pos(), level.getBlockState(packet.pos()), level.getBlockState(packet.pos()), 3);
                }
                case COLLECTOR_EAST_WEST_OFFSET -> {
                    collector.setEastWestOffset(packet.intValue());
                    level.sendBlockUpdated(packet.pos(), level.getBlockState(packet.pos()), level.getBlockState(packet.pos()), 3);
                }
                case COLLECTOR_TOP_SIDE -> {
                    collector.setTopSideActive(packet.boolValue());
                    level.sendBlockUpdated(packet.pos(), level.getBlockState(packet.pos()), level.getBlockState(packet.pos()), 3);
                }
                case COLLECTOR_EAST_SIDE -> {
                    collector.setEastSideActive(packet.boolValue());
                    level.sendBlockUpdated(packet.pos(), level.getBlockState(packet.pos()), level.getBlockState(packet.pos()), 3);
                }
                case COLLECTOR_FRONT_SIDE -> {
                    collector.setFrontSideActive(packet.boolValue());
                    level.sendBlockUpdated(packet.pos(), level.getBlockState(packet.pos()), level.getBlockState(packet.pos()), 3);
                }
                case COLLECTOR_WEST_SIDE -> {
                    collector.setWestSideActive(packet.boolValue());
                    level.sendBlockUpdated(packet.pos(), level.getBlockState(packet.pos()), level.getBlockState(packet.pos()), 3);
                }
                case COLLECTOR_BOTTOM_SIDE -> {
                    collector.setBottomSideActive(packet.boolValue());
                    level.sendBlockUpdated(packet.pos(), level.getBlockState(packet.pos()), level.getBlockState(packet.pos()), 3);
                }
                case COLLECTOR_BACK_SIDE -> {
                    collector.setBackSideActive(packet.boolValue());
                    level.sendBlockUpdated(packet.pos(), level.getBlockState(packet.pos()), level.getBlockState(packet.pos()), 3);
                }
            }
        }
    }

    private static void handleVoidFilterConfig(ModConfigPacket packet, ServerPlayer player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        ItemStack filterItem = null;
        if (mainHand.getItem() instanceof VoidFilterItem) {
            filterItem = mainHand;
        } else if (offHand.getItem() instanceof VoidFilterItem) {
            filterItem = offHand;
        }

        if (filterItem == null) return;

        VoidFilterData currentData = filterItem.getOrDefault(ModDataComponents.VOID_FILTER_DATA.get(), VoidFilterData.DEFAULT);

        VoidFilterData newData = switch (packet.configType()) {
            case VOID_FILTER_IGNORE_NBT -> currentData.withIgnoreNBT(packet.boolValue());
            case VOID_FILTER_IGNORE_DURABILITY -> currentData.withIgnoreDurability(packet.boolValue());
            default -> currentData;
        };

        filterItem.set(ModDataComponents.VOID_FILTER_DATA.get(), newData);
    }

    public enum ConfigTarget {
        COLLECTOR_BLOCK,
        VOID_FILTER_ITEM;

        public static final StreamCodec<FriendlyByteBuf, ConfigTarget> STREAM_CODEC = StreamCodec.of(
                (buf, target) -> buf.writeEnum(target),
                buf -> buf.readEnum(ConfigTarget.class)
        );
    }

    public enum ConfigType {
        COLLECTOR_XP_COLLECTION_TOGGLE,
        COLLECTOR_DOWN_UP_OFFSET,
        COLLECTOR_NORTH_SOUTH_OFFSET,
        COLLECTOR_EAST_WEST_OFFSET,
        COLLECTOR_TOP_SIDE,
        COLLECTOR_EAST_SIDE,
        COLLECTOR_FRONT_SIDE,
        COLLECTOR_WEST_SIDE,
        COLLECTOR_BOTTOM_SIDE,
        COLLECTOR_BACK_SIDE,

        VOID_FILTER_IGNORE_NBT,
        VOID_FILTER_IGNORE_DURABILITY;

        public static final StreamCodec<FriendlyByteBuf, ConfigType> STREAM_CODEC = StreamCodec.of(
                (buf, type) -> buf.writeEnum(type),
                buf -> buf.readEnum(ConfigType.class)
        );
    }
}