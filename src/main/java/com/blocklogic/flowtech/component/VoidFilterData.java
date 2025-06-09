package com.blocklogic.flowtech.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public record VoidFilterData(
        List<ItemStack> filterItems,
        boolean ignoreNBT,
        boolean ignoreDurability
) {

    public static final Codec<VoidFilterData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(ItemStack.CODEC).optionalFieldOf("filterItems", List.of()).forGetter(data ->
                            data.filterItems().stream()
                                    .filter(stack -> !stack.isEmpty())
                                    .toList()
                    ),
                    Codec.BOOL.optionalFieldOf("ignoreNBT", true).forGetter(VoidFilterData::ignoreNBT),
                    Codec.BOOL.optionalFieldOf("ignoreDurability", true).forGetter(VoidFilterData::ignoreDurability)
            ).apply(instance, (items, ignoreNBT, ignoreDurability) -> {
                NonNullList<ItemStack> fullList = NonNullList.withSize(45, ItemStack.EMPTY);
                for (int i = 0; i < Math.min(items.size(), 45); i++) {
                    fullList.set(i, items.get(i));
                }
                return new VoidFilterData(fullList, ignoreNBT, ignoreDurability);
            })
    );

    public static final VoidFilterData DEFAULT = new VoidFilterData(
            createEmptyFilterList(),
            true,
            true
    );

    private static List<ItemStack> createEmptyFilterList() {
        NonNullList<ItemStack> list = NonNullList.withSize(45, ItemStack.EMPTY);
        return list;
    }

    public VoidFilterData withIgnoreNBT(boolean ignoreNBT) {
        return new VoidFilterData(this.filterItems, ignoreNBT, this.ignoreDurability);
    }

    public VoidFilterData withIgnoreDurability(boolean ignoreDurability) {
        return new VoidFilterData(this.filterItems, this.ignoreNBT, ignoreDurability);
    }

    public VoidFilterData withFilterItems(List<ItemStack> filterItems) {
        return new VoidFilterData(filterItems, this.ignoreNBT, this.ignoreDurability);
    }

    public void loadIntoHandler(ItemStackHandler handler) {
        for (int i = 0; i < Math.min(filterItems.size(), handler.getSlots()); i++) {
            handler.setStackInSlot(i, filterItems.get(i).copy());
        }
    }

    public static VoidFilterData fromHandler(ItemStackHandler handler, boolean ignoreNBT, boolean ignoreDurability) {
        NonNullList<ItemStack> items = NonNullList.withSize(handler.getSlots(), ItemStack.EMPTY);
        for (int i = 0; i < handler.getSlots(); i++) {
            items.set(i, handler.getStackInSlot(i).copy());
        }
        return new VoidFilterData(items, ignoreNBT, ignoreDurability);
    }

    public boolean shouldVoidItem(ItemStack itemToCheck) {
        if (itemToCheck.isEmpty()) {
            return false;
        }

        for (ItemStack filterItem : filterItems) {
            if (filterItem.isEmpty()) {
                continue;
            }

            if (itemsMatch(itemToCheck, filterItem)) {
                return true;
            }
        }

        return false;
    }

    private boolean itemsMatch(ItemStack itemToCheck, ItemStack filterItem) {
        if (!itemToCheck.is(filterItem.getItem())) {
            return false;
        }

        if (ignoreNBT && ignoreDurability) {
            return true;
        }

        if (ignoreNBT && !ignoreDurability) {
            return itemToCheck.getDamageValue() == filterItem.getDamageValue();
        }

        if (!ignoreNBT && ignoreDurability) {
            ItemStack itemCopy = itemToCheck.copy();
            ItemStack filterCopy = filterItem.copy();

            itemCopy.setDamageValue(0);
            filterCopy.setDamageValue(0);

            return ItemStack.isSameItemSameComponents(itemCopy, filterCopy);
        }

        return ItemStack.isSameItemSameComponents(itemToCheck, filterItem);
    }
}