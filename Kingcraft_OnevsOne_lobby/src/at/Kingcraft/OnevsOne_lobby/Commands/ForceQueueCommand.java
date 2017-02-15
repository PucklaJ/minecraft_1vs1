package at.Kingcraft.OnevsOne_lobby.Commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import net.md_5.bungee.api.ChatColor;

public class ForceQueueCommand implements CommandExecutor {

	public static HashMap<UUID,UUID> getForceQueued()
	{
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM Duel_ForceQueue");
			ResultSet rs = ps.executeQuery();
			
			HashMap<UUID, UUID> forceQueue = new HashMap<>();
			
			while(rs.next())
			{
				forceQueue.put(UUID.fromString(rs.getString(2)), UUID.fromString(rs.getString(1)));
			}
			
			return forceQueue;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void remove(UUID uuid)
	{
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("DELETE FROM Duel_ForceQueue WHERE UUID = ?");
			ps.setString(1, uuid.toString());
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private Player getPlayer(String name)
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(p.getDisplayName().equals(name))
				return p;
		}
		
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only for Players!");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(args.length == 1)
		{
			UUID uuid = null;
			String name = "";
			
			
			Player p1 = getPlayer(args[0]);
			if(p1 == null)
			{
				if(args[0].equalsIgnoreCase("cancel"))
				{	
					remove(p.getUniqueId());
					
					p.sendMessage(Messages.forceQueueRemove);
					return true;
				}
				else
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
					
					if(op == null)
					{
						p.sendMessage(Messages.playerDoesntExists(args[0]));
						return true;
					}
					
					uuid = op.getUniqueId();
					name = op.getName();
				}
			}
			else
			{
				uuid = p1.getUniqueId();
				name = p1.getDisplayName();
			}
			
			
			
			if(p.getUniqueId().equals(uuid))
			{
				p.sendMessage(Messages.forceQueueSelf);
				return true; 
			}
			
			try
			{
				PreparedStatement ps;
				
				remove(p.getUniqueId());
				
				ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("INSERT INTO Duel_ForceQueue (UUID,Player) VALUES (?,?)");
				ps.setString(1, p.getUniqueId().toString());
				ps.setString(2, uuid.toString());
				ps.executeUpdate();
				
				p.sendMessage(Messages.forceQueueAdd(name));
			}
			catch (SQLException e) 
			{
				e.printStackTrace();
				p.sendMessage(ChatColor.RED + "An error accoured");
			}
			
			return true;
		}
		
		return false;
	}

}
