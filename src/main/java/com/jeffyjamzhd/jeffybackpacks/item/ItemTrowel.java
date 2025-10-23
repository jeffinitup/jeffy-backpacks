package com.jeffyjamzhd.jeffybackpacks.item;

import emi.shims.java.com.unascribed.retroemi.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;

public class ItemTrowel extends ItemWithInventory {
    /**
     * Constructor for {@code ItemWithInventory}
     *
     * @param id   Item ID to occupy
     */
    public ItemTrowel(int id) {
        super(id, new Pair<>(2, 2));
        jl$cantBeWorn();
        setFull3D();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.itemIcon = register.registerIcon(this.getIconString());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamage(int damage) {
        return this.itemIcon;
    }
}
