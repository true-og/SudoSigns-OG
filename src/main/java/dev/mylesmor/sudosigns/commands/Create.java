package dev.mylesmor.sudosigns.commands;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import org.bukkit.entity.Player;

public class Create {

    /**
     * Creates a sign.
     * 
     * @param p    The player running the command.
     * @param args 0 or 1 arguments: The name of the sign (or null for click
     *             selection).
     */
    public static void create(Player p, String[] args) {

        if (p.hasPermission(Permissions.CREATE)) {

            if (args == null) {

                Util.sudoSignsMessage(p, "&cERROR: Invalid syntax! &6Correct syntax: &d/ss create <name>&6.");
                return;

            } else {

                String name = args[0];
                if (Util.checkName(name)) {

                    if (!SudoSigns.signs.containsKey(name)) {

                        SudoSigns.users.get(p.getUniqueId()).setCreate(true);
                        SudoSigns.users.get(p.getUniqueId()).setPassThru(name);
                        SudoSigns.signs.put(name, new SudoSign(name));

                        Util.sudoSignsMessage(p, "&6Please click on the sign you'd like to create.");

                    } else {

                        Util.sudoSignsMessage(p, "&cERROR: A sign with name &e%NAME% &calready exists!", name);

                    }

                } else {

                    Util.sudoSignsMessage(p, "&cERROR: The name of a SudoSign must only contain numbers and letters!");

                }

            }

        } else {

            Util.sudoSignsErrorMessage(p);

        }

    }

}
