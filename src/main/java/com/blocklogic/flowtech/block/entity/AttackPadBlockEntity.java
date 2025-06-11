package com.blocklogic.flowtech.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class AttackPadBlockEntity extends BlockEntity {

    @Nullable
    private BlockPos controllerPos;
    @Nullable
    private UUID placer;

    private static final float BASE_DAMAGE = 3.0f;
    private static final int DAMAGE_INTERVAL = 5;
    private int tickCounter = 0;

    public AttackPadBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ATTACK_PAD_BE.get(), pos, blockState);
    }

    public static <T extends BlockEntity> BlockEntityTicker<T> createTicker() {
        return (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof AttackPadBlockEntity attackPad) {
                attackPad.tick();
            }
        };
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;

        tickCounter++;
        if (tickCounter >= DAMAGE_INTERVAL) {
            tickCounter = 0;

            if (isPowered()) {
                dealDamage();
            }
        }
    }

    private boolean isPowered() {
        if (level == null) return false;
        return level.hasNeighborSignal(worldPosition);
    }

    private void dealDamage() {
        if (level == null) return;

        AABB damageArea = new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                worldPosition.getX() + 1D, worldPosition.getY() + 1D, worldPosition.getZ() + 1D)
                .inflate(0.0625D, 0.0625D, 0.0625D);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, damageArea);
        if (entities.isEmpty()) return;

        FlowtechControllerBlockEntity controller = getLinkedController();
        float damage = calculateDamage(controller);

        for (LivingEntity entity : entities) {
            if (entity instanceof Player) continue;

            DamageSource damageSource = level.damageSources().generic();
            entity.hurt(damageSource, damage);
        }
    }

    private float calculateDamage(FlowtechControllerBlockEntity controller) {
        if (controller == null) return BASE_DAMAGE;

        float damage = BASE_DAMAGE;

        // Add sharpness bonus damage
        int sharpness = controller.getModuleCount(0);
        if (sharpness > 0) {
            damage += sharpness * 1.25f; // Each sharpness level adds 1.25 damage
        }

        // Add smite bonus damage (could be conditional based on entity type)
        int smite = controller.getModuleCount(2);
        if (smite > 0) {
            damage += smite * 2.5f; // Each smite level adds 2.5 damage to undead
        }

        // Add bane of arthropods bonus damage (could be conditional based on entity type)
        int baneOfArthropods = controller.getModuleCount(3);
        if (baneOfArthropods > 0) {
            damage += baneOfArthropods * 2.5f; // Each bane level adds 2.5 damage to arthropods
        }

        return damage;
    }

    @Nullable
    private FlowtechControllerBlockEntity getLinkedController() {
        if (controllerPos == null || level == null) return null;

        if (level.getBlockEntity(controllerPos) instanceof FlowtechControllerBlockEntity controller) {
            return controller;
        }
        return null;
    }

    @Nullable
    public BlockPos getControllerPos() {
        return controllerPos;
    }

    public void setControllerPos(@Nullable BlockPos controllerPos) {
        this.controllerPos = controllerPos;
        setChanged();

        if (level != null && !level.isClientSide()) {
            com.blocklogic.flowtech.block.custom.AttackPadBlock.updateLinkedState(level, getBlockPos(), controllerPos != null);
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public void clearControllerPos() {
        setControllerPos(null);
    }

    public boolean isLinked() {
        return controllerPos != null;
    }

    public boolean isLinkedTo(BlockPos pos) {
        return controllerPos != null && controllerPos.equals(pos);
    }

    public void setPlacer(@Nullable Player player) {
        this.placer = player != null ? player.getUUID() : null;
        setChanged();
    }

    @Nullable
    public UUID getPlacer() {
        return placer;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        if (controllerPos != null) {
            tag.putLong("controllerPos", controllerPos.asLong());
        }

        if (placer != null) {
            tag.putUUID("placer", placer);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("controllerPos")) {
            controllerPos = BlockPos.of(tag.getLong("controllerPos"));
        } else {
            controllerPos = null;
        }

        if (tag.hasUUID("placer")) {
            placer = tag.getUUID("placer");
        } else {
            placer = null;
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);

        if (controllerPos != null) {
            tag.putLong("controllerPos", controllerPos.asLong());
        }

        if (placer != null) {
            tag.putUUID("placer", placer);
        }

        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}