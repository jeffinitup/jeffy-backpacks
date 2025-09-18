package com.jeffyjamzhd.jeffybackpacks.emi;

import btw.item.BTWItems;
import com.google.common.collect.Lists;
import com.jeffyjamzhd.jeffybackpacks.api.recipe.RecipeBundle;
import com.jeffyjamzhd.jeffybackpacks.item.ItemWithInventory;
import emi.dev.emi.emi.EmiUtil;
import emi.dev.emi.emi.api.recipe.EmiCraftingRecipe;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.GeneratedSlotWidget;
import emi.dev.emi.emi.api.widget.SlotWidget;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.dev.emi.emi.recipe.EmiShapedRecipe;
import emi.dev.emi.emi.recipe.btw.special.EmiKnittingRecipe;
import emi.shims.java.com.unascribed.retroemi.ItemStacks;
import emi.shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.src.ItemStack;

import java.util.List;
import java.util.Random;

public class EMIBundleRecipe extends EmiCraftingRecipe {
    protected final int unique;

    public EMIBundleRecipe(RecipeBundle recipe) {
        super(EmiShapedRecipe.padIngredients(recipe), EmiStack.of(recipe.getRecipeOutput()),
                new SyntheticIdentifier(recipe, "/coloring"), false, null);
        unique = EmiUtil.RANDOM.nextInt();
    }

    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 18);

        for(int i = 0; i < 9; ++i) {
            widgets.add(this.getInputWidget(i, i % 3 * 18, i / 3 * 18));
        }

        widgets.add(this.getOutputWidget(92, 14).large(true).recipeContext(this));
    }

    public boolean supportsRecipeTree() {
        return false;
    }

    public SlotWidget getInputWidget(int slot, int x, int y) {
        return new GeneratedSlotWidget((r) -> {
            List<EmiStack> items = this.getItems(r).stream().map(EmiStack::of).toList();
            return (slot < items.size() ? items.get(slot) : EmiStack.EMPTY);
        }, this.unique, x, y);
    }

    public SlotWidget getOutputWidget(int x, int y) {
        return new GeneratedSlotWidget((r) -> {
            ItemStack stack = this.output.getItemStack();
            int range = EmiKnittingRecipe.getAverageWoolColors(this.getItems(r));
            ItemWithInventory bundleItem = (ItemWithInventory) stack.getItem();
            bundleItem.func_82813_b(stack, range);
            return EmiStack.of(stack);
        }, this.unique, x, y);
    }

    private List<ItemStack> getItems(Random random) {
        List<ItemStack> items = Lists.newArrayList();

        for(int i = 0; i < 9; ++i) {
            ItemStack stack = ((this.input.get(i)).getEmiStacks().get(0)).getItemStack();
            if (stack == null) {
                items.add(ItemStacks.EMPTY);
            } else if (stack.itemID == BTWItems.woolKnit.itemID) {
                items.add(new ItemStack(BTWItems.woolKnit, 1, random.nextInt(15)));
            } else {
                items.add(stack);
            }
        }

        return items;
    }
}
