package com.roocky.foundation.mixin;

import com.roocky.foundation.api.event.ClaimEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void rock$onAttack(Entity target, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ActionResult result = ClaimEvents.ENTITY_INTERACT.invoker().check(player, player.getWorld(), target);
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
    @org.spongepowered.asm.mixin.Unique
    private int rock$lastChunkX = Integer.MIN_VALUE;
    @org.spongepowered.asm.mixin.Unique
    private int rock$lastChunkZ = Integer.MIN_VALUE;

    @Inject(method = "tick", at = @At("HEAD"))
    private void rock$onTick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        // Skip tracking if player not fully joined or logic invalid
        if (player.getWorld() == null) return;
        
        var currentChunkPos = player.getChunkPos();
        int currentChunkX = currentChunkPos.x;
        int currentChunkZ = currentChunkPos.z;

        if (rock$lastChunkX == Integer.MIN_VALUE) {
            // First tick initialization
            rock$lastChunkX = currentChunkX;
            rock$lastChunkZ = currentChunkZ;
            // Fire enter for initial spawn/login chunk
            ClaimEvents.ENTER_CHUNK.invoker().onChunkChange(player, player.getWorld(), new net.minecraft.util.math.ChunkPos(currentChunkX, currentChunkZ));
            return;
        }

        if (currentChunkX != rock$lastChunkX || currentChunkZ != rock$lastChunkZ) {
            // Player moved chunks
            net.minecraft.util.math.ChunkPos oldPos = new net.minecraft.util.math.ChunkPos(rock$lastChunkX, rock$lastChunkZ);
            net.minecraft.util.math.ChunkPos newPos = new net.minecraft.util.math.ChunkPos(currentChunkX, currentChunkZ);
            
            ClaimEvents.EXIT_CHUNK.invoker().onChunkChange(player, player.getWorld(), oldPos);
            ClaimEvents.ENTER_CHUNK.invoker().onChunkChange(player, player.getWorld(), newPos);
            
            rock$lastChunkX = currentChunkX;
            rock$lastChunkZ = currentChunkZ;
        }
    }
}
