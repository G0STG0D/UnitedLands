package org.unitedlands.unitedlands.utils.serializers;

import org.unitedlands.unitedlands.classes.GeopolAttributeModifier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class GeopolAttributeModifierSerializer implements JsonSerializer<GeopolAttributeModifier> {

    @Override
    public JsonElement serialize(GeopolAttributeModifier src, Type typeOfSrc, JsonSerializationContext context) {
        var jsonAttributeModifier = new JsonObject();
        jsonAttributeModifier.addProperty("type", src.getType().toString());
        jsonAttributeModifier.addProperty("key", src.getKey());
        jsonAttributeModifier.addProperty("valueModifier", src.getValueModifier());
        jsonAttributeModifier.addProperty("minValueModifier", src.getMinValueModifier());
        jsonAttributeModifier.addProperty("maxValueModifier", src.getMaxValueModifier());
        jsonAttributeModifier.addProperty("dailyChangeModifier", src.getDailyChangeModifier());
        return jsonAttributeModifier;
    }
}
