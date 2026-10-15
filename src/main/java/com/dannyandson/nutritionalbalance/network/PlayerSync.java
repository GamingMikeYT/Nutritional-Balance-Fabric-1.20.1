
package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.capabilities.DefaultPlayerNutrient;
import com.dannyandson.nutritionalbalance.events.ClientHelpers;
import com.dannyandson.nutritionalbalance.gui.NutrientGUI;
import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.util.Identifier;

import java.util.Map;

public record PlayerSync(String jsonData, boolean openGUI) {

    public static final Identifier ID =
            new Identifier(
                    NutritionalBalance.MODID,
                    "player_sync"
            );

    public PlayerSync(INutritionalBalancePlayer iNutritionalBalancePlayer) {
        this(iNutritionalBalancePlayer, false);
    }

    public PlayerSync(
            INutritionalBalancePlayer iNutritionalBalancePlayer,
            boolean openGUI
    ) {
        this(buildJson(iNutritionalBalancePlayer), openGUI);
    }

    private static String buildJson(
            INutritionalBalancePlayer iNutritionalBalancePlayer
    ) {
        JsonObject json = new JsonObject();

        for (
                IPlayerNutrient iPlayerNutrient :
                iNutritionalBalancePlayer.getPlayerNutrients()
        ) {
            // Temporary diagnostic: log the values the server is sending.
            System.out.println(
                    "[NutritionalBalance] Server sync: "
                            + iPlayerNutrient.getNutrientName()
                            + ", value=" + iPlayerNutrient.getValue()
            );

            json.addProperty(
                    iPlayerNutrient.getNutrientName(),
                    iPlayerNutrient.getValue()
            );
        }

        return json.toString();
    }

    public static void handle(
            PlayerSync packet,
            net.minecraft.client.network.ClientPlayerEntity player
    ) {
        JsonObject inutritionalbalancePlayerJson =
                JsonParser.parseString(packet.jsonData()).getAsJsonObject();

        INutritionalBalancePlayer iNutritionalBalancePlayer =
                PlayerNutritionData.getWorldNutritionData()
                        .getNutritionalBalancePlayer(
                                ClientHelpers.getLocalPlayer()
                        );

        for (
                Map.Entry<String, JsonElement> jsonElementEntry :
                inutritionalbalancePlayerJson.entrySet()
        ) {
            IPlayerNutrient playerNutrient =
                    iNutritionalBalancePlayer.getPlayerNutrientByName(
                            jsonElementEntry.getKey()
                    );

            if (playerNutrient == null) {
                Nutrient worldNutrient =
                        WorldNutrients.getByName(
                                jsonElementEntry.getKey()
                        );

                if (worldNutrient == null) {
                    System.out.println(
                            "[NutritionalBalance] Unknown nutrient received: "
                                    + jsonElementEntry.getKey()
                    );
                    continue;
                }

                playerNutrient =
                        new DefaultPlayerNutrient(worldNutrient);

                iNutritionalBalancePlayer
                        .getPlayerNutrients()
                        .add(playerNutrient);
            }

            float syncedValue = jsonElementEntry.getValue().getAsFloat();

            System.out.println(
                    "[NutritionalBalance] Client sync: "
                            + jsonElementEntry.getKey()
                            + ", received=" + syncedValue
                            + ", before=" + playerNutrient.getValue()
            );

            playerNutrient.setValue(syncedValue);
        }

        if (packet.openGUI()) {
            NutrientGUI.open();
        }
    }
}
