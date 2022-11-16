package games.play4ever.mapclocks;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static void clearClocks() {
        clocks.clear();
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
        MapClocks.logInfo("-> initialize all clocks");
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
        clocks.values().stream().forEach(clock -> clock.updateImages() );
    }

    public static Clock getClockByName(String clockName) {
        return clocks.get(clockName);
    }

    private FileStorage createdClockMapsStorage = FileStorage.getInstance();

    @EventHandler
    public void onMapInitEvent(MapInitializeEvent event) {
        MapClocks.logInfo("> onMapInitEvent / map ID: " + event.getMap().getId());
        int id = event.getMap().getId();
        boolean hasClock = createdClockMapsStorage.getConfig().get("ids." + id) != null;
        if (hasClock) {
            String clockName = (String)createdClockMapsStorage.getConfig().get("ids." + id);
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
        createdClockMapsStorage.getConfig().set("ids." + mapId, clockName);
        createdClockMapsStorage.saveConfig();
    }
}