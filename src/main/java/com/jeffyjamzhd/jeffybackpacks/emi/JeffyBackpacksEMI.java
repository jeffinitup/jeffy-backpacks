package com.jeffyjamzhd.jeffybackpacks.emi;

import btw.item.tag.Tag;
import com.jeffyjamzhd.jeffybackpacks.api.recipe.RecipeBundle;
import com.jeffyjamzhd.jeffybackpacks.item.ItemFilter;
import com.jeffyjamzhd.jeffybackpacks.registry.JBItems;
import com.jeffyjamzhd.jeffybackpacks.registry.JBPackets;
import com.jeffyjamzhd.jeffybackpacks.registry.JBRecipes;
import com.jeffyjamzhd.jeffybackpacks.registry.JBTags;
import emi.dev.emi.emi.api.EmiPlugin;
import emi.dev.emi.emi.api.EmiRegistry;
import emi.dev.emi.emi.api.recipe.EmiInfoRecipe;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.shims.java.net.minecraft.text.Text;
import net.minecraft.src.*;

import java.util.List;
import java.util.stream.Collectors;

public class JeffyBackpacksEMI implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        // Recipe handlers
        registry.addRecipe(new EMIBundleRecipe((RecipeBundle) JBRecipes.bundleRecipe));
        registry.addRecipe(new EMIAddFilterRecipe());
        JBTags.TAG_COLORED_BAGS.getItems()
                .forEach(stack -> registry.addRecipe(new EMIBackpackDyeRecipe(stack.getItem())));
        JBTags.TAG_BACKPACKS.getItems()
                .forEach(stack -> registry.addRecipe(new EMIRemoveFilterRecipe(stack.getItem())));


        // Phantom stack handler
        addClickStackHandler(registry);

        // Addon info recipes
        registry.addRecipe(new EmiInfoRecipe(
                parseTag(JBTags.TAG_INVENTORY_ITEMS),
                List.of(
                        Text.translatable("jbp.inventory_items.info.1"),
                        Text.literal(""),
                        Text.translatable("jbp.inventory_items.info.2")),
                null
        ));
        registry.addRecipe(new EmiInfoRecipe(
                parseTag(JBTags.TAG_BACKPACKS),
                List.of(Text.translatable("jbp.backpacks.info")),
                null
        ));
        registry.addRecipe(new EmiInfoRecipe(
                List.of(EmiStack.of(JBItems.lunchbox), EmiStack.of(new ItemStack(JBItems.lunchbox, 1, 1))),
                List.of(Text.translatable("jbp.lunchbox.info")),
                null
        ));
        registry.addRecipe(new EmiInfoRecipe(
                List.of(EmiStack.of(JBItems.filter)),
                List.of(Text.translatable("jbp.filter.info")),
                null
        ));
        registry.addRecipe(new EmiInfoRecipe(
                List.of(EmiStack.of(JBItems.trowel)),
                List.of(Text.translatable("jbp.trowel.info.1"),
                        Text.literal(""),
                        Text.translatable("jbp.trowel.info.2")),
                null
        ));
    }

    public static void addClickStackHandler(EmiRegistry registry) {
        registry.addGenericDragDropHandler((screen, stack, x, y) ->
        {
            if (screen instanceof GuiContainer container) {
                Slot slot = container.getSlotAtPosition(x, y);
                if (slot != null && slot.getHasStack()) {
                    ItemStack stackAt = slot.getStack();
                    if (stackAt != null && stackAt.getItem() instanceof ItemFilter filter) {
                        // Add to filter
                        AbstractClientPlayer player = Minecraft.getMinecraft().thePlayer;
                        ItemStack fakeStack = stack.getEmiStacks().get(0).getItemStack();
                        filter.itemRightClickedWithStack(stackAt, fakeStack,
                                player, player.worldObj, false);

                        // Sync with server
                        NetClientHandler handler = Minecraft.getMinecraft().getNetHandler();
                        JBPackets.sendFilterEMIPacket(handler, slot.slotNumber, fakeStack);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    private List<EmiIngredient> parseTag(Tag tag) {
        return tag.getItems()
                .stream()
                .map(EmiStack::of)
                .collect(Collectors.toUnmodifiableList());
    }
}
