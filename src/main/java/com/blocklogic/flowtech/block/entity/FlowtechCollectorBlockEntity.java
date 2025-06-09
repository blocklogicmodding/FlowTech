package com.blocklogic.flowtech.block.entity;

import com.blocklogic.flowtech.block.custom.FlowtechCollectorBlock;
import com.blocklogic.flowtech.screen.custom.FlowtechCollectorMenu;
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

import java.util.List;

import javax.annotation.Nullable;

public class FlowtechCollectorBlockEntity extends BlockEntity implements MenuProvider {

    private int tickCounter = 0;
    private static final int COLLECTION_INTERVAL = 10;

    public final ItemStackHandler moduleSlots = new ItemStackHandler(5) {
        @Override
        public int getSlotLimit(int slot) {
            return switch (slot) {
                case 0 -> 8;
                case 1 -> 10;
                case 2, 3, 4 -> 1;
                default -> 1;
            };
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public final ItemStackHandler outputInventory = new ItemStackHandler(35) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
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
    public void setDownUpOffset(int offset) { this.downUpOffset = Math.max(-10, Math.min(10, offset)); setChanged(); }

    public int getNorthSouthOffset() { return northSouthOffset; }
    public void setNorthSouthOffset(int offset) { this.northSouthOffset = Math.max(-10, Math.min(10, offset)); setChanged(); }

    public int getEastWestOffset() { return eastWestOffset; }
    public void setEastWestOffset(int offset) { this.eastWestOffset = Math.max(-10, Math.min(10, offset)); setChanged(); }

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
        int baseRange = 3;
        int modules = moduleSlots.getStackInSlot(0).getCount();
        return baseRange + modules;
    }

    public int getSlotCapacity() {
        int baseCapacity = 64;
        int modules = moduleSlots.getStackInSlot(1).getCount();
        return baseCapacity + (modules * 64);
    }

    public void clearContents() {
        for (int i = 0; i < outputInventory.getSlots(); i++) {
            outputInventory.setStackInSlot(i, ItemStack.EMPTY);
        }
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

        tag.put("moduleSlots", moduleSlots.serializeNBT(registries));
        tag.put("outputInventory", outputInventory.serializeNBT(registries));

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

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        moduleSlots.deserializeNBT(registries, tag.getCompound("moduleSlots"));
        outputInventory.deserializeNBT(registries, tag.getCompound("outputInventory"));

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
        return saveWithoutMetadata(pRegistries);
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
                ItemStack stack = itemEntity.getItem();
                if (canCollectItem(stack)) {
                    ItemStack remaining = insertIntoInventory(stack);
                    if (remaining.isEmpty()) {
                        itemEntity.discard();
                    } else {
                        itemEntity.setItem(remaining);
                    }
                }
            }
        }
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
        int range = getPickupRange();
        BlockPos pos = getBlockPos();

        double minX = pos.getX() - range + eastWestOffset;
        double maxX = pos.getX() + range + 1 + eastWestOffset;
        double minY = pos.getY() - range + downUpOffset;
        double maxY = pos.getY() + range + 1 + downUpOffset;
        double minZ = pos.getZ() - range + northSouthOffset;
        double maxZ = pos.getZ() + range + 1 + northSouthOffset;

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private boolean canCollectItem(ItemStack stack) {
        ItemStack filter1 = moduleSlots.getStackInSlot(2);
        ItemStack filter2 = moduleSlots.getStackInSlot(3);
        ItemStack filter3 = moduleSlots.getStackInSlot(4);

        if (filter1.isEmpty() && filter2.isEmpty() && filter3.isEmpty()) {
            return true;
        }
        // TODO: Implement actual filter logic based on filter modules
        return true;
    }

    private ItemStack insertIntoInventory(ItemStack stack) {
        int slotCapacity = getSlotCapacity();

        for (int i = 0; i < outputInventory.getSlots(); i++) {
            ItemStack slotStack = outputInventory.getStackInSlot(i);

            if (slotStack.isEmpty()) {
                int insertAmount = Math.min(stack.getCount(), slotCapacity);
                ItemStack toInsert = stack.copy();
                toInsert.setCount(insertAmount);
                outputInventory.setStackInSlot(i, toInsert);

                stack.shrink(insertAmount);
                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            } else if (ItemStack.isSameItemSameComponents(stack, slotStack)) {
                int spaceLeft = slotCapacity - slotStack.getCount();
                if (spaceLeft > 0) {
                    int insertAmount = Math.min(stack.getCount(), spaceLeft);
                    slotStack.grow(insertAmount);
                    stack.shrink(insertAmount);

                    if (stack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
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