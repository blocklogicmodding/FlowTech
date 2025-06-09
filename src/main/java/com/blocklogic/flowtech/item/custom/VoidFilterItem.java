package com.blocklogic.flowtech.item.custom;

import com.blocklogic.flowtech.screen.custom.VoidFilterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VoidFilterItem extends Item {

    public VoidFilterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, playerEntity) -> new VoidFilterMenu(containerId, playerInventory, stack),
                    Component.translatable("gui.flowtech.void_filter")
            ));

            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.success(stack);
    }
}