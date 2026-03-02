package dev.mylesmor.sudosigns.commands;

import org.bukkit.entity.Player;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import net.trueog.utilitiesog.UtilitiesOG;

public class Fix {

    /**
     * Attempt to fix an invalid entry by placing a sign in the config location.
     * 
     * @param p    The player running the command.
     * @param args 0 or 1 arguments: The name of the sign.
     */
    public static void fix(Player p, String[] args) {

        if (p.hasPermission(Permissions.FIX)) {

            final String name;
            if (args == null) {

                final int size = SudoSigns.config.getInvalidEntriesManager().getInvalidEntries().size();
                if (SudoSigns.config.getInvalidEntriesManager().fixInvalidEntry(null, true)) {

                    SudoSigns.config.loadSigns();
                    if (SudoSigns.config.getInvalidEntriesManager().getInvalidEntries().size() == 0) {

                        UtilitiesOG.trueogMessage(p,
                                "&aAll invalid entries have been successfully purged from the config.");

                    } else if (SudoSigns.config.getInvalidEntriesManager().getInvalidEntries().size() == size) {

                        UtilitiesOG.trueogMessage(p,
                                "&cERROR: No invalid entries were able to be automatically purged from the config!");

                    } else if (SudoSigns.config.getInvalidEntriesManager().getInvalidEntries().size() < size) {

                        UtilitiesOG.trueogMessage(p,
                                "&e" + (SudoSigns.config.getInvalidEntriesManager().getInvalidEntries().size() - size)
                                        + "&a invalid entries were able to be automatically purged from the config. &e"
                                        + Integer.toString(
                                                SudoSigns.config.getInvalidEntriesManager().getInvalidEntries().size())
                                        + "&c were unable to be automatically purged from the config!");

                    }

                }

                return;

            }

            if (args.length > 2) {

                UtilitiesOG.trueogMessage(p, "&cERROR: Invalid syntax! &6Correct syntax: &d/ss fix [name]&6.");

                return;

            }

            name = args[0];
            if (SudoSigns.config.getInvalidEntriesManager().fixInvalidEntry(name, false)) {

                SudoSigns.config.loadSigns();
                if (SudoSigns.signs.containsKey(name)) {

                    UtilitiesOG.trueogMessage(p, "&aSign &e" + name + "&asuccessfully fixed!");

                } else {

                    UtilitiesOG.trueogMessage(p, "&cERROR: Sign &e" + name + "&c was unable to be fixed!");

                }

            } else {

                UtilitiesOG.trueogMessage(p, "&6WARNING: Nothing fixed! That entry is not invalid.");

            }

        } else {

            Util.sudoSignsPermissionsError(p);

        }

    }

}