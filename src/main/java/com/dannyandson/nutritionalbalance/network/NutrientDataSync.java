
package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record NutrientDataSync(
        net.minecraft.item.Item item,
        List<Nutrient> nutrients
) {

    public static final Identifier ID =
            new Identifier(
                    NutritionalBalance.MODID,
                    "nutrient_data_sync"
            );

    public static NutrientDataSync decode(
            net.minecraft.network.PacketByteBuf buf
    ) {
        ItemStack stack = buf.readItemStack();
        net.minecraft.item.Item item = stack.getItem();

        List<Nutrient> nutrients = new ArrayList<>();

        String[] nutrientStrings = buf.readString().split(",");

        for (String nutrientString : nutrientStrings) {
            Nutrient nutrient =
                    WorldNutrients.getByName(nutrientString);

            if (nutrient != null) {
                nutrients.add(nutrient);
            }
        }

        return new NutrientDataSync(item, nutrients);
    }

    public static void encode(
            net.minecraft.network.PacketByteBuf buf,
            NutrientDataSync packet
    ) {
        buf.writeItemStack(packet.item().getDefaultStack());

        StringBuilder nutrientString = new StringBuilder();

        for (Nutrient nutrient : packet.nutrients()) {
            if (nutrientString.length() > 0) {
                nutrientString.append(",");
            }

            nutrientString.append(nutrient.name);
        }

        buf.writeString(nutrientString.toString());
    }

    public static void handle(NutrientDataSync packet) {
        NutritionalBalance.LOGGER.info(
                "[Nutrient Sync Debug] Received {} with nutrients: {}",
                Registries.ITEM.getId(packet.item()),
                packet.nutrients().stream()
                        .map(nutrient -> nutrient.name)
                        .toList()
        );

        WorldNutrients.setItemNutrients(
                packet.item(),
                packet.nutrients()
        );
    }
}