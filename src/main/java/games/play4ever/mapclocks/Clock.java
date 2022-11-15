package games.play4ever.mapclocks;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapPalette;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Clock {

    private ClockRenderer clockRenderer = null;

    private BufferedImage background;

    /** Updated final image to display in maps. */
    private BufferedImage updated;

    private MinecraftColors minuteHandColor = MinecraftColors.DARK_GRAY;
    private MinecraftColors hourHandColor = MinecraftColors.DARK_GRAY;
    private MinecraftColors centerColor = MinecraftColors.DARK_GRAY;

    private int centerOffsetX = 64;
    private int centerOffsetY = 64;
    private int radius = 60;

    // digital clock digits and separator (colon)
    private BufferedImage[] digits;
    private BufferedImage separator;

    public enum TYPES {
        analog,
        digital
    }

    private TYPES clockType = TYPES.analog;

    private String name;

    private File directory;

    public Clock(File directory) throws IOException, InvalidConfigurationException {
        this.directory = directory;
        this.name = directory.getName();
        YamlConfiguration config = new YamlConfiguration();
        config.load(new File(directory,"clock.yml"));
        this.clockType = TYPES.valueOf(config.getString("type"));
        this.clockRenderer = new ClockRenderer(this);

        this.radius = config.getInt("radius", 60);

        String minuteHandColorCode = config.getString("colors.minutes");
        String hourHandColorCode = config.getString("colors.hours");
        String centerColorCode = config.getString("colors.center");

        this.minuteHandColor = MinecraftColors.getByCode(minuteHandColorCode, MinecraftColors.DARK_GRAY);
        this.hourHandColor = MinecraftColors.getByCode(hourHandColorCode, MinecraftColors.DARK_GRAY);
        this.centerColor = MinecraftColors.getByCode(centerColorCode, MinecraftColors.DARK_GRAY);

        this.centerOffsetX = config.getInt("center.x", 64);
        this.centerOffsetY = config.getInt("center.y", 64);

    }

    public BufferedImage getUpdated() {
        return this.updated;
    }

    public void updateImage() {
        this.updated = this.clockRenderer.renderClock();
    }

    public TYPES getType() {
        return this.clockType;
    }

    public MinecraftColors getMinuteHandColor() {
        return minuteHandColor;
    }

    public MinecraftColors getHourHandColor() {
        return hourHandColor;
    }

    public MinecraftColors getCenterColor() {
        return centerColor;
    }

    public int getCenterOffsetX() {
        return centerOffsetX;
    }

    public int getCenterOffsetY() {
        return centerOffsetY;
    }

    public int getRadius() {
        return radius;
    }

    public void initialize() throws InvalidConfigurationException {
        this.background = MapPalette.resizeImage(loadImage(new File(directory, "background.png")));

        if(this.clockType == TYPES.digital) {
            this.separator = loadImage(new File(directory, "separator.png"));
            for(int i = 0; i < 10; i++) {
                this.digits[i] = loadImage(new File(directory, String.valueOf(i) + ".png"));
            }
        }
    }

    private BufferedImage loadImage(File imageFile) throws InvalidConfigurationException {
        try {
            return ImageIO.read(imageFile);
        } catch(IOException e) {
            MapClocks.logError("Failed to load image '" + imageFile.getName() + "': " + e);
            throw new InvalidConfigurationException("Failed to load image '" + imageFile.getName() + "': " + e);
        }
    }

    public TYPES getClockType() {
        return this.clockType;
    }

    public String getName() {
        return this.name;
    }

    public BufferedImage getBackground() {
        return background;
    }

    public BufferedImage[] getDigits() {
        return digits;
    }

    public BufferedImage getSeparator() {
        return separator;
    }
}
