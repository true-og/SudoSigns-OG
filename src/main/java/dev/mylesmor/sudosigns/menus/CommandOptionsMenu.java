package dev.mylesmor.sudosigns.menus;

import java.util.ArrayList;
import java.util.List;

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

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.PlayerInput;
import dev.mylesmor.sudosigns.data.SignCommand;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.data.SudoUser;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import net.kyori.adventure.text.TextComponent;
import net.trueog.utilitiesog.UtilitiesOG;

public class CommandOptionsMenu {

    private Inventory menu;
    private final SudoUser su;
    private final SudoSign sign;
    private final SignCommand sc;
    private final Player p;
    private final SignEditor editor;

    CommandOptionsMenu(SudoUser su, Player p, SudoSign sign, ItemStack item, SignEditor editor) {

        this.su = su;
        this.sign = sign;
        this.p = p;
        this.editor = editor;
        this.sc = findSignCommand(item);

        if (this.sc == null) {

            editor.goToCommands();

            return;

        }

    }

    public void goToCommandOptionsMenu() {

        createCommandOptionsMenu();
        p.openInventory(menu);

    }

    private SignCommand findSignCommand(ItemStack item) {

        final NamespacedKey key = new NamespacedKey(SudoSigns.sudoSignsPlugin, "command-number");
        final ItemMeta itemMeta = item.getItemMeta();
        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (container.has(key, PersistentDataType.INTEGER)) {

            final int foundValue = container.get(key, PersistentDataType.INTEGER);

            return sign.getSignCommandByNumber(foundValue);

        }

        return null;

    }

    private void createCommandOptionsMenu() {

        final Inventory playersInventory = p.getInventory();
        final InventoryHolder inventoryContainer = playersInventory.getHolder(false);
        final TextComponent nameHandler = UtilitiesOG.trueogColorize("Command options: /" + sc.getCommand());

        menu = Bukkit.createInventory(inventoryContainer, 45, nameHandler);
        for (int i = 0; i < menu.getSize(); i++) {

            menu.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

        }

        final List<String> lore = new ArrayList<>();
        final ItemStack arrow = new ItemStack(Material.ARROW);
        final ItemMeta arrowMeta = arrow.getItemMeta();

        arrowMeta.displayName(UtilitiesOG.trueogColorize("&r&dBACK"));
        arrow.setItemMeta(arrowMeta);

        final ItemStack clock = new ItemStack(Material.CLOCK);
        final ItemMeta clockMeta = clock.getItemMeta();

        lore.add("&eChange the delay after which the command executes");
        clockMeta.displayName(UtilitiesOG.trueogColorize("&r&dChange Delay"));
        clockMeta.lore(Util.convertToTextComponents(lore));
        clock.setItemMeta(clockMeta);

        final ItemStack barrier = new ItemStack(Material.BARRIER);
        final ItemMeta barrierMeta = barrier.getItemMeta();

        barrierMeta.displayName(UtilitiesOG.trueogColorize("&r&cDelete Command"));
        barrier.setItemMeta(barrierMeta);

        if (p.hasPermission(Permissions.COMMAND_OPTIONS) && p.hasPermission(Permissions.DELETE_COMMAND)) {

            menu.setItem(20, clock);
            menu.setItem(24, barrier);

        } else {

            menu.setItem(22, clock);

        }

        menu.setItem(36, arrow);

    }

    public void addDelay() {

        p.closeInventory();

        UtilitiesOG.trueogMessage(p, "&6Please enter the delay in seconds. To cancel, type &cCANCEL&6.");

        su.addTextInput(PlayerInput.COMMAND_DELAY);

    }

    public void setDelay(double delay) {

        final double oldDelay = sc.getDelay();
        sc.setDelay(delay * 1000);

        SudoSigns.config.deleteCommand(sign, sc, sc.getType(), oldDelay);
        SudoSigns.config.addCommand(sign, sc, sc.getType());

    }

    public void deleteCommand() {

        if (sc.getType() == PlayerInput.CONSOLE_COMMAND) {

            SudoSigns.config.deleteCommand(sign, sc, PlayerInput.CONSOLE_COMMAND, sc.getDelay());
            sign.deleteConsoleCommand(sc);

        } else if (sc.getType() == PlayerInput.PLAYER_COMMAND) {

            SudoSigns.config.deleteCommand(sign, sc, PlayerInput.PLAYER_COMMAND, sc.getDelay());
            sign.deletePlayerCommand(sc);

        }

        editor.goToCommands();

    }

}