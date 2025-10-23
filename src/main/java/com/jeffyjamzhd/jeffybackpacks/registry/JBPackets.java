package com.jeffyjamzhd.jeffybackpacks.registry;

import com.jeffyjamzhd.jeffybackpacks.JeffyBackpacks;
import com.jeffyjamzhd.jeffybackpacks.item.ItemFilter;
import com.jeffyjamzhd.jeffybackpacks.item.ItemWithInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.io.*;

public class JBPackets {
    public static String PACKET_S2C_INSERT_SFX = "jbp|insertSFX";
    public static String PACKET_C2S_UPDATE_SCROLL = "jbp|invPos";
    public static String PACKET_C2S_FILTER_EMI = "jbp|filterEMI";

    public static void register(JeffyBackpacks addon) {
        JeffyBackpacks.logInfo("Registering packets...");
        addon.registerPacketHandler(PACKET_C2S_UPDATE_SCROLL, JBPackets::handleInventoryPositionPacket);
        addon.registerPacketHandler(PACKET_C2S_FILTER_EMI, JBPackets::handleFilterEMI);

        if (!MinecraftServer.getIsServer())
            registerClient(addon);
    }

    public static void registerClient(JeffyBackpacks addon) {
        JeffyBackpacks.logInfo("Registering client packets...");
        addon.registerPacketHandler(PACKET_S2C_INSERT_SFX, JBPackets::handleInsertSFXPacket);
    }

    /**
     * Sends a C2S packet that updates the scroll value in backpack on the server side
     * @param slotID Slot id backpack is located
     * @param scrollValue Scroll value in backpack
     */
    @Environment(EnvType.CLIENT)
    public static void sendInventoryPositionPacket(NetClientHandler handler, int slotID, int scrollValue) {
        // Setup stream
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        // Write data
        try {
            dataStream.writeShort(slotID);
            dataStream.writeShort(scrollValue);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }

        // Queue it
        byte[] data = byteStream.toByteArray();
        Packet250CustomPayload packet = new Packet250CustomPayload(PACKET_C2S_UPDATE_SCROLL, data);
        handler.addToSendQueue(packet);
    }

    /**
     * Sends a C2S packet that updates filter additions from EMI phantom stack
     * @param slotID Slot id filter is located
     * @param fakeStack {@link ItemStack} on the client
     */
    @Environment(EnvType.CLIENT)
    public static void sendFilterEMIPacket(NetClientHandler handler, int slotID, ItemStack fakeStack) {
        // Setup stream
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        // Write data
        try {
            dataStream.writeShort(slotID);
            Packet.writeItemStack(fakeStack, dataStream);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }

        // Queue it
        byte[] data = byteStream.toByteArray();
        Packet250CustomPayload packet = new Packet250CustomPayload(PACKET_C2S_FILTER_EMI, data);
        handler.addToSendQueue(packet);
    }

    /**
     * Sends a S2C packet that plays the worn backpack's insertion sound on the client.
     * @param handler Net handler
     */
    public static void sendInsertSFXPacket(NetServerHandler handler) {
        Packet250CustomPayload packet = new Packet250CustomPayload(PACKET_S2C_INSERT_SFX, null);
        handler.sendPacketToPlayer(packet);
    }

    /**
     * Verifies and sets scroll NBT in provided slotID
     * @param payload Scroll packet to parse
     * @param player Player who sent the packet
     */
    public static void handleInventoryPositionPacket(Packet250CustomPayload payload, EntityPlayer player) {
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(payload.data));
        try {
            // Parse data
            int slotID = stream.readShort();
            int scrollValue = stream.readShort();

            // Check for cursor stack first!!
            if (slotID == -999 && player.inventory.getItemStack() != null) {
                ItemStack cursorStack = player.inventory.getItemStack();
                if (cursorStack.getItem() instanceof ItemWithInventory invItem) {
                    invItem.setSelectedSlot(cursorStack, scrollValue);
                    // JeffyBackpacks.logInfo("Set cursor selected slot to {} on server", scrollValue);
                }
                return;
            }

            // Okay, now check for slot
            Slot slot = player.openContainer.getSlot(slotID);
            if (slot != null && slot.getHasStack()) {
                // Check item in slot
                ItemStack stack = slot.getStack();
                if (stack.getItem() instanceof ItemWithInventory invItem) {
                    // Set selected slot
                    invItem.setSelectedSlot(stack, scrollValue);
                    // JeffyBackpacks.logInfo("Set item selected slot to {} on server", scrollValue);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    public static void handleFilterEMI(Packet250CustomPayload payload, EntityPlayer player) {
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(payload.data));
        try {
            // Get data
            int slotID = stream.readShort();
            ItemStack fakeStack = Packet.readItemStack(stream);

            // Do the client thing on the server doohickey
            Container container = player.openContainer;
            if (container.inventorySlots.size() < slotID)
                return;

            Slot slot = container.getSlot(slotID);
            if (slot != null && slot.getHasStack()) {
                ItemStack stack = slot.getStack();
                if (stack != null && stack.getItem() instanceof ItemFilter filter) {
                    filter.itemRightClickedWithStack(stack, fakeStack,
                            player, player.getEntityWorld(), false);
                }
            }

        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    public static void handleInsertSFXPacket(Packet250CustomPayload payload, EntityPlayer player) {
        // No data to parse from payload, just play sfx
        ItemStack chestplate = player.inventory.armorItemInSlot(2);
        if (chestplate != null && chestplate.getItem() instanceof ItemWithInventory inv) {
            inv.playInsertSFX(player.worldObj, player);
        }
    }
}
