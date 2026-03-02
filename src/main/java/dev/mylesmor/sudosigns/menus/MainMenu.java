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
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import net.kyori.adventure.text.TextComponent;
import net.trueog.diamondbankog.api.DiamondBankAPIJava;
import net.trueog.utilitiesog.UtilitiesOG;

public class MainMenu {

    private Inventory menu;
    private final SudoSign sign;
    private final Player p;
    private int lineNumber = 0;
    private final DiamondBankAPIJava diamondBankAPI;

    public MainMenu(Player p, SudoSign sign, SignEditor editor, DiamondBankAPIJava diamondBankAPI) {

        this.sign = sign;
        this.p = p;
        this.diamondBankAPI = diamondBankAPI;

    }

    public void goToMainMenu() {

        createMainMenu();
        p.openInventory(menu);

    }

    private void createMainMenu() {

        final ArrayList<ItemStack> items = new ArrayList<>();

        final Inventory playersInventory = p.getInventory();
        final InventoryHolder inventoryContainer = playersInventory.getHolder(false);
        final TextComponent nameHandler = UtilitiesOG.trueogColorize("&r&2Editing: &e" + sign.getName());

        menu = Bukkit.createInventory(inventoryContainer, 45, nameHandler);
        for (int i = 0; i < menu.getSize(); i++) {

            menu.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

            final List<String> lore = new ArrayList<>();
            lore.add(" ");
            menu.getItem(i).lore(lore.stream().map(UtilitiesOG::trueogColorize).toList());

        }

        if (p.hasPermission(Permissions.RENAME)) {

            final ItemStack nametag = new ItemStack(Material.NAME_TAG);
            final ItemMeta ntMeta = nametag.getItemMeta();

            ntMeta.displayName(UtilitiesOG.trueogColorize("&r&5Rename Sign"));

            final List<String> lore = new ArrayList<>();
            lore.add("&eChange the text that refers");
            lore.add("&eto this sign in the config");
            lore.add("&efile and in commands.");

            ntMeta.lore(lore.stream().map(UtilitiesOG::trueogColorize).toList());
            nametag.setItemMeta(ntMeta);

            items.add(nametag);

        }

        if (p.hasPermission(Permissions.EDIT_TEXT)) {

            final ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
            final ItemMeta bookMeta = book.getItemMeta();

            bookMeta.displayName(UtilitiesOG.trueogColorize("&r&5Edit Sign Text"));

            final List<String> lore = new ArrayList<>();
            lore.add("&eChange the text that displays");
            lore.add("&eon the sign itself. Colored");
            lore.add("&esigns require another plugin.");

            bookMeta.lore(lore.stream().map(UtilitiesOG::trueogColorize).toList());
            book.setItemMeta(bookMeta);

            items.add(book);

        }

        if (p.hasPermission(Permissions.VIEW_PERMISSION)) {

            final ItemStack barrier = new ItemStack(Material.BARRIER);
            final ItemMeta barrierMeta = barrier.getItemMeta();

            barrierMeta.displayName(UtilitiesOG.trueogColorize("&r&dPermissions"));

            final List<String> lore = new ArrayList<>();
            lore.add("&ePlayers must have all permissions");
            lore.add("&elisted in this section to be able");
            lore.add("&eto use the sign.");

            barrierMeta.lore(lore.stream().map(UtilitiesOG::trueogColorize).toList());
            barrier.setItemMeta(barrierMeta);

            items.add(barrier);

        }

        if (p.hasPermission(Permissions.VIEW_COMMAND)) {

            final ItemStack cmdBlock = new ItemStack(Material.COMMAND_BLOCK);
            final ItemMeta cmdBlockMeta = cmdBlock.getItemMeta();

            final List<String> lore = new ArrayList<>();
            lore.add("&eCommands listed here will be executed");
            lore.add("&ewhen a player uses the sign.");

            cmdBlockMeta.lore(lore.stream().map(UtilitiesOG::trueogColorize).toList());
            cmdBlockMeta.displayName(UtilitiesOG.trueogColorize("&r&5Commands"));
            cmdBlock.setItemMeta(cmdBlockMeta);

            items.add(cmdBlock);

        }

        if (p.hasPermission(Permissions.VIEW_MESSAGE)) {

            final ItemStack signBlock = new ItemStack(Material.OAK_SIGN);
            final ItemMeta signMeta = signBlock.getItemMeta();

            signMeta.displayName(UtilitiesOG.trueogColorize("&r&5Messages"));

            final List<String> lore = new ArrayList<>();
            lore.add("&eMessages listed here will be shown");
            lore.add("&eto the player when they use the sign.");

            signMeta.lore(lore.stream().map(UtilitiesOG::trueogColorize).toList());
            signBlock.setItemMeta(signMeta);

            items.add(signBlock);

        }

        final boolean condition = diamondBankAPI != null && p.hasPermission(Permissions.VIEW_PRICE);
        if (condition) {

            final ItemStack goldNugget = new ItemStack(Material.GOLD_NUGGET);
            final ItemMeta goldNuggetItemMeta = goldNugget.getItemMeta();

            final List<String> lore = new ArrayList<>();
            lore.add("&eThe cost to use this sign.");
            lore.add("&e");

            if (!SudoSigns.getPlugin().getConfig().getBoolean("config.currency-symbol-in-front")) {

                if (Util.priceIsInteger()) {

                    if (sign.getPriceAsInteger() == 1) {

                        lore.add("&e" + sign.getPriceAsInteger()
                                + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular"));

                    } else {

                        lore.add("&e" + sign.getPriceAsInteger()
                                + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural"));

                    }

                } else {

                    if (sign.getPriceAsDouble() == 1.0) {

                        lore.add("&e" + sign.getPriceAsDouble()
                                + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular"));

                    } else {

                        lore.add("&e" + sign.getPriceAsDouble()
                                + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural"));

                    }

                }

            } else {

                if (Util.priceIsInteger()) {

                    if (sign.getPriceAsInteger() == 1) {

                        lore.add(SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular") + "&e"
                                + sign.getPriceAsInteger());

                    } else {

                        lore.add(SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural") + "&e"
                                + sign.getPriceAsInteger());

                    }

                } else {

                    if (sign.getPriceAsDouble() == 1.0) {

                        lore.add(SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular") + "&e"
                                + sign.getPriceAsDouble());

                    } else {

                        lore.add(SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural") + "&e"
                                + sign.getPriceAsDouble());

                    }

                }

            }

            if (p.hasPermission(Permissions.SET_PRICE)) {

                lore.add("&e");
                lore.add("&aClick to edit!");

            }

            goldNuggetItemMeta.lore(lore.stream().map(UtilitiesOG::trueogColorize).toList());
            goldNuggetItemMeta.displayName(UtilitiesOG.trueogColorize("&r&dPrice"));
            goldNugget.setItemMeta(goldNuggetItemMeta);

            items.add(goldNugget);

        }

        switch (items.size()) {

            case 6:
                menu.setItem(11, items.get(0));
                menu.setItem(13, items.get(1));
                menu.setItem(15, items.get(2));
                menu.setItem(29, items.get(3));
                menu.setItem(31, items.get(4));
                menu.setItem(33, items.get(5));
            case 5:
                menu.setItem(11, items.get(0));
                menu.setItem(13, items.get(1));
                menu.setItem(15, items.get(2));
                menu.setItem(29, items.get(3));
                menu.setItem(31, items.get(4));
            case 4:
                menu.setItem(11, items.get(0));
                menu.setItem(13, items.get(1));
                menu.setItem(15, items.get(2));
                menu.setItem(29, items.get(3));
                break;
            case 3:
                menu.setItem(20, items.get(0));
                menu.setItem(22, items.get(1));
                menu.setItem(24, items.get(2));
                break;
            case 2:
                menu.setItem(21, items.get(0));
                menu.setItem(23, items.get(1));
                break;
            case 1:
                menu.setItem(22, items.get(0));
                break;

        }

    }

    public void prepareSetPrice() {

        p.closeInventory();

        UtilitiesOG.trueogMessage(p,
                "&6Please enter the price to use the sign! Type &cCANCEL &6to cancel the operation.");
        SudoSigns.users.get(p.getUniqueId()).addTextInput(PlayerInput.SET_PRICE);

    }

    public void setPriceAsDouble(double price) {

        sign.setPriceAsDouble(price);
        SudoSigns.config.setPrice(sign.getName(), price);

        if (!SudoSigns.getPlugin().getConfig().getBoolean("config.currency-symbol-in-front")) {

            if (sign.getPriceAsDouble() == 1.0) {

                UtilitiesOG.trueogMessage(p, "&6The price of &e" + sign.getName() + " &6has been set to: &e" + price
                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular") + "&6.");

            } else {

                UtilitiesOG.trueogMessage(p, "&6The price of &e" + sign.getName() + " &6has been set to: &e" + price
                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural") + "&6.");

            }

        } else {

            if (sign.getPriceAsInteger() == 1) {

                UtilitiesOG.trueogMessage(p,
                        "&6The price of &e" + sign.getName() + " &6has been set to: "
                                + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular") + "&e"
                                + price + "&6.");

            } else {

                UtilitiesOG.trueogMessage(p,
                        "&6The price of &e" + sign.getName() + " &6has been set to: "
                                + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural") + "&e"
                                + price + "&6.");

            }

        }

    }

    public void setPriceAsInteger(int price) {

        sign.setPriceAsInteger(price);
        SudoSigns.config.setPrice(sign.getName(), price);

        if (!SudoSigns.getPlugin().getConfig().getBoolean("config.currency-symbol-in-front")) {

            if (sign.getPriceAsInteger() == 1) {

                UtilitiesOG.trueogMessage(p, "&6The price of &e" + sign.getName() + " &6has been set to: &e" + price
                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular") + "&6.");

            } else {

                UtilitiesOG.trueogMessage(p, "&6The price of &e" + sign.getName() + " &6has been set to: &e" + price
                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural") + "&6.");

            }

        } else {

            if (sign.getPriceAsInteger() == 1) {

                UtilitiesOG.trueogMessage(p,
                        "&6The price of &e" + sign.getName() + " &6has been set to: "
                                + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular") + "&e"
                                + price + "&6.");

            } else {

                UtilitiesOG.trueogMessage(p,
                        "&6The price of &e" + sign.getName() + " &6has been set to: "
                                + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural") + "&e"
                                + price + "&6.");

            }

        }

    }

    public void setLineNumber(int lineNumber) {

        this.lineNumber = lineNumber;
        UtilitiesOG.trueogMessage(p,
                "&6Please enter the new text (maximum 15 characters), using the &e& &6symbol for color codes. Type &cCANCEL &6to cancel the operation.");
        SudoSigns.users.get(p.getUniqueId()).addTextInput(PlayerInput.EDIT_TEXT);

    }

    public void setText(TextComponent message) {

        UtilitiesOG.trueogMessage(p, "&aLine &e" + lineNumber + " &ahas been set to: " + message.content());
        sign.editLine(lineNumber - 1, message);
        SudoSigns.config.editSignText(sign.getName(), lineNumber, message);

    }

}