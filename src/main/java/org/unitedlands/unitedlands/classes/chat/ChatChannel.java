package org.unitedlands.unitedlands.classes.chat;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ChatChannel {

    private final String key;
    private final ChatChannelType type;
    private Set<Player> viewers = new HashSet<>();
    private int range = -1;

    public ChatChannel(String key, ChatChannelType type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public ChatChannelType getType() {
        return type;
    }

    public Set<Player> getViewers() {
        return viewers;
    }

    public Set<Player> getViewersInRange(Location loc) {
        return viewers.stream().filter(viewer -> viewer.getLocation().distanceSquared(loc) <= range * range).collect(Collectors.toSet());
    }

    public void addViewer(Player player) {
        viewers.add(player);
    }

    public void removeViewer(Player player) {
        viewers.remove(player);
    }

    public void clearViewers() {
        viewers.clear();
    }

    public int getViewerCount() {
        return viewers.size();
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChatChannel other = (ChatChannel) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

}
