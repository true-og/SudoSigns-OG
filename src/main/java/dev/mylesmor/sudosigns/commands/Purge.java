package dev.mylesmor.sudosigns.commands;

import org.bukkit.entity.Player;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import net.trueog.utilitiesog.UtilitiesOG;

public class Purge {

    /**
     * Purge an invalid sign's config entry (or all if name not provided).
     * 
     * @param p    The player running the command.
     * @param args 0 or 1 arguments: The name of the sign.
     */
    public static void purge(Player p, String[] args) {

        if (p.hasPermission(Permissions.PURGE)) {

            String name = null;
            if (args == null) {

                purgeAll(p);

                return;

            }

            if (args.length > 1) {

                UtilitiesOG.trueogMessage(p,
                        "&cERROR: Invalid syntax! " + "&6Correct syntax: " + "&d/ss purge [name]" + "&6.");
                return;

            }

            if (args.length == 1) {

                name = args[0];

            }

            if (name != null) {

                if (SudoSigns.config.getInvalidEntriesManager().purgeInvalidEntry(args[0], false)) {

                    UtilitiesOG.trueogMessage(p, "&aSign &e" + args[0] + "&a successfully purged from the config!");

                } else {

                    UtilitiesOG.trueogMessage(p, "&eWARNING: &6Nothing purged! That entry is not invalid.");

                }

            }

        } else {

            Util.sudoSignsPermissionsError(p);

        }

    }

    private static void purgeAll(Player p) {

        final int beforeSize = SudoSigns.config.getInvalidEntriesManager().getInvalidEntries().size();
        if (beforeSize == 0) {

            UtilitiesOG.trueogMessage(p, "&eWARNING: &6No invalid entries found to remove from the config!");

            return;

        }

        if (SudoSigns.config.getInvalidEntriesManager().purgeInvalidEntry(null, true)) {

            final int afterSize = SudoSigns.config.getInvalidEntriesManager().getInvalidEntries().size();
            if (afterSize == 0) {

                UtilitiesOG.trueogMessage(p, "&aAll invalid entries have been successfully purged from the config!");

            } else if (afterSize == beforeSize) {

                UtilitiesOG.trueogMessage(p, "&cERROR: No invalid entries were able to be removed automatically!");

            } else {

                UtilitiesOG.trueogMessage(p,
                        "&e" + (beforeSize - afterSize)
                                + "&a invalid config entries were able to be removed automatically. &e" + afterSize
                                + " &6were unable to be removed automatically.");

            }

        }

        return;

    }

    public static void confirmPurge(Player p, String[] args) {

        if (!p.hasPermission(Permissions.PURGE)) {

            return;

        }

        if (args.length != 1) {

            return;

        }

        Util.selectMenuParser(p, "&a&l[YES]", ("&6Are you sure you want to purge sign &e" + args[0] + "&6?"),
                p.getName(), ("/ss purge " + args[0]), ("&aYes, delete the sign: &6" + args[0] + "&a!"));

    }

}