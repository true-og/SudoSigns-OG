package dev.mylesmor.sudosigns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import dev.mylesmor.sudosigns.commands.Commands;
import dev.mylesmor.sudosigns.commands.SudoSignsTabCompleter;
import dev.mylesmor.sudosigns.config.ConfigManager;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.data.SudoUser;
import dev.mylesmor.sudosigns.listeners.ChatListener;
import dev.mylesmor.sudosigns.listeners.InventoryListener;
import dev.mylesmor.sudosigns.listeners.PlayerListener;
import dev.mylesmor.sudosigns.listeners.SignListener;
import net.trueog.diamondbankog.api.DiamondBankAPIJava;

/**
 * Main class for SudoSigns.
 * 
 * @author MylesMor
 * @author https://mylesmor.dev
 */
public class SudoSigns extends JavaPlugin {

    public static Map<String, SudoSign> signs = new HashMap<>();
    public static ArrayList<String> invalidSigns = new ArrayList<>();
    public static Map<UUID, SudoUser> users = new HashMap<>();
    public static ConfigManager config;
    public static Plugin sudoSignsPlugin;
    public static String version;
    public DiamondBankAPIJava diamondBankAPI;

    @Override
    public void onEnable() {

        final String version = getServer().getVersion().split("MC: ")[1];
        SudoSigns.version = StringUtils.substring(version, 0, version.length() - 1);

        final RegisteredServiceProvider<DiamondBankAPIJava> provider = getServer().getServicesManager()
                .getRegistration(DiamondBankAPIJava.class);
        if (provider == null) {

            getLogger().severe("DiamondBank-OG API is null – disabling plugin.");

            Bukkit.getPluginManager().disablePlugin(this);

            return;

        }

        this.diamondBankAPI = provider.getProvider();

        getServer().getPluginManager().registerEvents(new SignListener(diamondBankAPI), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        sudoSignsPlugin = this;

        // Import signs.yml file(s).
        config = new ConfigManager(diamondBankAPI);
        config.loadModules();

        // Import config.yml file.
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        this.getCommand("sudosigns").setExecutor(new Commands(diamondBankAPI));
        this.getCommand("sudosigns").setTabCompleter(new SudoSignsTabCompleter());

    }

    public static Plugin getPlugin() {

        return sudoSignsPlugin;

    }

    public static void setSudoSignsPlugin(Plugin sudoSignsPlugin) {

        SudoSigns.sudoSignsPlugin = sudoSignsPlugin;

    }

    @Override
    public void onDisable() {

        getLogger().info("SudoSigns-OG v" + version + " has been disabled.");

    }

}