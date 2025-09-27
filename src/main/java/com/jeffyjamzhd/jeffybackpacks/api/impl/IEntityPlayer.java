package com.jeffyjamzhd.jeffybackpacks.api.impl;

import net.minecraft.src.InventoryPlayer;

public interface IEntityPlayer {
    /**
     * Updates the total count of items within backpacks held
     * by the player
     */
    default void jbp$updateBackpackItemCount(InventoryPlayer inventory) {}

    /**
     * Returns the amount of items stored within held backpacks
     */
    default int jbp$getBackpackItemCount() {
        return 0;
    }
}
