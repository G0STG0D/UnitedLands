package org.unitedlands.unitedlands.utils.serializers;

import java.lang.reflect.Type;

import org.unitedlands.unitedlands.classes.metadata.BooleanMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.DoubleMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.FloatMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.IntegerMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.LongMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.MetaDataField;
import org.unitedlands.unitedlands.classes.metadata.StringMetaDataField;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MetaDataFieldSerializer implements JsonSerializer<MetaDataField<?>> {

    @Override
    public JsonElement serialize(MetaDataField<?> src, Type typeOfSrc, JsonSerializationContext context) {
        
        var json = new JsonObject();

        json.addProperty("key", src.getKey());
        json.addProperty("dataType", src.getDataType());
        json.addProperty("label", src.getLabel());
        json.addProperty("showInScreens", src.showInScreens());

        if (src instanceof StringMetaDataField typedSrc) {
            json.addProperty("value", typedSrc.getValue());
        } else if (src instanceof IntegerMetaDataField typedSrc) {
            json.addProperty("value", typedSrc.getValue());
        } else if (src instanceof BooleanMetaDataField typedSrc) {
            json.addProperty("value", typedSrc.getValue());
        } else if (src instanceof DoubleMetaDataField typedSrc) {
            json.addProperty("value", typedSrc.getValue());
        } else if (src instanceof FloatMetaDataField typedSrc) {
            json.addProperty("value", typedSrc.getValue());
        } else if (src instanceof LongMetaDataField typedSrc) {
            json.addProperty("value", typedSrc.getValue());
        }
        return json;
    }


}
