package at.Kingcraft.OnevsOne_lobby.Arenas;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import MySQL.MySQL;
import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Lobby.LobbyListener;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messenger;

public class ArenaManager {
	
	private static MySQL mysql;
	private static String prefix;
	
	public static String giveArena()
	{
		prefix = MainClass.getInstance().getConfig().getString("Arenaserver.Prefix");
		
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT * FROM Duel_Servers");
			ResultSet rs = ps.executeQuery();
			
			String name = "";
			int free = 0;
			
			while(rs.next())
			{
				free = rs.getInt(2);
				if(free == 0)
				{
					continue;
				}
				name = rs.getString(1);
				if(!(name.length() < prefix.length()) && name.startsWith(prefix))
				return name;
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return "";
		
	}
	
	public static String getPrefix()
	{
		return prefix;
	}
	
	public static String getPrefix(String str)
	{
		int pos = -1;
		
		for(int i = str.length()-1;i>=0;i--)
		{
			if(str.charAt(i) == '-')
			{
				pos = i;
				break;
			}
		}
		
		return pos == -1 ? "" : str.substring(0, pos);
	}
	
	public static void updateServers(String[] servers)
	{
		prefix = MainClass.getInstance().getConfig().getString("Arenaserver.Prefix");
		
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT Name FROM Duel_Servers");
			
			ArrayList<String> allServers = new ArrayList<>();
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				allServers.add(rs.getString(1));
			}
			
			for(int i = 0;i<allServers.size();i++)
			{
				if(allServers.get(i).length() < prefix.length() || !allServers.get(i).startsWith(prefix))
				{
					continue;
				}
				
				boolean contains = false;
				for(int u = 0;u<servers.length;u++)
				{
					if(servers[u].equals(allServers.get(i)))
					{
						contains = true;
						break;
					}
				}
				
				if(!contains)
				{
					ps = mysql.getConnection().prepareStatement("DELETE FROM Duel_Servers WHERE Name = ?");
					ps.setString(1, allServers.get(i));
					
					ps.executeUpdate();
				}
				
			}
			
			for(int i = 0;i<servers.length;i++)
			{
				if(servers[i].length() < prefix.length() || !servers[i].startsWith(prefix))
				{
					continue;
				}
				
				if(!allServers.contains(servers[i]))
				{
					ps = mysql.getConnection().prepareStatement("INSERT INTO Duel_Servers (Name,free) VALUES (?,?)");
					ps.setString(1, servers[i]);
					ps.setInt(2, 1);
					
					ps.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean check(int amount)
	{
		prefix = MainClass.getInstance().getConfig().getString("Arenaserver.Prefix");
		
		if(mysql.isConnected())
		{
			try
			{
				PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT * FROM Duel_Servers");
				ResultSet rs = ps.executeQuery();
				
				int servers = 0;
				while(rs.next())
				{
					if(rs.getInt(2) == 1 && !(rs.getString(1).length() < prefix.length()) && rs.getString(1).startsWith(prefix))
					{
						servers++;
					}
				}
				
				if(servers >= amount)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		return false;
	}


	public static void moveToUsed(String server)
	{
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE Duel_Servers SET free = 0 WHERE Name = ?");
			ps.setString(1, server);
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void moveToFree(String server)
	{
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE Duel_Servers SET free = 1 WHERE Name = ?");
			ps.setString(1, server);
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static void sendServerToMySQL(String server,int free)
	{
		try
		{
			// Ask if already exists
			
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT * FROM Duel_Servers WHERE name = ?");
			ps.setString(1, server);
			ResultSet rs = ps.executeQuery();
			if(!rs.first())
			{
				ps = mysql.getConnection().prepareStatement("INSERT INTO Duel_Servers (Name,free) VALUES (?,?)");
				ps.setString(1, server);
				ps.setInt(2, free);
				ps.executeUpdate();
			}
			
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean teleportToArena(Player p,String server,boolean ovso)
	{
		moveToUsed(server);
		String[] args = new String[1];
		args[0] = server;
		
		if(ovso)
			LobbyListener.leftByDuel.add(p.getUniqueId());
		
		if(!LobbyListener.ovoCmdBlock.contains(p.getUniqueId()))
			LobbyListener.ovoCmdBlock.add(p.getUniqueId());
		
		return Messenger.sendMessage(p, "BungeeCord", "Connect", args);
	}
	
	public static boolean teleportToArena(OfflinePlayer op,String server,boolean ovso)
	{
		moveToUsed(server);
		String[] args = new String[2];
		args[1] = server;
		args[0] = op.getName();
		
		if(Bukkit.getServer().getOnlinePlayers().contains(op.getPlayer()))
		{
			if(ovso)
				LobbyListener.leftByDuel.add(op.getUniqueId());
			if(!LobbyListener.ovoCmdBlock.contains(op.getUniqueId()))
				LobbyListener.ovoCmdBlock.add(op.getUniqueId());
		}
		return Messenger.sendMessage(null, "BungeeCord", "ConnectOther", args);
	}
	
	public static boolean teleportToArena(String name,String server)
	{
		moveToUsed(server);
		String[] args = new String[2];
		args[1] = server;
		args[0] = name;
		return Messenger.sendMessage(null, "BungeeCord", "ConnectOther", args);
	}
	
	public static void setup(MySQL mysql)
	{
		ArenaManager.mysql = mysql;
		
		if(mysql.isConnected())
		{
			try
			{
				PreparedStatement ps = mysql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Duel_Servers (Name VARCHAR(100), free INT(1))");
				ps.executeUpdate();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static ArrayList<String> getServers(int free)
	{
		ArrayList<String> servers = new ArrayList<>();
		
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT Name FROM Duel_Servers WHERE free = ?");
			ps.setInt(1, free);
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				String server = rs.getString(1);
				if(server.startsWith(getPrefix()))
				{
					servers.add(server);
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return servers;
	}
}
