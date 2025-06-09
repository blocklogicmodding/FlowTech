package com.blocklogic.flowtech.block.entity;

import com.blocklogic.flowtech.block.custom.FlowtechCollectorBlock;
import com.blocklogic.flowtech.component.ModDataComponents;
import com.blocklogic.flowtech.component.VoidFilterData;
import com.blocklogic.flowtech.item.custom.VoidFilterItem;
import com.blocklogic.flowtech.screen.custom.FlowtechCollectorMenu;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import org.slf4j.Logger;

import java.util.List;

import javax.annotation.Nullable;

public class FlowtechCollectorBlockEntity extends BlockEntity implements MenuProvider {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static Logger getLogger() { return LOGGER; }

    private AABB cachedCollectionArea;
    private boolean collectionAreaDirty = true;
    private int cachedPickupRange = -1;
    private boolean pickupRangeDirty = true;

    private void invalidateCollectionAreaCache() {
        collectionAreaDirty = true;
    }

    private void invalidatePickupRangeCache() {
        pickupRangeDirty = true;
        invalidateCollectionAreaCache();
    }

    private int tickCounter = 0;
    private static final int COLLECTION_INTERVAL = 5;

    public final ItemStackHandler moduleSlots = new ItemStackHandler(4) {
        @Override
        public int getSlotLimit(int slot) {
            return switch (slot) {
                case 0 -> 8;
                case 1, 2, 3 -> 1;
                default -> 1;
            };
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (slot == 0) {
                invalidatePickupRangeCache();
            }

            setChanged();
            if (!level.isClientSide()) {
                try {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                } catch (Exception e) {
                    LOGGER.error("Failed to send block update for collector at {}", getBlockPos(), e);
                }
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) {
                return true;
            }

            try {
                stack.save(level.registryAccess());
                return super.isItemValid(slot, stack);
            } catch (Exception e) {
                LOGGER.warn("Rejected invalid item {} for collector slot {}", stack, slot, e);
                return false;
            }
        }
    };

    public final ItemStackHandler outputInventory = new ItemStackHandler(35) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return stack.getMaxStackSize();
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) return ItemStack.EMPTY;

            int limit = getStackLimit(slot, stack);
            ItemStack existing = getStackInSlot(slot);

            if (!existing.isEmpty() && !ItemStack.isSameItemSameComponents(stack, existing)) {
                return stack;
            }

            int existingCount = existing.getCount();
            int canInsert = limit - existingCount;
            if (canInsert <= 0) return stack;

            int toInsert = Math.min(stack.getCount(), canInsert);

            if (!simulate) {
                if (existing.isEmpty()) {
                    setStackInSlot(slot, stack.copyWithCount(toInsert));
                } else {
                    existing.grow(toInsert);
                }
            }

            return toInsert == stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - toInsert);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }

            try {
                stack.save(level.registryAccess());
                return true;
            } catch (Exception e) {
                LOGGER.warn("Rejected invalid item {} for collector output slot {}", stack, slot, e);
                return false;
            }
        }
    };

    private int storedXP = 0;
    private boolean xpCollectionEnabled = false;
    private int downUpOffset = 0;
    private int northSouthOffset = 0;
    private int eastWestOffset = 0;

    private boolean topSideActive = false;
    private boolean eastSideActive = false;
    private boolean frontSideActive = false;
    private boolean westSideActive = false;
    private boolean bottomSideActive = false;
    private boolean backSideActive = false;

    public FlowtechCollectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.COLLECTOR_BE.get(), pos, blockState);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.COLLECTOR_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof FlowtechCollectorBlockEntity collector) {
                        return collector.getItemHandler(direction);
                    }
                    return null;
                });
    }

    @Nullable
    public IItemHandler getItemHandler(@Nullable Direction direction) {
        if (direction == null) {
            return outputInventory;
        }

        Direction blockFacing = getBlockState().getValue(FlowtechCollectorBlock.FACING);

        boolean sideActive = switch (direction) {
            case UP -> topSideActive;
            case DOWN -> bottomSideActive;
            case NORTH, SOUTH, EAST, WEST -> {
                if (direction == blockFacing) {
                    yield frontSideActive;
                } else if (direction == blockFacing.getOpposite()) {
                    yield backSideActive;
                } else if (direction == blockFacing.getClockWise()) {
                    yield eastSideActive;
                } else if (direction == blockFacing.getCounterClockWise()) {
                    yield westSideActive;
                } else {
                    yield false;
                }
            }
        };

        if (sideActive) {
            return new ExtractOnlyWrapper(outputInventory);
        }

        return EmptyItemHandler.INSTANCE;
    }

    private static class ExtractOnlyWrapper implements IItemHandler {
        private final IItemHandler wrapped;

        public ExtractOnlyWrapper(IItemHandler wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public int getSlots() {
            return wrapped.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return wrapped.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return wrapped.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return wrapped.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    }

    public void loadFromBlockItem(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (level != null) {
                HolderLookup.Provider registries = level.registryAccess();

                if (tag.contains("moduleSlots")) {
                    moduleSlots.deserializeNBT(registries, tag.getCompound("moduleSlots"));
                }

                storedXP = tag.getInt("storedXP");
                xpCollectionEnabled = tag.getBoolean("xpCollectionEnabled");
                downUpOffset = tag.getInt("downUpOffset");
                northSouthOffset = tag.getInt("northSouthOffset");
                eastWestOffset = tag.getInt("eastWestOffset");

                topSideActive = tag.getBoolean("topSideActive");
                eastSideActive = tag.getBoolean("eastSideActive");
                frontSideActive = tag.getBoolean("frontSideActive");
                westSideActive = tag.getBoolean("westSideActive");
                bottomSideActive = tag.getBoolean("bottomSideActive");
                backSideActive = tag.getBoolean("backSideActive");

                if (!level.isClientSide()) {
                    FlowtechCollectorBlock.updateXpCollectionState(level, worldPosition, xpCollectionEnabled);
                }

                setChanged();

                if (!level.isClientSide()) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }
        }
    }

    public int getStoredXP() { return storedXP; }

    public void setStoredXP(int xp) {
        int oldXP = this.storedXP;
        this.storedXP = Math.max(0, xp);

        if (oldXP != this.storedXP) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    }

    public boolean isXpCollectionEnabled() { return xpCollectionEnabled; }
    public void setXpCollectionEnabled(boolean enabled) {
        if (this.xpCollectionEnabled != enabled) {
            this.xpCollectionEnabled = enabled;
            setChanged();

            if (level != null && !level.isClientSide()) {
                FlowtechCollectorBlock.updateXpCollectionState(level, worldPosition, enabled);
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    }

    public int getDownUpOffset() { return downUpOffset; }
    public void setDownUpOffset(int offset) {
        int newOffset = Math.max(-10, Math.min(10, offset));
        if (this.downUpOffset != newOffset) {
            this.downUpOffset = newOffset;
            invalidateCollectionAreaCache();
            setChanged();
        }
    }

    public int getNorthSouthOffset() { return northSouthOffset; }
    public void setNorthSouthOffset(int offset) {
        int newOffset = Math.max(-10, Math.min(10, offset));
        if (this.northSouthOffset != newOffset) {
            this.northSouthOffset = newOffset;
            invalidateCollectionAreaCache();
            setChanged();
        }
    }

    public int getEastWestOffset() { return eastWestOffset; }
    public void setEastWestOffset(int offset) {
        int newOffset = Math.max(-10, Math.min(10, offset));
        if (this.eastWestOffset != newOffset) {
            this.eastWestOffset = newOffset;
            invalidateCollectionAreaCache();
            setChanged();
        }
    }

    public boolean isTopSideActive() { return topSideActive; }
    public void setTopSideActive(boolean active) { this.topSideActive = active; setChanged(); }

    public boolean isEastSideActive() { return eastSideActive; }
    public void setEastSideActive(boolean active) { this.eastSideActive = active; setChanged(); }

    public boolean isFrontSideActive() { return frontSideActive; }
    public void setFrontSideActive(boolean active) { this.frontSideActive = active; setChanged(); }

    public boolean isWestSideActive() { return westSideActive; }
    public void setWestSideActive(boolean active) { this.westSideActive = active; setChanged(); }

    public boolean isBottomSideActive() { return bottomSideActive; }
    public void setBottomSideActive(boolean active) { this.bottomSideActive = active; setChanged(); }

    public boolean isBackSideActive() { return backSideActive; }
    public void setBackSideActive(boolean active) { this.backSideActive = active; setChanged(); }

    public int getPickupRange() {
        if (pickupRangeDirty || cachedPickupRange == -1) {
            int baseRange = 3;
            int modules = moduleSlots.getStackInSlot(0).getCount();
            cachedPickupRange = baseRange + modules;
            pickupRangeDirty = false;
        }
        return cachedPickupRange;
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(outputInventory.getSlots());
        for (int i = 0; i < outputInventory.getSlots(); i++) {
            inv.setItem(i, outputInventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);

        for (int i = 0; i < outputInventory.getSlots(); i++) {
            outputInventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        try {
            tag.put("moduleSlots", moduleSlots.serializeNBT(registries));
        } catch (Exception e) {
            LOGGER.error("Failed to serialize module slots for collector at {}", getBlockPos(), e);
            tag.put("moduleSlots", new ItemStackHandler(4).serializeNBT(registries));
        }

        try {
            tag.put("outputInventory", outputInventory.serializeNBT(registries));
        } catch (Exception e) {
            LOGGER.error("Failed to serialize output inventory for collector at {}", getBlockPos(), e);
            tag.put("outputInventory", new ItemStackHandler(35).serializeNBT(registries));
        }

        tag.putInt("storedXP", storedXP);
        tag.putBoolean("xpCollectionEnabled", xpCollectionEnabled);
        tag.putInt("downUpOffset", downUpOffset);
        tag.putInt("northSouthOffset", northSouthOffset);
        tag.putInt("eastWestOffset", eastWestOffset);

        tag.putBoolean("topSideActive", topSideActive);
        tag.putBoolean("eastSideActive", eastSideActive);
        tag.putBoolean("frontSideActive", frontSideActive);
        tag.putBoolean("westSideActive", westSideActive);
        tag.putBoolean("bottomSideActive", bottomSideActive);
        tag.putBoolean("backSideActive", backSideActive);
    }

    public void invalidateAllCaches() {
        invalidatePickupRangeCache();
        invalidateCollectionAreaCache();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        try {
            if (tag.contains("moduleSlots")) {
                moduleSlots.deserializeNBT(registries, tag.getCompound("moduleSlots"));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to deserialize module slots for collector at {}", getBlockPos(), e);
            for (int i = 0; i < moduleSlots.getSlots(); i++) {
                moduleSlots.setStackInSlot(i, ItemStack.EMPTY);
            }
        }

        try {
            if (tag.contains("outputInventory")) {
                outputInventory.deserializeNBT(registries, tag.getCompound("outputInventory"));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to deserialize output inventory for collector at {}", getBlockPos(), e);
            for (int i = 0; i < outputInventory.getSlots(); i++) {
                outputInventory.setStackInSlot(i, ItemStack.EMPTY);
            }
        }

        storedXP = tag.getInt("storedXP");
        xpCollectionEnabled = tag.getBoolean("xpCollectionEnabled");
        downUpOffset = tag.getInt("downUpOffset");
        northSouthOffset = tag.getInt("northSouthOffset");
        eastWestOffset = tag.getInt("eastWestOffset");

        topSideActive = tag.getBoolean("topSideActive");
        eastSideActive = tag.getBoolean("eastSideActive");
        frontSideActive = tag.getBoolean("frontSideActive");
        westSideActive = tag.getBoolean("westSideActive");
        bottomSideActive = tag.getBoolean("bottomSideActive");
        backSideActive = tag.getBoolean("backSideActive");

        invalidatePickupRangeCache();
        invalidateCollectionAreaCache();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu.flowtech.collector");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new FlowtechCollectorMenu(i, inventory, this);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        try {
            return saveWithoutMetadata(pRegistries);
        } catch (Exception e) {
            LOGGER.error("Failed to create update tag for collector at {}", getBlockPos(), e);
            CompoundTag tag = new CompoundTag();
            tag.putString("id", ModBlockEntities.COLLECTOR_BE.get().toString());
            return tag;
        }
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;

        tickCounter++;
        if (tickCounter >= COLLECTION_INTERVAL) {
            tickCounter = 0;
            collectItems();
            if (xpCollectionEnabled) {
                collectXP();
            }
        }
    }

    private void collectItems() {
        AABB collectionArea = getCollectionArea();
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, collectionArea);

        for (ItemEntity itemEntity : items) {
            if (itemEntity.isAlive() && !itemEntity.hasPickUpDelay()) {
                ItemStack stack = itemEntity.getItem().copy();

                if (shouldVoidItem(stack)) {
                    int amount = stack.getCount();
                    ItemStack currentStack = itemEntity.getItem();

                    if (amount >= currentStack.getCount()) {
                        itemEntity.discard();
                    } else {
                        ItemStack newStack = currentStack.copyWithCount(currentStack.getCount() - amount);
                        itemEntity.setItem(newStack);
                    }
                } else {
                    ItemStack remaining = insertIntoInventory(stack);

                    int insertedAmount = stack.getCount() - remaining.getCount();

                    if (insertedAmount > 0) {
                        ItemStack currentStack = itemEntity.getItem();
                        if (insertedAmount >= currentStack.getCount()) {
                            itemEntity.discard();
                        } else {
                            ItemStack newStack = currentStack.copyWithCount(currentStack.getCount() - insertedAmount);
                            itemEntity.setItem(newStack);
                        }
                    }
                }
            }
        }
    }

    private boolean shouldVoidItem(ItemStack stack) {
        for (int i = 1; i <= 3; i++) {
            ItemStack moduleStack = moduleSlots.getStackInSlot(i);
            if (moduleStack.getItem() instanceof VoidFilterItem) {
                VoidFilterData filterData = moduleStack.getOrDefault(ModDataComponents.VOID_FILTER_DATA.get(), VoidFilterData.DEFAULT);
                if (filterData.shouldVoidItem(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void collectXP() {
        if (!xpCollectionEnabled) return;

        AABB collectionArea = getCollectionArea();
        List<ExperienceOrb> xpOrbs = level.getEntitiesOfClass(ExperienceOrb.class, collectionArea);

        int collectedXP = 0;
        for (ExperienceOrb orb : xpOrbs) {
            if (orb.isAlive()) {
                collectedXP += orb.getValue();
                orb.discard();
            }
        }

        if (collectedXP > 0) {
            storedXP += collectedXP;
            setChanged();

            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    }

    private AABB getCollectionArea() {
        if (collectionAreaDirty || cachedCollectionArea == null) {
            int range = getPickupRange();
            BlockPos pos = getBlockPos();

            double minX = pos.getX() - range + eastWestOffset;
            double maxX = pos.getX() + range + 1 + eastWestOffset;
            double minY = pos.getY() - range + downUpOffset;
            double maxY = pos.getY() + range + 1 + downUpOffset;
            double minZ = pos.getZ() - range + northSouthOffset;
            double maxZ = pos.getZ() + range + 1 + northSouthOffset;

            cachedCollectionArea = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
            collectionAreaDirty = false;
        }
        return cachedCollectionArea;
    }

    private ItemStack insertIntoInventory(ItemStack stack) {
        for (int i = 0; i < outputInventory.getSlots(); i++) {
            stack = outputInventory.insertItem(i, stack, false);
            if (stack.isEmpty()) break;
        }
        return stack;
    }

    public static <T extends BlockEntity> BlockEntityTicker<T> createTicker() {
        return (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof FlowtechCollectorBlockEntity collector) {
                collector.tick();
            }
        };
    }
}