package com.jeffyjamzhd.jeffybackpacks.registry;

import btw.block.BTWBlocks;
import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import btw.item.tag.BTWTags;
import btw.item.tag.TagInstance;
import btw.item.tag.TagOrStack;
import com.jeffyjamzhd.jeffybackpacks.api.recipe.RecipeAddFilter;
import com.jeffyjamzhd.jeffybackpacks.api.recipe.RecipeBundle;
import com.jeffyjamzhd.jeffybackpacks.api.recipe.RecipeRemoveFilter;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ShapedRecipes;

public class JBRecipes {
    public static ShapedRecipes bundleRecipe;
    public static final RecipeAddFilter RECIPE_TYPE_ADD_FILTER;
    public static final RecipeRemoveFilter RECIPE_TYPE_REMOVE_FILTER;

    /**
     * Registers addon recipes
     */
    public static void register() {
        registerCrafts();
        registerStokedCauldron();
    }

    private static void registerCrafts() {
        // Add crafting recipes
        bundleRecipe = RecipeManager.addShapedRecipeWithCustomClass(
                RecipeBundle.class,
                new ItemStack(JBItems.bundle),
                new Object[]{
                        "SS",
                        "##",
                        "##",
                        'S', Item.silk,
                        '#', new TagInstance(BTWTags.knitWools, 1)
                });
        RecipeManager.addRecipe(
                new ItemStack(JBItems.satchel),
                new Object[]{
                        "SSS",
                        "FFF",
                        "FFF",
                        'S', BTWItems.hempFibers,
                        'F', BTWItems.fabric
                });
        RecipeManager.addRecipe(
                new ItemStack(JBItems.backpack),
                new Object[]{
                        "LLL",
                        "BLB",
                        "LLL",
                        'L', BTWTags.tannedLeathers,
                        'B', BTWItems.belt
                });
        RecipeManager.addRecipe(
                new ItemStack(JBItems.lunchbox),
                new Object[]{
                        "ICI",
                        "III",
                        'I', Item.ingotIron,
                        'C', BTWBlocks.chest
                }
        );
        RecipeManager.addShapelessRecipe(
                new ItemStack(JBItems.filter),
                new Object[]{
                        new ItemStack(Item.nameTag),
                        new ItemStack(BTWItems.soulUrn),
                }
        );

        // Add custom shapeless recipe types
        CraftingManager.getInstance().getRecipes().add(RECIPE_TYPE_ADD_FILTER);
        CraftingManager.getInstance().getRecipes().add(RECIPE_TYPE_REMOVE_FILTER);
    }

    private static void registerStokedCauldron() {
        RecipeManager.addStokedCauldronRecipe(
                new ItemStack(BTWItems.glue, 4),
                new TagOrStack[]{
                        new ItemStack(JBItems.backpack)
                });
        RecipeManager.addStokedCrucibleRecipe(
                new ItemStack(BTWItems.ironNugget, 30),
                new TagOrStack[]{
                        new ItemStack(JBItems.lunchbox)
                });
    }

    static {
        RECIPE_TYPE_ADD_FILTER = new RecipeAddFilter();
        RECIPE_TYPE_REMOVE_FILTER = new RecipeRemoveFilter();
    }
}
