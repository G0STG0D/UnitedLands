package org.unitedlands.unitedlands.commands.handlers.admin;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.registrars.messages.UnitedMessagesRegistrar;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.configs.GeneralConfig;
import org.unitedlands.unitedlands.classes.configs.TitlesConfig;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.utils.United;

@UnitedSubCommand(
        parent = CmdAdmin.class,
        name = "reload",
        description = "Admin reload command",
        usage = "/ula reload"
)
public class CmdAdminReload implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        GeneralConfig.get().reload();
        TitlesConfig.get().reload();

        UnitedMessagesRegistrar.reload(UnitedLands.instance());
        
        PermissionManager.instance().reloadRankPermissions();

        UnitedLands.instance().reloadConfig();

        UnitedLands.instance().getWebServices().reloadConfig();

        Settings.loadSettings(UnitedLands.instance().getConfig());

        United.messenger().send(sender, "reload");
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] arg1) {
        return null;
    }

}
