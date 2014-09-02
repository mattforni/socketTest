package com.example.sockettest;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JsonCodeSerializer implements JsonSerializer<Byte> {

	public JsonElement serialize(Byte code, Type arg1, JsonSerializationContext arg2) {
		final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("CODE", code);
        return jsonObject;
	}
}
