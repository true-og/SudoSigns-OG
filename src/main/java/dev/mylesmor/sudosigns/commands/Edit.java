package dev.mylesmor.sudosigns.commands;

import org.bukkit.entity.Player;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.menus.SignEditor;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import net.trueog.diamondbankog.api.DiamondBankAPIJava;
import net.trueog.utilitiesog.UtilitiesOG;

public class Edit {

    /**
     * Edits a sign.
     * 
     * @param p    The player running the command.
     * @param args 0 or 1 arguments: The name of the sign (or null for click
     *             selection).
     */
    public static void edit(Player p, String[] args, DiamondBankAPIJava diamondBankAPI) {

        if (p.hasPermission(Permissions.EDIT)) {

            final String name;
            if (args == null) {

                UtilitiesOG.trueogMessage(p, "&6Please click on the sign you'd like to edit.");

                SudoSigns.users.get(p.getUniqueId()).setEdit(true);
                return;

            }

            if (args.length > 1) {

                UtilitiesOG.trueogMessage(p, "&cERROR: Invalid syntax! &aCorrect syntax: &d/ss edit [name]&a.");
                return;

            }

            name = args[0];
            if (SudoSigns.signs.containsKey(name)) {

                final SignEditor editor = new SignEditor(p, SudoSigns.signs.get(name),
                        SudoSigns.users.get(p.getUniqueId()), diamondBankAPI);
                SudoSigns.users.get(p.getUniqueId()).setEditor(editor);

            } else {

                UtilitiesOG.trueogMessage(p, "&cERROR: A sign with the name &e" + name + " &cdoes not exist!");

            }

        } else {

            Util.sudoSignsPermissionsError(p);

        }

    }

}