package com.jeffyjamzhd.jeffybackpacks.item;

import com.jeffyjamzhd.jeffybackpacks.inventory.BackpackInventory;
import com.jeffyjamzhd.jeffybackpacks.inventory.FilterInventory;
import com.jeffyjamzhd.jeffybackpacks.registry.JBSounds;
import emi.shims.java.com.unascribed.retroemi.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class ItemFilter extends ItemWithInventory {
    /**
     * Constructor for {@code ItemFilter}
     * @param id  Item ID to occupy
     */
    public ItemFilter(int id) {
        super(id, new Pair<>(3, 3));
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        if (player.timesCraftedThisTick == 0 && world.isRemote) {
            player.playSound("mob.ghast.moan", 0.3F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
        }

        super.onCreated(stack, world, player);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        // Do nothing
        return stack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List stringList, boolean shift) {
        // Read filter mode
        FilterInventory inv = (FilterInventory) createInventory(stack);
        String filterString = I18n.getStringParams("tooltip.filter",
                I18n.getString(inv.isBlacklist() ? "tooltip.filter.blacklist" : "tooltip.filter.whitelist"));
        stringList.add(addStringFormatting(filterString));

        super.addInformation(stack, player, stringList, shift);
    }

    @Override
    public void jl$onItemDestroyed(ItemStack stack, World world, double x, double y, double z) {
        // Do nothing
    }

    @Override
    public boolean jl$canBeWorn() {
        return false;
    }

    //***       IItemExtendedInteraction        ***//

    @Override
    public void itemRightClicked(ItemStack item, EntityPlayer player,
                                 World world, boolean holdingShift) {
        FilterInventory inv = (FilterInventory) createInventory(item);
        if (!holdingShift) {
            inv.decrStackSize(inv.currentSlotID, 1);
        } else {
            inv.invertMode();
        }

        item.stackTagCompound = inv.writeToNBT(item.stackTagCompound);
        playSFX(world, player);
    }

    @Override
    public void itemRightClickedWithStack(ItemStack itemStack, ItemStack mouseStack,
                                          EntityPlayer player, World world, boolean holdingShift) {
        if (isItemValidForInsertion(mouseStack)) {
            FilterInventory inv = (FilterInventory) createInventory(itemStack);
            ItemStack result = inv.putStackSmart(mouseStack);
            itemStack.stackTagCompound = inv.writeToNBT(itemStack.stackTagCompound);

            if (result != null)
                playSFX(world, player);
        }
    }

    @Override
    public ItemStack itemRightClickAsMouseStack(ItemStack item, EntityPlayer player,
                                                World world, boolean holdingShift) {
        // Do nothing
        return null;
    }

    @Override
    public ItemStack itemRightClickedWithStackAsMouseStack(ItemStack slotStack, ItemStack mouseStack,
                                                           EntityPlayer player, World world, boolean holdingShift) {
        return slotStack;
    }

    //***       ItemWithInventory        ***//

    @Override
    public int getItemCountInStack(ItemStack stack) {
        return 0;
    }

    @Override
    public BackpackInventory createInventory(ItemStack stack) {
        return new FilterInventory(stack);
    }

    @Override
    public void playFullSFX(World world, EntityPlayer player) {
        playSFX(world, player);
    }

    @Override
    public void playExtractSFX(World world, EntityPlayer player) {
        playSFX(world, player);
    }

    @Override
    public void playInsertSFX(World world, EntityPlayer player) {
        playSFX(world, player);
    }

    private void playSFX(World world, EntityPlayer player) {
        if (world.isRemote) {
            world.playSound(
                    player.posX, player.posY, player.posZ, JBSounds.FILTER_WRITE.sound(),
                    0.9f + world.rand.nextFloat() * 0.1f, 1.0f + world.rand.nextFloat() * 0.25f);
        }

        if (!world.isRemote) {
            world.playSoundToNearExcept(
                    player, JBSounds.FILTER_WRITE.sound(),
                    0.9f + world.rand.nextFloat() * 0.1f, 1.0f + world.rand.nextFloat() * 0.25f);
        }
    }

    //***       Client methods        ***//

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        itemIcon = register.registerIcon(getIconString());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamage(int damage) {
        return itemIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamageForRenderPass(int damage, int pass) {
        return getIconFromDamage(damage);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
