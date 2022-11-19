package games.play4ever.mapclocks;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapPalette;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Clock {

    private ClockRenderer clockRenderer = null;

    private BufferedImage background;

    /**
     * Updated final images to display in maps. 12 images are updated every minute,
     * to have one image for every time zone (offset).
     */
    private BufferedImage[] updated = new BufferedImage[12];

    private MinecraftColors minuteHandColor = MinecraftColors.DARK_GRAY;
    private MinecraftColors hourHandColor = MinecraftColors.DARK_GRAY;
    private MinecraftColors centerColor = MinecraftColors.DARK_GRAY;

    private int offsetX = 64;
    private int offsetY = 64;

    private int minuteHandWidth = 6;
    private int hourHandWidth = 10;
    private int centerWidth = 4;
    private int radius = 60;

    private Color backgroundColor = null;

    // digital clock digits and separator (colon)
    private BufferedImage[] digits = new BufferedImage[10];
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

        String backgroundColorTxt = config.getString("background-color");
        if(backgroundColorTxt != null) {
            try {
                String redTxt = backgroundColorTxt.substring(0, backgroundColorTxt.indexOf(","));
                String greenTxt = backgroundColorTxt.substring(backgroundColorTxt.indexOf(",") + 1, backgroundColorTxt.lastIndexOf(","));
                String blueTxt = backgroundColorTxt.substring(backgroundColorTxt.lastIndexOf(",") + 1);
                int red = Integer.parseInt(redTxt);
                int green = Integer.parseInt(greenTxt);
                int blue = Integer.parseInt(blueTxt);
                this.backgroundColor = new Color(red, green, blue);
            } catch(Exception e) {
                MapClocks.logError("Failed to set background color: " + backgroundColorTxt + ", reason: " + e);
            }
        }

        this.radius = config.getInt("radius", 60);

        String minuteHandColorCode = config.getString("colors.minutes");
        String hourHandColorCode = config.getString("colors.hours");
        String centerColorCode = config.getString("colors.center");

        this.minuteHandColor = MinecraftColors.getByCode(minuteHandColorCode, MinecraftColors.DARK_GRAY);
        this.hourHandColor = MinecraftColors.getByCode(hourHandColorCode, MinecraftColors.DARK_GRAY);
        this.centerColor = MinecraftColors.getByCode(centerColorCode, MinecraftColors.DARK_GRAY);

        this.offsetX = config.getInt("offset.x", 64);
        this.offsetY = config.getInt("offset.y", 64);

        this.minuteHandWidth = config.getInt("width.minute", 6);
        this.hourHandWidth = config.getInt("width.hour", 8);
        this.centerWidth = config.getInt("width.center", 12);

    }

    public BufferedImage getUpdated(int hour, int offset) {
        int index = hour + offset;
        if(index < 1) {
            index += 12;
        } else if(index > 12) {
            index -= 12;
        }
        return this.updated[index - 1];
    }

    public void updateImages() {
        for(int hour = 0; hour < 12; hour ++) {
            MapClocks.logInfo("-> update clock image no. " + hour);
            this.updated[hour] = this.clockRenderer.renderClock(hour + 1);
        }
    }

    public TYPES getType() {
        return this.clockType;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
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

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getRadius() {
        return radius;
    }

    public int getCenterWidth() {
        return centerWidth;
    }

    public int getMinuteHandWidth() {
        return minuteHandWidth;
    }

    public int getHourHandWidth() {
        return hourHandWidth;
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
