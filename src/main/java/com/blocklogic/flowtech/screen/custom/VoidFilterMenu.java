package com.blocklogic.flowtech.screen.custom;

import com.blocklogic.flowtech.component.ModDataComponents;
import com.blocklogic.flowtech.component.VoidFilterData;
import com.blocklogic.flowtech.item.custom.VoidFilterItem;
import com.blocklogic.flowtech.network.ModConfigPacket;
import com.blocklogic.flowtech.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;

public class VoidFilterMenu extends AbstractContainerMenu {
    private final ItemStack filterItem;
    private final ItemStackHandler filterSlots;

    private boolean ignoreNBT = true;
    private boolean ignoreDurability = true;

    public VoidFilterMenu(int containerId, Inventory playerInventory, ItemStack filterItem) {
        super(ModMenuTypes.VOID_FILTER_MENU.get(), containerId);
        this.filterItem = filterItem;

        this.filterSlots = new ItemStackHandler(45) {
            @Override
            protected void onContentsChanged(int slot) {
                saveFilterData();
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return super.extractItem(slot, amount, simulate);
            }
        };

        loadFilterData();

        addFilterSlots();
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    public VoidFilterMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.getMainHandItem());
    }

    private void addFilterSlots() {
        int slotIndex = 0;
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                int x = 8 + (col * 18);
                int y = 15 + (row * 18);
                this.addSlot(new FilterSlot(filterSlots, slotIndex, x, y));
                slotIndex++;
            }
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = 8 + (col * 18);
                int y = 121 + (row * 18);
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, x, y));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; col++) {
            int x = 8 + (col * 18);
            int y = 180;
            this.addSlot(new Slot(playerInventory, col, x, y));
        }
    }

    private void loadFilterData() {
        VoidFilterData data = filterItem.getOrDefault(ModDataComponents.VOID_FILTER_DATA.get(), VoidFilterData.DEFAULT);

        this.ignoreNBT = data.ignoreNBT();
        this.ignoreDurability = data.ignoreDurability();

        data.loadIntoHandler(filterSlots);
    }

    private void saveFilterData() {
        VoidFilterData newData = VoidFilterData.fromHandler(filterSlots, ignoreNBT, ignoreDurability);

        filterItem.set(ModDataComponents.VOID_FILTER_DATA.get(), newData);
    }

    public boolean isIgnoreNBT() {
        return ignoreNBT;
    }

    public void setIgnoreNBT(boolean ignoreNBT) {
        this.ignoreNBT = ignoreNBT;

        if (filterItem.getItem() instanceof VoidFilterItem) {
            PacketDistributor.sendToServer(new ModConfigPacket(
                    ModConfigPacket.ConfigTarget.VOID_FILTER_ITEM,
                    null,
                    ModConfigPacket.ConfigType.VOID_FILTER_IGNORE_NBT,
                    0,
                    ignoreNBT
            ));
        }

        saveFilterData();
    }

    public boolean isIgnoreDurability() {
        return ignoreDurability;
    }

    public void setIgnoreDurability(boolean ignoreDurability) {
        this.ignoreDurability = ignoreDurability;

        if (filterItem.getItem() instanceof VoidFilterItem) {
            PacketDistributor.sendToServer(new ModConfigPacket(
                    ModConfigPacket.ConfigTarget.VOID_FILTER_ITEM,
                    null,
                    ModConfigPacket.ConfigType.VOID_FILTER_IGNORE_DURABILITY,
                    0,
                    ignoreDurability
            ));
        }

        saveFilterData();
    }

    public ItemStackHandler getFilterSlots() {
        return filterSlots;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < 45) {
            FilterSlot filterSlot = (FilterSlot) slots.get(slotId);
            ItemStack carried = getCarried();

            if (!carried.isEmpty()) {
                filterSlot.setFilterItem(carried);
            } else {
                filterSlot.setFilterItem(ItemStack.EMPTY);
            }
            return;
        }

        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getInventory().contains(filterItem);
    }

    private static class FilterSlot extends SlotItemHandler {
        public FilterSlot(ItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return true;
        }

        @Override
        public ItemStack remove(int amount) {
            ItemStack result = getItem().copy();
            set(ItemStack.EMPTY);
            return result;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            set(ItemStack.EMPTY);
        }

        @Override
        public boolean allowModification(Player player) {
            return true;
        }

        public void setFilterItem(ItemStack stack) {
            if (stack.isEmpty()) {
                set(ItemStack.EMPTY);
            } else {
                set(stack.copyWithCount(1));
            }
        }
    }
}