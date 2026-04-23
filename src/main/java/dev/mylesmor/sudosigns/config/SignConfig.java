package dev.mylesmor.sudosigns.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.PlayerInput;
import dev.mylesmor.sudosigns.data.SignCommand;
import dev.mylesmor.sudosigns.data.SignMessage;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.util.Util;
import net.kyori.adventure.text.TextComponent;
import net.trueog.diamondbankog.api.DiamondBankAPIJava;
import net.trueog.utilitiesog.UtilitiesOG;

public class SignConfig {

    private FileConfiguration signConfig;
    private final ConfigManager configManager;
    private final DiamondBankAPIJava diamondBankAPI;

    SignConfig(ConfigManager configManager, DiamondBankAPIJava diamondBankAPI) {

        this.configManager = configManager;
        this.signConfig = configManager.getSignConfig();
        this.diamondBankAPI = diamondBankAPI;

    }

    public void setSignConfig(FileConfiguration signConfig) {

        this.signConfig = signConfig;

    }

    // Loads the signs from the config.
    public ArrayList<String> loadSigns() {

        final Map<String, SudoSign> tempSigns = new HashMap<>();
        final Set<String> loadedLocations = new HashSet<>();
        final Set<String> signSection = signConfig.getConfigurationSection("signs").getKeys(false);

        String name;
        final ArrayList<String> invalidSigns = new ArrayList<>();
        for (String key : signSection) {

            name = key;

            try {

                final ConfigurationSection locSec = signConfig.getConfigurationSection("signs." + key + ".location");

                final String world = locSec.getString("world");
                final double x = locSec.getDouble("x");
                final double y = locSec.getDouble("y");
                final double z = locSec.getDouble("z");
                final World w = Bukkit.getServer().getWorld(world);

                if (w != null) {

                    final Location loc = new Location(Bukkit.getServer().getWorld(world), x, y, z);
                    final String locationKey = world + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":"
                            + loc.getBlockZ();
                    if (!loadedLocations.add(locationKey)) {

                        invalidSigns.add(key);
                        Bukkit.getLogger()
                                .warning(SudoSigns.getPlugin().getConfig().getString("config.console-prefix")
                                        + "ERROR: Failed to load Sign " + key + " at " + world + " " + loc.getBlockX()
                                        + ", " + loc.getBlockY() + ", " + loc.getBlockZ()
                                        + "! Cause: another SudoSign already uses that block location. Skipping...");
                        continue;

                    }

                    final Sign sign = Util.getSign(loc.getBlock());
                    if (sign != null) {

                        final SudoSign ss = new SudoSign(key, diamondBankAPI);
                        ss.setSign(sign);

                        final List<Map<?, ?>> pCommands = signConfig.getMapList("signs." + key + ".player-commands");
                        final List<Map<?, ?>> cCommands = signConfig.getMapList("signs." + key + ".console-commands");
                        final List<String> permissions = signConfig.getStringList("signs." + key + ".permissions");
                        final List<Map<?, ?>> messages = signConfig.getMapList("signs." + key + ".messages");

                        if (Util.priceIsInteger()) {

                            try {

                                ss.setPriceAsInteger((Integer) signConfig.get("signs." + key + ".price"));

                            } catch (Exception error) {

                                UtilitiesOG.logToConsole(
                                        SudoSigns.getPlugin().getConfig().getString("config.console-prefix"),
                                        "ERROR: Invalid price for sign " + name + "! Defaulting to 0...");

                                ss.setPriceAsInteger(0);

                            }

                        } else {

                            try {

                                ss.setPriceAsDouble((Double) signConfig.get("signs." + key + ".price"));

                            } catch (Exception error) {

                                Bukkit.getLogger()
                                        .info(SudoSigns.getPlugin().getConfig().getString("config.console-prefix")
                                                + "ERROR: Invalid price for sign " + name + "! Defaulting to 0...");

                                ss.setPriceAsDouble(0.0);

                            }

                        }

                        int number = 0;
                        for (Map<?, ?> sc : pCommands) {

                            for (Map.Entry<?, ?> cmd : sc.entrySet()) {

                                ss.addPlayerCommand(new SignCommand(number++, (String) cmd.getKey(),
                                        (Double) cmd.getValue(), PlayerInput.PLAYER_COMMAND));

                            }

                        }

                        for (Map<?, ?> sc : cCommands) {

                            for (Map.Entry<?, ?> cmd : sc.entrySet()) {

                                ss.addConsoleCommand(new SignCommand(number++, (String) cmd.getKey(),
                                        (Double) cmd.getValue(), PlayerInput.CONSOLE_COMMAND));

                            }

                        }

                        permissions.forEach(ss::addPermission);

                        number = 0;
                        for (Map<?, ?> sc : messages) {

                            for (Map.Entry<?, ?> message : sc.entrySet()) {

                                ss.addMessage(new SignMessage(number++, (String) message.getKey(),
                                        (Double) message.getValue(), PlayerInput.MESSAGE));

                            }

                        }

                        tempSigns.put(name, ss);

                    } else {

                        invalidSigns.add(key);
                        final String actualBlockType = loc.getBlock().getType().toString();
                        Bukkit.getLogger()
                                .warning(SudoSigns.getPlugin().getConfig().getString("config.console-prefix")
                                        + "ERROR: Failed to load Sign " + key + " at " + world + " " + loc.getBlockX()
                                        + ", " + loc.getBlockY() + ", " + loc.getBlockZ()
                                        + "! Cause: the block at the provided location is " + actualBlockType
                                        + ", not a sign. Skipping...");

                    }

                } else {

                    invalidSigns.add(key);
                    Bukkit.getLogger().warning(SudoSigns.getPlugin().getConfig().getString("config.console-prefix")
                            + "ERROR: Failed to load Sign " + key + " at " + world + " " + x + ", " + y + ", " + z
                            + "! Cause: the world '" + world + "' does not exist or is not loaded. Skipping...");

                }

            } catch (Exception error) {

                error.printStackTrace();

                return null;

            }

        }

        configManager.getInvalidEntriesManager().setInvalidEntries(invalidSigns);

        SudoSigns.signs = tempSigns;
        SudoSigns.invalidSigns = invalidSigns;

        return invalidSigns;

    }

    /**
     * Deletes a sign from the config.
     * 
     * @param name The name of the sign.
     */
    public void deleteSign(String name) {

        if (signConfig.isSet("signs." + name)) {

            signConfig.set("signs." + name, null);

        }

        configManager.save();

    }

    /**
     * Saves the SudoSigns to config.
     * 
     * @param s        The SudoSign object.
     * @param singular True to save a singular sign, False to save all.
     * @param p        The player who has edited the sign.
     */
    public void saveToFile(SudoSign s, boolean singular, Player p) {

        if (singular) {

            saveSign(s, p);

        } else {

            SudoSigns.signs.entrySet().forEach(entry -> saveSign(entry.getValue(), p));

        }

        configManager.save();

    }

    /**
     * Saves a SudoSign in the config.
     * 
     * @param s The SudoSign object.
     * @param p The player who has edited the sign.
     */
    public void saveSign(SudoSign s, Player p) {

        final String name = s.getName();
        try {

            if (!signConfig.isConfigurationSection("signs." + name + "")) {

                signConfig.createSection("signs." + name + "");
                signConfig.createSection("signs." + name + ".text");

                final ArrayList<String> lines = new ArrayList<>(s.getText());

                signConfig.set("signs." + name + ".text", lines);
                signConfig.set("signs." + name + ".price", 0.0);

                final ConfigurationSection locSec = signConfig.createSection("signs." + name + ".location");

                final String world = s.getSign().getWorld().getName();
                final double x = s.getSign().getLocation().getX();
                final double y = s.getSign().getLocation().getY();
                final double z = s.getSign().getLocation().getZ();

                signConfig.set("signs." + name + ".blocktype", s.getSign().getType().toString());

                locSec.set("world", world);
                locSec.set("x", x);
                locSec.set("y", y);
                locSec.set("z", z);

                final BlockData blockData = s.getSign().getWorld().getBlockAt(s.getSign().getLocation()).getBlockData();
                if (blockData instanceof Rotatable) {

                    final Rotatable data = (Rotatable) blockData;
                    final BlockFace rotation = data.getRotation();

                    locSec.set("rotation", rotation);

                } else if (blockData instanceof WallSign) {

                    final WallSign facing = (WallSign) blockData;

                    locSec.set("rotation", facing.getFacing().toString());

                }

                signConfig.createSection("signs." + name + ".permissions");
                signConfig.createSection("signs." + name + ".messages");
                signConfig.createSection("signs." + name + ".player-commands");
                signConfig.createSection("signs." + name + ".console-commands");

                configManager.save();

            }

        } catch (Exception error) {

            if (p != null) {

                UtilitiesOG.trueogMessage(p, "&cERROR: Failed to save sign &e" + name + " &cto the config!");
                error.printStackTrace();

            }

        }

    }

    public void editText(String name, int lineNumber, TextComponent newText) {

        if (signConfig.isConfigurationSection("signs." + name + "")) {

            final List<String> text = signConfig.getStringList("signs." + name + ".text");

            text.set(lineNumber - 1, newText.content());

            signConfig.set("signs." + name + ".text", text);

        }

        configManager.save();

    }

    public void setPrice(String name, double price) {

        if (signConfig.isConfigurationSection("signs." + name + "")) {

            signConfig.set("signs." + name + ".price", price);

        }

        configManager.save();

    }

}
