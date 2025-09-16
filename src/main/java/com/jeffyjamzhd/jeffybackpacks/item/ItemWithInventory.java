package com.jeffyjamzhd.jeffybackpacks.item;

import btw.item.items.ArmorItem;
import btw.item.util.ItemUtils;
import com.jeffyjamzhd.jeffybackpacks.JeffyBackpacks;
import com.jeffyjamzhd.jeffybackpacks.api.IItemExtendedInteraction;
import com.jeffyjamzhd.jeffybackpacks.inventory.BackpackInventory;
import com.jeffyjamzhd.jeffybackpacks.registry.JBPackets;
import com.jeffyjamzhd.jeffybackpacks.registry.JBSounds;
import emi.shims.java.com.unascribed.retroemi.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

/**
 * An item that contains a certain amount of {@link ItemStack}.
 */
public class ItemWithInventory extends ArmorItem
        implements IItemExtendedInteraction {
    /**
     * The size of the inventory
     */
    public final int inventorySize;
    /**
     * Arrangement of the rendering grid
     */
    private final Pair<Integer, Integer> gridArrangement;

    /**
     * Constructor for {@code ItemWithInventory}
     * @param id Item ID to occupy
     * @param grid The grid size for storage and rendering
     */
    public ItemWithInventory(int id, Pair<Integer, Integer> grid) {
        super(id, EnumArmorMaterial.CHAIN, 1, 1, 0);

        this.damageReduceAmount = 0;
        this.inventorySize = grid.getLeft() * grid.getRight();
        this.gridArrangement = grid;

        this.setMaxDamage(0);
        this.setMaxStackSize(1);
    }

    /**
     * Update method, checks if NBT data needs to be initialized
     */
    @Override
    public void onUpdate(ItemStack stack, World world, EntityPlayer entity, int iInventorySlot, boolean bIsHandHeldItem) {
        super.onUpdate(stack, world, entity, iInventorySlot, bIsHandHeldItem);
        if (!hasProperCompoundTag(stack)) {
            BackpackInventory inv = createInventory(stack);
            stack.stackTagCompound = inv.writeToNBT(stack.getTagCompound());
        }
    }

    //***       IItemExtendedInteraction        ***//

    @Override
    @Environment(EnvType.CLIENT)
    public void beforeExtendedInteraction(ItemStack item, int slotID) {
        int selectedSlot = getSelectedSlot(item);
        sendInventoryPositionPacket(slotID, selectedSlot);
    }

    @Override
    public ItemStack itemRightClicked(ItemStack item, EntityPlayer player, World world, boolean holdingShift) {
        JeffyBackpacks.logInfo("Item right clicked on {}!", !world.isRemote ? "server" : "client");

        // Get item from inventory
        BackpackInventory inv = createInventory(item);
        ItemStack invStack = null;

        if (!holdingShift) {
            invStack = inv.getStackInSlot(inv.currentSlotID);
            inv.setInventorySlotContents(inv.currentSlotID, null);
        } else {
            ItemStack first = inv.popFirstStack();
            ItemUtils.givePlayerStackOrEject(player, first);
        }

        item.stackTagCompound = inv.writeToNBT(item.stackTagCompound);

        player.inventory.setItemStack(invStack);

        if (!world.isRemote) {
            if (invStack != null) {
                world.playSoundEffect(
                        player.posX, player.posY, player.posZ, JBSounds.BACKPACK_EXTRACT.sound(),
                        0.5f + world.rand.nextFloat() * 0.1f, 1.0f + world.rand.nextFloat() * 0.25f);
            }
        }

        return invStack;
    }

    @Override
    public ItemStack itemRightClickedWithStack(ItemStack item, ItemStack mouseStack, EntityPlayer player, World world, boolean holdingShift) {
        JeffyBackpacks.logInfo("Item right clicked with stack on {}!", !world.isRemote ? "server" : "client");

        // Attempt to merge with inventory
        BackpackInventory inv = createInventory(item);
        ItemStack result = holdingShift ? inv.putStackSmart(mouseStack) : inv.putStackAt(mouseStack, inv.currentSlotID);
        item.stackTagCompound = inv.writeToNBT(item.stackTagCompound);

        if (!world.isRemote) {
            if (result == null || !mouseStack.isItemEqual(result)) {
                // Play sound
                world.playSoundEffect(
                        player.posX, player.posY, player.posZ, JBSounds.BACKPACK_INSERT.sound(),
                        0.5f + world.rand.nextFloat() * 0.1f, 1.0f + world.rand.nextFloat() * 0.25f);
            } else if (mouseStack.isItemEqual(result)) {
                // Play "full" sound
                world.playSoundEffect(
                        player.posX, player.posY, player.posZ, JBSounds.BACKPACK_FULL.sound(),
                        0.5f + world.rand.nextFloat() * 0.1f, 1.0f + world.rand.nextFloat() * 0.25f);
            }


        }

        player.inventory.setItemStack(result);
        return result;
    }

    @Override
    public boolean itemScrolled(ItemStack item, EntityPlayer player, World world, int direction, boolean holdingShift) {
        // Don't run: stack has no tag data or is holding shift
        if (!hasProperCompoundTag(item) || holdingShift) {
            return false;
        }

        // Set scroll
        BackpackInventory inv = createInventory(item);
        int newScroll = inv.scrollCurrentSlotID(direction);
        setSelectedSlot(inv, item, newScroll);

        // Play sound
        Minecraft.getMinecraft().sndManager.playSoundFX("random.click", 0.6F, 1.5F);
        return true;
    }

    //***       Class specific methods        ***//

    /**
     * Sends C2S sync packet for item inventory position
     */
    @Environment(EnvType.CLIENT)
    public void sendInventoryPositionPacket(int slotID, int currentSlot) {
        PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
        JBPackets.sendInventoryPositionPacket(controller.getNetClientHandler(), slotID, currentSlot);
    }

    /**
     * {@code true} if the provided stack has an inventory.
     */
    public boolean hasProperCompoundTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return false;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) {
            NBTTagCompound inv = tag.getCompoundTag("BackpackInventory");
            return inv != null;
        }
        return false;
    }

    /**
     * Sets selected slot in provided {@link ItemStack}
     * @param slotID Slot to select
     */
    private void setSelectedSlot(BackpackInventory inv, ItemStack stack, int slotID) {
        NBTTagCompound compound = stack.getTagCompound();
        stack.stackTagCompound = inv.writeCurrentSlotToNBT(compound, slotID);
    }

    /**
     * Shorthand for creating an inventory instance for interaction handling
     */
    private BackpackInventory createInventory(ItemStack stack) {
        return new BackpackInventory(stack, inventorySize);
    }

    public Pair<Integer, Integer> getInvGridArrangement() {
        return gridArrangement;
    }

    public void setSelectedSlot(ItemStack stack, int slotID) {
        BackpackInventory inv = createInventory(stack);
        setSelectedSlot(inv, stack, slotID);
    }

    public int getSelectedSlot(ItemStack stack) {
        BackpackInventory inv = createInventory(stack);
        return inv.currentSlotID;
    }
}
