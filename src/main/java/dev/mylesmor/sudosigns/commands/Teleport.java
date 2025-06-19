package dev.mylesmor.sudosigns.commands;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Teleport {

    /**
     * Teleports a player to the sign.
     * @param p The player running the command.
     * @param args 1 argument: The name of the sign.
     */
    public static void tp(Player p, String[] args) {

        if (p.hasPermission(Permissions.TP)) {

            if (args == null || args.length != 1) {

                Util.sudoSignsMessage(p, "&cERROR: Invalid syntax! &6Correct syntax: &d/ss tp <name>&6.");
                return;
            }

            String name = args[0];
            if (SudoSigns.signs.containsKey(name)) {

                Location newLoc = SudoSigns.signs.get(name).getSign().getLocation();
                newLoc.setX(newLoc.getX() + 0.5);
                newLoc.setZ(newLoc.getZ() + 0.5);

                p.teleport(newLoc);

            } else {

                Util.sudoSignsMessage(p, "&cERROR: A sign with the name &e%NAME% &cdoes not exist!", name);
            }

        } else {

            Util.sudoSignsErrorMessage(p);
        }
    }
}
