package at.Kingcraft.OnevsOne_lobby.Commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Lobby.LobbyListener;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import net.md_5.bungee.api.ChatColor;

public class KitPlaceCommand implements CommandExecutor {

	private MainClass plugin;
	
	public KitPlaceCommand(MainClass plugin)
	{
		this.plugin = plugin;
	}
	
	private void setMinKit(Location loc)
	{
		plugin.getConfig().set("Kits.Place.x1", loc.getX());
		plugin.getConfig().set("Kits.Place.y1", loc.getY());
		plugin.getConfig().set("Kits.Place.z1", loc.getZ());
		
		plugin.saveConfig();
		plugin.reloadConfig();
	}
	
	private void setMaxKit(Location loc)
	{
		plugin.getConfig().set("Kits.Place.x2", loc.getX());
		plugin.getConfig().set("Kits.Place.y2", loc.getY());
		plugin.getConfig().set("Kits.Place.z2", loc.getZ());
		
		plugin.saveConfig();
		plugin.reloadConfig();
	}
	
	private void setMinNormal(Location loc)
	{
		plugin.getConfig().set("Kits.Place.Normal.x1", loc.getX());
		plugin.getConfig().set("Kits.Place.Normal.y1", loc.getY());
		plugin.getConfig().set("Kits.Place.Normal.z1", loc.getZ());
		
		plugin.saveConfig();
		plugin.reloadConfig();
	}
	
	private void setMaxNormal(Location loc)
	{
		plugin.getConfig().set("Kits.Place.Normal.x2", loc.getX());
		plugin.getConfig().set("Kits.Place.Normal.y2", loc.getY());
		plugin.getConfig().set("Kits.Place.Normal.z2", loc.getZ());
		
		plugin.saveConfig();
		plugin.reloadConfig();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only for Players");
			return true;
		}
		
		Player me = (Player)sender;
		
		if(cmd.getName().equalsIgnoreCase("setminkit"))
		{
			setMinKit(me.getLocation());
			me.sendMessage(Messages.minKitSet);
		}
		else if(cmd.getName().equalsIgnoreCase("setmaxkit"))
		{
			setMaxKit(me.getLocation());
			me.sendMessage(Messages.maxKitSet);
		}
		if(cmd.getName().equalsIgnoreCase("setminnormal"))
		{
			setMinNormal(me.getLocation());
			me.sendMessage(Messages.minKitSet);
		}
		else if(cmd.getName().equalsIgnoreCase("setmaxnormal"))
		{
			setMaxNormal(me.getLocation());
			me.sendMessage(Messages.maxKitSet);
		}
		else
		{
			return true;
		}
		
		if(cmd.getName().contains("kit"))
		{
			LobbyListener.setKitPlace();
		}
		else if(cmd.getName().contains("normal"))
		{
			LobbyListener.setNormalPlace();
		}
		
		
		
		
		
		
		return true;
	}

}
