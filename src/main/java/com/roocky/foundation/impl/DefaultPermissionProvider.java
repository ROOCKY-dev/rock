package com.roocky.foundation.impl;

import com.roocky.foundation.api.model.Claim;
import com.roocky.foundation.api.model.ClaimPermission;
import com.roocky.foundation.api.service.PermissionProvider;
import com.roocky.foundation.config.SimpleConfig;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;

public class DefaultPermissionProvider implements PermissionProvider {

    @Override
    public boolean checkPermission(ServerPlayerEntity player, ChunkPos pos, ClaimPermission permission) {
        if (com.roocky.foundation.RockAPI.isBypassing(player.getUuid())) return true;
        // Op bypass
        if (player.hasPermissionLevel(2)) return true;
        
        String debugPrefix = "Checking " + permission + " for " + player.getName().getString() + " in " + pos + ": ";

        ClaimManager manager = ClaimManager.get(player.getServerWorld());
        Claim claim = manager.getClaim(pos);

        if (claim == null) {
            com.roocky.foundation.RockAPI.log(debugPrefix + "ALLOWED (Wilderness)");
            return true; // Wilderness is free
        }

        boolean result = claim.hasPermission(player.getUuid(), permission);
        com.roocky.foundation.RockAPI.log(debugPrefix + (result ? "ALLOWED" : "DENIED"));
        return result;
    }

    @Override
    public String getDenialMessage(ServerPlayerEntity player, ChunkPos pos) {
        ClaimManager manager = ClaimManager.get(player.getServerWorld());
        Claim claim = manager.getClaim(pos);
        if (claim != null) {
            String ownerName = claim.getOwner().toString();
            // In a real mod we'd resolve UUID to Name cache, but UUID/Config string is fine for now
            return String.format(SimpleConfig.get().claimMessage, ownerName);
        }
        return "§cYou cannot do that here.";
    }
}
