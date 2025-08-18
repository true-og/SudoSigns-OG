package dev.mylesmor.sudosigns.config;

import dev.mylesmor.sudosigns.data.SudoSign;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;

public class PermissionConfig {

    private FileConfiguration signConfig;
    private ConfigManager configManager;

    PermissionConfig(ConfigManager configManager) {

        this.configManager = configManager;
        this.signConfig = configManager.getSignConfig();

    }

    public void addPermissionToConfig(SudoSign s, String perm) {

        String path = "signs." + s.getName() + ".permissions";
        List<String> perms = signConfig.getStringList(path);
        perms.add(perm);
        signConfig.set(path, perms);
        configManager.save();

    }

    public void deletePermissionFromConfig(SudoSign s, String perm) {

        String path = "signs." + s.getName() + ".permissions";
        List<String> perms = signConfig.getStringList(path);
        perms.remove(perm);
        signConfig.set(path, perms);
        configManager.save();

    }

}
