package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public record NutrientDataSyncTrigger(net.minecraft.item.Item item) {

    public static final Identifier ID =
            new Identifier(
                    NutritionalBalance.MODID,
                    "nutrient_sync_trigger"
            );

    public static NutrientDataSyncTrigger decode(
            net.minecraft.network.PacketByteBuf buf
    ) {
        ItemStack stack = buf.readItemStack();

        return new NutrientDataSyncTrigger(stack.getItem());
    }

    public static void encode(
            net.minecraft.network.PacketByteBuf buf,
            NutrientDataSyncTrigger packet
    ) {
        buf.writeItemStack(packet.item().getDefaultStack());
    }

    public static void handle(
            NutrientDataSyncTrigger packet,
            ServerPlayerEntity player
    ) {
        ModNetworkHandler.sendToClient(
                new NutrientDataSync(
                        packet.item(),
                        WorldNutrients.getNutrients(
                                packet.item(),
                                player.getWorld()
                        )
                ),
                player
        );
    }
}