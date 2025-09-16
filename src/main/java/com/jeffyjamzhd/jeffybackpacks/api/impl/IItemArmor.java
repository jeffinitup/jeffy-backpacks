package com.jeffyjamzhd.jeffybackpacks.api.impl;

import net.minecraft.src.ItemArmor;

public interface IItemArmor {
    /**
     * {@code true} if this can be worn as an armor piece
     */
    default boolean jbp$canBeWorn() {
        return true;
    }

    /**
     * Sets if this armor piece can be worn.
     */
    default ItemArmor jbp$cantBeWorn() {
        return null;
    }
}
