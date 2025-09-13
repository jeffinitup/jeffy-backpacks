package com.jeffyjamzhd.jeffybackpacks.inventory;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;

public class BackpackContainer extends Container {
    public BackpackInventory inventory;
    public final int inventorySize;

    public BackpackContainer(BackpackInventory inventory) {
        this.inventory = inventory;
        this.inventorySize = inventory.size;
    }

    @Override
    public boolean canInteractWith(EntityPlayer var1) {
        return true;
    }

    //***       Class specific methods        ***//

    public void writeStackToNBT(ItemStack stack) {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        inventory.writeToNBT(stack.getTagCompound());
    }
}
