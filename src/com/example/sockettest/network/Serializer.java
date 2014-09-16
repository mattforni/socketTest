package com.example.sockettest.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Serializer {
    public static final String ID_KEY = "ID";

    public static final JsonElement serializeId(final String id) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ID_KEY, id);
        return jsonObject;
    }
}
