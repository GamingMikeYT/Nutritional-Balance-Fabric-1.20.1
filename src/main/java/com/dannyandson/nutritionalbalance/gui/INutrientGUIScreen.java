package com.dannyandson.nutritionalbalance.gui;

import net.minecraft.client.MinecraftClient;

public interface INutrientGUIScreen {

    public int getWidth();
    public int getHeight();
    public ModWidget addModWidget(ModWidget modWidget);

    public MinecraftClient getMinecraft();
}