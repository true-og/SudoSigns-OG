package dev.mylesmor.sudosigns.commands;

import org.bukkit.entity.Player;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import net.trueog.diamondbankog.api.DiamondBankAPIJava;
import net.trueog.utilitiesog.UtilitiesOG;

public class Copy {

    /**
     * Copies one sign's data to another.
     * 
     * @param p    The player running the command.
     * @param args 1 or 2 arguments: If 1, the name of the new sign. If 2, the name
     *             of the old sign in index 0 and name of new one in index 1.
     */
    public static void copy(Player p, String[] args, DiamondBankAPIJava diamondBankAPI) {

        if (p.hasPermission(Permissions.COPY)) {

            String oldName = null;
            String newName = null;
            if (args == null || args.length < 1 || args.length > 2) {

                UtilitiesOG.trueogMessage(p,
                        "&cERROR: Invalid syntax! &6Correct syntax: &d/ss copy [old-sign-name] <new-sign-name>&6.");
                return;

            }

            if (args.length == 1) {

                newName = args[0];

            } else {

                newName = args[1];
                oldName = args[0];

            }

            if (Util.checkName(newName)) {

                SudoSigns.users.get(p.getUniqueId()).setPassThru(newName);
                if (oldName == null) {

                    UtilitiesOG.trueogMessage(p, "&6Please click the sign you'd like to copy from.");
                    SudoSigns.users.get(p.getUniqueId()).setSelectToCopy(true);

                } else {

                    final SudoSign oldSign = SudoSigns.signs.get(oldName);
                    if (oldSign != null) {

                        if (!SudoSigns.signs.containsKey(newName)) {

                            UtilitiesOG.trueogMessage(p, "&6Please click the sign you'd like to copy to.");
                            final SudoSign newSign = new SudoSign(newName, diamondBankAPI);

                            newSign.copyFrom(oldSign);

                            SudoSigns.users.get(p.getUniqueId()).setCopy(true);
                            SudoSigns.signs.put(newName, newSign);

                        } else {

                            UtilitiesOG.trueogMessage(p, "&cERROR: A sign with the name &e%NAME% &calready exists!");

                        }

                    } else {

                        UtilitiesOG.trueogMessage(p, "&cERROR: A sign with the name &e%NAME% &cdoes not exist!");

                    }

                }

            } else {

                UtilitiesOG.trueogMessage(p, "&cERROR: The name of a SudoSign must only contain numbers and letters!");

            }

        } else {

            Util.sudoSignsPermissionsError(p);

        }

    }

}