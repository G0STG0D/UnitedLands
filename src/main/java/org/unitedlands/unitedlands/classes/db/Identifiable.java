package org.unitedlands.unitedlands.classes.db;

import java.util.UUID;

public interface Identifiable {
    UUID getUuid();
    void setUuid(UUID id);
}