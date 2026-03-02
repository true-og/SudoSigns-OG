package dev.mylesmor.sudosigns.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import net.trueog.utilitiesog.UtilitiesOG;

public class View {

    /**
     * Views a sign's details, including location and number of permissions and
     * commands assigned to it.
     * 
     * @param p    The player running the command.
     * @param args 0 or 1 arguments: The name of the sign (or null for click
     *             selection).
     */
    public static void view(Player p, String[] args) {

        if (p.hasPermission(Permissions.VIEW)) {

            String name = null;
            if (args == null) {

                UtilitiesOG.trueogMessage(p, "&6Please click on the sign you would like to view.");
                SudoSigns.users.get(p.getUniqueId()).setView(true);
                return;

            }

            if (args.length > 1) {

                UtilitiesOG.trueogMessage(p, "&cERROR: Invalid syntax! &6Correct syntax: &d/ss view [name]&6.");
                return;

            }

            name = args[0];
            final SudoSign sign = SudoSigns.signs.get(name);
            if (sign != null) {

                final Location signLoc = sign.getSign().getLocation();
                final String locString = "x=" + signLoc.getX() + " y=" + signLoc.getY() + " z=" + signLoc.getZ() + "  ";
                UtilitiesOG.trueogMessage(p, "&6Displaying details for sign &e" + name + "&6:");
                if (p.hasPermission(Permissions.TP)) {

                    // Send a teleport confirmation menu with a clickable button.
                    Util.selectMenuParser(p, "&d&l[TP]", ("&6Location: &e" + "&a" + locString), p.getName(),
                            ("/ss tp " + name), ("&dTeleport to: &e" + name));

                } else {

                    UtilitiesOG.trueogMessage(p, "&6Location: &e" + locString);

                }

                if (!SudoSigns.getPlugin().getConfig().getBoolean("config.currency-symbol-in-front")) {

                    if (Util.priceIsInteger()) {

                        if (sign.getPriceAsInteger() == 1) {

                            UtilitiesOG.trueogMessage(p, "&6Price: &e" + sign.getPriceAsInteger()
                                    + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                                    + "&6.");

                        } else {

                            UtilitiesOG.trueogMessage(p, "&6Price: &e" + sign.getPriceAsInteger()
                                    + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                                    + "&6.");

                        }

                    } else {

                        if (sign.getPriceAsDouble() == 1.0) {

                            UtilitiesOG.trueogMessage(p, "&6Price: &e" + sign.getPriceAsDouble()
                                    + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                                    + "&6.");

                        } else {

                            UtilitiesOG.trueogMessage(p, "&6Price: &e" + sign.getPriceAsDouble()
                                    + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                                    + "&6.");

                        }

                    }

                } else {

                    if (Util.priceIsInteger()) {

                        if (sign.getPriceAsInteger() == 1) {

                            UtilitiesOG.trueogMessage(p,
                                    "&6Price: "
                                            + SudoSigns.getPlugin().getConfig()
                                                    .getString("config.currency-symbol-singular")
                                            + "&e" + sign.getPriceAsInteger() + "&6.");

                        } else {

                            UtilitiesOG.trueogMessage(p,
                                    "&6Price: "
                                            + SudoSigns.getPlugin().getConfig()
                                                    .getString("config.currency-symbol-plural")
                                            + "&e" + sign.getPriceAsInteger() + "&6.");

                        }

                    } else {

                        if (sign.getPriceAsDouble() == 1.0) {

                            UtilitiesOG.trueogMessage(p,
                                    "&6Price: "
                                            + SudoSigns.getPlugin().getConfig()
                                                    .getString("config.currency-symbol-singular")
                                            + "&e" + sign.getPriceAsDouble() + "&6.");

                        } else {

                            UtilitiesOG.trueogMessage(p,
                                    "&6Price: "
                                            + SudoSigns.getPlugin().getConfig()
                                                    .getString("config.currency-symbol-plural")
                                            + "&e" + sign.getPriceAsDouble() + "&6.");

                        }

                    }

                }

                UtilitiesOG.trueogMessage(p, "&bPrice: " + sign.getMessages().size());
                UtilitiesOG.trueogMessage(p, "&6Messages: &e" + sign.getMessages().size());
                UtilitiesOG.trueogMessage(p, "&6Permissions: &e" + sign.getPermissions().size());
                UtilitiesOG.trueogMessage(p, "&6Player Commands: &e" + sign.getPlayerCommands().size());
                UtilitiesOG.trueogMessage(p, "&6Console Commands: &e" + sign.getConsoleCommands().size());

            } else {

                UtilitiesOG.trueogMessage(p, "&cERROR: A sign with the name &e" + name + " &cdoes not exist!");

            }

        } else {

            Util.sudoSignsPermissionsError(p);

        }

    }

}