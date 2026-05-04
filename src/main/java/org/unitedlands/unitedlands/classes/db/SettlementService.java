package org.unitedlands.unitedlands.classes.db;

import java.util.UUID;

import org.unitedlands.unitedlands.classes.Settlement;

import com.j256.ormlite.dao.Dao;

public class SettlementService extends BaseDbService<Settlement> {

    public SettlementService(Dao<Settlement, UUID> dao) {
        super(dao);
    }

}
