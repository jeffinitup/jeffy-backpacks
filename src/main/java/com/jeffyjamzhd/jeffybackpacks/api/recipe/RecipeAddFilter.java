package com.jeffyjamzhd.jeffybackpacks.api.recipe;

import com.jeffyjamzhd.jeffybackpacks.item.ItemFilter;
import com.jeffyjamzhd.jeffybackpacks.item.ItemWithInventory;
import com.jeffyjamzhd.jeffybackpacks.registry.JBTags;
import net.minecraft.src.*;

public class RecipeAddFilter implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        ItemStack[] stacks = getInputStacks(inventory);
        return stacks[0] != null && stacks[1] != null;
    }

    private ItemStack[] getInputStacks(InventoryCrafting inventory) {
        ItemStack backpackStack = null;
        ItemStack filterStack = null;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stackAt = inventory.getStackInSlot(i);
            if (stackAt != null) {
                // Test for backpack first
                if (JBTags.TAG_BACKPACKS.test(new ItemStack(stackAt.getItem()))) {
                    // Make sure it doesn't already have a filter
                    ItemWithInventory itemInv = (ItemWithInventory) stackAt.getItem();
                    if (itemInv.hasFilterTag(stackAt) || backpackStack != null)
                        break;
                    backpackStack = stackAt;
                }

                // Test for filter
                if (stackAt.getItem() instanceof ItemFilter) {
                    if (filterStack != null)
                        break;
                    filterStack = stackAt;
                }
            }
        }

        return new ItemStack[]{backpackStack, filterStack};
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        // Get stacks
        ItemStack[] stacks = getInputStacks(inventory);
        ItemStack backpackStack = stacks[0];
        ItemStack filterStack = stacks[1];

        if (backpackStack != null && filterStack != null) {
            // New backpack stack
            ItemStack newBackpack = backpackStack.copy();

            // Merge NBT data
            NBTTagCompound filterData = filterStack.getTagCompound();
            filterData = filterData.getCompoundTag("FilterInventory");
            newBackpack.getTagCompound().setCompoundTag("FilterInventory", filterData);

            // Return new backpack
            return newBackpack;
        }

        return null;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Override
    public boolean matches(IRecipe recipe) {
        return false;
    }

    @Override
    public boolean hasSecondaryOutput() {
        return false;
    }

    @Override
    public ItemStack[] getSecondaryOutput(IInventory iInventory) {
        return null;
    }
}
