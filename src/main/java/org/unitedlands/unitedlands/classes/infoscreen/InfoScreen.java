package org.unitedlands.unitedlands.classes.infoscreen;

import java.util.LinkedList;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.utils.Messenger;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public abstract class InfoScreen {

    protected final Plugin plugin;
    protected final IMessageProvider messageProvider;

    protected LinkedList<InfoScreenComponent> components = new LinkedList<>();

    public InfoScreen(Plugin plugin, IMessageProvider messageProvider) {
        this.plugin = plugin;
        this.messageProvider = messageProvider;
    }

    public LinkedList<InfoScreenComponent> getComponents() {
        return components;
    }

    public void addComponent(String id, Component content) {
        components.add(new InfoScreenComponent(id, content));
    }

    public void addComponent(String id, String content, Map<String, String> replacements) {
        components.add(new InfoScreenComponent(id, Messenger.getMessage(content, replacements)));
    }

    public void addComponent(int index, String id, Component content) {
        components.add(index, new InfoScreenComponent(id, content));
    }

    public void addComponent(int index, String id, String content, Map<String, String> replacements) {
        components.add(index, new InfoScreenComponent(id, Messenger.getMessage(content, replacements)));
    }


    public void addComponent(String afterKey, String id, Component content) {
        int index = 0;
        for (var c : components) {
            index++;
            if (c.getId().equals(afterKey))
                break;
        }
        components.add(index, new InfoScreenComponent(id, content));
    }

    public void removeComponent(String id) {
        InfoScreenComponent componentToRemove = null;
        for (var c : components) {
            if (c.getId().equals(id))
                componentToRemove = c;
        }
        if (componentToRemove != null)
            components.remove(componentToRemove);
    }

    public void removeComponent(int index) {
        components.remove(index);
    }

    public void send(Audience receiver) {
        var components = getComponents();
        if (components != null && !components.isEmpty()) {
            for (var component : components) {
                Messenger.send(receiver, component.getContent());
            }
        }
    }

    public Component buildHeader(String name) {
        return buildHeader(name, UnitedLands.instance().getMessageConfig().get());
    }

    public Component buildHeader(String name, YamlConfiguration messageConfig) {

        var header = "";

        var maxWidth = messageConfig.getInt("info-screens.header.max-chars");

        var fillerStart = messageConfig.getString("info-screens.header.filler-start");
        var filler = messageConfig.getString("info-screens.header.filler");
        var fillerEnd = messageConfig.getString("info-screens.header.filler-end");
        var fillerColor = messageConfig.getString("info-screens.header.filler-color");
        var titleColor = messageConfig.getString("info-screens.header.title-color");
        var titleStart = messageConfig.getString("info-screens.header.title-start");
        var titleEnd = messageConfig.getString("info-screens.header.title-end");

        var nameLength = name.length();
        var fillerStartLength = fillerStart.length();
        var fillerEndLength = fillerEnd.length();
        var titleStartLength = titleStart.length();
        var titleEndLength = titleEnd.length();

        var spaceToFill = Math.max(0,
                (maxWidth - nameLength - fillerStartLength - fillerEndLength - titleStartLength - titleEndLength));
        var halfSpace = spaceToFill / 2;
        var fillspaceFront = halfSpace;
        var fillspaceBack = halfSpace;
        if (spaceToFill % 2 == 0)
            fillspaceFront += 1;

        header += "<" + fillerColor + ">" + fillerStart;
        for (int i = 0; i < fillspaceFront; i++)
            header += filler;
        header += titleStart + "<" + titleColor + ">" + name + "</" + titleColor + ">" + titleEnd;
        for (int i = 0; i < fillspaceBack; i++)
            header += filler;
        header += fillerEnd + "</" + fillerColor + ">";

        return MiniMessage.miniMessage().deserialize(header);
    }

}
