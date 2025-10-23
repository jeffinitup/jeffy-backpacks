package com.jeffyjamzhd.jeffybackpacks.mixin.render;

import com.jeffyjamzhd.jeffybackpacks.api.IArmorCustomModel;
import com.jeffyjamzhd.jeffybackpacks.registry.JBItems;
import com.jeffyjamzhd.jeffybackpacks.render.ModelBackpackBase;
import com.jeffyjamzhd.jeffybackpacks.render.model.ModelBackpack;
import com.jeffyjamzhd.jeffybackpacks.render.model.ModelSatchel;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(RenderPlayer.class)
abstract public class RenderPlayerMixin extends RendererLivingEntity {
    @Shadow
    private ModelBiped modelArmor;

    @Unique
    private ModelSatchel modelSatchel;
    @Unique
    private ModelBackpack modelBackpack;

    public RenderPlayerMixin(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);
    }

    @ModifyArg(method = "<init>", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/ModelRenderer;addBox(FFFIIIF)V",
            ordinal = 3),
            index = 0)
    private float unfuckArm(float par1) {
        return -3.0F;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addCustomBackpacks(CallbackInfo ci) {
        modelSatchel = new ModelSatchel();
        modelBackpack = new ModelBackpack();
    }

    @Inject(method = "setArmorModel", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/RenderPlayer;setRenderPassModel(Lnet/minecraft/src/ModelBase;)V"),
            cancellable = true)
    private void cancelBackpackRender4Now(
            AbstractClientPlayer par1AbstractClientPlayer,
            int armorSlot, float par3, CallbackInfoReturnable<Integer> cir,
            @Local(ordinal = 0) ModelBiped model,
            @Local(ordinal = 0) ItemStack stack,
            @Local(ordinal = 0) ItemArmor item) {
        if (item instanceof IArmorCustomModel armorItem && armorItem.hasCustomModel() && armorSlot != 2) {
            // Do not render any normal breastplate stuff
            model.bipedBody.showModel = false;
            model.bipedLeftArm.showModel = false;
            model.bipedRightArm.showModel = false;

            // Render custom model
            int color = item.getColor(stack);
            boolean hasColor = color != -1;
            ModelBackpackBase armor = armorItem.getCustomModel();
            renderCustomArmorModel(armor, hasColor);

            if (hasColor) {
                float r = (float)(color >> 16 & 0xFF) / 255.0F;
                float g = (float)(color >> 8 & 0xFF) / 255.0F;
                float b = (float)(color & 0xFF) / 255.0F;
                GL11.glColor3f(r, g, b);

                if (item.hasEffect(stack)) {
                    cir.setReturnValue(15);
                    return;
                }
                cir.setReturnValue(1);
                return;
            }

            GL11.glColor3f(1F, 1F, 1F);
            if (item.hasEffect(stack)) {
                cir.setReturnValue(15);
                return;
            }
            cir.setReturnValue(1);
        }
    }

    @Unique
    private void renderCustomArmorModel(ModelBackpackBase model, boolean hasColor) {
        model.isSneaking = this.modelArmor.isSneak;
        model.onGround = this.mainModel.onGround;

        this.bindTexture(model.getTexture(hasColor));
        this.setRenderPassModel(model);
    }
}
