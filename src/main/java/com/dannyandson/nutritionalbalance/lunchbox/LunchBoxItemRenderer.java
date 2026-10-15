package com.dannyandson.nutritionalbalance.lunchbox;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class LunchBoxItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    /*
     * This is the model that tells Minecraft to use this custom renderer.
     * models/item/lunchbox_3d.json
     */
    public static final Identifier LUNCHBOX_3D_MODEL =
            Identifier.of(NutritionalBalance.MODID, "item/lunchbox_3d");

    /*
     * This is the actual generated model used for the lunchbox body.
     * models/item/lunchbox.json
     */
    private static final Identifier LUNCHBOX_BASE_MODEL =
            Identifier.of(NutritionalBalance.MODID, "item/lunchbox");

    private BakedModel baseModel;

    private BakedModel getBaseModel() {
        if (baseModel == null) {
            baseModel = MinecraftClient.getInstance()
                    .getBakedModelManager()
                    .getModel(LUNCHBOX_BASE_MODEL);
        }

        return baseModel;
    }

    @Override
    public void render(
            ItemStack stack,
            ModelTransformationMode mode,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();

        /*
         * Render the actual lunchbox body.
         */
        BakedModel model = getBaseModel();

        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);

        itemRenderer.renderItem(
                stack,
                ModelTransformationMode.NONE,
                false,
                matrices,
                vertexConsumers,
                light,
                overlay,
                model
        );

        matrices.pop();

        /*
         * Render the selected food item on both faces of the lunchbox.
         */
        if (stack.getItem() instanceof LunchBoxItem lunchBoxItem) {

            ItemStack activeStack =
                    lunchBoxItem.getActiveFoodItemStack(stack);

            if (activeStack != null) {

                /*
                 * Front face
                 */
                matrices.push();

                matrices.translate(0.5, 0.35, 0.55);
                matrices.scale(0.5f, 0.5f, 0.5f);

                itemRenderer.renderItem(
                        activeStack,
                        ModelTransformationMode.FIXED,
                        light,
                        overlay,
                        matrices,
                        vertexConsumers,
                        minecraft.world,
                        0
                );

                matrices.pop();

                /*
                 * Back face
                 */
                matrices.push();

                matrices.translate(0.5, 0.35, 0.45);

                matrices.multiply(
                        RotationAxis.POSITIVE_Y.rotationDegrees(180)
                );

                matrices.scale(0.5f, 0.5f, 0.5f);

                itemRenderer.renderItem(
                        activeStack,
                        ModelTransformationMode.FIXED,
                        light,
                        overlay,
                        matrices,
                        vertexConsumers,
                        minecraft.world,
                        0
                );

                matrices.pop();
            }
        }
    }
}