package artemis.better_climbing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("better_climbing_modmenu.json");

    public boolean improveHorizontalMovement = true;
    public boolean fastDescent = true;
    public boolean fastClimbing = true;
    public boolean climbingJump = true;
    public boolean cancelUnintentionalCollision = true;

    private static Config INSTANCE = new Config();

    private Config() {}

    public static Config get() {
        return INSTANCE;
    }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                try (Reader reader = Files.newBufferedReader(FILE)) {
                    Config loaded = GSON.fromJson(reader, Config.class);
                    if (loaded != null) {
                        INSTANCE = loaded;
                    }
                }
            } else {
                save();
            }
        } catch (Exception e) {
            System.err.println("[Better Climbing] Failed to load config: " + e);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (Exception e) {
            System.err.println("[Better Climbing] Failed to save config: " + e);
        }
    }
}
