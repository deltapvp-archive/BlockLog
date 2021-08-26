package net.deltapvp.blocklog;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.deltapvp.blocklog.listener.PlayerListener;
import net.deltapvp.blocklog.model.BlockLogThing;
import net.deltapvp.blocklog.profile.Profile;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlockLog extends JavaPlugin {

    private static BlockLog instance;
    public MongoDatabase mongoDatabase;
    public MongoCollection<Document> collection;

    public static BlockLog getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        mongo();
        getCommand("blocklog").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = Bukkit.getPlayer(args[0]);
        Profile profile = Profile.getFromUuid(player.getUniqueId());

        for (BlockLogThing log : profile.blocksBroken) {
            sender.sendMessage("Location: " + log.getLocation());
            sender.sendMessage("Type: " + log.getMaterial().name());
            sender.sendMessage("Time: " + log.getTime());
        }

        return false;
    }

    public void mongo() {
        mongoDatabase = new MongoClient(getConfig().getString("MONGO.HOST"),
                getConfig().getInt("MONGO.PORT")).getDatabase(getConfig().getString("MONGO.DB", "BlockLog"));
        collection = mongoDatabase.getCollection("profiles");
    }
}
