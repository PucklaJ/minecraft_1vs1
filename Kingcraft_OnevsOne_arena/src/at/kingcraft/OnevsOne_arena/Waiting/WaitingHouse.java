package at.kingcraft.OnevsOne_arena.Waiting;


import org.bukkit.Bukkit;
import org.bukkit.Location;

import at.kingcraft.OnevsOne_arena.MainClass;

public class WaitingHouse
{
	private static Location waitingSpawn;
	private static MainClass plugin;
	
	public static void setup(MainClass plugin)
	{
		WaitingHouse.plugin = plugin;
		
		plugin.getConfig().options().header("Einstellungen für " + plugin.getName());
		
		LocationToConfig(new Location(Bukkit.getWorlds().get(0),0.0,0.0,0.0), true, plugin);
		
		
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
		
		waitingSpawn = new Location(Bukkit.getWorld(plugin.getConfig().getString("WaitingHouse.Spawn.World")),
									plugin.getConfig().getDouble("WaitingHouse.Spawn.x"),
									plugin.getConfig().getDouble("WaitingHouse.Spawn.y"),
									plugin.getConfig().getDouble("WaitingHouse.Spawn.z"));
	}
	
	public static void setWaitingSpawn(Location loc)
	{
		waitingSpawn = loc.clone();
		
		LocationToConfig(waitingSpawn, false,plugin);
	}
	
	private static void LocationToConfig(Location loc,boolean first,MainClass plugin)
	{
		if(first)
		{
			plugin.getConfig().addDefault("WaitingHouse.Spawn.World", loc.getWorld().getName());
			plugin.getConfig().addDefault("WaitingHouse.Spawn.x", loc.getX());
			plugin.getConfig().addDefault("WaitingHouse.Spawn.y", loc.getY());
			plugin.getConfig().addDefault("WaitingHouse.Spawn.z", loc.getZ());
		}
		else
		{
			plugin.getConfig().set("WaitingHouse.Spawn.World", loc.getWorld().getName());
			plugin.getConfig().set("WaitingHouse.Spawn.x", loc.getX());
			plugin.getConfig().set("WaitingHouse.Spawn.y", loc.getY());
			plugin.getConfig().set("WaitingHouse.Spawn.z", loc.getZ());
			
			plugin.saveConfig();
			plugin.reloadConfig();
		}
		
	}
	
	public static Location getWaitngSpawn()
	{
		return waitingSpawn;
	}

}
