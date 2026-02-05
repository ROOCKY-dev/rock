package com.roocky.foundation.api.model;

public enum PermissionCategory {
    CORE("Core", "minecraft:beacon"),
    MANAGEMENT("Management", "minecraft:command_block"),
    BUILDING("Building", "minecraft:bricks"),
    FARMING("Farming", "minecraft:wheat"),
    CONTAINERS("Containers", "minecraft:chest"),
    MECHANISMS("Mechanisms", "minecraft:redstone"),
    SIGNS("Signs", "minecraft:oak_sign"),
    ANIMALS("Animals", "minecraft:lead"),
    COMBAT("Combat", "minecraft:diamond_sword"),
    MOVEMENT("Movement", "minecraft:elytra"),
    VEHICLES("Vehicles", "minecraft:minecart");

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
