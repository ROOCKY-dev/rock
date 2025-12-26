package com.roocky.foundation.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;

public class PacketHelper {
    public static void sendVisuals(ServerPlayerEntity player, ChunkPos chunk, int color) {
        ServerPlayNetworking.send(player, new VisualPayload(chunk.x, chunk.z, color));
    }
}
