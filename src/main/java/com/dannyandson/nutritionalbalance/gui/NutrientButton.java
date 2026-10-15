package com.dannyandson.nutritionalbalance.gui;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.network.GUITrigger;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class NutrientButton extends ClickableWidget {

    private final Identifier buttonGUI =
            Identifier.of(NutritionalBalance.MODID, "textures/gui/nutrient_button.png");

    private final Identifier buttonGUIHover =
            Identifier.of(NutritionalBalance.MODID, "textures/gui/nutrient_button_hover.png");

    private final InventoryScreen gui;

    public NutrientButton(InventoryScreen gui, Text title) {
        super(
                getGuiX(gui) + Config.NUTRIENT_BUTTON_X.get(),
                getGuiY(gui) + Config.NUTRIENT_BUTTON_Y.get(),
                20,
                18,
                title
        );

        this.gui = gui;
        updateLocation();
    }

    private static int getGuiX(InventoryScreen gui) {
        return (gui.width - 176) / 2;
    }

    private static int getGuiY(InventoryScreen gui) {
        return (gui.height - 166) / 2;
    }

    private void updateLocation() {
        this.setX(getGuiX(gui) + Config.NUTRIENT_BUTTON_X.get());
        this.setY(getGuiY(gui) + Config.NUTRIENT_BUTTON_Y.get());
        this.width = 20;
        this.height = 18;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        ModNetworkHandler.sendToServer(new GUITrigger());
    }

    @Override
    protected void renderButton(
            DrawContext drawContext,
            int mouseX,
            int mouseY,
            float delta
    ) {
        if (visible) {
            updateLocation();

            Identifier texture;

            if (mouseX > getX()
                    && mouseX < getX() + width
                    && mouseY > getY()
                    && mouseY < getY() + height) {
                texture = buttonGUIHover;
            } else {
                texture = buttonGUI;
            }

            drawContext.drawTexture(
                    texture,
                    getX(),
                    getY(),
                    0,
                    0,
                    width,
                    height,
                    256,
                    256
            );
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}