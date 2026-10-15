package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxItem;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public record LunchBoxActiveItemSync(String activeItemId) {

    public static final Identifier ID =
            new Identifier(
                    NutritionalBalance.MODID,
                    "lunchbox_active"
            );

    public static LunchBoxActiveItemSync decode(PacketByteBuf buf) {
        return new LunchBoxActiveItemSync(
                buf.readString()
        );
    }

    public void encode(PacketByteBuf buf) {
        buf.writeString(activeItemId);
    }

    public static void handle(
            LunchBoxActiveItemSync packet,
            ServerPlayerEntity player
    ) {
        ItemStack stack = player.getMainHandStack();

        if (stack.getItem() instanceof LunchBoxItem lunchBoxItem) {
            lunchBoxItem.setActiveFood(
                    stack,
                    packet.activeItemId()
            );
        }
    }
}