package org.unitedlands.unitedlands.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.managers.ConfirmationManager;
import org.unitedlands.utils.Messenger;

public class ApprovalCommand implements CommandExecutor, TabCompleter {

    @SuppressWarnings("unused")
    private final UnitedLands plugin;
    private final IMessageProvider messageProvider;

    public ApprovalCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        this.plugin = plugin;
        this.messageProvider = messageProvider;
    }

    List<String> completes = List.of("settlement");

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
            @NotNull String alias, @NotNull String @NotNull [] args) {

        if (args.length == 1) {
            var pendingConfirmations = ConfirmationManager.instance().getReceiverConfirmations((Player) sender);
            return pendingConfirmations.stream().map(Confirmation::getKey).collect(Collectors.toList());
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
            @NotNull String @NotNull [] args) {

        var player = (Player) sender;
        var pendingConfirmations = ConfirmationManager.instance().getReceiverConfirmations(args[0], player);

        if (pendingConfirmations.size() == 0) {
            Messenger.sendMessage(player, messageProvider.get("approval.no-approval-pending"), null,
                    messageProvider.get("prefix"));
            return false;
        }

        if (pendingConfirmations.size() > 1 && args.length == 1) {
            Messenger.sendMessage(player, messageProvider.get("approval.multiple-approvals"), null,
                    messageProvider.get("prefix"));
            return false;
        }

        ConfirmationManager.instance().executeConfirmation(pendingConfirmations.get(0));

        return true;
    }

}
