package games.play4ever.mapclocks;

import java.awt.*;
import java.util.Arrays;

/**
 * Convenience handler for Minecraft color palette.
 *
 * @author Marcel Schoen
 */
public enum MinecraftColors {

    BLACK("&0", 0, 0, 0),
    DARK_BLUE("&1", 0, 0, 170),
    DARK_GREEN("&2", 0, 170, 0),
    DARK_AQUA("&3", 0, 170, 170),
    DARK_RED("&4", 170, 0, 0),
    DARK_PURPLE("&5", 170, 0, 170),
    GOLD("&6", 255, 170, 0),
    GRAY("&7", 170, 170, 170),
    DARK_GRAY("&8", 85, 85, 85),
    BLUE("&9", 85, 85, 255),
    GREEN("&a", 85, 255, 85),
    AQUA("&b", 85, 255, 255),
    RED("&c", 255, 85, 85),
    LIGHT_PURPLE("&d", 255, 85, 255),
    YELLOW("&e", 255, 255, 85),
    WHITE("&f", 255, 255, 255);

    private Color javaColor;

    private String minecraftCode;

    MinecraftColors(String minecraftCode, int red, int green, int blue) {
        this.javaColor = new Color(red, green, blue);
        this.minecraftCode = minecraftCode;
    }

    public static MinecraftColors getByCode(String code, MinecraftColors defaultValue) {
        return Arrays.stream(values()).filter(v -> v.minecraftCode.equals(code)).findFirst().orElse(defaultValue);
    }

    public Color getJavaColor() {
        return this.javaColor;
    }

    public String getMinecraftCode() {
        return this.minecraftCode;
    }
}
