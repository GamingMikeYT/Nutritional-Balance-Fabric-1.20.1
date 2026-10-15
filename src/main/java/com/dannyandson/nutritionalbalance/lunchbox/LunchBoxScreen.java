package com.dannyandson.nutritionalbalance.lunchbox;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.gui.INutrientGUIScreen;
import com.dannyandson.nutritionalbalance.gui.ModWidget;
import com.dannyandson.nutritionalbalance.gui.NutrientGUIHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.NotNull;

public class LunchBoxScreen
        extends HandledScreen<LunchBoxMenu>
        implements INutrientGUIScreen {

    public static final Identifier GUI =
            new Identifier(
                    NutritionalBalance.MODID,
                    "textures/gui/lunchbox_gui.png"
            );

    public static final Identifier GUI_SLOT =
            new Identifier(
                    NutritionalBalance.MODID,
                    "textures/gui/slot.png"
            );

    private final LunchBoxMenu lunchBoxMenu;
    private final ModWidget[] widgets =
            new ModWidget[Config.LUNCHBOX_SLOT_COUNT.get()];

    public LunchBoxScreen(
            LunchBoxMenu lunchBoxMenu,
            PlayerInventory playerInventory,
            Text title
    ) {
        super(lunchBoxMenu, playerInventory, title);

        this.lunchBoxMenu = lunchBoxMenu;

        this.backgroundWidth = 250;
        this.backgroundHeight = 250;

        this.playerInventoryTitleY = this.y + 156;
        this.playerInventoryTitleX = this.x + 46;

        this.titleY = this.y + 111;
        this.titleX = this.x + 46;
    }

    @Override
    protected void init() {
        super.init();

        NutrientGUIHelper.init(
                this,
                this.backgroundWidth,
                this.backgroundHeight / 2,
                -this.backgroundHeight / 4
        );

        for (int i = 0; i < Config.LUNCHBOX_SLOT_COUNT.get(); i++) {
            int finalI = i;

            addDrawableChild(
                    ModWidget.buildButton(
                            this.x + 46 + (i * 18),
                            this.y + 123,
                            18,
                            10,
                            Text.empty(),
                            button -> toggleActive(finalI)
                    )
            );

            addDrawableChild(
                    new ModWidget(
                            this.x + 46 + (i * 18),
                            this.y + 133,
                            18,
                            18,
                            GUI_SLOT
                    )
            );
        }
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();

        if (this.widgets[0] == null) {
            renderActiveOverlays();
        }
    }

    @Override
    public void resize(
            @NotNull MinecraftClient client,
            int width,
            int height
    ) {
        super.resize(client, width, height);
        renderActiveOverlays();
    }

    @Override
    public void render(
            @NotNull DrawContext drawContext,
            int mouseX,
            int mouseY,
            float delta
    ) {
        super.render(drawContext, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(drawContext, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(
            DrawContext drawContext,
            float delta,
            int mouseX,
            int mouseY
    ) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        drawContext.drawTexture(
                GUI,
                x,
                y,
                0,
                0,
                this.backgroundWidth,
                this.backgroundHeight,
                256,
                256
        );
    }

    private void toggleActive(int slot) {
        lunchBoxMenu.setActiveSlot(slot);
        renderActiveOverlays();
    }

    private void renderActiveOverlays() {
        for (int i = 0; i < Config.LUNCHBOX_SLOT_COUNT.get(); i++) {

            if (widgets[i] != null) {
                remove(widgets[i]);
            }

            int color = 0xFFFFFFFF;

            String slotItem =
                    lunchBoxMenu.slots
                            .get(i)
                            .getStack()
                            .getItem()
                            .getTranslationKey();

            if (
                    lunchBoxMenu.getLunchBoxItemStack().getItem()
                            instanceof LunchBoxItem lunchBoxItem
            ) {
                if (
                        lunchBoxItem.getActiveFoodItemStack(
                                lunchBoxMenu.getLunchBoxItemStack()
                        ) != null
                ) {
                    if (
                            lunchBoxItem.getActiveFoodItemStack(
                                            lunchBoxMenu.getLunchBoxItemStack()
                                    )
                                    .getItem()
                                    .getTranslationKey()
                                    .equals(slotItem)
                    ) {
                        color = 0xFF00FF00;
                    }
                }
            }

            widgets[i] =
                    new ModWidget(
                            this.x + 47 + i * 18,
                            this.y + 124,
                            16,
                            8,
                            color
                    );

            addDrawableChild(widgets[i]);
        }
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
    public ModWidget addModWidget(ModWidget modWidget) {
        return addDrawableChild(modWidget);
    }
}