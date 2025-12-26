package com.roocky.foundation.api.model;

import net.minecraft.util.math.ChunkPos;
import java.util.UUID;

/**
 * Represents a claim within the world.
 */
public interface Claim {
    /**
     * @return The UUID of the owner of this claim.
     */
    UUID getOwner();

    /**
     * @return The type of this claim.
     */
    ClaimType getType();

    /**
     * Checks if a player has a specific permission in this claim.
     *
     * @param player     The UUID of the player to check.
     * @param permission The permission to check for.
     * @return True if the player has permission, false otherwise.
     */
    boolean hasPermission(UUID player, ClaimPermission permission);

    /**
     * @return The chunk position of this claim.
     */
    ChunkPos getPosition();
}
