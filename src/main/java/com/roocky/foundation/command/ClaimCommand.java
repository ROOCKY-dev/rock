package com.roocky.foundation.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.roocky.foundation.api.model.Claim;
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
                                .executes(ClaimCommand::trustPlayer)))
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

        // Check if player has reached max claims (not implemented in manager yet, but config exists)
        // For now, just add it.
        
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
        } else {
             context.getSource().sendFeedback(() -> Text.literal("Claim Owner: " + claim.getOwner() + " (" + claim.getType() + ")").formatted(Formatting.GOLD), false);
        }
        return 1;
    }

    private static int trustPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
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
         
         if (claim instanceof ClaimManager.ClaimImpl impl) {
             impl.trusted.add(target.getUuid());
             manager.markDirty();
             context.getSource().sendFeedback(() -> Text.literal("Trusted " + target.getName().getString() + " in this claim.").formatted(Formatting.GREEN), false);
         }
         
         return 1;
    }
}
