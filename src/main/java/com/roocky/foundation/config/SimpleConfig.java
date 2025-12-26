package com.roocky.foundation.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("foundation_claims.json");

    private static SimpleConfig INSTANCE;

    public boolean enableExplosions = false;
    public int maxClaimsPerPlayer = 9;
    public String claimMessage = "§cThis chunk is owned by %s";

    public static SimpleConfig get() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                INSTANCE = GSON.fromJson(json, SimpleConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
                INSTANCE = new SimpleConfig();
            }
        } else {
            INSTANCE = new SimpleConfig();
            save();
        }
    }

    public static void save() {
        try {
            String json = GSON.toJson(INSTANCE);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
