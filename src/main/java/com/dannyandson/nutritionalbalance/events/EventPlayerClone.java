package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class EventPlayerClone {

    public static void init() {
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (!alive) {
                INutritionalBalancePlayer iNutritionalBalancePlayer =
                        PlayerNutritionData.getWorldNutritionData()
                                .getNutritionalBalancePlayer(newPlayer);

                for (IPlayerNutrient playerNutrient :
                        iNutritionalBalancePlayer.getPlayerNutrients()) {

                    float newNutrientValue = Math.max(
                            Config.NUTRIENT_INITIAL.get().floatValue(),
                            playerNutrient.getValue()
                                    - Config.NUTRIENT_DEATH_LOSS.get().floatValue()
                    );

                    iNutritionalBalancePlayer
                            .getPlayerNutrientByName(playerNutrient.getNutrientName())
                            .setValue(newNutrientValue);
                }

                PlayerNutritionData.getWorldNutritionData().setDirty();
            }
        });
    }
}