package games.play4ever.mapclocks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

/**
 * Allows to use maps as clocks.
 *
 * @author Marcel Schoen
 */
public final class MapClocks extends JavaPlugin implements CommandExecutor, TabCompleter {
    private List<String> completions = new ArrayList<>();
    private final String CONFIG_FILENAME = "config.yml";

    /** Stores all configures clocks. */
    private static Map<String, Clock> clocks = new HashMap<>();

    private Map<String, Clock.TYPES> includedClocks = new HashMap<>();

    @Override
    public void onEnable() {
        includedClocks.put("analog", Clock.TYPES.analog);

        // Prefix as defined in "plugin.yml"
        Objects.requireNonNull(getCommand("mapclocks")).setExecutor(this);
        Objects.requireNonNull(getCommand("mapclocks")).setTabCompleter(this);

        // Plugin startup logic
        completions = new ArrayList<>(Arrays.asList("reload", "help", "give"));

        readConfig();
        ClockManager clockManager = ClockManager.getInstance();
        clockManager.init();
    }

    public static Clock getClockByName(String clockName) {
        return clocks.get(clockName);
    }

    public static void logInfo(String message) {
        PluginLogger.getLogger(MapClocks.class.getName()).info("[MapClocks] " + message);
    }

    public static void logWarn(String message) {
        PluginLogger.getLogger(MapClocks.class.getName()).warning("[MapClocks] " + message);
    }

    public static void logError(String message) {
        PluginLogger.getLogger(MapClocks.class.getName()).severe("[MapClocks] " + message);
    }

    private void readConfig() {

        try {
            File clocksDir = new File(getDataFolder(), "clocks");
            clocksDir.mkdirs();
            File configFile = new File(getDataFolder(), CONFIG_FILENAME);
            if(!configFile.exists()) {
                saveDefaultConfig();
            }
            for(String clockName : includedClocks.keySet()) {
                File clockDir = new File(clocksDir, clockName);
                clockDir.mkdirs();
                saveResource("clocks/" + clockName + "/clock.yml", false);
                Clock clock = new Clock(clockDir);
                if(clock.getClockType() == Clock.TYPES.analog) {
                    saveResource("clocks/" + clockName + "/minute_hand.png", false);
                    saveResource("clocks/" + clockName + "/hour_hand.png", false);
                }
                saveResource("clocks/" + clockName + "/background.png", false);
            }

            YamlConfiguration config = new YamlConfiguration();
            config.load(configFile);

            File clocksDirectory = new File(getDataFolder(), "clocks");
            for(String subDirName : clocksDirectory.list()) {
                File subDir = new File(clocksDirectory, subDirName);
                if(subDir.isDirectory()) {
                    try {
                        clocks.put(subDirName, new Clock(subDir));
                        completions.add("give " + subDirName);
                    } catch(InvalidConfigurationException ex) {
                        MapClocks.logError("Failed to load clock from directory: " + subDir.getName() + " / " + ex);
                    }
                }
            }
            logInfo("[MapClocks] Number of clocks found: " + clocks.size());

            logInfo("[MapClocks] Configuration loaded.");

        } catch (Exception e) {
            MapClocks.logError("Failed to load configuration: " + e);
            throw new RuntimeException(e);
        }
        logInfo("Loaded MapClocks configuration.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        logInfo("->>> COMPLETE / command: " + command.getName() + " / alias: " + alias + " / args: " + (args == null ? "0" : args.length));
        return completions;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        logInfo("===>>> onCommand: " + command.getName());
        final String cmd = command.getName().toLowerCase();
        if (!cmd.equals("mapclocks") && !cmd.equals("mclocks")) {
            return false;
        }
        for(String arg : args) {
            logInfo("===>>> argument: " + arg);
        }

        if (args[0].equalsIgnoreCase("reload")) {
            logInfo("Reloading MapClocks configuration...");
            readConfig();
            return true;
        } else if (args[0].equalsIgnoreCase("help")) {
            showHelp(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("give")) {
            logInfo("Give clock...");
            if(args.length != 3) {
                sender.sendMessage(ChatColor.RED + "Invalid arguments for 'give' command, must be 3, see help:");
                showHelp(sender);
            } else {
                String clockName = args[1];
                logInfo("Clock name: " + clockName);
                if(!clocks.containsKey(clockName)) {
                    sender.sendMessage(ChatColor.RED + "Clock '" + clockName + "' not found, check for typo.");
                } else {
                    String playerName = args[2];
                    logInfo("Player name: " + playerName);
                    Player player = Bukkit.getPlayer(playerName);
                    if(player == null) {
                        sender.sendMessage(ChatColor.RED + "Player '" + playerName + "' not found, check for typo.");
                    } else {
                        logInfo("Give clock to player now...");
                        MapView view = Bukkit.createMap(player.getWorld());
                        ItemStack map = new ItemStack(Material.FILLED_MAP);
                        MapMeta meta = (MapMeta) map.getItemMeta();
                        meta.setMapView(view);
                        map.setItemMeta(meta);
                        //Give items and drop if inventory is full
                        HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(map);
                        for (Map.Entry<Integer, ItemStack> entry : failedItems.entrySet()) {
                            player.getWorld().dropItem(player.getLocation(), entry.getValue());
                        }
                        ClockManager manager = ClockManager.getInstance();
                        manager.saveClock(view.getId(), clockName);
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void showHelp(final CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "MapClocks commands:");
        sender.sendMessage(ChatColor.GREEN + "help - shows this help");
        sender.sendMessage(ChatColor.GREEN + "give <clock> <playername> - OP/console only: Give clock <clockname> to player <player>");
        sender.sendMessage(ChatColor.GREEN + "reload - OP/console only: reload MapClocks configuration");
    }

    @Override
    public void onDisable() {
        // TODO - disable MapClock update thread
    }
}
