package dev.mylesmor.sudosigns.commands;

import org.bukkit.entity.Player;

import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;

public class Select {

	/**
	 * Select a sign (display selection message in chat).
	 * @param p The player running the command.
	 * @param args 1 argument: The name of the sign.
	 */
	public static void select(Player p, String[] args) {

		if (p.hasPermission(Permissions.SELECT)) {

			if (args == null || args.length != 1) {

				Util.sudoSignsMessage(p, "&cERROR: Invalid syntax! " + "&6Correct syntax: &d/ss select <name> &6.");

			}
			else {

				Util.sendSelectMenus(p, args[0]);

			}

		}
		else {

			Util.sudoSignsErrorMessage(p);

		}

	}

}