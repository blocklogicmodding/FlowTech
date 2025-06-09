package com.blocklogic.flowtech.screen.custom;

import com.blocklogic.flowtech.block.ModBlocks;
import com.blocklogic.flowtech.block.entity.FlowtechCollectorBlockEntity;
import com.blocklogic.flowtech.item.ModItems;
import com.blocklogic.flowtech.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class FlowtechCollectorMenu extends AbstractContainerMenu {
    public final FlowtechCollectorBlockEntity blockEntity;
    private final Level level;

    public FlowtechCollectorMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public FlowtechCollectorMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ModMenuTypes.COLLECTOR_MENU.get(), containerId);
        this.blockEntity = ((FlowtechCollectorBlockEntity) blockEntity);
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new ModuleSlot(this.blockEntity.moduleSlots, 0, 152, 15, ModItems.PICKUP_ZONE_SIZE_MODULE.get()));
        this.addSlot(new ModuleSlot(this.blockEntity.moduleSlots, 1, 152, 33, ModItems.STACK_SIZE_MODULE.get()));
        this.addSlot(new ModuleSlot(this.blockEntity.moduleSlots, 2, 152, 51, ModItems.VOID_FILTER_MODULE.get()));
        this.addSlot(new ModuleSlot(this.blockEntity.moduleSlots, 3, 152, 69, ModItems.VOID_FILTER_MODULE.get()));
        this.addSlot(new ModuleSlot(this.blockEntity.moduleSlots, 4, 152, 87, ModItems.VOID_FILTER_MODULE.get()));

        int slotIndex = 0;
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 7; col++) {
                int x = 8 + (col * 18);
                int y = 15 + (row * 18);
                this.addSlot(new OutputSlot(this.blockEntity.outputInventory, slotIndex, x, y));
                slotIndex++;
            }
        }
    }

    private static class ModuleSlot extends SlotItemHandler {
        private final net.minecraft.world.item.Item allowedModule;

        public ModuleSlot(net.neoforged.neoforge.items.IItemHandler itemHandler, int index, int xPosition, int yPosition, net.minecraft.world.item.Item allowedModule) {
            super(itemHandler, index, xPosition, yPosition);
            this.allowedModule = allowedModule;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() == allowedModule;
        }
    }

    private static class OutputSlot extends SlotItemHandler {
        public OutputSlot(net.neoforged.neoforge.items.IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int MODULE_SLOTS_FIRST_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int MODULE_SLOTS_COUNT = 5;
    private static final int OUTPUT_SLOTS_FIRST_INDEX = MODULE_SLOTS_FIRST_INDEX + MODULE_SLOTS_COUNT;
    private static final int OUTPUT_SLOTS_COUNT = 35;

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot sourceSlot = slots.get(slotIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (slotIndex < VANILLA_SLOT_COUNT) {
            if (isValidModule(sourceStack)) {
                if (!moveItemStackTo(sourceStack, MODULE_SLOTS_FIRST_INDEX, MODULE_SLOTS_FIRST_INDEX + MODULE_SLOTS_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        }
        else if (slotIndex < MODULE_SLOTS_FIRST_INDEX + MODULE_SLOTS_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        }
        else if (slotIndex < OUTPUT_SLOTS_FIRST_INDEX + OUTPUT_SLOTS_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        }
        else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    private boolean isValidModule(ItemStack stack) {
        return stack.getItem() == ModItems.PICKUP_ZONE_SIZE_MODULE.get() ||
                stack.getItem() == ModItems.STACK_SIZE_MODULE.get() ||
                stack.getItem() == ModItems.VOID_FILTER_MODULE.get();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.FLOWTECH_COLLECTOR.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 37 + l * 18, 151 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 37 + i * 18, 210));
        }
    }
}