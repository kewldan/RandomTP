package com.ylouscraft.rtp;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	Logger log = Bukkit.getLogger();
	String prefix = "[Random TP]";
	Random rand;
	
	public void onEnable() {
		rand = new Random();
		print("Plugin enabled!");
		File cfg = new File(getDataFolder() + File.separator + "config.yml");
		if(!cfg.exists()) {
			print("Config created");
			getConfig().options().copyDefaults(true);
			saveDefaultConfig();
		}else {
			print("Config loaded");
		}
	}
	
	public void onDisable() {
		print("Plugin disabled!");
	}
	
	public void print(String t) {
		log.info(prefix + " " + t);
	}
	
	public Player getPlayerInMyWorld(World myWorld, Player iam) {
		ArrayList<Player> players = new ArrayList<Player>(Bukkit.getOnlinePlayers());
		int ind;
		while(true) {
			if(players.size() == 0) return null; 
			ind = rand.nextInt(players.size());
			if(!players.get(ind).equals(iam)) {
				if(players.get(ind).getWorld().equals(myWorld)) {
					return players.get(ind);
				}
			}
			players.remove(ind);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	    if(command.getName().equalsIgnoreCase("rtpn")){
	    	if (sender.getName().equals("CONSOLE")) {
	    		sender.sendMessage(getConfig().getString("messages.console"));
	    		return true;
	    	}
	    	sender.sendMessage(getConfig().getString("messages.before").replace('&', '§'));
	    	int distance = rand.nextInt(getConfig().getInt("distance-min") + getConfig().getInt("distance-max")) + getConfig().getInt("distance-min");
    		float angle = (float) ((rand.nextFloat()*2f-1f)*Math.PI);
    		Player sp = (Player) sender;
    		Player nearPlayer = getPlayerInMyWorld(sp.getWorld(), sp);
    		int x, y, z;
    		
	    	if(nearPlayer == null) {
	    		
	    		x = rand.nextInt(getConfig().getInt("world-size")) - getConfig().getInt("world-size")/2;
	    		z = rand.nextInt(getConfig().getInt("world-size")) - getConfig().getInt("world-size")/2;
	    		y = sp.getWorld().getHighestBlockYAt(x, z);
	    		
	    		sp.teleport(new Location(sp.getWorld(), x, y, z));
	    		sender.sendMessage(getConfig().getString("messages.after").replace('&', '§'));
	    	}else {
	    		Location near = nearPlayer.getLocation();
	    		
	    		x = (int) Math.round(Math.cos(angle) * distance + near.getX());
	    		z = (int) Math.round(Math.sin(angle) * distance + near.getZ());
	    		y = sp.getWorld().getHighestBlockYAt(x, z);
	    		
	    		sp.teleport(new Location(sp.getWorld(), x, y, z));
	    		sender.sendMessage(getConfig().getString("messages.after_near_player").replace('&', '§').replace("{NEAR}", nearPlayer.getDisplayName()).replace("{DIST}", String.valueOf(distance)));
	    	}
	    	return true;
	    }
	    return false;
	}
}
