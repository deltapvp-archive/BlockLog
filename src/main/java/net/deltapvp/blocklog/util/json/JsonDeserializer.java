package net.deltapvp.blocklog.util.json;

import com.google.gson.JsonObject;

public interface JsonDeserializer<T> {

    T deserialize(JsonObject object);

}
