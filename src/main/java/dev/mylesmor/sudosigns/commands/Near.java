package dev.mylesmor.sudosigns.commands;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Near {

    /**
     * Displays signs within a set radius.
     * @param p The player running the command.
     * @param args 0 or 1 arguments, with the only argument being the radius to search in (default is 5 blocks).
     */
    public static void near(Player p, String[] args) {
        if (p.hasPermission(Permissions.NEAR)) {
            int r;
            String radius = null;
            if (args != null) {
                if (args.length > 1) {
                    Util.sudoSignsMessage(
                            p, "&cERROR: Invalid syntax! " + "&6Correct syntax: " + "&d/ss near [radius]" + "&6.");
                    return;
                } else if (args.length == 1) {
                    radius = args[0];
                }
            }
            if (radius == null) {
                r = 5;
            } else {
                try {
                    r = Integer.parseInt(radius);
                } catch (NumberFormatException e) {
                    Util.sudoSignsMessage(
                            p, "&cERROR: &e" + radius + " &cis not a valid number! &6Reverting to a radius of &e5&6.");
                    r = 5;
                }
            }
            boolean found = false;
            for (Map.Entry<String, SudoSign> entry : SudoSigns.signs.entrySet()) {
                Location signLoc = entry.getValue().getSign().getLocation();
                try {
                    if (signLoc.distance(p.getLocation()) <= r) {
                        if (!found)
                            Util.sudoSignsMessage(
                                    p,
                                    "&6Displaying SudoSigns within a radius of &e%NAME% &6block(s):&e",
                                    Integer.toString(r));
                        found = true;
                        Util.sendSelectMenus(p, entry.getKey());
                    }
                } catch (Exception ignored) {
                }
            }
            if (!found) {
                Util.sudoSignsMessage(
                        p, "&6No SudoSigns found within a radius of &e%NAME% &6block(s).&e", Integer.toString(r));
            }
        } else {
            Util.sudoSignsMessage(p, "&cYou do not have permission to do that!");
        }
    }
}
