package at.Kingcraft.OnevsOne_lobby.Tournaments;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import MySQL.MySQL;
import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Sounds;
import at.Kingcraft.OnevsOne_lobby.Arenas.ArenaManager;
import at.Kingcraft.OnevsOne_lobby.Duels.ChallangeManager;
import at.Kingcraft.OnevsOne_lobby.Duels.Team;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamManager;
import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;
import at.Kingcraft.OnevsOne_lobby.Lobby.LobbyListener;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messenger;
import at.Kingcraft.OnevsOne_lobby.Scoreboard.MyScoreboardManager;
import at.Kingcraft.OnevsOne_lobby.Special.EnquieryMenu;
import at.Kingcraft.OnevsOne_lobby.Special.MapMenu;
import at.Kingcraft.OnevsOne_lobby.Special.MapSymbol;
import at.Kingcraft.OnevsOne_lobby.Special.MenuManager;
import at.Kingcraft.OnevsOne_lobby.Special.TournamentViewMenu;
import net.md_5.bungee.api.ChatColor;

public class Tournament
{
	private ArrayList<ArrayList<Player>> contestants;
	private ArrayList<Round> rounds;
	private Player leader;
	private MainClass plugin;
	private ArrayList<String> arenaServers;
	private int startCountdownID;
	private int startCountdown = 10;
	private boolean hasStarted = false;
	private boolean isInArena = false;
	int id;
	private Tournament instance;
	private ArrayList<Player> roundSkipper;
	private int teamSize = 1;
	
	public static final int NO_ARENAS = 0;
	public static final int NOT_ENOUGH_PLAYERS = 1;
	public static final int UNKNOWN_ERROR = 2;
	public static final int HAS_STARTED = 3;
	public static final int PLAYER_SIZE = 2;
	
	public Tournament(ArrayList<Player> leader,MainClass plugin,int id)
	{
		this.leader = getLeader(leader);
		teamSize = leader.size();
		contestants = new ArrayList<>();
		this.id = id;
		rounds = new ArrayList<>();
		arenaServers = new ArrayList<>();
		this.plugin = plugin;
		addContestant(leader,false,false);
		
		instance = this;
	}
	
	public int getTeamSize()
	{
		return teamSize;
	}
	
	private Player getLeader(ArrayList<Player> p)
	{
		Team t = TeamManager.getTeam(p.get(0));
		if(t != null)
		{
			return t.getLeader();
		}
		else
		{
			return p.get(0);
		}
	}
	
	public ItemStack contestantToItemStack(int i )
	{
		if(i+1 > contestants.size())
			return new ItemStack(Material.AIR);
		
		ItemStack is = new ItemStack(Material.SKULL_ITEM,1,(short)3);
		SkullMeta sm = (SkullMeta) is.getItemMeta();
		sm.setOwner(contestants.get(i).get(0).getDisplayName());
		
		String name = "";
		
		for(int j = 0;j<contestants.get(i).size();j++)
		{
			name += (contestants.get(i).get(0).getUniqueId().equals(leader.getUniqueId()) ? ChatColor.GOLD : ChatColor.GREEN) + contestants.get(i).get(j).getDisplayName() + (j+1==contestants.get(i).size() ? "" : (ChatColor.WHITE + ", "));
		}
		
		sm.setDisplayName(name);
		is.setItemMeta(sm);
		
		return is;
	}
	
	public ItemStack toItemStack()
	{
		ItemStack is = new ItemStack(Material.SKULL_ITEM,1,(short)3);
		SkullMeta sm = (SkullMeta) is.getItemMeta();
		sm.setOwner(leader.getDisplayName());
		sm.setDisplayName(ChatColor.YELLOW + "Turnier von " + ChatColor.GREEN + leader.getDisplayName());
		ArrayList<String> lore = new ArrayList<>();
		
		for(int i = 0;i<contestants.size();i++)
		{
			String line = "";
			
			for(int j = 0;j<contestants.get(i).size();j++)
			{
				line += ChatColor.WHITE + contestants.get(i).get(j).getDisplayName() + (j+1 == contestants.get(i).size() ? "" : ", ");
			}
			
			lore.add(line);
		}
		
		lore.add(ChatColor.GRAY + "ID: " + id);
		
		sm.setLore(lore);
		is.setItemMeta(sm);
		
		return is;
	}
	
	public String allRoundsToString()
	{
		String str = "";
		for(int i = 0;i<rounds.size();i++)
		{
			str += rounds.get(i).toString() + "|" + "NO_LOSER" + "\n";
		}
		
		return str;
	}
	
	public int getID()
	{
		return id;
	}
	
	private int calculateMaxRoundLevel(int players)
	{
	    int start = 2;
	    int times = 1;
	    int amount = 0;

	    if(players == 2)
	    {
	        return 0;
	    }

	    while(true)
	    {
	        for(int i = 1;i<=times;i++)
	        {
	            if(players == start)
	                return amount;

	            start++;
	        }

	        times*=2;

	        amount++;
	    }
	}
	
	public int getMaxRoundLevel()
	{
		return calculateMaxRoundLevel(contestants.size());
	}
	
	public String contestantsToString()
	{
		String cont = "";
		
		for(int i = 0;i<contestants.size();i++)
		{	
			String cont1 = "";
			
			for(int j = 0;j<contestants.get(i).size();j++)
			{
				cont1 += contestants.get(i).get(j).getUniqueId().toString() + (j+1 == contestants.get(i).size() ? "" : ";");
			}
			
			cont += cont1 + (i+1 == contestants.size() ? "" : "|");
		}
		
		return cont;
	}
	
	public String serversToString()
	{
		String str = "";
		
		for(int i = 0;i<arenaServers.size();i++)
		{
			str += arenaServers.get(i) + (i+1 == arenaServers.size() ? "" : "|");
		}
		
		if(str.equals(""))
		{
			str = "NO_SERVERS";
		}
		
		return str;
	}
	
	public String arenasToString()
	{
		String str = "";
		
		ArrayList<MapSymbol> maps = new ArrayList<>();
		
		for(int i = 0;i<contestants.size();i++)
		{
			for(int j = 0;j<contestants.get(i).size();j++)
			{
				ArrayList<MapSymbol> contMaps = MenuManager.getSettingMenu(contestants.get(i).get(j)).getMapMenu().getMyMaps();
				
				
				for(int u = 0;u<contMaps.size();u++)
				{
					boolean exists = false;
					for(int k = 0;k<maps.size();k++)
					{
						if(maps.get(k).equals(contMaps.get(u)))
						{
							exists = true;
							break;
						}
					}
					if(!exists)
					{
						maps.add(contMaps.get(u));
					}
				}
			}
			
			
		}
		
		if(maps.size() == 0)
		{
			maps.addAll(MapMenu.getAllMaps());
		}
		
		for(int i = 0;i<maps.size();i++)
		{
			str += maps.get(i).getName() + (i+1 == maps.size() ? "" : "|");
		}
		
		
		return str;
	}
	
	public String contestantNamesToString()
	{
		String cont = "";
		
		for(int i = 0;i<contestants.size();i++)
		{
			String cont1 = "";
			
			for(int j = 0;j<contestants.get(i).size();j++)
			{
				cont1 += contestants.get(i).get(j).getName() + (j+1 == contestants.get(i).size() ? "" : ";");
			}
			
			cont += cont1 + (i+1 == contestants.size() ? "" : "|");
		}
		
		return cont;
	}
	
	public String roundsToString()
	{
		String roundsStr = "";
		
		for(int i = 0;i<rounds.size();i++)
		{
			roundsStr += rounds.get(i).toString() + "\n";
		}
		
		return roundsStr;
	}
	
	public boolean isIn(ArrayList<Player> p)
	{
		for(int i = 0;i<contestants.size();i++)
		{
			boolean isIt = true;
			for(int j = 0;j<contestants.get(i).size() && j<p.size();j++)
			{
				if(!contestants.get(i).get(j).getUniqueId().equals(p.get(j).getUniqueId()))
				{
					isIt = false;
					break;
				}
			}
			if(isIt)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void addContestant(ArrayList<Player> p,boolean message,boolean updateMenus)
	{
		if(isIn(p))
		{
			if(message)
			{
				for(int i = 0;i<p.size();i++)
					p.get(i).sendMessage(Messages.isYourTournament);
			}
				
			return;
		}
		
		if(MenuManager.getSettingMenu(leader).getTourSettingMenu().getMaxPlayers() == contestants.size())
		{
			for(int i = 0;i<p.size();i++)
				p.get(i).sendMessage(Messages.tournamentIsFull);
			return;
		}
		
		if(message)
			for(int i = 0;i<contestants.size();i++)
			{
				for(int j = 0;j<contestants.get(i).size();j++)
					contestants.get(i).get(j).sendMessage(Messages.tournamentJoinOther(p));
			}
		
		Tournament t = TournamentManager.getTournament(p);
		if(t != null)
		{
			t.removeContestants(p, true, true,true);
		}
		
		contestants.add(p);
		
		if(plugin.getWaitingSnake().isIn(p))
		{
			plugin.getWaitingSnake().removePlayer(p, true, true);
		}
		
		if(message)
			for(int i = 0;i<p.size();i++)
			{
				p.get(i).sendMessage(Messages.tournamentJoin(leader.getDisplayName()));
				p.get(i).playSound(p.get(i).getLocation(), Sounds.tournamentJoin, Sounds.tournamentJoinVolume, Sounds.DEFAULT_PITCH);
			}
				
		
		if(hasStarted && !isInArena)
		{
			stopCountdown();
		}
		
		for(int i = 0;i<p.size();i++)
			LobbyListener.setupSpawnItems(p.get(i), false, true);
		
		if(!updateMenus)
		{
			return;
		}
		
		for(Player p1 : Bukkit.getServer().getOnlinePlayers())
		{
			EnquieryMenu em = ChallangeManager.getEnquiryMenu(p1);
			em.setStartingTournaments(TournamentManager.getTournaments());
			if(em.isOpen())
				em.updateInventory();
		}
		
		ArrayList<TournamentViewMenu> menus = MenuManager.getTournamentViewMenus();
		for(int i = 0;i<menus.size();i++)
		{
			if(menus.get(i).isOpen())
			{
				if(menus.get(i).getViewedLeader() != null && menus.get(i).getViewedLeader().getUniqueId().equals(leader.getUniqueId()))
				menus.get(i).loadTournament(leader);
			}
		}
		
		if(contestants.size() == MenuManager.getSettingMenu(leader).getTourSettingMenu().getMaxPlayers())
		{
			for(int i = 0;i<contestants.size();i++)
			{
				if(message)
					for(int j = 0;j<contestants.get(i).size();j++)
						contestants.get(i).get(j).sendMessage(Messages.tournamentMaxPlayersHasBeenReached);
			}
			if(hasStarted)
			{
				cancelStartcountdown();
			}
			leader.performCommand("start 0");
		}
		else
		{
			for(Player p1 : Bukkit.getOnlinePlayers())
			{
				MyScoreboardManager.updateScoreboard(p1);
			}
		}
	}
	
	public String roundSkipperToString()
	{
		String str = "";
		
		if(roundSkipper != null)
		{
			String str1 = "";
			String str2 = "";
			for(int i = 0;i<roundSkipper.size();i++)
			{
				str1 += roundSkipper.get(i).getUniqueId().toString() + (i+1==roundSkipper.size() ? "" : ";");
				str2 += roundSkipper.get(i).getName() + (i+1==roundSkipper.size() ? "" : ";");
			}
			
			str += "1|1|" + str1 + "|" + str2 + "}";
		}
		
		if(str.equals(""))
		{
			str = "NO_ROUND_SKIPPER";
		}
		
		return str;
	}
	
	public boolean isLeader(ArrayList<Player> p)
	{	
		return getLeader(p).getUniqueId().equals(leader.getUniqueId());
	}
	
	private boolean is(ArrayList<Player> p1, ArrayList<Player> p2)
	{
		for(int i = 0;i<p1.size() && i<p2.size();i++)
		{
			if(!p1.get(i).getUniqueId().equals(p2.get(i).getUniqueId()))
				return false;
		}
		
		return true;
	}
	
	private void remove(ArrayList<Player> p)
	{
		for(int i = 0;i<contestants.size();i++)
		{
			if(is(contestants.get(i),p))
			{
				contestants.remove(i);
				return;
			}
		}
	}
	
	public void removeContestants(ArrayList<Player> p,boolean message,boolean newLeaderMessage,boolean updateMenus)
	{
		if(!isIn(p))
		{
			return;
		}
		
		remove(p);
		
		if(message)
			for(int i = 0;i<p.size();i++)
				p.get(i).sendMessage(Messages.tournamentLeave);
		
		if(message)
			for(int i = 0;i<contestants.size();i++)
			{
				for(int j = 0;j<contestants.get(i).size();j++)
					contestants.get(i).get(j).sendMessage(Messages.tournamentLeaveOther(p));
			}
		
		if(isLeader(p))
		{
			if(!contestants.isEmpty())
			{
				leader = null;
				
					for(int i = 0;i<contestants.size();i++)
					{
						if(!is(contestants.get(i),p))
						{
							leader = getLeader(contestants.get(i));
							if(!leader.hasPermission("command.turnier.create"))
							{
								leader = null;
							}
						}
						
					}
					
					if(leader == null)
					{
						TournamentManager.deleteTournament(this, true);
					}
					else if(newLeaderMessage)
					{
						leader.sendMessage(Messages.newTournamentLeader);
					}
			}
			else
			{
				TournamentManager.deleteTournament(this, false);
			}
		}
		else if(contestants.isEmpty())
		{
			TournamentManager.deleteTournament(this, false);
		}
		
		if(hasStarted && !isInArena)
		{
			stopCountdown();
		}
		
		for(int i = 0;i<p.size();i++)
			LobbyListener.setupSpawnItems(p.get(i), false, false);
		
		if(!updateMenus)
		{
			return;
		}
		
		for(Player p1 : Bukkit.getServer().getOnlinePlayers())
		{
			EnquieryMenu em = ChallangeManager.getEnquiryMenu(p1);
			em.setStartingTournaments(TournamentManager.getTournaments());
			if(em.isOpen())
				em.updateInventory();
		}
		
		if(leader != null)
		{
			if(!contestants.isEmpty())
			LobbyListener.setupSpawnItems(leader, false, true);
			
			ArrayList<TournamentViewMenu> menus = MenuManager.getTournamentViewMenus();
			for(int i = 0;i<menus.size();i++)
			{
				if(menus.get(i).isOpen())
				{
					if(menus.get(i).getViewedLeader() != null && menus.get(i).getViewedLeader().getUniqueId().equals(leader.getUniqueId()))
					menus.get(i).loadTournament(leader);
				}
			}
			
				for(Player p1 : Bukkit.getOnlinePlayers())
					MyScoreboardManager.updateScoreboard(p1);
		}
		
		
	}
	
	public boolean hasStarted()
	{
		return hasStarted;
	}
	
	private void stopCountdown()
	{
		Bukkit.getScheduler().cancelTask(startCountdownID);
		hasStarted = false;
		leader.sendMessage(Messages.startCancelled);
		
		for(int i = 0;i<arenaServers.size();i++)
		{
			ArenaManager.moveToFree(arenaServers.get(i));
		}
	}
	
	private int calculateRounds()
	{
		roundSkipper = null;
		rounds.clear();
		
		int arenas = contestants.size()%2 == 0 ? contestants.size()/2 : (contestants.size()-1)/2;
		
		if(!ArenaManager.check(arenas))
		{
			return NO_ARENAS;
		}
		
		for(int i = 0;i<arenas;i++)
		{
			String server = ArenaManager.giveArena();
			ArenaManager.moveToUsed(server);
			arenaServers.add(server);
		}
		
		int roundSkipperIndex = 0;
		if(contestants.size() % 2 != 0)
		{
			roundSkipperIndex = new Random().nextInt(contestants.size());
			roundSkipper = contestants.get(roundSkipperIndex);
		}
		
		if(roundSkipper != null)
		{
			for(int i = 0;i<roundSkipper.size();i++)
				roundSkipper.get(i).sendMessage(Messages.skipRound);
			ArrayList<Player> temp = contestants.get(contestants.size()-1);
			hardRemove(contestants.get(contestants.size()-1));
			hardRemove(roundSkipper);
			hardAdd(temp);
			hardAdd(roundSkipper);
		}
		
		
		int u = 0;
		for(int i = 0;i<contestants.size();i+=2)
		{
			if(i+1 > contestants.size()-1)
			{
				break;
			}
			rounds.add(new Round(contestants.get(i),
							     contestants.get(i+1),
							     arenaServers.get(u),
							     MapMenu.getRandomArena(MenuManager.getSettingMenu(leader).getMapMenu().getMyMaps(),null),
							     id,
							     -1,
							     KitManager.getChoosenKitKit(leader),
							     MenuManager.getSettingMenu(leader).getTourSettingMenu().getRounds()));
			u++;
		}
		
		
		return -1;
	}
	
	private void hardRemove(ArrayList<Player> p)
	{
		for(int i = 0;i<contestants.size();i++)
		{
			if(is(contestants.get(i),p))
			{
				contestants.remove(i);
				return;
			}
		}
	}
	
	private void hardAdd(ArrayList<Player> p)
	{
		for(int i = 0;i<contestants.size();i++)
		{
			if(is(contestants.get(i),p))
			{
				return;
			}
		}
		
		contestants.add(p);
	}
	
	private void startCountdown()
	{
		startCountdownID = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			
			@Override
			public void run()
			{
					if(startCountdown < 0)
					{
						Bukkit.getScheduler().cancelTask(startCountdownID);
						return;
					}
					if(startCountdown == 0)
					{
						try
						{
							if(calculateRounds() == NO_ARENAS)
							{
								leader.sendMessage(Messages.noFreeArena);
								cancelStartcountdown();
								return;
							}
							
							for(int i = 0;i<contestants.size();i++)
							{
								for(int j = 0;j<contestants.get(i).size();j++)
									contestants.get(i).get(j).sendMessage(Messages.tournamentStarts);
							}
							
							isInArena = true;
							
							TournamentManager.uploadTournament(instance);
							
							startRounds();
							
							if(roundSkipper != null)
							{
								for(int i = 0;i<roundSkipper.size();i++)
								{
									LobbyListener.tournamentRoundSkipper.put(roundSkipper.get(i).getUniqueId(), id);
									MySQL.getInstance().getConnection().prepareStatement("INSERT INTO Duel_JoinSpectators (UUID,HomeServer) VALUES ('" + roundSkipper.get(i).getUniqueId().toString() + "','" + MainClass.getInstance().serverName + "')").executeUpdate();
									Bukkit.getScheduler().runTaskLater(plugin,new SendRoundSkipperRun(roundSkipper.get(i), arenaServers.get(0)), 10);
								}
							}
								
							TournamentManager.deleteTournament(instance, false);
							
							Bukkit.getScheduler().cancelTask(startCountdownID);
						}
						catch(Exception e)
						{
							for(int i = 0;i<contestants.size();i++)
							{
								for(int j = 0;j<contestants.get(i).size();j++)
									if(!isOnServer(contestants.get(i).get(j)))
									{
										String[] args = new String[2];
										args[0] = contestants.get(i).get(j).getName();
										args[1] = plugin.serverName;
										Messenger.sendMessage(null, "BungeeCord", "ConnectOther", args);
									}
							}
							
							
							try
							{
								PreparedStatement ps = plugin.getMySQL().getConnection().prepareStatement("DELETE FROM Duel_Tournaments WHERE ID = ?");
								ps.setInt(1, id);
								
								ps.executeUpdate();
								
								ps = plugin.getMySQL().getConnection().prepareStatement("DELETE FROM Duel_Challenges WHERE Tournament = ?");
								ps.setInt(1, id);
								
								ps.executeUpdate();
								
								ps = plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Duel_Spectators WHERE TournamentID = ?");
								ps.setInt(1, id);
								
								ResultSet rs = ps.executeQuery();
								
								while(rs.next())
								{
									String name = rs.getString(2);
									String homeServer = rs.getString(4);
									
									String[] args = new String[2];
									args[0] = name;
									args[1] = homeServer;
									
									Messenger.sendMessage(null, "BungeeCord", "ConnectOther", args);
								}
								
								ps = plugin.getMySQL().getConnection().prepareStatement("DELETE FROM Duel_Spectators WHERE TournamentID = ?");
								ps.setInt(1, id);
								
								ps.executeUpdate();
									
								
							}
							catch (SQLException e1)
							{
								e1.printStackTrace();
							}
							
							for(int i = 0;i<arenaServers.size();i++)
							{
								ArenaManager.sendServerToMySQL(arenaServers.get(i), 1);
							}
							
							for(int i = 0;i<contestants.size();i++)
							{
								for(int j = 0;j<contestants.get(i).size();j++)
									contestants.get(i).get(j).sendMessage(ChatColor.RED + "An Error Accoured");
							}
							
							leader.sendMessage(ChatColor.RED + "Try Again");
							
							e.printStackTrace();
						}
						
						
						return;
					}
					
					if((startCountdown >= 60 && startCountdown % 60 == 0) || (startCountdown < 60 && startCountdown >= 30 && startCountdown % 30 == 0) || (startCountdown < 30 && startCountdown >= 10 && startCountdown % 10 == 0) || (startCountdown < 10))
					{
						for(int i = 0;i<contestants.size();i++)
						{
							for(int j = 0;j<contestants.get(i).size();j++)
								contestants.get(i).get(j).sendMessage(Messages.tournamentStartsTimer(String.valueOf(startCountdown)));
						}
					}
					
					
					startCountdown--;
			}
		}, 0, 20*1).getTaskId();
	}
	
	private boolean isOnServer(Player player)
	{
		for(Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if(p.getUniqueId().equals(player.getUniqueId()))
			{
				return true;
			}
		}
		
		return false;
	}

	private void startRounds()
	{
		for(int i = 0;i<rounds.size();i++)
		{
			rounds.get(i).start();
		}
	}
	
	public int start(int time)
	{
		try
		{
			if(contestants.size() < PLAYER_SIZE)
			{
				return NOT_ENOUGH_PLAYERS;
			}
			
			if(hasStarted)
			{
				cancelStartcountdown();
				return start(time);
			}
			
			/*int calc = calculateRounds();
			if(calc != -1)
			{
				return calc;
			}*/
			
			startCountdown = time;
			if(startCountdown < 0)
				startCountdown = 0;
			
			if(!(startCountdown % 60 == 0 || startCountdown % 30 == 0 || startCountdown % 10 == 0 || startCountdown < 10))
			{
				for(int i = 0;i<contestants.size();i++)
					for(int j = 0;j<contestants.get(i).size();j++)
						contestants.get(i).get(j).sendMessage(Messages.tournamentStartsTimer("" +startCountdown));
			}
			
			
			startCountdown();
			
			hasStarted = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			return UNKNOWN_ERROR;
		}
		
		
		
		
		return -1;
	}
	
	public boolean cancelStartcountdown()
	{
		if(!hasStarted)
		{
			return false;
		}
		
		Bukkit.getScheduler().cancelTask(startCountdownID);
		hasStarted = false;
		
		for(int i = 0;i<arenaServers.size();i++)
		{
			ArenaManager.moveToFree(arenaServers.get(i));
		}
		
		arenaServers.clear();
		
		return true;
	}
	
	public Player getLeader()
	{
		return leader;
	}
	
	public ArrayList<ArrayList<Player>> getContestants()
	{
		return contestants;
	}
	
	public String getQualiPoints()
	{
		String str = "";
		
		str += "1#";
		
		for(int i = 0;i<contestants.size();i++)
		{
			String str1 = "";
			for(int j = 0;j<contestants.get(i).size();j++)
			{
				str1 += contestants.get(i).get(j).getUniqueId().toString() + (j+1==contestants.get(i).size() ? "" : ";");
			}
			
			str += str1 + ":" + 0 + (i == contestants.size()-1 ? "" : "#");
		}
		
		return str;
	}
}
