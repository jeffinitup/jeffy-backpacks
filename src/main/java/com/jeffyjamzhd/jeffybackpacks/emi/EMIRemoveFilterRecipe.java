package com.jeffyjamzhd.jeffybackpacks.emi;

import com.jeffyjamzhd.jeffybackpacks.registry.JBItems;
import emi.dev.emi.emi.EmiUtil;
import emi.dev.emi.emi.api.plugin.BTWPlugin;
import emi.dev.emi.emi.api.recipe.EmiCraftingRecipe;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.SlotWidget;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.dev.emi.emi.screen.tooltip.EmiSecondaryOutputComponent;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ResourceLocation;

import java.util.List;

public class EMIRemoveFilterRecipe extends EmiCraftingRecipe {
    protected final int unique;
    protected final Item bag;

    public EMIRemoveFilterRecipe(Item item) {
        super(List.of(EmiStack.of(item)), EmiStack.of(JBItems.filter),
                new ResourceLocation("jbp:removeFilter/" + item.itemID), true, new ItemStack[]{new ItemStack(item)});
        unique = EmiUtil.RANDOM.nextInt();
        this.bag = item;
    }

    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 18);
        widgets.addTexture(EmiTexture.SHAPELESS, 97, 0);

        for(int i = 0; i < 9; ++i) {
            widgets.add(this.getInputWidget(i, i % 3 * 18, i / 3 * 18));
        }

        widgets.addTexture(BTWPlugin.SMALL_PLUS, 84, 23)
                .tooltip(List.of(new EmiSecondaryOutputComponent(this.secondaryOutputs)));
        widgets.add(this.getOutputWidget(92, 14).large(true).recipeContext(this));
    }

    public SlotWidget getInputWidget(int slot, int x, int y) {
        return new SlotWidget(slot == 0 ? getInputStack(bag) : EmiStack.EMPTY, x, y);
    }

    public SlotWidget getOutputWidget(int x, int y) {
        return new SlotWidget(this.output, x, y);
    }

    private EmiStack getInputStack(Item item) {
        ItemStack stack = new ItemStack(item);
        stack.stackTagCompound = new NBTTagCompound();
        stack.stackTagCompound.setBoolean("EmiEffect", true);
        return EmiStack.of(stack);
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }
}
