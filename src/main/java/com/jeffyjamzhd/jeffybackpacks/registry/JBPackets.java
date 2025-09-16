package com.jeffyjamzhd.jeffybackpacks.registry;

import com.jeffyjamzhd.jeffybackpacks.JeffyBackpacks;
import com.jeffyjamzhd.jeffybackpacks.item.ItemWithInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.io.*;

public class JBPackets {
    public static String PACKET_UPDATE_SCROLL = "jbp|inventoryPosition";

    public static void register(JeffyBackpacks addon) {
        JeffyBackpacks.logInfo("Registering packets...");
        addon.registerPacketHandler(PACKET_UPDATE_SCROLL, JBPackets::handleInventoryPositionPacket);

        if (!MinecraftServer.getIsServer())
            registerClientPackets(addon);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClientPackets(JeffyBackpacks addon) {
        JeffyBackpacks.logInfo("Registering client packets...");
    }

    /**
     * Sends a C2S packet that updates the scroll value in backpack on the server side
     * @param slotID Slot backpack is in
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
        Packet250CustomPayload packet = new Packet250CustomPayload(PACKET_UPDATE_SCROLL, data);
        handler.addToSendQueue(packet);
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

            // Get slot at ID
            Slot slot = player.openContainer.getSlot(slotID);
            if (slot != null && slot.getHasStack()) {
                // Check item in slot
                ItemStack stack = slot.getStack();
                if (stack.getItem() instanceof ItemWithInventory specialItem) {
                    // Set selected slot
                    specialItem.setSelectedSlot(stack, scrollValue);
                    JeffyBackpacks.logInfo("Set slot to {} on server", scrollValue);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
