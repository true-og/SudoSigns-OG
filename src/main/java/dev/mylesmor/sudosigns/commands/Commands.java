package dev.mylesmor.sudosigns.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.SudoUser;
import net.trueog.diamondbankog.api.DiamondBankAPIJava;
import net.trueog.utilitiesog.UtilitiesOG;

public class Commands implements CommandExecutor {

    DiamondBankAPIJava diamondBankAPI;
    Map<String, BiConsumer<Player, String[]>> commands = new HashMap<>();

    public Commands(DiamondBankAPIJava diamondBankAPI) {

        this.diamondBankAPI = diamondBankAPI;

        commands.put("help", Help::help);
        commands.put("near", Near::near);
        commands.put("list", List::list);
        commands.put("delete", Delete::delete);
        commands.put("edit", (p, args) -> Edit.edit(p, args, diamondBankAPI));
        commands.put("view", View::view);
        commands.put("run", Run::run);
        commands.put("confirmdelete", Delete::confirmDelete);
        commands.put("create", (p, args) -> Create.create(p, args, diamondBankAPI));
        commands.put("tp", Teleport::tp);
        commands.put("copy", (p, args) -> Copy.copy(p, args, diamondBankAPI));
        commands.put("select", Select::select);
        commands.put("purge", Purge::purge);
        commands.put("confirmpurge", Purge::confirmPurge);
        commands.put("fix", Fix::fix);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player p) {

            SudoSigns.users.computeIfAbsent(p.getUniqueId(), k -> new SudoUser(p));
            try {

                final BiConsumer<Player, String[]> command = commands.get(StringUtils.lowerCase(args[0]));

                if (command != null) {

                    if (args.length > 1) {

                        final String[] newArgs = Arrays.copyOfRange(args, 1, args.length);

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

            } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {

                invalidCommandError(p);

            }

        }

        return true;

    }

    private void invalidCommandError(Player p) {

        UtilitiesOG.trueogMessage(p, "&cERROR: Invalid command! &6Type &d/ss help &6for a list of valid commands.");

    }

}