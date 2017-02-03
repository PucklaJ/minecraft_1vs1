package at.Kingcraft.OnevsOne_lobby.Tournaments;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import MySQL.MySQL;
import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Sounds;
import at.Kingcraft.OnevsOne_lobby.Arenas.ArenaManager;
import at.Kingcraft.OnevsOne_lobby.Duels.ChallangeManager;
import at.Kingcraft.OnevsOne_lobby.Duels.Team;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamManager;
import at.Kingcraft.OnevsOne_lobby.Lobby.LobbyListener;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import at.Kingcraft.OnevsOne_lobby.Scoreboard.MyScoreboardManager;
import at.Kingcraft.OnevsOne_lobby.Special.EnquieryMenu;
import at.Kingcraft.OnevsOne_lobby.Special.MenuManager;

public class TournamentManager
{
	private static ArrayList<Tournament> tournaments;
	private static MainClass plugin;
	private static MySQL mysql;
	
	public static void setup(MainClass plugin)
	{
		TournamentManager.plugin = plugin;
		tournaments = new ArrayList<>();
		
		mysql = plugin.getMySQL();
		
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Duel_Tournaments (Contestants TEXT(65535),ID INT(10), Rounds TEXT(65535),Names TEXT(65535),Loser TEXT(65535),RoundSkipper TEXT(65535),Servers VARCHAR(1000),Arenas VARCHAR(1000),MaxRoundLevel INT(10),AllRounds TEXT(65535),KitMode INT(1),QualiRounds TEXT(65535))");
			ps.executeUpdate();
			
			ps = mysql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Duel_Spectators (UUID VARCHAR(100), Name VARCHAR(100), TournamentID INT(10), HomeServer VARCHAR(100))");
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/*public static SpecTournament getTournamentFromMySQL(Player p,int id)
	{
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT * FROM Duel_Tournaments WHERE ID = ?");
			ps.setInt(1, id);
			
			ResultSet rs = ps.executeQuery();
			
			SpecTournament t = null;
			while(rs.next())
			{
				// Players
				ArrayList<TourPlayer> players = new ArrayList<>();
				
				String uuids = rs.getString(1),names = rs.getString(4);
				
				String[] playerUUIDs = splitString('|', uuids);
				
				// UUIDs
				
				for(int i = 0;i<playerUUIDs.length;i++)
				{
					players.add(new TourPlayer("null",UUID.fromString(playerUUIDs[i])));
				}
				
				
				// Names
				String[] playerNames = splitString('|', names);
				
				for(int i = 0;i<playerNames.length;i++)
				{
					players.get(i).name = playerNames[i];
				}
				
				
				// Rounds
				ArrayList<SpecRound> rounds = new ArrayList<>();
				String roundStr = rs.getString(3);
				
				if(!roundStr.equals("NO_ROUNDS"))
				{
					String[] roundsStr = roundStr.split("\n");
					
					for(int i = 0;i<roundsStr.length;i++)
					{
						rounds.add(SpecRound.fromString(roundsStr[i]));
					}
				}
				
				// Losers
				ArrayList<TourPlayer> loser = new ArrayList<>();
				String loserStr = rs.getString(5);
				
				if(!loserStr.equals("NO_LOSER"))
				{
					String[] loserArray = loserStr.split("\n");
					
					for(int i = 0;i<loserArray.length;i++)
					{
						String uuid = "";
						String name = "";
						
						int u;
						for(u = 0;u<loserArray[i].length() && loserArray[i].charAt(u) != '|';u++)
						{
							uuid += loserArray[i].charAt(u);
						}
						for(u++;u<loserArray[i].length();u++)
						{
							name += loserArray[i].charAt(u);
						}
						
						loser.add(new TourPlayer(name,UUID.fromString(uuid)));
					}
				}
				
				// RoundSkipper
				TourPlayer roundSkipper = null;
				String roundSkipperStr = rs.getString(6);
				
				if(!roundSkipperStr.equals("NO_ROUND_SKIPPER"))
				{
					String uuid = "";
					String name = "";
					
					int u;
					for(u = 0;u<roundSkipperStr.length() && roundSkipperStr.charAt(u) != '|';u++)
					{
						uuid += roundSkipperStr.charAt(u);
					}
					for(u++;u<roundSkipperStr.length();u++)
					{
						name += roundSkipperStr.charAt(u);
					}
					
					roundSkipper = new TourPlayer(name,UUID.fromString(uuid));
				}
				
				// Servers
				String[] servers = null;
				String serverStr = rs.getString(7);
				
				if(!serverStr.equals("NO_SERVERS"))
				{
					servers = splitString('|', serverStr);
				}
				
				//Arenas
				String arenaStr = rs.getString(8);
				String[] arenas = splitString('|', arenaStr);
				
				
				// MaxRoundLevel
				int maxRoundLevel = rs.getInt(9);
				
				// Tournament Creation
				t = new SpecTournament();
				
				t.setContestants(players);
				t.setRounds(rounds);
				t.setLosers(loser);
				t.setRoundSkipper(roundSkipper);
				t.setServers(servers);
				t.setArenas(arenas);
				t.setMaxRoundLevel(maxRoundLevel);
				
				return t;
				
			}
			
			return t;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}*/
	
	/*public static boolean addSpectator(Player p,int tournamentID)
	{
		SpecTournament st = getTournamentFromMySQL(p, tournamentID);
		
		if(st == null)
		{
			return false;
		}
		
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT UUID FROM Duel_Spectators WHERE UUID = ?");
			ps.setString(1, p.getUniqueId().toString());
			
			ResultSet rs = ps.executeQuery();
			
			if(rs.first())
			{
				return false;
			}
			
			ps = mysql.getConnection().prepareStatement("INSERT INTO Duel_Spectators (UUID,Name,TournamentID,HomeServer) VALUES (?,?,?,?)");
			ps.setString(1, p.getUniqueId().toString());
			ps.setString(2, p.getName());
			ps.setInt(3, tournamentID);
			ps.setString(4, plugin.serverName);
			
			ps.executeUpdate();
		
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		
		
		if(LobbyListener.tournamentRoundSkipper.get(p.getUniqueId()) != null)
		{
			LobbyListener.leftByTour.add(p.getUniqueId());
			LobbyListener.tournamentRoundSkipper.remove(p.getUniqueId());
		}
		
		return Messenger.sendMessage(p, "BungeeCord", "Connect", st.getServers().get(0));
	}*/
	
	private static int getID()
	{
		Random rand = new Random();
		
		int randI = 0;
		
		do
		{
			
			randI = rand.nextInt(Integer.MAX_VALUE);
			
		}while(idAlreadyExists(randI));
		
		return randI;
	}
	
	public static String[] splitString(char regex,String str)
	{
		ArrayList<String> strs = new ArrayList<>();
		
		String temp = "";
		for(int i = 0;i<str.length();i++)
		{
			if(str.charAt(i) != regex)
			{
				temp += str.charAt(i);
			}
			else
			{
				strs.add(temp);
				temp = "";
			}
		}
		
		strs.add(temp);
		
		return toArray(strs);
	}
	
	private static String[] toArray(ArrayList<String> strs)
	{
		String[] str = new String[strs.size()];
		
		for(int i = 0;i<strs.size();i++)
		{
			str[i] = strs.get(i);
		}
		
		return str;
	}
	
	
	public static void removeRoundSkipper(Player p,int id)
	{
		if(!mysql.isConnected())
		{
			return;
		}
		
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT Contestants,Names,RoundSkipper FROM Duel_Tournaments WHERE ID = ?");
			ps.setInt(1, id);
			
			ResultSet rs = ps.executeQuery();
			
			String contStr;
			String namesStr;
			String roundSkipper;
			
			while(rs.next())
			{
				contStr = rs.getString(1);
				namesStr = rs.getString(2);
				roundSkipper = rs.getString(3);
				
				if(!roundSkipper.equals(p.getUniqueId().toString() + "|" + p.getName()))
				{
					return;
				}
				
				String[] roundSk = splitString('|', roundSkipper);
				
				String[] conts = splitString('|',contStr);
				String[] names = splitString('|',namesStr);
				
				ArrayList<String> contsA = new ArrayList<>(),namesA = new ArrayList<>();
				
				for(int i = 0;i<conts.length;i++)
				{
					if(!conts[i].equals(roundSk[0]))
					{
						contsA.add(conts[i]);
					}
				}
				for(int i = 0;i<names.length;i++)
				{
					if(!names[i].equals(roundSk[1]))
					{
						namesA.add(names[i]);
					}
				}
				
				String newConts = "",newNames = "";
				for(int i = 0;i<contsA.size();i++)
				{
					newConts += contsA.get(i) + (i+1 == contsA.size() ? "" : "|");
					newNames += namesA.get(i) + (i+1 == namesA.size() ? "" : "|");
				}
				
				roundSkipper = "NO_ROUND_SKIPPER";
			
				ps = mysql.getConnection().prepareStatement("UPDATE Duel_Tournaments SET Contestants = ?,Names = ?,RoundSkipper = ? WHERE ID = ?");
				ps.setString(1, newConts);
				ps.setString(2, newNames);
				ps.setString(3, roundSkipper);
				ps.setInt(4, id);
				ps.executeUpdate();
				return;
			}
			
			
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private static boolean idAlreadyExists(int id)
	{
		for(int i = 0;i<tournaments.size();i++)
		{
			if(tournaments.get(i).getID() == id)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static ArrayList<Tournament> getTournaments()
	{
		return tournaments;
	}
	
	private static void sendCreateMessageToArena(Player creator,int size)
	{
		
		Player p;
		
			if(Bukkit.getServer().getOnlinePlayers().isEmpty())
			{
				return;
			}
			p=(Player) Bukkit.getServer().getOnlinePlayers().toArray()[0];
			if(p == null)
			{
				return;
			}
			
		ArrayList<String> servers = ArenaManager.getServers(0);
			
		for(int i = 0;i<servers.size();i++)
		{
			ByteArrayDataOutput bo = ByteStreams.newDataOutput();
			bo.writeUTF("Forward");
			
			bo.writeUTF(servers.get(i));
			bo.writeUTF("Tournament");
			
			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);
			try
			{
				msgout.writeUTF(plugin.serverName);
				msgout.writeUTF(size+"");
				msgout.writeUTF(LobbyListener.getPrefix(p));
				msgout.writeUTF(p.getDisplayName()); // You can do anything you want with msgout
			}
			catch(IOException e)
			{
				e.printStackTrace();
				System.out.println("Tournament Send Arena Message Error");
				return;
			}
			
			bo.writeShort(msgbytes.toByteArray().length);
			bo.write(msgbytes.toByteArray());
			
			p.sendPluginMessage(MainClass.getInstance(), "BungeeCord", bo.toByteArray());
		}
		
	}
	
	public static void createTournament(Player p)
	{
		ArrayList<Player> players;
		Team team = TeamManager.getTeam(p);
		if(team != null)
		{
			players = team.getPlayers();
		}
		else
		{
			players = new ArrayList<>();
			players.add(p);
		}
		
		Tournament t = TournamentManager.getTournament(players);
		
		if(t != null)
		{
			p.sendMessage(Messages.alreadyTournament);
			return;
		}
		
		t = new Tournament(players,plugin,getID());
		
		tournaments.add(t);
		
		for(Player p1 : Bukkit.getServer().getOnlinePlayers())
		{
			EnquieryMenu em = ChallangeManager.getEnquiryMenu(p1);
			em.setStartingTournaments(tournaments);
			if(em.isOpen())
				em.updateInventory();
		}
		
		p.sendMessage(Messages.tournamentCreated);
		
		for(Player p1 : Bukkit.getOnlinePlayers())
		{
			MyScoreboardManager.updateScoreboard(p1);
			if(p1.getUniqueId().equals(p.getUniqueId()))
				continue;
			p1.playSound(p1.getLocation(), Sounds.tournamentCreate, Sounds.tournamentCreateVolume, Sounds.DEFAULT_PITCH);
			p1.sendMessage(Messages.tournamentOtherCreate(p.getDisplayName(), t.getTeamSize()));
		}
		
		sendCreateMessageToArena(p,t.getTeamSize());
		
		MenuManager.getSettingMenu(p).getTourSettingMenu().open();
	}
	
	public static boolean joinTournament(Player p,Player leader)
	{
		Tournament t = getTournament(leader);
		
		if(t == null)
		{
			return false;
		}
		
		if(t.getContestants().size() == MenuManager.getSettingMenu(t.getLeader()).getTourSettingMenu().getMaxPlayers())
		{
			p.sendMessage(Messages.tournamentIsFull);
			return true;
		}
		
		Team team = TeamManager.getTeam(p);
		
		ArrayList<Player> players;
		if(team != null)
		{
			players = team.getPlayers();
		}
		else
		{
			players = new ArrayList<>();
			players.add(p);
		}
		
		if(players.size() != t.getTeamSize())
		{
			p.sendMessage(Messages.tournamentYouMustHaveTeamSize(t.getTeamSize()));
			return true;
		}
		
		t.addContestant(players, true,true);
		
		return true;
	}
	
	public static boolean joinTournament(Player p,Tournament t)
	{	
		if(t == null)
		{
			return false;
		}
		
		if(t.getContestants().size() == MenuManager.getSettingMenu(t.getLeader()).getTourSettingMenu().getMaxPlayers())
		{
			p.sendMessage(Messages.tournamentIsFull);
			return true;
		}
		
		Team team = TeamManager.getTeam(p);
		ArrayList<Player> players;
		if(team != null)
		{
			players = team.getPlayers();
		}
		else
		{
			players = new ArrayList<>();
			players.add(p);
		}
		
		t.addContestant(players, true,true);
		
		return true;
	}
	
	public static void deleteTournament(Tournament t,boolean message)
	{
		if(message)
		{
			for(int i = 0;i<t.getContestants().size();i++)
			{
				for(int j = 0;j<t.getContestants().get(i).size();j++)
					t.getContestants().get(i).get(j).sendMessage(Messages.tournamentDelete);
			}
		}
		
		tournaments.remove(t);
		
		for(Player p1 : Bukkit.getServer().getOnlinePlayers())
		{
			EnquieryMenu em = ChallangeManager.getEnquiryMenu(p1);
			em.setStartingTournaments(tournaments);
			if(em.isOpen())
				em.updateInventory();
		}
		
		for(Player p1 : Bukkit.getOnlinePlayers())
		{
			MyScoreboardManager.updateScoreboard(p1);
		}
	}
	
	public static Tournament getTournament(ArrayList<Player> p)
	{
		for(int i = 0;i<tournaments.size();i++)
		{
			if(tournaments.get(i).isIn(p))
				return tournaments.get(i);
		}
		
		return null;
	}
	
	public static Tournament getTournament(Player p)
	{
		for(int i = 0;i<tournaments.size();i++)
		{
			ArrayList<ArrayList<Player>> conts = tournaments.get(i).getContestants();
			for(int j = 0;j<conts.size();j++)
			{
				for(int k = 0;k<conts.get(j).size();k++)
				{
					if(p.getUniqueId().equals(conts.get(j).get(k).getUniqueId()))
					{
						return tournaments.get(i);
					}
				}
			}
		}
		
		return null;
	}
	
	public static boolean uploadTournament(Tournament t)
	{
		if(!mysql.isConnected())
		{
			return false;
		}
		
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT ID FROM Duel_Tournaments WHERE ID = ?");
			ps.setInt(1, t.getID());
			
			if(ps.executeQuery().first())
			{
				return false;
			}
			
			ps = mysql.getConnection().prepareStatement("INSERT INTO Duel_Tournaments (Contestants,ID,Rounds,Names,Loser,RoundSkipper,Servers,Arenas,MaxRoundLevel,AllRounds,KitMode,QualiRounds) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, t.contestantsToString());
			ps.setInt(2, t.getID());
			ps.setString(3, t.roundsToString());
			ps.setString(4, t.contestantNamesToString());
			ps.setString(5, "NO_LOSER");
			ps.setString(6, t.roundSkipperToString());
			ps.setString(7, t.serversToString());
			ps.setString(8, t.arenasToString());
			ps.setInt(9, t.getMaxRoundLevel());
			ps.setString(10, t.allRoundsToString());
			ps.setInt(11, MenuManager.getSettingMenu(t.getLeader()).getTourSettingMenu().getKitMode());
			ps.setString(12, t.getQualiPoints());
			
			ps.executeUpdate();
			
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static Tournament getTournament(int id)
	{
		for(int i = 0;i<tournaments.size();i++)
		{
			if(tournaments.get(i).getID() == id)
				return tournaments.get(i);
		}
		
		return null;
	}
}
