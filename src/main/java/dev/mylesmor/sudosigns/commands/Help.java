package dev.mylesmor.sudosigns.commands;

import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import org.bukkit.entity.Player;

public class Help {

    /**
     * Sends the player a help message.
     * 
     * @param p    The player running the command.
     * @param args Not required.
     */
    public static void help(Player p, String[] args) {

        if (p.hasPermission(Permissions.HELP)) {

            Util.sudoSignsMessageNoPrefix(p, "&b======" + Util.chatPrefix + " &eHelp" + "&b======");
            Util.sudoSignsMessageNoPrefix(p, "&e[!]" + "&d <>" + "&f - &6indicates a required argument.");
            Util.sudoSignsMessageNoPrefix(p, "&e[!]" + "&d []" + "&f - &6indicates an optional argument.");

            if (p.hasPermission(Permissions.HELP)) {

                Util.sudoSignsMessageNoPrefix(p, "&e[!]" + "&d /ss help" + " &f- &6Displays this menu.");

            }

            if (p.hasPermission(Permissions.LIST)) {

                Util.sudoSignsMessageNoPrefix(p, "&e[!]" + "&d /ss list" + " &f- &6Lists all SudoSigns.");

            }

            if (p.hasPermission(Permissions.NEAR)) {

                Util.sudoSignsMessageNoPrefix(p, "&e[!]" + "&d /ss near [radius]"
                        + " &f- &6Lists SudoSigns within a specified radius (default 5 blocks).");

            }

            if (p.hasPermission(Permissions.VIEW)) {

                Util.sudoSignsMessageNoPrefix(p,
                        "&e[!]" + "&d /ss view [name]" + " &f- &6Displays the details of a SudoSign.");

            }

            if (p.hasPermission(Permissions.RUN)) {

                Util.sudoSignsMessageNoPrefix(p,
                        "&e[!]" + "&d /ss run [name]" + " &f- &6Runs a SudoSigns commands remotely.");

            }

            if (p.hasPermission(Permissions.TP)) {

                Util.sudoSignsMessageNoPrefix(p, "&e[!]" + "&d /ss tp [name]" + " &f- &6Teleport to a SudoSign.");

            }

            if (p.hasPermission(Permissions.CREATE)) {

                Util.sudoSignsMessageNoPrefix(p,
                        "&e[!]" + "&d /ss create <name>" + " &f- &6Creates a SudoSign with the specified name.");

            }

            if (p.hasPermission(Permissions.EDIT)) {

                Util.sudoSignsMessageNoPrefix(p,
                        "&e[!]" + "&d /ss edit [name]" + " &f- &6Displays the editor for the specified SudoSign.");

            }

            if (p.hasPermission(Permissions.COPY)) {

                Util.sudoSignsMessageNoPrefix(p, "&e[!]" + "&d /ss copy [old-sign-name] <new-sign-name>"
                        + " &f- &6Copies the specified SudoSign to a new sign.");

            }

            if (p.hasPermission(Permissions.DELETE)) {

                Util.sudoSignsMessageNoPrefix(p,
                        "&e[!]" + "&d /ss delete [name]" + " &f- &6Deletes the specified SudoSign.");

            }

            if (p.hasPermission(Permissions.RELOAD)) {

                Util.sudoSignsMessageNoPrefix(p,
                        "&e[!]" + "&d /ss reload" + " &f- &6Reloads the configuration file (signs.yml).");

            }

            if (p.hasPermission(Permissions.PURGE)) {

                Util.sudoSignsMessageNoPrefix(p, "&e[!]" + "&d /ss purge [name]"
                        + " &f- &6Removes a specified invalid entry from the config (leave name blank to purge all invalid entries).");

            }

        } else {

            Util.sudoSignsErrorMessage(p);

        }

    }

}
