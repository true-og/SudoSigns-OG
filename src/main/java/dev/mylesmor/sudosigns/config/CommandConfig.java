package dev.mylesmor.sudosigns.config;

import dev.mylesmor.sudosigns.data.PlayerInput;
import dev.mylesmor.sudosigns.data.SignCommand;
import dev.mylesmor.sudosigns.data.SudoSign;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;

public class CommandConfig {

    private FileConfiguration signConfig;
    private ConfigManager configManager;

    CommandConfig(ConfigManager configManager) {
        this.configManager = configManager;
        this.signConfig = configManager.getSignConfig();
    }

    public void addCommandToConfig(SudoSign s, SignCommand cmd, PlayerInput type) {
        if (signConfig.isConfigurationSection("signs." + s.getName())) {
            HashMap<String, Double> map;
            switch (type) {
                case PLAYER_COMMAND:
                    List<Map<?, ?>> pCmds = signConfig.getMapList("signs." + s.getName() + ".player-commands");
                    map = new HashMap<>();
                    map.put(cmd.getCommand(), cmd.getDelay());
                    pCmds.add(map);
                    signConfig.set("signs." + s.getName() + ".player-commands", pCmds);
                    break;
                case CONSOLE_COMMAND:
                    List<Map<?, ?>> cCmds = signConfig.getMapList("signs." + s.getName() + ".console-commands");
                    map = new HashMap<>();
                    map.put(cmd.getCommand(), cmd.getDelay());
                    cCmds.add(map);
                    signConfig.set("signs." + s.getName() + ".console-commands", cCmds);
                    break;
                default:
                    break;
            }
        }
        configManager.save();
    }

    /**
     * Deletes a command from a sign in the config.
     * @param s The SudoSign object to edit.
     * @param s The .
     * @param s The SudoSign object to edit.
     */
    public void deleteCommandFromConfig(SudoSign s, SignCommand cmd, PlayerInput type, double oldDelay) {
        if (type.equals(PlayerInput.PLAYER_COMMAND)) {
            List<Map<?, ?>> mapList = signConfig.getMapList("signs." + s.getName() + ".player-commands");
            Map<?, ?> found = null;
            for (Map<?, ?> map : mapList) {
                for (Map.Entry<?, ?> cmds : map.entrySet()) {
                    if (cmds.getKey().equals(cmd.getCommand()) && (Double) cmds.getValue() == oldDelay) {
                        found = map;
                        break;
                    }
                }
            }
            if (found != null) {
                mapList.remove(found);
            }
            signConfig.set("signs." + s.getName() + ".player-commands", mapList);
        } else if (type.equals(PlayerInput.CONSOLE_COMMAND)) {
            List<Map<?, ?>> mapList = signConfig.getMapList("signs." + s.getName() + ".console-commands");
            Map<?, ?> found = null;
            for (Map<?, ?> map : mapList) {
                for (Map.Entry<?, ?> cmds : map.entrySet()) {
                    if (cmds.getKey().equals(cmd.getCommand()) && (Double) cmds.getValue() == oldDelay) {
                        found = map;
                        break;
                    }
                }
            }
            if (found != null) {
                mapList.remove(found);
            }
            signConfig.set("signs." + s.getName() + ".console-commands", mapList);
        }
        configManager.save();
    }
}
