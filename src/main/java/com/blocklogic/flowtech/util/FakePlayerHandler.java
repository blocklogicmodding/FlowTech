package com.blocklogic.flowtech.util;

import com.blocklogic.flowtech.FlowTech;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class FakePlayerHandler {

    private static final GameProfile DEFAULT_PROFILE = new GameProfile(
            UUID.fromString("12345678-1234-1234-1234-123456789012"),
            "[FlowTech]"
    );

    private static FakePlayer getDefault(ServerLevel level) {
        return FakePlayerFactory.get(level, DEFAULT_PROFILE);
    }

    private static FakePlayer get(ServerLevel level, @Nullable UUID placer) {
        FakePlayer fakePlayer;
        if (placer == null) {
            fakePlayer = getDefault(level);
        } else {
            fakePlayer = FakePlayerFactory.get(level, new GameProfile(placer,
                    Component.translatable("fakeplayer.flowtech.attack_pad").getString()));
        }

        fakePlayer.getPersistentData().putBoolean(FlowTech.MODID, true);
        return fakePlayer;
    }

    public static WeakReference<FakePlayer> get(WeakReference<FakePlayer> previous, ServerLevel level,
                                                @Nullable UUID placer, BlockPos pos) {
        FakePlayer fakePlayer = previous.get();
        if (fakePlayer == null) {
            fakePlayer = get(level, placer);
            fakePlayer.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            return new WeakReference<>(fakePlayer);
        } else {
            fakePlayer.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            return previous;
        }
    }

    public static boolean isFlowTechFakePlayer(FakePlayer fakePlayer) {
        return fakePlayer.getPersistentData().contains(FlowTech.MODID);
    }
}