package com.jeffyjamzhd.jeffybackpacks.api;

import com.jeffyjamzhd.jeffybackpacks.render.ModelBackpackBase;
import net.minecraft.src.ResourceLocation;

public interface IArmorCustomModel {
    /**
     * {@code true} if this item has a custom armor model
     */
    boolean hasCustomModel();

    /**
     * Returns the armor's custom model.
     */
    ModelBackpackBase getCustomModel();


}
