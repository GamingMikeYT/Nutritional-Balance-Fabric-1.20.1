package com.dannyandson.nutritionalbalance.gui;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import net.minecraft.util.Identifier;

public record PacketOpenGui() {

    public static final Identifier ID =
            Identifier.of(NutritionalBalance.MODID, "open_gui");

    public static void handle() {
        NutrientGUI.open();
    }
}