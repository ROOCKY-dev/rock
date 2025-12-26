# Rock Engine 🪨

![Fabric](https://img.shields.io/badge/Fabric-1.21+-blue?style=flat-square)
![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)

**Rock Engine** is a high-performance, API-first land claiming engine for Minecraft Fabric. Designed as a foundation for complex server environments, it provides a robust permission system, spatial awareness, and developer-friendly tools without checking external databases or heavy dependencies.

## Key Features

- **Granular Permissions**: Control exactly what players can do (`BREAK`, `PLACE`, `OPEN`, `REDSTONE`, `MANAGE`).
- **Spatial Awareness**:
  - **Action Bar Feedback**: Know immediately when you enter or exit claimed territory.
  - **Visual Borders**: See claim boundaries with particle effects when inspecting.
- **Performance First**: Built on native Fabric API events and vanilla-style NBT persistence (per-dimension chunk storage).
- **Admin Suite**:
  - **Hot-Reload**: Update config without restarting the server.
  - **Verbose Debug**: Console logging for every permission check to trace issues.
  - **Bypass Mode**: Allow staff to override protections seamlessly.

## Commands

| Command | Permission | Description |
| :--- | :--- | :--- |
| `/claim` | User | Claims the chunk you are standing in. |
| `/claim remove` | User | Unclaims the current chunk (must be owner). |
| `/claim info` | User | Displays owner, claim type, and **renders visual borders**. |
| `/claim trust <player> [perm]` | User | Grants permissions. If `perm` is omitted, grants **ALL**. |
| `/claim admin reload` | Admin (Lvl 2) | Hot-reloads `foundation_claims.json`. |
| `/claim admin debug` | Admin (Lvl 2) | Toggles verbose console logging for permission checks. |
| `/claim admin bypass` | Admin (Lvl 2) | Toggles bypass mode for the executor. |

### Permissions List
When using `/claim trust <player> <perm>`, valid permissions are:
- `BLOCK_BREAK`
- `BLOCK_PLACE`
- `CONTAINER_OPEN`
- `INTERACT_REDSTONE`
- `INTERACT_BLOCK`
- `INTERACT_ENTITY`
- `MANAGE_PERMISSIONS`

## Configuration

The configuration file is located at `config/foundation_claims.json`.

```json
{
  "enableExplosions": false,
  "maxClaimsPerPlayer": 9,
  "claimMessage": "§cThis chunk is owned by %s"
}
```

- **`enableExplosions`**: If true, TNT and Creeper explosions will damage claimed land.
- **`maxClaimsPerPlayer`**: Hard limit on chunks per player.
- **`claimMessage`**: Message shown when action is denied. `%s` is the owner's name/UUID.
