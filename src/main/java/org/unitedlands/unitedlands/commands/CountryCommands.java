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
import org.unitedlands.unitedlands.commands.handlers.country.CountryClaimCommand;
import org.unitedlands.unitedlands.commands.handlers.country.CountryCreateCommand;
import org.unitedlands.unitedlands.commands.handlers.country.CountryDeleteCommand;
import org.unitedlands.unitedlands.commands.handlers.country.CountryDepositCommand;
import org.unitedlands.unitedlands.commands.handlers.country.CountryInfoCommand;
import org.unitedlands.unitedlands.commands.handlers.country.CountryRankSubcommands;
import org.unitedlands.unitedlands.commands.handlers.country.CountrySetColorCommand;
import org.unitedlands.unitedlands.commands.handlers.country.CountrySetNameCommand;
import org.unitedlands.unitedlands.commands.handlers.country.CountryWithdrawCommand;
import org.unitedlands.unitedlands.commands.handlers.country.diplomacy.CountryDiplomacySubcommands;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Formatter;

public class CountryCommands extends BaseCommandExecutor<UnitedLands> {

    private CountryInfoCommand infoCommand;

    public CountryCommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {
        infoCommand = new CountryInfoCommand(plugin, messageProvider);
        handlers.put("deposit", new CountryDepositCommand(plugin, messageProvider));
        handlers.put("withdraw", new CountryWithdrawCommand(plugin, messageProvider));
        handlers.put("create", new CountryCreateCommand(plugin, messageProvider));
        handlers.put("claim", new CountryClaimCommand(plugin, messageProvider));
        handlers.put("setcolor", new CountrySetColorCommand(plugin, messageProvider));
        handlers.put("setname", new CountrySetNameCommand(plugin, messageProvider));
        handlers.put("delete", new CountryDeleteCommand(plugin, messageProvider));
        handlers.put("info", infoCommand);
        handlers.put("rank", new CountryRankSubcommands(plugin, messageProvider));
        handlers.put("diplomacy", new CountryDiplomacySubcommands(plugin, messageProvider));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, String alias,
            String[] args) {
        List<String> options = null;
        String input = args[args.length - 1];
        if (args.length == 1) {
            options = new ArrayList<String>(this.handlers.keySet());
            if (args[0].length() >= 3)
                options.addAll(UnitedLandsDataManager.instance().getCountryNames());
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
