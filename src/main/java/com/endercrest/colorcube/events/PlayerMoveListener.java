package com.endercrest.colorcube.events;

import com.endercrest.colorcube.ColorCube;
import com.endercrest.colorcube.MessageManager;
import com.endercrest.colorcube.game.Game;
import com.endercrest.colorcube.GameManager;
import com.endercrest.colorcube.logging.LoggingManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        final Player player = event.getPlayer();
        final List<Player> black = new ArrayList<Player>();
        int id = GameManager.getInstance().getPlayerGameID(player);
        if(GameManager.getInstance().getGame(id) != null){
            if(GameManager.getInstance().getGame(id).getStatus() == Game.Status.INGAME) {
                int teamID = GameManager.getInstance().getPlayerTeamID(player);
                Location loc = player.getLocation().subtract(0, 1, 0);
                if(loc.getBlock().getType() == Material.STAINED_CLAY) {
                    if(loc.getBlock().getData() != (byte) 15) {
                        changeBlock(id, loc.getBlock(), teamID);
                    }else{
                        if(!black.contains(player)) {
                            loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 0F, false, false);
                            Random random = new Random();
                            player.setVelocity(new Vector(random.nextDouble(), random.nextDouble() * 2, random.nextDouble()));
                            black.add(player);
                            player.setFallDistance(0);
                            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                            scheduler.scheduleSyncDelayedTask(ColorCube.getPlugin(), new Runnable() {
                                @Override
                                public void run() {
                                    black.remove(player);
                                }
                            }, 20L);
                        }
                    }
                }
            }
        }
    }

    public void changeBlock(int id, Block block, int team){
        byte data;
        switch(team) {
            case 0://Red Team
                data = 14;
                break;
            case 1://Blue Team
                data = 3;
                break;
            case 2://Green Team
                data = 5;
                break;
            case 3://Yellow Team
                data = 4;
                break;
            default:
                data = 0;
                break;
        }
        if(block.getData() != data){
            switch (block.getData()){
                case 14:
                    scoreManagement(id, team, 0, 1);
                    break;
                case 3:
                    scoreManagement(id ,team, 1, 1);
                    break;
                case 5:
                    scoreManagement(id, team, 2, 1);
                    break;
                case 4:
                    scoreManagement(id, team, 3, 1);
                    break;
                case 0:
                    scoreManagement(id, team, -1, 1);
                    break;
            }
            LoggingManager.getInstance().logBlockDestoryed(block);
            block.setData(data);
        }

    }

    public void scoreManagement(int id, int teamincrease, int teamdecrease, int amount){
        Game game = GameManager.getInstance().getGame(id);
        game.increaseScore(teamincrease, amount);
        game.decreaseScore(teamdecrease, amount);
    }
}