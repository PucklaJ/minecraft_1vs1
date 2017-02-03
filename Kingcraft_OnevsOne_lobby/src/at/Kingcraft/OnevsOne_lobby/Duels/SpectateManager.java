package at.Kingcraft.OnevsOne_lobby.Duels;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import MySQL.MySQL;
import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Special.MenuManager;
import at.Kingcraft.OnevsOne_lobby.Special.SpectateMenu;

public class SpectateManager
{
	private static ArrayList<SpectateDuel> spectateDuels;
	
	public static void setup()
	{
		spectateDuels = new ArrayList<>();
		
		try
		{
			PreparedStatement ps = MySQL.getInstance().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Duel_SpectateDuels (P1 VARCHAR(1000),P2 VARCHAR(1000), Server VARCHAR(100), ID INT(100))");
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		startUpdate();
	}
	
	private static ArrayList<UUID> decodeFromMySQL(String str)
	{
		String[] str1 = str.split(";");
		ArrayList<UUID> uuids = new ArrayList<>();
		
		if(str.equals(""))
			return uuids;
		
		for(int i = 0;i<str1.length;i++)
		{
			UUID u = UUID.fromString(str1[i]);
			uuids.add(u);
		}
		
		return uuids;
	}
	
	private static String compressForMySQL(ArrayList<Player> p)
	{
		String str = "";
		
		for(int i = 0;i<p.size();i++)
		{
			str += p.get(i).getUniqueId().toString() + (i+1==p.size() ? "" : ";");
		}
		
		return str;
	}
	
	private static String compressForMySQL(ArrayList<UUID> p,boolean nothing)
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
			String u1 = p1 == null ? "" : compressForMySQL(p1);
			String u2 = p2 == null ? "" : compressForMySQL(p2);
			PreparedStatement ps = MySQL.getInstance().getConnection().prepareStatement("DELETE FROM Duel_SpectateDuels WHERE ID = ?");
			ps.setInt(1,id);
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
	
	public static void uploadToMySQL(ArrayList<UUID> p1uuid,ArrayList<UUID> p2uuid,String server,int id,boolean nothing)
	{
		try
		{
			String u1 = p1uuid == null ? "" : compressForMySQL(p1uuid,true);
			String u2 = p2uuid == null ? "" : compressForMySQL(p2uuid,true);
			PreparedStatement ps = MySQL.getInstance().getConnection().prepareStatement("DELETE FROM Duel_SpectateDuels WHERE ID = ?");
			ps.setInt(1,id);
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
	
	private static void loadDuels()
	{
		spectateDuels.clear();
		try
		{
			PreparedStatement ps = MySQL.getInstance().getConnection().prepareStatement("SELECT * FROM Duel_SpectateDuels");
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				spectateDuels.add(new SpectateDuel(decodeFromMySQL(rs.getString(1)), decodeFromMySQL(rs.getString(2)), rs.getString(3)));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void updateMenus()
	{
		SpectateMenu.spectateDuels.clear();
		SpectateMenu.spectateDuels = (ArrayList<SpectateDuel>) spectateDuels.clone();
		
		ArrayList<SpectateMenu> spm = MenuManager.getSpectateMenus();
		
		for(int i = 0;i<spm.size();i++)
		{
			spm.get(i).update();
		}
	}
	
	private static void startUpdate()
	{
		Bukkit.getScheduler().runTaskTimerAsynchronously(MainClass.getInstance(), new Runnable() {
			
			@Override
			public void run()
			{
				if(!Bukkit.getOnlinePlayers().isEmpty())
				{
					loadDuels();
					updateMenus();
				}
			}
		}, 20*1, 20*1);
	}
	
	public static ArrayList<SpectateDuel> getSpecDuels()
	{
		return spectateDuels;
	}
}
