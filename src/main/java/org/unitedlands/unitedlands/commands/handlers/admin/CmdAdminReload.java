package org.unitedlands.unitedlands.commands.handlers.admin;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdAdmin.class,
        name = "reload",
        description = "Admin reload command",
        usage = "/ula reload"
)
public class CmdAdminReload implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        UnitedLands.instance().reloadConfig();
        UnitedLands.instance().getMessageConfig().reload();
        UnitedLands.instance().getPermissionConfig().reload();
        UnitedLands.instance().getMessageProvider().reload(UnitedLands.instance().getMessageConfig().get());
        UnitedLands.instance().getPermissionManager().reloadRankPermissions();
        UnitedLands.instance().getWebServices().reloadConfig();
        
        Settings.loadSettings(UnitedLands.instance().getConfig());
        
        Messenger.sendMessage(sender, MessageProvider.instance().get(Message.RELOAD.path()), null, MessageProvider.instance().get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] arg1) {
        return null;
    }

}
