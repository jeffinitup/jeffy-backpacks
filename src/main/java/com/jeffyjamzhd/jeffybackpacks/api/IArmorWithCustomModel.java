package com.jeffyjamzhd.jeffybackpacks.api;

import com.jeffyjamzhd.jeffybackpacks.api.model.CustomArmorModel;
import net.minecraft.src.ResourceLocation;

public interface IArmorWithCustomModel {
    /**
     * Returns the armor's custom model.
     */
    CustomArmorModel getCustomModel();

    /**
     * Returns the armor's custom model texture path.
     */
    ResourceLocation getCustomModelTexture();
}
