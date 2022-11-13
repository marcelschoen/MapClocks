package games.play4ever.mapclocks;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ClockManager implements Listener {
    private static ClockManager instance = null;
    public static ClockManager getInstance() {
        if (instance == null) {
            instance = new ClockManager();
        }
        return instance;
    }
    private CustomFile createdClockMapsStorage = new CustomFile();

    private Map<Integer, String> savedClocks = new HashMap<Integer, String>();

    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, MapClocks.getPlugin(MapClocks.class));
    }
    @EventHandler
    public void onMapInitEvent(MapInitializeEvent event) {
        MapClocks.logInfo("Map initialization event");
        if (hasClock(event.getMap().getId())) {
            MapClocks.logInfo("Map ID exists");
            MapView view = event.getMap();
            view.getRenderers().clear();
            String clockName = ClockManager.getInstance().getClock(view.getId());
            MapClocks.logInfo("Clock name: " + clockName);
            if(clockName == null) {
                MapClocks.logError("Invalid map ID, no clock name found: " + view.getId());
                return;
            }
            Clock clock = MapClocks.getClockByName(clockName);
            MapClocks.logInfo("Clock: " + clock);
            if(clock == null) {
                MapClocks.logError("Invalid clock name, no clock found: " + clockName);
                return;
            }
            view.addRenderer(new ClockRenderer(clock));
            view.setScale(MapView.Scale.FARTHEST);
            view.setTrackingPosition(false);
        }
    }

    public void saveClock(Integer mapId, String clockName) {
        getData().set("ids." + mapId, clockName);
        saveData();
    }

    public boolean hasClock(int id) {
        return savedClocks.containsKey(id);
    }
    public String getClock(int id) {
        return savedClocks.get(id);
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