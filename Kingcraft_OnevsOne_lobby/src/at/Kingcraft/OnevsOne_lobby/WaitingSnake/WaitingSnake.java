package at.Kingcraft.OnevsOne_lobby.WaitingSnake;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import MySQL.MySQL;
import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Arenas.ArenaManager;
import at.Kingcraft.OnevsOne_lobby.Duels.DuelManager;
import at.Kingcraft.OnevsOne_lobby.Duels.SpectateManager;
import at.Kingcraft.OnevsOne_lobby.Duels.Team;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamManager;
import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;
import at.Kingcraft.OnevsOne_lobby.Lobby.LobbyListener;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import at.Kingcraft.OnevsOne_lobby.Special.MapMenu;
import at.Kingcraft.OnevsOne_lobby.Special.MenuManager;
import at.Kingcraft.OnevsOne_lobby.Tournaments.Tournament;
import at.Kingcraft.OnevsOne_lobby.Tournaments.TournamentManager;

public class WaitingSnake
{
	private MySQL mysql;
	private MainClass plugin;
	private ArrayList<Player> playersToUpload;
	private int checkThreadID = 0;
	private ArrayList<UUID> playersInWaitingSnake;
	
	public WaitingSnake(MainClass plugin,MySQL mysql)
	{
		this.mysql = mysql;
		playersToUpload = new ArrayList<>();
		playersInWaitingSnake = new ArrayList<>();
		this.plugin = plugin;
		
		if(mysql.isConnected())
		{
			try
			{
				PreparedStatement ps = mysql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Duel_WaitingSnake_Players (UUID VARCHAR(100), Settings VARCHAR(100),ServerName VARCHAR(100),Kit VARCHAR(1000), Arena VARCHAR(100), Team VARCHAR(100),Name VARCHAR(100))");
				ps.executeUpdate();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			
			checkPlayers(plugin);
		}
	}
	
	public boolean isIn(Player p)
	{
		return playersInWaitingSnake.contains(p.getUniqueId());
	}
	
	public boolean isIn(ArrayList<Player> p)
	{
		for(int i = 0;i<p.size();i++)
		{
			if(isIn(p.get(i)))
				return true;
		}
		
		return false;
	}
	
	public boolean isInMySQL(Player p)
	{
		return isInMySQL(p.getUniqueId());
	}
	
	public boolean isInMySQL(String uuid)
	{
		if(mysql.isConnected())
		{
			try
			{
				PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT UUID FROM Duel_WaitingSnake_Players WHERE UUID = ?");
				ps.setString(1, uuid);
				ResultSet rs = ps.executeQuery();
				
				if(rs.first())
				{
					return true;
				}
				return false;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	public boolean isInMySQL(UUID uuid)
	{
		return isInMySQL(uuid.toString());
	}
	
	public void addPlayer(Player p,boolean message)
	{
		if(playersToUpload.contains(p))
		{
			return;
		}
		
		playersToUpload.add(p);
		playersInWaitingSnake.add(p.getUniqueId());
		
		Tournament tour = TournamentManager.getTournament(p);
		ArrayList<Player> players;
		Team t = TeamManager.getTeam(p);
		if(t != null)
		{
			players = t.getPlayers();
		}
		else
		{
			players = new ArrayList<>();
			players.add(p);
		}
		if(tour != null)
		{
			tour.removeContestants(players, true, true,true);
		}
		
		if(Settings.getSettings(p).isQuickWS() && TeamManager.getTeam(p) == null)
		{
			Settings.getSettings(p).addToWSOnJoin(true);
		}
		
		if(message)
		p.sendMessage(Messages.waitingSnakeJoin);
		
		
		
		if(t != null)
		{
			for(int i = 0;i< t.getPlayers().size();i++)
			{
				addPlayer(t.getPlayers().get(i),message);
			}
		}
	}
	
	public void removePlayer(Player p,boolean teamMembers,boolean message)
	{
		
		if(!playersToUpload.contains(p) && !playersInWaitingSnake.contains(p.getUniqueId()))
		{
			return;
		}
		
		playersToUpload.remove(p);
		playersInWaitingSnake.remove(p.getUniqueId());
		
		try
		{
			if(isInMySQL(p))
			{
				PreparedStatement ps = mysql.getConnection().prepareStatement("DELETE FROM Duel_WaitingSnake_Players WHERE UUID = ?");
				ps.setString(1, p.getUniqueId().toString());
				ps.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		if(message)
			p.sendMessage(Messages.waitingSnakeLeave);
		
		
		if(!teamMembers)
			return;
		
		Team t = TeamManager.getTeam(p);
		
		if(t != null)
		{
			for(int i = 0;i<t.getPlayers().size();i++)
			{
				removePlayer(t.getPlayers().get(i),false,message);
			}
		}
	}
	
	public void removePlayer(ArrayList<Player> p,boolean teamMembers,boolean message)
	{
		for(int i = 0;i<p.size();i++)
		{
			removePlayer(p.get(i), teamMembers, message);
		}
	}
	
	private void removeFromPlayersToUpload(UUID uuid)
	{
		for(int i = 0;i<playersToUpload.size();i++)
		{
			if(playersToUpload.get(i).getUniqueId().equals(uuid))
			{
				playersToUpload.remove(i);
				return;
			}
		}
	}
	
	public void removePlayer(UUID uuid,boolean teamMembers,boolean message)
	{	
		if(isOnServer(uuid))
		{
			removePlayer(Bukkit.getOfflinePlayer(uuid).getPlayer(),teamMembers,message);
			return;
		}
		
		removeFromPlayersToUpload(uuid);
		playersInWaitingSnake.remove(uuid);
		
		try
		{
			if(isInMySQL(uuid))
			{
				PreparedStatement ps = mysql.getConnection().prepareStatement("DELETE FROM Duel_WaitingSnake_Players WHERE UUID = ?");
				ps.setString(1, uuid.toString());
				ps.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private void checkPlayers(MainClass plugin)
	{
		checkThreadID = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
			
			@Override
			public void run()
			{
				if(plugin.getServer().getOnlinePlayers().size() != 0)
				{
					uploadPlayers();
					makeDuels();
				}
			}
		}, 20*2, 20*2).getTaskId();
	}
	
	public void stopCheckTask()
	{
		Bukkit.getScheduler().cancelTask(checkThreadID);
	}
	
	private void uploadPlayers()
	{
		if(!mysql.isConnected())
		{
			return;
		}
		
		for(int i = 0;i<playersToUpload.size();i++)
		{
			System.out.println("Upload " + playersToUpload.get(i).getDisplayName());
			if(!isInMySQL(playersToUpload.get(i)))
			{
				uploadPlayer(playersToUpload.get(i));
				playersToUpload.remove(playersToUpload.get(i));
			}
			else
			{
				System.out.println(playersToUpload.get(i) + " is already in MySQL");
				playersToUpload.remove(playersToUpload.get(i));
			}
		}
	}
	

	private void makeDuels()
	{
		ArrayList<WaitingSnakeUpload> uploadedPlayers = new ArrayList<>();
		
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT * FROM Duel_WaitingSnake_Players");
			ResultSet rs = ps.executeQuery();
			
			
			while(rs.next())
			{	
				uploadedPlayers.add(new WaitingSnakeUpload(UUID.fromString(rs.getString(1)),
														   Settings.fromString(rs.getString(2)),
														   rs.getString(3),
														   rs.getString(4),
														   rs.getString(5),
														   rs.getString(6),
														   rs.getString(7)));
			}
			
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		if(uploadedPlayers.isEmpty())
		{
			return;
		}
		
		for(int i = 0;i<uploadedPlayers.size();i++)
		{
			boolean found = false;
			for(int j = 0;j<uploadedPlayers.size();j++)
			{
				if(j!=i)
				{
						WaitingSnakeUpload op1 = uploadedPlayers.get(i);
						WaitingSnakeUpload op2 = uploadedPlayers.get(j);
					
					if(op1.set.canPlayTogether(op2.set) && (!op1.team.equals(op2.team) || op1.team.equals("-1") || op2.team.equals("-1")))
					{
						
						int teamSize = getTeamSizeFromUpload(op1.team);
						if(teamSize != getTeamSizeFromUpload(op2.team))
						{
							continue;
						}
						else if(teamSize > 1)
						{
							ArrayList<WaitingSnakeUpload> op1A = new ArrayList<>(),op2A = new ArrayList<>();
							ArrayList<String> serverNames1 = new ArrayList<>(),serverNames2 = new ArrayList<>();
							op1A.add(op1);
							op2A.add(op2);
							serverNames1.add(op1.serverName);
							serverNames2.add(op2.serverName);
							
							int teamID1 = getTeamIDFromUpload(op1.team);
							int teamID2 = getTeamIDFromUpload(op2.team);
							
							for(int u = 0;u<uploadedPlayers.size();u++)
							{
								WaitingSnakeUpload op3 = uploadedPlayers.get(u);
								
								if(!op1.uuid.equals(op3.uuid) && teamID1 == getTeamIDFromUpload(op3.team))
								{
									op1A.add(op3);
									serverNames1.add(op3.serverName);
								}
								else if(!op2.uuid.equals(op3.uuid) && teamID2 == getTeamIDFromUpload(op3.team))
								{
									op2A.add(op3);
									serverNames2.add(op3.serverName);
									
								}
							}
							
							if(op1A.size() != teamSize || op2A.size() != teamSize)
							{
								continue;
							}
							
							for(int u = 0;u<op1A.size();u++)
							{
								if(!isInMySQL(op1A.get(u).uuid))
								{
									continue;
								}
							}
							
							for(int u = 0;u<op2A.size();u++)
							{
								if(!isInMySQL(op2A.get(u).uuid))
								{
									continue;
								}
							}
							
							String[] kits = new String[2];
							kits[0] = op1.kit;
							kits[1] = op2.kit;
							
							Settings[] settings = new Settings[2];
							settings[0] = op1.set;
							settings[1] = op2.set;
							
							String[] maps = new String[2];
							maps[0] = op1.arena;
							maps[1] = op2.arena;
							
							if(!makeNewDuel(op1A, op2A, settings, serverNames1, serverNames2, kits, maps))
							{
								System.out.println("New Duel didn't work");
								continue;
							}
							
							for(int u = 0;u<op1A.size();u++)
							{
								uploadedPlayers.remove(op1A.get(u));
							}
							for(int u = 0;u<op2A.size();u++)
							{
								uploadedPlayers.remove(op2A.get(u));
							}
							
						}
						else
						{
							if(!isInMySQL(op1.uuid))
							{
								break;
							}
							if(!isInMySQL(op2.uuid))
							{
								continue;
							}
							
							String[] kits = new String[2];
							kits[0] = op1.kit;
							kits[1] = op2.kit;
							
							Settings[] settings = new Settings[2];
							settings[0] = op1.set;
							settings[1] = op2.set;
							
							String[] maps = new String[2];
							maps[0] = op1.arena;
							maps[1] = op2.arena;
							
							if(!makeNewDuel(op1,op2,settings,op1.serverName,op2.serverName,kits,maps))
							{
								System.out.println("New Duel didn't work");
								continue;
							}
							
							uploadedPlayers.remove(op1);
							uploadedPlayers.remove(op2);
							
						}
						
						
						
						
						
						
						i=-1;
						
						found = true;
						break;
					}
				}
			}
			
			if(!found)
			{	
				uploadedPlayers.remove(i);
				
				i=-1;
			}
		}
	}
	
	private int getTeamIDFromUpload(String str)
	{
		if(str.equals("-1"))
		{
			return -1;
		}
		
		String str1 = "";
		
		for(int i = 0;i<str.length() && str.charAt(i) != '|';i++)
		{
			str1 += str.charAt(i);
		}
		
		return Integer.valueOf(str1);
	}
	
	private int getTeamSizeFromUpload(String str)
	{
		if(str.equals("-1"))
		{
			return 1;
		}
		
		String str1 = "";
		
		int i;
		for(i = 0;i<str.length() && str.charAt(i) != '|';i++)
		{
			
		}
		for(i++;i<str.length();i++)
		{
			str1 += str.charAt(i);
		}
		
		return Integer.valueOf(str1);
	}
	
	private void uploadPlayer(Player p)
	{
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("INSERT INTO Duel_WaitingSnake_Players (UUID,Settings,ServerName,Kit,Arena,Team,Name) VALUES (?,?,?,?,?,?,?)");
			ps.setString(1, p.getUniqueId().toString());
			
			Team t = TeamManager.getTeam(p);
			
			ps.setString(2,  t == null ? Settings.getSettings(p).toString() : Settings.getSettings(t.getLeader()).toString());
			ps.setString(3, plugin.serverName);
			
			String kit;
			Player kitPlayer = t == null ? p : t.getLeader();
			kit = KitManager.getChoosenKit(kitPlayer).getKit().itemsToString();
			
			ps.setString(4, kit);
			ps.setString(5, t == null ? MapMenu.getRandomArena(MenuManager.getSettingMenu(p).getMapMenu().getMyMaps(), null) : MapMenu.getRandomArena(MenuManager.getSettingMenu(t.getLeader()).getMapMenu().getMyMaps(), null));
			
			ps.setString(6, t == null ? String.valueOf(-1) : (String.valueOf(t.getID()) + "|" + t.getPlayers().size()));
			ps.setString(7, p.getName());
			ps.executeUpdate();
			
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	private int getNumberKit(int kitMode1,int kitMode2)
	{
		if(kitMode1 == Settings.DIF_KIT_MODE)
			return 1;
		
		if(kitMode1 == Settings.NO_KIT_MODE && kitMode2 == Settings.NO_KIT_MODE)
		{
			return new Random().nextInt(2);
		}
		
		if(kitMode1 == Settings.OWN_KIT_MODE && kitMode2 == Settings.NO_KIT_MODE)
		{
			return 0;
		}
		
		if(kitMode2 == Settings.OWN_KIT_MODE && kitMode1 == Settings.NO_KIT_MODE)
		{
			return 1;
		}
		
		return 0;
		
	}
	
	private int getNumberMap(int mapMode1,int mapMode2)
	{
		if(mapMode1 == Settings.NO_MAP_MODE && mapMode2 == Settings.NO_MAP_MODE)
		{
			return new Random().nextInt(2);
		}
		
		if(mapMode1 == Settings.OWN_MAP_MODE && mapMode2 == Settings.NO_MAP_MODE)
		{
			return 0;
		}
		
		if(mapMode2 == Settings.OWN_MAP_MODE && mapMode1 == Settings.NO_MAP_MODE)
		{
			return 1;
		}
		
		return 0;
		
	}
	
	private boolean makeNewDuel(WaitingSnakeUpload op1,WaitingSnakeUpload op2,Settings[] set,String serverName1,String serverName2,String[] kit,String[] arena)
	{
		String[] serverNames = new String[2];
		serverNames[0] = serverName1;
		serverNames[1] = serverName2;
		
		ArrayList<WaitingSnakeUpload> chers = new ArrayList<>();
		chers.add(op1);
		ArrayList<WaitingSnakeUpload> ched = new ArrayList<>();
		ched.add(op2);
		
		String server = ArenaManager.giveArena();
		
		if(server.length() == 0)
		{
			System.out.println("No free Arena");
			return false;
		}
		
		int kitNumber = getNumberKit(set[0].getKitMode(),set[1].getKitMode());
		int mapNumber = getNumberMap(set[0].getMapMode(),set[1].getMapMode());
		int id = (new Random()).nextInt(Integer.MAX_VALUE);
		
		if(DuelManager.sendDuelToSQL(chers,ched,id,-1, set[kitNumber], kit[kitNumber], serverNames,server, arena[mapNumber],1,-1) != 1)
		{
			System.out.println("MySQL Error");
			return false;
		}
		
		removePlayer(op1.uuid,false,false);
		if(isOnServer(op1.uuid))
		{
			LobbyListener.leftByWS.add(op1.uuid);
		}
		
		if(!ArenaManager.teleportToArena(op1.name, server))
		{
			System.out.println("Couldn't teleport " + op1.name);
			return false;
		}
		
		
		
		removePlayer(op2.uuid,false,false);
		if(isOnServer(op2.uuid))
		{
			LobbyListener.leftByWS.add(op2.uuid);
		}
		if(!ArenaManager.teleportToArena(op2.name, server))
		{
			System.out.println("Couldn't teleport " + op2.name);
			return false;
		}

		ArrayList<UUID> uuid1 = new ArrayList<>();
		uuid1.add(op1.uuid);
		ArrayList<UUID> uuid2 = new ArrayList<>();
		uuid2.add(op2.uuid);
		
		SpectateManager.uploadToMySQL(uuid1, uuid2, server, id,true);
		
		return true;
	}
	
	private boolean makeNewDuel(ArrayList<WaitingSnakeUpload> op1, ArrayList<WaitingSnakeUpload> op2,Settings[] set,ArrayList<String> serverName1,ArrayList<String> serverName2,String[] kit,String[] arena)
	{
		String[] serverNames = new String[serverName1.size()+serverName2.size()];
		
		int u = 0;
		for(int i = 0;i<serverName1.size();i++)
		{
			serverNames[u] = serverName1.get(i);
			u++;
		}
		for(int i = 0;i<serverName2.size();i++)
		{
			serverNames[u] = serverName2.get(i);
			u++;
		}
		
		String server = ArenaManager.giveArena();
		
		if(server.length() == 0)
		{
			System.out.println("No free Arena");	
			return false;
		}
		
		int kitNumber = getNumberKit(set[0].getKitMode(),set[1].getKitMode());
		int mapNumber = getNumberMap(set[0].getMapMode(),set[1].getMapMode());
		int id = (new Random()).nextInt(Integer.MAX_VALUE);
		
		if(DuelManager.sendDuelToSQL(op1,op2,id,-1, set[kitNumber], kit[kitNumber], serverNames,server, arena[mapNumber],1,-1) != 1)
		{
			System.out.println("MySQL Error");
			return false;
		}
		
		for(int i = 0;i<op1.size();i++)
		{
			removePlayer(op1.get(i).uuid,false,false);
			if(isOnServer(op1.get(i).uuid))
			{
				LobbyListener.leftByWS.add(op1.get(i).uuid);
			}
		}
		
		for(int i = 0;i<op1.size();i++)
		{
			
			if(!ArenaManager.teleportToArena(op1.get(i).name, server))
			{
				System.out.println("Couldn't teleport " + op1.get(i).name);
				return false;
			}
		}
		
		for(int i = 0;i<op2.size();i++)
		{
			removePlayer(op2.get(i).uuid,false,false);
			if(isOnServer(op2.get(i).uuid))
			{
				LobbyListener.leftByWS.add(op2.get(i).uuid);
			}
		}
		
		for(int i = 0;i<op2.size();i++)
		{
			
			if(!ArenaManager.teleportToArena(op2.get(i).name, server))
			{
				System.out.println("Couldn't teleport " + op2.get(i).name);
				return false;
			}
			
		}
		
		ArrayList<UUID> uuid1 = new ArrayList<>();
		for(int i = 0;i<op1.size();i++)
		{
			uuid1.add(op1.get(i).uuid);
		}
		ArrayList<UUID> uuid2 = new ArrayList<>();
		for(int i = 0;i<op2.size();i++)
		{
			uuid2.add(op2.get(i).uuid);
		}
		
		SpectateManager.uploadToMySQL(uuid1, uuid2, server, id,true);
		
		return true;
	}
	
	private boolean isOnServer(UUID uuid)
	{
		for(Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if(p.getUniqueId().equals(uuid))
			{
				return true;
			}
		}
		
		return false;
	}
	
}
