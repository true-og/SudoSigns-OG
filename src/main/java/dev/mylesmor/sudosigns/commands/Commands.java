package dev.mylesmor.sudosigns.commands;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.SudoUser;
import dev.mylesmor.sudosigns.util.Util;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    Map<String, BiConsumer<Player, String[]>> commands = new HashMap<>();

    public Commands() {

        commands.put("help", Help::help);
        commands.put("near", Near::near);
        commands.put("list", List::list);
        commands.put("delete", Delete::delete);
        commands.put("edit", Edit::edit);
        commands.put("view", View::view);
        commands.put("run", Run::run);
        commands.put("confirmdelete", Delete::confirmDelete);
        commands.put("reload", Reload::reload);
        commands.put("create", Create::create);
        commands.put("tp", Teleport::tp);
        commands.put("copy", Copy::copy);
        commands.put("select", Select::select);
        commands.put("purge", Purge::purge);
        commands.put("confirmpurge", Purge::confirmPurge);
        commands.put("fix", Fix::fix);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {

            final Player p = (Player) sender;
            SudoSigns.users.computeIfAbsent(p.getUniqueId(), k -> new SudoUser(p));
            try {

                BiConsumer<Player, String[]> command = commands.get(args[0].toLowerCase());

                if (command != null) {

                    if (args.length > 1) {

                        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);

                        command.accept(p, newArgs);

                    } else {

                        try {

                            command.accept(p, null);

                        } catch (CommandException error) {

                            invalidCommandError(p);

                        }

                    }

                } else {

                    invalidCommandError(p);

                }

            } catch (ArrayIndexOutOfBoundsException e) {

                invalidCommandError(p);

            }

        }

        return true;

    }

    private void invalidCommandError(Player p) {

        Util.sudoSignsMessage(p, "&cERROR: Invalid command! &6Type &d/ss help &6for a list of valid commands.");

    }

}
