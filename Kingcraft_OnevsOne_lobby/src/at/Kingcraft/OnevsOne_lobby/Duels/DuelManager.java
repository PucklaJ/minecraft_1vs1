package at.Kingcraft.OnevsOne_lobby.Duels;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.bukkit.entity.Player;


import MySQL.MySQL;
import at.Kingcraft.OnevsOne_lobby.Kits.Kit;
import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;
import at.Kingcraft.OnevsOne_lobby.WaitingSnake.Settings;
import at.Kingcraft.OnevsOne_lobby.WaitingSnake.WaitingSnakeUpload;


public class DuelManager {
	
	private static MySQL mysql;
	
	public static void setup(MySQL mysql)
	{
		DuelManager.mysql = mysql;
	}
	
	public static int sendDuelToSQL(Challenge c,String server,String arena,boolean dif,int tournament,boolean deleteChall,int mode)
	{
		
		// �berpr�fen ob Challenge schon vorhanden
		try
		{
			PreparedStatement ps1 = mysql.getConnection().prepareStatement("SELECT ChallengeID FROM Duel_Challenges WHERE ChallengeID = ?");
			ps1.setInt(1, c.ID);
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.first())
			{
				return -1;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		
		
		
		ArrayList<Player> challengers = c.getChallengers();
		ArrayList<Player> challenged = c.getChallenged();
		
		String uuid1="",uuid2="",names1="",names2="";
		int cID = c.ID;String serverName="";
		
		int u = 0;
		for(int i = 0;i<challengers.size();i++)
		{
			uuid1 += challengers.get(i).getUniqueId().toString() + "|";
			names1 += challengers.get(i).getDisplayName() + "|";
			serverName+=c.getServer(u) + "|";
			u++;
		}
		
		for(int i = 0;i<challenged.size();i++)
		{
			uuid2 += challenged.get(i).getUniqueId().toString() + "|";
			names2 += challenged.get(i).getDisplayName() + "|";
			serverName+=c.getServer(u) + "|";
			u++;
		}
		
		
		
		if(mysql.isConnected())
		{
			try
			{
				 PreparedStatement ps = mysql.getConnection().prepareStatement("INSERT INTO Duel_Challenges (ChallengerUUID,ChallengedUUID,ChallengerName,ChallengedName,ArenaName,ChallengeID,ServerName,ArenaServer,Kit,Tournament,Mode,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setString(1, uuid1);
				ps.setString(2, uuid2);
				ps.setString(3, names1);
				ps.setString(4, names2);
				ps.setString(5, arena);
				ps.setInt(6, cID);
				ps.setString(7, serverName);
				ps.setString(8, server);
				
				
				if(!dif)
				{
					
					Kit kit = KitManager.getChoosenKit(challengers.get(0)).getKit();
					
					ps.setString(9, kit.itemsToString());
				}
				else
				{
					ps.setString(9, "�diff�");
				}
				ps.setInt(10, tournament);
				ps.setInt(11, mode);
				ps.setInt(12, c.getTime());
				
				ps.executeUpdate();
				
				if(deleteChall)
				{
					ChallangeManager.deleteChallenge(challengers.get(0), null, c, false, false, false, false);
				}
				
				return 1;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		return -2;
		
	}
	
	public static int sendDuelToSQL(Challenge c,Kit kit,String server,String arena,boolean dif,int tournament,int mode)
	{
		
		// �berpr�fen ob Challenge schon vorhanden
		try
		{
			PreparedStatement ps1 = mysql.getConnection().prepareStatement("SELECT ChallengeID FROM Duel_Challenges WHERE ChallengeID = ?");
			ps1.setInt(1, c.ID);
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.first())
			{
				return -1;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		
		
		
		ArrayList<Player> challengers = c.getChallengers();
		ArrayList<Player> challenged = c.getChallenged();
		
		String uuid1="",uuid2="",names1="",names2="";
		int cID = c.ID;String serverName="";
		
		int u = 0;
		for(int i = 0;i<challengers.size();i++)
		{
			uuid1 += challengers.get(i).getUniqueId().toString() + "|";
			names1 += challengers.get(i).getDisplayName() + "|";
			serverName+=c.getServer(u) + "|";
			u++;
		}
		
		for(int i = 0;i<challenged.size();i++)
		{
			uuid2 += challenged.get(i).getUniqueId().toString() + "|";
			names2 += challenged.get(i).getDisplayName() + "|";
			serverName+=c.getServer(u) + "|";
			u++;
		}
		
		
		
		if(mysql.isConnected())
		{
			try
			{
				 PreparedStatement ps = mysql.getConnection().prepareStatement("INSERT INTO Duel_Challenges (ChallengerUUID,ChallengedUUID,ChallengerName,ChallengedName,ArenaName,ChallengeID,ServerName,ArenaServer,Kit,Tournament,Mode,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setString(1, uuid1);
				ps.setString(2, uuid2);
				ps.setString(3, names1);
				ps.setString(4, names2);
				ps.setString(5, arena);
				ps.setInt(6, cID);
				ps.setString(7, serverName);
				ps.setString(8, server);
				
				
				if(!kit.isDif())
				{
					ps.setString(9, kit.itemsToString());
				}
				else
				{
					ps.setString(9, "�diff�");
				}
				ps.setInt(10, tournament);
				ps.setInt(11, mode);
				ps.setInt(12, c.getTime());
				
				
				
				ps.executeUpdate();
				
				return 1;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		return -2;
		
	}
	
	public static int sendDuelToSQL(ArrayList<WaitingSnakeUpload> challengers,ArrayList<WaitingSnakeUpload> challenged,int cID,int tournamentID,Settings set,String kit,String[] serverNames,String server,String arena,int mode,int time)
	{
		
		// �berpr�fen ob Challenge schon vorhanden
		try
		{
			PreparedStatement ps1 = mysql.getConnection().prepareStatement("SELECT ChallengeID FROM Duel_Challenges WHERE ChallengeID = ?");
			ps1.setInt(1, cID);
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.first())
			{
				return -1;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		
		
	
		
		String uuid1="",uuid2="",names1="",names2="";
		String serverName="";
		
		int u = 0;
		for(int i = 0;i<challengers.size();i++)
		{
			uuid1 += challengers.get(i).uuid.toString() + "|";
			names1 += challengers.get(i).name + "|";
			serverName+=serverNames[u] + "|";
			u++;
		}
		
		for(int i = 0;i<challenged.size();i++)
		{
			uuid2 += challenged.get(i).uuid.toString() + "|";
			names2 += challenged.get(i).name + "|";
			serverName+=serverNames[u] + "|";
			u++;
		}
		
		
		
		if(mysql.isConnected())
		{
			try
			{
				 PreparedStatement ps = mysql.getConnection().prepareStatement("INSERT INTO Duel_Challenges (ChallengerUUID,ChallengedUUID,ChallengerName,ChallengedName,ArenaName,ChallengeID,ServerName,ArenaServer,Kit,Tournament,Mode,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setString(1, uuid1);
				ps.setString(2, uuid2);
				ps.setString(3, names1);
				ps.setString(4, names2);
				ps.setString(5, arena);
				ps.setInt(6, cID);
				ps.setString(7, serverName);
				ps.setString(8, server);
				
				
				if(set != null && set.getKitMode() == Settings.DIF_KIT_MODE)
				{
					ps.setString(9, "�diff�");
				}
				else
				{
					ps.setString(9, kit);
				}
				ps.setInt(10, tournamentID);
				ps.setInt(11, mode);
				ps.setInt(12, time);
				
				
				
				
				ps.executeUpdate();
				
				return 1;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		return -2;
		
	}
}