package org.unitedlands.unitedlands.utils.serializers;

import java.lang.reflect.Type;

import org.unitedlands.unitedlands.classes.GeopolAttributeModifier;
import org.unitedlands.unitedlands.classes.GeopolAttributeModifier.Mode;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

public class GeopolAttributeModifierDeserializer implements JsonDeserializer<GeopolAttributeModifier> {
    @Override
    public GeopolAttributeModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        var jsonObj = json.getAsJsonObject();
        var attributeKey = jsonObj.get("attributeKey").getAsString();
        var modifierKey = jsonObj.get("modifierKey").getAsString();
        var mode = Mode.valueOf(jsonObj.get("mode").getAsString());
        var valueModifier = jsonObj.get("valueModifier").getAsDouble();
        var minValueModifier = jsonObj.get("minValueModifier").getAsDouble();
        var maxValueModifier = jsonObj.get("maxValueModifier").getAsDouble();
        var dailyChangeModifier = jsonObj.get("dailyChangeModifier").getAsDouble();
        return new GeopolAttributeModifier(attributeKey, modifierKey, mode, valueModifier, minValueModifier, maxValueModifier, dailyChangeModifier);
    }
}
