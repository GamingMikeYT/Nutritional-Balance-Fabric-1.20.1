package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.PlayerSync;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class CommandSyncClient implements Command<ServerCommandSource> {

    private static final CommandSyncClient CMD = new CommandSyncClient();

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        final PlayerSync[] playerSync = new PlayerSync[1];

        if (context.getSource().getEntity() instanceof ServerPlayerEntity player) {
            INutritionalBalancePlayer iNutritionalBalancePlayer =
                    PlayerNutritionData.getWorldNutritionData()
                            .getNutritionalBalancePlayer(player);

            playerSync[0] = new PlayerSync(iNutritionalBalancePlayer);

            ModNetworkHandler.sendToClient(
                    playerSync[0],
                    context.getSource().getPlayerOrThrow()
            );
        }

        return 0;
    }

    public static ArgumentBuilder<ServerCommandSource, ?> register(
            CommandDispatcher<ServerCommandSource> dispatcher) {

        return CommandManager.literal("sync")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }
}
