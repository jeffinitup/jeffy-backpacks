package com.jeffyjamzhd.jeffybackpacks.api;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

/**
 * An item that can be interacted by unconventional means,
 * such as right-clicking and scrolling.
 */
public interface IItemExtendedInteraction {
    /**
     * {@link ItemStack} right-clicked. {@code true} if the interaction is successful
     */
    ItemStack itemRightClicked(ItemStack item, EntityPlayer player, World world);

    /**
     * {@link ItemStack} right-clicked with an {@link ItemStack} in the cursor slot.
     * @return Modified {@code mouseStack}
     */
    ItemStack itemRightClickedWithStack(ItemStack item, ItemStack mouseStack, EntityPlayer player, World world);

    /**
     * {@link ItemStack} hovered over is being scrolled
     */
    boolean itemScrolled(ItemStack item, EntityPlayer player, World world, int direction);
}
