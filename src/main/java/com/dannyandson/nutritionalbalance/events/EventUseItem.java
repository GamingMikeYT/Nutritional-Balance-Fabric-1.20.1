package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class EventUseItem {

    public static void onUseItemFinish(ItemStack item, World world, ServerPlayerEntity player) {
        if (item.getItem().getFoodComponent() != null) {

            System.out.println("[Nutrient Debug] EventUseItem received: " + item.getItem());

            INutritionalBalancePlayer iNutritionalBalancePlayer =
                    PlayerNutritionData.getWorldNutritionData()
                            .getNutritionalBalancePlayer(player);

            System.out.println("[Nutrient Debug] Calling consume...");
            iNutritionalBalancePlayer.consume(item, world);
            System.out.println("[Nutrient Debug] consume returned.");

            PlayerNutritionData.getWorldNutritionData().setDirty();
            System.out.println("[Nutrient Debug] World nutrition data marked dirty.");
        }
    }
}
