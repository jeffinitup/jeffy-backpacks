package com.jeffyjamzhd.jeffybackpacks.item;

import btw.item.items.BucketItemDrinkable;
import btw.item.util.ItemUtils;
import btw.util.sounds.BTWSoundManager;
import com.jeffyjamzhd.jeffybackpacks.JeffyBackpacks;
import com.jeffyjamzhd.jeffybackpacks.inventory.BackpackInventory;
import emi.shims.java.com.unascribed.retroemi.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.HashMap;

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
            ItemStack firstStack = inv.getFirstStack();
            if (canConsume(player, firstStack)) {
                if (world.isRemote) {
                    stack.setItemDamage(foodIcons.containsKey(inv.getFirstStack().itemID) ? inv.getFirstStack().itemID : 1);
                    player.playSound(BTWSoundManager.CHEST_OPEN.sound(),
                            0.7f + world.rand.nextFloat() * 0.1f, 1.4f + world.rand.nextFloat() * 0.25f);
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
                player.playSound(BTWSoundManager.CHEST_CLOSE.sound(),
                        0.7f + world.rand.nextFloat() * 0.1f, 1.4f + world.rand.nextFloat() * 0.25f);
            }

            // Run eat method
            ItemStack newStack = foodStack.getItem().onEaten(foodStack, world, player);
            if (foodStack.itemID != newStack.itemID)
                ItemUtils.givePlayerStackOrEject(player, newStack);

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
            player.playSound(BTWSoundManager.CHEST_CLOSE.sound(),
                    0.7f + world.rand.nextFloat() * 0.1f, 1.4f + world.rand.nextFloat() * 0.25f);
        }
    }

    @Override
    public int jl$getIDForEatingParticle(ItemStack stack) {
        BackpackInventory inv = new BackpackInventory(stack, inventorySize);
        return inv.getFirstStack().itemID;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        BackpackInventory inv = new BackpackInventory(stack, inventorySize);
        ItemStack stackInside = inv.getFirstStack();
        if (stackInside != null) {
            return isLiquid(stackInside.getItem(), stackInside.getItemDamage())
                    ? EnumAction.drink : EnumAction.eat;
        }
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
        return stack != null && isValidFood(stack.getItem(), stack.getItemDamage());
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

    //***       Unique methods        ***//

    private boolean isLiquid(Item item, int damage) {
        return item instanceof BucketItemDrinkable || (item instanceof ItemPotion && damage == 0);
    }

    private boolean isValidFood(Item item, int damage) {
        // Account for food
        if (item instanceof ItemFood)
            return true;
        // Account for drinks
        return isLiquid(item, damage);
    }

    private boolean canConsume(EntityPlayer player, ItemStack stack) {
        if (isLiquid(stack.getItem(), stack.getItemDamage()))
            return player.canDrink();
        return player.canEat(false);
    }

    //***       Clientside methods        ***//

    @Environment(EnvType.CLIENT)
    private HashMap<Integer, Icon> foodIcons;
    @Environment(EnvType.CLIENT)
    private Icon fullIcon;
    @Environment(EnvType.CLIENT)
    private Icon emptyIcon;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        foodIcons = new HashMap<>();
        for (Item item : Item.itemsList) {
            if (isValidFood(item, 0)) {
                // Parse icon string
                String name = item.iconString;
                if (name.startsWith("btw:")) {
                    name = name.substring(4);
                }

                try {
                    // Check if the icon exists, perhaps a bit inefficiently
                    ResourceLocation location = new ResourceLocation("jbp", "textures/items/lunchbox/lunchbox_" + name + ".png");
                    Minecraft.getMinecraft().getResourceManager().getResource(location);

                    // It will have thrown an exception at this point, so register icon
                    // since it exists
                    Icon icon = register.registerIcon("jbp:lunchbox/lunchbox_" + name);
                    foodIcons.put(item.itemID, icon);
                } catch (Exception e) {
                    // Make it known an icon doesn't exist for that food item
                    JeffyBackpacks.logInfo("Could not find custom lunchbox icon for " + name);
                }
            }
        }

        itemIcon = register.registerIcon(getIconString());
        fullIcon = register.registerIcon(getIconString() + "_full");
        emptyIcon = register.registerIcon(getIconString() + "_empty");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamage(int damage) {
        if (foodIcons.containsKey(damage)) {
            return foodIcons.get(damage);
        } else if (damage > 0) {
            return fullIcon;
        }

        return itemIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamageForRenderPass(int damage, int pass) {
        return getIconFromDamage(damage);
    }
}
