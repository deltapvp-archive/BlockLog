package net.deltapvp.blocklog.listener;

import net.deltapvp.blocklog.BlockLog;
import net.deltapvp.blocklog.model.BlockLogThing;
import net.deltapvp.blocklog.profile.Profile;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayerListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();
        Profile profile = Profile.getFromUuid(player.getUniqueId());
        BlockLogThing thing = new BlockLogThing(block.getType(), block.getLocation(), System.currentTimeMillis());
        List<BlockLogThing> thingList = profile.blocksBroken;
        thingList.add(thing);
        profile.blocksBroken = thingList;
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        Profile profile = new Profile(event.getUniqueId(), event.getName());
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Profile profile = Profile.getFromUuid(event.getPlayer().getUniqueId());
        profile.save(false);
        Profile.profiles.remove(event.getPlayer().getUniqueId());
    }
}
