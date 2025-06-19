package dev.mylesmor.sudosigns.commands;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.util.Permissions;
import dev.mylesmor.sudosigns.util.Util;
import org.bukkit.entity.Player;

public class Run {
    /**
     * Runs a sign remotely.
     * @param p The player running the command.
     * @param args 0 or 1 arguments: The name of the sign (or null for click selection).
     */
    public static void run(Player p, String[] args) {

        if (p.hasPermission(Permissions.RUN)) {

            String name;
            if (args == null) {

                Util.sudoSignsMessage(p, "&6Please click on the sign you would like to run.");
                SudoSigns.users.get(p.getUniqueId()).setRun(true);
                return;
            }

            if (args.length > 1) {

                Util.sudoSignsMessage(p, "&cERROR: Invalid syntax! &6Correct syntax: &d/ss run [name]&6.");
                return;
            }

            name = args[0];
            if (SudoSigns.signs.containsKey(name)) {

                SudoSign sign = SudoSigns.signs.get(name);

                Util.sudoSignsMessage(
                        p,
                        "&6Executing &e" + sign.getMessages().size() + "&6 message(s) and &e"
                                + (sign.getPlayerCommands().size()
                                        + sign.getConsoleCommands().size()) + "&6 command(s)...");
                SudoSigns.signs.get(name).executeCommands(p);

            } else {

                Util.sudoSignsMessage(p, "&cERROR: A sign with the name &e%NAME% &cdoes not exist!", name);
            }

        } else {

            Util.sudoSignsErrorMessage(p);
        }
    }
}
