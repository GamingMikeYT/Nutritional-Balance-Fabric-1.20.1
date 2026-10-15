package com.dannyandson.nutritionalbalance.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModWidget extends ClickableWidget {

    public enum HAlignment {
        LEFT, CENTER, RIGHT
    }

    public enum VAlignment {
        TOP, MIDDLE, BOTTOM
    }

    private HAlignment halignment = HAlignment.LEFT;
    private VAlignment valignment = VAlignment.TOP;
    private float scale = 1.0f;
    private int color;
    private int bgcolor = -1;
    private int textWidth;
    private int textHeight;
    private Text toolTipTextComponent;
    private Identifier texture;

    public ModWidget(int x, int y, int width, int height, Text title, int textColor, int bgColor) {
        super(x, y, width, height, title);
        this.color = textColor;
        this.bgcolor = bgColor;

        if (!title.getString().isEmpty()) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            this.textWidth = textRenderer.getWidth(title);
            this.textHeight = textRenderer.fontHeight;
        }
    }

    public ModWidget(int x, int y, int width, int height, Text title, int textColor) {
        this(x, y, width, height, title, textColor, -1);
    }

    public ModWidget(int x, int y, int width, int height, Text title) {
        this(x, y, width, height, title, 0xFFFFFFFF, -1);
    }

    public ModWidget(int x, int y, int width, int height, int bgColor) {
        this(x, y, width, height, Text.empty(), 0xFFFFFFFF, bgColor);
    }

    public ModWidget(int x, int y, int width, int height, Identifier texture) {
        this(x, y, width, height, Text.empty(), 0xFFFFFFFF, -1);
        this.texture = texture;
    }

    public ModWidget setTextHAlignment(HAlignment alignment) {
        this.halignment = alignment;
        return this;
    }

    public ModWidget setTextVAlignment(VAlignment alignment) {
        this.valignment = alignment;
        return this;
    }

    public ModWidget setToolTip(Text textComponent) {
        this.toolTipTextComponent = textComponent;
        return this;
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public void renderButton(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if (!visible) {
            return;
        }

        // Draw the background first so it cannot cover the text or texture.
        if (bgcolor != -1) {
            drawContext.fill(
                    getX(),
                    getY(),
                    getX() + width,
                    getY() + height,
                    bgcolor
            );
        }

        if (texture != null) {
            drawContext.drawTexture(
                    texture,
                    getX(),
                    getY(),
                    0,
                    0,
                    width,
                    height,
                    width,
                    height
            );
        } else if (!getMessage().getString().isEmpty()) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            int drawX;
            int drawY;

            switch (halignment) {
                case CENTER:
                    drawX = getX() + (width - textWidth) / 2;
                    break;

                case RIGHT:
                    drawX = getX() + width - textWidth;
                    break;

                case LEFT:
                default:
                    drawX = getX();
                    break;
            }

            switch (valignment) {
                case MIDDLE:
                    drawY = getY() + (height - textHeight) / 2;
                    break;

                case BOTTOM:
                    drawY = getY() + height - textHeight;
                    break;

                case TOP:
                default:
                    drawY = getY();
                    break;
            }

            if (scale != 1.0f) {
                drawContext.getMatrices().push();
                drawContext.getMatrices().translate(drawX, drawY, 0);
                drawContext.getMatrices().scale(scale, scale, 1.0f);

                drawContext.drawText(
                        textRenderer,
                        getMessage(),
                        0,
                        0,
                        color,
                        false
                );

                drawContext.getMatrices().pop();
            } else {
                drawContext.drawText(
                        textRenderer,
                        getMessage(),
                        drawX,
                        drawY,
                        color,
                        false
                );
            }
        }

        if (toolTipTextComponent != null
                && mouseX >= getX()
                && mouseX <= getX() + width
                && mouseY >= getY()
                && mouseY <= getY() + height) {
            renderHoverToolTip(drawContext, mouseX, mouseY);
        }
    }

    public void renderHoverToolTip(DrawContext drawContext, int mouseX, int mouseY) {
        if (toolTipTextComponent == null) {
            return;
        }

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        int tooltipWidth = textRenderer.getWidth(toolTipTextComponent);
        int tooltipHeight = textRenderer.fontHeight;

        drawContext.fill(
                mouseX,
                mouseY + 10,
                mouseX + tooltipWidth + 4,
                mouseY + 10 + tooltipHeight + 4,
                0xCC000000
        );

        drawContext.fill(
                mouseX + 1,
                mouseY + 11,
                mouseX + tooltipWidth + 3,
                mouseY + 10 + tooltipHeight + 3,
                0xFF555555
        );

        drawContext.drawText(
                textRenderer,
                toolTipTextComponent,
                mouseX + 3,
                mouseY + 13,
                0xFFFEFEFE,
                false
        );
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    public static ButtonWidget buildButton(
            Integer xPos,
            Integer yPos,
            Integer width,
            Integer height,
            Text component,
            ButtonWidget.PressAction onPress
    ) {
        return ButtonWidget.builder(component, onPress)
                .dimensions(xPos, yPos, width, height)
                .build();
    }
}
