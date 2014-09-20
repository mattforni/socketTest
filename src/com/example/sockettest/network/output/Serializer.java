package com.example.sockettest.network.output;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Serializer {
    public static final String CODE_KEY = "CODE";
    public static final String ID_KEY = "ID";

    public static final JsonElement publishClientId(final String id) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(CODE_KEY, PublishClientId.CODE);
        jsonObject.addProperty(ID_KEY, id);
        return jsonObject;
    }
}
