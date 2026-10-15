package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.gui.PacketOpenGui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModNetworkHandler {

    public static void initServer() {

        ServerPlayNetworking.registerGlobalReceiver(
                GUITrigger.ID,
                (server, player, handler, buf, responseSender) -> {
                    server.execute(() -> {
                        GUITrigger.handle(player);
                    });
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(
                LunchBoxActiveItemSync.ID,
                (server, player, handler, buf, responseSender) -> {
                    LunchBoxActiveItemSync packet =
                            LunchBoxActiveItemSync.decode(buf);

                    server.execute(() -> {
                        LunchBoxActiveItemSync.handle(packet, player);
                    });
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(
                NutrientDataSyncTrigger.ID,
                (server, player, handler, buf, responseSender) -> {
                    NutrientDataSyncTrigger packet =
                            NutrientDataSyncTrigger.decode(buf);

                    server.execute(() -> {
                        NutrientDataSyncTrigger.handle(packet, player);
                    });
                }
        );
    }

    public static void initClient() {

        ClientPlayNetworking.registerGlobalReceiver(
                PlayerSync.ID,
                (client, handler, buf, responseSender) -> {
                    PlayerSync packet =
                            new PlayerSync(
                                    buf.readString(),
                                    buf.readBoolean()
                            );

                    client.execute(() -> {
                        PlayerSync.handle(
                                packet,
                                client.player
                        );
                    });
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(
                NutrientDataSync.ID,
                (client, handler, buf, responseSender) -> {
                    NutrientDataSync packet =
                            NutrientDataSync.decode(buf);

                    client.execute(() -> {
                        NutrientDataSync.handle(packet);
                    });
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(
                NutrientDataBulkSync.ID,
                (client, handler, buf, responseSender) -> {
                    NutrientDataBulkSync packet =
                            NutrientDataBulkSync.decode(buf);

                    client.execute(() -> {
                        NutrientDataBulkSync.handle(packet);
                    });
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(
                PacketOpenGui.ID,
                (client, handler, buf, responseSender) -> {
                    client.execute(PacketOpenGui::handle);
                }
        );
    }

    public static void sendToClient(
            Object packet,
            ServerPlayerEntity player
    ) {
        if (packet instanceof PlayerSync ps) {

            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeString(ps.jsonData());
            buf.writeBoolean(ps.openGUI());

            ServerPlayNetworking.send(
                    player,
                    PlayerSync.ID,
                    buf
            );

        } else if (packet instanceof NutrientDataSync nds) {

            PacketByteBuf buf = PacketByteBufs.create();

            NutrientDataSync.encode(buf, nds);

            ServerPlayNetworking.send(
                    player,
                    NutrientDataSync.ID,
                    buf
            );

        } else if (packet instanceof NutrientDataBulkSync ndbs) {

            PacketByteBuf buf = PacketByteBufs.create();

            NutrientDataBulkSync.encode(buf, ndbs);

            ServerPlayNetworking.send(
                    player,
                    NutrientDataBulkSync.ID,
                    buf
            );

        } else if (packet instanceof PacketOpenGui) {

            PacketByteBuf buf = PacketByteBufs.create();

            ServerPlayNetworking.send(
                    player,
                    PacketOpenGui.ID,
                    buf
            );
        }
    }

    public static void sendToServer(Object packet) {

        if (packet instanceof GUITrigger) {

            PacketByteBuf buf = PacketByteBufs.create();

            ClientPlayNetworking.send(
                    GUITrigger.ID,
                    buf
            );

        } else if (packet instanceof LunchBoxActiveItemSync lbais) {

            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeString(lbais.activeItemId());

            ClientPlayNetworking.send(
                    LunchBoxActiveItemSync.ID,
                    buf
            );

        } else if (packet instanceof NutrientDataSyncTrigger ndst) {

            PacketByteBuf buf = PacketByteBufs.create();

            NutrientDataSyncTrigger.encode(buf, ndst);

            ClientPlayNetworking.send(
                    NutrientDataSyncTrigger.ID,
                    buf
            );
        }
    }
}