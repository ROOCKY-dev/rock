package com.roocky.foundation.api.model;

public enum PermissionCategory {
    CORE("Core", "minecraft:beacon"),
    BUILDING("Building", "minecraft:bricks"),
    CONTAINERS("Containers", "minecraft:chest"),
    MECHANISMS("Mechanisms", "minecraft:redstone"),
    ANIMALS("Animals", "minecraft:lead"),
    COMBAT("Combat", "minecraft:diamond_sword"),
    MANAGEMENT("Management", "minecraft:command_block");

    private final String displayName;
    private final String iconItem;

    PermissionCategory(String displayName, String iconItem) {
        this.displayName = displayName;
        this.iconItem = iconItem;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIconItem() {
        return iconItem;
    }
}
