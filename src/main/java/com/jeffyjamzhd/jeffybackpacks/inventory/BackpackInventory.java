package com.jeffyjamzhd.jeffybackpacks.inventory;

import com.jeffyjamzhd.jeffybackpacks.api.IItemStackInventory;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;

import java.util.UUID;

public class BackpackInventory implements IItemStackInventory {
    protected ItemStack[] inventory;
    protected final int size;

    public BackpackInventory(ItemStack stack, int inventorySize) {
        // Create tag compound if one doesn't exist
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        // Prepare inventory
        NBTTagCompound compound = stack.getTagCompound();
        this.size = inventorySize;
        this.inventory = new ItemStack[this.size];

        // Read stack NBT
        readFromNBT(compound);
    }

    @Override
    public void onInventoryChanged() {
        for (int index = 0; index < this.size; index++) {
            ItemStack at = getStackInSlot(index);
            if (at != null && at.stackSize == 0) {
                setInventorySlotContents(index, null);
            }
        }
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        // Don't do anything if there's no stack
        if (inventory[index] == null) {
            return null;
        }

        // Prepare for split
        ItemStack at = inventory[index];
        ItemStack split;

        // Split if count is below stack size, otherwise
        // null out stack in inventory and return it
        if (inventory[index].stackSize > count) {
            split = at.splitStack(count);
        } else {
            split = at;
            inventory[index] = null;
        }
        return split;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        ItemStack stack = getStackInSlot(index);
        this.setInventorySlotContents(index, null);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (0 <= index && index < this.size)
            inventory[index] = stack;
    }

    @Override
    public int getSizeInventory() {
        return size;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory[index];
    }

    //***       Class specific methods        ***//

    /**
     * Gets first {@link ItemStack} in inventory
     */
    public ItemStack getFirstStack() {
        for (int i = 0; i < size; i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack != null) {
                return stack;
            }
        }
        return null;
    }

    /**
     * Puts an {@link ItemStack} at {@code slot}.
     * Handles stack merging when applicable.
     */
    public ItemStack putStackAt(ItemStack stack, int slot) {
        ItemStack slotStack = getStackInSlot(slot);

        if (slotStack != null) {
            // Attempt merge
            int countInSlot = slotStack.stackSize;
            int maxCount = slotStack.getMaxStackSize();
            int diff = Math.min(maxCount - countInSlot, stack.stackSize);

            setInventorySlotContents(slot, slotStack.setStackSize(slotStack.getStackSize() + diff));
            stack.stackSize -= diff;
        } else {
            // Put stack in slot, splitting incase
            // the stack is over maximum
            setInventorySlotContents(slot, stack);
            return null;
        }

        return stack;
    }

    /**
     * Attempts to put an {@link ItemStack} into this
     * inventory, somewhat similar behavior to shift clicking
     */
    public ItemStack putStackSmart(ItemStack stack) {
        // Iterate inventory and look for an open slot
        // or matching stack
        for (int slot = 0; slot < this.size; slot++) {
            ItemStack slotStack = getStackInSlot(slot);

            // Attempt to put stack in slot
            if (slotStack == null) {
                stack = putStackAt(stack, slot);
            } else if (slotStack.itemID == stack.itemID) {
                stack = putStackAt(stack, slot);
            }

            // No stack, return null
            if (stack == null)
                return null;

            // Stack size under 0, return null
            if (stack.stackSize <= 0)
                return null;
        }

        // Not enough room, so return remainder
        return stack;
    }

    public void readFromNBT(NBTTagCompound compound) {
        // Get inventory tag
        NBTTagCompound content = compound.getCompoundTag("BackpackInventory");
        if (content == null)
            return;

        // Begin parsing item list
        NBTTagList list = content.getTagList("ItemStacks");
        for (int i = 0; i < list.tagCount() && i < inventory.length; i++) {
            // Get ItemStack entry
            NBTTagCompound entry = (NBTTagCompound) list.tagAt(i);
            int slot = entry.getInteger("Slot");

            // Attempt to parse ItemStack data
            try {
                inventory[slot] = ItemStack.loadItemStackFromNBT(entry);
            } catch (NullPointerException npe) {
                inventory[slot] = null;
            }
        }
    }

    public void writeToNBT(NBTTagCompound compound) {
        // Iterate through inventory
        NBTTagList list = new NBTTagList();
        for (int slot = 0; slot < this.inventory.length; slot++) {
            if (this.inventory[slot] != null && this.inventory[slot].stackSize > 0) {
                // Write item data to NBT
                NBTTagCompound slotTag = new NBTTagCompound();
                list.appendTag(slotTag);
                slotTag.setInteger("Slot", slot);
                this.inventory[slot].writeToNBT(slotTag);
            }
        }

        NBTTagCompound backpack = new NBTTagCompound();
        backpack.setTag("ItemStacks", list);
        compound.setTag("BackpackInventory", backpack);
        compound.setInteger("Size", size);
    }

    /**
     * Gets inventory slot count from provided NBT compound.
     * If there is no tag/data, returns {@code orDefault}
     */
    private int readSlotCountFromNBT(NBTTagCompound compound, int orDefault) {
        // Get value from tag
        if (compound != null) {
            NBTTagCompound content = compound.getCompoundTag("BackpackInventory");
            if (content != null) {
                // It has a set inventory size, nice
                return content.getInteger("InventorySize");
            }
        }
        // No tag or value, return param value
        return orDefault;
    }

    /**
     * Gets this inventory's UUID (identifier) from provided
     * NBT compound. If there is no tag/data, returns a freshly
     * generated UUID
     */
    private String readUUIDFromNBT(NBTTagCompound compound) {
        // Get value from tag
        if (compound != null) {
            NBTTagCompound content = compound.getCompoundTag("BackpackInventory");
            if (content != null) {
                // It has a set UUID, nice
                return content.getString("Identifier");
            }
        }
        // No tag or value, make one!
        return UUID.randomUUID().toString();
    }

    //***       Unused interface methods        ***//

    @Override
    public String getInvName() {
        return "";
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean isInvNameLocalized() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void openChest() {
    }

    @Override
    public void closeChest() {
    }

}
