package com.jeffyjamzhd.jeffybackpacks.registry;

import com.jeffyjamzhd.jeffybackpacks.JeffyBackpacks;
import com.jeffyjamzhd.jeffybackpacks.item.ItemLunchbox;
import com.jeffyjamzhd.jeffybackpacks.item.ItemWithInventory;
import emi.shims.java.com.unascribed.retroemi.Pair;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class JBItems {
    public static Item bundle = new ItemWithInventory(31000, new Pair<>(2, 2))
            .hasSecondRenderPass()
            .jbp$cantBeWorn()
            .setCreativeTab(CreativeTabs.tabTools)
            .setUnlocalizedName("bundle")
            .setTextureName("jbp:bundle")
            .jbp$setModNamespace("jbp");
    public static Item satchel = new ItemWithInventory(31001, new Pair<>(3, 3))
            .setCreativeTab(CreativeTabs.tabTools)
            .setUnlocalizedName("satchel")
            .setTextureName("jbp:satchel")
            .jbp$setModNamespace("jbp");
    public static Item backpack = new ItemWithInventory(31002, new Pair<>(6, 3))
            .setCreativeTab(CreativeTabs.tabTools)
            .setUnlocalizedName("backpack")
            .setTextureName("jbp:backpack")
            .jbp$setModNamespace("jbp");
    public static Item lunchbox = new ItemLunchbox(31003)
            .jbp$cantBeWorn()
            .setCreativeTab(CreativeTabs.tabTools)
            .setUnlocalizedName("lunchbox")
            .setTextureName("jbp:lunchbox")
            .jbp$setModNamespace("jbp");

    public static void register() {
        JeffyBackpacks.logInfo("Registering items...");
    }
}
