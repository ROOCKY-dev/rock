package com.roocky.foundation.gui;

import com.roocky.foundation.api.model.Claim;
import com.roocky.foundation.api.model.ClaimPermission;
import com.roocky.foundation.api.model.PermissionCategory;
import com.roocky.foundation.impl.ClaimManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ClaimMenus {

    public static void openMainMenu(ServerPlayerEntity player, Claim claim) {
        InventoryMenu menu = new InventoryMenu(Text.literal("Claim Management"), 3);

        // Info
        ItemStack info = new ItemStack(Items.PAPER);
        info.set(DataComponentTypes.CUSTOM_NAME, Text.literal("§eClaim Info"));
        menu.setItem(13, info);

        // Trusted Players
        ItemStack trusted = HeadUtil.getPlayerHead("Steve");
        trusted.set(DataComponentTypes.CUSTOM_NAME, Text.literal("§bTrusted Players"));
        menu.setItem(11, trusted, (e) -> openTrustedPlayers(player, claim));

        // Close
        ItemStack close = new ItemStack(Items.BARRIER);
        close.set(DataComponentTypes.CUSTOM_NAME, Text.literal("§cClose"));
        menu.setItem(26, close, (e) -> player.closeHandledScreen());

        menu.open(player);
    }

    public static void openTrustedPlayers(ServerPlayerEntity player, Claim claim) {
        InventoryMenu menu = new InventoryMenu(Text.literal("Trusted Players"), 6);

        int slot = 0;
        for (UUID trusted : claim.getTrustedPlayers()) {
            if (slot >= 45) break;

            String name = player.getServer().getUserCache().getByUuid(trusted)
                    .map(profile -> profile.getName())
                    .orElse(trusted.toString());

            ItemStack head = HeadUtil.getPlayerHead(name);
            head.set(DataComponentTypes.CUSTOM_NAME, Text.literal("§a" + name));

            final int index = slot;
            menu.setItem(slot++, head, (e) -> openPlayerPermissions(player, claim, trusted));
        }

        // Back
        ItemStack back = new ItemStack(Items.ARROW);
        back.set(DataComponentTypes.CUSTOM_NAME, Text.literal("§7Back"));
        menu.setItem(45, back, (e) -> openMainMenu(player, claim));

        menu.open(player);
    }

    public static void openPlayerPermissions(ServerPlayerEntity player, Claim claim, UUID target) {
        InventoryMenu menu = new InventoryMenu(Text.literal("Permissions"), 6);

        // Default Category: BUILDING
        updatePermissionView(menu, player, claim, target, PermissionCategory.BUILDING);

        menu.open(player);
    }

    private static void updatePermissionView(InventoryMenu menu, ServerPlayerEntity player, Claim claim, UUID target, PermissionCategory category) {
        // Clear slots 9-44 (Middle area)
        for (int i = 9; i < 45; i++) {
            menu.setItem(i, ItemStack.EMPTY);
        }

        // Categories Row (0-8)
        int catSlot = 0;
        for (PermissionCategory cat : PermissionCategory.values()) {
             ItemStack icon = new ItemStack(Registries.ITEM.get(Identifier.of(cat.getIconItem())));
             String name = cat.getDisplayName();
             if (cat == category) {
                 name = "§l§6" + name;
             } else {
                 name = "§e" + name;
             }
             icon.set(DataComponentTypes.CUSTOM_NAME, Text.literal(name));

             menu.setItem(catSlot++, icon, (e) -> updatePermissionView(menu, player, claim, target, cat));
        }

        // Permissions
        int permSlot = 9;
        for (ClaimPermission perm : ClaimPermission.values()) {
            if (perm.getCategory() == category) {
                boolean has = claim.hasPermission(target, perm);

                ItemStack item = new ItemStack(Registries.ITEM.get(Identifier.of(perm.getIconItem())));
                item.set(DataComponentTypes.CUSTOM_NAME, Text.literal((has ? "§a" : "§c") + perm.getDisplayName()));

                menu.setItem(permSlot++, item, (e) -> {
                    if (has) claim.revokePermission(target, perm);
                    else claim.grantPermission(target, perm);

                    // Save
                    ClaimManager manager = ClaimManager.get(player.getServerWorld());
                    manager.save();

                    // Refresh
                    updatePermissionView(menu, player, claim, target, category);
                });
            }
        }

        // Back Button
        ItemStack back = new ItemStack(Items.ARROW);
        back.set(DataComponentTypes.CUSTOM_NAME, Text.literal("§7Back"));
        menu.setItem(45, back, (e) -> openTrustedPlayers(player, claim));
    }
}
