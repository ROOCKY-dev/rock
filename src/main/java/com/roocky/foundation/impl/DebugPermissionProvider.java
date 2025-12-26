package com.roocky.foundation.impl;

import com.roocky.foundation.api.model.ClaimPermission;
import com.roocky.foundation.api.service.PermissionProvider;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugPermissionProvider implements PermissionProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger("RockDebug");
    private final PermissionProvider delegate;

    public DebugPermissionProvider(PermissionProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean checkPermission(ServerPlayerEntity player, ChunkPos pos, ClaimPermission permission) {
        boolean result = delegate.checkPermission(player, pos, permission);
        LOGGER.info("[DEBUG] Permission Check - Player: {}, Chunk: {}, Perm: {}, Result: {}", 
                player.getName().getString(), pos, permission, result);
        return result;
    }

    @Override
    public String getDenialMessage(ServerPlayerEntity player, ChunkPos pos) {
        return delegate.getDenialMessage(player, pos) + " [DEBUG: DENIED]";
    }
}
