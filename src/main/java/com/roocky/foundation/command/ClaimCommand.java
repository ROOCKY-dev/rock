package com.roocky.foundation.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.roocky.foundation.api.model.Claim;
import com.roocky.foundation.api.model.ClaimPermission;
import com.roocky.foundation.config.SimpleConfig;
import com.roocky.foundation.impl.ClaimManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ChunkPos;



public class ClaimCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(ClaimCommand::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("claim")
                .executes(ClaimCommand::claimChunk)
                .then(CommandManager.literal("remove")
                        .executes(ClaimCommand::unclaimChunk))
                .then(CommandManager.literal("info")
                        .executes(ClaimCommand::claimInfo))
                .then(CommandManager.literal("trust")
                        .then(CommandManager.argument("target", EntityArgumentType.player())
                                .executes(ctx -> trustPlayer(ctx, null)) // Default: All
                                .then(CommandManager.argument("permission", StringArgumentType.word())
                                        .executes(ctx -> trustPlayer(ctx, StringArgumentType.getString(ctx, "permission"))))))
                // Admin Commands
                .then(CommandManager.literal("admin")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("reload")
                                .executes(ctx -> {
                                    SimpleConfig.reload();
                                    ctx.getSource().sendFeedback(() -> Text.literal("Config Reloaded").formatted(Formatting.GOLD), true);
                                    return 1;
                                }))
                        .then(CommandManager.literal("debug")
                                .executes(ctx -> {
                                    boolean newState = !com.roocky.foundation.RockAPI.isDebugging();
                                    com.roocky.foundation.RockAPI.setDebugMode(newState);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Debug Mode: " + (newState ? "ON" : "OFF")).formatted(Formatting.YELLOW), true);
                                    return 1;
                                }))
                        .then(CommandManager.literal("bypass")
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                                    boolean newState = !com.roocky.foundation.RockAPI.isBypassing(player.getUuid());
                                    com.roocky.foundation.RockAPI.setBypass(player.getUuid(), newState);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Admin Bypass: " + (newState ? "ON" : "OFF")).formatted(Formatting.LIGHT_PURPLE), true);
                                    return 1;
                                })))
        );
    }

    private static int claimChunk(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ChunkPos pos = new ChunkPos(player.getBlockPos());
        ClaimManager manager = ClaimManager.get(player.getServerWorld());

        if (manager.getClaim(pos) != null) {
            context.getSource().sendFeedback(() -> Text.literal("This chunk is already claimed!").formatted(Formatting.RED), false);
            return 0;
        }
        
        manager.addClaim(pos, player.getUuid());
        context.getSource().sendFeedback(() -> Text.literal("Successfully claimed chunk " + pos.toString()).formatted(Formatting.GREEN), false);
        return 1;
    }

    private static int unclaimChunk(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ChunkPos pos = new ChunkPos(player.getBlockPos());
        ClaimManager manager = ClaimManager.get(player.getServerWorld());
        Claim claim = manager.getClaim(pos);

        if (claim == null) {
            context.getSource().sendFeedback(() -> Text.literal("This chunk is not claimed.").formatted(Formatting.YELLOW), false);
            return 0;
        }

        if (!claim.getOwner().equals(player.getUuid()) && !player.hasPermissionLevel(2)) {
             context.getSource().sendFeedback(() -> Text.literal("You do not own this chunk!").formatted(Formatting.RED), false);
             return 0;
        }

        manager.removeClaim(pos);
        context.getSource().sendFeedback(() -> Text.literal("Unclaimed chunk " + pos.toString()).formatted(Formatting.YELLOW), false);
        return 1;
    }

    private static int claimInfo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ChunkPos pos = new ChunkPos(player.getBlockPos());
        ClaimManager manager = ClaimManager.get(player.getServerWorld());
        Claim claim = manager.getClaim(pos);

        if (claim == null) {
            context.getSource().sendFeedback(() -> Text.literal("Wilderness").formatted(Formatting.GREEN), false);
            // Visuals: Green for Wilderness (Safe)
            com.roocky.foundation.network.PacketHelper.sendVisuals(player, pos, 0x00FF00);
        } else {
             context.getSource().sendFeedback(() -> Text.literal("Claim Owner: " + claim.getOwner() + " (" + claim.getType() + ")").formatted(Formatting.GOLD), false);
             // Visuals: Red for Claim (Owned)
             com.roocky.foundation.network.PacketHelper.sendVisuals(player, pos, 0xFF0000);
        }
        return 1;
    }

    private static int trustPlayer(CommandContext<ServerCommandSource> context, String permissionName) throws CommandSyntaxException {
         ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
         ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
         ChunkPos pos = new ChunkPos(player.getBlockPos());
         ClaimManager manager = ClaimManager.get(player.getServerWorld());
         Claim claim = manager.getClaim(pos);

         if (claim == null) {
             context.getSource().sendFeedback(() -> Text.literal("This chunk is not claimed.").formatted(Formatting.RED), false);
             return 0;
         }

         if (!claim.getOwner().equals(player.getUuid()) && !player.hasPermissionLevel(2)) {
             context.getSource().sendFeedback(() -> Text.literal("You do not own this chunk!").formatted(Formatting.RED), false);
             return 0;
         }
         
         if (permissionName == null) {
             // Grant ALL
             for (ClaimPermission p : ClaimPermission.values()) {
                 claim.grantPermission(target.getUuid(), p);
             }
             manager.markDirty();
             context.getSource().sendFeedback(() -> Text.literal("Granted ALL permissions to " + target.getName().getString()).formatted(Formatting.GREEN), false);
         } else {
             try {
                 ClaimPermission p = ClaimPermission.valueOf(permissionName.toUpperCase());
                 claim.grantPermission(target.getUuid(), p);
                 manager.markDirty();
                 context.getSource().sendFeedback(() -> Text.literal("Granted " + p.name() + " to " + target.getName().getString()).formatted(Formatting.GREEN), false);
             } catch (IllegalArgumentException e) {
                 context.getSource().sendFeedback(() -> Text.literal("Invalid permission: " + permissionName).formatted(Formatting.RED), false);
                 return 0;
             }
         }
         
         return 1;
    }
}
