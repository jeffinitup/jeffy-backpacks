package com.jeffyjamzhd.jeffybackpacks.registry;

import com.jeffyjamzhd.jeffybackpacks.JeffyBackpacks;
import com.jeffyjamzhd.jeffybackpacks.api.IItemExtendedInteraction;
import com.jeffyjamzhd.jeffybackpacks.item.ItemWithInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.io.*;
import java.util.ArrayList;

public class JBPackets {
    public static String PACKET_EXTENDED_SLOT_INTERACTION = "jbp|extendedSlotInteraction";

    public static void register(JeffyBackpacks addon) {
        JeffyBackpacks.logInfo("Registering packets...");
        addon.registerPacketHandler(PACKET_EXTENDED_SLOT_INTERACTION, JBPackets::handleExtendedInteraction);

        if (!MinecraftServer.getIsServer())
            registerClientPackets(addon);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClientPackets(JeffyBackpacks addon) {
        JeffyBackpacks.logInfo("Registering client packets...");
    }

    /**
     * Sends extended interaction packet (Client -> Server)
     * @param windowID Open container (0 - player inventory)
     * @param slotID Slot right-clicked on (-999 - no slot)
     */
    @Environment(EnvType.CLIENT)
    public static void sendExtendedInteraction(int windowID, int slotID, ItemStack cursorStack, NetClientHandler handler) {
        // Begin forming packet
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        try {
            dataStream.writeShort(windowID);
            dataStream.writeShort(slotID);
            Packet.writeItemStack(cursorStack, dataStream);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }

        byte[] data = byteStream.toByteArray();
        Packet250CustomPayload packet = new Packet250CustomPayload(PACKET_EXTENDED_SLOT_INTERACTION, data);
        handler.addToSendQueue(packet);
    }

    /**
     * Parses extended interaction packet
     * @param payload Packet
     * @param player Player
     */
    public static void handleExtendedInteraction(Packet250CustomPayload payload, EntityPlayer player) {
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(payload.data));
        try {
            // Parse data
            int windowID = stream.readShort();
            int slotID = stream.readShort();
            ItemStack cursorStack = Packet.readItemStack(stream);

            if (windowID != player.openContainer.windowId || !player.openContainer.isPlayerNotUsingContainer(player)) {
                // Container mismatch
                return;
            }

            // Get item at slot
            Slot slotAt = player.openContainer.getSlot(slotID);

            // Holding stack?
            if (cursorStack != null && slotAt.getHasStack()) {
                // Do not allow nesting
                if (cursorStack.getItem() instanceof ItemWithInventory) {
                    return;
                }

                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                ItemStack itemStackAt = slotAt.getStack();
                IItemExtendedInteraction item = (IItemExtendedInteraction) itemStackAt.getItem();

                // Handle transaction
                short transaction = playerMP.openContainer.getNextTransactionID(playerMP.inventory);
                playerMP.playerNetServerHandler.sendPacketToPlayer(new Packet106Transaction(windowID, transaction, true));

                // Handle interaction
                ItemStack result = item.itemRightClickedWithStack(itemStackAt, cursorStack, player, player.getEntityWorld());
                playerMP.playerInventoryBeingManipulated = true;
                playerMP.inventory.setItemStack(result.stackSize > 0 ? result : null);
                playerMP.updateHeldItem();
                playerMP.openContainer.detectAndSendChanges();
                playerMP.playerInventoryBeingManipulated = false;

//                // Sync with client
//                ArrayList<ItemStack> stacks = new ArrayList<>();
//                for (int i = 0; i < playerMP.openContainer.inventorySlots.size(); ++i)
//                    stacks.add(((Slot) playerMP.openContainer.inventorySlots.get(i)).getStack());
//                playerMP.updateCraftingInventory(playerMP.openContainer, stacks);
            }

        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
