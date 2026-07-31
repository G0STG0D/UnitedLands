package org.unitedlands.unitedlands.commands.handlers.admin;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.utils.Messenger;

public class ReloadCommand extends BaseCommandHandler<UnitedLands> {

    public ReloadCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        plugin.reloadConfig();
        plugin.getMessageConfig().reload();
        plugin.getPermissionConfig().reload();
        plugin.getMessageProvider().reload(plugin.getMessageConfig().get());
        plugin.getPermissionManager().reloadRankPermissions();
        plugin.getWebServices().reloadConfig();
        
        Settings.loadSettings(plugin.getConfig());
        
        Messenger.sendMessage(sender, messageProvider.get(Message.RELOAD.path()), null, messageProvider.get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] arg1) {
        return null;
    }

}
