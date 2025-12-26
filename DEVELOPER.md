# Rock Engine - Developer Guide 🛠️

Rock is designed as an **Engine**, meaning its core logic can be swapped out or extended by other mods (e.g., a "Nations" mod).

## Architecture

The core of Rock is the `PermissionProvider` interface. By default, Rock uses `DefaultPermissionProvider`, which checks the local `ClaimManager` (NBT storage).

You can **override** this logic to inject your own permissions system (e.g., SQL database, team-based logic, global bans) while keeping Rock's event handling and visual feedback.

## Integration

### 1. Setting the Provider
To take control of permission checks, set your provider in your mod's initializer:

```java
import com.roocky.foundation.RockAPI;
import com.roocky.foundation.api.service.PermissionProvider;

public class MyNationsMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Inject your custom logic
        RockAPI.setProvider(new MyNationsProvider());
    }
}
```

### 2. Implementing PermissionProvider
Your provider must implement the `PermissionProvider` interface:

```java
public class MyNationsProvider implements PermissionProvider {

    @Override
    public boolean checkPermission(ServerPlayerEntity player, ChunkPos pos, ClaimPermission permission) {
        // Your Custom Logic
        if (MyNationsDB.isWarZone(pos)) return true;
        
        // Fallback to default if needed, or implement full custom logic
        return false;
    }

    @Override
    public String getDenialMessage(ServerPlayerEntity player, ChunkPos pos) {
        return "§cYou cannot build in this Nation's territory!";
    }
}
```

### 3. Debugging
RockAPI provides tools to help you debug your integration:

```java
// Check if debug mode is active
if (RockAPI.isDebugging()) {
    RockAPI.log("Custom check for " + player.getName().getString());
}

// Check if a player is in bypass mode
if (RockAPI.isBypassing(player.getUuid())) {
    return true;
}
```

## Events API
Rock exposes Fabric-standard events in `ClaimEvents`. You can listen to these if you need to react to actions without replacing the entire Provider.

| Event Field | Description |
| :--- | :--- |
| `ENTER_CHUNK` | Fired when a player crosses a chunk boundary. ideal for UI updates. |
| `EXIT_CHUNK` | Fired when a player leaves a chunk. |
| `BLOCK_BREAK` | Cancellable event for breaking blocks. |
| `BLOCK_PLACE` | Cancellable event for placing blocks. |
| `BLOCK_INTERACT` | Cancellable event for right-clicking blocks. |
| `ENTITY_INTERACT`| Cancellable event for interacting with entities. |
| `EXPLOSION` | Cancellable event for explosion damage. |

### Visuals
The client-side visual engine listens for `VisualPayload` (Channel: `rock:claim_visuals`).
To trigger visuals manually (e.g., if you add a wand item):

```java
import com.roocky.foundation.network.PacketHelper;

// Send a red border to the player for the current chunk
PacketHelper.sendVisuals(player, chunkPos, 0xFF0000);
```
