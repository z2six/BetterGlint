package org.z2six.betterglint.config;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.z2six.betterglint.Constants;
import org.z2six.betterglint.client.BetterGlintClientConfig;

public final class BetterGlintNeoForgeClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue DISABLE_VANILLA_GLINT;
    public static final ModConfigSpec.BooleanValue ENABLE_OUTLINE;
    public static final ModConfigSpec.DoubleValue OUTLINE_TRANSPARENCY;
    public static final ModConfigSpec.ConfigValue<String> OUTLINE_COLOR;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("BetterGlint client settings").push("client");

        DISABLE_VANILLA_GLINT = BUILDER
            .comment("Disables the vanilla enchantment glint render layer.")
            .define("disableVanillaGlint", true);

        ENABLE_OUTLINE = BUILDER
            .comment("Enables the enchantment outline effect.")
            .define("enableEnchantmentOutline", true);

        OUTLINE_TRANSPARENCY = BUILDER
            .comment("Outline transparency from 0.0 (opaque) to 1.0 (fully transparent).")
            .defineInRange("outlineTransparency", 0.0, 0.0, 1.0);

        OUTLINE_COLOR = BUILDER
            .comment("Outline color as HEX RGB, e.g. #AA00FF.")
            .define("outlineColor", "#AA00FF");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private BetterGlintNeoForgeClientConfig() {
    }

    public static void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() != ModConfig.Type.CLIENT || !event.getConfig().getModId().equals(Constants.MOD_ID)) {
            return;
        }
        applyToCommon();
    }

    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() != ModConfig.Type.CLIENT || !event.getConfig().getModId().equals(Constants.MOD_ID)) {
            return;
        }
        applyToCommon();
    }

    private static void applyToCommon() {
        BetterGlintClientConfig.update(
            DISABLE_VANILLA_GLINT.get(),
            ENABLE_OUTLINE.get(),
            OUTLINE_TRANSPARENCY.get(),
            OUTLINE_COLOR.get()
        );
    }
}

