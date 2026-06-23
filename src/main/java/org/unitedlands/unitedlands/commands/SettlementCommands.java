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
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementClaimCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementCreateCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementDeleteCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementDepositCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementInfoCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementInviteCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementJoinCountryCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementKickCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementLeaveCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementMapCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementPermissionCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementRankSubcommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementRenameCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementSetBoardCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementSetColorCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementSetSpawnCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementSetTaxesCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementSpawnCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementToggleCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementUnclaimCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementUseTaxPercentCommand;
import org.unitedlands.unitedlands.commands.handlers.settlement.SettlementWithdrawCommand;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Formatter;

public class SettlementCommands extends BaseCommandExecutor<UnitedLands> {

    private SettlementCommandHandler infoCommand;

    public SettlementCommands(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {
        infoCommand = new SettlementInfoCommand(plugin, messageProvider);
        handlers.put("create", new SettlementCreateCommand(plugin, messageProvider));
        handlers.put("permission", new SettlementPermissionCommand(plugin, messageProvider));
        handlers.put("toggle", new SettlementToggleCommand(plugin, messageProvider));
        handlers.put("info", infoCommand);
        handlers.put("rename", new SettlementRenameCommand(plugin, messageProvider));
        handlers.put("spawn", new SettlementSpawnCommand(plugin, messageProvider));
        handlers.put("claim", new SettlementClaimCommand(plugin, messageProvider));
        handlers.put("unclaim", new SettlementUnclaimCommand(plugin, messageProvider));
        handlers.put("invite", new SettlementInviteCommand(plugin, messageProvider));
        handlers.put("leave", new SettlementLeaveCommand(plugin, messageProvider));
        handlers.put("delete", new SettlementDeleteCommand(plugin, messageProvider));
        handlers.put("setboard", new SettlementSetBoardCommand(plugin, messageProvider));
        handlers.put("rank", new SettlementRankSubcommand(plugin, messageProvider));
        handlers.put("kick", new SettlementKickCommand(plugin, messageProvider));
        handlers.put("setspawn", new SettlementSetSpawnCommand(plugin, messageProvider));
        handlers.put("setcolor", new SettlementSetColorCommand(plugin, messageProvider));
        handlers.put("map", new SettlementMapCommand(plugin, messageProvider));
        handlers.put("deposit", new SettlementDepositCommand(plugin, messageProvider));
        handlers.put("withdraw", new SettlementWithdrawCommand(plugin, messageProvider));
        handlers.put("joincountry", new SettlementJoinCountryCommand(plugin, messageProvider));
        handlers.put("settaxes", new SettlementSetTaxesCommand(plugin, messageProvider));
        handlers.put("usetaxpercent", new SettlementUseTaxPercentCommand(plugin, messageProvider));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, String alias,
            String[] args) {
        List<String> options = null;
        String input = args[args.length - 1];
        if (args.length == 1) {
            options = new ArrayList<String>(this.handlers.keySet());
            if (args[0].length() >= 3)
                options.addAll(GlobalDataManager.instance().getSettlementNames());
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
