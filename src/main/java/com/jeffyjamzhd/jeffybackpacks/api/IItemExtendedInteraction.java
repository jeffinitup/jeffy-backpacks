package com.jeffyjamzhd.jeffybackpacks.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

/**
 * An item that can be interacted by unconventional means,
 * such as right-clicking and scrolling.
 */
public interface IItemExtendedInteraction {
    /**
     * Called before extended interaction hooks on client, usually to
     * send sync/validation packets to server
     */
    @Environment(EnvType.CLIENT)
    void beforeExtendedInteraction(ItemStack item, int slotID);

    /**
     * {@link ItemStack} right-clicked. {@code true} if the interaction is successful
     * @param holdingShift {@code true} if shift is pressed
     */
    ItemStack itemRightClicked(
            ItemStack stack,
            EntityPlayer player,
            World world,
            boolean holdingShift
    );

    /**
     * {@link ItemStack} right-clicked with an {@link ItemStack} in the cursor slot.
     * @param holdingShift {@code true} if shift is pressed
     * @return Modified {@code mouseStack}
     */
    ItemStack itemRightClickedWithStack(
            ItemStack stack,
            ItemStack cursorStack,
            EntityPlayer player,
            World world,
            boolean holdingShift
    );

    /**
     * {@link ItemStack} hovered over is being scrolled
     * @param direction Scroll direction
     * @param holdingShift {@code true} if shift is pressed
     */
    boolean itemScrolled(
            ItemStack item,
            EntityPlayer player,
            World world,
            int direction,
            boolean holdingShift
    );
}
