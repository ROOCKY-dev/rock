package com.roocky.foundation;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class RockAPITest {

    @Test
    public void testIsBypassing() {
        UUID player = UUID.randomUUID();

        // Initially should be false
        assertFalse(RockAPI.isBypassing(player), "Player should not be bypassing initially");

        // Set bypass to true
        RockAPI.setBypass(player, true);
        assertTrue(RockAPI.isBypassing(player), "Player should be bypassing after setBypass(true)");

        // Set bypass to false
        RockAPI.setBypass(player, false);
        assertFalse(RockAPI.isBypassing(player), "Player should not be bypassing after setBypass(false)");
    }
}
