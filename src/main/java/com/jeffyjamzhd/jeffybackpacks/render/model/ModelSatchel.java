// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.5.2
// Paste this class into your mod and call render() in your Entity Render class
// Note: You may need to adjust the y values of the 'setRotationPoint's

package com.jeffyjamzhd.jeffybackpacks.render.model;

import com.jeffyjamzhd.jeffybackpacks.render.ModelBackpackBase;
import net.minecraft.src.ModelRenderer;
import net.minecraft.src.ResourceLocation;

public class ModelSatchel extends ModelBackpackBase {
	private final ResourceLocation tex = new ResourceLocation("jbp", "textures/models/satchel_overlay.png");
	private final ResourceLocation texGrayscale = new ResourceLocation("jbp", "textures/models/satchel_overlay_grayscale.png");

	public ModelSatchel() {
		textureWidth = 64;
		textureHeight = 32;

		this.model = new ModelRenderer(this);
		this.model.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.model.setTextureOffset(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.5F);
		this.model.setTextureOffset(0, 25).addBox(-3.0F, 7.5F, -5.0F, 6, 4, 3, 0.0F);
		this.model.setTextureOffset(5, 29).addBox(-0.5F, 8.25F, -5.5F, 1, 2, 1, 0.0F);
	}

	@Override
	public ResourceLocation getTexture(boolean hasColor) {
		return hasColor ? texGrayscale : tex;
	}
}