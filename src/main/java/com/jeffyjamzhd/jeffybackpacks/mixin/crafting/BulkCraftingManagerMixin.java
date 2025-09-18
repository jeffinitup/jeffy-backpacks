package com.jeffyjamzhd.jeffybackpacks.mixin.crafting;

import btw.crafting.manager.BulkCraftingManager;
import btw.crafting.recipe.types.BulkRecipe;
import btw.inventory.util.InventoryUtils;
import btw.item.tag.TagOrStack;
import com.jeffyjamzhd.jeffybackpacks.inventory.BackpackInventory;
import com.jeffyjamzhd.jeffybackpacks.item.ItemWithInventory;
import com.llamalad7.mixinextras.sugar.Local;
import emi.shims.java.com.unascribed.retroemi.Pair;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

/**
 * Adding checks for backpack contents
 */
@Mixin(BulkCraftingManager.class)
public class BulkCraftingManagerMixin {
    @Inject(method = "consumeIngredientsAndReturnResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lbtw/crafting/recipe/types/BulkRecipe;consumeInventoryIngredients(Lnet/minecraft/src/IInventory;)Z"
            ),
            cancellable = true
    )
    private void addBackpackContents(
            IInventory inventory,
            CallbackInfoReturnable<List<ItemStack>> cir,
            @Local(ordinal = 0) BulkRecipe recipe) {
        // Get item id of first item
        TagOrStack tagOrStack = recipe.getFirstIngredient();
        Map<Integer, Pair<Integer, Integer>> ids = tagOrStack.getItemIds();
        int id = (int) ids.keySet().toArray()[0];

        if (Item.itemsList[id] instanceof ItemWithInventory itemWInv) {
            // Get stack in slot
            int slot = InventoryUtils.getFirstOccupiedStackOfItem(inventory, id);
            ItemStack stack = inventory.getStackInSlot(slot);

            // Consume ingredient
            recipe.consumeInventoryIngredients(inventory);

            // Get inventory contents and merge with recipe output
            BackpackInventory stackInv = new BackpackInventory(stack, itemWInv.inventorySize);
            List<ItemStack> stackInvContents = stackInv.inventory.stream()
                    .filter(Objects::nonNull).toList();
            List<ItemStack> recipeOutput = new ArrayList<>(List.copyOf(recipe.getCraftingOutputList()));
            recipeOutput.addAll(stackInvContents);
            cir.setReturnValue(recipeOutput);
        }
    }
}
