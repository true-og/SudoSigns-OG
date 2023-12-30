package dev.mylesmor.sudosigns.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.util.Consumer;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.PlayerInput;
import dev.mylesmor.sudosigns.data.SudoUser;
import dev.mylesmor.sudosigns.menus.SignEditor;
import dev.mylesmor.sudosigns.util.Util;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;


/**
 * The ChatListener class for taking user input in chat.
 * @author MylesMor
 * @author https://mylesmor.dev
 */
public class ChatListener implements Listener {

	@EventHandler
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {

		Player p = e.getPlayer();
		SudoUser user = SudoSigns.users.get(p.getUniqueId());
		if (user != null) {

			if (user.isTextInput() && user.isEditing()) {

				if (user.getInputType() == PlayerInput.CONSOLE_COMMAND || user.getInputType() == PlayerInput.PLAYER_COMMAND) {

					user.getEditor().getCommandsMenu().addCommand(e.getMessage().substring(1), user.getInputType());
					user.removeTextInput();

				}
				else {

					Util.sudoSignsMessage(p, "&eWARNING: &6Operation Cancelled!");
					user.removeTextInput();

				}

				e.setCancelled(true);

			}

		}

	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncChatEvent e) {

		Player p = e.getPlayer();
		SudoUser user = SudoSigns.users.get(p.getUniqueId());
		if (user != null) {

			if (user.isTextInput() && user.isEditing()) {

				SignEditor editor = user.getEditor();

				switch (user.getInputType()) {
				case PLAYER_COMMAND: case CONSOLE_COMMAND:
					if (PlainTextComponentSerializer.plainText().serialize(e.message()).equalsIgnoreCase("cancel")) {

						handle(e, true, "Cancelled!", editor, user, null, editor::goToCommands);

						return;

					}

					user.removeTextInput();

					handle(e, true, "No command found! Cancelling...", editor, user, null, editor::goToCommands);

					break;
				case RENAME:
					if (PlainTextComponentSerializer.plainText().serialize(e.message()).equalsIgnoreCase("cancel")) {

						handle(e, true, "Cancelled!", editor, user, null, editor::goToMain);

						return;

					}

					handle(e, true, null, editor, user, edit -> editor.renameSign(PlainTextComponentSerializer.plainText().serialize(e.message())), editor::goToMain);

					break;
				case PERMISSION:
					if (PlainTextComponentSerializer.plainText().serialize(e.message()).equalsIgnoreCase("cancel")) {

						handle(e, true, "Cancelled!", editor, user, null, editor::goToPermissions);

						return;

					}

					handle(e, true, null, editor, user, edit -> editor.getPermMenu().addPermission(true, PlainTextComponentSerializer.plainText().serialize(e.message())), editor::goToPermissions);

					break;
				case MESSAGE:
					if (PlainTextComponentSerializer.plainText().serialize(e.message()).equalsIgnoreCase("cancel")) {

						handle(e, true, "Cancelled!", editor, user, null, editor::goToMessages);

						return;

					}

					handle(e, true, null, editor, user, edit -> editor.getMessagesMenu().addMessage(PlainTextComponentSerializer.plainText().serialize(e.message())), editor::goToMessages);

					break;
				case COMMAND_DELAY:
					if (PlainTextComponentSerializer.plainText().serialize(e.message()).equalsIgnoreCase("cancel")) {

						handle(e, true, "Cancelled!", editor, user, null, editor::goToCommands);

						return;

					}

					double delay = 0;
					try {

						delay = Double.parseDouble(PlainTextComponentSerializer.plainText().serialize(e.message()));

					}
					catch (NumberFormatException nfe) {

						e.setCancelled(true);
						Util.sudoSignsMessage(p, "&cERROR: Please enter a valid number.");

						return;

					}

					double finalDelay = delay;

					handle(e, true, null, editor, user, edit -> editor.getCommandOptionsMenu().setDelay(finalDelay), editor::goToCommands);

					break;
				case MESSAGE_DELAY:
					if (PlainTextComponentSerializer.plainText().serialize(e.message()).equalsIgnoreCase("cancel")) {

						handle(e, true, "Cancelled!", editor, user, null, editor::goToMessages);

						return;

					}

					double messageDelay = 0;
					try {

						messageDelay = Double.parseDouble(PlainTextComponentSerializer.plainText().serialize(e.message()));

					}
					catch (NumberFormatException nfe) {

						e.setCancelled(true);
						Util.sudoSignsMessage(p, "&cERROR: Please enter a valid number!");

						return;

					}

					double finalMessageDelay = messageDelay;

					handle(e, true, null, editor, user, edit -> editor.getMessageOptionsMenu().setDelay(finalMessageDelay), editor::goToMessages);

					break;
				case EDIT_TEXT_NUMBER:
					if (PlainTextComponentSerializer.plainText().serialize(e.message()).equalsIgnoreCase("cancel")) {

						handle(e, true, "Cancelled!", editor, user, null, editor::goToMessages);

						return;

					}

					int lineNumber = 0;
					try {

						lineNumber = Integer.parseInt(PlainTextComponentSerializer.plainText().serialize(e.message()));

					}
					catch (NumberFormatException nfe) {

						e.setCancelled(true);
						Util.sudoSignsMessage(p, "&cERROR: Please enter a valid number between 1-4!");

						return;

					}
					if (lineNumber < 1 || lineNumber > 4) {

						e.setCancelled(true);
						Util.sudoSignsMessage(p, "&cERROR: Please enter a valid number between 1-4!");

						return;

					}

					final int finalLineNumber = lineNumber;

					handle(e, true, null, editor, user, edit -> editor.getMainMenu().setLineNumber(finalLineNumber), null);

					break;
				case EDIT_TEXT:
					if (PlainTextComponentSerializer.plainText().serialize(e.message()).equalsIgnoreCase("cancel")) {

						handle(e, true, "Cancelled!", editor, user, null, editor::goToMessages);

						return;

					}

					// Regular expression contributed by lazerl0rd (MIT) at https://github.com/MylesMor/SudoSigns/pull/4
					if (PlainTextComponentSerializer.plainText().serialize(e.message()).replaceAll("&([0-9]|[a-g]|[k-o]|r)", "").length() > 15) {

						e.setCancelled(true);
						Util.sudoSignsMessage(p, "&cERROR: The message can't be greater than 15 characters!");

						return;

					}

					handle(e, true, null, editor, user, edit -> editor.getMainMenu().setText(Util.legacySerializerAnyCase(PlainTextComponentSerializer.plainText().serialize(e.message()))), editor::goToMain);

					break;
				case SET_PRICE:
					if (PlainTextComponentSerializer.plainText().serialize(e.message()).equalsIgnoreCase("cancel")) {

						handle(e, true, "Cancelled!", editor, user, null, editor::goToMessages);

						return;

					}

					if(Util.priceIsInteger()) {

						int price;
						try {

							price = Integer.parseInt(PlainTextComponentSerializer.plainText().serialize(e.message()));

						}
						catch (NumberFormatException nfe) {

							e.setCancelled(true);
							Util.sudoSignsMessage(p, "&cERROR: Please enter a valid integer!");

							return;

						}

						if (price < 0) {

							e.setCancelled(true);
							Util.sudoSignsMessage(p, "&cERROR: Please enter &e0&c, or a positive integer!");

							return;

						}

						handle(e, true, null, editor, user, edit -> editor.getMainMenu().setPriceAsInteger(price), editor::goToMain);

					}
					else {

						double price;
						try {

							price = Double.parseDouble(PlainTextComponentSerializer.plainText().serialize(e.message()));

						}
						catch (NumberFormatException nfe) {

							e.setCancelled(true);
							Util.sudoSignsMessage(p, "&cERROR: Please enter a valid number!");

							return;

						}

						if (price < 0) {

							e.setCancelled(true);
							Util.sudoSignsMessage(p, "&cERROR: Please enter &e0&c, or a positive number!");

							return;

						}

						handle(e, true, null, editor, user, edit -> editor.getMainMenu().setPriceAsDouble(price), editor::goToMain);

					}

					break;

				}

			}

		}

	}

	private void handle(AsyncChatEvent e, boolean cancel, String message, SignEditor editor, SudoUser user, Consumer<SignEditor> eventConsumer, Runnable finalAction) {

		if (cancel) {

			e.setCancelled(true);

		}

		if (message != null) {

			Util.sudoSignsMessage(e.getPlayer(), "&c" + message);

		}

		if (eventConsumer != null) {

			Bukkit.getScheduler().runTask(SudoSigns.sudoSignsPlugin, () -> eventConsumer.accept(editor));

		}

		user.removeTextInput();

		if (finalAction != null) {

			Bukkit.getScheduler().runTask(SudoSigns.sudoSignsPlugin, finalAction);

		}

	}

}