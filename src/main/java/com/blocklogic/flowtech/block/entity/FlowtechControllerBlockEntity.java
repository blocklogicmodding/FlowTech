package com.blocklogic.flowtech.block.entity;

import com.blocklogic.flowtech.item.ModItems;
import com.blocklogic.flowtech.screen.custom.FlowtechControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlowtechControllerBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        public int getSlotLimit(int slot) {
            return 10;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.getItem() == ModItems.SHARPNESS_MODULE.get();
                case 1 -> stack.getItem() == ModItems.FIRE_ASPECT_MODULE.get();
                case 2 -> stack.getItem() == ModItems.SMITE_MODULE.get();
                case 3 -> stack.getItem() == ModItems.BOA_MODULE.get();
                case 4 -> stack.getItem() == ModItems.LOOTING_MODULE.get();
                default -> false;
            };
        }
    };

    private final Set<BlockPos> linkedPads = new HashSet<>();
    private boolean playerKillMode = false;

    public FlowtechControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CONTROLLER_BE.get(), pos, blockState);
    }

    public void addPad(BlockPos padPos) {
        linkedPads.add(padPos);
        setChanged();

        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public void removePad(BlockPos padPos) {
        linkedPads.remove(padPos);
        setChanged();

        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public Set<BlockPos> getLinkedPads() {
        return new HashSet<>(linkedPads);
    }

    public List<BlockPos> getLinkedPadsList() {
        return new ArrayList<>(linkedPads);
    }

    public int getLinkedPadCount() {
        return linkedPads.size();
    }

    public void clearAllLinkedPads() {
        if (level != null && !level.isClientSide()) {
            for (BlockPos padPos : linkedPads) {
                if (level.getBlockEntity(padPos) instanceof AttackPadBlockEntity attackPad) {
                    attackPad.clearControllerPos();
                }
            }
        }
        linkedPads.clear();
        setChanged();
    }

    public boolean isPlayerKillMode() {
        return playerKillMode;
    }

    public void setPlayerKillMode(boolean playerKillMode) {
        this.playerKillMode = playerKillMode;
        setChanged();

        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public int getModuleCount(int slot) {
        if (slot < 0 || slot >= inventory.getSlots()) {
            return 0;
        }
        ItemStack stack = inventory.getStackInSlot(slot);
        return stack.isEmpty() ? 0 : Math.min(stack.getCount(), 10);
    }

    public boolean hasSharpnessModule() {
        return getModuleCount(0) > 0;
    }

    public boolean hasFireAspectModule() {
        return getModuleCount(1) > 0;
    }

    public boolean hasSmiteModule() {
        return getModuleCount(2) > 0;
    }

    public boolean hasBaneOfArthropodsModule() {
        return getModuleCount(3) > 0;
    }

    public boolean hasLootingModule() {
        return getModuleCount(4) > 0;
    }

    public int getSharpnessLevel() {
        return getModuleCount(0);
    }

    public int getFireAspectLevel() {
        return getModuleCount(1);
    }

    public int getSmiteLevel() {
        return getModuleCount(2);
    }

    public int getBaneOfArthropodsLevel() {
        return getModuleCount(3);
    }

    public int getLootingLevel() {
        return getModuleCount(4);
    }

    public void clearContents() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
        setChanged();
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for(int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public boolean removePadAt(BlockPos padPos) {
        if (linkedPads.remove(padPos)) {
            if (level != null && !level.isClientSide()) {
                if (level.getBlockEntity(padPos) instanceof AttackPadBlockEntity attackPad) {
                    attackPad.clearControllerPos();
                }
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
            setChanged();
            return true;
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putBoolean("playerKillMode", playerKillMode);

        ListTag padList = new ListTag();
        for (BlockPos padPos : linkedPads) {
            padList.add(LongTag.valueOf(padPos.asLong()));
        }
        tag.put("linkedPads", padList);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        playerKillMode = tag.getBoolean("playerKillMode");

        linkedPads.clear();
        if (tag.contains("linkedPads")) {
            ListTag padList = tag.getList("linkedPads", 4);
            for (int i = 0; i < padList.size(); i++) {
                long posLong = ((LongTag) padList.get(i)).getAsLong();
                BlockPos padPos = BlockPos.of(posLong);
                linkedPads.add(padPos);
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu.flowtech.controller");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new FlowtechControllerMenu(i, inventory, this);
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
}