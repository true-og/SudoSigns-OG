package dev.mylesmor.sudosigns.commands;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import java.util.Map;
import org.bukkit.entity.Player;

public class List {

    /**
     * Lists all signs.
     * @param p The player running the command.
     * @param args Not required
     */
    public static void list(Player p, String[] args) {

        if (p.hasPermission(Permissions.LIST)) {

            if (SudoSigns.signs.size() == 0) {

                Util.sudoSignsMessage(p, "&cERROR: No SudoSigns were found!");
                return;
            }

            Util.sudoSignsMessage(p, "&6Listing all SudoSigns...");
            for (Map.Entry<String, SudoSign> entry : SudoSigns.signs.entrySet()) {

                Util.sendSelectMenus(p, entry.getKey());
            }

        } else {

            Util.sudoSignsErrorMessage(p);
        }
    }
}
