package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.PlayerSync;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class EventPlayerJoin {

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;

            INutritionalBalancePlayer inutritionalbalancePlayer =
                    PlayerNutritionData.getWorldNutritionData()
                            .getNutritionalBalancePlayer(player);

            PlayerSync playerSync = new PlayerSync(inutritionalbalancePlayer);

            ModNetworkHandler.sendToClient(playerSync, player);
        });
    }
}