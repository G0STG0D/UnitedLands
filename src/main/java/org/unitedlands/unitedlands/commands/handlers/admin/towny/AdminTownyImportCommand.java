package org.unitedlands.unitedlands.commands.handlers.admin.towny;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.Coordinates;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.LocationMembership;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.SettlementChunk;
import org.unitedlands.unitedlands.classes.metadata.BooleanMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.DoubleMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.IntegerMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.LocationMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.LongMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.StringMetaDataField;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.EconomyManager;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.utils.Messenger;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyPermission.ActionType;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import com.palmergames.bukkit.towny.object.metadata.DecimalDataField;
import com.palmergames.bukkit.towny.object.metadata.IntegerDataField;
import com.palmergames.bukkit.towny.object.metadata.LocationDataField;
import com.palmergames.bukkit.towny.object.metadata.LongDataField;

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

            // Metadata

            var townyMeta = town.getMetadata();
            for (var tmf : townyMeta) {
                var type = tmf.getTypeID();
                switch (type) {
                    case "towny_booldf":
                        var boolMeta = new BooleanMetaDataField(tmf.getKey());
                        boolMeta.setLabel(tmf.getLabel());
                        boolMeta.setShowInScreens(tmf.shouldDisplayInStatus());
                        boolMeta.setValue(((BooleanDataField) tmf).getValue());
                        settlement.addMetadata(boolMeta);
                        break;
                    case "towny_decdf":
                        var doubleMeta = new DoubleMetaDataField(tmf.getKey());
                        doubleMeta.setLabel(tmf.getLabel());
                        doubleMeta.setShowInScreens(tmf.shouldDisplayInStatus());
                        doubleMeta.setValue(((DecimalDataField) tmf).getValue());
                        settlement.addMetadata(doubleMeta);
                        break;
                    case "towny_intdf":
                        var intMeta = new IntegerMetaDataField(tmf.getKey());
                        intMeta.setLabel(tmf.getLabel());
                        intMeta.setShowInScreens(tmf.shouldDisplayInStatus());
                        intMeta.setValue(((IntegerDataField) tmf).getValue());
                        settlement.addMetadata(intMeta);
                        break;
                    case "towny_locationdf":
                        var locMeta = new LocationMetaDataField(tmf.getKey());
                        locMeta.setLabel(tmf.getLabel());
                        locMeta.setShowInScreens(tmf.shouldDisplayInStatus());
                        locMeta.setValue(((LocationDataField) tmf).getValue());
                        settlement.addMetadata(locMeta);
                        break;
                    case "towny_longdf":
                        var longMeta = new LongMetaDataField(tmf.getKey());
                        longMeta.setLabel(tmf.getLabel());
                        longMeta.setShowInScreens(tmf.shouldDisplayInStatus());
                        longMeta.setValue(((LongDataField) tmf).getValue());
                        settlement.addMetadata(longMeta);
                        break;
                    default:
                        var stringMeta = new StringMetaDataField(tmf.getKey());
                        stringMeta.setLabel(tmf.getLabel());
                        stringMeta.setShowInScreens(tmf.shouldDisplayInStatus());
                        stringMeta.setValue(String.valueOf(tmf.getValue()));
                        settlement.addMetadata(stringMeta);
                        break;
                }

            }

            GlobalDataManager.instance().registerSettlement(settlement);
            GlobalDataManager.instance().createSettlementDbData(settlement);

            EconomyManager.instance().createAccount(settlement.getUuid(), settlement.getName());

            var residents = town.getResidents();
            for (var resident : residents)
            {
                var citizen = GlobalDataManager.instance().getCitizen(resident.getUUID());
                if (citizen == null)
                {
                    citizen = new Citizen();
                    citizen.setUuid(resident.getUUID());
                    citizen.setName(resident.getName());
                    citizen.setJoined(resident.getRegistered());
                    citizen.setLastLogon(resident.getLastOnline());
                    citizen.setSettlement(settlement);
                    settlement.addCitizen(citizen);

                    GlobalDataManager.instance().createCitizenDbData(citizen);
                }
            }


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
