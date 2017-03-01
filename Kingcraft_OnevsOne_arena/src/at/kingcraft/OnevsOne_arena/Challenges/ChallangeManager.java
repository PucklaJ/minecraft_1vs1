package at.kingcraft.OnevsOne_arena.Challenges;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.MainClass;
import at.kingcraft.OnevsOne_arena.Messaging.Messenger;
import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_arena.Duels.DuelManager;
import at.kingcraft.OnevsOne_arena.Kits.Kit;
import at.kingcraft.OnevsOne_arena.Listener.DuelListener;

public class ChallangeManager {
	private static ArrayList<Challenge> challenges = new ArrayList<Challenge>();

	private static MainClass plugin;
	
	public static void setup(MainClass plugin)
	{
		ChallangeManager.plugin = plugin;
	}
	
	private static Challenge getChallenge(int id) {
		if (id == Challenge.NO_ID)
			return null;

		for (Challenge c : challenges)
		{
			if (c.ID == id)
				return c;
		}
		return null;
	}

	private static int isAlreadyIn(Player challenger1, Player challenged1) {
		for (int i = 0; i < challenges.size(); i++)
		{
			Challenge c = challenges.get(i);
			
			if (c.getChallengersUUID().contains(challenger1.getUniqueId())
				&& c.getChallengedUUID().contains(challenged1.getUniqueId()))
			{
				return c.ID;
			}
		}

		return Challenge.NO_ID;
	}
	
	public static void sendChallengeToMySQL(Challenge c,String arena,String server,String kit,int tournament,int mode,int time)
	{
		// Überprüfen ob Challenge schon vorhanden
				try
				{
					PreparedStatement ps1 = plugin.getMySQL().getConnection().prepareStatement("SELECT ChallengeID FROM Duel_Challenges WHERE ChallengeID = ?");
					ps1.setInt(1, c.ID);
					ResultSet rs1 = ps1.executeQuery();
					if(rs1.first())
					{
						return;
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
					serverName+=c.getPreviousServer(u) + "|";
					u++;
				}
				
				for(int i = 0;i<challenged.size();i++)
				{
					uuid2 += challenged.get(i).getUniqueId().toString() + "|";
					names2 += challenged.get(i).getDisplayName() + "|";
					serverName+=c.getPreviousServer(u) + "|";
					u++;
				}
				
				
				
				if(plugin.getMySQL().isConnected())
				{
					try
					{
						 PreparedStatement ps = plugin.getMySQL().getConnection().prepareStatement("INSERT INTO Duel_Challenges (ChallengerUUID,ChallengedUUID,ChallengerName,ChallengedName,ArenaName,ChallengeID,ServerName,ArenaServer,Kit,Tournament,Mode,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
						ps.setString(1, uuid1);
						ps.setString(2, uuid2);
						ps.setString(3, names1);
						ps.setString(4, names2);
						ps.setString(5, arena);
						ps.setInt(6, cID);
						ps.setString(7, serverName);
						ps.setString(8, server);
						
						
						ps.setString(9, kit);
						ps.setInt(10, tournament);
						ps.setInt(11, mode);
						ps.setInt(12, time);
						
						
						
						
						ps.executeUpdate();
					}
					catch (SQLException e)
					{
						e.printStackTrace();
					}
				}
				
	}
	
	public static void sendChallengeToMySQL(ArrayList<UUID> chersUUID,ArrayList<UUID> chedUUID,ArrayList<String> chersName,String[] previousServers,ArrayList<String> chedName,int id,String arena,String server,String kit,int tournament,int mode,int time)
	{
		// Überprüfen ob Challenge schon vorhanden
				try
				{
					PreparedStatement ps1 = plugin.getMySQL().getConnection().prepareStatement("SELECT ChallengeID FROM Duel_Challenges WHERE ChallengeID = ?");
					ps1.setInt(1, id);
					ResultSet rs1 = ps1.executeQuery();
					if(rs1.first())
					{
						return;
					}
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				
				
				
				
				String uuid1="",uuid2="",names1="",names2="";
				int cID = id;String serverName="";
				
				int u = 0;
				for(int i = 0;i<chersUUID.size();i++)
				{
					uuid1 += chersUUID.get(i).toString() + "|";
					names1 += chersName.get(i) + "|";
					serverName+=previousServers[u] + "|";
					u++;
				}
				
				for(int i = 0;i<chedUUID.size();i++)
				{
					uuid2 += chedUUID.get(i).toString() + "|";
					names2 += chedName.get(i) + "|";
					serverName+=previousServers[u] + "|";
					u++;
				}
				
				
				
				if(plugin.getMySQL().isConnected())
				{
					try
					{
						 PreparedStatement ps = plugin.getMySQL().getConnection().prepareStatement("INSERT INTO Duel_Challenges (ChallengerUUID,ChallengedUUID,ChallengerName,ChallengedName,ArenaName,ChallengeID,ServerName,ArenaServer,Kit,Tournament,Mode,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
						ps.setString(1, uuid1);
						ps.setString(2, uuid2);
						ps.setString(3, names1);
						ps.setString(4, names2);
						ps.setString(5, arena);
						ps.setInt(6, cID);
						ps.setString(7, serverName);
						ps.setString(8, server);
						
						
						ps.setString(9, kit);
						ps.setInt(10, tournament);
						ps.setInt(11, mode);
						ps.setInt(12, time);
						
						
						
						
						ps.executeUpdate();
					}
					catch (SQLException e)
					{
						e.printStackTrace();
					}
				}
				
	}
	
	public static int getChallengeID()
	{
		ArrayList<Integer> results = new ArrayList<>();
		
		try
		{
			PreparedStatement ps = plugin.getMySQL().getConnection().prepareStatement("SELECT ChallengeID FROM Duel_Challenges");
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				results.add(rs.getInt(1));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
			
		int id = -1;
		
		do
		{
			Random rand = new Random();
			id = rand.nextInt(Integer.MAX_VALUE);
			
		}while(results.contains(id));
		
		return id;
		
	}
	

	private static String[] decodeDataFromMySQL(String str)
	{
			ArrayList<String> uuis =  new ArrayList<String>();
		
			int i = -1;
			String uuid = "";
			while(i < str.length())
			{
				uuid = "";
				for(i++;i<str.length() && str.charAt(i)!= '|';i++)
				{
					uuid+=str.charAt(i);
				}
				
				if(uuid.length() != 0)
				{
					uuis.add(uuid);
				}
				
			}
			
			String[] strings = new String[uuis.size()];
			
			for(int u = 0;u<strings.length;u++)
			{
				strings[u] = uuis.get(u);
			}
			
			uuis.clear();
		
		
		return strings;
	}
	
	private static ArrayList<UUID> decodeUUIDMySQL(String str)
	{
		ArrayList<UUID> uuis =  new ArrayList<UUID>();
		
		String[] strings = decodeDataFromMySQL(str);
		
		for(int i = 0;i<strings.length;i++)
		{
			uuis.add(UUID.fromString(strings[i]));
		}
		
		return uuis;
		
		
		
	}
	
	public static Challenge newChallenge(Player p)
	{
		if(plugin.getMySQL().isConnected())
		{
			try
			{
				PreparedStatement ps = plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Duel_Challenges");
				ResultSet rs = ps.executeQuery();
				
				String challengerUUID="",challengedUUID="";
				int id;String arenaName ="",serverName="",arenaServer="";
				String kit = "";int tournament;int mode;int time;
				
				int numChallsFound = 0;
				
				while(rs.next())
				{
					
					challengerUUID = rs.getString(1);
					challengedUUID = rs.getString(2);
					arenaName = rs.getString(5);
					id = rs.getInt(6);
					serverName = rs.getString(7);
					arenaServer = rs.getString(8);
					kit = rs.getString(9);
					tournament = rs.getInt(10);
					mode = rs.getInt(11);
					time = rs.getInt(12);
					
					if(challengerUUID.contains(p.getUniqueId().toString()) || challengedUUID.contains(p.getUniqueId().toString()))
					{
						if(plugin.serverName.equals(""))
							plugin.serverName = arenaServer;
						numChallsFound++;
						ArrayList<UUID> challenger = decodeUUIDMySQL(challengerUUID);
						ArrayList<UUID> challenged = decodeUUIDMySQL(challengedUUID);
						String[] serverNames = decodeDataFromMySQL(serverName);
						
						if(numChallsFound>1)
						{
							challenges.remove(challenges.size()-1);
						}
						
						challenges.add(new Challenge(challenger, challenged,arenaName,id,serverNames,Kit.decodeMySQLKit(p, kit),tournament,arenaServer,mode,time));
						
						// Fertig und dann von Tabelle löschen
						PreparedStatement ps1 = plugin.getMySQL().getConnection().prepareStatement("DELETE FROM Duel_Challenges WHERE ChallengeID = ?");
						ps1.setInt(1, id);
						ps1.executeUpdate();
					}
				}
				
				if(numChallsFound >= 1)
				{
					Challenge c = challenges.get(challenges.size()-1);
					
					if(c.isTournament())
					{
						DuelListener.lastTournamentID = c.getTournamentID();
					}
					
					System.out.println("[ChallangeManager] Create new Challenge");
					
					return c;
				}
				
				
				return null;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				return null;
			}
			
		}
		
		return null;
		
	}

	
	public static ArrayList<Challenge> getAllChallenges()
	{
		return challenges;
	}
	
	/// TRUE Challenge existiert
	/// FALSE Challenge existiert nicht
	public static boolean deleteChallenge(int id) {
		for (int i = 0; i < challenges.size(); i++)
		{
			if (challenges.get(i).ID == id)
			{
				challenges.remove(i);

				return true;
			}
		}
		return false;
	}

	/// Gibt Challengers/Challenged zurück
	/// null Wenn keine Challenge vorhanden
	public static ArrayList<Player> deleteChallenge(Player p) {
		
		ArrayList<Player> rv = new ArrayList<Player>();
		
		// Challenges durchgehen
		for (int i = 0; i < challenges.size(); i++)
		{
			
				Challenge c = challenges.get(i);
				int role = getRole(p, c);
				
				if(role != Challenge.NO_ID && role != Challenge.NO_ROLE) 
				{
					if(role == Challenge.IS_CHALLANGER)
					{
						rv = c.getChallenged();
					}
					else if(role == Challenge.IS_CHALLANGED)
					{
						rv = c.getChallengers();
					}
					
					challenges.remove(i);
					
					
					return rv;
				}
		}

		return null;
	}

	public static Challenge getChallenge(Player p) {
		for (Challenge c : challenges)
		{
			int role = getRole(p,c);
			if (role != Challenge.NO_ID && role != Challenge.NO_ROLE)
			{
				return c;
			}
		}

		return null;
	}

	public static Challenge getChallenge(Player challenger1, Player challenged1) {
		return getChallenge(isAlreadyIn(challenger1, challenged1));
	}

	public static int getRole(Player p, int id)
	{
		Challenge rv = getChallenge(id);
		if (rv != null)
		{
			for(int i =0;i<rv.getChallengersUUID().size();i++)
			{
				if(rv.getChallengersUUID().get(i).equals(p.getUniqueId()))
				{
					return Challenge.IS_CHALLANGER;
				}
			}
			
			for(int i =0;i<rv.getChallengedUUID().size();i++)
			{
				if(rv.getChallengedUUID().get(i).equals(p.getUniqueId()))
				{
					return Challenge.IS_CHALLANGED;
				}
			}
			
			return Challenge.NO_ROLE;
			
		}
		return Challenge.NO_ID;

	}
	
	public static int getRole(Player p, Challenge c)
	{
		if (c != null)
		{
			for(int i =0;i<c.getChallengersUUID().size();i++)
			{
				if(c.getChallengersUUID().get(i).toString().equals(p.getUniqueId().toString()))
				{
					return Challenge.IS_CHALLANGER;
				}
			}
			
			for(int i =0;i<c.getChallengedUUID().size();i++)
			{
				if(c.getChallengedUUID().get(i).toString().equals(p.getUniqueId().toString()))
				{
					return Challenge.IS_CHALLANGED;
				}
			}
			
			return Challenge.NO_ROLE;
			
		}
		return Challenge.NO_ID;

	}


	public static ArrayList<Challenge> getChallenges(Player p) {
		ArrayList<Challenge> challs = new ArrayList<Challenge>();

		for (int i = 0; i < challenges.size(); i++)
		{
			int role = getRole(p,challenges.get(i));
			if(role != Challenge.NO_ID && role != Challenge.NO_ROLE)
			{
				challs.add(challenges.get(i));
			}
		}

		return challs;

	}
	
	private static Challenge getChallengeMySQL(Player p)
	{
		if(plugin.getMySQL().isConnected())
		{
			try
			{
				PreparedStatement ps = plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Duel_Challenges");
				ResultSet rs = ps.executeQuery();
				
				String challengerUUID="",challengedUUID="";
				int id;String arenaName ="",serverName="",arenaServer="";
				String kit = "";int tournament;int mode;int time;
				
				while(rs.next())
				{
					
					challengerUUID = rs.getString(1);
					challengedUUID = rs.getString(2);
					arenaName = rs.getString(5);
					id = rs.getInt(6);
					serverName = rs.getString(7);
					arenaServer = rs.getString(8);
					kit = rs.getString(9);
					tournament = rs.getInt(10);
					mode = rs.getInt(11);
					time = rs.getInt(12);
					
					if(challengerUUID.contains(p.getUniqueId().toString()) || challengedUUID.contains(p.getUniqueId().toString()))
					{
						ArrayList<UUID> challenger = decodeUUIDMySQL(challengerUUID);
						ArrayList<UUID> challenged = decodeUUIDMySQL(challengedUUID);
						String[] serverNames = decodeDataFromMySQL(serverName);
						
						Challenge c = new Challenge(challenger, challenged,arenaName,id,serverNames,Kit.decodeMySQLKit(p, kit),tournament,arenaServer,mode,time);
						
						return c;
					}
					
					return null;
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static void checkPlayers()
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			Duel d = DuelManager.getDuel(p);
			
			if(d==null)
			{
				Challenge c = getChallengeMySQL(p);
				
				if(c!=null)
				{
					if(c.getArenaServer().equals(MainClass.getInstance().serverName))
					{
						DuelListener.onSpawnCalled.remove(p.getUniqueId());
						DuelListener.onSpawn(p, true, false);
					}
					else
					{
						Messenger.sendMessage(p, "BungeeCord", "Connect", c.getArenaServer());
					}
				}
			}
		}
	}
}
