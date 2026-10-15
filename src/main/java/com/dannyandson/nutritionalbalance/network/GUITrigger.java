package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class GUITrigger {

    public static final Identifier ID =
            new Identifier(
                    NutritionalBalance.MODID,
                    "gui_trigger"
            );

    public static void handle(ServerPlayerEntity player) {
        INutritionalBalancePlayer inutritionalbalancePlayer =
                PlayerNutritionData.getWorldNutritionData()
                        .getNutritionalBalancePlayer(player);

        ModNetworkHandler.sendToClient(
                new PlayerSync(inutritionalbalancePlayer, true),
                player
        );
    }
}