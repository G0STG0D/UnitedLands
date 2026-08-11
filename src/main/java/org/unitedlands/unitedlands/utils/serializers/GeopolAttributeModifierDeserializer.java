package org.unitedlands.unitedlands.utils.serializers;

import java.lang.reflect.Type;

import org.unitedlands.unitedlands.classes.GeopolAttributeModifier;
import org.unitedlands.unitedlands.classes.GeopolAttributeType;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

public class GeopolAttributeModifierDeserializer implements JsonDeserializer<GeopolAttributeModifier> {
    @Override
    public GeopolAttributeModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        var jsonObj = json.getAsJsonObject();
        var type = GeopolAttributeType.valueOf(jsonObj.get("type").getAsString());
        var key = jsonObj.get("key").getAsString();
        var valueModifier = jsonObj.get("valueModifier").getAsDouble();
        var minValueModifier = jsonObj.get("minValueModifier").getAsDouble();
        var maxValueModifier = jsonObj.get("maxValueModifier").getAsDouble();
        var dailyChangeModifier = jsonObj.get("dailyChangeModifier").getAsDouble();
        return new GeopolAttributeModifier(type, key, valueModifier, minValueModifier, maxValueModifier, dailyChangeModifier);
    }
}
