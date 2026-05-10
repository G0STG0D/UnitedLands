package org.unitedlands.unitedlands.managers;

import java.sql.SQLException;

import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.RegionChunk;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.SettlementChunk;
import org.unitedlands.unitedlands.classes.db.CitizenService;
import org.unitedlands.unitedlands.classes.db.CountryService;
import org.unitedlands.unitedlands.classes.db.RegionChunkService;
import org.unitedlands.unitedlands.classes.db.RegionService;
import org.unitedlands.unitedlands.classes.db.SchemaVersion;
import org.unitedlands.unitedlands.classes.db.SettlementChunkService;
import org.unitedlands.unitedlands.classes.db.SettlementService;
import org.unitedlands.utils.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseManager {

    private static DatabaseManager instance;
    public static DatabaseManager instance() {
        return instance;
    }

    private final UnitedLands plugin;

    private HikariDataSource hikariDataSource;
    private ConnectionSource connectionSource;

    private CountryService countryService;
    private RegionService regionService;
    private RegionChunkService regionChunkService;
    private SettlementService settlementService;
    private SettlementChunkService settlementChunkService;
    private CitizenService citizenService;

    public DatabaseManager(UnitedLands plugin) {
        this.plugin = plugin;
    }

    public void initialize() {


        instance = this;
        
        var fileConfig = plugin.getConfig();

        String host = fileConfig.getString("mysql.host");
        int port = fileConfig.getInt("mysql.port");
        String database = fileConfig.getString("mysql.database");
        String username = fileConfig.getString("mysql.username");
        String password = fileConfig.getString("mysql.password");

        String jdbcUrl = String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=%s&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                host,
                port,
                database,
                plugin.getConfig().getBoolean("developer-mode") ? "false" : "true");

        try {

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);

            // Connection pool settings
            config.setMaximumPoolSize(24);
            config.setMinimumIdle(2);
            config.setIdleTimeout(600000); // 10 minutes
            config.setMaxLifetime(1800000); // 30 minutes
            config.setConnectionTimeout(5000); // 5 seconds
            config.setValidationTimeout(3000); // 3 seconds

            // Validation query
            config.setConnectionTestQuery("SELECT 1");
            config.setLeakDetectionThreshold(15000); // Warn if connection held 15+ seconds
            hikariDataSource = new HikariDataSource(config);
            connectionSource = new DataSourceConnectionSource(hikariDataSource, jdbcUrl);

            Logger.log("Connected to MySQL database with HikariCP.", "UnitedLands");

            verifySchemaVersion();
            registerServices();

            Logger.log("DatabaseManager initialized successfully.", "UnitedLands");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void registerServices() throws SQLException {
        this.countryService = new CountryService(getDao(Country.class));
        this.regionService = new RegionService(getDao(Region.class));
        this.regionChunkService = new RegionChunkService(getDao(RegionChunk.class));
        this.settlementService = new SettlementService(getDao(Settlement.class));
        this.settlementChunkService = new SettlementChunkService(getDao(SettlementChunk.class));
        this.citizenService = new CitizenService(getDao(Citizen.class));
    }

    private void verifySchemaVersion() throws SQLException {
        Dao<SchemaVersion, Integer> versionDao = getDao(SchemaVersion.class);
        SchemaVersion version = versionDao.queryForId(1);

        if (version == null) {
            version = new SchemaVersion(1);
            versionDao.create(version);
        }

        applyMigrations(versionDao, version);
    }

    private void applyMigrations(Dao<SchemaVersion, Integer> versionDao, SchemaVersion version) throws SQLException {
        // Example for future migrations on production server

        // if (version.getVersion() < 2) {
        // // Migration 1 → 2: Add new field to `PlayerData`

        // versionDao.executeRaw("ALTER TABLE test_data ADD COLUMN new_field
        // VARCHAR(255) DEFAULT NULL;");

        // version.setVersion(2);
        // versionDao.update(version);
        // }
    }

    public <T, ID> Dao<T, ID> getDao(Class<T> clazz) throws SQLException {

        // In developer mode, drop the table if it exists
        if (plugin.getConfig().getBoolean("developer-mode"))
            TableUtils.dropTable(connectionSource, clazz, true);

        TableUtils.createTableIfNotExists(connectionSource, clazz);
        return DaoManager.createDao(connectionSource, clazz);
    }

    public void close() {
        try {
            if (connectionSource != null) {
                connectionSource.close();
                Logger.log("Disconnected from MySQL database.", "UnitedLands");
            }
            if (hikariDataSource != null) {
                hikariDataSource.close();
                Logger.log("HikariCP connection closed.", "UnitedLands");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CountryService getCountryService() {
        return countryService;
    }

    public RegionService getRegionService() {
        return regionService;
    }

    public RegionChunkService getRegionChunkService() {
        return regionChunkService;
    }

    public SettlementService getSettlementService() {
        return settlementService;
    }

    public SettlementChunkService getSettlementChunkService() {
        return settlementChunkService;
    }

    public CitizenService getCitizenService() {
        return citizenService;
    }

    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }

}
