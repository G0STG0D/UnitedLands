package org.unitedlands.unitedlands.classes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.unitedlands.unitedlands.classes.db.Identifiable;
import org.unitedlands.unitedlands.managers.GlobalDataManager;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public class Citizen implements Identifiable {

    @DatabaseField(id = true, width = 36, canBeNull = false)
    private UUID uuid;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private long joined;

    @DatabaseField(canBeNull = false, columnName = "last_logon")
    private long lastLogon;

    @DatabaseField(width = 36, columnName = "settlement_uuid")
    private UUID settlementUuid;

    @DatabaseField(dataType = DataType.LONG_STRING, columnName = "settlement_ranks_serialized")
    private String settlementRanksSerialized;
    @DatabaseField(dataType = DataType.LONG_STRING, columnName = "country_ranks_serialized")
    private String countryRanksSerialized;

    private OfflinePlayer player;
    private Settlement settlement;
    private transient Set<String> settlementRanks;
    private transient Set<String> countryRanks;

    public Citizen() {

    }

    public Citizen(OfflinePlayer player) {
        this.uuid = player.getUniqueId();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getJoined() {
        return joined;
    }

    public void setJoined(long joined) {
        this.joined = joined;
    }

    public long getLastLogon() {
        return lastLogon;
    }

    public void setLastLogon(long lastLogon) {
        this.lastLogon = lastLogon;
    }

    public OfflinePlayer getPlayer() {
        if (player == null)
            player = Bukkit.getOfflinePlayer(uuid);
        return player;
    }

    public void setPlayer(OfflinePlayer player) {
        this.player = player;
        this.uuid = player.getUniqueId();
    }

    public Settlement getSettlement() {
        if (settlement == null && settlementUuid != null)
            settlement = GlobalDataManager.instance().getSettlement(settlementUuid);
        return settlement;
    }

    public Boolean hasSettlement() {
        return getSettlement() != null;
    }

    public void setSettlement(Settlement settlement) {
        this.settlement = settlement;
        this.settlementUuid = settlement.getUuid();
    }

    public void removeSettlement() {
        this.settlement = null;
        this.settlementUuid = null;
    }

    public void addSettlementRank(String rank) {
        var r = new HashSet<>(getSettlementRanks());
        r.add(rank);
        setSettlementRanks(r);
    }

    public void removeSettlementRank(String rank) {
        var r = new HashSet<>(getSettlementRanks());
        r.remove(rank);
        setSettlementRanks(r);
    }

    public void removeSettlementRanks() {
        var r = new HashSet<String>();
        setSettlementRanks(r);
    }

    public boolean hasSettlementRank(String rank) {
        return getSettlementRanks() != null ? getSettlementRanks().contains(rank) : false;
    }

    public Set<String> getSettlementRanks() {
        if (settlementRanks == null) {
            if (settlementRanksSerialized != null) {
                settlementRanks = Arrays.stream(settlementRanksSerialized.split(";"))
                        .collect(Collectors.toSet());
            } else {
                settlementRanks = new HashSet<>();
            }
        }
        return settlementRanks;
    }

    public void setSettlementRanks(Set<String> ranks) {
        this.settlementRanks = ranks;
        if (ranks != null && !ranks.isEmpty()) {
            this.settlementRanksSerialized = ranks.stream()
                    .collect(Collectors.joining(";"));
        } else {
            this.settlementRanksSerialized = null;
        }
    }

    public void addCountryRank(String rank) {
        var r = new HashSet<>(getCountryRanks());
        r.add(rank);
        setCountryRanks(r);
    }

    public void removeCountryRank(String rank) {
        var r = new HashSet<>(getCountryRanks());
        r.add(rank);
        setCountryRanks(r);
    }

    public void removeCountryRanks() {
        var r = new HashSet<String>();
        setCountryRanks(r);
    }

    public boolean hasCountry() {
        if (getSettlement() == null)
            return false;
        return getSettlement().hasCountry();
    }

    public Country getCountry() {
        if (getSettlement() == null)
            return null;
        return getSettlement().getCountry();
    }

    public boolean hasCountryRank(String rank) {
        return getCountryRanks() != null ? getCountryRanks().contains(rank) : false;
    }

    public Set<String> getCountryRanks() {
        if (countryRanks == null) {
            if (countryRanksSerialized != null) {
                countryRanks = Arrays.stream(countryRanksSerialized.split(";"))
                        .collect(Collectors.toSet());
            } else {
                countryRanks = new HashSet<>();
            }
        }
        return countryRanks;
    }

    public void setCountryRanks(Set<String> ranks) {
        this.countryRanks = ranks;
        if (ranks != null && !ranks.isEmpty()) {
            this.countryRanksSerialized = ranks.stream()
                    .collect(Collectors.joining(";"));
        } else {
            this.countryRanksSerialized = null;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
        Citizen other = (Citizen) obj;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

}
