package com.jeffyjamzhd.jeffybackpacks.item;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.item.util.ItemUtils;
import com.jeffyjamzhd.jeffybackpacks.JeffyBackpacks;
import com.jeffyjamzhd.jeffybackpacks.api.IItemExtendedInteraction;
import com.jeffyjamzhd.jeffybackpacks.inventory.BackpackContainer;
import com.jeffyjamzhd.jeffybackpacks.inventory.BackpackInventory;
import net.minecraft.src.*;

/**
 * An item that contains a certain amount of {@link ItemStack}.
 */
public class ItemWithInventory extends Item
        implements IItemExtendedInteraction {

    private final int inventorySize;

    public ItemWithInventory(int id, int invSize) {
        super(id);

        this.setMaxStackSize(1);

        this.inventorySize = invSize;
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);
    }

    //***       IItemExtendedInteraction        ***//

    @Override
    public ItemStack itemRightClicked(ItemStack item, EntityPlayer player, World world) {
        if (!world.isRemote) {
            JeffyBackpacks.logInfo("Item right clicked!");

            BackpackInventory inv = createInventory(item);
            ItemStack invStack = inv.getFirstStack();

            if (invStack != null)
                player.inventory.setItemStack(invStack);

            world.playSoundEffect(
                    player.posX, player.posY, player.posZ, "step.gravel",
                    0.1f + world.rand.nextFloat() * 0.1f, 1.0f + world.rand.nextFloat() * 0.25f);
        }
        return item;
    }

    @Override
    public ItemStack itemRightClickedWithStack(ItemStack item, ItemStack mouseStack, EntityPlayer player, World world) {
        if (!world.isRemote) {
            JeffyBackpacks.logInfo("Item right clicked with stack!");

            // Attempt to merge with inventory
            BackpackInventory inv = createInventory(item);
            ItemStack result = inv.putStackSmart(mouseStack);

            // Play sound
            world.playAuxSFX(BTWEffectManager.BLOCK_PLACE_EFFECT_ID,
                    (int) player.posX, (int) player.posY, (int) player.posZ, BTWBlocks.hamper.blockID);
            return result;
        }
        return mouseStack;
    }

    @Override
    public boolean itemScrolled(ItemStack item, EntityPlayer player, World world, int direction) {
        return false;
    }

    private BackpackInventory createInventory(ItemStack stack) {
        return new BackpackInventory(stack, 4);
    }
}
