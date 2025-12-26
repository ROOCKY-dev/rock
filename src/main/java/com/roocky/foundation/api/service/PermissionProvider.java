package com.roocky.foundation.api.service;

import com.roocky.foundation.api.model.ClaimPermission;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;

public interface PermissionProvider {
    /**
     * Checks if a player has permission to perform an action in a specific chunk.
     *
     * @param player     The player attempting the action.
     * @param pos        The chunk position where the action is occurring.
     * @param permission The type of permission required.
     * @return True if allowed, false if denied.
     */
    boolean checkPermission(ServerPlayerEntity player, ChunkPos pos, ClaimPermission permission);

    /**
     * Gets the denial message to show the player when checkPermission returns false.
     *
     * @param player The player who was denied.
     * @param pos    The chunk position.
     * @return The message string (can handle coloring codes).
     */
    String getDenialMessage(ServerPlayerEntity player, ChunkPos pos);
}
