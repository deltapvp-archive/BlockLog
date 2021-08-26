package net.deltapvp.blocklog.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import net.deltapvp.blocklog.BlockLog;
import net.deltapvp.blocklog.model.BlockLogThing;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Profile {

    public final UUID uuid;
    public String name;
    public List<BlockLogThing> blocksBroken  = new ArrayList<>();
    public static Map<UUID, Profile> profiles = new ConcurrentHashMap<>();
    public boolean loaded = false;

    public Profile(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        load(true);

    }

    public static Profile getFromUuid(UUID uuid) {
        if (profiles.containsKey(uuid)) {
            return profiles.get(uuid);
        }
        return new Profile(uuid, Bukkit.getPlayer(uuid).getName());
    }

    public void save(boolean async) {
        if (async) {
            Bukkit.getScheduler().runTask(BlockLog.getInstance(), () -> save(false));
            return;
        }

        // save stuff
        Document doc = new Document();
        doc.put("name", name);
        doc.put("uuid", uuid.toString());

        JsonArray array = new JsonArray();

        for (BlockLogThing log : blocksBroken) {
            array.add(log.serialize());
        }
        doc.put("blocks", array.toString());

        BlockLog.getInstance().collection.replaceOne(Filters.eq("uuid", uuid.toString()), doc, new ReplaceOptions().upsert(true));

    }

    public void load(boolean async) {
        if (loaded) return;
        if (async) {
            Bukkit.getScheduler().runTask(BlockLog.getInstance(), () -> load(false));
            return;
        }

        Document document = BlockLog.getInstance().collection.find(Filters.eq("uuid", uuid.toString())).first();

        if (document != null) {
            if (name == null) {
                name = document.getString("name");
            }

            if (document.containsKey("blocks")) {
                JsonArray logList = JsonParser.parseString(document.getString("blocks")).getAsJsonArray();
                for (JsonElement yeet : logList) {
                    BlockLogThing log = BlockLogThing.deserialize(yeet.getAsJsonObject());

                    this.blocksBroken.add(log);
                }
            }

            this.loaded = true;
            profiles.put(this.uuid, this);
        }
    }
}
