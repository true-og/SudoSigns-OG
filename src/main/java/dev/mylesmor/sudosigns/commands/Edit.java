package dev.mylesmor.sudosigns.commands;

import org.bukkit.entity.Player;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.menus.SignEditor;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;

public class Edit {

	/**
	 * Edits a sign.
	 * @param p The player running the command.
	 * @param args 0 or 1 arguments: The name of the sign (or null for click selection).
	 */
	public static void edit(Player p, String[] args) {

		if (p.hasPermission(Permissions.EDIT)) {

			String name;
			if (args == null) {

				Util.sudoSignsMessage(p, "&6Please click on the sign you'd like to edit.");

				SudoSigns.users.get(p.getUniqueId()).setEdit(true);
				return;

			}
			if (args.length > 1) {

				Util.sudoSignsMessage(p, "&cERROR: Invalid syntax! &aCorrect syntax: &d/ss edit [name]&a.");
				return;

			}

			name = args[0];
			if (SudoSigns.signs.containsKey(name)) {

				SignEditor editor = new SignEditor(p, SudoSigns.signs.get(name), SudoSigns.users.get(p.getUniqueId()));
				SudoSigns.users.get(p.getUniqueId()).setEditor(editor);

			}
			else {

				Util.sudoSignsMessage(p, "&cERROR: A sign with the name %NAME% &cdoes not exist!", name);

			}

		}
		else {

			Util.sudoSignsErrorMessage(p);

		}

	}

}