package com.jeffyjamzhd.jeffybackpacks.emi;

import com.jeffyjamzhd.jeffybackpacks.registry.JBItems;
import com.jeffyjamzhd.jeffybackpacks.registry.JBTags;
import emi.dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.GeneratedSlotWidget;
import emi.dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ResourceLocation;

import java.util.List;

public class EMIAddFilterRecipe extends EmiPatternCraftingRecipe {
    private int nextBag = 0;

    public EMIAddFilterRecipe() {
        super(List.of(
                EmiIngredient.of(JBTags.TAG_BACKPACKS),
                EmiStack.of(JBItems.filter)),
                EmiStack.of(JBItems.filter), new ResourceLocation("jbp:addFilterRecipe"),
                true
        );
    }

    @Override
    public SlotWidget getInputWidget(int slot, int x, int y) {
        return switch (slot) {
            case 0 -> new GeneratedSlotWidget(random -> EmiStack.of(getNextBag(false)), this.unique, x, y);
            case 1 -> new SlotWidget(EmiStack.of(JBItems.filter), x, y);
            default -> new SlotWidget(EmiStack.EMPTY, x, y);
        };
    }

    @Override
    public SlotWidget getOutputWidget(int x, int y) {
        return new GeneratedSlotWidget(random ->
            EmiStack.of(getNextBag(true)), this.unique, x, y);
    }

    private ItemStack getNextBag(boolean isOutput) {
        List<ItemStack> stacks = JBTags.TAG_BACKPACKS.getItems();
        ItemStack stack = stacks.get(nextBag).copy();
        if (isOutput) {
            stack.stackTagCompound = new NBTTagCompound();
            stack.stackTagCompound.setBoolean("EmiEffect", true);
            nextBag = nextBag + 1 >= stacks.size() ? 0 : nextBag + 1;
        }

        return stack;
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }
}
