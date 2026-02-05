package com.roocky.foundation.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.UUID;

public class HeadUtil {

    public static ItemStack getPlayerHead(String name) {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        // Create a simple profile with name
        GameProfile profile = new GameProfile(null, name);
        stack.set(DataComponentTypes.PROFILE, new ProfileComponent(profile));
        return stack;
    }

    public static ItemStack getCustomHead(String displayName, String base64Texture) {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);

        // Random UUID to avoid conflicts, or fixed if we want stacking?
        GameProfile profile = new GameProfile(UUID.randomUUID(), "custom_head");
        profile.getProperties().put("textures", new Property("textures", base64Texture));

        stack.set(DataComponentTypes.PROFILE, new ProfileComponent(profile));

        if (displayName != null) {
            stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(displayName));
        }

        return stack;
    }
}
