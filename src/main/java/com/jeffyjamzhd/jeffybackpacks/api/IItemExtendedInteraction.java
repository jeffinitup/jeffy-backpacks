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
    void beforeExtendedInteraction(ItemStack item, int slotID, boolean holdingShift);

    /**
     * This item right-clicked.
     * @param holdingShift {@code true} if shift is pressed
     */
    void itemRightClicked(
            ItemStack stack,
            EntityPlayer player,
            World world,
            boolean holdingShift
    );

    /**
     * This item right-clicked while grabbed by mouse.
     * Assumes there is no stack present at slot.
     * @param holdingShift {@code true} if shift is pressed
     * @return Stack generated from this action (if applicable)
     */
    ItemStack itemRightClickAsMouseStack(
            ItemStack stack,
            EntityPlayer player,
            World world,
            boolean holdingShift
    );

    /**
     * This item right-clicked with an {@link ItemStack} in the cursor slot.
     * @param holdingShift {@code true} if shift is pressed
     */
    void itemRightClickedWithStack(
            ItemStack stack,
            ItemStack cursorStack,
            EntityPlayer player,
            World world,
            boolean holdingShift
    );

    /**
     * {@link ItemStack} right-clicked with this item in the cursor slot.
     * @param holdingShift {@code true} if shift is pressed
     * @return Modified {@code stack}
     */
    ItemStack itemRightClickedWithStackAsMouseStack(
            ItemStack stack,
            ItemStack cursorStack,
            EntityPlayer player,
            World world,
            boolean holdingShift
    );

    /**
     * {@link ItemStack} hovered over is being scrolled
     *
     * @param direction    Scroll direction
     * @param holdingShift {@code true} if shift is pressed
     */
    void itemScrolled(
            ItemStack item,
            EntityPlayer player,
            World world,
            int direction,
            boolean holdingShift
    );
}
