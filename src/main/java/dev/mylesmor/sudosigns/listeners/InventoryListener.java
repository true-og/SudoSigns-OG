package dev.mylesmor.sudosigns.listeners;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.PlayerInput;
import dev.mylesmor.sudosigns.data.SudoUser;
import dev.mylesmor.sudosigns.menus.SignEditor;
import dev.mylesmor.sudosigns.util.Permissions;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * InventoryListener class to listen for GUI actions.
 * 
 * @author MylesMor
 * @author https://mylesmor.dev
 */
public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        final Player p = (Player) e.getWhoClicked();
        final SudoUser user = SudoSigns.users.get(p.getUniqueId());
        final boolean condition = user != null && user.isEditing()
                && Objects.equals(e.getClickedInventory(), e.getView().getTopInventory());
        if (condition) {

            final SignEditor editor = user.getEditor();
            if (e.getCurrentItem() != null) {

                e.setCancelled(true);
                final Material m = e.getCurrentItem().getType();
                final String itemName = e.getCurrentItem().getDisplayName();
                switch (editor.getCurrentPage()) {

                    case MAIN -> checkForMainMenuClicks(editor, m);
                    case COMMANDS -> checkForCommandsClicks(p, editor, m, e.getCurrentItem());
                    case PERMISSIONS -> checkForPermissionsClicks(p, editor, m, itemName);
                    case CHOOSE_COMMAND -> chooseCommandType(editor, m, itemName);
                    case CHOOSE_PERMISSION -> choosePermissionType(editor, m);
                    case MESSAGES -> checkForMessagesClicks(p, editor, m, e.getCurrentItem());
                    case COMMAND_OPTIONS -> checkForCommandOptionClicks(p, editor, m, itemName);
                    case MESSAGE_OPTIONS -> checkForMessageOptionClicks(p, editor, m, itemName);

                }

            }

        }

    }

    @EventHandler
    public void closeInventory(InventoryCloseEvent e) {

        final Player p = (Player) e.getPlayer();
        final SudoUser user = SudoSigns.users.get(p.getUniqueId());
        final boolean condition = user != null && user.isEditing();
        // Checks whether the user has closed the GUI.
        if (condition) {

            Bukkit.getScheduler().scheduleSyncDelayedTask(SudoSigns.sudoSignsPlugin, () -> {

                final InventoryView currentInv = p.getOpenInventory();
                if (PlainTextComponentSerializer.plainText().serialize(currentInv.title()).equalsIgnoreCase("CRAFTING")
                        && !user.isTextInput())
                {

                    user.getEditor().endEditor();
                    user.removeEditor();

                }

            }, 5L);

        }

    }

    /**
     * Checks for GUI clicks in the main menu.
     * 
     * @param editor The SignEditor class of the particular user.
     * @param m      The material clicked on in the menu.
     */
    public void checkForMainMenuClicks(SignEditor editor, Material m) {

        if (m.equals(Material.OAK_SIGN)) {

            editor.goToMessages();
            return;

        }

        switch (m) {

            case NAME_TAG -> editor.prepareRename();
            case WRITABLE_BOOK -> editor.editSignNumber();
            case BARRIER -> editor.goToPermissions();
            case COMMAND_BLOCK -> editor.goToCommands();
            case BIRCH_SIGN -> editor.goToMessages();
            case GOLD_NUGGET -> editor.getMainMenu().prepareSetPrice();
            default -> {

            }

        }

    }

    /**
     * Checks for GUI clicks in the Command Options menu.
     * 
     * @param editor The SignEditor class of the particular user.
     * @param m      The material clicked on in the menu.
     * @param item   The ItemStack of the clicked item.
     */
    public void checkForCommandsClicks(Player p, SignEditor editor, Material m, ItemStack item) {

        switch (m) {

            case WRITABLE_BOOK -> editor.getCommandsMenu().prepareCommand();
            case BOOK -> {

                if (p.hasPermission(Permissions.COMMAND_OPTIONS)) {

                    editor.goToCommandOptionsMenu(item);

                }

            }
            case ARROW -> editor.goToMain();
            default -> {

            }

        }

    }

    /**
     * Checks for GUI clicks in the Command Options menu.
     * 
     * @param editor   The SignEditor class of the particular user.
     * @param m        The material clicked on in the menu.
     * @param itemName The name of the item clicked.
     */
    public void checkForCommandOptionClicks(Player p, SignEditor editor, Material m, String itemName) {

        switch (m) {

            case CLOCK -> editor.getCommandOptionsMenu().addDelay();
            case BARRIER -> {

                if (p.hasPermission(Permissions.DELETE_COMMAND)) {

                    editor.getCommandOptionsMenu().deleteCommand();

                }

            }
            case ARROW -> editor.goToCommands();
            default -> {

            }

        }

    }

    /**
     * Checks for GUI clicks in the Commands menu.
     * 
     * @param editor   The SignEditor class of the particular user.
     * @param m        The material clicked on in the menu.
     * @param itemName The name of the item clicked.
     */
    public void checkForMessageOptionClicks(Player p, SignEditor editor, Material m, String itemName) {

        switch (m) {

            case CLOCK -> editor.getMessageOptionsMenu().addDelay();
            case BARRIER -> {

                if (p.hasPermission(Permissions.DELETE_MESSAGE)) {

                    editor.getMessageOptionsMenu().deleteMessage();

                }

            }
            case ARROW -> editor.goToMessages();
            default -> {

            }

        }

    }

    /**
     * Checks for GUI clicks in the Commands menu.
     * 
     * @param editor The SignEditor class of the particular user.
     * @param m      The material clicked on in the menu.
     * @param item   The ItemStack of the clicked item.
     */
    public void checkForMessagesClicks(Player p, SignEditor editor, Material m, ItemStack item) {

        switch (m) {

            case WRITABLE_BOOK -> editor.getMessagesMenu().prepareMessage();
            case BOOK -> {

                if (p.hasPermission(Permissions.MESSAGE_OPTIONS)) {

                    editor.goToMessageOptionsMenu(item);

                }

            }
            case ARROW -> editor.goToMain();
            default -> {

            }

        }

    }

    public void choosePermissionType(SignEditor editor, Material m) {

        switch (m) {

            case PLAYER_HEAD -> editor.getPermMenu().addPermission(true, null);
            case COMMAND_BLOCK -> editor.getPermMenu().addPermission(false, null);
            case ARROW -> editor.goToPermissions();
            default -> {

            }

        }

    }

    public void chooseCommandType(SignEditor editor, Material m, String itemName) {

        switch (m) {

            case PLAYER_HEAD -> editor.getCommandsMenu().chooseCommandType(PlayerInput.PLAYER_COMMAND);
            case COMMAND_BLOCK -> editor.getCommandsMenu().chooseCommandType(PlayerInput.CONSOLE_COMMAND);
            case ARROW -> editor.goToCommands();
            default -> {

            }

        }

    }

    /**
     * Checks for GUI clicks in the Permissions menu.
     * 
     * @param editor   The SignEditor class of the particular user.
     * @param m        The material clicked on in the menu.
     * @param itemName The name of the item clicked.
     */
    public void checkForPermissionsClicks(Player p, SignEditor editor, Material m, String itemName) {

        switch (m) {

            case BOOK:
                if (p.hasPermission(Permissions.DELETE_PERMISSION)) {

                    editor.getPermMenu().deletePermission(itemName);

                }
                break;
            case WRITABLE_BOOK:
                editor.getPermMenu().preparePermission();
                break;
            case ARROW:
                editor.goToMain();
            default:
                break;

        }

    }

}
