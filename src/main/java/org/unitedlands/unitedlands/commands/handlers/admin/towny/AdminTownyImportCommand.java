package org.unitedlands.unitedlands.commands.handlers.admin.towny;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Coordinates;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.LocationMembership;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.SettlementChunk;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.EconomyManager;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyPermission.ActionType;

public class AdminTownyImportCommand extends BaseCommandHandler<UnitedLands> {

    public AdminTownyImportCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return plugin.getTownyProvider().getTownNames();
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length > 1) {
            Messenger.sendMessage(player, messageProvider.get("admin.usage.towny.import"),
                    null, messageProvider.get("prefix"));
        }

        if (args.length == 1) {
            var town = plugin.getTownyProvider().getTown(args[0]);
            if (town == null)
                return;

            importTown(player, town);
        } else {
            var towns = plugin.getTownyProvider().getTowns();

            for (Town town : towns) {
                var spawnLocation = town.getSpawnOrNull();
                if (spawnLocation == null)
                    continue;
                var regionCoords = CoordinateUtils.locationToRegionCoordinates(spawnLocation);
                var region = GlobalDataManager.instance().getRegion(regionCoords);
                if (region == null)
                    continue;

                importTown(player, town);
            }
        }

    }

    private void importTown(Player player, Town town) {
        var plots = town.getTownBlocks();

        if (GlobalDataManager.instance().getSettlement(town.getUUID()) != null) {
            return;
        }

        var settlement = new Settlement();
        settlement.setUuid(town.getUUID());
        settlement.setName(town.getName());
        settlement.setFoundingTimestamp(town.getRegistered());

        try {
            var spawnLocation = town.getSpawn();

            var homeChunkCoord = CoordinateUtils.locationToChunkCoordinates(spawnLocation);
            settlement.setHomeChunkCoordinates(homeChunkCoord);
            settlement.setSpawn(spawnLocation);
            settlement.setWorld(spawnLocation.getWorld());

            var regionCoords = CoordinateUtils.locationToRegionCoordinates(spawnLocation);
            var region = GlobalDataManager.instance().getRegion(regionCoords);
            if (region != null) {
                region.addSettlement(settlement);
                settlement.setRegion(region);
            }

            for (var plot : plots) {
                var chunk = convertTownBlockToSettlementChunk(town, plot);

                chunk.setSettlement(settlement);
                settlement.addChunk(chunk);
            }

            var townPerms = town.getPermissions();
            var placePerms = getMembershipFlags(townPerms, ActionType.BUILD);
            var breakPerms = getMembershipFlags(townPerms, ActionType.DESTROY);
            var switchPerms = getMembershipFlags(townPerms, ActionType.SWITCH);
            var blockUsePerms = getMembershipFlags(townPerms, ActionType.ITEM_USE);

            settlement.setPlacePermissions(placePerms);
            settlement.setBreakPermissions(breakPerms);
            settlement.setSwitchPermissions(switchPerms);
            settlement.setInteractPermissions(switchPerms);
            settlement.setContainerPermissions(switchPerms);
            settlement.setBlockUsePermissions(blockUsePerms);

            GlobalDataManager.instance().registerSettlement(settlement);
            GlobalDataManager.instance().createSettlementDbData(settlement);

            EconomyManager.instance().createAccount(settlement.getUuid(), settlement.getName());

            Messenger.sendMessage(player, messageProvider.get("admin.towny.import.success-settlement"),
                    Map.of("settlement", settlement.getName(), "plots", plots.size() + ""),
                    messageProvider.get("prefix"));

            if (town.hasNation() && town.isCapital() && region != null && !region.hasCountry()) {
                var nation = town.getNation();

                var country = GlobalDataManager.instance().getCountry(nation.getUUID());
                if (country == null) {

                    country = new Country();
                    country.setUuid(nation.getUUID());
                    country.setName(nation.getName());
                    country.setFoundingTimestamp(nation.getRegistered());
                    country.setWorld(settlement.getWorld());

                    country.setStrokeColor(nation.getMapColorHexCode());
                    country.setFillColor(nation.getMapColorHexCode() + "10");

                    EconomyManager.instance().createAccount(country.getUuid(), country.getName());

                    country.addRegion(region);
                    country.addSettlement(settlement);
                    country.setCapital(settlement);

                    region.setCountry(country);
                    settlement.setCountry(country);

                    GlobalDataManager.instance().createCountryDbData(country);
                    GlobalDataManager.instance().updateRegionDbData(region);
                    GlobalDataManager.instance().updateSettlementDbData(settlement);

                    Pl3xMapRenderer.instance().renderRegion(region);
                    Pl3xMapRenderer.instance().renderCountry(country);

                    Messenger.sendMessage(player, messageProvider.get("admin.towny.import.success-country"),
                            Map.of("country", country.getName()), messageProvider.get("prefix"));
                }
            }

            Pl3xMapRenderer.instance().renderSettlement(settlement);

        } catch (Exception ex) {
            Messenger.sendMessage(player, messageProvider.get("admin.towny.import.error"),
                    Map.of("message", ex.getMessage()), messageProvider.get("prefix"));
        }
    }

    private SettlementChunk convertTownBlockToSettlementChunk(Town town, TownBlock block) {

        SettlementChunk chunk = new SettlementChunk();
        chunk.setUuid(UUID.randomUUID());

        chunk.setCoordinates(new Coordinates(block.getX(), block.getZ(), block.getWorld().getName()));

        var townPerms = town.getPermissions();
        var blockPerms = block.getPermissions();

        if (townPerms.mobs != blockPerms.mobs) {
            chunk.setAllowAnimals(blockPerms.mobs);
            chunk.setAllowMonsters(blockPerms.mobs);
        }
        if (townPerms.fire != blockPerms.fire)
            chunk.setAllowFire(blockPerms.fire);
        if (townPerms.explosion != blockPerms.explosion)
            chunk.setAllowExplosions(blockPerms.explosion);
        if (townPerms.pvp != blockPerms.pvp)
            chunk.setAllowPvp(blockPerms.pvp);

        var placePerms = getPlotMembershipFlags(townPerms, blockPerms, ActionType.BUILD);
        var breakPerms = getPlotMembershipFlags(townPerms, blockPerms, ActionType.DESTROY);
        var switchPerms = getPlotMembershipFlags(townPerms, blockPerms, ActionType.SWITCH);
        var blockUsePerms = getPlotMembershipFlags(townPerms, blockPerms, ActionType.ITEM_USE);

        chunk.setPlacePermissions(placePerms);
        chunk.setBreakPermissions(breakPerms);
        chunk.setSwitchPermissions(switchPerms);
        chunk.setInteractPermissions(switchPerms);
        chunk.setContainerPermissions(switchPerms);
        chunk.setBlockUsePermissions(blockUsePerms);

        return chunk;
    }

    private int getPlotMembershipFlags(TownyPermission townPerms, TownyPermission plotPerms, ActionType action) {
        int membership = LocationMembership.UNSET;

        var anyOverrides = townPerms.getResidentPerm(action) != plotPerms.getResidentPerm(action) ||
                townPerms.getNationPerm(action) != plotPerms.getNationPerm(action) ||
                townPerms.getAllyPerm(action) != plotPerms.getAllyPerm(action) ||
                townPerms.getOutsiderPerm(action) != plotPerms.getOutsiderPerm(action);

        if (!anyOverrides)
            return membership;

        if (plotPerms.getResidentPerm(action)) {
            membership |= LocationMembership.SETTLEMENT_RESIDENT;
        }

        if (plotPerms.getNationPerm(action)) {
            membership |= LocationMembership.COUNTRY_RESIDENT;
        }

        if (plotPerms.getAllyPerm(action)) {
            membership |= LocationMembership.ALLY;
        }

        if (plotPerms.getOutsiderPerm(action)) {
            membership |= LocationMembership.FOREIGNER;
        }

        return membership;
    }

    private int getMembershipFlags(TownyPermission perms, ActionType action) {
        int membership = LocationMembership.OWNER;
        if (perms.getResidentPerm(action)) {
            membership |= LocationMembership.SETTLEMENT_RESIDENT;
        }
        if (perms.getNationPerm(action)) {
            membership |= LocationMembership.COUNTRY_RESIDENT;
        }
        if (perms.getAllyPerm(action)) {
            membership |= LocationMembership.ALLY;
        }
        if (perms.getOutsiderPerm(action)) {
            membership |= LocationMembership.FOREIGNER;
        }
        return membership;
    }

}
