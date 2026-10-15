package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import java.util.List;

public class EventRightClickBlock {

    public static void init() {

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {

            BlockState blockState = world.getBlockState(hitResult.getBlockPos());
            Block block = blockState.getBlock();

            if (block instanceof CakeBlock) {

                if (!world.isClient && player.canConsume(false)) {

                    INutritionalBalancePlayer iNutritionalBalancePlayer =
                            PlayerNutritionData.getWorldNutritionData()
                                    .getNutritionalBalancePlayer(player);

                    Item cakeItem = block.asItem();

                    List<Nutrient> nutrients =
                            WorldNutrients.getNutrients(cakeItem, world);

                    for (Nutrient nutrient : nutrients) {

                        float nutrientunits =
                                2.4f
                                        * Config.NUTRIENT_INCREMENT_RATE.get().floatValue()
                                        / nutrients.size();

                        iNutritionalBalancePlayer
                                .getPlayerNutrientByName(nutrient.name)
                                .changeValue(nutrientunits);

                        PlayerNutritionData.getWorldNutritionData().setDirty();
                    }
                }
            }

            return ActionResult.PASS;
        });
    }
}