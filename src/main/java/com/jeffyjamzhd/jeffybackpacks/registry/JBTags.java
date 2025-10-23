package com.jeffyjamzhd.jeffybackpacks.registry;

import btw.item.tag.Tag;
import net.minecraft.src.ResourceLocation;

public class JBTags {
    public static final Tag TAG_INVENTORY_ITEMS =
            Tag.of(loc("inventory_items"))
                    .add(JBItems.bundle, JBItems.satchel, JBItems.backpack, JBItems.trowel, JBItems.lunchbox);
    public static final Tag TAG_COLORED_BAGS =
            Tag.of(loc("colored_bags"))
                    .add(JBItems.bundle, JBItems.satchel, JBItems.backpack);
    public static final Tag TAG_BACKPACKS =
            Tag.of(loc("backpacks"))
                    .add(JBItems.satchel, JBItems.backpack);

    private static ResourceLocation loc(String id) {
        return new ResourceLocation("jbp", id);
    }

    public static void register() {}
}
