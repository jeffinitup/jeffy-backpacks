// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.5.2
// Paste this class into your mod and call render() in your Entity Render class
// Note: You may need to adjust the y values of the 'setRotationPoint's

package com.jeffyjamzhd.jeffybackpacks.render.model;


import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

public class ModelSatchel extends ModelBase {
	public boolean isSneaking = false;
	public float onGround = 0F;

	public final ModelRenderer satchel;
	public final ModelRenderer satchel_strand;

	public ModelSatchel(ModelBase model) {
		textureWidth = 64;
		textureHeight = 32;

		satchel = new ModelRenderer(this);
		satchel.setRotationPoint(0F, 0F, 0F);
		this.satchel.setTextureOffset(0, 0).addBox(-4.0F, -0.5F, -2.0F, 8, 12, 4, .525F);
		this.satchel.setTextureOffset(0, 25).addBox(-3.0F, 7.5F, -5.0F, 6, 4, 3, 0F);

		satchel_strand = new ModelRenderer(this);
		satchel_strand.setRotationPoint(0.0F, 7.5F, 0.0F);
		satchel.addChild(satchel_strand);
		setRotation(satchel_strand, 0.7854F, 0.0F, 0.0F);
		this.satchel_strand.setTextureOffset(-2, 23).addBox(-2.0F, -2.5F, -6.5F, 4, 0, 2, 0.0F);
	}

	/**
	* Sets the models various rotation angles then renders the model.
	*/
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		satchel.render(f5);
	}

	/**
	* Sets the model's various rotation angles. For bipeds, f and f1 are used for animating the movement of arms
	* and legs, where f represents the time(so that arms and legs swing back and forth) and f1 represents how
	* "far" arms and legs can swing at most.
	*/
	@Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		if (onGround > -9990F) {
			satchel.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(onGround) * (float) Math.PI * 2.0F) * 0.2F;
		}


		if (isSneaking) {
			satchel.rotateAngleX = 0.5F;
		} else {
			satchel.rotateAngleX = 0F;
		}
    }
	
	/**
	*	Sets the rotation of a ModelRenderer. Only called if the ModelRenderer has a rotation
	*/
    public void setRotation(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}