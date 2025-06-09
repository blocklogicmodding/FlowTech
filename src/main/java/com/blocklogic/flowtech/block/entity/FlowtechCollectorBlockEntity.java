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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

import java.util.List;

import javax.annotation.Nullable;

public class FlowtechCollectorBlockEntity extends BlockEntity implements MenuProvider {

    private int tickCounter = 0;
    private static final int COLLECTION_INTERVAL = 20; // Collect every 20 ticks (1 second)

    // Module slots (5 total)
    public final ItemStackHandler moduleSlots = new ItemStackHandler(5) {
        @Override
        public int getSlotLimit(int slot) {
            return switch (slot) {
                case 0 -> 8;  // Pickup Zone Size modules (max 8)
                case 1 -> 10; // Stack Size modules (max 10)
                case 2, 3, 4 -> 1; // Filter modules (max 1 each)
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

    // Output-only inventory (35 slots, 5x7 grid) - extract only
    public final ItemStackHandler outputInventory = new ItemStackHandler(35) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack; // No insertion allowed
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    // Persistent data
    private int storedXP = 0;
    private boolean xpCollectionEnabled = false;
    private int downUpOffset = 0;
    private int northSouthOffset = 0;
    private int eastWestOffset = 0;

    // Side configuration (6 sides)
    private boolean topSideActive = false;
    private boolean eastSideActive = false;
    private boolean frontSideActive = false;
    private boolean westSideActive = false;
    private boolean bottomSideActive = false;
    private boolean backSideActive = false;

    public FlowtechCollectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.COLLECTOR_BE.get(), pos, blockState);
    }

    // Register capabilities
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.COLLECTOR_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof FlowtechCollectorBlockEntity collector) {
                        return collector.getItemHandler(direction);
                    }
                    return null;
                });
    }

    // Get item handler based on side configuration
    @Nullable
    public IItemHandler getItemHandler(@Nullable Direction direction) {
        if (direction == null) {
            return outputInventory; // Internal access gets full inventory
        }

        // Get the block's facing direction
        Direction blockFacing = getBlockState().getValue(FlowtechCollectorBlock.FACING);

        // Map absolute direction to relative side based on block facing
        boolean sideActive = switch (direction) {
            case UP -> topSideActive;
            case DOWN -> bottomSideActive;
            case NORTH, SOUTH, EAST, WEST -> {
                if (direction == blockFacing) {
                    yield frontSideActive; // This direction is the front of the block
                } else if (direction == blockFacing.getOpposite()) {
                    yield backSideActive; // This direction is the back of the block
                } else if (direction == blockFacing.getClockWise()) {
                    yield eastSideActive; // Relative east side
                } else if (direction == blockFacing.getCounterClockWise()) {
                    yield westSideActive; // Relative west side
                } else {
                    yield false;
                }
            }
        };

        if (sideActive) {
            // Return extract-only wrapper for the output inventory
            return new ExtractOnlyWrapper(outputInventory);
        }

        return EmptyItemHandler.INSTANCE; // No access for inactive sides
    }

    // Wrapper that only allows extraction
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
            return stack; // No insertion allowed
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
            return false; // No insertion allowed
        }
    }

    // Load data from placed block item
    public void loadFromBlockItem(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (level != null) {
                HolderLookup.Provider registries = level.registryAccess();

                // Load module slots
                if (tag.contains("moduleSlots")) {
                    moduleSlots.deserializeNBT(registries, tag.getCompound("moduleSlots"));
                }

                // Load persistent data
                storedXP = tag.getInt("storedXP");
                xpCollectionEnabled = tag.getBoolean("xpCollectionEnabled");
                downUpOffset = tag.getInt("downUpOffset");
                northSouthOffset = tag.getInt("northSouthOffset");
                eastWestOffset = tag.getInt("eastWestOffset");

                // Load side configuration
                topSideActive = tag.getBoolean("topSideActive");
                eastSideActive = tag.getBoolean("eastSideActive");
                frontSideActive = tag.getBoolean("frontSideActive");
                westSideActive = tag.getBoolean("westSideActive");
                bottomSideActive = tag.getBoolean("bottomSideActive");
                backSideActive = tag.getBoolean("backSideActive");

                // Update block state for XP collection immediately
                if (!level.isClientSide()) {
                    FlowtechCollectorBlock.updateXpCollectionState(level, worldPosition, xpCollectionEnabled);
                }

                setChanged();

                // Force update to clients
                if (!level.isClientSide()) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }
        }
    }

    // Getters and setters for persistent data
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

            // DEBUG: Print state change
            System.out.println("XP Collection " + (enabled ? "ENABLED" : "DISABLED"));

            // Update block state immediately
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

    // Side configuration getters/setters
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

    // Calculate pickup range based on modules
    public int getPickupRange() {
        int baseRange = 3; // 7x7 default (3 blocks in each direction)
        int modules = moduleSlots.getStackInSlot(0).getCount();
        return baseRange + modules; // Each module adds 1 block range
    }

    // Calculate slot capacity based on modules
    public int getSlotCapacity() {
        int baseCapacity = 64;
        int modules = moduleSlots.getStackInSlot(1).getCount();
        return baseCapacity + (modules * 64); // Each module adds 64 capacity
    }

    public void clearContents() {
        // Clear ONLY the output inventory, keep modules for persistence
        for (int i = 0; i < outputInventory.getSlots(); i++) {
            outputInventory.setStackInSlot(i, ItemStack.EMPTY);
        }
        // Don't clear modules here - they're saved to the block item
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(outputInventory.getSlots());
        for (int i = 0; i < outputInventory.getSlots(); i++) {
            inv.setItem(i, outputInventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);

        // Clear the output inventory after dropping
        for (int i = 0; i < outputInventory.getSlots(); i++) {
            outputInventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        // Save inventories
        tag.put("moduleSlots", moduleSlots.serializeNBT(registries));
        tag.put("outputInventory", outputInventory.serializeNBT(registries));

        // Save persistent data
        tag.putInt("storedXP", storedXP);
        tag.putBoolean("xpCollectionEnabled", xpCollectionEnabled);
        tag.putInt("downUpOffset", downUpOffset);
        tag.putInt("northSouthOffset", northSouthOffset);
        tag.putInt("eastWestOffset", eastWestOffset);

        // Save side configuration
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

        // Load inventories
        moduleSlots.deserializeNBT(registries, tag.getCompound("moduleSlots"));
        outputInventory.deserializeNBT(registries, tag.getCompound("outputInventory"));

        // Load persistent data
        storedXP = tag.getInt("storedXP");
        xpCollectionEnabled = tag.getBoolean("xpCollectionEnabled");
        downUpOffset = tag.getInt("downUpOffset");
        northSouthOffset = tag.getInt("northSouthOffset");
        eastWestOffset = tag.getInt("eastWestOffset");

        // Load side configuration
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

    // Tick method for item and XP collection
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
        if (!xpCollectionEnabled) return; // Don't collect if disabled

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

            // DEBUG: Print to console
            System.out.println("Collected " + collectedXP + " XP, total now: " + storedXP);

            // Force block state update
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    }

    private AABB getCollectionArea() {
        int range = getPickupRange();
        BlockPos pos = getBlockPos();

        // Apply offsets
        double minX = pos.getX() - range + eastWestOffset;
        double maxX = pos.getX() + range + 1 + eastWestOffset;
        double minY = pos.getY() - range + downUpOffset;
        double maxY = pos.getY() + range + 1 + downUpOffset;
        double minZ = pos.getZ() - range + northSouthOffset;
        double maxZ = pos.getZ() + range + 1 + northSouthOffset;

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private boolean canCollectItem(ItemStack stack) {
        // Check item filters if any are installed
        ItemStack filter1 = moduleSlots.getStackInSlot(2);
        ItemStack filter2 = moduleSlots.getStackInSlot(3);
        ItemStack filter3 = moduleSlots.getStackInSlot(4);

        // If no filters installed, collect everything
        if (filter1.isEmpty() && filter2.isEmpty() && filter3.isEmpty()) {
            return true;
        }

        // TODO: Implement actual filter logic based on filter modules
        // For now, just return true
        return true;
    }

    private ItemStack insertIntoInventory(ItemStack stack) {
        int slotCapacity = getSlotCapacity();

        for (int i = 0; i < outputInventory.getSlots(); i++) {
            ItemStack slotStack = outputInventory.getStackInSlot(i);

            if (slotStack.isEmpty()) {
                // Empty slot - insert up to capacity
                int insertAmount = Math.min(stack.getCount(), slotCapacity);
                ItemStack toInsert = stack.copy();
                toInsert.setCount(insertAmount);
                outputInventory.setStackInSlot(i, toInsert);

                stack.shrink(insertAmount);
                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            } else if (ItemStack.isSameItemSameComponents(stack, slotStack)) {
                // Same item - try to merge
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

        return stack; // Return remaining items that couldn't be inserted
    }

    // Static ticker method for registration
    public static <T extends BlockEntity> BlockEntityTicker<T> createTicker() {
        return (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof FlowtechCollectorBlockEntity collector) {
                collector.tick();
            }
        };
    }
}