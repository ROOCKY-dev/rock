package com.roocky.foundation.api.model;

/**
 * Represents the different types of permissions a player can have in a claim.
 */
public enum ClaimPermission {
    // Management
    MANAGE_PERMISSIONS(PermissionCategory.MANAGEMENT, "Manage Permissions", "Grant/Revoke permissions", "minecraft:command_block"),

    // Building
    BLOCK_BREAK(PermissionCategory.BUILDING, "Break Blocks", "Destroy blocks", "minecraft:diamond_pickaxe"),
    BLOCK_PLACE(PermissionCategory.BUILDING, "Place Blocks", "Place new blocks", "minecraft:grass_block"),

    // Containers
    CONTAINER_OPEN(PermissionCategory.CONTAINERS, "Open Containers", "Open chests, barrels, etc.", "minecraft:chest"),

    // Mechanisms
    INTERACT_REDSTONE(PermissionCategory.MECHANISMS, "Redstone", "Use buttons, levers, etc.", "minecraft:redstone"),
    INTERACT_BLOCK(PermissionCategory.MECHANISMS, "Use Blocks", "Open doors, gates, etc.", "minecraft:oak_door"),
    ITEM_USE(PermissionCategory.MECHANISMS, "Use Items", "Use items like flint & steel, buckets", "minecraft:flint_and_steel"),

    // Animals / Entities
    INTERACT_ENTITY(PermissionCategory.ANIMALS, "Interact Entities", "Trade, shear, feed animals", "minecraft:villager_spawn_egg"),
    ENTITY_RIDE(PermissionCategory.ANIMALS, "Ride Entities", "Ride horses, pigs, boats", "minecraft:saddle"),

    // Combat
    ENTITY_DAMAGE(PermissionCategory.COMBAT, "Damage Entities", "Hurt animals and mobs", "minecraft:iron_sword"),
    ENTITY_PVP(PermissionCategory.COMBAT, "PvP", "Fight other players", "minecraft:diamond_sword");

    private final PermissionCategory category;
    private final String displayName;
    private final String description;
    private final String iconItem;

    ClaimPermission(PermissionCategory category, String displayName, String description, String iconItem) {
        this.category = category;
        this.displayName = displayName;
        this.description = description;
        this.iconItem = iconItem;
    }

    public PermissionCategory getCategory() {
        return category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getIconItem() {
        return iconItem;
    }
}
