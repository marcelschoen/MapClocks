package games.play4ever.mapclocks;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Handler for storing and retrieving data from and to a YAML file.
 *
 * @author Marcel Schoen
 */
class FileStorage {
    private final MapClocks plugin = MapClocks.getPlugin(MapClocks.class);
    private FileConfiguration dataConfig = null;
    private File dataConfigFile = null;
    private final String name = "clockMapData.yml";

    private static FileStorage instance = new FileStorage();

    public static FileStorage getInstance() {
        return instance;
    }

    private FileStorage() {
        this.plugin.saveResource(name, false);
    }

    public void reloadConfig() {
        if (dataConfigFile == null) {
            dataConfigFile = new File(plugin.getDataFolder(), name);
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(dataConfigFile);
    }

    public FileConfiguration getConfig() {
        if (this.dataConfig == null) {
            reloadConfig();
        }
        return this.dataConfig;
    }

    public void saveConfig() {
        if ((dataConfig == null) || (dataConfigFile == null)) {
            return;
        }
        try {
            getConfig().save(dataConfigFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to "
                    + dataConfigFile, e);
        }
    }
}
