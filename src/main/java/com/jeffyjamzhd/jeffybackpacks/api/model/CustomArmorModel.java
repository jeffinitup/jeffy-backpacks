package com.jeffyjamzhd.jeffybackpacks.api.model;

import net.minecraft.src.*;

import java.util.Optional;

public class CustomArmorModel extends ModelBase {
    public boolean isSneaking = false;
    public float onGround = 0F;

    private ChestplateModel chestplate = null;

    public void setChestplateModel(ChestplateModel model) {
        chestplate = model;
    }

    public Optional<ChestplateModel> getChestplate() {
        return Optional.ofNullable(chestplate);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        getChestplate().ifPresent(model -> model.render(f5));
    }

    /**
     * Sets the model's various rotation angles. For bipeds, f and f1 are used for animating the movement of arms
     * and legs, where f represents the time(so that arms and legs swing back and forth) and f1 represents how
     * "far" arms and legs can swing at most.
     */
    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        Optional<ChestplateModel> chestplate = getChestplate();

        if (onGround > -9990F) {
            chestplate.ifPresent(model -> model.chest.rotateAngleY =
                    MathHelper.sin(MathHelper.sqrt_float(onGround) * (float) Math.PI * 2.0F) * 0.2F);
        }

        if (isSneaking) {
            chestplate.ifPresent(model -> model.chest.rotateAngleX = 0.5F);
        } else {
            chestplate.ifPresent(model -> model.chest.rotateAngleX = 0F);
        }
    }

    public record ChestplateModel(ModelRenderer chest, Optional<ModelRenderer> leftArm, Optional<ModelRenderer> rightArm) {
        public void render(float f5) {
            chest.render(f5);
            leftArm.ifPresent(model -> model.render(f5));
            rightArm.ifPresent(model -> model.render(f5));
        }

    }
}
