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

    private BufferedImage background;

    private enum TYPES {
        analog,
        digital
    }

    private TYPES clockType = TYPES.analog;

    public Clock(File directory) throws IOException, InvalidConfigurationException {
        YamlConfiguration config = new YamlConfiguration();
        config.load(new File(directory,"clock.yml"));

        config.get("type");
        this.clockType = TYPES.valueOf(config.getString("type"));

        this.background = MapPalette.resizeImage(ImageIO.read(new File(directory, "background.png")));
    }

    public Image getBackground() {
        return background;
    }
}
