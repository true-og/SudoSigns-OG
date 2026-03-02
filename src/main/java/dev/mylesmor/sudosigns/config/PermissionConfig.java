package dev.mylesmor.sudosigns.config;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import dev.mylesmor.sudosigns.data.SudoSign;

public class PermissionConfig {

    private final FileConfiguration signConfig;
    private final ConfigManager configManager;

    PermissionConfig(ConfigManager configManager) {

        this.configManager = configManager;
        this.signConfig = configManager.getSignConfig();

    }

    public void addPermissionToConfig(SudoSign s, String perm) {

        final String path = "signs." + s.getName() + ".permissions";
        final List<String> perms = signConfig.getStringList(path);
        perms.add(perm);
        signConfig.set(path, perms);
        configManager.save();

    }

    public void deletePermissionFromConfig(SudoSign s, String perm) {

        final String path = "signs." + s.getName() + ".permissions";
        final List<String> perms = signConfig.getStringList(path);
        perms.remove(perm);
        signConfig.set(path, perms);
        configManager.save();

    }

}
