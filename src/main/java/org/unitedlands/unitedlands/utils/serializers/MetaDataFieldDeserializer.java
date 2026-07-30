package org.unitedlands.unitedlands.utils.serializers;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import org.unitedlands.unitedlands.classes.metadata.BooleanMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.DoubleMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.FloatMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.IntegerMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.LongMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.MetaDataField;
import org.unitedlands.unitedlands.classes.metadata.StringMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.UuidListMetaDataField;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

public class MetaDataFieldDeserializer implements JsonDeserializer<MetaDataField<?>> {

    @Override
    public MetaDataField<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        var obj = json.getAsJsonObject();
        var key = obj.get("key").getAsString();
        var dataType = obj.get("dataType").getAsString();
        var showInScreens = obj.get("showInScreens").getAsBoolean();

        String label = null;
        var labelElement = obj.get("label");
        if (labelElement != null)
            label = labelElement.getAsString();

        if (dataType.equals("INTEGER")) {
            var value = obj.get("value").getAsInt();
            return new IntegerMetaDataField(key, value, label, showInScreens);
        } else if (dataType.equals("STRING")) {
            var value = obj.get("value").getAsString();
            return new StringMetaDataField(key, value, label, showInScreens);
        } else if (dataType.equals("BOOLEAN")) {
            var value = obj.get("value").getAsBoolean();
            return new BooleanMetaDataField(key, value, label, showInScreens);
        } else if (dataType.equals("DOUBLE")) {
            var value = obj.get("value").getAsDouble();
            return new DoubleMetaDataField(key, value, label, showInScreens);
        } else if (dataType.equals("FLOAT")) {
            var value = obj.get("value").getAsFloat();
            return new FloatMetaDataField(key, value, label, showInScreens);
        } else if (dataType.equals("LONG")) {
            var value = obj.get("value").getAsLong();
            return new LongMetaDataField(key, value, label, showInScreens);
        } else if (dataType.equals("UUIDLIST")) {
            var valueStr = obj.get("value").getAsString();
            var value = Arrays.stream(valueStr.split(";")).map(c -> UUID.fromString(c)).collect(Collectors.toList());
            return new UuidListMetaDataField(key, value, label, showInScreens);
        } 
        return null;
    }

}
