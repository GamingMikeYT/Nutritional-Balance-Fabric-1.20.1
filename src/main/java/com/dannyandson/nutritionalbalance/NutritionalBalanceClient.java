
package com.dannyandson.nutritionalbalance;

import com.dannyandson.nutritionalbalance.events.EventNutrientButton;
import com.dannyandson.nutritionalbalance.events.EventTooltip;
import com.dannyandson.nutritionalbalance.keybinding.ModInputHandler;
import com.dannyandson.nutritionalbalance.keybinding.ModKeyBindings;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;

import net.fabricmc.api.ClientModInitializer;

public class NutritionalBalanceClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register the client's network packet receivers.
        ModNetworkHandler.initClient();

        // Register the nutrient GUI keybind (N by default).
        ModKeyBindings.init();

        // Register the keybind handler.
        ModInputHandler.init();

        // Add the nutrient overview button to the inventory screen.
        EventNutrientButton.init();

        // Register nutrient information in item tooltips.
        EventTooltip.init();

        NutritionalBalance.LOGGER.info(
                "Nutritional Balance client initialized."
        );
    }
}