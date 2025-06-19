package dev.mylesmor.sudosigns.menus;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.PlayerInput;
import dev.mylesmor.sudosigns.data.SignMessage;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.data.SudoUser;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class MessagesMenu {

    private Inventory menu;
    private Player p;
    private SudoSign sign;
    private SudoUser su;

    public MessagesMenu(SudoUser su, Player p, SudoSign sign, SignEditor editor) {

        this.su = su;
        this.sign = sign;
        this.p = p;
    }

    public void goToMessagesMenu() {

        createMessagesMenu();
        p.openInventory(menu);
    }

    private void createMessagesMenu() {

        Inventory playersInventory = p.getInventory();
        InventoryHolder inventoryContainer = playersInventory.getHolder(false);
        TextComponent nameHandler = Util.legacySerializerAnyCase("&6Messages: " + sign.getName());

        menu = Bukkit.createInventory(inventoryContainer, 45, nameHandler);

        for (int i = 0; i < menu.getSize(); i++) {

            menu.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta arrowMeta = arrow.getItemMeta();

        arrowMeta.displayName(Util.legacySerializerAnyCase("&r&dBACK"));
        arrow.setItemMeta(arrowMeta);

        if (p.hasPermission(Permissions.ADD_MESSAGE)) {

            ItemStack bookQuill = new ItemStack(Material.WRITABLE_BOOK);
            ItemMeta bqMeta = bookQuill.getItemMeta();

            bqMeta.displayName(Util.legacySerializerAnyCase("&r&2Add new message"));
            bookQuill.setItemMeta(bqMeta);

            menu.setItem(40, bookQuill);
        }

        ItemStack signItem = null;
        if (SudoSigns.version.contains("1.13")) {

            signItem = new ItemStack(Material.valueOf("SIGN"));

        } else {

            signItem = new ItemStack(Material.BIRCH_SIGN);
        }

        ItemMeta signMeta = signItem.getItemMeta();

        signMeta.displayName(Util.legacySerializerAnyCase("&r&dMessages"));
        signItem.setItemMeta(signMeta);

        List<String> lore = new ArrayList<>();
        NamespacedKey key = new NamespacedKey(SudoSigns.sudoSignsPlugin, "message-number");

        ArrayList<SignMessage> orderedSignMessages = sign.getMessages();
        orderedSignMessages.sort(Comparator.comparing(SignMessage::getDelay));

        int i = 1;
        for (SignMessage sm : orderedSignMessages) {

            if (i > 35) break;

            ItemStack book = new ItemStack(Material.BOOK);
            ItemMeta bookMeta = book.getItemMeta();

            lore.clear();
            bookMeta.displayName(Util.legacySerializerAnyCase("&dMessage:"));
            lore.add("&r&f" + sm.getMessage());
            lore.add("");
            lore.add("");
            lore.add("&6Delay: &e" + (sm.getDelay() / 1000) + "s");

            if (p.hasPermission(Permissions.MESSAGE_OPTIONS)) {

                lore.add("");
                lore.add("&2Click for options!");
            }

            bookMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, sm.getNumber());
            bookMeta.lore(Util.convertToTextComponents(lore));
            book.setItemMeta(bookMeta);

            if (i == 9 || i == 18 || i == 27) i++;
            menu.setItem(i, book);

            i++;
        }

        menu.setItem(0, signItem);
        menu.setItem(9, signItem);
        menu.setItem(18, signItem);
        menu.setItem(27, signItem);
        menu.setItem(36, arrow);
    }

    public void addMessage(String message) {

        SignMessage signMessage = new SignMessage(sign.getNextMessageNumber(), message, 0, PlayerInput.MESSAGE);
        SudoSigns.config.addMessage(sign, signMessage);
        sign.addMessage(signMessage);
    }

    public void prepareMessage() {

        p.closeInventory();

        Util.sudoSignsMessage(
                p,
                "&6Please enter in chat the message which will be shown when the sign is clicked. Use the & symbol for chat color codes and the placeholder &e%PLAYER% &6for the player who clicked the sign. To cancel the operation, type &cCANCEL&6.");
        su.addTextInput(PlayerInput.MESSAGE);
    }
}
