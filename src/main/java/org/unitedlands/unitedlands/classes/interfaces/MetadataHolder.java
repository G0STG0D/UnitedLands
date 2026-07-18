package org.unitedlands.unitedlands.classes.interfaces;

import java.util.Map;

import org.unitedlands.unitedlands.classes.metadata.MetaDataField;

public interface MetadataHolder {
    boolean hasMetadata(String key);
    void addMetadata(MetaDataField<?> field);
    Map<String, MetaDataField<?>> getMetadata();
    MetaDataField<?> getMetadata(String key);
    void removeMetadata(String key);
    void saveMetadata();
}
