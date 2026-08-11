package org.unitedlands.unitedlands.utils;

import org.bukkit.Location;
import org.unitedlands.unitedlands.classes.GeopolAttribute;
import org.unitedlands.unitedlands.classes.GeopolAttributeModifier;
import org.unitedlands.unitedlands.classes.metadata.BooleanMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.DoubleMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.FloatMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.IntegerMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.LocationMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.LongMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.MetaDataField;
import org.unitedlands.unitedlands.classes.metadata.StringMetaDataField;
import org.unitedlands.unitedlands.utils.serializers.GeopolAttributeDeserializer;
import org.unitedlands.unitedlands.utils.serializers.GeopolAttributeModifierDeserializer;
import org.unitedlands.unitedlands.utils.serializers.GeopolAttributeModifierSerializer;
import org.unitedlands.unitedlands.utils.serializers.GeopolAttributeSerializer;
import org.unitedlands.unitedlands.utils.serializers.LocationDeserializer;
import org.unitedlands.unitedlands.utils.serializers.LocationSerializer;
import org.unitedlands.unitedlands.utils.serializers.MetaDataFieldDeserializer;
import org.unitedlands.unitedlands.utils.serializers.MetaDataFieldSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JsonUtils {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(Location.class, new LocationSerializer())
            .registerTypeAdapter(Location.class, new LocationDeserializer())

            .registerTypeAdapter(GeopolAttribute.class, new GeopolAttributeSerializer())
            .registerTypeAdapter(GeopolAttribute.class, new GeopolAttributeDeserializer())
            .registerTypeAdapter(GeopolAttributeModifier.class, new GeopolAttributeModifierSerializer())
            .registerTypeAdapter(GeopolAttributeModifier.class, new GeopolAttributeModifierDeserializer())

            .registerTypeAdapter(MetaDataField.class, new MetaDataFieldSerializer())
            .registerTypeAdapter(MetaDataField.class, new MetaDataFieldDeserializer())

            .registerTypeAdapter(StringMetaDataField.class, new MetaDataFieldSerializer())
            .registerTypeAdapter(IntegerMetaDataField.class, new MetaDataFieldSerializer())
            .registerTypeAdapter(BooleanMetaDataField.class, new MetaDataFieldSerializer())
            .registerTypeAdapter(DoubleMetaDataField.class, new MetaDataFieldSerializer())
            .registerTypeAdapter(FloatMetaDataField.class, new MetaDataFieldSerializer())
            .registerTypeAdapter(LocationMetaDataField.class, new MetaDataFieldSerializer())
            .registerTypeAdapter(LongMetaDataField.class, new MetaDataFieldSerializer())

            .registerTypeAdapter(StringMetaDataField.class, new MetaDataFieldDeserializer())
            .registerTypeAdapter(IntegerMetaDataField.class, new MetaDataFieldDeserializer())
            .registerTypeAdapter(BooleanMetaDataField.class, new MetaDataFieldDeserializer())
            .registerTypeAdapter(DoubleMetaDataField.class, new MetaDataFieldDeserializer())
            .registerTypeAdapter(FloatMetaDataField.class, new MetaDataFieldDeserializer())
            .registerTypeAdapter(LocationMetaDataField.class, new MetaDataFieldDeserializer())
            .registerTypeAdapter(LongMetaDataField.class, new MetaDataFieldDeserializer())

            .create();

    public static <T> T deserialize(String jsonString, Class<T> clazz) {
        return gson.fromJson(jsonString, clazz);
    }

    public static <T> T deserialize(String jsonString, TypeToken<T> t) {
        return gson.fromJson(jsonString, t);
    }

    public static String serialize(Object obj) {
        return gson.toJson(obj);
    }

}
