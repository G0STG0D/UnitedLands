package org.unitedlands.unitedlands.commands.handlers.channel;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.ChatChannel;
import org.unitedlands.unitedlands.managers.ChatChannelManager;

public class ChannelJoinCommand extends BaseCommandHandler<UnitedLands> {

    public ChannelJoinCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(ChatChannel.values()).map(Enum::toString).toList();
        }
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        
        if (args.length < 1)
            return;

        ChatChannelManager.instance().switchChannel((Player) sender, ChatChannel.valueOf(args[0]));
    }

}
