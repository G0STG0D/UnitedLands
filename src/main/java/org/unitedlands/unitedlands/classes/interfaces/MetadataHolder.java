package org.unitedlands.unitedlands.classes.interfaces;

import java.util.Map;

import org.unitedlands.unitedlands.classes.metadata.MetaDataField;

public interface MetadataHolder {
    void addMetadata(MetaDataField<?> field);
    Map<String, MetaDataField<?>> getMetadata();
    MetaDataField<?> getMetadata(String key);
    void removeMetadata(String key);
    void saveMetadata();
}
