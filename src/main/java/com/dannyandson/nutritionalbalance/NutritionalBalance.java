
package com.dannyandson.nutritionalbalance;

import com.dannyandson.nutritionalbalance.commands.ModCommands;
import com.dannyandson.nutritionalbalance.events.EventPlayerClone;
import com.dannyandson.nutritionalbalance.events.EventPlayerJoin;
import com.dannyandson.nutritionalbalance.events.EventPlayerTick;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.NutrientDataBulkSync;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.dannyandson.nutritionalbalance.setup.Registration;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraftforge.fml.config.ModConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NutritionalBalance implements ModInitializer {

    public static final String MODID = "nutritionalbalance";
    public static final Logger LOGGER = LogManager.getLogger();

    public static boolean modEffectsLoaded = false;

    @Override
    public void onInitialize() {

        // Register items, status effects, and menus before registries freeze.
        Registration.register();

        // Register configuration files through Forge Config API Port.
        ForgeConfigRegistry.INSTANCE.register(
                MODID,
                ModConfig.Type.SERVER,
                Config.SERVER_CONFIG
        );

        ForgeConfigRegistry.INSTANCE.register(
                MODID,
                ModConfig.Type.CLIENT,
                Config.CLIENT_CONFIG
        );

        // Register server-side network packet receivers.
        ModNetworkHandler.initServer();

        // Player clone / death handling.
        EventPlayerClone.init();

        // Player join.
        EventPlayerJoin.init();

        // Player tick and nutrient decay.
        EventPlayerTick.init();

        // Commands.
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        ModCommands.register(dispatcher)
        );

        // Server started.
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            PlayerNutritionData.init(server.getOverworld());

            // Pre-resolve nutrients for every food item during world load.
            WorldNutrients.prewarm(server.getOverworld());
        });

        // Sync nutrient data when a player joins or datapack contents are synchronized.
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            WorldNutrients.prewarm(player.getServer().getOverworld());

            NutrientDataBulkSync packet =
                    new NutrientDataBulkSync(WorldNutrients.snapshotForSync());

            ModNetworkHandler.sendToClient(packet, player);
        });

        /*
         * Fabric's datapack/tag reload handling still needs to be
         * implemented separately.
         */

        LOGGER.info("{} initialized.", MODID);
    }
}
