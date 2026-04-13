package dev.mylesmor.sudosigns.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.SudoSign;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.trueog.utilitiesog.UtilitiesOG;

public class Util {

    // Create a colored chat prefix.
    public static String chatPrefix = SudoSigns.getPlugin().getConfig().getString("config.prefix");

    public static ArrayList<TextComponent> convertToTextComponents(List<String> stringList) {

        final ArrayList<TextComponent> textComponents = new ArrayList<>(18);
        stringList.stream().map(UtilitiesOG::trueogColorize)
                .forEach(textComponent -> textComponents.add(textComponent));

        return textComponents;

    }

    // Send the sign selection menu according to permissions.
    public static void sendSelectMenus(Player p, String name) {

        selectMenuParser(p, chatPrefix, "&e-----", name, "", "&2True&4OG&a's Fork of &BSudoSigns&a.");

        if (p.hasPermission(Permissions.VIEW)) {

            selectMenuParser(p, "&9&l[V]", "&9View Details", name, ("/ss view " + name),
                    ("&9View &6" + name + "\'s &9details."));

        }

        if (p.hasPermission(Permissions.RUN)) {

            selectMenuParser(p, "&2&l[R]", "&2Run Commands", name, ("/ss run " + name),
                    ("&2Run &6" + name + "\'s &2commands."));

        }

        if (p.hasPermission(Permissions.TP)) {

            selectMenuParser(p, "&3&l[TP]", "&3Teleport To", name, ("/ss tp " + name),
                    ("&3Teleport to &6" + name + "&3."));

        }

        if (p.hasPermission(Permissions.EDIT)) {

            selectMenuParser(p, "&d&l[EDIT]", "&dEdit Details", name, ("/ss edit " + name),
                    ("&dEdit &6" + name + "\'s &dcolor."));

        }

        if (p.hasPermission(Permissions.DELETE)) {

            selectMenuParser(p, "&c&l[DEL]", "&cDelete SudoSign", name, ("/ss confirmdelete " + name),
                    ("&cDelete &6" + name + "&c."));

        }

    }

    // Sends customizable clickable message dialogues to the player.
    public static void selectMenuParser(Player p, String prefix, String prompt, String name, String command,
            String hoverMessage)
    {

        // The clickable part of the confirmation message.
        TextComponent button = UtilitiesOG.trueogColorize(prefix);

        // Add a hover dialogue to the clickable confirmation text.
        button.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, UtilitiesOG.trueogColorize(hoverMessage)));

        // Make sure that a command is intended to be run.
        if (!"".equals(command)) {

            // Create a click event on the delete confirmation chat text.
            button = button.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, (command)));

        }

        // Send the prompt and clickable confirmation button to the player.
        sudoSignsPrompt(p, (prompt + "     "), button);

    }

    // Sends a permissions error message to the player.
    public static void sudoSignsPermissionsError(Player p) {

        UtilitiesOG.trueogMessage(p, "&c&lERROR: &cYou do not have permission to do that!");

    }

    // Sends a formatted message without a prefix to the player.
    public static void sudoSignsMessageNoPrefix(Player p, String message) {

        UtilitiesOG.trueogMessage(p, message);

    }

    // Sends a formatted prompt and clickable chat button to the player.
    public static void sudoSignsPrompt(Player player, String prompt, TextComponent button) {

        player.sendMessage(UtilitiesOG.trueogColorize(prompt).append(button));

    }

    public static boolean checkName(String name) {

        final String regex = "^[A-z0-9]+$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(name);

        return matcher.matches();

    }

    public static String findSign(Sign s) {

        String found = null;
        for (Map.Entry<String, SudoSign> entry : SudoSigns.signs.entrySet()) {

            if (sameBlockLocation(entry.getValue(), s.getLocation())) {

                found = entry.getKey();

            }

        }

        return found;

    }

    public static boolean sameBlockLocation(Location first, Location second) {

        if (first == null || second == null || first.getWorld() == null || second.getWorld() == null) {

            return false;

        }

        return first.getWorld().getName().equals(second.getWorld().getName()) && first.getBlockX() == second.getBlockX()
                && first.getBlockY() == second.getBlockY() && first.getBlockZ() == second.getBlockZ();

    }

    public static boolean sameBlockLocation(SudoSign sign, Location location) {

        if (sign == null || sign.getWorldName() == null || location == null || location.getWorld() == null) {

            return false;

        }

        return sign.getWorldName().equals(location.getWorld().getName()) && sign.getBlockX() == location.getBlockX()
                && sign.getBlockY() == location.getBlockY() && sign.getBlockZ() == location.getBlockZ();

    }

    public static boolean isSignState(BlockState blockState) {

        return blockState instanceof Sign;

    }

    private static boolean isSignMaterial(Material material) {

        return material != null && material.name().endsWith("SIGN");

    }

    private static void ensureChunkLoaded(Block block) {

        final Chunk chunk = block.getChunk();
        if (!chunk.isLoaded()) {

            chunk.load();

        }

    }

    public static boolean isSignBlock(Block block) {

        ensureChunkLoaded(block);

        if (isSignMaterial(block.getType())) {

            return true;

        }

        return isSignState(block.getState());

    }

    public static Sign getSign(Block block) {

        ensureChunkLoaded(block);

        final BlockState blockState = block.getState();
        if (isSignState(blockState)) {

            return (Sign) blockState;

        }

        return null;

    }

    public static boolean priceIsInteger() {

        return SudoSigns.getPlugin().getConfig().getBoolean("config.use-decimals-in-prices");

    }

}
