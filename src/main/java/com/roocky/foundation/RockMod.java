package com.roocky.foundation;

import com.roocky.foundation.api.event.ClaimEvents;
import com.roocky.foundation.api.model.Claim;
import com.roocky.foundation.api.model.ClaimPermission;
import com.roocky.foundation.config.SimpleConfig;
import com.roocky.foundation.impl.ClaimManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RockMod implements ModInitializer {
    public static final String MOD_ID = "rock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("ROCK key components initializing...");
        
        // Load Config
        SimpleConfig.load();

        // Register Commands
        com.roocky.foundation.command.ClaimCommand.register();

        // Register Event Listeners

        // Block Break
        ClaimEvents.BLOCK_BREAK.register((player, world, pos) -> {
            if (world.isClient) return ActionResult.PASS;
            if (player.hasPermissionLevel(2)) return ActionResult.PASS; // Op bypass

            ChunkPos chunkPos = new ChunkPos(pos);
            ClaimManager manager = ClaimManager.get((net.minecraft.server.world.ServerWorld) world);
            Claim claim = manager.getClaim(chunkPos);

            if (claim != null) {
                if (!claim.hasPermission(player.getUuid(), ClaimPermission.BREAK)) {
                    sendFeedback(player, claim.getOwner().toString()); // Simple verification for now
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        // Block Place
        ClaimEvents.BLOCK_PLACE.register((player, world, pos) -> {
            if (world.isClient) return ActionResult.PASS;
            if (player.hasPermissionLevel(2)) return ActionResult.PASS;

            ChunkPos chunkPos = new ChunkPos(pos);
            ClaimManager manager = ClaimManager.get((net.minecraft.server.world.ServerWorld) world);
            Claim claim = manager.getClaim(chunkPos);

            if (claim != null) {
                if (!claim.hasPermission(player.getUuid(), ClaimPermission.PLACE)) {
                    sendFeedback(player, claim.getOwner().toString());
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        // Block/Entity Interact
        ClaimEvents.BLOCK_INTERACT.register((player, world, pos) -> {
            if (world.isClient) return ActionResult.PASS;
            if (player.hasPermissionLevel(2)) return ActionResult.PASS;

            ChunkPos chunkPos = new ChunkPos(pos);
            ClaimManager manager = ClaimManager.get((net.minecraft.server.world.ServerWorld) world);
            Claim claim = manager.getClaim(chunkPos);

            if (claim != null) {
                if (!claim.hasPermission(player.getUuid(), ClaimPermission.INTERACT)) {
                    sendFeedback(player, claim.getOwner().toString());
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });
        
        ClaimEvents.ENTITY_INTERACT.register((player, world, target) -> {
            if (world.isClient) return ActionResult.PASS;
            if (player.hasPermissionLevel(2)) return ActionResult.PASS;

            ChunkPos chunkPos = new ChunkPos(target.getBlockPos());
            ClaimManager manager = ClaimManager.get((net.minecraft.server.world.ServerWorld) world);
            Claim claim = manager.getClaim(chunkPos);

            if (claim != null) {
                if (!claim.hasPermission(player.getUuid(), ClaimPermission.INTERACT)) {
                    sendFeedback(player, claim.getOwner().toString());
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        // Explosion
        ClaimEvents.EXPLOSION.register((world, explosion) -> {
            if (world.isClient) return ActionResult.PASS;
            if (SimpleConfig.get().enableExplosions) return ActionResult.PASS;

            // Check if any affected block is in a claim
            // Ideally we check specific blocks, but for now we can just check the center or iterate
            // The mixin handles clearing blocks, so here we might want to check if the center is safe?
            // Actually, the Mixin fires per explosion. We can iterate blocks in the mixin, but the event is passed the explosion.
            // For this phase, let's just say "If explosion starts in claim, cancel it" OR rely on the mixin to clear blocks.
            // BUT, our Mixin logic says: if event returns FAIL, mixin clears blocks.
            // So we need to decide: do we return FAIL if *any* block is claimed?
            // Simple approach: Check explosion center.
            
            ChunkPos centerChunk = new ChunkPos(new net.minecraft.util.math.BlockPos((int)explosion.getPosition().x, (int)explosion.getPosition().y, (int)explosion.getPosition().z));
            ClaimManager manager = ClaimManager.get((net.minecraft.server.world.ServerWorld) world);
            Claim claim = manager.getClaim(centerChunk);
            
            if (claim != null) {
               // Explosion in claim
               if (claim.getType() != com.roocky.foundation.api.model.ClaimType.WILDERNESS) {
                   return ActionResult.FAIL; 
               }
            }
            return ActionResult.PASS;
        });
        
        LOGGER.info("ROCK fully initialized.");
    }
    
    private void sendFeedback(net.minecraft.entity.player.PlayerEntity player, String ownerName) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
             serverPlayer.sendMessage(Text.literal(String.format(SimpleConfig.get().claimMessage, ownerName)), true);
        }
    }
}
