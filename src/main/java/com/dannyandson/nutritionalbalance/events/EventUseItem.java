package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class EventUseItem {

    public static void onUseItemFinish(ItemStack item, World world, ServerPlayerEntity player) {
        if (item.getItem().getFoodComponent() != null) {



            INutritionalBalancePlayer iNutritionalBalancePlayer =
                    PlayerNutritionData.getWorldNutritionData()
                            .getNutritionalBalancePlayer(player);
            iNutritionalBalancePlayer.consume(item, world);
            PlayerNutritionData.getWorldNutritionData().setDirty();

        }
    }
}
