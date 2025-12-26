package com.roocky.foundation.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.particle.DustParticleEffect;
import org.joml.Vector3f;

public class RockClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(com.roocky.foundation.network.VisualPayload.ID, (payload, context) -> {
            int chunkX = payload.chunkX();
            int chunkZ = payload.chunkZ();
            int color = payload.color();

            context.client().execute(() -> {
                if (context.client().world != null && context.client().player != null) {
                    float r = ((color >> 16) & 0xFF) / 255.0f;
                    float g = ((color >> 8) & 0xFF) / 255.0f;
                    float b = (color & 0xFF) / 255.0f;
                    
                    double startX = chunkX * 16.0;
                    double startZ = chunkZ * 16.0;
                    double endX = startX + 16.0;
                    double endZ = startZ + 16.0;
                    double y = context.client().player.getY(); // Render at player height

                    DustParticleEffect particle = new DustParticleEffect(new Vector3f(r, g, b), 1.0f);

                    // North & South borders
                    for (double x = startX; x <= endX; x += 2.0) {
                        context.client().world.addParticle(particle, x, y, startZ, 0, 0, 0);
                        context.client().world.addParticle(particle, x, y, endZ, 0, 0, 0);
                    }
                    
                    // East & West borders
                    for (double z = startZ; z <= endZ; z += 2.0) {
                        context.client().world.addParticle(particle, startX, y, z, 0, 0, 0);
                        context.client().world.addParticle(particle, endX, y, z, 0, 0, 0);
                    }
                }
            });
        });
    }
}
