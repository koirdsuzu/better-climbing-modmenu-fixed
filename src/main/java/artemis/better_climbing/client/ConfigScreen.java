package artemis.better_climbing.client;

import artemis.better_climbing.Config;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final Config config = Config.get();

    public ConfigScreen(Screen parent) {
        super(Component.literal("Better Climbing"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 100;
        int y = this.height / 2 - 90;
        int gap = 25;

        addToggle(x, y, "登攀中の横移動改善", () -> config.improveHorizontalMovement,
                value -> config.improveHorizontalMovement = value);
        addToggle(x, y + gap, "高速降下", () -> config.fastDescent,
                value -> config.fastDescent = value);
        addToggle(x, y + gap * 2, "高速登攀", () -> config.fastClimbing,
                value -> config.fastClimbing = value);
        addToggle(x, y + gap * 3, "登攀中のジャンプ", () -> config.climbingJump,
                value -> config.climbingJump = value);
        addToggle(x, y + gap * 4, "意図しない衝突を無視", () -> config.cancelUnintentionalCollision,
                value -> config.cancelUnintentionalCollision = value);

        addRenderableWidget(Button.builder(Component.literal("完了"), button -> onClose())
                .bounds(x, y + gap * 5 + 10, 200, 20)
                .build());
    }

    private void addToggle(int x, int y, String label, BooleanSupplier getter, Consumer<Boolean> setter) {
        Button button = addRenderableWidget(Button.builder(Component.empty(), b -> {
            boolean value = !getter.getAsBoolean();
            setter.accept(value);
            updateText(b, label, value);
        }).bounds(x, y, 200, 20).build());

        updateText(button, label, getter.getAsBoolean());
    }

    private static void updateText(Button button, String label, boolean enabled) {
        button.setMessage(Component.literal(label + ": " + (enabled ? "ON" : "OFF")));
    }

    @Override
    public void onClose() {
        Config.save();
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(font, title, width / 2, 30, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, delta);
    }
}
