package com.roocky.foundation.api.model;

/**
 * Represents the different types of permissions a player can have in a claim.
 */
public enum ClaimPermission {
    /**
     * Permission to break blocks.
     */
    BREAK,
    
    /**
     * Permission to place blocks.
     */
    PLACE,
    
    /**
     * Permission to interact with blocks or entities.
     */
    INTERACT,
    
    /**
     * Permission for explosions to occur or destroy blocks.
     */
    EXPLODE,
    
    /**
     * Full management access (trusting others, resizing).
     */
    MANAGE
}
