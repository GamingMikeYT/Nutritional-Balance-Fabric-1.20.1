package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ModCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> cmdHD = dispatcher.register(
                CommandManager.literal(NutritionalBalance.MODID)
                        .then(CommandGetNutrients.register(dispatcher))
                        .then(CommandSyncClient.register(dispatcher))
                        .then(CommandGetUnassignedFoods.register(dispatcher))
                        .then(CommandResetPlayerEffects.register(dispatcher))
                        .then(CommandSetNutrient.register(dispatcher))
        );

        dispatcher.register(
                CommandManager.literal("nb").redirect(cmdHD)
        );
    }

}
