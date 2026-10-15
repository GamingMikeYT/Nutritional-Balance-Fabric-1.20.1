package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.StringJoiner;

public class CommandGetNutrients implements Command<ServerCommandSource> {

    private static final CommandGetNutrients CMD = new CommandGetNutrients();

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        String feedback = "Must be run on client.";

        if (context.getSource().getEntity() instanceof ServerPlayerEntity player) {
            INutritionalBalancePlayer iNutritionalBalancePlayer =
                    PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(player);

            StringJoiner stringJoiner = new StringJoiner("\n");
            stringJoiner.add("Player Nutrition:");

            for (IPlayerNutrient nutrient : iNutritionalBalancePlayer.getPlayerNutrients()) {
                stringJoiner.add(
                        nutrient.getNutrient().name + ": "
                                + (((float) Math.round(nutrient.getValue() * 10)) / 10)
                                + " " + nutrient.getStatus().name()
                );
            }

            stringJoiner.add("Overall Status: " + iNutritionalBalancePlayer.getStatus().name());

            context.getSource().sendFeedback(
                    () -> Text.literal(stringJoiner.toString()),
                    false
            );
        }

        return 0;
    }

    public static ArgumentBuilder<ServerCommandSource, ?> register(
            CommandDispatcher<ServerCommandSource> dispatcher) {

        return CommandManager.literal("get_nutrients")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }
}