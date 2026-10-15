package com.dannyandson.nutritionalbalance.keybinding;

import com.dannyandson.nutritionalbalance.NutritionalBalance;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.HashMap;
import java.util.Map;

public class ModKeyBindings {

    public static Map<String, KeyBinding> keyBindings = new HashMap<>();

    public static void init() {
        KeyBinding nutritionguikeybind = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key." + NutritionalBalance.MODID + ".opennutrientgui",
                        InputUtil.Type.KEYSYM,
                        78,
                        "Nutritional Balance"
                )
        );

        keyBindings.put("nutrientgui", nutritionguikeybind);
    }
}