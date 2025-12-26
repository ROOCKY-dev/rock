package com.roocky.foundation.api.model;

/**
 * Defines the type of a claim.
 */
public enum ClaimType {
    /**
     * Administration claim, bypasses most checks, usually owned by server.
     */
    ADMIN,
    
    /**
     * Standard player claim.
     */
    PLAYER,
    
    /**
     * Unclaimed wilderness (technically not a claim, but used for fallback).
     */
    WILDERNESS
}
