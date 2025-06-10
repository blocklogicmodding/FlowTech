package com.blocklogic.flowtech.block.custom;

import com.blocklogic.flowtech.block.entity.FlowtechControllerBlockEntity;
import com.blocklogic.flowtech.component.ModDataComponents;
import com.blocklogic.flowtech.component.PadWrenchData;
import com.blocklogic.flowtech.item.custom.PadWrenchItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FlowtechControllerBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);
    public static final MapCodec<FlowtechControllerBlock> CODEC = simpleCodec(FlowtechControllerBlock::new);

    public FlowtechControllerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
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
        builder.add(FACING);
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
        return new FlowtechControllerBlockEntity(blockPos, blockState);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof FlowtechControllerBlockEntity flowtechControllerBlockEntity) {
                flowtechControllerBlockEntity.clearAllLinkedPads();

                clearControllerFromNearbyWrenches(level, pos);

                flowtechControllerBlockEntity.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private void clearControllerFromNearbyWrenches(Level level, BlockPos controllerPos) {
        if (level.isClientSide()) return;

        level.players().forEach(player -> {
            if (player.distanceToSqr(controllerPos.getX(), controllerPos.getY(), controllerPos.getZ()) <= 64 * 64) {
                clearControllerFromPlayerWrenches(player, controllerPos);
            }
        });
    }

    private void clearControllerFromPlayerWrenches(Player player, BlockPos controllerPos) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof PadWrenchItem) {
            clearControllerFromWrench(mainHand, controllerPos, player);
        }

        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof PadWrenchItem) {
            clearControllerFromWrench(offHand, controllerPos, player);
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof PadWrenchItem) {
                clearControllerFromWrench(stack, controllerPos, player);
            }
        }
    }

    private void clearControllerFromWrench(ItemStack wrenchStack, BlockPos controllerPos, Player player) {
        PadWrenchData data = wrenchStack.getOrDefault(
                ModDataComponents.PAD_WRENCH_DATA.get(),
                PadWrenchData.DEFAULT
        );

        if (data.selectedController() != null && data.selectedController().equals(controllerPos)) {
            PadWrenchData newData = data.withSelectedController(null);
            wrenchStack.set(ModDataComponents.PAD_WRENCH_DATA.get(), newData);

            Component message = Component.translatable("item.flowtech.pad_wrench.controller.cleared");
            player.displayClientMessage(message, true);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.getBlockEntity(pos) instanceof FlowtechControllerBlockEntity flowtechControllerBlockEntity) {
            if(!level.isClientSide()) {
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(flowtechControllerBlockEntity, Component.translatable("gui.flowtech.controller")), pos);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.SUCCESS;
    }
}