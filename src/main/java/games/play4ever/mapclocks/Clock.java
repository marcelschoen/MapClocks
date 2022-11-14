package games.play4ever.mapclocks;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapPalette;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Clock {

    private BufferedImage background;

    // analog clock hands (hours and minutes)
    private BufferedImage hourHand;
    private BufferedImage minuteHand;
    private BufferedImage center;

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
    }

    public TYPES getType() {
        return this.clockType;
    }

    public void initialize() throws InvalidConfigurationException {
        this.background = MapPalette.resizeImage(loadImage(new File(directory, "background.png")));

        if(this.clockType == TYPES.analog) {
            this.center = loadImage(new File(directory, "center.png"));
            this.hourHand = loadImage(new File(directory, "hour_hand.png"));
            this.minuteHand = loadImage(new File(directory, "minute_hand.png"));
        } else {
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

    public BufferedImage getCenter() {
        return center;
    }

    public BufferedImage getHourHand() {
        return hourHand;
    }

    public BufferedImage getMinuteHand() {
        return minuteHand;
    }

    public BufferedImage[] getDigits() {
        return digits;
    }

    public BufferedImage getSeparator() {
        return separator;
    }
}
