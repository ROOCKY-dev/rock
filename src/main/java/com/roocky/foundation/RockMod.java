package com.roocky.foundation;

import com.roocky.foundation.api.event.ClaimEvents;
import com.roocky.foundation.api.model.ClaimPermission;
import com.roocky.foundation.api.service.PermissionProvider;
import com.roocky.foundation.config.SimpleConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RockMod implements ModInitializer {
    public static final String MOD_ID = "rock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("ROCK key components initializing...");
        
        SimpleConfig.load();
        
        // Networking Registration
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playS2C().register(com.roocky.foundation.network.VisualPayload.ID, com.roocky.foundation.network.VisualPayload.CODEC);
        
        com.roocky.foundation.command.ClaimCommand.register();

        // Block Break
        ClaimEvents.BLOCK_BREAK.register((player, world, pos) -> {
            if (world.isClient) return ActionResult.PASS;
            ChunkPos chunkPos = new ChunkPos(pos);
            
            if (player instanceof ServerPlayerEntity serverPlayer) {
                 PermissionProvider provider = RockAPI.getProvider();
                 if (!provider.checkPermission(serverPlayer, chunkPos, ClaimPermission.BLOCK_BREAK)) {
                     sendFeedback(serverPlayer, provider.getDenialMessage(serverPlayer, chunkPos));
                     return ActionResult.FAIL;
                 }
            }
            return ActionResult.PASS;
        });

        // Block Place
        ClaimEvents.BLOCK_PLACE.register((player, world, pos) -> {
            if (world.isClient) return ActionResult.PASS;
            ChunkPos chunkPos = new ChunkPos(pos);
            
            if (player instanceof ServerPlayerEntity serverPlayer) {
                 PermissionProvider provider = RockAPI.getProvider();
                 if (!provider.checkPermission(serverPlayer, chunkPos, ClaimPermission.BLOCK_PLACE)) {
                     sendFeedback(serverPlayer, provider.getDenialMessage(serverPlayer, chunkPos));
                     return ActionResult.FAIL;
                 }
            }
            return ActionResult.PASS;
        });

        // Block Interact
        ClaimEvents.BLOCK_INTERACT.register((player, world, pos) -> {
            if (world.isClient) return ActionResult.PASS;
            ChunkPos chunkPos = new ChunkPos(pos);
            
            if (player instanceof ServerPlayerEntity serverPlayer) {
                BlockState state = world.getBlockState(pos);
                // Check if container
                boolean isContainer = state.hasBlockEntity() && 
                                     (world.getBlockEntity(pos) instanceof Inventory || 
                                      state.createScreenHandlerFactory(world, pos) != null);

                ClaimPermission required = isContainer ? ClaimPermission.CONTAINER_OPEN : ClaimPermission.INTERACT_BLOCK;
                
                PermissionProvider provider = RockAPI.getProvider();
                 if (!provider.checkPermission(serverPlayer, chunkPos, required)) {
                     sendFeedback(serverPlayer, provider.getDenialMessage(serverPlayer, chunkPos));
                     return ActionResult.FAIL;
                 }
            }
            return ActionResult.PASS;
        });
        
        // Entity Interact
        ClaimEvents.ENTITY_INTERACT.register((player, world, target) -> {
            if (world.isClient) return ActionResult.PASS;
            ChunkPos chunkPos = new ChunkPos(target.getBlockPos());
            
            if (player instanceof ServerPlayerEntity serverPlayer) {
                 PermissionProvider provider = RockAPI.getProvider();
                 if (!provider.checkPermission(serverPlayer, chunkPos, ClaimPermission.INTERACT_ENTITY)) {
                     sendFeedback(serverPlayer, provider.getDenialMessage(serverPlayer, chunkPos));
                     return ActionResult.FAIL;
                 }
            }
            return ActionResult.PASS;
        });

        // Explosion (Logic remains here for now as it doesn't involve a player directly in the check permission method yet)
        // Ideally we expand PermissionProvider to handle explosions, but the interface mandates a Player.
        // For Phase 2, we keep Explosion logic "Hardcoded" to ClaimManager or add a new method to Provider later.
        // Given instructions, we focus on Player permissions.
        ClaimEvents.EXPLOSION.register((world, explosion) -> {
            if (world.isClient) return ActionResult.PASS;
            if (SimpleConfig.get().enableExplosions) return ActionResult.PASS;

            ChunkPos centerChunk = new ChunkPos(new net.minecraft.util.math.BlockPos((int)explosion.getPosition().x, (int)explosion.getPosition().y, (int)explosion.getPosition().z));
            com.roocky.foundation.impl.ClaimManager manager = com.roocky.foundation.impl.ClaimManager.get((net.minecraft.server.world.ServerWorld) world);
            com.roocky.foundation.api.model.Claim claim = manager.getClaim(centerChunk);
            
            if (claim != null) {
               if (claim.getType() != com.roocky.foundation.api.model.ClaimType.WILDERNESS) {
                   return ActionResult.FAIL; 
               }
            }
            return ActionResult.PASS;
        });
        
        LOGGER.info("ROCK fully initialized (API Mode).");
    }
    
    private void sendFeedback(ServerPlayerEntity player, String message) {
        player.sendMessage(Text.literal(message), true);
    }
}
