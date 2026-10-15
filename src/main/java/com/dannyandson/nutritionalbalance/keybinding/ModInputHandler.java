
package com.dannyandson.nutritionalbalance.keybinding;

import com.dannyandson.nutritionalbalance.network.GUITrigger;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;

public class ModInputHandler {

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KeyBinding nutrientKey =
                    ModKeyBindings.keyBindings.get("nutrientgui");

            if (nutrientKey != null && nutrientKey.wasPressed()) {
                ModNetworkHandler.sendToServer(new GUITrigger());
            }
        });
    }
}
