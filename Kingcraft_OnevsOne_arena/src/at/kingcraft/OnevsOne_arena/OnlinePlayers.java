package at.kingcraft.OnevsOne_arena;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;

public class OnlinePlayers
{

	private static int prevNumOnlineThisServer = -1;
	
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
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(MainClass.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				updateNumPlayers();
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
	
	private static void updateNumPlayers()
	{
		int nowOnline = Bukkit.getOnlinePlayers().size();
		
		if(MainClass.getInstance().serverName.equals(""))
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
}
