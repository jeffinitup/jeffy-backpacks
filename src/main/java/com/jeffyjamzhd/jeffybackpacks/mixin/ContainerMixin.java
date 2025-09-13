package com.jeffyjamzhd.jeffybackpacks.mixin;

import com.jeffyjamzhd.jeffybackpacks.api.IItemExtendedInteraction;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Container.class)
public class ContainerMixin {
    @Inject(method = "slotClick", at = @At("HEAD"), cancellable = true)
    private void slotClick(int slotID, int clickType, int IDFK,
                           EntityPlayer player, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack cursorStack = player.inventory.getItemStack();
        if (clickType == 1 && slotID >= 0 && slotID <= player.openContainer.getInventory().size()) {
            // Get stack
            Slot slotAt = player.openContainer.getSlot(slotID);
            ItemStack stackAt = slotAt.getStack();

            // Do right click with stack if applicable
            if (stackAt != null && stackAt.getItem() instanceof IItemExtendedInteraction ext) {
                if (cursorStack != null) {
                    stackAt = ext.itemRightClickedWithStack(stackAt, cursorStack, player, player.getEntityWorld());
                } else {
                    stackAt = ext.itemRightClicked(stackAt, player, player.getEntityWorld());
                }
                cir.setReturnValue(stackAt);
            }
        }
    }
}
