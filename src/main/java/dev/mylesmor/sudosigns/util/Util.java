package dev.mylesmor.sudosigns.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.SudoSign;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Util {

	// Create a colored chat prefix.
	public static String chatPrefix = "&8[&2SudoSigns&4-OG&8] ";

	// Send the sign selection menu according to permissions.
	public static void sendSelectMenus(Player p, String name) {

		selectMenuParser(p, Util.chatPrefix, "&e-----", name, "", "&2True&4OG&a's Fork of &BSudoSigns&a.");

		if (p.hasPermission(Permissions.VIEW)) {

			selectMenuParser(p, "&9&l[V]", "&9View Details", name, ("/ss view " + name), ("&9View &6" + name + "\'s &9details."));

		}

		if (p.hasPermission(Permissions.RUN)) {

			selectMenuParser(p, "&2&l[R]", "&2Run Commands", name, ("/ss run " + name), ("&2Run &6" + name + "\'s &2commands."));

		}

		if (p.hasPermission(Permissions.TP)) {

			selectMenuParser(p, "&3&l[TP]", "&3Teleport To", name, ("/ss tp " + name), ("&3Teleport to &6" + name + "&3."));

		}

		if (p.hasPermission(Permissions.EDIT)) {

			selectMenuParser(p, "&d&l[EDIT]", "&dEdit Details", name, ("/ss edit " + name), ("&dEdit &6" + name + "\'s &dcolor."));

		}

		if (p.hasPermission(Permissions.DELETE)) {

			selectMenuParser(p, "&c&l[DEL]", "&cDelete SudoSign", name, ("/ss confirmdelete " + name), ("&cDelete &6" + name + "&c."));

		}

	}

	// Sends customizable clickable message dialogues to the player.
	public static void selectMenuParser(Player p, String prefix, String prompt, String name, String command, String hoverMessage) {

		// The clickable part of the confirmation message.
		TextComponent button = LegacyComponentSerializer.legacyAmpersand().deserialize(prefix);

		// Add a hover dialogue to the clickable confirmation text.
		button.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, LegacyComponentSerializer.legacyAmpersand().deserialize(hoverMessage)));

		// Make sure that a command is intended to be run.
		if(! command.equals("")) {
			// Create a click event on the delete confirmation chat text.
			button = button.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, (command)));
		}

		// Send the prompt and clickable confirmation button to the player.
		Util.sudoSignsPrompt(p, (prompt + "     "), button);

	}

	// Sends a formatted message to the player.
	public static void sudoSignsMessage(Player p, String message) {

		p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(chatPrefix + message));

	}

	// Sends a formatted message to the player (including name replacement).
	public static void sudoSignsMessage(Player p, String message, String name) {

		if (message.contains("%NAME%")) {

			message = message.replace("%NAME%", "&6" + name);

		}

		sudoSignsMessage(p, message);

	}

	// Sends a formatted message without a prefix to the player.
	public static void sudoSignsMessageNoPrefix(Player p, String message) {

		p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));

	}

	// Sends a standard error message to the player.
	public static void sudoSignsErrorMessage(Player p) {

		sudoSignsMessage(p, "&c&lERROR: &cYou do not have permission to do that!");

	}

	// Sends a formatted prompt and clickable chat button to the player.
	public static void sudoSignsPrompt(Player player, String prompt, TextComponent button) {

		player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prompt).append(button));

	}

	public static boolean checkName(String name) {

		String regex = "^[A-z0-9]+$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(name);

		return matcher.matches();

	}

	public static String findSign(Sign s) {

		String found = null;
		for (Map.Entry<String, SudoSign> entry : SudoSigns.signs.entrySet()) {

			Sign sign = entry.getValue().getSign();
			if (sign != null && sign.equals(s)) {

				found = entry.getKey();

			}

		}

		return found;

	}

	public static ArrayList<TextComponent> convertToTextComponents(List<String> stringList) {
		ArrayList<TextComponent> textComponents = new ArrayList<TextComponent>(18);

		for (String str : stringList) {

			TextComponent textComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(str);
			textComponents.add(textComponent);

		}

		return textComponents;
	}

}