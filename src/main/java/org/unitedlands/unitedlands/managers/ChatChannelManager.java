package org.unitedlands.unitedlands.managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.chat.ChatChannel;
import org.unitedlands.unitedlands.classes.chat.ChatChannelType;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ChatChannelManager {

    private static ChatChannelManager instance;

    public static ChatChannelManager instance() {
        return instance;
    }

    private final UnitedLands plugin;
    @SuppressWarnings("unused")
    private final MessageProvider messageProvider;

    private Map<String, ChatChannel> channels = new HashMap<>();
    private Map<Player, ChatChannel> playerChannels = new HashMap<>();

    public ChatChannelManager(UnitedLands plugin, MessageProvider messageProvider) {
        instance = this;
        this.plugin = plugin;
        this.messageProvider = messageProvider;

        registerCommands();
    }

    private void registerCommands() {

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            ArgumentCommandNode<CommandSourceStack, String> messageArgument = Commands.argument("message", StringArgumentType.greedyString())
                    .executes(context -> {

                        Player sender = (Player) context.getSource().getSender();
                        String usedAlias = context.getNodes().get(0).getNode().getName();
                        String message = StringArgumentType.getString(context, "message");

                        switch (usedAlias) {
                        case "lc", "local", "localchat":
                            sendMessage(sender, ChatChannelType.LOCAL, message);
                            break;
                        case "gc", "global", "globalchat":
                            sendMessage(sender, ChatChannelType.GLOBAL, message);
                            break;
                        case "stc", "staff", "staffchat":
                            sendMessage(sender, ChatChannelType.STAFF, message);
                            break;
                        case "sc", "settlementchat":
                            sendMessage(sender, ChatChannelType.SETTLEMENT, message);
                            break;
                        case "cc", "countrychat":
                            sendMessage(sender, ChatChannelType.COUNTRY, message);
                            break;
                        default:
                            break;
                        }

                        return Command.SINGLE_SUCCESS;
                    })
                    .build();

            LiteralCommandNode<CommandSourceStack> localChatCommand = Commands.literal("localchat")
                    .then(messageArgument)
                    .executes(context -> {
                        ChatChannelManager.instance().switchChannel((Player) context.getSource().getSender(), ChatChannelType.LOCAL);
                        return 0;
                    })
                    .build();

            LiteralCommandNode<CommandSourceStack> globalChatCommand = Commands.literal("globachat")
                    .then(messageArgument)
                    .executes(context -> {
                        ChatChannelManager.instance().switchChannel((Player) context.getSource().getSender(), ChatChannelType.GLOBAL);
                        return 0;
                    })
                    .build();

            LiteralCommandNode<CommandSourceStack> staffChatCommand = Commands.literal("staffchat")
                    .then(messageArgument)
                    .executes(context -> {
                        ChatChannelManager.instance().switchChannel((Player) context.getSource().getSender(), ChatChannelType.STAFF);
                        return 0;
                    })
                    .build();

            LiteralCommandNode<CommandSourceStack> settlementChatCommand = Commands.literal("settlementchat")
                    .then(messageArgument)
                    .executes(context -> {
                        ChatChannelManager.instance().switchChannel((Player) context.getSource().getSender(), ChatChannelType.SETTLEMENT);
                        return 0;
                    })
                    .build();

            LiteralCommandNode<CommandSourceStack> countryChatCommand = Commands.literal("countrychat")
                    .then(messageArgument)
                    .executes(context -> {
                        ChatChannelManager.instance().switchChannel((Player) context.getSource().getSender(), ChatChannelType.COUNTRY);
                        return 0;
                    })
                    .build();

            commands.register(localChatCommand, "Local chat", List.of("lc", "local"));
            commands.register(globalChatCommand, "Global chat", List.of("gc", "global"));
            commands.register(staffChatCommand, "Staff chat", List.of("stc", "staff"));
            commands.register(settlementChatCommand, "Settlement chat", List.of("sc"));
            commands.register(countryChatCommand, "Country chat", List.of("cc"));

        });

    }

    public void switchChannel(Player player, ChatChannelType type) {

        var citizen = UnitedLandsDataManager.instance().getCitizen(player);
        if (citizen == null) {
            Logger.logError("Couldn't find citizen data for chat channel switch", "UnitedLands");
            return;
        }

        var channel = getOrCreateChannel(citizen, type);
        if (channel == null) {
            Messenger.sendMessage(player, "<yellow>You don't have access to that channel.");
            return;
        }

        playerChannels.put(player, channel);
        Messenger.sendMessage(player, "Now talking in <" + channel.getType().getColor() + ">" + channel.getType() + "</" + channel.getType().getColor() + ">");
    }

    public ChatChannel getPlayerChannel(Player player) {
        return playerChannels.get(player);
    }

    public ChatChannel getOrCreateChannel(Citizen citizen, ChatChannelType type) {

        switch (type) {
        case GLOBAL:
            return channels.computeIfAbsent("global", v -> new ChatChannel("global", ChatChannelType.GLOBAL));
        case LOCAL:
            return channels.computeIfAbsent("local", v -> new ChatChannel("local", ChatChannelType.LOCAL));
        case STAFF:
            // TODO: Permission check
            return channels.computeIfAbsent("staff", v -> new ChatChannel("staff", ChatChannelType.STAFF));
        case COUNTRY:
            if (citizen.hasSettlement()) {
                return channels.computeIfAbsent(citizen.getSettlement().getUuid().toString(),
                        v -> new ChatChannel(citizen.getSettlement().getUuid().toString(), ChatChannelType.SETTLEMENT));
            }
            break;
        case SETTLEMENT:
            if (citizen.hasCountry()) {
                return channels.computeIfAbsent(citizen.getCountry().getUuid().toString(),
                        v -> new ChatChannel(citizen.getCountry().getUuid().toString(), ChatChannelType.COUNTRY));
            }
            break;
        }
        return null;
    }

    public void registerPlayer(Player player) {

        var citizen = UnitedLandsDataManager.instance().getCitizen(player);
        if (citizen == null) {
            Logger.logError("Could not add player " + player.getName() + " to chat because citizen data is missing.", "UnitedLands");
            return;
        }

        var globalChannel = getOrCreateChannel(citizen, ChatChannelType.GLOBAL);
        globalChannel.addViewer(player);

        var localChannel = getOrCreateChannel(citizen, ChatChannelType.LOCAL);
        localChannel.addViewer(player);

        if (player.isOp()) {
            var staffChannel = getOrCreateChannel(citizen, ChatChannelType.STAFF);
            staffChannel.addViewer(player);
        }

        playerChannels.put(player, globalChannel);
    }

    public void unregisterPlayer(Player player) {
        playerChannels.remove(player);
        for (var channel : channels.values()) {
            channel.removeViewer(player);
        }
        channels.values().removeIf(c -> c.getViewerCount() == 0 && c.getType().removeEmpty());
    }

    public void sendMessage(Player player, ChatChannelType channelType, String message) {

        var citizen = UnitedLandsDataManager.instance().getCitizen(player);
        if (citizen == null)
            return;

        var channel = getOrCreateChannel(citizen, channelType);
        if (channel == null) {
            Messenger.sendMessage(player, "<yellow>You don't have access to that channel.");
            return;
        }

        Set<Player> viewers = new HashSet<>();
        if (channel.getRange() == -1) {
            viewers = channel.getViewers();
        } else {
            viewers = channel.getViewersInRange(player.getLocation());
        }

        var color = channel.getType().getColor();
        var formattedMessage = getFormattedMessage(channel, message, color, player);

        Audience.audience(viewers).sendMessage(formattedMessage);
    }

    public void handleMessage(AsyncChatEvent event) {

        var channel = getPlayerChannel(event.getPlayer());

        event.viewers().clear();
        if (channel.getRange() == -1) {
            event.viewers().addAll(channel.getViewers());
        } else {
            event.viewers().addAll(channel.getViewersInRange(event.getPlayer().getLocation()));
        }

        var text = PlainTextComponentSerializer.plainText().serialize(event.message());
        var color = channel.getType().getColor();

        event.renderer((source, sourceDisplayName, message, viewer) -> getFormattedMessage(channel, text, color, source));

    }

    private @NotNull TextComponent getFormattedMessage(ChatChannel channel, String text, String color, Player source) {
        var miniMessage = MiniMessage.miniMessage();
        return Component.text()
                .append(miniMessage.deserialize(channel.getType().getPrefix()))
                .append(Component.text(source.getName()))
                .append(miniMessage.deserialize("<dark_gray>: </dark_gray>"))
                .append(miniMessage.deserialize("<" + color + ">" + text + "</" + color + ">"))
                .build();
    }

}
