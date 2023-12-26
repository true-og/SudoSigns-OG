package dev.mylesmor.sudosigns.menus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.PlayerInput;
import dev.mylesmor.sudosigns.data.SignCommand;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.data.SudoUser;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import net.kyori.adventure.text.TextComponent;

public class CommandsMenu {

	private Inventory menu;
	private Player p;
	private SudoSign sign;
	private SignEditor editor;
	private SudoUser su;

	public CommandsMenu(SudoUser su, Player p, SudoSign sign, SignEditor editor) {

		this.su = su;
		this.sign = sign;
		this.editor = editor;
		this.p = p;

	}

	public void goToCommandsMenu() {

		createCommandsMenu();

		p.openInventory(menu);

	}

	private void createCommandsMenu() {

		Inventory playersInventory = p.getInventory();
		InventoryHolder inventoryContainer = playersInventory.getHolder(false);
		TextComponent nameHandler = Util.legacySerializerAnyCase("Commands: " + sign.getName());

		menu = Bukkit.createInventory(inventoryContainer, 45, nameHandler);
		for (int i = 0; i < menu.getSize(); i++) {

			menu.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

		}

		ItemStack arrow = new ItemStack(Material.ARROW);
		ItemMeta arrowMeta = arrow.getItemMeta();

		arrowMeta.displayName(Util.legacySerializerAnyCase("&r&dBACK"));
		arrow.setItemMeta(arrowMeta);

		if (p.hasPermission(Permissions.ADD_COMMAND)) {

			ItemStack bookQuill = new ItemStack(Material.WRITABLE_BOOK);
			ItemMeta bqMeta = bookQuill.getItemMeta();

			bqMeta.displayName(Util.legacySerializerAnyCase("&r&2Add new command"));
			bookQuill.setItemMeta(bqMeta);

			menu.setItem(40, bookQuill);

		}

		ItemStack cmdBlock = new ItemStack(Material.COMMAND_BLOCK);
		ItemMeta cmdBlockMeta = cmdBlock.getItemMeta();

		cmdBlockMeta.displayName(Util.legacySerializerAnyCase("&r&5Console Commands"));
		cmdBlock.setItemMeta(cmdBlockMeta);

		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		ItemMeta headMeta = head.getItemMeta();

		headMeta.displayName(Util.legacySerializerAnyCase("&r&d Player Commands"));
		head.setItemMeta(headMeta);

		List<String> lore = new ArrayList<>();
		NamespacedKey key = new NamespacedKey(SudoSigns.sudoSignsPlugin, "command-number");

		ArrayList<SignCommand> orderedSignCommands = sign.getPlayerCommands();
		orderedSignCommands.sort(Comparator.comparing(SignCommand::getDelay));

		// Populates spaces with commands.
		int i = 1;
		for (SignCommand sc : orderedSignCommands) {

			if (i > 26) break;

			ItemStack book = new ItemStack(Material.BOOK);
			ItemMeta bookMeta = book.getItemMeta();

			lore.add("&6Player Command");
			bookMeta.displayName(Util.legacySerializerAnyCase("&r&6/" + sc.getCommand()));
			lore.add("&6Delay: &e" + (sc.getDelay() / 1000) + "s");

			if (p.hasPermission(Permissions.COMMAND_OPTIONS)) {

				lore.add("");
				lore.add("&2Click for options!");

			}

			bookMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, sc.getNumber());
			bookMeta.lore(Util.convertToTextComponents(lore));

			lore.clear();
			book.setItemMeta(bookMeta);

			if (i == 9) i++;

			menu.setItem(i, book);

			i++;

		}

		orderedSignCommands = sign.getConsoleCommands();
		orderedSignCommands.sort(Comparator.comparing(SignCommand::getDelay));

		i = 19;
		for (SignCommand sc : orderedSignCommands) {

			if (i > 35) break;

			ItemStack book = new ItemStack(Material.BOOK);
			ItemMeta bookMeta = book.getItemMeta();

			lore.add("&6Console Command");
			bookMeta.displayName(Util.legacySerializerAnyCase("&r&6/" + sc.getCommand()));
			lore.add("&6Delay: &e" + (sc.getDelay() / 1000) + "s");

			if (p.hasPermission(Permissions.COMMAND_OPTIONS)) {

				lore.add("");
				lore.add("&2Click for options!");

			}

			bookMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, sc.getNumber());
			bookMeta.lore(Util.convertToTextComponents(lore));

			lore.clear();
			book.setItemMeta(bookMeta);

			if (i == 27) i++;

			menu.setItem(i, book);

			i++;

		}

		menu.setItem(0, head);
		menu.setItem(9, head);
		menu.setItem(18, cmdBlock);
		menu.setItem(27, cmdBlock);
		menu.setItem(36, arrow);

	}

	public void prepareCommand() {

		Inventory playersInventory = p.getInventory();
		InventoryHolder inventoryContainer = playersInventory.getHolder(false);
		TextComponent nameHandler = Util.legacySerializerAnyCase("Player or Console command?");

		Inventory choiceInv = Bukkit.createInventory(inventoryContainer, 45, nameHandler);
		for (int i = 0; i < choiceInv.getSize(); i++) {

			choiceInv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

		}

		ItemStack arrow = new ItemStack(Material.ARROW);
		ItemMeta arrowMeta = arrow.getItemMeta();

		arrowMeta.displayName(Util.legacySerializerAnyCase("&r&dBACK"));
		arrow.setItemMeta(arrowMeta);

		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		ItemMeta headMeta = head.getItemMeta();
		List<String> lore = new ArrayList<>();

		lore.add("&cThe player must have permission to run the command!");
		headMeta.displayName(Util.legacySerializerAnyCase("&r&dPlayer Command"));
		headMeta.lore(Util.convertToTextComponents(lore));
		head.setItemMeta(headMeta);

		ItemStack cmdBlock = new ItemStack(Material.COMMAND_BLOCK);
		ItemMeta cmdBlockMeta = cmdBlock.getItemMeta();
		cmdBlockMeta.displayName(Util.legacySerializerAnyCase("&r&dConsole Command"));
		cmdBlock.setItemMeta(cmdBlockMeta);

		if (p.hasPermission(Permissions.CONSOLE_COMMAND)) {

			choiceInv.setItem(21, head);
			choiceInv.setItem(23, cmdBlock);

		}
		else {

			choiceInv.setItem(22, head);

		}

		choiceInv.setItem(36, arrow);

		editor.setCurrentPage(GUIPage.CHOOSE_COMMAND);
		p.openInventory(choiceInv);

	}


	public void chooseCommandType(PlayerInput type) {

		p.closeInventory();

		Util.sudoSignsMessage(p, "&6Please enter the full command in chat with the beginning &e/&6. The placeholder &e%PLAYER% &6will be replaced with the player who clicked the sign. To cancel, type &cCANCEL&6.");
		su.addTextInput(type);

	}

	public void addCommand(String cmd, PlayerInput type) {

		SignCommand command = new SignCommand(sign.getNextCommandNumber(), cmd, 0, type);
		switch (type) {
		case CONSOLE_COMMAND:
			sign.addConsoleCommand(command);
			break;
		case PLAYER_COMMAND:
			sign.addPlayerCommand(command);
			break;
		default:
			break;
		}

		SudoSigns.config.addCommand(sign, command, type);

		Util.sudoSignsMessage(p, "&aCommand added successfully!");
		editor.goToCommands();

	}

}