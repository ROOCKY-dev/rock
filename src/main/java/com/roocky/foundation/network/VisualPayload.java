package com.roocky.foundation.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record VisualPayload(int chunkX, int chunkZ, int color) implements CustomPayload {
    public static final CustomPayload.Id<VisualPayload> ID = new CustomPayload.Id<>(Identifier.of("rock", "claim_visuals"));
    public static final PacketCodec<RegistryByteBuf, VisualPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, VisualPayload::chunkX,
            PacketCodecs.INTEGER, VisualPayload::chunkZ,
            PacketCodecs.INTEGER, VisualPayload::color,
            VisualPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
