package com.jeffyjamzhd.jeffybackpacks.api.recipe;

import btw.item.items.WoolItem;
import btw.item.tag.TagOrStack;
import com.jeffyjamzhd.jeffybackpacks.item.ItemWithInventory;
import net.minecraft.src.*;

public class RecipeBundle extends ShapedRecipes {
    public RecipeBundle(int par1, int par2, TagOrStack[] tagOrStacks, ItemStack stack) {
        super(par1, par2, tagOrStacks, stack);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack result = super.getCraftingResult(inventory);
        if (result != null && result.getItem() instanceof ItemWithInventory item) {
            int color = WoolItem.averageWoolColorsInGrid(inventory);
            if (color != 0xFFFFFF)
                item.func_82813_b(result, color);
        }
        return result;
    }
}
