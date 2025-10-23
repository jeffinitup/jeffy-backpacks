package com.jeffyjamzhd.jeffybackpacks.render;

import net.minecraft.src.*;

public class ModelBackpackBase extends ModelBase {
    public boolean isSneaking = false;

    public ModelRenderer model;

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        model.render(f5);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, f and f1 are used for animating the movement of arms
     * and legs, where f represents the time(so that arms and legs swing back and forth) and f1 represents how
     * "far" arms and legs can swing at most.
     */
    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        if (onGround > -9990F) {
            model.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(onGround) * (float) Math.PI * 2.0F) * 0.2F;
        }

        if (isSneaking) {
            model.rotateAngleX = 0.5F;
        } else {
            model.rotateAngleX = 0F;
        }
    }

    /**
     * Returns the texture of this backpack model
     */
    public ResourceLocation getTexture(boolean hasColor) {
        return new ResourceLocation("");
    }
}
