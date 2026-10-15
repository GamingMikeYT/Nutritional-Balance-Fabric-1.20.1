package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.effects.ModMobAffects;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.command.CommandManager;

public class CommandResetPlayerEffects implements Command<ServerCommandSource> {

    private static final CommandResetPlayerEffects CMD = new CommandResetPlayerEffects();

    @Override
    public int run(CommandContext<ServerCommandSource> context) {

        if (context.getSource().getEntity() instanceof ServerPlayerEntity player) {
            ModMobAffects.resetPlayerEffects(player);
        }
        return 0;
    }

    public static ArgumentBuilder<ServerCommandSource, ?> register(CommandDispatcher<ServerCommandSource> dispatcher) {
        return CommandManager.literal("reset_player_effect")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }
}