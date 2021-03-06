package com.endercrest.colorcube.events;

import com.endercrest.colorcube.*;
import com.endercrest.colorcube.game.Game;
import com.endercrest.colorcube.game.Powerup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerMoveListener implements Listener {

    private ColorCube plugin;

    public PlayerMoveListener(ColorCube plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        final Player player = event.getPlayer();
        final List<Player> black = new ArrayList<>();
        int id = GameManager.getInstance().getActivePlayerGameID(player);
        if(GameManager.getInstance().getGame(id) != null){
            Game game = GameManager.getInstance().getGame(id);
            if(game.getStatus() == Game.Status.IN_GAME) {
                Game.CCTeam team = game.getCCTeam(player);
                Location loc = player.getLocation().subtract(0, 1, 0);
                List<String> paintable = SettingsManager.getInstance().getPluginConfig().getStringList("paintable-blocks");
                paintable.remove("AIR");
                if(paintable.contains(loc.getBlock().getType().toString())){
                    if(game.getArena().containsBlock(loc)) {
                        if(loc.getBlock().getType().equals(Material.STAINED_CLAY)) {
                            //noinspection deprecation
                            if (loc.getBlock().getData() != (byte) 15) {
                                game.changeBlock(loc, team);
                            }
                        }else{
                            game.changeBlock(loc, team);
                        }
                    }
                }

                //Check for explosive blocks
                if(loc.getBlock().getType() == Material.STAINED_CLAY){
                    //noinspection deprecation
                    if(loc.getBlock().getData() == (byte) 15){
                        if (!black.contains(player)) {
                            loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 0F, false, false);
                            Random random = new Random();
                            player.setVelocity(new Vector(random.nextDouble(), random.nextDouble() * 2, random.nextDouble()));
                            black.add(player);
                            player.setFallDistance(0);
                            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                            scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    black.remove(player);
                                }
                            }, 20L);
                        }
                    }
                }

                //Check if at powerup location
                List<Powerup> remove = new ArrayList<>();
                if(game.getPowerups().size() > 0) {
                    for (Powerup powerup : game.getPowerups()) {
                        if(powerup.getLocation().getBlockX() == player.getLocation().getBlockX()){
                            if(powerup.getLocation().getBlockY() == player.getLocation().getBlockY()){
                                if(powerup.getLocation().getBlockZ() == player.getLocation().getBlockZ()){
                                    for(int i = 0; i < 9; i++){
                                        if(player.getInventory().getItem(i) == null){
                                            MessageManager.getInstance().sendFMessage("game.pickup", player, "type-" + PowerupManager.getInstance().getPowerupName(powerup.getType()));
                                            remove.add(powerup);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //Remove Powerup and give item to player
                if(!remove.isEmpty()){
                    for(Powerup pu: remove){
                        for(int i = 0; i < 9; i++){
                            if(player.getInventory().getItem(i) == null){
                                player.getInventory().setItem(i, pu.getType().getItem());
                                break;
                            }
                        }
                        game.removePowerup(pu);
                    }
                }
            }
        }
    }
}
