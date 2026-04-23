package dev.mylesmor.sudosigns.commands;

import org.bukkit.entity.Player;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import net.trueog.utilitiesog.UtilitiesOG;

public class Delete {

    /**
     * Deletes a sign.
     * 
     * @param p    The player running the command.
     * @param args 1 or 2 arguments: The name of the sign (or null for click
     *             selection).
     */
    public static void delete(Player p, String[] args) {

        if (p.hasPermission(Permissions.DELETE)) {

            final String name;
            if (args != null) {

                if (args.length > 1) {

                    UtilitiesOG.trueogMessage(p, "&cERROR: Invalid syntax! &7Correct syntax: &d/ss delete [name]&7.");

                    return;

                }

            } else {

                UtilitiesOG.trueogMessage(p, "&6Please click the sign you'd like to delete.");

                SudoSigns.users.get(p.getUniqueId()).setDelete(true);

                return;

            }

            name = args[0];
            if (SudoSigns.signs.containsKey(name)) {

                SudoSigns.signs.remove(name);
                SudoSigns.config.deleteSign(name);

                UtilitiesOG.trueogMessage(p, "&6Sign &e" + name + " &6successfully deleted.");

            } else if (SudoSigns.config.getInvalidEntriesManager().purgeInvalidEntry(name, false)) {

                UtilitiesOG.trueogMessage(p, "&6Sign &e" + name + " &6successfully deleted.");

            } else {

                UtilitiesOG.trueogMessage(p, "&cERROR: A sign with the name &e" + name + " &cdoes not exist!");

            }

        } else {

            Util.sudoSignsPermissionsError(p);

        }

    }

    /**
     * Confirms deletion of a sign (only run when [DELETE] is clicked in the
     * selection message).
     * 
     * @param p    The player running the command.
     * @param args 1 argument: The name of the sign.
     */
    public static void confirmDelete(Player p, String[] args) {

        // If the player has permission to delete signs, do this...
        if (!p.hasPermission(Permissions.DELETE)) {

            return;

        }

        // If the sign that was selected does not exist, do this...
        if (args.length != 1) {

            return;

        }

        if (SudoSigns.signs.containsKey(args[0])) {

            // Send a deletion confirmation menu with a clickable button.
            Util.selectMenuParser(p, "&c&l[CONFIRM DELETION]",
                    ("&6Are you sure you want to delete sign &e" + args[0] + "&6?"), p.getName(),
                    ("/ss delete " + args[0]), ("&cYes, delete the sign: &6" + args[0] + "&c!"));

        } else {

            UtilitiesOG.trueogMessage(p, "&cERROR: A sign with the name &e" + args[0] + " &cdoes not exist!");

        }

    }

}