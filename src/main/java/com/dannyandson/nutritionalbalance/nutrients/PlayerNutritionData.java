package com.dannyandson.nutritionalbalance.nutrients;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.capabilities.DefaultNutritionalBalancePlayer;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class PlayerNutritionData extends PersistentState {

    private static PlayerNutritionData worldNutritionData;

    public static void init(ServerWorld serverWorld) {
        PersistentStateManager stateManager =
                serverWorld.getPersistentStateManager();

        worldNutritionData = stateManager.getOrCreate(
                PlayerNutritionData::load,
                PlayerNutritionData::new,
                NutritionalBalance.MODID
        );

        worldNutritionData.markDirty();
    }

    public static PlayerNutritionData getWorldNutritionData() {
        if (worldNutritionData == null) {
            worldNutritionData = new PlayerNutritionData();
        }

        return worldNutritionData;
    }

    Map<String, INutritionalBalancePlayer> playerUUIDDataMap =
            new HashMap<>();

    PlayerNutritionData() {
    }

    public static PlayerNutritionData load(NbtCompound nbt) {
        PlayerNutritionData data =
                new PlayerNutritionData();

        for (String uuid : nbt.getKeys()) {

            DefaultNutritionalBalancePlayer nutritionalBalancePlayer =
                    new DefaultNutritionalBalancePlayer();

            NbtCompound playerNBT =
                    nbt.getCompound(uuid);

            for (Nutrient nutrient : WorldNutrients.get()) {

                nutritionalBalancePlayer
                        .getPlayerNutrientByName(nutrient.name)
                        .setValue(
                                playerNBT.getFloat(nutrient.name)
                        );
            }

            data.playerUUIDDataMap.put(
                    uuid,
                    nutritionalBalancePlayer
            );
        }

        return data;
    }

    @Override
    public NbtCompound writeNbt(
            NbtCompound nbt
    ) {
        for (
                Map.Entry<String, INutritionalBalancePlayer> entry :
                playerUUIDDataMap.entrySet()
        ) {

            NbtCompound playerNBT =
                    new NbtCompound();

            for (
                    IPlayerNutrient playerNutrient :
                    entry.getValue().getPlayerNutrients()
            ) {

                playerNBT.putFloat(
                        playerNutrient.getNutrient().name,
                        playerNutrient.getValue()
                );
            }

            nbt.put(
                    entry.getKey(),
                    playerNBT
            );
        }

        return nbt;
    }

    public INutritionalBalancePlayer getNutritionalBalancePlayer(
            PlayerEntity player
    ) {
        String uuid =
                player.getUuidAsString();

        if (!playerUUIDDataMap.containsKey(uuid)) {
            playerUUIDDataMap.put(
                    uuid,
                    new DefaultNutritionalBalancePlayer()
            );

            markDirty();
        }

        return playerUUIDDataMap.get(uuid);
    }

    public void setDirty() {
        markDirty();
    }
}