package games.play4ever.mapclocks;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Manages mapping clocks to maps, storing and reloading
 * map data etc.
 *
 * @author Marcel Schoen
 */
public class ClockManager implements Listener {
    private static ClockManager instance = null;

    /** Stores all configures clocks. */
    private static Map<String, Clock> clocks = new HashMap<>();

    public static ClockManager getInstance() {
        if (instance == null) {
            instance = new ClockManager();
        }
        return instance;
    }

    public static void addClock(Clock clock) {
        clocks.put(clock.getName(), clock);
    }

    public static boolean hasClock(String name) {
        return clocks.containsKey(name);
    }

    public static List<String> getClockNames() {
        return clocks.keySet().stream().collect(Collectors.toList());
    }

    public static void initializeAllClocks() {
        clocks.values().stream().forEach(clock -> {
            try {
                clock.initialize();
            } catch (InvalidConfigurationException e) {
                MapClocks.logError("Failed to initialize clock: " + clock.getName() + ", reason: " + e);
            }
        });
    }

    public static void updateAllClockImages() {
        MapClocks.logInfo("Updating " + clocks.size() + " clock images...");
        clocks.values().stream().forEach(clock -> clock.updateImage() );
    }

    public static Clock getClockByName(String clockName) {
        return clocks.get(clockName);
    }

    private CustomFile createdClockMapsStorage = new CustomFile();

    @EventHandler
    public void onMapInitEvent(MapInitializeEvent event) {
        MapClocks.logInfo("> onMapInitEvent / map ID: " + event.getMap().getId());
        if (hasClock(event.getMap().getId())) {
            String clockName = (String)getData().get("ids." + event.getMap().getId());
            Clock clock = getClockByName(clockName);
            MapView view = event.getMap();
            view.getRenderers().clear();
            view.addRenderer(new ClockRenderer(clock));
            view.setScale(MapView.Scale.FARTHEST);
            view.setTrackingPosition(false);
        }
    }

    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, MapClocks.getPlugin(MapClocks.class));
        createdClockMapsStorage.reloadConfig();
    }

    public void saveClock(Integer mapId, String clockName) {
        MapClocks.logInfo(">> save map '" + mapId + "' with clock: " + clockName);
        getData().set("ids." + mapId, clockName);
        saveData();
    }

    public boolean hasClock(int id) {
        return getData().get("ids." + id) != null;
    }

    public FileConfiguration getData() {
        return createdClockMapsStorage.getConfig();
    }

    public void saveData() {
        createdClockMapsStorage.saveConfig();
    }
    class CustomFile {
        private final MapClocks plugin = MapClocks.getPlugin(MapClocks.class);
        private FileConfiguration dataConfig = null;
        private File dataConfigFile = null;
        private final String name = "clockMapData.yml";
        public CustomFile() {
            this.plugin.saveResource(name, false);
        }

        public void reloadConfig() {
            MapClocks.logInfo("> reloadConfig()");
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
}