package com.jeffyjamzhd.jeffybackpacks.emi;

import btw.item.tag.Tag;
import com.jeffyjamzhd.jeffybackpacks.api.recipe.RecipeBundle;
import com.jeffyjamzhd.jeffybackpacks.registry.JBItems;
import com.jeffyjamzhd.jeffybackpacks.registry.JBRecipes;
import com.jeffyjamzhd.jeffybackpacks.registry.JBTags;
import emi.dev.emi.emi.api.EmiPlugin;
import emi.dev.emi.emi.api.EmiRegistry;
import emi.dev.emi.emi.api.recipe.EmiInfoRecipe;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.shims.java.net.minecraft.text.Text;
import net.minecraft.src.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class JeffyBackpacksEMI implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        // Bundle colors recipe
        registry.addRecipe(new EMIBundleRecipe((RecipeBundle) JBRecipes.bundleRecipe));

        // Addon info recipes
        registry.addRecipe(new EmiInfoRecipe(
                parseTag(JBTags.TAG_INVENTORY_ITEMS),
                List.of(
                        Text.translatable("jbp.inventory_items.info.1"),
                        Text.literal(""),
                        Text.translatable("jbp.inventory_items.info.2")),
                null
        ));
        registry.addRecipe(new EmiInfoRecipe(
                parseTag(JBTags.TAG_BACKPACKS),
                List.of(Text.translatable("jbp.backpacks.info")),
                null
        ));
        registry.addRecipe(new EmiInfoRecipe(
                List.of(EmiStack.of(JBItems.lunchbox), EmiStack.of(new ItemStack(JBItems.lunchbox, 1, 1))),
                List.of(Text.translatable("jbp.lunchbox.info")),
                null
        ));
        registry.addRecipe(new EmiInfoRecipe(
                List.of(EmiStack.of(JBItems.bundle)),
                List.of(Text.translatable("jbp.bundle.info")),
                null
        ));
    }

    private List<EmiIngredient> parseTag(Tag tag) {
        return tag.getItems()
                .stream()
                .map(EmiStack::of)
                .collect(Collectors.toUnmodifiableList());
    }
}
