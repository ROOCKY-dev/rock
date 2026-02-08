package com.roocky.foundation.impl;

import com.roocky.foundation.api.model.Claim;
import com.roocky.foundation.api.model.ClaimPermission;
import com.roocky.foundation.api.model.ClaimType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ClaimManager extends PersistentState {

    private static final Type<ClaimManager> TYPE = new Type<>(
            ClaimManager::new,
            ClaimManager::createFromNbt,
            null
    );

    private final Map<ChunkPos, Claim> claims = new HashMap<>();

    public static ClaimManager get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                TYPE,
                "rock_claims"
        );
    }

    public void addClaim(ChunkPos pos, UUID owner) {
        claims.put(pos, new ClaimImpl(owner, pos, ClaimType.PLAYER));
        markDirty();
    }

    public void removeClaim(ChunkPos pos) {
        claims.remove(pos);
        markDirty();
    }

    public Claim getClaim(ChunkPos pos) {
        return claims.get(pos);
    }

    public void save() {
        this.markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList list = new NbtList();
        for (Map.Entry<ChunkPos, Claim> entry : claims.entrySet()) {
            NbtCompound claimTag = new NbtCompound();
            claimTag.putLong("Pos", entry.getKey().toLong());
            claimTag.putUuid("Owner", entry.getValue().getOwner());
            claimTag.putString("Type", entry.getValue().getType().name());
            
            // Save permissions
            if (entry.getValue() instanceof ClaimImpl impl) {
                NbtList permissionsList = new NbtList();
                for (Map.Entry<UUID, Set<ClaimPermission>> permEntry : impl.permissions.entrySet()) {
                    NbtCompound playerTag = new NbtCompound();
                    playerTag.putUuid("UUID", permEntry.getKey());
                    
                    NbtList perms = new NbtList();
                    for (ClaimPermission p : permEntry.getValue()) {
                        perms.add(NbtString.of(p.name()));
                    }
                    playerTag.put("Perms", perms);
                    permissionsList.add(playerTag);
                }
                claimTag.put("Permissions", permissionsList);
            }
            
            list.add(claimTag);
        }
        nbt.put("Claims", list);
        return nbt;
    }

    public static ClaimManager createFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        ClaimManager manager = new ClaimManager();
        NbtList list = nbt.getList("Claims", 10); // 10 = Compound
        for (int i = 0; i < list.size(); i++) {
            NbtCompound claimTag = list.getCompound(i);
            ChunkPos pos = new ChunkPos(claimTag.getLong("Pos"));
            UUID owner = claimTag.getUuid("Owner");
            ClaimType type = ClaimType.valueOf(claimTag.getString("Type"));
            
            ClaimImpl claim = new ClaimImpl(owner, pos, type);
            
            // Migration Logic: Check for old "Trusted" list
            if (claimTag.contains("Trusted")) {
                NbtList trustedList = claimTag.getList("Trusted", 10); // List of Compounds {UUID: ...}
                for (int j = 0; j < trustedList.size(); j++) {
                     // Check if it's a compound or directly UUIDs (Legacy impl used Compound {UUID: ...})
                     // Based on previous code: trustedTag.putUuid("UUID", trusted);
                     UUID trustedUUID = trustedList.getCompound(j).getUuid("UUID");
                     claim.grantAll(trustedUUID);
                }
            }
            
            // New Logic: "Permissions" list
            if (claimTag.contains("Permissions")) {
                 NbtList permList = claimTag.getList("Permissions", 10);
                 for (int j = 0; j < permList.size(); j++) {
                     NbtCompound playerTag = permList.getCompound(j);
                     UUID playerUUID = playerTag.getUuid("UUID");
                     
                     NbtList perms = playerTag.getList("Perms", 8); // 8 = String
                     if (perms.isEmpty()) {
                         // Fallback or empty
                         continue;
                     }
                     
                     Set<ClaimPermission> granted = EnumSet.noneOf(ClaimPermission.class);
                     for (int k = 0; k < perms.size(); k++) {
                         try {
                             granted.add(ClaimPermission.valueOf(perms.getString(k)));
                         } catch (IllegalArgumentException ignored) {
                             // Ignore invalid/old permissions
                         }
                     }
                     claim.permissions.put(playerUUID, granted);
                 }
            }
            
            manager.claims.put(pos, claim);
        }
        return manager;
    }

    public static class ClaimImpl implements Claim {
        private final UUID owner;
        private final ChunkPos pos;
        private final ClaimType type;
        
        // Granular Permissions Map
        public final Map<UUID, Set<ClaimPermission>> permissions = new HashMap<>();

        public ClaimImpl(UUID owner, ChunkPos pos, ClaimType type) {
            this.owner = owner;
            this.pos = pos;
            this.type = type;
        }

        public void grantAll(UUID player) {
            permissions.put(player, EnumSet.allOf(ClaimPermission.class));
        }

        @Override
        public UUID getOwner() {
            return owner;
        }

        @Override
        public ClaimType getType() {
            return type;
        }

        @Override
        public boolean hasPermission(UUID player, ClaimPermission permission) {
            if (player.equals(owner)) return true;
            if (type == ClaimType.WILDERNESS) return true;
            if (type == ClaimType.ADMIN) return false; 
            
            Set<ClaimPermission> playerPerms = permissions.get(player);
            return playerPerms != null && playerPerms.contains(permission);
        }

        @Override
        public void grantPermission(UUID player, ClaimPermission permission) {
            permissions.computeIfAbsent(player, k -> EnumSet.noneOf(ClaimPermission.class))
                       .add(permission);
        }

        @Override
        public void revokePermission(UUID player, ClaimPermission permission) {
            Set<ClaimPermission> playerPerms = permissions.get(player);
            if (playerPerms != null) {
                playerPerms.remove(permission);
                if (playerPerms.isEmpty()) {
                    permissions.remove(player);
                }
            }
        }

        @Override
        public ChunkPos getPosition() {
            return pos;
        }

        @Override
        public Set<UUID> getTrustedPlayers() {
            return permissions.keySet();
        }
    }
}
