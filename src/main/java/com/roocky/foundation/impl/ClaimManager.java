package com.roocky.foundation.impl;

import com.roocky.foundation.api.model.Claim;
import com.roocky.foundation.api.model.ClaimPermission;
import com.roocky.foundation.api.model.ClaimType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClaimManager extends PersistentState {

    private final Map<ChunkPos, Claim> claims = new HashMap<>();

    public static ClaimManager get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                new Type<>(ClaimManager::new, ClaimManager::createFromNbt, null),
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

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList list = new NbtList();
        for (Map.Entry<ChunkPos, Claim> entry : claims.entrySet()) {
            NbtCompound claimTag = new NbtCompound();
            claimTag.putLong("Pos", entry.getKey().toLong());
            claimTag.putUuid("Owner", entry.getValue().getOwner());
            claimTag.putString("Type", entry.getValue().getType().name());
            
            // Save trusted players
            if (entry.getValue() instanceof ClaimImpl impl) {
                NbtList trustedList = new NbtList();
                for (UUID trusted : impl.trusted) {
                    NbtCompound trustedTag = new NbtCompound();
                    trustedTag.putUuid("UUID", trusted);
                    trustedList.add(trustedTag);
                }
                claimTag.put("Trusted", trustedList);
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
            if (claimTag.contains("Trusted")) {
                NbtList trustedList = claimTag.getList("Trusted", 10);
                for (int j = 0; j < trustedList.size(); j++) {
                     claim.trusted.add(trustedList.getCompound(j).getUuid("UUID"));
                }
            }
            
            manager.claims.put(pos, claim);
        }
        return manager;
    }

    // Simple implementation of the Claim interface for storage
    public static class ClaimImpl implements Claim {
        private final UUID owner;
        private final ChunkPos pos;
        private final ClaimType type;
        public final java.util.Set<UUID> trusted = new java.util.HashSet<>();

        public ClaimImpl(UUID owner, ChunkPos pos, ClaimType type) {
            this.owner = owner;
            this.pos = pos;
            this.type = type;
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
            if (type == ClaimType.ADMIN) return false; // Admin claims restricted by default
            return trusted.contains(player);
        }

        @Override
        public ChunkPos getPosition() {
            return pos;
        }
    }
}
