package games.play4ever.mapclocks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
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
    private Map<String, Clock> clocks = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        completions = new ArrayList<>(Arrays.asList("reload"));
        readConfig();
    }

    public static void logInfo(String message) {
        PluginLogger.getLogger(MapClocks.class.getName()).info("[MapClocks] " + message);
    }

    public static void logWarn(String message) {
        PluginLogger.getLogger(MapClocks.class.getName()).warning("[MapClocks] " + message);
    }

    private void readConfig() {

        try {
            YamlConfiguration config = new YamlConfiguration();
            config.load(CONFIG_FILENAME);

            File clocksDirectory = new File(getDataFolder(), "clocks");
            for(String subDirName : clocksDirectory.list()) {
                File subDir = new File(clocksDirectory, subDirName);
                if(subDir.isDirectory()) {
                    clocks.put(subDirName, new Clock(subDir));
                }
            }
            logInfo("[MapClocks] Number of clocks found: " + clocks.size());

            logInfo("[MapClocks] Configuration loaded.");

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        logInfo("Loaded custom placeholders configuration. Placeholders: ");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return completions;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final String cmd = command.getName().toLowerCase();
        if (!cmd.equals("mapclocks") && !cmd.equals("mclocks")) {
            return false;
        }
        if(args.length < 1) {
            String msg = "Missing command parameters.";
            sender.sendMessage(ChatColor.RED + msg);
            logWarn(msg);
        }

        if (args[0].equalsIgnoreCase("reload")) {
            logInfo("Reloading MapClocks configuration...");
            readConfig();
            return true;
        } else if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.GREEN + "MyCustomPlaceholders commands:");
            sender.sendMessage(ChatColor.GREEN + "help - shows this help");
            sender.sendMessage(ChatColor.GREEN + "reload - reload placeholder configuration");
            sender.sendMessage(ChatColor.GREEN + "set <name> <value> - Sets the configured placeholder <name> to the given <value>");
            return true;
        }
        return false;
    }

    @Override
    public void onDisable() {
        // TODO - disable MapClock update thread
    }
}
