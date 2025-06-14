package com.blocklogic.flowtech.screen.custom;

import com.blocklogic.flowtech.block.ModBlocks;
import com.blocklogic.flowtech.block.entity.FlowtechControllerBlockEntity;
import com.blocklogic.flowtech.item.ModItems;
import com.blocklogic.flowtech.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class FlowtechControllerMenu extends AbstractContainerMenu {
    public final FlowtechControllerBlockEntity blockEntity;
    private final Level level;

    public FlowtechControllerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public FlowtechControllerMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ModMenuTypes.CONTROLLER_MENU.get(), containerId);
        this.blockEntity = ((FlowtechControllerBlockEntity) blockEntity);
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new ModuleSlot(this.blockEntity.inventory, 0, 44, 18, ModItems.SHARPNESS_MODULE.get()));
        this.addSlot(new ModuleSlot(this.blockEntity.inventory, 1, 62, 18, ModItems.FIRE_ASPECT_MODULE.get()));
        this.addSlot(new ModuleSlot(this.blockEntity.inventory, 2, 80, 18, ModItems.SMITE_MODULE.get()));
        this.addSlot(new ModuleSlot(this.blockEntity.inventory, 3, 98, 18, ModItems.BOA_MODULE.get()));
        this.addSlot(new ModuleSlot(this.blockEntity.inventory, 4, 116, 18, ModItems.LOOTING_MODULE.get()));
    }

    private static class ModuleSlot extends SlotItemHandler {
        private final Item allowedModule;

        public ModuleSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Item allowedModule) {
            super(itemHandler, index, xPosition, yPosition);
            this.allowedModule = allowedModule;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() == allowedModule;
        }

        @Override
        public int getMaxStackSize() {
            return 10;
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return 10;
        }
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int MODULE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int MODULE_INVENTORY_SLOT_COUNT = 5;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (isValidModule(sourceStack)) {
                if (!moveItemStackTo(sourceStack, MODULE_INVENTORY_FIRST_SLOT_INDEX, MODULE_INVENTORY_FIRST_SLOT_INDEX
                        + MODULE_INVENTORY_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < MODULE_INVENTORY_FIRST_SLOT_INDEX + MODULE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    private boolean isValidModule(ItemStack stack) {
        return stack.getItem() == ModItems.SHARPNESS_MODULE.get() ||
                stack.getItem() == ModItems.FIRE_ASPECT_MODULE.get() ||
                stack.getItem() == ModItems.SMITE_MODULE.get() ||
                stack.getItem() == ModItems.BOA_MODULE.get() ||
                stack.getItem() == ModItems.LOOTING_MODULE.get();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.FLOWTECH_CONTROLLER.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 55 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 114));
        }
    }
}