package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * One-shot packet sent from the server to a client to populate the entire local nutrient cache, so the
 * client never needs to round-trip per-item NutrientDataSyncTrigger requests during JEI/REI/EMI tooltip
 * indexing or general inventory hovering. Sent on player join and re-broadcast after datapack reloads.
 *
 * Wire format: VarInt count, then for each entry: Identifier (item id) + String (comma-separated
 * nutrient names; empty string means "this item is known to have no nutrients").
 */
public record NutrientDataBulkSync(
        Map<Identifier, List<String>> data
) {

    public static final Identifier ID =
            new Identifier(
                    NutritionalBalance.MODID,
                    "nutrient_data_bulk_sync"
            );

    public static NutrientDataBulkSync decode(PacketByteBuf buf) {
        int count = buf.readVarInt();

        Map<Identifier, List<String>> data =
                new HashMap<>(count);

        for (int i = 0; i < count; i++) {
            Identifier key = buf.readIdentifier();
            String csv = buf.readString();

            List<String> values =
                    csv.isEmpty()
                            ? new ArrayList<>()
                            : new ArrayList<>(Arrays.asList(csv.split(",")));

            data.put(key, values);
        }

        return new NutrientDataBulkSync(data);
    }

    public static void encode(
            PacketByteBuf buf,
            NutrientDataBulkSync packet
    ) {
        buf.writeVarInt(packet.data().size());

        for (Map.Entry<Identifier, List<String>> entry :
                packet.data().entrySet()) {

            buf.writeIdentifier(entry.getKey());
            buf.writeString(String.join(",", entry.getValue()));
        }
    }

    public static void handle(NutrientDataBulkSync packet) {
        WorldNutrients.applyBulkSync(packet.data());
    }
}