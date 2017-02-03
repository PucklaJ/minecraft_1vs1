package at.kingcraft.OnevsOne_setup;

import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import MySQL.MySQL;
import at.kingcraft.OnevsOne_setup.Commands.MapCommand;
import at.kingcraft.OnevsOne_setup.Commands.SetSpawnCommand;
import at.kingcraft.OnevsOne_setup.Maps.MapManager;
import net.md_5.bungee.api.ChatColor;

public class MainClass extends JavaPlugin {

	private MySQL mysql;
	private static MainClass instance = null;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only for Players");
			return true;
		}
		
		Player me = (Player)sender;
		
		if(command.getName().equalsIgnoreCase("spawn"))
		{
			if(args.length != 0)
			{
				return false;
			}
			
			me.teleport(me.getWorld().getSpawnLocation());
			
			
		}
		
		return true;
	}

	@Override
	public void onDisable() {
		super.onDisable();
		System.out.println("["+ getName() +"] Successfully disabled");
	}

	private void setupCommands()
	{
		getCommand("map").setExecutor(new MapCommand(this));
		getCommand("setspawn").setExecutor(new SetSpawnCommand());
	}
	
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		setupCommands();
		
		try
		{
			mysql = MySQL.getInstance() != null ? MySQL.getInstance() : new MySQL(this);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		MapManager.setup(this,mysql);
		
		getServer().getPluginManager().registerEvents(new MapListener(), this);
		
		System.out.println("["+ getName() +"] Successfully enabled");
		
		if(instance == null)
		{
			instance = this;
		}
		
	}

	public static MainClass getInstance()
	{
		return instance;
	}

	

}
