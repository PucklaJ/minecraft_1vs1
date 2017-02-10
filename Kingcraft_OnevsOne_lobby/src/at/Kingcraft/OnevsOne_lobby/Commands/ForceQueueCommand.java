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
			OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
			
			if(op == null)
			{
				p.sendMessage(Messages.playerDoesntExists(args[0]));
				return true;
			}
			
			if(p.getUniqueId().equals(op.getUniqueId()))
			{
				p.sendMessage(ChatColor.RED + "You cannot forcequeue yourself");
				return true; 
			}
			
			try
			{
				PreparedStatement ps;
				
				ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("DELETE FROM Duel_ForceQueue WHERE UUID = ?");
				ps.setString(1, p.getUniqueId().toString());
				ps.executeUpdate();
				
				ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("INSERT INTO Duel_ForceQueue (UUID,Player) VALUES (?,?)");
				ps.setString(1, p.getUniqueId().toString());
				ps.setString(2, op.getUniqueId().toString());
				ps.executeUpdate();
				
				p.sendMessage(ChatColor.YELLOW + "You are forcequeueing " + ChatColor.GREEN + op.getName());
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
