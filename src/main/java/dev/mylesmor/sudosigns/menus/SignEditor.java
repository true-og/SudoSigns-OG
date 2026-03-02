package dev.mylesmor.sudosigns.menus;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.PlayerInput;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.data.SudoUser;
import net.trueog.diamondbankog.api.DiamondBankAPIJava;
import net.trueog.utilitiesog.UtilitiesOG;

public class SignEditor {

    private final Player p;
    private final SudoUser su;
    private final SudoSign sign;
    private MainMenu mainMenu;
    private PermissionsMenu permMenu;
    private CommandsMenu commandsMenu;
    private CommandOptionsMenu commandOptionsMenu;
    private MessageOptionsMenu messageOptionsMenu;
    private MessagesMenu messagesMenu;
    private GUIPage currentPage;
    private final DiamondBankAPIJava diamondBankAPI;

    public SignEditor(Player p, SudoSign s, SudoUser su, DiamondBankAPIJava diamondBankAPI) {

        this.su = su;
        this.p = p;
        this.sign = s;
        this.diamondBankAPI = diamondBankAPI;

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

            mainMenu = new MainMenu(p, sign, this, diamondBankAPI);

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

        UtilitiesOG.trueogMessage(p, "&aChanges saved to sign &e" + sign.getName() + " &a.");

    }

    public void editSignNumber() {

        UtilitiesOG.trueogMessage(p, "&6Please enter the line you would like to edit (1-4) or type &cCANCEL&6!");
        su.addTextInput(PlayerInput.EDIT_TEXT_NUMBER);
        p.closeInventory();

    }

    public void prepareRename() {

        UtilitiesOG.trueogMessage(p, "&6Please enter the new name for the sign in chat or type &cCANCEL&6!");

        su.addTextInput(PlayerInput.RENAME);

        p.closeInventory();

    }

    public void renameSign(String s) {

        if (SudoSigns.signs.containsKey(s)) {

            UtilitiesOG.trueogMessage(p, "&cERROR: A sign with the name &e" + s + " &calready exists! &6Cancelling...");

        } else {

            sign.setName(s);

            Map.Entry<String, SudoSign> found = null;
            for (Iterator<Entry<String, SudoSign>> iterator = SudoSigns.signs.entrySet().iterator(); iterator
                    .hasNext();)
            {

                final Map.Entry<String, SudoSign> entry = iterator.next();
                if (entry.getValue().equals(sign)) {

                    found = entry;

                }

            }

            if (found != null) {

                SudoSigns.signs.remove(found.getKey());
                SudoSigns.signs.put(s, sign);

                UtilitiesOG.trueogMessage(p, "&aSign successfully renamed to &e" + s + "&a.");

                SudoSigns.config.saveSign(found.getValue(), true, p);
                SudoSigns.config.deleteSign(found.getKey());

            }

        }

    }

}