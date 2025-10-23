package com.jeffyjamzhd.jeffybackpacks.item;

import btw.item.items.ArmorItem;
import btw.item.util.ItemUtils;
import com.jeffyjamzhd.jeffybackpacks.api.IArmorCustomModel;
import com.jeffyjamzhd.jeffybackpacks.inventory.FilterInventory;
import com.jeffyjamzhd.jeffybackpacks.render.ModelBackpackBase;
import com.jeffyjamzhd.jeffylib.api.IItemExtendedInteraction;
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
        implements IItemExtendedInteraction, IArmorCustomModel
{
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
    public void jl$onItemDestroyed(ItemStack stack, World world, double x, double y, double z) {
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
            // Create tag for this item, if one doesn't exist
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
        return jl$canBeWorn() ? super.onItemRightClick(stack, world, player) : stack;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List stringList, boolean shift) {
        BackpackInventory inv = createInventory(stack);
        int slotCount = inventorySize - inv.getSizeInventory();

        super.addInformation(stack, player, stringList, shift);
        if (hasProperCompoundTag(stack)) {
            // Determine tooltip
            String slotCountString;
            if (slotCount > 1)
                slotCountString = I18n.getStringParams("tooltip.backpack.slots", slotCount);
            else if (slotCount == 1)
                slotCountString = I18n.getStringParams("tooltip.backpack.slot", slotCount);
            else
                slotCountString = I18n.getString("tooltip.backpack.full");

            // Add to list
            slotCountString = addStringFormatting(slotCountString);
            stringList.add(slotCountString);
        }

        // Add filter field
        if (hasFilterTag(stack)) {
            String filterString = addStringFormatting(I18n.getString("tooltip.backpack.filtered"));
            stringList.add(filterString);
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
    public boolean canInteractWithSelf(ItemStack itemStack, ItemStack itemStack1) {
        return false;
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

        // Mark for update
        player.inventory.onInventoryChanged();
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

        // Mark for update
        player.inventory.onInventoryChanged();
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

            // Mark for update
            player.inventory.onInventoryChanged();
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

            // Mark for update
            player.inventory.onInventoryChanged();
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

        // Mark for update
        player.inventory.onInventoryChanged();
    }

    //***       IArmorWithCustomModel        ***//

    /**
     * Armor model, assigned upon registration
     */
    @Environment(EnvType.CLIENT)
    private ModelBackpackBase model;
    @Environment(EnvType.CLIENT)
    private boolean hasCustomModel;

    @Override
    public boolean hasCustomModel() {
        return hasCustomModel;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ModelBackpackBase getCustomModel() {
        return model;
    }

    /**
     * Sets the armor model in this {@code ItemWithInventory}
     */
    @Environment(EnvType.CLIENT)
    public void setCustomModel(ModelBackpackBase model) {
        this.model = model;
        hasCustomModel = true;
    }

    //***       Class specific methods        ***//

    /**
     * Called by player inventory, for merging stack from world -> item inventory
     */
    public int putStackInInventory(ItemStack backpack, ItemStack stack,
                                   EntityPlayer player, World world) {

        if (isItemValidForInsertion(stack) && stackMatchesFilter(backpack, stack)) {
            // Create inventory and put stack inside
            BackpackInventory inv = createInventory(backpack);
            ItemStack result = inv.putStackSmart(stack);
            backpack.stackTagCompound = inv.writeToNBT(backpack.getTagCompound());

            // Play sfx and return stack size
            if (result == null || !stack.isItemEqual(result)) {
                if (!world.isRemote)
                    JBPackets.sendInsertSFXPacket(((EntityPlayerMP) player).playerNetServerHandler);
            }

            // Mark for update
            player.inventory.onInventoryChanged();
            return result == null ? 0 : result.stackSize;
        }
        return stack.stackSize;
    }

    /**
     * {@code true} if provided {@link ItemStack} matches tag within this {@code ItemWithInventory}
     */
    public boolean stackMatchesFilter(ItemStack backpack, ItemStack stack) {
        NBTTagCompound compound = backpack.getTagCompound();
        if (compound != null) {
            if (compound.hasKey("FilterInventory")) {
                FilterInventory filter = new FilterInventory(compound);
                return filter.matches(stack);
            }
            return true;
        }
        return true;
    }

    /**
     * Gets the amount of items currently in the provided stack.
     */
    public int getItemCountInStack(ItemStack stack) {
        BackpackInventory inv = createInventory(stack);
        return inv.getSizeInventory();
    }

    /**
     * {@code true} if provided {@link ItemStack} is able to
     * be inserted into this item
     */
    public boolean isItemValidForInsertion(ItemStack stack) {
        return !(stack.getItem() instanceof ItemWithInventory);
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
            BackpackInventory inv = createInventory(stack);
            return tag.hasKey(inv.getRootTagString());
        }
        return false;
    }

    /**
     * {@code true} if the provided stack has a filter applied
     */
    public boolean hasFilterTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return false;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) {
            BackpackInventory inv = createInventory(stack);
            return tag.hasKey("FilterInventory") && tag.hasKey("BackpackInventory");
        }
        return false;
    }

    /**
     * {@code true} if the provided stack has the enchantment glint tag (for EMI display)
     */
    public boolean hasEffectEMI(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return false;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) {
            return tag.hasKey("EmiEffect");
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
    public BackpackInventory createInventory(ItemStack stack) {
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

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return hasEffectEMI(stack) ? getUnlocalizedName() + ".filter" : super.getUnlocalizedName(stack);
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
            return -1;
        } else {
            NBTTagCompound var3 = var2.getCompoundTag("display");
            return var3 == null ? -1 : (var3.hasKey("color") ? var3.getInteger("color") : -1);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int pass) {
        return pass > 0 ? -1 : getColor(par1ItemStack);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamage(int damage) {
        return damage == 1 ? dyedIcon : itemIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamageForRenderPass(int damage, int pass) {
        return pass == 1 && hasSecondPass ? secondPassIcon : itemIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return hasSecondPass;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return hasEffectEMI(stack) || hasFilterTag(stack);
    }

    @Environment(EnvType.CLIENT)
    public String addStringFormatting(String string) {
        return EnumChatFormatting.GRAY.toString() + EnumChatFormatting.ITALIC +
                string + EnumChatFormatting.RESET;
    }
}
