package com.jeffyjamzhd.jeffybackpacks.item;

import btw.item.items.FoodItem;
import btw.util.sounds.BTWSoundManager;
import com.jeffyjamzhd.jeffybackpacks.inventory.BackpackInventory;
import emi.shims.java.com.unascribed.retroemi.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class ItemLunchbox extends ItemWithInventory {
    /**
     * Constructor for {@code ItemWithInventory}
     * @param id   Item ID to occupy
     */
    public ItemLunchbox(int id) {
        super(id, new Pair<>(2, 2));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        BackpackInventory inv = new BackpackInventory(stack, inventorySize);
        if (inv.getSizeInventory() > 0) {
            if (player.canEat(false)) {
                if (world.isRemote) {
                    stack.setItemDamage(1);
                    Minecraft.getMinecraft().sndManager.playSoundFX(BTWSoundManager.CHEST_OPEN.sound(), 0.7F, 1.5F);
                }
                player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
            } else {
                player.onCantConsume();
            }
        }
        return stack;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        BackpackInventory inv = new BackpackInventory(stack, inventorySize);

        // Get food from inventory
        ItemStack foodStack = inv.getFirstStack();
        if (foodStack != null) {
            // Run food eating method
            if (world.isRemote) {
                stack.setItemDamage(0);
                Minecraft.getMinecraft().sndManager.playSoundFX(BTWSoundManager.CHEST_CLOSE.sound(), 0.7F, 1.5F);
            }
            foodStack.getItem().onEaten(foodStack, world, player);
            inv.writeToNBT(stack.stackTagCompound);
        }

        // Return self
        return stack;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int duration) {
        super.onPlayerStoppedUsing(stack, world, player, duration);
        if (world.isRemote) {
            stack.setItemDamage(0);
            Minecraft.getMinecraft().sndManager.playSoundFX(BTWSoundManager.CHEST_CLOSE.sound(), 0.7F, 1.5F);
        }
    }

    @Override
    public int jbp$getIDForEatingParticle(ItemStack stack) {
        BackpackInventory inv = new BackpackInventory(stack, inventorySize);
        return inv.getFirstStack().itemID;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.eat;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 32;
    }

    @Override
    public boolean isMultiUsePerClick() {
        return false;
    }

    @Override
    public EnumArmorMaterial getArmorMaterial() {
        // Override this to prevent dye
        return EnumArmorMaterial.CHAIN;
    }

    @Override
    public boolean isItemValidForInsertion(ItemStack stack) {
        return stack != null && (stack.getItem() instanceof ItemFood || stack.getItem() instanceof FoodItem);
    }

    @Override
    public void playInsertSFX(World world, EntityPlayer player) {
        if (world.isRemote) {
            world.playSound(
                    player.posX, player.posY, player.posZ, BTWSoundManager.ITEM_FRAME_ITEM_ADD.sound(),
                    0.9f + world.rand.nextFloat() * 0.1f, 1.4f + world.rand.nextFloat() * 0.25f);
        }

        if (!world.isRemote) {
            world.playSoundToNearExcept(
                    player, BTWSoundManager.ITEM_FRAME_ITEM_ADD.sound(),
                    0.9f + world.rand.nextFloat() * 0.1f, 1.4f + world.rand.nextFloat() * 0.25f);
        }
    }

    @Override
    public void playExtractSFX(World world, EntityPlayer player) {
        if (world.isRemote) {
            world.playSound(
                    player.posX, player.posY, player.posZ, BTWSoundManager.ITEM_FRAME_ITEM_REMOVE.sound(),
                    0.9f + world.rand.nextFloat() * 0.1f, 1.4f + world.rand.nextFloat() * 0.25f);
        }

        if (!world.isRemote) {
            world.playSoundToNearExcept(
                    player, BTWSoundManager.ITEM_FRAME_ITEM_REMOVE.sound(),
                    0.9f + world.rand.nextFloat() * 0.1f, 1.4f + world.rand.nextFloat() * 0.25f);
        }
    }

    @Override
    public void playFullSFX(World world, EntityPlayer player) {
        if (world.isRemote) {
            world.playSound(
                    player.posX, player.posY, player.posZ, BTWSoundManager.ITEM_FRAME_ITEM_ROTATE.sound(),
                    0.9f + world.rand.nextFloat() * 0.1f, 0.6f + world.rand.nextFloat() * 0.25f);
        }

        if (!world.isRemote) {
            world.playSoundToNearExcept(
                    player, BTWSoundManager.ITEM_FRAME_ITEM_ROTATE.sound(),
                    0.9f + world.rand.nextFloat() * 0.1f, 0.6f + world.rand.nextFloat() * 0.25f);
        }
    }

    //***       Clientside methods        ***//

    @Environment(EnvType.CLIENT)
    private Icon fullIcon;
    @Environment(EnvType.CLIENT)
    private Icon emptyIcon;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        itemIcon = register.registerIcon(getIconString());
        fullIcon = register.registerIcon(getIconString() + "_full");
        emptyIcon = register.registerIcon(getIconString() + "_empty");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamage(int damage) {
        return switch (damage) {
            case 0 -> itemIcon;
            case 1 -> fullIcon;
            default -> emptyIcon;
        };
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamageForRenderPass(int damage, int pass) {
        return getIconFromDamage(damage);
    }
}
