package com.endercrest.colorcube.events;

import com.endercrest.colorcube.ColorCube;
import com.endercrest.colorcube.GameManager;
import com.endercrest.colorcube.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitScheduler;

public class PlayerRespawnListener implements Listener {

    int id;
    Player p;
    int spawnID;

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        p = event.getPlayer();
        if(GameManager.getInstance().isPlayerActive(p)){
            id = GameManager.getInstance().getPlayerGameID(p);
            for(int i: GameManager.getInstance().getGame(id).getSpawns().keySet()){
                spawnID = i;
                if(GameManager.getInstance().getGame(id).getSpawns().get(i) == p){
                    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                    scheduler.scheduleSyncDelayedTask(ColorCube.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            p.teleport(SettingsManager.getInstance().getSpawnPoint(id, spawnID));
                        }
                    }, 1L);
                }
            }
        }
    }
}