package com.blocklogic.flowtech.block.entity;

import com.blocklogic.flowtech.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FlowPadBlockEntity extends BlockEntity {

    private static final int MOVEMENT_INTERVAL = 2;
    private int tickCounter = 0;

    private static final double FAST_SPEED = 0.1;
    private static final double FASTER_SPEED = 0.2;
    private static final double FASTEST_SPEED = 0.5;

    public FlowPadBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FLOW_PAD_BE.get(), pos, blockState);
    }

    public static <T extends BlockEntity> BlockEntityTicker<T> createTicker() {
        return (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof FlowPadBlockEntity flowPad) {
                flowPad.tick();
            }
        };
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;

        tickCounter++;
        if (tickCounter >= MOVEMENT_INTERVAL) {
            tickCounter = 0;
            applyMovement();
        }
    }

    private void applyMovement() {
        if (level == null) return;

        AABB movementArea = new AABB(
                worldPosition.getX() - 0.1,
                worldPosition.getY() + 0.0625,
                worldPosition.getZ() - 0.1,
                worldPosition.getX() + 1.1,
                worldPosition.getY() + 1.5,
                worldPosition.getZ() + 1.1
        );

        List<Entity> entities = level.getEntitiesOfClass(Entity.class, movementArea, this::shouldMoveEntity);
        if (entities.isEmpty()) return;

        Direction facing = getFacing();
        double speed = getSpeedMultiplier();
        Vec3 movement = getMovementVector(facing, speed);

        for (Entity entity : entities) {
            applyMovementToEntity(entity, movement);
        }
    }

    private boolean shouldMoveEntity(Entity entity) {
        if (entity instanceof Player player && player.isShiftKeyDown()) {
            return false;
        }

        if (entity instanceof LivingEntity || entity instanceof ItemEntity) {
            double entityBottom = entity.getBoundingBox().minY;
            double padTop = worldPosition.getY() + 0.0625;

            return Math.abs(entityBottom - padTop) < 0.1;
        }
        return false;
    }

    private Direction getFacing() {
        BlockState state = getBlockState();
        if (state.hasProperty(HorizontalDirectionalBlock.FACING)) {
            return state.getValue(HorizontalDirectionalBlock.FACING);
        }
        return Direction.NORTH;
    }

    private double getSpeedMultiplier() {
        Block block = getBlockState().getBlock();

        if (block == ModBlocks.FAST_FLOW_PAD.get()) {
            return FAST_SPEED;
        } else if (block == ModBlocks.FASTER_FLOW_PAD.get()) {
            return FASTER_SPEED;
        } else if (block == ModBlocks.FASTEST_FLOW_PAD.get()) {
            return FASTEST_SPEED;
        }

        return FAST_SPEED;
    }

    private Vec3 getMovementVector(Direction facing, double speed) {
        return switch (facing) {
            case NORTH -> new Vec3(0, 0, speed);
            case SOUTH -> new Vec3(0, 0, -speed);
            case EAST -> new Vec3(-speed, 0, 0);
            case WEST -> new Vec3(speed, 0, 0);
            default -> Vec3.ZERO;
        };
    }

    private void applyMovementToEntity(Entity entity, Vec3 movement) {
        Vec3 currentVelocity = entity.getDeltaMovement();
        Vec3 newVelocity = currentVelocity.add(movement);

        double maxVelocity = 2.0;
        newVelocity = new Vec3(
                Math.max(-maxVelocity, Math.min(maxVelocity, newVelocity.x)),
                newVelocity.y,
                Math.max(-maxVelocity, Math.min(maxVelocity, newVelocity.z))
        );

        entity.setDeltaMovement(newVelocity);

        entity.hurtMarked = true;
    }
}