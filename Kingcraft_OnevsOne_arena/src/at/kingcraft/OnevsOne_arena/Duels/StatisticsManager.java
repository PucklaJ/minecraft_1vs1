package at.kingcraft.OnevsOne_arena.Duels;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import MySQL.MySQL;

public class StatisticsManager
{
	private static HashMap<UUID,Statistics> statistics;
	
	public static void setup()
	{
		statistics = new HashMap<>();
		
		try
		{
			PreparedStatement ps = MySQL.getInstance().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Duel_Statistics (UUID VARCHAR(100),Statistics VARCHAR(100))");
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void loadStatistics(UUID u,boolean createNew)
	{
		try
		{
			PreparedStatement ps = MySQL.getInstance().getConnection().prepareStatement("SELECT Statistics FROM Duel_Statistics WHERE UUID = ?");
			ps.setString(1, u.toString());
			
			ResultSet rs = ps.executeQuery();
			
			Statistics stats = null;
			
			while(rs.next())
			{
				stats = Statistics.fromString(rs.getString(1));
				break;
			}
			
			if(createNew && stats == null)
			{
				stats = new Statistics(0, 0, 0, 0, 0, 0,100);
			}
			
			stats.setELO(Duel.getELO(u));
			
			if(stats != null)
				statistics.put(u, stats);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void deleteStatistics(Player p)
	{
		deleteStatistics(p.getUniqueId());
	}
	
	public static void deleteStatistics(UUID u)
	{
		if(statistics.get(u) != null)
			statistics.remove(u);
	}
	
	public static Statistics getStatistics(Player p,boolean createNew)
	{
		return getStatistics(p.getUniqueId(),createNew);
	}
	
	public static Statistics getStatistics(UUID u,boolean createNew)
	{
		if(statistics.get(u) != null)
		{
			statistics.remove(u);
		}
		
		loadStatistics(u,createNew);
		
		return statistics.get(u);
	}
}
