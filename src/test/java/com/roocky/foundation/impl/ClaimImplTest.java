package com.roocky.foundation.impl;

import com.roocky.foundation.api.model.ClaimPermission;
import com.roocky.foundation.api.model.ClaimType;
import net.minecraft.util.math.ChunkPos;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class ClaimImplTest {

    @Test
    public void testOwnerHasAllPermissions() {
        UUID owner = UUID.randomUUID();
        ChunkPos pos = new ChunkPos(0, 0);
        ClaimManager.ClaimImpl claim = new ClaimManager.ClaimImpl(owner, pos, ClaimType.PLAYER);

        assertTrue(claim.hasPermission(owner, ClaimPermission.BLOCK_BREAK));
        assertTrue(claim.hasPermission(owner, ClaimPermission.BLOCK_PLACE));
    }

    @Test
    public void testOtherPlayerNoPermissionsInitially() {
        UUID owner = UUID.randomUUID();
        UUID other = UUID.randomUUID();
        ChunkPos pos = new ChunkPos(0, 0);
        ClaimManager.ClaimImpl claim = new ClaimManager.ClaimImpl(owner, pos, ClaimType.PLAYER);

        assertFalse(claim.hasPermission(other, ClaimPermission.BLOCK_BREAK));
    }

    @Test
    public void testGrantAndRevokePermission() {
        UUID owner = UUID.randomUUID();
        UUID other = UUID.randomUUID();
        ChunkPos pos = new ChunkPos(0, 0);
        ClaimManager.ClaimImpl claim = new ClaimManager.ClaimImpl(owner, pos, ClaimType.PLAYER);

        claim.grantPermission(other, ClaimPermission.BLOCK_BREAK);
        assertTrue(claim.hasPermission(other, ClaimPermission.BLOCK_BREAK));
        assertFalse(claim.hasPermission(other, ClaimPermission.BLOCK_PLACE));

        claim.revokePermission(other, ClaimPermission.BLOCK_BREAK);
        assertFalse(claim.hasPermission(other, ClaimPermission.BLOCK_BREAK));
    }

    @Test
    public void testGrantAll() {
        UUID owner = UUID.randomUUID();
        UUID other = UUID.randomUUID();
        ChunkPos pos = new ChunkPos(0, 0);
        ClaimManager.ClaimImpl claim = new ClaimManager.ClaimImpl(owner, pos, ClaimType.PLAYER);

        claim.grantAll(other);
        for (ClaimPermission perm : ClaimPermission.values()) {
            assertTrue(claim.hasPermission(other, perm));
        }
    }
}
