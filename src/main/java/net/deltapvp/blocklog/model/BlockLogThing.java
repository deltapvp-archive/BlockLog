package net.deltapvp.blocklog.model;

import com.google.gson.JsonObject;
import net.deltapvp.blocklog.util.json.JsonChain;
import net.deltapvp.blocklog.util.json.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Locale;
import java.util.UUID;

public class BlockLogThing {

    private final Material material;
    private final UUID uuid;
    private final Location location;
    private final long time;

    public BlockLogThing(UUID uuid, Material material, Location location, long time) {
        this.uuid = uuid;
        this.material = material;
        this.time = time;
        this.location = location;
    }

    public BlockLogThing(Material material, Location location, long time) {
        this.uuid = UUID.randomUUID();
        this.material = material;
        this.time = time;
        this.location = location;
    }

    public static BlockLogThing deserialize(JsonObject element) {
        return new BlockLogThing(UUID.fromString(element.get("uuid").getAsString()),
                Material.matchMaterial(element.get("material").getAsString()),
                LocationUtil.fromJson(element.get("location")),
                element.get("time").getAsLong());
    }

    public Material getMaterial() {
        return material;
    }

    public Location getLocation() {
        return location;
    }

    public long getTime() {
        return time;
    }

    public JsonObject serialize() {
        return new JsonChain()
                .addProperty("uuid", uuid.toString())
                .addProperty("material", material.name().toLowerCase(Locale.ROOT))
                .addProperty("time", time)
                .add("location", LocationUtil.toJson(location))
                .get();
    }
}
