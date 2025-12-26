package com.roocky.foundation.api.model;

/**
 * Represents the different types of permissions a player can have in a claim.
 */
public enum ClaimPermission {
    BLOCK_BREAK,
    BLOCK_PLACE,
    CONTAINER_OPEN,
    INTERACT_REDSTONE,
    INTERACT_BLOCK,  // Added for general block interaction (Doors, Gates, etc.)
    INTERACT_ENTITY, // Added for entity interaction (Item Frames, Armor Stands)
    MANAGE_PERMISSIONS
}
