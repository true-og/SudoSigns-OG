package dev.mylesmor.sudosigns.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.util.Util;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.trueog.diamondbankog.DiamondBankException.EconomyDisabledException;
import net.trueog.diamondbankog.DiamondBankException.InsufficientFundsException;
import net.trueog.diamondbankog.DiamondBankException.InvalidPlayerException;
import net.trueog.diamondbankog.DiamondBankException.PlayerNotOnlineException;
import net.trueog.diamondbankog.api.DiamondBankAPIJava;
import net.trueog.utilitiesog.UtilitiesOG;

/**
 * The class assigned to each created sign.
 * 
 * @author MylesMor
 * @author https://mylesmor.dev
 * @maintainer NotAlexxNoyle
 * @maintainer https://true-og.net
 */
public class SudoSign {

    private ArrayList<SignCommand> playerCommands = new ArrayList<>();
    private ArrayList<SignCommand> consoleCommands = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private ArrayList<SignMessage> messages = new ArrayList<>();
    private final ArrayList<String> text = new ArrayList<>();
    private double priceAsDouble = 0;
    private int priceAsInteger = 0;
    private String worldName;
    private double x;
    private double y;
    private double z;
    private String name;
    private final DiamondBankAPIJava diamondBankAPI;

    public SudoSign(String name, DiamondBankAPIJava diamondBankAPI) {

        this.name = name;
        this.diamondBankAPI = diamondBankAPI;

    }

    public void setSign(Sign sign) {

        final Location loc = sign.getLocation();

        this.worldName = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();

    }

    public Sign getSign() {

        if (worldName != null) {

            final Location loc = new Location(Bukkit.getWorld(worldName), x, y, z);
            return Util.getSign(loc.getBlock());

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

        final Sign sign = getSign();

        for (int i = 0; i < 4; i++) {

            final String line = PlainTextComponentSerializer.plainText().serialize(sign.line(i));

            text.add(line.replaceAll("§", "&"));
            sign.line(i, UtilitiesOG.trueogColorize(line));

        }

        sign.update();

    }

    public void editLine(int lineNumber, TextComponent message) {

        final Sign sign = getSign();
        final String line = PlainTextComponentSerializer.plainText().serialize(sign.line(lineNumber));

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

        return priceAsInteger;

    }

    /**
     * Executes all of the commands attached to the sign, if the player has the
     * required permissions.
     * 
     * @param p The player who is running the sign.
     */
    public void executeCommands(Player p) {

        final boolean hasPermission = permissions.stream().allMatch(p::hasPermission);

        if (!hasPermission) {

            Util.sudoSignsPermissionsError(p);

            return;

        }

        final boolean priceIsInteger = Util.priceIsInteger();
        final double priceAsDiamonds = priceIsInteger ? (double) priceAsInteger : priceAsDouble;
        final boolean paidSign = priceIsInteger ? priceAsInteger != 0 : priceAsDouble != 0.0D;
        if (paidSign) {

            if (diamondBankAPI == null) {

                UtilitiesOG.trueogMessage(p,
                        "&cERROR: The Diamond economy is currently unavailable. You cannot purchase this sign right now.");

                return;

            }

            final long priceAsShards;
            try {

                priceAsShards = diamondBankAPI.diamondsToShards(priceAsDiamonds);

            } catch (RuntimeException conversionError) {

                if (!priceIsInteger) {

                    final BigDecimal entered = BigDecimal.valueOf(priceAsDouble).stripTrailingZeros();
                    final String enteredText = entered.toPlainString();

                    String suggestionText = null;
                    if (entered.scale() > 1) {

                        suggestionText = entered.setScale(1, RoundingMode.DOWN).stripTrailingZeros().toPlainString();

                    }

                    if (suggestionText != null && !suggestionText.equals(enteredText)) {

                        UtilitiesOG.trueogMessage(p,
                                "&cERROR: Invalid price number: &e" + enteredText
                                        + "&c. Use at most one decimal place (for example, enter &e" + suggestionText
                                        + "&c instead of &e" + enteredText + "&c).");

                    } else {

                        UtilitiesOG.trueogMessage(p, "&cERROR: Invalid price number: &e" + enteredText
                                + "&c. Use at most one decimal place (for example, &e1.1&c instead of &e1.11&c).");

                    }

                } else {

                    UtilitiesOG.trueogMessage(p, "&cERROR: Invalid price number configured on this sign.");

                }

                SudoSigns.getPlugin().getLogger()
                        .warning("Invalid DiamondBank-OG price precision while executing sign for " + p.getName()
                                + " (price=" + priceAsDiamonds + ").");
                conversionError.printStackTrace();

                return;

            }

            try {

                if (priceAsShards > 0L) {

                    diamondBankAPI.consumeFromPlayer(p.getUniqueId(), priceAsShards, "Player " + p.getName() + " paid "
                            + diamondBankAPI.shardsToDiamonds(priceAsShards) + " Diamonds to execute a SudoSign.",
                            "Plugin: SudoSigns-OG");

                }

            } catch (InsufficientFundsException insufficientFundsError) {

                sendNotEnoughMoneyMessage(p, priceIsInteger);

                return;

            } catch (EconomyDisabledException economyDisabledError) {

                SudoSigns.getPlugin().getLogger().severe("DiamondBank-OG economy disabled while executing sign for "
                        + p.getName() + ": " + economyDisabledError.getMessage());
                economyDisabledError.printStackTrace();

                UtilitiesOG.trueogMessage(p,
                        "&cERROR: The Diamond economy is currently disabled. You cannot purchase this sign right now.");

                return;

            } catch (InvalidPlayerException invalidPlayerError) {

                SudoSigns.getPlugin().getLogger()
                        .warning("Invalid player in DiamondBank-OG while executing sign for " + p.getName() + ".");
                invalidPlayerError.printStackTrace();

                UtilitiesOG.trueogMessage(p,
                        "&cERROR: Your player account could not be found by DiamondBank-OG. Contact an administrator.");
                return;

            } catch (PlayerNotOnlineException temporaryPlayerNotOnlineError) {

                // TODO: Remove after DiamondBank-OG update if this is no longer thrown.
                SudoSigns.getPlugin().getLogger()
                        .warning("DiamondBank-OG reported PlayerNotOnlineException while charging " + p.getName()
                                + " for sign execution.");
                temporaryPlayerNotOnlineError.printStackTrace();

                UtilitiesOG.trueogMessage(p, "&cERROR: Could not charge your account right now. Please try again.");

                return;

            }

            sendWithdrawnMessage(p, priceIsInteger);

        }

        messages.forEach(sm -> new BukkitRunnable() {

            @Override
            public void run() {

                UtilitiesOG.trueogMessage(p, (sm.getMessage().replaceAll("(?i)%PLAYER%", p.getName())));

            }

        }.runTaskLater(SudoSigns.sudoSignsPlugin, (long) (sm.getDelay() / 50)));

        playerCommands.forEach(sc -> new BukkitRunnable() {

            @Override
            public void run() {

                p.performCommand(sc.getCommand());

            }

        }.runTaskLater(SudoSigns.sudoSignsPlugin, (long) (sc.getDelay() / 50)));

        consoleCommands.forEach(sc -> {

            final String cmd = sc.getCommand().replaceAll("(?i)%PLAYER%", p.getName());

            new BukkitRunnable() {

                @Override
                public void run() {

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);

                }

            }.runTaskLater(SudoSigns.sudoSignsPlugin, (long) (sc.getDelay() / 50));

        });

    }

    private void sendNotEnoughMoneyMessage(Player p, boolean priceIsInteger) {

        if (!SudoSigns.getPlugin().getConfig().getBoolean("config.currency-symbol-in-front")) {

            if (priceIsInteger) {

                if (priceAsInteger == 1) {

                    UtilitiesOG.trueogMessage(p, "&cERROR: You do not have a"
                            + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                            + " &cto run this sign! &6The cost is: &e" + priceAsInteger
                            + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular") + "&6.");

                } else {

                    UtilitiesOG.trueogMessage(p, "&cERROR: You do not have enough"
                            + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                            + " &cto run this sign! &6The cost is: &e" + priceAsInteger
                            + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural") + "&6.");

                }

            } else {

                if (priceAsDouble == 1.0) {

                    UtilitiesOG.trueogMessage(p, "&cERROR: You do not have a"
                            + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                            + " &cto run this sign! &6The cost is: &e" + priceAsDouble
                            + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular") + "&6.");

                } else {

                    UtilitiesOG.trueogMessage(p, "&cERROR: You do not have enough"
                            + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                            + " &cto run this sign! &6The cost is: &e" + priceAsDouble
                            + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural") + "&6.");

                }

            }

            return;

        }

        if (priceIsInteger) {

            if (priceAsInteger == 1) {

                UtilitiesOG.trueogMessage(p,
                        "&cERROR: You do not have a"
                                + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                                + " &cto run this sign! &6The cost is: "
                                + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular") + "&e"
                                + priceAsInteger + "&6.");

            } else {

                UtilitiesOG.trueogMessage(p,
                        "&cERROR: You do not have enough"
                                + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                                + " &cto run this sign! &6The cost is: "
                                + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural") + "&e"
                                + priceAsInteger + "&6.");

            }

        } else {

            if (priceAsDouble == 1.0) {

                UtilitiesOG.trueogMessage(p,
                        "&cERROR: You do not have a"
                                + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                                + " &cto run this sign! &6The cost is: "
                                + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular") + "&e"
                                + priceAsDouble + "&6.");

            } else {

                UtilitiesOG.trueogMessage(p,
                        "&cERROR: You do not have enough"
                                + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                                + " &cto run this sign! &6The cost is: "
                                + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural") + "&e"
                                + priceAsDouble + "&6.");

            }

        }

    }

    private void sendWithdrawnMessage(Player p, boolean priceIsInteger) {

        if (!SudoSigns.getPlugin().getConfig().getBoolean("config.currency-symbol-in-front")) {

            if (priceIsInteger) {

                if (priceAsInteger == 1) {

                    UtilitiesOG.trueogMessage(p,
                            "&e" + priceAsInteger
                                    + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                                    + " &6has been withdrawn from your balance.");

                } else if (priceAsInteger != 0) {

                    UtilitiesOG.trueogMessage(p,
                            "&e" + priceAsInteger
                                    + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                                    + "  &6have been withdrawn from your balance.");

                }

            } else {

                if (priceAsDouble == 1.0) {

                    UtilitiesOG.trueogMessage(p,
                            "&e" + priceAsDouble
                                    + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular")
                                    + " &6has been withdrawn from your balance.");

                } else if (priceAsDouble != 0.0) {

                    UtilitiesOG.trueogMessage(p,
                            "&e" + priceAsDouble
                                    + SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural")
                                    + "  &6have been withdrawn from your balance.");

                }

            }

            return;

        }

        if (priceIsInteger) {

            if (priceAsInteger == 1) {

                UtilitiesOG.trueogMessage(p,
                        SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular") + "&e"
                                + priceAsInteger + " &6has been withdrawn from your balance.");

            } else if (priceAsInteger != 0) {

                UtilitiesOG.trueogMessage(p,
                        SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural") + "&e"
                                + priceAsInteger + "  &6have been withdrawn from your balance.");

            }

        } else {

            if (priceAsDouble == 1.0) {

                UtilitiesOG.trueogMessage(p,
                        SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-singular") + "&e"
                                + priceAsDouble + " &6has been withdrawn from your balance.");

            } else if (priceAsDouble != 0.0) {

                UtilitiesOG.trueogMessage(p,
                        SudoSigns.getPlugin().getConfig().getString("config.currency-symbol-plural") + "&e"
                                + priceAsDouble + " &6have been withdrawn from your balance.");

            }

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
