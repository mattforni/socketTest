package com.example.sockettest;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JsonFrameSerializer implements JsonSerializer<Integer> {

	@Override
	public JsonElement serialize(Integer size, Type arg1, JsonSerializationContext arg2) {
		final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SIZE", size);
        
        return jsonObject;
	}

}
