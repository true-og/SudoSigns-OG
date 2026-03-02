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
import net.trueog.utilitiesog.UtilitiesOG;

/**
 * The ChatListener class for taking user input in chat.
 * 
 * @author MylesMor
 * @author https://mylesmor.dev
 * @Maintainer NotAlexNoyle
 * @Maintainer https://true-og.net
 */
public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {

        final Player p = playerCommandPreprocessEvent.getPlayer();
        final SudoUser user = SudoSigns.users.get(p.getUniqueId());
        final boolean condition = user != null && user.isTextInput() && user.isEditing();
        if (!condition) {

            return;

        }

        if (user.getInputType() == PlayerInput.CONSOLE_COMMAND || user.getInputType() == PlayerInput.PLAYER_COMMAND) {

            user.getEditor().getCommandsMenu().addCommand(playerCommandPreprocessEvent.getMessage().substring(1),
                    user.getInputType());
            user.removeTextInput();

        } else {

            UtilitiesOG.trueogMessage(p, "&eWARNING: &6Operation Cancelled!");

            user.removeTextInput();

        }

        playerCommandPreprocessEvent.setCancelled(true);

    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncChatEvent asyncChatEvent) {

        final Player p = asyncChatEvent.getPlayer();
        final SudoUser user = SudoSigns.users.get(p.getUniqueId());
        final boolean condition = user != null && user.isTextInput() && user.isEditing();
        if (!condition) {

            return;

        }

        final SignEditor editor = user.getEditor();
        switch (user.getInputType()) {

            case PLAYER_COMMAND, CONSOLE_COMMAND -> {

                if (PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message())
                        .equalsIgnoreCase("cancel"))
                {

                    handle(asyncChatEvent, true, "Cancelled!", editor, user, null, editor::goToCommands);

                    return;

                }

                user.removeTextInput();
                handle(asyncChatEvent, true, "No command found! Cancelling...", editor, user, null,
                        editor::goToCommands);

            }
            case RENAME -> {

                if (PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message())
                        .equalsIgnoreCase("cancel"))
                {

                    handle(asyncChatEvent, true, "Cancelled!", editor, user, null, editor::goToMain);

                    return;

                }

                handle(asyncChatEvent, true, null, editor, user,
                        edit -> editor.renameSign(
                                PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message())),
                        editor::goToMain);

            }
            case PERMISSION -> {

                if (PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message())
                        .equalsIgnoreCase("cancel"))
                {

                    handle(asyncChatEvent, true, "Cancelled!", editor, user, null, editor::goToPermissions);

                    return;

                }

                handle(asyncChatEvent, true, null, editor, user,
                        edit -> editor.getPermMenu().addPermission(true,
                                PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message())),
                        editor::goToPermissions);

            }
            case MESSAGE -> {

                if (PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message())
                        .equalsIgnoreCase("cancel"))
                {

                    handle(asyncChatEvent, true, "Cancelled!", editor, user, null, editor::goToMessages);

                    return;

                }

                handle(asyncChatEvent, true, null, editor, user,
                        edit -> editor.getMessagesMenu().addMessage(
                                PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message())),
                        editor::goToMessages);

            }
            case COMMAND_DELAY -> {

                if (PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message())
                        .equalsIgnoreCase("cancel"))
                {

                    handle(asyncChatEvent, true, "Cancelled!", editor, user, null, editor::goToCommands);

                    return;

                }

                double delay = 0;
                try {

                    delay = Double
                            .parseDouble(PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message()));

                } catch (NumberFormatException numberFormatException) {

                    asyncChatEvent.setCancelled(true);
                    UtilitiesOG.trueogMessage(p, "&cERROR: Please enter a valid number.");

                    return;

                }

                final double finalDelay = delay;
                handle(asyncChatEvent, true, null, editor, user,
                        edit -> editor.getCommandOptionsMenu().setDelay(finalDelay), editor::goToCommands);

            }
            case MESSAGE_DELAY -> {

                if (PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message())
                        .equalsIgnoreCase("cancel"))
                {

                    handle(asyncChatEvent, true, "Cancelled!", editor, user, null, editor::goToMessages);

                    return;

                }

                double messageDelay = 0;
                try {

                    messageDelay = Double
                            .parseDouble(PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message()));

                } catch (NumberFormatException nfe) {

                    asyncChatEvent.setCancelled(true);
                    UtilitiesOG.trueogMessage(p, "&cERROR: Please enter a valid number!");

                    return;

                }

                final double finalMessageDelay = messageDelay;
                handle(asyncChatEvent, true, null, editor, user,
                        edit -> editor.getMessageOptionsMenu().setDelay(finalMessageDelay), editor::goToMessages);

            }
            case EDIT_TEXT_NUMBER -> {

                if (PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message())
                        .equalsIgnoreCase("cancel"))
                {

                    handle(asyncChatEvent, true, "Cancelled!", editor, user, null, editor::goToMessages);

                    return;

                }

                int lineNumber = 0;
                try {

                    lineNumber = Integer
                            .parseInt(PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message()));

                } catch (NumberFormatException nfe) {

                    asyncChatEvent.setCancelled(true);
                    UtilitiesOG.trueogMessage(p, "&cERROR: Please enter a valid number between 1-4!");

                    return;

                }

                if (lineNumber < 1 || lineNumber > 4) {

                    asyncChatEvent.setCancelled(true);
                    UtilitiesOG.trueogMessage(p, "&cERROR: Please enter a valid number between 1-4!");

                    return;

                }

                final int finalLineNumber = lineNumber;
                handle(asyncChatEvent, true, null, editor, user,
                        edit -> editor.getMainMenu().setLineNumber(finalLineNumber), null);

            }
            case EDIT_TEXT -> {

                if (PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message())
                        .equalsIgnoreCase("cancel"))
                {

                    handle(asyncChatEvent, true, "Cancelled!", editor, user, null, editor::goToMessages);

                    return;

                }

                // Regular expression contributed by lazerl0rd (MIT) at
                // https://github.com/MylesMor/SudoSigns/pull/4
                // https://web.archive.org/web/20260302082938/https://github.com/MylesMor/SudoSigns/pull/4
                if (PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message())
                        .replaceAll("&([0-9]|[a-g]|[k-o]|r)", "").length() > 15)
                {

                    asyncChatEvent.setCancelled(true);
                    UtilitiesOG.trueogMessage(p, "&cERROR: The message can't be greater than 15 characters!");

                    return;

                }

                handle(asyncChatEvent, true, null, editor, user,
                        edit -> editor.getMainMenu()
                                .setText(UtilitiesOG.trueogColorize(
                                        PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message()))),
                        editor::goToMain);

            }
            case SET_PRICE -> {

                if (PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message())
                        .equalsIgnoreCase("cancel"))
                {

                    handle(asyncChatEvent, true, "Cancelled!", editor, user, null, editor::goToMessages);

                    return;

                }

                if (Util.priceIsInteger()) {

                    final int price;
                    try {

                        price = Integer
                                .parseInt(PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message()));

                    } catch (NumberFormatException nfe) {

                        asyncChatEvent.setCancelled(true);
                        UtilitiesOG.trueogMessage(p, "&cERROR: Please enter a valid integer!");

                        return;

                    }

                    if (price < 0) {

                        asyncChatEvent.setCancelled(true);
                        UtilitiesOG.trueogMessage(p, "&cERROR: Please enter &e0&c, or a positive integer!");

                        return;

                    }

                    handle(asyncChatEvent, true, null, editor, user,
                            edit -> editor.getMainMenu().setPriceAsInteger(price), editor::goToMain);

                } else {

                    final double price;
                    try {

                        price = Double.parseDouble(
                                PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message()));

                    } catch (NumberFormatException nfe) {

                        asyncChatEvent.setCancelled(true);
                        UtilitiesOG.trueogMessage(p, "&cERROR: Please enter a valid number!");

                        return;

                    }

                    if (price < 0) {

                        asyncChatEvent.setCancelled(true);
                        UtilitiesOG.trueogMessage(p, "&cERROR: Please enter &e0&c, or a positive number!");

                        return;

                    }

                    handle(asyncChatEvent, true, null, editor, user,
                            edit -> editor.getMainMenu().setPriceAsDouble(price), editor::goToMain);

                }

            }

        }

    }

    private void handle(AsyncChatEvent e, boolean cancel, String message, SignEditor editor, SudoUser user,
            Consumer<SignEditor> eventConsumer, Runnable finalAction)
    {

        if (cancel) {

            e.setCancelled(true);

        }

        if (message != null) {

            UtilitiesOG.trueogMessage(e.getPlayer(), "&c" + message);

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