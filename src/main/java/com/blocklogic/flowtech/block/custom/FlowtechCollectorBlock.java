package com.blocklogic.flowtech.block.custom;

import com.blocklogic.flowtech.block.entity.FlowtechCollectorBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.storage.loot.LootParams;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class FlowtechCollectorBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty XP_COLLECTION = BooleanProperty.create("xp_collection");
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);
    public static final MapCodec<FlowtechCollectorBlock> CODEC = simpleCodec(FlowtechCollectorBlock::new);

    public FlowtechCollectorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(XP_COLLECTION, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, XP_COLLECTION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FlowtechCollectorBlockEntity(blockPos, blockState);
    }

    // Override getDrops to return empty list since we handle drops manually
    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return Collections.emptyList();
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof FlowtechCollectorBlockEntity collector) {
                // Drop the output inventory contents
                collector.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);

        if (level.getBlockEntity(pos) instanceof FlowtechCollectorBlockEntity collector) {
            saveCollectorDataToItem(stack, collector, level);
        }

        return stack;
    }

    // THIS IS THE KEY METHOD - Gets called when block is broken by player
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof FlowtechCollectorBlockEntity collector) {
            // Create the item stack
            ItemStack stack = new ItemStack(this);

            // Save data to the item
            saveCollectorDataToItem(stack, collector, level);

            // Drop the item with saved data
            popResource(level, pos, stack);

            // DON'T clear contents here - let onRemove handle the dropping
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof FlowtechCollectorBlockEntity collector) {
            if (!level.isClientSide()) {
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(collector, Component.translatable("gui.flowtech.collector")), pos);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    // Update block state when XP collection changes
    public static void updateXpCollectionState(Level level, BlockPos pos, boolean xpCollection) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof FlowtechCollectorBlock) {
            level.setBlock(pos, state.setValue(XP_COLLECTION, xpCollection), 3);
        }
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (!level.isClientSide()) {
            return FlowtechCollectorBlockEntity.createTicker();
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.getBlockEntity(pos) instanceof FlowtechCollectorBlockEntity collector) {
            collector.loadFromBlockItem(stack);
        }
    }

    private void saveCollectorDataToItem(ItemStack stack, FlowtechCollectorBlockEntity collector, LevelReader level) {
        if (level instanceof ServerLevel serverLevel) {
            HolderLookup.Provider registries = serverLevel.registryAccess();
            CompoundTag tag = new CompoundTag();

            // Save module slots
            tag.put("moduleSlots", collector.moduleSlots.serializeNBT(registries));

            // Save persistent data
            tag.putInt("storedXP", collector.getStoredXP());
            tag.putBoolean("xpCollectionEnabled", collector.isXpCollectionEnabled());
            tag.putInt("downUpOffset", collector.getDownUpOffset());
            tag.putInt("northSouthOffset", collector.getNorthSouthOffset());
            tag.putInt("eastWestOffset", collector.getEastWestOffset());

            // Save side configuration
            tag.putBoolean("topSideActive", collector.isTopSideActive());
            tag.putBoolean("eastSideActive", collector.isEastSideActive());
            tag.putBoolean("frontSideActive", collector.isFrontSideActive());
            tag.putBoolean("westSideActive", collector.isWestSideActive());
            tag.putBoolean("bottomSideActive", collector.isBottomSideActive());
            tag.putBoolean("backSideActive", collector.isBackSideActive());

            // Save the data to the item
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }
}