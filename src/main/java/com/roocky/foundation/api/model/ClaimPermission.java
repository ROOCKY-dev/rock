package com.roocky.foundation.api.model;

/**
 * Represents the different types of permissions a player can have in a claim.
 */
public enum ClaimPermission {
    // Management
    MANAGE_PERMISSIONS(PermissionCategory.MANAGEMENT, "Manage Permissions", "Grant/Revoke permissions", "minecraft:command_block"),
    MANAGE_SETTINGS(PermissionCategory.MANAGEMENT, "Manage Settings", "Change claim flags/settings", "minecraft:comparator"),
    KICK_PLAYERS(PermissionCategory.MANAGEMENT, "Kick Players", "Kick players from the claim", "minecraft:leather_boots"),
    BAN_PLAYERS(PermissionCategory.MANAGEMENT, "Ban Players", "Ban players from the claim", "minecraft:barrier"),

    // Building
    BLOCK_BREAK(PermissionCategory.BUILDING, "Break Blocks", "Destroy blocks", "minecraft:diamond_pickaxe"),
    BLOCK_PLACE(PermissionCategory.BUILDING, "Place Blocks", "Place new blocks", "minecraft:grass_block"),
    PAINTING_PLACE(PermissionCategory.BUILDING, "Place Paintings", "Place paintings and item frames", "minecraft:painting"),
    PAINTING_BREAK(PermissionCategory.BUILDING, "Break Paintings", "Break paintings and item frames", "minecraft:shears"),
    FLUID_PLACE(PermissionCategory.BUILDING, "Place Fluid", "Place water, lava, etc.", "minecraft:water_bucket"),
    FLUID_PICKUP(PermissionCategory.BUILDING, "Pickup Fluid", "Pickup water, lava, etc.", "minecraft:bucket"),

    // Signs
    SIGN_EDIT(PermissionCategory.SIGNS, "Edit Signs", "Edit text on signs", "minecraft:oak_sign"),

    // Farming
    CROP_PLANT(PermissionCategory.FARMING, "Plant Crops", "Plant seeds and saplings", "minecraft:wheat_seeds"),
    CROP_HARVEST(PermissionCategory.FARMING, "Harvest Crops", "Harvest grown crops", "minecraft:wheat"),
    BONE_MEAL(PermissionCategory.FARMING, "Use Bone Meal", "Grow crops with bone meal", "minecraft:bone_meal"),

    // Containers
    CONTAINER_OPEN(PermissionCategory.CONTAINERS, "Open Containers", "Open chests, barrels, etc.", "minecraft:chest"),
    ENDER_CHEST(PermissionCategory.CONTAINERS, "Use Ender Chest", "Access ender chests", "minecraft:ender_chest"),

    // Mechanisms
    INTERACT_REDSTONE(PermissionCategory.MECHANISMS, "Redstone", "Interact with general redstone components", "minecraft:redstone"),
    INTERACT_BLOCK(PermissionCategory.MECHANISMS, "Use Blocks", "Interact with general blocks", "minecraft:oak_door"),
    ITEM_USE(PermissionCategory.MECHANISMS, "Use Items", "Use items like flint & steel", "minecraft:flint_and_steel"),

    NOTE_BLOCK_USE(PermissionCategory.MECHANISMS, "Tune Note Blocks", "Change note block pitch", "minecraft:note_block"),
    JUKEBOX_USE(PermissionCategory.MECHANISMS, "Use Jukebox", "Insert/Remove discs", "minecraft:jukebox"),
    TRAPDOOR_OPEN(PermissionCategory.MECHANISMS, "Open Trapdoors", "Open/Close trapdoors", "minecraft:oak_trapdoor"),
    FENCE_GATE_OPEN(PermissionCategory.MECHANISMS, "Open Fence Gates", "Open/Close fence gates", "minecraft:oak_fence_gate"),
    BUTTON_USE(PermissionCategory.MECHANISMS, "Press Buttons", "Use buttons", "minecraft:stone_button"),
    LEVER_USE(PermissionCategory.MECHANISMS, "Flip Levers", "Use levers", "minecraft:lever"),
    PRESSURE_PLATE(PermissionCategory.MECHANISMS, "Trigger Plates", "Walk on pressure plates", "minecraft:stone_pressure_plate"),
    SENSOR_TRIGGER(PermissionCategory.MECHANISMS, "Trigger Sensors", "Trigger tripwires and sensors", "minecraft:tripwire_hook"),

    // Animals
    INTERACT_ENTITY(PermissionCategory.ANIMALS, "Interact Entities", "General entity interaction", "minecraft:villager_spawn_egg"),
    ENTITY_BREED(PermissionCategory.ANIMALS, "Breed Animals", "Feed animals to breed", "minecraft:wheat"),
    ENTITY_SHEAR(PermissionCategory.ANIMALS, "Shear Animals", "Shear sheep, etc.", "minecraft:shears"),
    VILLAGER_TRADE(PermissionCategory.ANIMALS, "Villager Trade", "Trade with villagers", "minecraft:emerald"),
    RIDE_ANIMALS(PermissionCategory.ANIMALS, "Ride Animals", "Ride horses, pigs, etc.", "minecraft:saddle"),

    // Combat
    ENTITY_DAMAGE(PermissionCategory.COMBAT, "Damage Entities", "General entity damage", "minecraft:iron_sword"),
    ENTITY_PVP(PermissionCategory.COMBAT, "PvP", "Fight other players", "minecraft:diamond_sword"),
    HURT_MONSTERS(PermissionCategory.COMBAT, "Hurt Monsters", "Damage hostile mobs", "minecraft:zombie_head"),
    HURT_ANIMALS(PermissionCategory.COMBAT, "Hurt Animals", "Damage passive animals", "minecraft:porkchop"),

    // Movement
    ENTER_CLAIM(PermissionCategory.MOVEMENT, "Enter Claim", "Walk into the claim", "minecraft:oak_door"),
    TELEPORT_HERE(PermissionCategory.MOVEMENT, "Teleport Here", "Teleport into the claim", "minecraft:ender_pearl"),
    FLY(PermissionCategory.MOVEMENT, "Fly", "Fly within the claim", "minecraft:feather"),
    ELYTRA(PermissionCategory.MOVEMENT, "Use Elytra", "Glide with elytra", "minecraft:elytra"),

    // Vehicles
    VEHICLE_PLACE(PermissionCategory.VEHICLES, "Place Vehicles", "Place boats, minecarts", "minecraft:minecart"),
    VEHICLE_BREAK(PermissionCategory.VEHICLES, "Break Vehicles", "Destroy boats, minecarts", "minecraft:tnt_minecart"),
    VEHICLE_RIDE(PermissionCategory.VEHICLES, "Ride Vehicles", "Ride boats, minecarts", "minecraft:saddle");

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
