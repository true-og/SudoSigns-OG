package dev.mylesmor.sudosigns.menus;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.PlayerInput;
import dev.mylesmor.sudosigns.data.SignMessage;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.data.SudoUser;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import java.util.ArrayList;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class MessageOptionsMenu {

    private Inventory menu;
    private SudoUser su;
    private SudoSign sign;
    private SignMessage sm;
    private Player p;
    private SignEditor editor;

    MessageOptionsMenu(SudoUser su, Player p, SudoSign sign, ItemStack item, SignEditor editor) {

        this.su = su;
        this.sign = sign;
        this.p = p;
        this.editor = editor;
        this.sm = findSignMessage(item);

        if (this.sm == null) {

            editor.goToMessages();

            return;

        }

    }

    public void goToMessageOptionsMenu() {

        createMessageOptionsMenu();
        p.openInventory(menu);

    }

    private SignMessage findSignMessage(ItemStack item) {

        NamespacedKey key = new NamespacedKey(SudoSigns.sudoSignsPlugin, "message-number");
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (container.has(key, PersistentDataType.INTEGER)) {

            int foundValue = container.get(key, PersistentDataType.INTEGER);

            return sign.getSignMessageByNumber(foundValue);

        }

        return null;

    }

    private void createMessageOptionsMenu() {

        Inventory playersInventory = p.getInventory();
        InventoryHolder inventoryContainer = playersInventory.getHolder(false);
        TextComponent nameHandler = Util.legacySerializerAnyCase("&6Message options");

        menu = Bukkit.createInventory(inventoryContainer, 45, nameHandler);

        for (int i = 0; i < menu.getSize(); i++) {

            menu.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

        }

        List<String> lore = new ArrayList<>();

        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta arrowMeta = arrow.getItemMeta();

        arrowMeta.displayName(Util.legacySerializerAnyCase("&r&dBACK"));
        arrow.setItemMeta(arrowMeta);

        ItemStack clock = new ItemStack(Material.CLOCK);
        ItemMeta clockMeta = clock.getItemMeta();

        lore.add("&eChange the delay after which the message is sent");
        clockMeta.displayName(Util.legacySerializerAnyCase("&r&dChange Delay"));
        clockMeta.lore(Util.convertToTextComponents(lore));
        clock.setItemMeta(clockMeta);

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta barrierMeta = barrier.getItemMeta();

        barrierMeta.displayName(Util.legacySerializerAnyCase("&r&dDelete Message"));
        barrier.setItemMeta(barrierMeta);

        if (p.hasPermission(Permissions.MESSAGE_OPTIONS) && p.hasPermission(Permissions.DELETE_MESSAGE)) {

            menu.setItem(20, clock);
            menu.setItem(24, barrier);

        } else {

            menu.setItem(22, clock);

        }

        menu.setItem(36, arrow);

    }

    public void addDelay() {

        p.closeInventory();
        Util.sudoSignsMessage(p, "&6Please enter the delay in seconds. To cancel, type &cCANCEL &6.");
        su.addTextInput(PlayerInput.MESSAGE_DELAY);

    }

    public void setDelay(double delay) {

        double oldDelay = sm.getDelay();
        sm.setDelay(delay * 1000);

        SudoSigns.config.deleteMessage(sign, sm, oldDelay);
        SudoSigns.config.addMessage(sign, sm);

    }

    public void deleteMessage() {

        sign.removeMessage(sm);
        SudoSigns.config.deleteMessage(sign, sm, sm.getDelay());
        editor.goToMessages();

    }

}
