package com.dannyandson.nutritionalbalance.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ClientHelpers {

    public static void showStatusToast(String statusName) {
        MinecraftClient.getInstance().getToastManager().add(
                new net.minecraft.client.toast.SystemToast(
                        net.minecraft.client.toast.SystemToast.Type.PERIODIC_NOTIFICATION,
                        Text.translatable("nutritionalbalance.nutrientstatus." + statusName),
                        Text.translatable("nutritionalbalance.nutrientstatus.msg." + statusName)
                )
        );
    }

    public static PlayerEntity getLocalPlayer() {
        return MinecraftClient.getInstance().player;
    }
}