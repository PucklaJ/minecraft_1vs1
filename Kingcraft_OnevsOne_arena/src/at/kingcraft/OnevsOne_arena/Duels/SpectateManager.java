package at.kingcraft.OnevsOne_arena.Duels;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

import MySQL.MySQL;

public class SpectateManager
{	
	private static String compressForMySQL(ArrayList<Player> p)
	{
		String str = "";
		
		for(int i = 0;i<p.size();i++)
		{
			str += p.get(i).getUniqueId().toString() + (i+1==p.size() ? "" : ";");
		}
		
		return str;
	}
	
	private static String compressForMySQL(ArrayList<UUID> p,boolean b)
	{
		String str = "";
		
		for(int i = 0;i<p.size();i++)
		{
			str += p.get(i).toString() + (i+1==p.size() ? "" : ";");
		}
		
		return str;
	}
	
	public static void uploadToMySQL(ArrayList<Player> p1,ArrayList<Player> p2,String server,int id)
	{
		try
		{
			String u1 = compressForMySQL(p1);
			String u2 = compressForMySQL(p2);
			PreparedStatement ps = MySQL.getInstance().getConnection().prepareStatement("DELETE FROM Duel_SpectateDuels WHERE P1 = ? AND P2 = ? AND Server = ?");
			ps.setString(1, u1);
			ps.setString(2, u2);
			ps.setString(3, server);
			ps.executeUpdate();
			
			ps = MySQL.getInstance().getConnection().prepareStatement("INSERT INTO Duel_SpectateDuels (P1,P2,Server) VALUES (?,?,?)");
			ps.setString(1, u1);
			ps.setString(2, u2);
			ps.setString(3, server);
			ps.executeUpdate();
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void uploadToMySQL(ArrayList<UUID> p1,ArrayList<UUID> p2,String server,int id,boolean b)
	{
		try
		{
			String u1 = compressForMySQL(p1,true);
			String u2 = compressForMySQL(p2,true);
			PreparedStatement ps = MySQL.getInstance().getConnection().prepareStatement("DELETE FROM Duel_SpectateDuels WHERE ID = ?");
			ps.setInt(1, id);
			ps.executeUpdate();
			
			ps = MySQL.getInstance().getConnection().prepareStatement("INSERT INTO Duel_SpectateDuels (P1,P2,Server,ID) VALUES (?,?,?,?)");
			ps.setString(1, u1);
			ps.setString(2, u2);
			ps.setString(3, server);
			ps.setInt(4, id);
			ps.executeUpdate();
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void deleteFromMySQL(int id)
	{
		try
		{
			PreparedStatement ps = MySQL.getInstance().getConnection().prepareStatement("DELETE FROM Duel_SpectateDuels WHERE ID = ?");
			ps.setInt(1, id);
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
