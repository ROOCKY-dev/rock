package com.roocky.foundation.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public final class ClaimEvents {

    /**
     * Callback for block-related permissions (Break, Place, block interaction).
     */
    @FunctionalInterface
    public interface ClaimBlockPermission {
        ActionResult check(PlayerEntity player, World world, BlockPos pos);
    }

    /**
     * Fired when a player attempts to break a block.
     * Return FAIL to cancel.
     */
    public static final Event<ClaimBlockPermission> BLOCK_BREAK = EventFactory.createArrayBacked(ClaimBlockPermission.class,
            (listeners) -> (player, world, pos) -> {
                for (ClaimBlockPermission listener : listeners) {
                    ActionResult result = listener.check(player, world, pos);
                    if (result != ActionResult.PASS) return result;
                }
                return ActionResult.PASS;
            });

    /**
     * Fired when a player attempts to place a block.
     * Return FAIL to cancel.
     */
    public static final Event<ClaimBlockPermission> BLOCK_PLACE = EventFactory.createArrayBacked(ClaimBlockPermission.class,
            (listeners) -> (player, world, pos) -> {
                for (ClaimBlockPermission listener : listeners) {
                    ActionResult result = listener.check(player, world, pos);
                    if (result != ActionResult.PASS) return result;
                }
                return ActionResult.PASS;
            });

    /**
     * Fired when a player attempts to interact with a block.
     * Return FAIL to cancel.
     */
    public static final Event<ClaimBlockPermission> BLOCK_INTERACT = EventFactory.createArrayBacked(ClaimBlockPermission.class,
            (listeners) -> (player, world, pos) -> {
                for (ClaimBlockPermission listener : listeners) {
                    ActionResult result = listener.check(player, world, pos);
                    if (result != ActionResult.PASS) return result;
                }
                return ActionResult.PASS;
            });

    /**
     * Callback for entity-related permissions.
     */
    @FunctionalInterface
    public interface ClaimEntityPermission {
        ActionResult check(PlayerEntity player, World world, Entity target);
    }

    /**
     * Fired when a player attempts to interact with an entity.
     * Return FAIL to cancel.
     */
    public static final Event<ClaimEntityPermission> ENTITY_INTERACT = EventFactory.createArrayBacked(ClaimEntityPermission.class,
            (listeners) -> (player, world, target) -> {
                for (ClaimEntityPermission listener : listeners) {
                    ActionResult result = listener.check(player, world, target);
                    if (result != ActionResult.PASS) return result;
                }
                return ActionResult.PASS;
            });

    /**
     * Callback for explosions.
     */
    @FunctionalInterface
    public interface ClaimExplosionPermission {
        ActionResult check(World world, Explosion explosion);
    }

    /**
     * Fired when an explosion occurs.
     * Return FAIL to cancel.
     */
    public static final Event<ClaimExplosionPermission> EXPLOSION = EventFactory.createArrayBacked(ClaimExplosionPermission.class,
            (listeners) -> (world, explosion) -> {
                for (ClaimExplosionPermission listener : listeners) {
                    ActionResult result = listener.check(world, explosion);
                    if (result != ActionResult.PASS) return result;
                }
                return ActionResult.PASS;
            });

    /**
     * Callback for chunk changes.
     */
    @FunctionalInterface
    public interface ChunkChangeCallback {
        void onChunkChange(ServerPlayerEntity player, World world, net.minecraft.util.math.ChunkPos chunkPos);
    }

    /**
     * Fired when a player enters a chunk.
     */
    public static final Event<ChunkChangeCallback> ENTER_CHUNK = EventFactory.createArrayBacked(ChunkChangeCallback.class,
            (listeners) -> (player, world, chunkPos) -> {
                for (ChunkChangeCallback listener : listeners) {
                    listener.onChunkChange(player, world, chunkPos);
                }
            });

    /**
     * Fired when a player exits a chunk.
     */
    public static final Event<ChunkChangeCallback> EXIT_CHUNK = EventFactory.createArrayBacked(ChunkChangeCallback.class,
            (listeners) -> (player, world, chunkPos) -> {
                for (ChunkChangeCallback listener : listeners) {
                    listener.onChunkChange(player, world, chunkPos);
                }
            });

    private ClaimEvents() {}
}
