package org.unitedlands.unitedlands.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.unitedlands.unitedlands.managers.ChatChannelManager;
import io.papermc.paper.event.player.AsyncChatEvent;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncChatEvent event) {
        ChatChannelManager.instance().handleMessage(event);
    }

}
