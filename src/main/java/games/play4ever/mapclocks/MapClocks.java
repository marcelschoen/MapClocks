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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Allows to use maps as clocks.
 *
 * @author Marcel Schoen
 */
public final class MapClocks extends JavaPlugin implements CommandExecutor, TabCompleter {
    private final String CONFIG_FILENAME = "config.yml";

    private Map<String, Clock.TYPES> includedClocks = new HashMap<>();

    private List<String> offsets = Arrays.asList(
            "-12", "-11", "-10", "-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1",
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");

    private FileStorage userDataStorage = null;

    @Override
    public void onEnable() {
        userDataStorage = FileStorage.getInstance();

        includedClocks.put("analog", Clock.TYPES.analog);
        includedClocks.put("digital", Clock.TYPES.analog);
        includedClocks.put("analog_square_blue", Clock.TYPES.analog);
        includedClocks.put("analog_square_wooden", Clock.TYPES.analog);
        includedClocks.put("digital_lcd", Clock.TYPES.analog);
        includedClocks.put("digital_bookshelf", Clock.TYPES.analog);

        // Prefix as defined in "plugin.yml"
        Objects.requireNonNull(getCommand("mapclocks")).setExecutor(this);
        Objects.requireNonNull(getCommand("mapclocks")).setTabCompleter(this);

        // Plugin startup logic

        ClockManager clockManager = ClockManager.getInstance();
        clockManager.init();
        readConfig();
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
                extractResourceIfDoesNotExist("clocks/" + clockName + "/clock.yml");
            }

            for(String clockName: clocksDir.list()) {
                File clockDir = new File(clocksDir, clockName);
                Clock clock = ClockManager.getClockByName(clockName);
                if(clock == null) {
                    logInfo("Adding clock '" + clockName + "' to clock manager...");
                    clock = new Clock(clockName);
                    ClockManager.addClock(clock);
                }
                try {
                    clock.configure(clockDir);
                } catch(InvalidConfigurationException ex) {
                    MapClocks.logError("Failed to load clock from directory: " + clockDir.getName() + " / " + ex);
                }

                extractResourceIfDoesNotExist("clocks/" + clockName + "/background.png");
                if(clock.getType() == Clock.TYPES.digital) {
                    for(int i = 0; i < 10; i++) {
                        extractResourceIfDoesNotExist("clocks/" + clockName + "/" + i + ".png");
                    }
                    extractResourceIfDoesNotExist("clocks/" + clockName + "/separator.png");
                }
            }

            YamlConfiguration config = new YamlConfiguration();
            config.load(configFile);

            ClockManager.initializeAllClocks();
            ClockManager.updateAllClockImages();
            ClockUpdateThread.launch();

            logInfo("Configuration loaded.");

        } catch (Exception e) {
            MapClocks.logError("Failed to load configuration: " + e);
            throw new RuntimeException(e);
        }
    }

    private void extractResourceIfDoesNotExist(String resourcePath) {
        File targetFile = new File(getDataFolder(), resourcePath);
        if(!targetFile.exists()) {
            saveResource(resourcePath, false);
        }
    }

    private static boolean isConsoleOrOP(CommandSender sender) {
        return (!(sender instanceof Player) || ((Player)sender).isOp());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        boolean consoleOrOP = isConsoleOrOP(sender);
        if(args != null && args.length > 0) {
            if(args.length == 1) {
                if(consoleOrOP) {
                    return Arrays.asList("reload", "help", "give", "offset");
                } else {
                    return Arrays.asList("help", "offset");
                }
            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("give") && consoleOrOP) {
                    return ClockManager.getClockNames();
                } else if(args[0].equalsIgnoreCase("offset")) {
                    return offsets;
                }
            } else if(args.length == 3 && consoleOrOP) {
                return Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        boolean consoleOrOP = isConsoleOrOP(sender);

        final String cmd = command.getName().toLowerCase();
        if (!cmd.equals("mapclocks") && !cmd.equals("mclocks")) {
            return false;
        }

        if (args[0].equalsIgnoreCase("reload") && consoleOrOP) {
            logInfo("Reloading MapClocks configuration...");
            readConfig();
            return true;
        } else if (args[0].equalsIgnoreCase("help")) {
            showHelp(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("offset")) {
            if(sender.getName() != null) {
                try {
                    int value = Integer.parseInt(args[1]);
                    if(value < -12 || value > 12) {
                        sender.sendMessage("Offset value must be a number ranging from -12 to 12!");
                    } else {
                        userDataStorage.getConfig().set("players." + sender.getName() + ".offset", value);
                        userDataStorage.saveConfig();
                        sender.sendMessage("Offset set to " + value);
                    }
                } catch(Exception e) {
                    logError("Invalid offset. Reason: " + e);
                    sender.sendMessage("Offset value must be a number ranging from -12 to 12!");
                }
            }
        } else if (args[0].equalsIgnoreCase("give") && consoleOrOP) {
            if(args.length != 3) {
                sender.sendMessage(ChatColor.RED + "Invalid arguments for 'give' command, must be 3, see help:");
                showHelp(sender);
            } else {
                String clockName = args[1];
                if(!ClockManager.hasClock(clockName)) {
                    sender.sendMessage(ChatColor.RED + "Clock '" + clockName + "' not found, check for typo.");
                } else {
                    String playerName = args[2];
                    Player player = Bukkit.getPlayer(playerName);
                    if(player == null) {
                        sender.sendMessage(ChatColor.RED + "Player '" + playerName + "' not found, check for typo.");
                    } else {
                        logInfo("Give clock to player now...");
                        MapView view = Bukkit.createMap(player.getWorld());

                        view.getRenderers().clear();
                        Clock clock = ClockManager.getClockByName(clockName);
                        view.addRenderer(new ClockRenderer(clock));
                        view.setScale(MapView.Scale.FARTHEST);
                        view.setTrackingPosition(false);

                        ClockManager manager = ClockManager.getInstance();
                        manager.saveClock(view.getId(), clockName);

                        ItemStack map = new ItemStack(Material.FILLED_MAP);

                        MapMeta meta = (MapMeta) map.getItemMeta();
                        meta.addEnchant(Enchantment.LURE, 1, false);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        meta.setDisplayName(getColor("&r&fClock " + clock.getDisplayName()));
                        meta.setLore(Arrays.stream(clock.getLore()).map(l -> getColor(l)).collect(Collectors.toList()));
                        meta.setMapView(view);
                        map.setItemMeta(meta);
                        //Give items and drop if inventory is full
                        HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(map);
                        for (Map.Entry<Integer, ItemStack> entry : failedItems.entrySet()) {
                            player.getWorld().dropItem(player.getLocation(), entry.getValue());
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    private static String getColor(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private void showHelp(final CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "MapClocks commands:");
        sender.sendMessage(ChatColor.GREEN + "help - shows this help");
        sender.sendMessage(ChatColor.GREEN + "offset - set your timezone offset");
        if(isConsoleOrOP(sender)) {
            sender.sendMessage(ChatColor.GREEN + "give <clock> <playername> - OP/console only: Give clock <clockname> to player <player>");
            sender.sendMessage(ChatColor.GREEN + "reload - OP/console only: reload MapClocks configuration");
        }
    }

    @Override
    public void onDisable() {
        // TODO - disable MapClock update thread
    }
}
