package com.jeffyjamzhd.jeffybackpacks.registry;

import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import btw.item.tag.BTWTags;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class JBRecipes {
    /**
     * Registers addon recipes
     */
    public static void register() {
        RecipeManager.addRecipe(new ItemStack(JBItems.bundle), new Object[]{
                "SS",
                "##",
                "##",
                Character.valueOf('S'), Item.silk,
                Character.valueOf('#'), BTWTags.knitWools
        });
        RecipeManager.addRecipe(new ItemStack(JBItems.satchel), new Object[]{
                "SSS",
                "FFF",
                "FFF",
                Character.valueOf('S'), BTWItems.hempFibers,
                Character.valueOf('F'), BTWItems.fabric
        });
        RecipeManager.addRecipe(new ItemStack(JBItems.backpack), new Object[]{
                "LLL",
                "BLB",
                "LLL",
                Character.valueOf('L'), BTWTags.tannedLeathers,
                Character.valueOf('B'), BTWItems.belt
        });
    }
}
