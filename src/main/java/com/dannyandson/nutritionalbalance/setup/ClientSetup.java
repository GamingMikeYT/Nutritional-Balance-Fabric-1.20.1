package com.dannyandson.nutritionalbalance.setup;

import com.dannyandson.nutritionalbalance.events.EventNutrientButton;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxItemRenderer;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxScreen;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.item.ItemGroups;

public class ClientSetup {

    public static void init() {

        // Register the nutrient button on the inventory screen
        EventNutrientButton.init();

        // Register the lunchbox screen
        HandledScreens.register(
                Registration.LUNCHBOX_MENU_TYPE,
                LunchBoxScreen::new
        );

        // Add the lunchbox to the Food & Drink creative tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register(entries -> entries.add(Registration.LUNCHBOX_ITEM));

        // Register the lunchbox's custom 3D renderer
        BuiltinItemRendererRegistry.INSTANCE.register(
                Registration.LUNCHBOX_ITEM,
                new LunchBoxItemRenderer()
        );

        // Load the lunchbox 3D model
        ModelLoadingPlugin.register(context -> {
            context.addModels(LunchBoxItemRenderer.LUNCHBOX_3D_MODEL);
        });
    }
}