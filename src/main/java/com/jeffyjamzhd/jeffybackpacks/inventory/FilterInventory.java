package com.jeffyjamzhd.jeffybackpacks.inventory;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;

public class FilterInventory extends BackpackInventory {
    private boolean blacklist;

    public FilterInventory(ItemStack stack) {
        super(stack, 9);
        blacklist = readFilterModeFromNBT(stack.stackTagCompound);
    }

    public FilterInventory(NBTTagCompound compound) {
        super(compound, 9);
        blacklist = readFilterModeFromNBT(compound);
    }

    @Override
    public String getRootTagString() {
        return "FilterInventory";
    }

    /**
     * Checks if provided stack is part of this filter inventory.
     * {@code true} if stack has a match
     */
    public boolean matches(ItemStack stack) {
        boolean match = false;
        for (ItemStack invStack : inventory) {
            if (!blacklist && invStack.matches(stack, true)) {
                return true;
            } else if (blacklist && invStack.matches(stack, true)) {
                return false;
            } else if (blacklist && !invStack.matches(stack, true)) {
                match = true;
            }
        }
        return match;
    }

    @Override
    public ItemStack popFirstStack() {
        super.popFirstStack();
        return null;
    }

    @Override
    public ItemStack putStack(ItemStack stack) {
        if (inventory.size() < this.size) {
            // Check if not already in filter
            for (ItemStack invStack : inventory) {
                if (invStack.isItemEqual(stack))
                    return null;
            }

            super.putStack(new ItemStack(stack.getItem(), 1, stack.getItemDamage()));
        }

        return stack;
    }

    @Override
    public ItemStack putStackAt(ItemStack stack, int slot) {
        // Do nothing, this function is irrelevant
        return stack;
    }

    @Override
    public ItemStack putStackSmart(ItemStack stack) {
        return putStack(stack);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = super.writeToNBT(compound);
        NBTTagCompound root = tag.getCompoundTag(getRootTagString());
        root.setBoolean("FilterMode", blacklist);
        return tag;
    }

    private boolean readFilterModeFromNBT(NBTTagCompound compound) {
        if (compound != null) {
            NBTTagCompound rootTag = compound.getCompoundTag(getRootTagString());
            if (rootTag != null && rootTag.hasKey("FilterMode")) {
                return rootTag.getBoolean("FilterMode");
            }
        }
        return false;
    }

    public boolean isBlacklist() {
        return blacklist;
    }

    public void invertMode() {
        blacklist = !blacklist;
    }

}
