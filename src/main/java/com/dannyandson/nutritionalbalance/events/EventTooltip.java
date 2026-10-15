package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.StringJoiner;

public class EventTooltip {

    public static void init() {

        ItemTooltipCallback.EVENT.register((itemStack, context, tooltip) -> {

            Item item = itemStack.getItem();

            World world = MinecraftClient.getInstance().world;

            if (world == null) {
                NutritionalBalance.LOGGER.error(
                        "Exception during attempt to access tooltip without a world."
                );
                return;
            }

            FoodComponent foodComponent = item.getFoodComponent();

            boolean isCake =
                    item instanceof BlockItem blockItem
                            && blockItem.getBlock() instanceof CakeBlock;

            if (foodComponent != null || isCake) {

                StringJoiner stringJoiner = new StringJoiner(", ");

                try {

                    List<Nutrient> nutrients =
                            WorldNutrients.getNutrients(itemStack, world);

                    if (nutrients != null) {

                        for (Nutrient nutrient : nutrients) {
                            stringJoiner.add(nutrient.getLocalizedName());
                        }

                        if (stringJoiner.length() > 0) {

                            String NUvalue = "";

                            if (foodComponent != null) {
                                NUvalue =
                                        " ("
                                                + ((float) Math.round(
                                                WorldNutrients.getEffectiveFoodQuality(
                                                        foodComponent,
                                                        nutrients.size()
                                                ) * 10
                                        )) / 10
                                                + "NU)";
                            }

                            tooltip.add(
                                    Text.literal(
                                            "§7"
                                                    + I18n.translate(
                                                    "nutritionalbalance.nutrients"
                                            )
                                                    + ": §2"
                                                    + stringJoiner
                                                    + "§7"
                                                    + NUvalue
                                                    + "§r"
                                    )
                            );
                        }
                    }

                    /*
                     * Preserve the original developer-only tag display.
                     */
                    if (MinecraftClient.getInstance().player != null
                            && MinecraftClient.getInstance()
                            .player
                            .getDisplayName()
                            .getString()
                            .equals("Dev")
                            && MinecraftClient.getInstance()
                            .options
                            .advancedItemTooltips) {

                        if (Screen.hasShiftDown()) {

                            for (TagKey<Item> tagKey :
                                    itemStack.streamTags().toList()) {

                                tooltip.add(
                                        Text.literal(
                                                "#" + tagKey.id().toString()
                                        )
                                );
                            }

                        } else {

                            tooltip.add(
                                    Text.literal(
                                            "§8--Hold shift for tag info--§r"
                                    )
                            );
                        }
                    }

                } catch (Exception e) {

                    NutritionalBalance.LOGGER.error(
                            "Exception thrown while adding nutrient info for '"
                                    + itemStack.getName().getString()
                                    + "' to tooltips: "
                                    + e.getMessage()
                    );
                }
            }
        });
    }
}