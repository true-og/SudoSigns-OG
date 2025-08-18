package dev.mylesmor.sudosigns.data;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.util.Util;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The class assigned to each created sign.
 * 
 * @author MylesMor
 * @author https://mylesmor.dev
 */
public class SudoSign {

    private ArrayList<SignCommand> playerCommands = new ArrayList<>();
    private ArrayList<SignCommand> consoleCommands = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private ArrayList<SignMessage> messages = new ArrayList<>();
    private ArrayList<String> text = new ArrayList<>();
    private double priceAsDouble = 0;
    private int priceAsInteger = 0;
    private String worldName;
    private double x;
    private double y;
    private double z;
    private String name;

    public SudoSign(String name) {

        this.name = name;

    }

    public void setSign(Sign sign) {

        Location loc = sign.getLocation();

        this.worldName = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();

    }

    public Sign getSign() {

        if (worldName != null) {

            Location loc = new Location(Bukkit.getWorld(worldName), x, y, z);

            BlockState blockState = loc.getBlock().getState();
            if (blockState instanceof Sign || blockState instanceof WallSign) {

                return (Sign) blockState;

            }

        }

        Bukkit.getLogger().warning("Failed to locate sign " + name + "!");

        return null;

    }

    public void setName(String name) {

        this.name = name;

    }

    public void addPlayerCommand(SignCommand sc) {

        playerCommands.add(sc);

    }

    public List<String> getText() {

        return text;

    }

    public void addLines() {

        Sign sign = getSign();

        for (int i = 0; i < 4; i++) {

            String line = PlainTextComponentSerializer.plainText().serialize(sign.line(i));

            text.add(line.replaceAll("§", "&"));
            sign.line(i, Util.legacySerializerAnyCase(line));

        }

        sign.update();

    }

    public void editLine(int lineNumber, TextComponent message) {

        Sign sign = getSign();
        String line = PlainTextComponentSerializer.plainText().serialize(sign.line(lineNumber));

        text.add(line.replaceAll("§", "&"));

        sign.line(lineNumber, message);
        sign.update();

    }

    public void addConsoleCommand(SignCommand sc) {

        consoleCommands.add(sc);

    }

    public void deleteConsoleCommand(SignCommand sc) {

        consoleCommands.remove(sc);

    }

    public void deletePlayerCommand(SignCommand sc) {

        playerCommands.remove(sc);

    }

    public void addPermission(String s) {

        permissions.add(s);

    }

    public void removePermission(String s) {

        permissions.remove(s);

    }

    public ArrayList<String> getPermissions() {

        return permissions;

    }

    public void addMessage(SignMessage s) {

        messages.add(s);

    }

    public void removeMessage(SignMessage s) {

        messages.remove(s);

    }

    public void copyFrom(SudoSign s) {

        this.permissions = s.getPermissions();
        this.playerCommands = s.getPlayerCommands();
        this.consoleCommands = s.getConsoleCommands();
        this.messages = s.getMessages();

        if (Util.priceIsInteger()) {

            this.priceAsInteger = s.getPriceAsInteger();

        } else {

            this.priceAsDouble = s.getPriceAsDouble();

        }

    }

    public ArrayList<SignMessage> getMessages() {

        return messages;

    }

    public void setMessages(ArrayList<SignMessage> messages) {

        this.messages = messages;

    }

    public void setPlayerCommands(ArrayList<SignCommand> playerCommands) {

        this.playerCommands = playerCommands;

    }

    public void setConsoleCommands(ArrayList<SignCommand> consoleCommands) {

        this.consoleCommands = consoleCommands;

    }

    public void setPermissions(ArrayList<String> permissions) {

        this.permissions = permissions;

    }

    public ArrayList<SignCommand> getPlayerCommands() {

        return playerCommands;

    }

    public ArrayList<SignCommand> getConsoleCommands() {

        return consoleCommands;

    }

    public void setPriceAsDouble(double price) {

        this.priceAsDouble = price;

    }

    public double getPriceAsDouble() {

        return priceAsDouble;

    }

    public void setPriceAsInteger(int price) {

        this.priceAsInteger = price;

    }

    public int getPriceAsInteger() {

        return (int) priceAsInteger;

    }

    /**
     * Executes all of the commands attached to the sign, if the player has the
     * required permissions.
     * 
     * @param p The player who is running the sign.
     */
    public void executeCommands(Player p) {

        boolean hasPermission = true;
        for (String perm : permissions) {

            if (!p.hasPermission(perm)) {

                hasPermission = false;

            }

        }

        if (hasPermission) {

            EconomyResponse r = null;
            if (SudoSigns.econ != null) {

                if (Util.priceIsInteger()) {

                    r = SudoSigns.econ.withdrawPlayer(p, priceAsInteger);

                } else {

                    r = SudoSigns.econ.withdrawPlayer(p, priceAsDouble);

                }

                if (!r.transactionSuccess()) {

                    if (!SudoSigns.getPlugin().getConfig().getBoolean("config.currency-symbol-in-front")) {

                        if (Util.priceIsInteger()) {

                            if (priceAsInteger == 1) {

                                Util.sudoSignsMessage(p, "&cERROR: You do not have a"
                                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                                        + " &cto run this sign! &6The cost is: &e" + priceAsInteger
                                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                                        + "&6.");

                            } else {

                                Util.sudoSignsMessage(p, "&cERROR: You do not have enough"
                                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                                        + " &cto run this sign! &6The cost is: &e" + priceAsInteger
                                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                                        + "&6.");

                            }

                        } else {

                            if (priceAsDouble == 1.0) {

                                Util.sudoSignsMessage(p, "&cERROR: You do not have a"
                                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                                        + " &cto run this sign! &6The cost is: &e" + priceAsDouble
                                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                                        + "&6.");

                            } else {

                                Util.sudoSignsMessage(p, "&cERROR: You do not have enough"
                                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                                        + " &cto run this sign! &6The cost is: &e" + priceAsDouble
                                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                                        + "&6.");

                            }

                        }

                        return;

                    } else {

                        if (Util.priceIsInteger()) {

                            if (priceAsInteger == 1) {

                                Util.sudoSignsMessage(p, "&cERROR: You do not have a"
                                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                                        + " &cto run this sign! &6The cost is: "
                                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                                        + "&e" + priceAsInteger + "&6.");

                            } else {

                                Util.sudoSignsMessage(p, "&cERROR: You do not have enough"
                                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                                        + " &cto run this sign! &6The cost is: "
                                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                                        + "&e" + priceAsInteger + "&6.");

                            }

                        } else {

                            if (priceAsDouble == 1.0) {

                                Util.sudoSignsMessage(p, "&cERROR: You do not have a"
                                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                                        + " &cto run this sign! &6The cost is: "
                                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                                        + "&e" + priceAsDouble + "&6.");

                            } else {

                                Util.sudoSignsMessage(p, "&cERROR: You do not have enough"
                                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                                        + " &cto run this sign! &6The cost is: "
                                        + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                                        + "&e" + priceAsDouble + "&6.");

                            }

                        }

                        return;

                    }

                } else {

                    if (!SudoSigns.getPlugin().getConfig().getBoolean("config.currency-symbol-in-front")) {

                        if (Util.priceIsInteger()) {

                            if (priceAsInteger == 1) {

                                Util.sudoSignsMessage(p,
                                        "&e" + priceAsInteger
                                                + SudoSigns.getPlugin().getConfig()
                                                        .getString("config.currency-symbol-singular")
                                                + " &6has been withdrawn from your balance.");

                            } else if (priceAsInteger != 0) {

                                Util.sudoSignsMessage(p,
                                        "&e" + priceAsInteger
                                                + SudoSigns.getPlugin().getConfig()
                                                        .getString("config.currency-symbol-plural")
                                                + "  &6have been withdrawn from your balance.");

                            }

                        } else {

                            if (priceAsDouble == 1.0) {

                                Util.sudoSignsMessage(p,
                                        "&e" + priceAsDouble
                                                + SudoSigns.getPlugin().getConfig()
                                                        .getString("config.currency-symbol-singular")
                                                + " &6has been withdrawn from your balance.");

                            } else if (priceAsDouble != 0.0) {

                                Util.sudoSignsMessage(p,
                                        "&e" + priceAsDouble
                                                + SudoSigns.getPlugin().getConfig()
                                                        .getString("config.currency-symbol-plural")
                                                + "  &6have been withdrawn from your balance.");

                            }

                        }

                    } else {

                        if (Util.priceIsInteger()) {

                            if (priceAsInteger == 1) {

                                Util.sudoSignsMessage(p,
                                        SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                                                + "&e" + priceAsInteger + " &6has been withdrawn from your balance.");

                            } else if (priceAsInteger != 0) {

                                Util.sudoSignsMessage(p,
                                        SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                                                + "&e" + priceAsInteger + "  &6have been withdrawn from your balance.");

                            }

                        } else {

                            if (priceAsDouble == 1.0) {

                                Util.sudoSignsMessage(p,
                                        SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                                                + "&e" + priceAsDouble + " &6has been withdrawn from your balance.");

                            } else if (priceAsDouble != 0.0) {

                                Util.sudoSignsMessage(p,
                                        SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                                                + "&e" + priceAsDouble + " &6have been withdrawn from your balance.");

                            }

                        }

                    }

                }

            }

            for (SignMessage sm : messages) {

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        Util.sudoSignsMessage(p, (sm.getMessage().replaceAll("(?i)%PLAYER%", p.getName())));

                    }

                }.runTaskLater(SudoSigns.sudoSignsPlugin, (long) (sm.getDelay() / 50));

            }

            for (SignCommand sc : playerCommands) {

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        p.performCommand(sc.getCommand());

                    }

                }.runTaskLater(SudoSigns.sudoSignsPlugin, (long) (sc.getDelay() / 50));

            }

            for (SignCommand sc : consoleCommands) {

                String cmd = sc.getCommand().replaceAll("(?i)%PLAYER%", p.getName());

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);

                    }

                }.runTaskLater(SudoSigns.sudoSignsPlugin, (long) (sc.getDelay() / 50));

            }

        } else {

            Util.sudoSignsErrorMessage(p);

        }

    }

    public String getName() {

        return name;

    }

    public int getNextCommandNumber() {

        int number = 0;
        for (SignCommand c : consoleCommands) {

            number = Math.max(c.getNumber(), number);

        }

        for (SignCommand sc : playerCommands) {

            number = Math.max(sc.getNumber(), number);

        }

        return number + 1;

    }

    public int getNextMessageNumber() {

        int number = 0;
        for (SignMessage m : messages) {

            number = Math.max(m.getNumber(), number);

        }

        return number + 1;

    }

    public SignMessage getSignMessageByNumber(int number) {

        for (SignMessage sm : messages) {

            if (sm.getNumber() == number) {

                return sm;

            }

        }

        return null;

    }

    public SignCommand getSignCommandByNumber(int number) {

        for (SignCommand c : consoleCommands) {

            if (c.getNumber() == number) {

                return c;

            }

        }

        for (SignCommand sc : playerCommands) {

            if (sc.getNumber() == number) {

                return sc;

            }

        }

        return null;

    }

}
