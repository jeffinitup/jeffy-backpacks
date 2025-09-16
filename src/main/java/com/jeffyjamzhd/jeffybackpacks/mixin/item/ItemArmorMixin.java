package com.jeffyjamzhd.jeffybackpacks.mixin.item;

import com.jeffyjamzhd.jeffybackpacks.api.impl.IItemArmor;
import net.minecraft.src.ItemArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemArmor.class)
public class ItemArmorMixin implements IItemArmor {
    @Unique
    public boolean jbp$canBeWorn = true;

    @Override
    public boolean jbp$canBeWorn() {
        return jbp$canBeWorn;
    }

    @Override
    public ItemArmor jbp$cantBeWorn() {
        this.jbp$canBeWorn = false;
        return (ItemArmor) (Object) this;
    }
}
