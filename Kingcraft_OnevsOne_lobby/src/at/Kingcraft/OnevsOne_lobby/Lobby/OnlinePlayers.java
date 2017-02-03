package at.Kingcraft.OnevsOne_lobby.Lobby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Scoreboard.MyScoreboardManager;

public class OnlinePlayers
{

	private static int prevNumOnlineThisServer = -1;
	private static int prevNumOnlineAllServer = -1;
	
	public static void setup()
	{
		try
		{
			MainClass.getInstance().getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Duel_Players (Server VARCHAR(1000),NumOnline INT(255))").executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return;
		}
		
		update();
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(MainClass.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				
			}
		}, 20*2, 20*2);
	}
	
	public static void deleteFromMySQL()
	{
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("DELETE FROM Duel_Players WHERE Server = ?");
			ps.setString(1, MainClass.getInstance().serverName);
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static int getNumOnline()
	{
		int online = 0;
		
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT NumOnline FROM Duel_Players");
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				online += rs.getInt(1);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		prevNumOnlineAllServer = online;
		
		return online;
	}
	
	private static void updateNumPlayers()
	{
		int nowOnline = Bukkit.getOnlinePlayers().size();
		
		if(MainClass.getInstance().serverName.equals("null"))
			return;
		
		if(nowOnline != prevNumOnlineThisServer)
		{
			prevNumOnlineThisServer = nowOnline;
			try
			{
				PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM Duel_Players WHERE Server = ?");
				ps.setString(1, MainClass.getInstance().serverName);
				
				ResultSet rs = ps.executeQuery();
				
				
				
				while(rs.next())
				{
					ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("UPDATE Duel_Players SET NumOnline = ? WHERE Server = ?");
					ps.setInt(1, nowOnline);
					ps.setString(2, MainClass.getInstance().serverName);
					
					ps.executeUpdate();
					return;
				}
				
				ps.close();
				
				ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("INSERT INTO Duel_Players (Server,NumOnline) VALUES (?,?)");
				ps.setString(1, MainClass.getInstance().serverName);
				ps.setInt(2, nowOnline);
				ps.executeUpdate();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	private static void update()
	{
		Bukkit.getScheduler().runTaskTimer(MainClass.getInstance(), new Runnable() {
			
			@Override
			public void run()
			{
				updateNumPlayers();
				
				if(prevNumOnlineAllServer == getNumOnline())
					return;
				
				for(Player p : Bukkit.getOnlinePlayers())
					MyScoreboardManager.updateScoreboard(p);
			}
		}, 20*2, 20*2);
	}
}
