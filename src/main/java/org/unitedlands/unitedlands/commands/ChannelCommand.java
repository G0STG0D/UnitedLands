package org.unitedlands.unitedlands.commands;

import org.unitedlands.classes.BaseCommandExecutor;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.commands.handlers.channel.ChannelJoinCommand;

public class ChannelCommand extends BaseCommandExecutor<UnitedLands> {

    public ChannelCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {
        handlers.put("join", new ChannelJoinCommand(plugin, messageProvider));
    }

}
