package com.jeffyjamzhd.jeffybackpacks.api;

import net.minecraft.src.IInventory;
import net.minecraft.src.NBTTagCompound;

public interface IItemStackInventory extends IInventory {
    /**
     * Read from NBT data
     * @param compound NBT data from associated {@link net.minecraft.src.ItemStack}
     */
    void readFromNBT(NBTTagCompound compound);

    /**
     * Read from NBT data
     * @param compound NBT data from associated {@link net.minecraft.src.ItemStack}
     */
    void writeToNBT(NBTTagCompound compound);

}
