package org.unitedlands.unitedlands.classes.db;

import java.util.UUID;

import org.unitedlands.unitedlands.classes.RegionChunk;

import com.j256.ormlite.dao.Dao;

public class RegionChunkService extends BaseDbService<RegionChunk> {

    public RegionChunkService(Dao<RegionChunk, UUID> dao) {
        super(dao);
    }

}
