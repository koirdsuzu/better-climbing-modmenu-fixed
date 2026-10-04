package artemis.better_climbing;

import net.fabricmc.api.ClientModInitializer;

public class BetterClimbingMod implements ClientModInitializer {
    public static final String MOD_ID = "better_climbing_modmenu";

    @Override
    public void onInitializeClient() {
        Config.load();
    }
}
