package dev.mylesmor.sudosigns.menus;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.PlayerInput;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.data.SudoUser;
import dev.mylesmor.sudosigns.util.Util;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SignEditor {

    private Player p;
    private SudoUser su;
    private SudoSign sign;
    private MainMenu mainMenu;
    private PermissionsMenu permMenu;
    private CommandsMenu commandsMenu;
    private CommandOptionsMenu commandOptionsMenu;
    private MessageOptionsMenu messageOptionsMenu;
    private MessagesMenu messagesMenu;
    private GUIPage currentPage;

    public SignEditor(Player p, SudoSign s, SudoUser su) {

        this.su = su;
        this.p = p;
        this.sign = s;

        goToMain();
    }

    public GUIPage getCurrentPage() {

        return currentPage;
    }

    public void setCurrentPage(GUIPage page) {

        currentPage = page;
    }

    public void goToMain() {

        if (mainMenu == null) {

            mainMenu = new MainMenu(p, sign, this);
        }

        setCurrentPage(GUIPage.MAIN);
        mainMenu.goToMainMenu();
    }

    public void goToPermissions() {

        if (permMenu == null) {

            permMenu = new PermissionsMenu(su, p, sign, this);
        }

        setCurrentPage(GUIPage.PERMISSIONS);
        permMenu.goToPermissionsMenu();
    }

    public void goToCommands() {

        if (commandsMenu == null) {

            commandsMenu = new CommandsMenu(su, p, sign, this);
        }

        setCurrentPage(GUIPage.COMMANDS);
        commandsMenu.goToCommandsMenu();
    }

    public void goToMessages() {

        if (messagesMenu == null) {

            messagesMenu = new MessagesMenu(su, p, sign, this);
        }

        setCurrentPage(GUIPage.MESSAGES);
        messagesMenu.goToMessagesMenu();
    }

    public void goToCommandOptionsMenu(ItemStack item) {

        commandOptionsMenu = new CommandOptionsMenu(su, p, sign, item, this);

        setCurrentPage(GUIPage.COMMAND_OPTIONS);
        commandOptionsMenu.goToCommandOptionsMenu();
    }

    public void goToMessageOptionsMenu(ItemStack item) {

        messageOptionsMenu = new MessageOptionsMenu(su, p, sign, item, this);

        setCurrentPage(GUIPage.MESSAGE_OPTIONS);
        messageOptionsMenu.goToMessageOptionsMenu();
    }

    public MainMenu getMainMenu() {

        return mainMenu;
    }

    public PermissionsMenu getPermMenu() {

        return permMenu;
    }

    public CommandsMenu getCommandsMenu() {

        return commandsMenu;
    }

    public CommandOptionsMenu getCommandOptionsMenu() {

        return commandOptionsMenu;
    }

    public MessagesMenu getMessagesMenu() {

        return messagesMenu;
    }

    public MessageOptionsMenu getMessageOptionsMenu() {

        return messageOptionsMenu;
    }

    public void endEditor() {

        Util.sudoSignsMessage(p, "&aChanges saved to sign &e%NAME%&a.", sign.getName());
    }

    public void editSignNumber() {

        Util.sudoSignsMessage(p, "&6Please enter the line you would like to edit (1-4) or type &cCANCEL&6!");
        su.addTextInput(PlayerInput.EDIT_TEXT_NUMBER);
        p.closeInventory();
    }

    public void prepareRename() {

        Util.sudoSignsMessage(p, "&6Please enter the new name for the sign in chat or type &cCANCEL&6!");
        su.addTextInput(PlayerInput.RENAME);
        p.closeInventory();
    }

    public void renameSign(String s) {

        if (SudoSigns.signs.containsKey(s)) {

            Util.sudoSignsMessage(p, "&cERROR: A sign with the name &e%NAME% &calready exists! &6Cancelling...", s);

        } else {

            sign.setName(s);

            Map.Entry<String, SudoSign> found = null;
            for (Map.Entry<String, SudoSign> entry : SudoSigns.signs.entrySet()) {

                if (entry.getValue().equals(sign)) {

                    found = entry;
                }
            }

            if (found != null) {

                SudoSigns.signs.remove(found.getKey());
                SudoSigns.signs.put(s, sign);

                Util.sudoSignsMessage(p, "&aSign successfully renamed to &e%NAME%&a.", s);

                SudoSigns.config.saveSign(found.getValue(), true, p);
                SudoSigns.config.deleteSign(found.getKey());
            }
        }
    }
}
