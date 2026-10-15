package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.item.Item;

import java.util.StringJoiner;

public class CommandGetUnassignedFoods implements Command<ServerCommandSource> {

    private static final CommandGetUnassignedFoods CMD = new CommandGetUnassignedFoods();

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        StringJoiner stringJoiner = new StringJoiner("\n");

        for (Item item : Registries.ITEM) {
            if (item.getFoodComponent() != null
                    && WorldNutrients.getNutrients(item, context.getSource().getWorld()).size() == 0) {

                stringJoiner.add(Registries.ITEM.getId(item).toString());
            }
        }

        context.getSource().sendFeedback(
                () -> Text.literal(stringJoiner.toString()),
                false
        );

        return 0;
    }

    public static ArgumentBuilder<ServerCommandSource, ?> register(
            CommandDispatcher<ServerCommandSource> dispatcher) {

        return CommandManager.literal("get_unassigned_foods")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }
}
