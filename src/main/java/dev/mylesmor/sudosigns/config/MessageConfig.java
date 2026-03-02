package dev.mylesmor.sudosigns.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import dev.mylesmor.sudosigns.data.SignMessage;
import dev.mylesmor.sudosigns.data.SudoSign;

public class MessageConfig {

    private final FileConfiguration signConfig;
    private final ConfigManager configManager;

    MessageConfig(ConfigManager configManager) {

        this.configManager = configManager;
        this.signConfig = configManager.getSignConfig();

    }

    public void addMessageToConfig(SudoSign s, SignMessage sm) {

        final HashMap<String, Double> map;
        final List<Map<?, ?>> messages = signConfig.getMapList("signs." + s.getName() + ".messages");
        map = new HashMap<>();
        map.put(sm.getMessage(), sm.getDelay());
        messages.add(map);
        signConfig.set("signs." + s.getName() + ".messages", messages);
        configManager.save();

    }

    public void deleteMessageFromConfig(SudoSign s, SignMessage sm, double oldDelay) {

        final List<Map<?, ?>> mapList = signConfig.getMapList("signs." + s.getName() + ".messages");
        Map<?, ?> found = null;
        for (Map<?, ?> map : mapList) {

            for (Map.Entry<?, ?> messages : map.entrySet()) {

                final String key = (String) messages.getKey();
                if (key.equals(sm.getMessage()) && (Double) messages.getValue() == oldDelay) {

                    found = map;
                    break;

                }

            }

        }

        if (found != null) {

            mapList.remove(found);

        }

        signConfig.set("signs." + s.getName() + ".messages", mapList);
        configManager.save();

    }

}
