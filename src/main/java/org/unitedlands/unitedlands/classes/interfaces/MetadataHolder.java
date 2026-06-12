package org.unitedlands.unitedlands.classes.interfaces;

import org.unitedlands.unitedlands.classes.metadata.MetaDataField;

public interface MetadataHolder {
    void addMetadata(MetaDataField<?> field);
    MetaDataField<?> getMetadata(String key);
    void saveMetadata();
}
