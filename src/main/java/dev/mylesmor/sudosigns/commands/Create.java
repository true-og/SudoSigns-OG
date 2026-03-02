package dev.mylesmor.sudosigns.commands;

import org.bukkit.entity.Player;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import net.trueog.diamondbankog.api.DiamondBankAPIJava;
import net.trueog.utilitiesog.UtilitiesOG;

public class Create {

    /**
     * Creates a sign.
     * 
     * @param p    The player running the command.
     * @param args 0 or 1 arguments: The name of the sign (or null for click
     *             selection).
     */
    public static void create(Player p, String[] args, DiamondBankAPIJava diamondBankAPI) {

        if (p.hasPermission(Permissions.CREATE)) {

            if (args == null) {

                UtilitiesOG.trueogMessage(p, "&cERROR: Invalid syntax! &6Correct syntax: &d/ss create <name>&6.");

                return;

            } else {

                final String name = args[0];
                if (Util.checkName(name)) {

                    if (!SudoSigns.signs.containsKey(name)) {

                        SudoSigns.users.get(p.getUniqueId()).setCreate(true);
                        SudoSigns.users.get(p.getUniqueId()).setPassThru(name);
                        SudoSigns.signs.put(name, new SudoSign(name, diamondBankAPI));

                        UtilitiesOG.trueogMessage(p, "&6Please click on the sign you'd like to create.");

                    } else {

                        UtilitiesOG.trueogMessage(p, "&cERROR: A sign with name &e" + name + " &calready exists!");

                    }

                } else {

                    UtilitiesOG.trueogMessage(p,
                            "&cERROR: The name of a SudoSign must only contain numbers and letters!");

                }

            }

        } else {

            Util.sudoSignsPermissionsError(p);

        }

    }

}