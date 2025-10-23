package com.jeffyjamzhd.jeffybackpacks.emi;

import com.google.common.collect.Lists;
import emi.dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.GeneratedSlotWidget;
import emi.dev.emi.emi.api.widget.SlotWidget;
import emi.shims.java.net.minecraft.item.DyeItem;
import emi.shims.java.net.minecraft.item.DyeableItem;
import emi.shims.java.net.minecraft.util.DyeColor;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EMIBackpackDyeRecipe extends EmiPatternCraftingRecipe {
    private static final List<DyeItem> DYES;
    private final Item bag;

    public EMIBackpackDyeRecipe(Item bag) {
        super(
                List.of(EmiIngredient.of(DYES.stream().map(EmiStack::of).toList()), EmiStack.of(bag)),
                EmiStack.of(bag),
                new ResourceLocation("jbp:bagDyeRecipe/" + bag.itemID),
                true
        );
        this.bag = bag;
    }

    public SlotWidget getInputWidget(int slot, int x, int y) {
        if (slot == 0) {
            return new SlotWidget(EmiStack.of(this.bag), x, y);
        } else {
            int s = slot - 1;
            return new GeneratedSlotWidget((r) -> {
                List<DyeItem> dyes = this.getDyes(r);
                return s < dyes.size() ? EmiStack.of(dyes.get(s)) : EmiStack.EMPTY;
            }, this.unique, x, y);
        }
    }

    public SlotWidget getOutputWidget(int x, int y) {
        return new GeneratedSlotWidget((r) ->
                EmiStack.of(DyeableItem.blendAndSetColor(new ItemStack(this.bag, 1, 1), this.getDyes(r))), this.unique, x, y);
    }

    private List<DyeItem> getDyes(Random random) {
        List<DyeItem> dyes = new ArrayList<>();
        int amount = 1 + random.nextInt(8);

        for(int i = 0; i < amount; ++i) {
            dyes.add(DYES.get(random.nextInt(DYES.size())));
        }

        return dyes;
    }

    static {
        DYES = Stream.of(DyeColor.values())
                .map(DyeItem::byColor)
                .collect(Collectors.toList());
    }
}
