package com.jeffyjamzhd.jeffybackpacks.api.impl;

import net.minecraft.src.ItemStack;

public interface IInventoryPlayer {
    /**
     * Hook for specifically adding items from the ground
     * to the player's inventory, for backpack filters to pick up on
     */
    default boolean jbp$addStackFromWorld(ItemStack stack) {
        return false;
    }
}
