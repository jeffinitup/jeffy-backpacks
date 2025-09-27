package com.jeffyjamzhd.jeffybackpacks.mixin.render;

import com.jeffyjamzhd.jeffybackpacks.api.IArmorWithCustomModel;
import com.jeffyjamzhd.jeffybackpacks.item.ItemWithInventory;
import com.jeffyjamzhd.jeffybackpacks.registry.JBItems;
import com.jeffyjamzhd.jeffybackpacks.render.model.ModelSatchel;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
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
    @Shadow private ModelBiped modelArmor;
    @Unique
    private ModelSatchel modelSatchel;

    public RenderPlayerMixin(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);
    }

    @ModifyArg(method = "<init>", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/ModelRenderer;addBox(FFFIIIF)V",
            ordinal = 3),
            index = 0)
    private float unfuckArm(float par1) {
        return -2.0F;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addCustomBackpacks(CallbackInfo ci) {
        modelSatchel = new ModelSatchel(this.mainModel);
    }

    @Inject(method = "setArmorModel", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/RenderPlayer;setRenderPassModel(Lnet/minecraft/src/ModelBase;)V"),
            cancellable = true)
    private void cancelBackpackRender4Now(
            AbstractClientPlayer par1AbstractClientPlayer,
            int par2, float par3, CallbackInfoReturnable<Integer> cir,
            @Local(ordinal = 0) ModelBiped model, @Local(ordinal = 0) ItemArmor item) {
        if (item instanceof IArmorWithCustomModel armorItem && par2 != 2) {
            // Do not render any normal breastplate stuff
            model.bipedBody.showModel = false;
            model.bipedLeftArm.showModel = false;
            model.bipedRightArm.showModel = false;

            ModelBase armorModel = armorItem.getCustomModel(par2);
            ResourceLocation armorTex = armorItem.getCustomModelTexture(par2);

            // Instead render custom backpack models
            if (item.itemID == JBItems.satchel.itemID) {
                modelSatchel.satchel.showModel = true;
                modelSatchel.satchel_strand.showModel = true;

                modelSatchel.isSneaking = this.modelArmor.isSneak;
                modelSatchel.onGround = this.mainModel.onGround;

                this.bindTexture(new ResourceLocation("jbp", "textures/models/satchel_overlay.png"));
                this.setRenderPassModel(modelSatchel);

                cir.setReturnValue(1);
            }
        }

    }
}
