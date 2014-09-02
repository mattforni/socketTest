package com.example.sockettest;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class JsonPortDeserializer implements JsonDeserializer<Integer> {

	@Override
	public Integer deserialize(JsonElement serializedId, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		final JsonObject jsonObject = serializedId.getAsJsonObject();
		return jsonObject.get("PORT").getAsInt();
	}
}
