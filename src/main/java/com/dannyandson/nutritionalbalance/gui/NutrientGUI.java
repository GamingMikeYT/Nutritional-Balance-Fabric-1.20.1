package com.dannyandson.nutritionalbalance.gui;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.keybinding.ModKeyBindings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class NutrientGUI extends Screen implements INutrientGUIScreen {

    private static final int WIDTH = 250;
    private static final int HEIGHT = 155;

    private static final Identifier GUI = Identifier.of(
            NutritionalBalance.MODID,
            "textures/gui/nutrient_gui.png"
    );

    public NutrientGUI() {
        super(Text.translatable("nutritionalbalance.nutrients"));
    }

    @Override
    protected void init() {
        NutrientGUIHelper.init(this, WIDTH, HEIGHT);

        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;

        addDrawableChild(
                ModWidget.buildButton(
                        relX + 85,
                        relY + 128,
                        80,
                        20,
                        Text.translatable("nutritionalbalance.close"),
                        button -> close()
                )
        );
    }

    @Override
    public ModWidget addModWidget(ModWidget modWidget) {
        return addDrawableChild(modWidget);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public MinecraftClient getMinecraft() {
        return MinecraftClient.getInstance();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {
        KeyBinding nutrientKey =
                ModKeyBindings.keyBindings.get("nutrientgui");

        if (nutrientKey != null
                && (nutrientKey.matchesKey(keyCode, scanCode)
                || client.options.inventoryKey.matchesKey(keyCode, scanCode))) {
            close();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(null);
    }

    @Override
    public void renderBackground(DrawContext drawContext) {
        // Darken the world behind the nutrition screen.
        drawContext.fill(
                0,
                0,
                this.width,
                this.height,
                0xC0101010
        );
    }

    @Override
    public void render(
            DrawContext drawContext,
            int mouseX,
            int mouseY,
            float delta
    ) {
        // 1. Draw the dark overlay.
        renderBackground(drawContext);

        // 2. Draw the GUI background texture.
        int relX = (this.width - WIDTH) / 2;
        int relY = (this.height - HEIGHT) / 2;

        drawContext.drawTexture(
                GUI,
                relX,
                relY,
                0,
                0,
                WIDTH,
                HEIGHT,
                256,
                256
        );

        // 3. Draw nutrient bars, buttons, and other widgets on top.
        super.render(drawContext, mouseX, mouseY, delta);
    }

    public static void open() {
        MinecraftClient.getInstance().setScreen(new NutrientGUI());
    }
}