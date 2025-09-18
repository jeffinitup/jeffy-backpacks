package com.jeffyjamzhd.jeffybackpacks.api.impl;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public interface IItem {
    /**
     * Sets the mod namespace in item
     */
    default Item jbp$setModNamespace(String namespace) {
        return null;
    }

    /**
     * Called when this item is destroyed (dropped item entity is destroyed, burned, etc.)
     * @param stack {@link ItemStack} that was destroyed
     */
    @SuppressWarnings(value = "unused")
    default void jbp$onItemDestroyed(ItemStack stack, World world, double x, double y, double z) {
    }

    /**
     * Returns item ID to pull icon from for eating particles
     * @param stack {@link ItemStack} that is being eaten
     */
    default int jbp$getIDForEatingParticle(ItemStack stack) {
        return stack.itemID;
    }
}
