package dev.mylesmor.sudosigns.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.PlayerInput;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.data.SudoUser;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import net.kyori.adventure.text.TextComponent;
import net.trueog.utilitiesog.UtilitiesOG;

public class PermissionsMenu {

    private final GUIPage PAGE = GUIPage.PERMISSIONS;

    private Inventory menu;
    private final Player p;
    private final SudoSign sign;
    private final SignEditor editor;
    private final SudoUser su;

    public PermissionsMenu(SudoUser su, Player p, SudoSign sign, SignEditor editor) {

        this.su = su;
        this.sign = sign;
        this.editor = editor;
        this.p = p;

    }

    public void goToPermissionsMenu() {

        createPermissionsMenu();

        p.openInventory(menu);
        editor.setCurrentPage(PAGE);

    }

    private void createPermissionsMenu() {

        final Inventory playersInventory = p.getInventory();
        final InventoryHolder inventoryContainer = playersInventory.getHolder(false);
        final TextComponent nameHandler = UtilitiesOG.trueogColorize("&6Default or Custom?");

        menu = Bukkit.createInventory(inventoryContainer, 45, nameHandler);
        for (int i = 0; i < menu.getSize(); i++) {

            menu.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

        }

        final ItemStack arrow = new ItemStack(Material.ARROW);
        final ItemMeta arrowMeta = arrow.getItemMeta();

        arrowMeta.displayName(UtilitiesOG.trueogColorize("&r&dBACK"));
        arrow.setItemMeta(arrowMeta);

        final ItemStack barrier = new ItemStack(Material.BARRIER);
        final ItemMeta barrierMeta = barrier.getItemMeta();

        barrierMeta.displayName(UtilitiesOG.trueogColorize("&r&5Permissions"));
        barrier.setItemMeta(barrierMeta);

        if (p.hasPermission(Permissions.ADD_PERMISSION)) {

            final ItemStack bookQuill = new ItemStack(Material.WRITABLE_BOOK);
            final ItemMeta bqMeta = bookQuill.getItemMeta();

            bqMeta.displayName(UtilitiesOG.trueogColorize("&aAdd new permission"));
            bookQuill.setItemMeta(bqMeta);
            menu.setItem(40, bookQuill);

        }

        final List<String> lore = new ArrayList<>();

        lore.add("&cClick to delete!");

        int i = 1;
        for (String perm : sign.getPermissions()) {

            if (i > 35) {

                break;

            }

            final ItemStack book = new ItemStack(Material.BOOK);
            final ItemMeta bookMeta = book.getItemMeta();
            bookMeta.displayName(UtilitiesOG.trueogColorize("&r&6" + perm));
            if (p.hasPermission(Permissions.DELETE_PERMISSION)) {

                lore.add("");
                bookMeta.lore(Util.convertToTextComponents(lore));

            }

            book.setItemMeta(bookMeta);

            if (i == 9 || i == 18 || i == 27) {

                i++;

            }

            menu.setItem(i, book);

            i++;

        }

        menu.setItem(0, barrier);
        menu.setItem(9, barrier);
        menu.setItem(18, barrier);
        menu.setItem(27, barrier);
        menu.setItem(36, arrow);

    }

    public void preparePermission() {

        final Inventory playersInventory = p.getInventory();
        final InventoryHolder inventoryContainer = playersInventory.getHolder(false);
        final TextComponent nameHandler = UtilitiesOG.trueogColorize("&6Default or Custom?");
        final Inventory choiceInv = Bukkit.createInventory(inventoryContainer, 45, nameHandler);

        final int inventorySize = choiceInv.getSize();
        final ArrayList<String> lore = new ArrayList<>(inventorySize);
        for (int i = 0; i < inventorySize; i++) {

            lore.add("");

            choiceInv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            choiceInv.getItem(i).lore(Util.convertToTextComponents(lore));

        }

        final ItemStack arrow = new ItemStack(Material.ARROW);
        final ItemMeta arrowMeta = arrow.getItemMeta();

        arrowMeta.displayName(UtilitiesOG.trueogColorize("&r&dBACK"));
        arrow.setItemMeta(arrowMeta);

        final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        final ItemMeta headMeta = head.getItemMeta();
        final List<String> loreString = new ArrayList<>();

        loreString.add("&6sudosigns.sign." + sign.getName());
        headMeta.displayName(UtilitiesOG.trueogColorize("&r&dDefault Permission"));
        headMeta.lore(Util.convertToTextComponents(loreString));
        head.setItemMeta(headMeta);

        final ItemStack cmdBlock = new ItemStack(Material.COMMAND_BLOCK);
        final ItemMeta cmdBlockMeta = cmdBlock.getItemMeta();

        cmdBlockMeta.displayName(UtilitiesOG.trueogColorize("&r&5Custom Permission"));
        cmdBlock.setItemMeta(cmdBlockMeta);

        choiceInv.setItem(21, head);
        choiceInv.setItem(23, cmdBlock);
        choiceInv.setItem(36, arrow);

        editor.setCurrentPage(GUIPage.CHOOSE_PERMISSION);

        p.openInventory(choiceInv);

    }

    public void addPermission(boolean provided, String perm) {

        if (provided) {

            if (perm == null) {

                sign.addPermission("sudosigns.sign." + sign.getName());
                SudoSigns.config.addPermission(sign, "sudosigns.sign." + sign.getName());

                goToPermissionsMenu();

            } else {

                SudoSigns.config.addPermission(sign, perm);
                sign.addPermission(perm);

            }

        } else {

            p.closeInventory();

            UtilitiesOG.trueogMessage(p,
                    "&6Please enter in chat the permission which the player must have to run this sign, or type &cCANCEL &6to cancel the operation.");

            su.addTextInput(PlayerInput.PERMISSION);

        }

    }

    public void deletePermission(String perm) {

        sign.removePermission(perm);
        SudoSigns.config.deletePermission(sign, perm);

        goToPermissionsMenu();

    }

}