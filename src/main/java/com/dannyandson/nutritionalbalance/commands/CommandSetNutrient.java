package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommandSetNutrient {

    public static ArgumentBuilder<ServerCommandSource, ?> register(
            CommandDispatcher<ServerCommandSource> dispatcher) {

        return CommandManager.literal("set_nutrient")
                .requires(cs -> cs.hasPermissionLevel(3))
                .then(CommandManager.argument(
                                "nutrient",
                                StringArgumentType.word())
                        .suggests(CommandSetNutrient::suggestNutrients)
                        .then(CommandManager.argument(
                                        "value",
                                        FloatArgumentType.floatArg(0.0f, 180f))
                                .executes(CommandSetNutrient::setNutrients)));
    }

    public static int setNutrients(
            CommandContext<ServerCommandSource> context)
            throws CommandSyntaxException {

        if (context.getSource().getEntity() instanceof ServerPlayerEntity player) {

            String nutrientName =
                    StringArgumentType.getString(context, "nutrient");

            if (!getNutrientList().contains(nutrientName)) {
                throw new SimpleCommandExceptionType(
                        Text.literal("Invalid nutrient argument")
                ).create();
            }

            INutritionalBalancePlayer iNutritionalBalancePlayer =
                    PlayerNutritionData.getWorldNutritionData()
                            .getNutritionalBalancePlayer(player);

            float value =
                    FloatArgumentType.getFloat(context, "value");

            iNutritionalBalancePlayer
                    .getPlayerNutrientByName(nutrientName)
                    .setValue(value);
        }

        return 0;
    }

    private static CompletableFuture<Suggestions> suggestNutrients(
            CommandContext<ServerCommandSource> context,
            SuggestionsBuilder builder) {

        return net.minecraft.command.CommandSource.suggestMatching(
                getNutrientList(),
                builder
        );
    }

    private static List<String> getNutrientList() {

        List<String> nutrientList = new ArrayList<>();

        List<Nutrient> nutrients = WorldNutrients.get();

        for (Nutrient nutrient : nutrients) {
            nutrientList.add(nutrient.name);
        }

        return nutrientList;
    }
}