package dev.mylesmor.sudosigns.commands;

import org.bukkit.entity.Player;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import net.trueog.utilitiesog.UtilitiesOG;

public class List {

    /**
     * Lists all signs.
     * 
     * @param p    The player running the command.
     * @param args Not required
     */
    public static void list(Player p, String[] args) {

        if (p.hasPermission(Permissions.LIST)) {

            if (SudoSigns.signs.isEmpty()) {

                UtilitiesOG.trueogMessage(p, "&cERROR: No SudoSigns were found!");

                return;

            }

            UtilitiesOG.trueogMessage(p, "&6Listing all SudoSigns...");

            SudoSigns.signs.entrySet().forEach(entry -> Util.sendSelectMenus(p, entry.getKey()));

        } else {

            Util.sudoSignsPermissionsError(p);

        }

    }

}