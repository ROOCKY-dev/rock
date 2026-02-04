package com.roocky.foundation;

import com.roocky.foundation.api.service.PermissionProvider;
import com.roocky.foundation.impl.DefaultPermissionProvider;

public class RockAPI {
    private static PermissionProvider activeProvider = new DefaultPermissionProvider();
    private static final java.util.Set<java.util.UUID> bypassPlayers = new java.util.HashSet<>();
    private static boolean debugMode = false;

    public static PermissionProvider getProvider() {
        return activeProvider;
    }

    public static void setProvider(PermissionProvider provider) {
        if (provider == null) throw new IllegalArgumentException("Provider cannot be null");
        RockAPI.activeProvider = provider;
    }

    public static boolean isDebugging() {
        return debugMode;
    }

    public static void setDebugMode(boolean debug) {
        debugMode = debug;
    }

    public static boolean isBypassing(java.util.UUID playerUuid) {
        return bypassPlayers.contains(playerUuid);
    }
    
    public static void setBypass(java.util.UUID player, boolean bypass) {
        if (bypass) bypassPlayers.add(player);
        else bypassPlayers.remove(player);
    }

    public static void log(String message) {
        if (debugMode) {
            com.roocky.foundation.RockMod.LOGGER.info("[ROCK DEBUG] " + message);
        }
    }
}
