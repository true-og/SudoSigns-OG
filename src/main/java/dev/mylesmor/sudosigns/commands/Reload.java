package dev.mylesmor.sudosigns.commands;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Reload {

    /**
     * Reloads the config file (signs.yml)
     * 
     * @param p    The player running the command.
     * @param args Not required
     */
    public static void reload(Player p, String[] args) {

        if (p.hasPermission(Permissions.RELOAD)) {

            Util.sudoSignsMessage(p, "&6Reloading config...");
            SudoSigns.signs.clear();

            if (SudoSigns.config.loadCustomConfig()) {

                ArrayList<String> invalidSigns = SudoSigns.config.loadSigns();
                if (invalidSigns != null) {

                    if (invalidSigns.size() == 0) {

                        Util.sudoSignsMessage(p, "&aConfig successfully reloaded! No invalid signs found.");

                    } else {

                        Util.sudoSignsMessage(p, "&aConfig successfully reloaded! &cERROR: Found &e"
                                + invalidSigns.size() + " &cinvalid signs!");
                        for (String name : invalidSigns) {

                            // If the player has permission to purge a sign, do this...
                            if (p.hasPermission(Permissions.PURGE)) {

                                // Send a fix confirmation menu with a clickable button.
                                Util.selectMenuParser(p, "&a&l[FIX]", ("&cSign: &e&l" + name), name,
                                        ("/ss fix " + name),
                                        ("&6Fix &e" + name + "&6by placing a replica of the sign at the location."));

                            }

                            // If the player has permission to fix a sign, do this...
                            if (p.hasPermission(Permissions.FIX)) {

                                // Send a purge confirmation menu with a clickable button.
                                Util.selectMenuParser(p, "&c&l[PURGE]", ("&cSign: &e&l" + name), name,
                                        ("/ss confirmpurge " + name),
                                        (("&cRemove &e" + name + "&6from the config file.")));

                            }

                        }

                    }

                    return;

                }

                Bukkit.getLogger().warning(SudoSigns.getPlugin().getConfig().getString("config.console-prefix")
                        + "There was an error with the SudoSigns config!");
                Util.sudoSignsMessage(p,
                        "&cERROR: The latest update to the SudoSigns config was invalid! &6Continuing to use old config...");

            } else {

                Bukkit.getLogger().warning(SudoSigns.getPlugin().getConfig().getString("config.console-prefix")
                        + "There was an error with the SudoSigns config!");
                Util.sudoSignsMessage(p,
                        "&cERROR: The latest update to the SudoSigns config was invalid! &6Please attempt to fix it before the next server restart or plugin reload attempt.");

            }

        } else {

            Util.sudoSignsErrorMessage(p);

        }

    }

}
