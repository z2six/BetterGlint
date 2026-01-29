package org.z2six.betterglint.client;

import java.util.Locale;
import java.util.regex.Pattern;

public final class BetterGlintClientConfig {
    private static final Pattern HEX_RGB = Pattern.compile("^#?[0-9a-fA-F]{6}$");
    private static final String DEFAULT_OUTLINE_COLOR_HEX = "#AA00FF";

    private static volatile boolean disableVanillaGlint = true;
    private static volatile boolean enableOutline = true;
    private static volatile double outlineTransparency = 0.0; // 0 = opaque, 1 = fully transparent
    private static volatile String outlineColorHex = DEFAULT_OUTLINE_COLOR_HEX;

    private BetterGlintClientConfig() {
    }

    public static boolean disableVanillaGlint() {
        return disableVanillaGlint;
    }

    public static boolean enableOutline() {
        return enableOutline;
    }

    public static double outlineTransparency() {
        return outlineTransparency;
    }

    public static String outlineColorHex() {
        return outlineColorHex;
    }

    public static void update(boolean disableVanillaGlint, boolean enableOutline, double outlineTransparency, String outlineColorHex) {
        BetterGlintClientConfig.disableVanillaGlint = disableVanillaGlint;
        BetterGlintClientConfig.enableOutline = enableOutline;
        BetterGlintClientConfig.outlineTransparency = clamp01(outlineTransparency);
        BetterGlintClientConfig.outlineColorHex = sanitizeHexRgb(outlineColorHex);
    }

    public static int outlineRed() {
        return (parseHexRgb(outlineColorHex) >> 16) & 0xFF;
    }

    public static int outlineGreen() {
        return (parseHexRgb(outlineColorHex) >> 8) & 0xFF;
    }

    public static int outlineBlue() {
        return parseHexRgb(outlineColorHex) & 0xFF;
    }

    public static int outlineAlpha() {
        return (int) Math.round(255.0 * (1.0 - clamp01(outlineTransparency)));
    }

    private static double clamp01(double value) {
        if (value < 0.0) return 0.0;
        if (value > 1.0) return 1.0;
        return value;
    }

    private static String sanitizeHexRgb(String hex) {
        if (hex == null) {
            return DEFAULT_OUTLINE_COLOR_HEX;
        }
        String trimmed = hex.trim();
        if (!HEX_RGB.matcher(trimmed).matches()) {
            return DEFAULT_OUTLINE_COLOR_HEX;
        }
        String upper = trimmed.toUpperCase(Locale.ROOT);
        return upper.startsWith("#") ? upper : ("#" + upper);
    }

    private static int parseHexRgb(String hex) {
        String sanitized = sanitizeHexRgb(hex);
        String withoutHash = sanitized.startsWith("#") ? sanitized.substring(1) : sanitized;
        try {
            return Integer.parseInt(withoutHash, 16);
        } catch (NumberFormatException e) {
            return Integer.parseInt(DEFAULT_OUTLINE_COLOR_HEX.substring(1), 16);
        }
    }
}

