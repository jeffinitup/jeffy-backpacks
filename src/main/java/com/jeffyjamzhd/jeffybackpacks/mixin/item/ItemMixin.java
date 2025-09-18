package com.jeffyjamzhd.jeffybackpacks.mixin.item;

import com.jeffyjamzhd.jeffybackpacks.api.impl.IItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin implements IItem {
    @Unique
    private String namespaceOverride;

    @Inject(method = "getModId", at = @At("RETURN"), cancellable = true)
    public void redirectModID(CallbackInfoReturnable<String> cir) {
        if (namespaceOverride != null)
            cir.setReturnValue(namespaceOverride);
    }

    @Override
    public Item jbp$setModNamespace(String namespace) {
        namespaceOverride = namespace;
        return (Item) (Object) this;
    }
}
