package com.jeffyjamzhd.jeffybackpacks.api.recipe;

import com.jeffyjamzhd.jeffybackpacks.item.ItemWithInventory;
import com.jeffyjamzhd.jeffybackpacks.registry.JBItems;
import com.jeffyjamzhd.jeffybackpacks.registry.JBTags;
import net.minecraft.src.*;

public class RecipeRemoveFilter implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        ItemStack input = getInputStack(inventory);
        return input != null;
    }

    private ItemStack getInputStack(InventoryCrafting inventory) {
        ItemStack backpackStack = null;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stackAt = inventory.getStackInSlot(i);
            if (stackAt != null) {
                // Test for backpack first
                if (JBTags.TAG_BACKPACKS.test(stackAt)) {
                    // Make sure it has a filter
                    ItemWithInventory itemInv = (ItemWithInventory) stackAt.getItem();
                    if (!itemInv.hasFilterTag(stackAt) || backpackStack != null)
                        break;
                    backpackStack = stackAt;
                } else {
                    return null;
                }
            }
        }

        return backpackStack;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        // Get stacks
        ItemStack backpackStack = getInputStack(inventory);

        if (backpackStack != null) {
            // Get FilterInventory nbt
            NBTTagCompound filterInv =
                    backpackStack.stackTagCompound.getCompoundTag("FilterInventory");

            // New filter stack
            ItemStack newFilter = new ItemStack(JBItems.filter, 1);
            newFilter.stackTagCompound = new NBTTagCompound();
            newFilter.stackTagCompound.setCompoundTag("FilterInventory", filterInv);

            // Return new backpack
            return newFilter;
        }
        return null;
    }

    @Override
    public ItemStack[] getSecondaryOutput(IInventory inventory) {
        ItemStack input = getInputStack((InventoryCrafting) inventory);
        if (input != null) {
            // Remove FilterInventory
            ItemStack newBackpack = input.copy();
            NBTTagCompound compound = newBackpack.getTagCompound();
            compound = compound.getCompoundTag("BackpackInventory");

            newBackpack.stackTagCompound = new NBTTagCompound();
            newBackpack.stackTagCompound.setCompoundTag("BackpackInventory", compound);

            return new ItemStack[]{newBackpack};
        }
        return null;
    }

    @Override
    public boolean hasSecondaryOutput() {
        return true;
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Override
    public boolean matches(IRecipe iRecipe) {
        return false;
    }
}
