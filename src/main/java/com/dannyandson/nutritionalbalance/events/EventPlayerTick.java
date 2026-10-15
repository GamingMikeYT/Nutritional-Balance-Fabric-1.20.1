package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.effects.ModMobAffects;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.PlayerSync;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import com.dannyandson.nutritionalbalance.setup.Registration;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class EventPlayerTick {

    public static void init() {

        /*
         * Fabric does not have NeoForge's PlayerTickEvent.
         *
         * SERVER_TICK runs once per server tick, so we iterate through
         * the server's players and perform the same per-player logic.
         */
        ServerTickEvents.END_SERVER_TICK.register(server -> {

            for (ServerPlayerEntity playerEntity : server.getPlayerManager().getPlayerList()) {

                INutritionalBalancePlayer iNutritionalBalancePlayer =
                        PlayerNutritionData.getWorldNutritionData()
                                .getNutritionalBalancePlayer(playerEntity);

                if (!NutritionalBalance.modEffectsLoaded) {
                    ModMobAffects.loadModEffects();
                }

                float playerSaturation =
                        playerEntity.getHungerManager().getSaturationLevel();

                int playerFoodLevel =
                        playerEntity.getHungerManager().getFoodLevel();

                float foodpoints = playerSaturation + playerFoodLevel;

                System.out.println("[Nutrient Decay Debug] EventPlayerTick is calling processSaturationChange()");
                iNutritionalBalancePlayer.processSaturationChange(foodpoints);

                /*
                 * 200 ticks = 10 seconds, same as the original.
                 */
                if (playerEntity.age % 200 == 0) {

                    IPlayerNutrient.NutrientStatus cachedStatus =
                            iNutritionalBalancePlayer.getCachedStatus();

                    IPlayerNutrient.NutrientStatus currentStatus =
                            iNutritionalBalancePlayer.getStatus();

                    StatusEffect nourishedEffect =
                            Registration.NOURISHED_EFFECT;

                    StatusEffect malnourishedEffect =
                            Registration.MALNOURISHED_EFFECT;

                    StatusEffect engorgedEffect =
                            Registration.ENGORGED_EFFECT;

                    StatusEffectInstance nourished =
                            playerEntity.getStatusEffect(nourishedEffect);

                    StatusEffectInstance malnourished =
                            playerEntity.getStatusEffect(malnourishedEffect);

                    StatusEffectInstance engorged =
                            playerEntity.getStatusEffect(engorgedEffect);

                    if (currentStatus == IPlayerNutrient.NutrientStatus.ENGORGED) {

                        if (nourished != null)
                            playerEntity.removeStatusEffect(nourishedEffect);

                        if (malnourished != null)
                            playerEntity.removeStatusEffect(malnourishedEffect);

                        if (engorged == null) {
                            playerEntity.addStatusEffect(
                                    new StatusEffectInstance(
                                            engorgedEffect,
                                            Integer.MAX_VALUE,
                                            0,
                                            true,
                                            false,
                                            true
                                    )
                            );
                        }

                    } else if (currentStatus == IPlayerNutrient.NutrientStatus.MALNOURISHED) {

                        if (nourished != null)
                            playerEntity.removeStatusEffect(nourishedEffect);

                        if (malnourished == null) {
                            playerEntity.addStatusEffect(
                                    new StatusEffectInstance(
                                            malnourishedEffect,
                                            Integer.MAX_VALUE,
                                            0,
                                            true,
                                            false,
                                            true
                                    )
                            );
                        }

                        if (engorged != null)
                            playerEntity.removeStatusEffect(engorgedEffect);

                    } else if (currentStatus == IPlayerNutrient.NutrientStatus.ON_TARGET) {

                        if (nourished == null) {
                            playerEntity.addStatusEffect(
                                    new StatusEffectInstance(
                                            nourishedEffect,
                                            Integer.MAX_VALUE,
                                            0,
                                            true,
                                            false,
                                            true
                                    )
                            );
                        }

                        if (malnourished != null)
                            playerEntity.removeStatusEffect(malnourishedEffect);

                        if (engorged != null)
                            playerEntity.removeStatusEffect(engorgedEffect);

                    } else {

                        if (nourished != null)
                            playerEntity.removeStatusEffect(nourishedEffect);

                        if (malnourished != null)
                            playerEntity.removeStatusEffect(malnourishedEffect);

                        if (engorged != null)
                            playerEntity.removeStatusEffect(engorgedEffect);
                    }

                    /*
                     * Synchronize the nutrient data/status with the client
                     * whenever the cached status changes.
                     */
                    if (cachedStatus != currentStatus) {

                        if (Config.SHOW_THRESHOLD_TOAST.get()) {
                            ClientHelpers.showStatusToast(currentStatus.name());
                        }

                        PlayerSync playerSync =
                                new PlayerSync(iNutritionalBalancePlayer);

                        ModNetworkHandler.sendToClient(
                                playerSync,
                                playerEntity
                        );
                    }
                }
            }
        });
    }
}
