package org.unitedlands.unitedlands.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.classes.BaseCommandExecutor;
import org.unitedlands.interfaces.ICommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.commands.handlers.citizen.CitizenInfoCommand;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Formatter;

public class CitizenCommands extends BaseCommandExecutor<UnitedLands> {

    private CitizenInfoCommand infoCommand;

    public CitizenCommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {
        infoCommand = new CitizenInfoCommand(plugin, messageProvider);
        handlers.put("info", infoCommand);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, String alias,
            String[] args) {
        List<String> options = null;
        String input = args[args.length - 1];
        if (args.length == 1) {
            options = new ArrayList<String>(this.handlers.keySet());
            if (args[0].length() >= 3)
                options.addAll(GlobalDataManager.instance().getCitizenNames());
        } else {
            String subcommand = args[0].toLowerCase();
            ICommandHandler handler = (ICommandHandler) this.handlers.get(subcommand);
            if (handler != null) {
                options = handler.handleTab(sender, (String[]) Arrays.copyOfRange(args, 1, args.length));
            } else {
                options = infoCommand.handleTab(sender, args);
            }
        }
        return Formatter.getSortedCompletions(input, options);
    }

    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label,
            String @NotNull [] args) {
        if (args.length == 0) {
            infoCommand.handleCommand(sender, args);
            return true;
        } else {
            String subcommand = args[0].toLowerCase();
            ICommandHandler handler = (ICommandHandler) this.handlers.get(subcommand);
            if (handler == null) {
                infoCommand.handleCommand(sender, args);
                return true;
            } else {
                handler.handleCommand(sender, (String[]) Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }
    }

}
