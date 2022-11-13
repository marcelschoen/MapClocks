package games.play4ever.mapclocks;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Clock {

    private enum TYPES {
        analog,
        digital
    }

    private TYPES clockType = TYPES.analog;

    public Clock(File directory) throws IOException, InvalidConfigurationException {
        YamlConfiguration config = new YamlConfiguration();
        config.load("clock.yml");

        config.get("type");
        this.clockType = TYPES.valueOf(config.getString("type"));
    }


}
