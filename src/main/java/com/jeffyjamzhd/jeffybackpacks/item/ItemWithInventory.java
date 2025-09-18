package com.jeffyjamzhd.jeffybackpacks.item;

import btw.item.items.ArmorItem;
import btw.item.util.ItemUtils;
import com.jeffyjamzhd.jeffybackpacks.api.IItemExtendedInteraction;
import com.jeffyjamzhd.jeffybackpacks.inventory.BackpackInventory;
import com.jeffyjamzhd.jeffybackpacks.registry.JBPackets;
import com.jeffyjamzhd.jeffybackpacks.registry.JBSounds;
import emi.shims.java.com.unascribed.retroemi.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.List;

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
     * If this item has a second render pass
     */
    private boolean hasSecondPass = false;

    /**
     * Constructor for {@code ItemWithInventory}
     * @param id Item ID to occupy
     * @param grid The grid size for storage and rendering
     */
    public ItemWithInventory(int id, Pair<Integer, Integer> grid) {
        super(id, EnumArmorMaterial.CLOTH, 1, 1, 0);

        this.damageReduceAmount = 0;
        this.inventorySize = grid.getLeft() * grid.getRight();
        this.gridArrangement = grid;

        this.setMaxDamage(0);
        this.setMaxStackSize(1);
    }

    @Override
    public void jbp$onItemDestroyed(ItemStack stack, World world, double x, double y, double z) {
        if (!world.isRemote) {
            // Create inventory
            BackpackInventory inv = createInventory(stack);

            // Iterate and spill contents into world
            for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
                // Get stack
                ItemStack stackInSlot = inv.getStackInSlot(slot);
                if (stackInSlot == null)
                    continue;

                // Create entity
                ItemUtils.ejectStackWithRandomVelocity(world, x, y, z, stackInSlot);
            }
        }
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

    /**
     * Damage set to 1 indicates this item is dyed, for rendering purposes
     */
    @Override
    public void func_82813_b(ItemStack stack, int color) {
        super.func_82813_b(stack, color);
        stack.setItemDamage(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        // Null out armor stuff if necessary
        return jbp$canBeWorn() ? super.onItemRightClick(stack, world, player) : stack;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List stringList, boolean shift) {
        BackpackInventory inv = createInventory(stack);
        int slotCount = inventorySize - inv.getSizeInventory();

        super.addInformation(stack, player, stringList, shift);
        if (hasProperCompoundTag(stack)) {
            if (slotCount > 1) {
                stringList.add(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.ITALIC +
                        "%s slots available".formatted(slotCount) + EnumChatFormatting.RESET);
            } else if (slotCount == 1) {
                stringList.add(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.ITALIC +
                        "1 slot available" + EnumChatFormatting.RESET);
            } else {
                stringList.add(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.ITALIC +
                        "Full" + EnumChatFormatting.RESET);
            }
        }

    }

    //***       IItemExtendedInteraction        ***//

    @Override
    @Environment(EnvType.CLIENT)
    public void beforeExtendedInteraction(ItemStack item, int slotID, boolean holdingShift) {
        if (holdingShift) {
            return;
        }
        int selectedSlot = getSelectedSlot(item);
        sendInventoryPositionPacket(slotID, selectedSlot);
    }

    @Override
    public void itemRightClicked(ItemStack item, EntityPlayer player,
                                 World world, boolean holdingShift) {
        // JeffyBackpacks.logInfo("Item right clicked on {}!", !world.isRemote ? "server" : "client");

        // Get item from inventory
        BackpackInventory inv = createInventory(item);
        ItemStack invStack = null;

        if (!holdingShift && !inv.inventory.isEmpty()) {
            invStack = inv.getStackInSlot(inv.currentSlotID);
            inv.setInventorySlotContents(inv.currentSlotID, null);
        } else {
            ItemStack first = inv.popFirstStack();
            if (first != null) {
                ItemUtils.givePlayerStackOrEject(player, first);
            }
        }

        item.stackTagCompound = inv.writeToNBT(item.stackTagCompound);
        player.inventory.setItemStack(invStack);

        if (invStack != null) {
            playExtractSFX(world, player);
        }
    }

    @Override
    public ItemStack itemRightClickAsMouseStack(ItemStack item, EntityPlayer player,
                                                World world, boolean holdingShift) {
        // JeffyBackpacks.logInfo("Item right clicked as mouse stack on {}!", !world.isRemote ? "server" : "client");

        // Get item from inventory
        BackpackInventory inv = createInventory(item);
        ItemStack invStack;

        if (!holdingShift && !inv.inventory.isEmpty()) {
            invStack = inv.getStackInSlot(inv.currentSlotID);
            inv.setInventorySlotContents(inv.currentSlotID, null);
        } else {
            invStack = inv.popFirstStack();
        }
        item.stackTagCompound = inv.writeToNBT(item.stackTagCompound);

        if (invStack != null) {
            playExtractSFX(world, player);
        }

        return invStack;
    }

    @Override
    public void itemRightClickedWithStack(ItemStack itemStack, ItemStack mouseStack,
                                          EntityPlayer player, World world, boolean holdingShift) {
        // JeffyBackpacks.logInfo("Item right clicked with stack on {}!", !world.isRemote ? "server" : "client");

        if (isItemValidForInsertion(mouseStack)) {
            // Attempt to merge with inventory
            BackpackInventory inv = createInventory(itemStack);
            ItemStack result = inv.putStackSmart(mouseStack);
            itemStack.stackTagCompound = inv.writeToNBT(itemStack.stackTagCompound);

            if (result == null || !mouseStack.isItemEqual(result)) {
                playInsertSFX(world, player);
            } else {
                playFullSFX(world, player);
            }

            player.inventory.setItemStack(result);
        }
    }

    @Override
    public ItemStack itemRightClickedWithStackAsMouseStack(ItemStack slotStack, ItemStack mouseStack,
                                                           EntityPlayer player, World world, boolean holdingShift) {
        // JeffyBackpacks.logInfo("Stack right clicked with item on {}!", !world.isRemote ? "server" : "client");

        if (isItemValidForInsertion(slotStack)) {
            // Attempt to merge with inventory
            BackpackInventory inv = createInventory(mouseStack);
            ItemStack result = inv.putStackSmart(slotStack);
            mouseStack.stackTagCompound = inv.writeToNBT(mouseStack.stackTagCompound);

            if (result == null || !slotStack.isItemEqual(result)) {
                playInsertSFX(world, player);
            } else {
                playFullSFX(world, player);
            }

            return result;
        }
        return slotStack;
    }

    @Override
    public void itemScrolled(ItemStack item, EntityPlayer player,
                             World world, int direction, boolean holdingShift) {
        // Don't run: stack has no tag data or is holding shift
        if (!hasProperCompoundTag(item) || holdingShift) {
            return;
        }

        // Set scroll
        BackpackInventory inv = createInventory(item);
        int newScroll = inv.scrollCurrentSlotID(direction);
        setSelectedSlot(inv, item, newScroll);

        // Play sound
        Minecraft.getMinecraft().sndManager.playSoundFX("random.click", 0.5F, 1.8F + (itemRand.nextFloat() * 0.2F));
    }

    //***       Class specific methods        ***//

    /**
     * {@code true} if provided {@link ItemStack} is able to
     * be inserted into this item
     */
    public boolean isItemValidForInsertion(ItemStack stack) {
        return true;
    }

    /**
     * Sets second render pass mode in this {@code ItemWithInventory}.
     */
    public ItemWithInventory hasSecondRenderPass() {
        if (!MinecraftServer.getIsServer()) {
            this.hasSecondPass = true;
        }
        return this;
    }

    /**
     * Called when item is inserted
     */
    public void playInsertSFX(World world, EntityPlayer player) {
        if (world.isRemote) {
            world.playSound(
                    player.posX, player.posY, player.posZ, JBSounds.BACKPACK_INSERT.sound(),
                    0.9f + world.rand.nextFloat() * 0.1f, 1.0f + world.rand.nextFloat() * 0.25f);
        }

        if (!world.isRemote) {
            world.playSoundToNearExcept(
                    player, JBSounds.BACKPACK_INSERT.sound(),
                    0.9f + world.rand.nextFloat() * 0.1f, 1.0f + world.rand.nextFloat() * 0.25f);
        }
    }

    /**
     * Called when item is extracted
     */
    public void playExtractSFX(World world, EntityPlayer player) {
        if (world.isRemote) {
            world.playSound(
                    player.posX, player.posY, player.posZ, JBSounds.BACKPACK_EXTRACT.sound(),
                    0.9f + world.rand.nextFloat() * 0.1f, 1.0f + world.rand.nextFloat() * 0.25f);
        }

        if (!world.isRemote) {
            world.playSoundToNearExcept(
                    player, JBSounds.BACKPACK_EXTRACT.sound(),
                    0.9f + world.rand.nextFloat() * 0.1f, 1.0f + world.rand.nextFloat() * 0.25f);
        }
    }

    /**
     * Called when item insertion is attempted, but fails
     */
    public void playFullSFX(World world, EntityPlayer player) {
        if (world.isRemote) {
            world.playSound(
                    player.posX, player.posY, player.posZ, JBSounds.BACKPACK_FULL.sound(),
                    0.9f + world.rand.nextFloat() * 0.1f, 1.0f + world.rand.nextFloat() * 0.25f);
        }

        if (!world.isRemote) {
            world.playSoundToNearExcept(
                    player, JBSounds.BACKPACK_FULL.sound(),
                    0.9f + world.rand.nextFloat() * 0.1f, 1.0f + world.rand.nextFloat() * 0.25f);
        }
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
            return tag.hasKey("BackpackInventory");
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

    //***       Clientside methods        ***//

    @Environment(EnvType.CLIENT)
    private Icon dyedIcon;
    @Environment(EnvType.CLIENT)
    private Icon secondPassIcon;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        dyedIcon = register.registerIcon(getIconString() + "_dyed");
        if (hasSecondPass)
            secondPassIcon = register.registerIcon(getIconString() + "_overlay");
    }

    /**
     * Sends C2S sync packet for item inventory position
     */
    @Environment(EnvType.CLIENT)
    public void sendInventoryPositionPacket(int slotID, int currentSlot) {
        PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
        JBPackets.sendInventoryPositionPacket(controller.getNetClientHandler(), slotID, currentSlot);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack par1ItemStack) {
        NBTTagCompound var2 = par1ItemStack.getTagCompound();
        if (var2 == null) {
            return 0xFFFFFF;
        } else {
            NBTTagCompound var3 = var2.getCompoundTag("display");
            return var3 == null ? 0xFFFFFF : (var3.hasKey("color") ? var3.getInteger("color") : 0xFFFFFF);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamage(int damage) {
        return damage == 1 ? dyedIcon : itemIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamageForRenderPass(int damage, int pass) {
        return pass == 1 && hasSecondPass ? secondPassIcon : super.getIconFromDamageForRenderPass(damage, pass);
    }
}
