package com.jeffyjamzhd.jeffybackpacks.registry;

import com.jeffyjamzhd.jeffybackpacks.JeffyBackpacks;
import com.jeffyjamzhd.jeffybackpacks.item.ItemWithInventory;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class JBItems {
    public static Item test_item = new ItemWithInventory(31000, 4)
            .setCreativeTab(CreativeTabs.tabTools)
            .setUnlocalizedName("backpack");

    public static void register() {
        JeffyBackpacks.logInfo("Registering items...");
    }
}
