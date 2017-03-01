package at.kingcraft.OnevsOne_arena.Tournaments;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;

import MySQL.MySQL;
import at.kingcraft.OnevsOne_arena.MainClass;
import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_arena.Duels.DuelManager;
import at.kingcraft.OnevsOne_arena.Kits.Kit;
import at.kingcraft.OnevsOne_arena.Menus.DuelsMenu;
import at.kingcraft.OnevsOne_arena.Menus.MenuManager;
import at.kingcraft.OnevsOne_arena.Messaging.Messenger;
import at.kingcraft.OnevsOne_arena.Waiting.WaitingHouse;
import at.kingcraft.OnevsOne_setup.Maps.Map;
import net.md_5.bungee.api.ChatColor;

public class TournamentManager
{
	private static ArrayList<Tournament> tournaments;
	private static MySQL mysql;
	private static MainClass plugin;
	public static ArrayList<UUID> leftBySpectator;
	private static ArrayList<Spectator> spectators;
	private static int specCheckID;
	private static boolean needsSpecCheckStart;
	public static boolean needsSpecUpdate;
	
	public static void setup(MySQL mysql,MainClass plugin)
	{
		tournaments = new ArrayList<>();
		TournamentManager.mysql = mysql;
		TournamentManager.plugin = plugin;
		spectators = new ArrayList<>();
		leftBySpectator = new ArrayList<>();
		needsSpecCheckStart = true;
		needsSpecUpdate = false;
		
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Duel_TournamentWinner (UUID VARCHAR(100))");
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void removeSpectatorArray(Player p)
	{
		for(int i = 0;i<spectators.size();i++)
		{
			if(spectators.get(i).player.getUniqueId().equals(p.getUniqueId()))
			{
				spectators.remove(i);
				return;
			}
		}
	}
	
	public static void removeSpectatorArray(TourPlayer p)
	{
		for(int i = 0;i<spectators.size();i++)
		{
			if(spectators.get(i).player.getUniqueId().equals(p.uuid))
			{
				spectators.remove(i);
				return;
			}
		}
	}
	
	public static void hideAllSpectators(Player p)
	{	
		for(Spectator s : spectators)
		{
			if(s.player.getUniqueId().equals(p.getUniqueId()))
			{
				continue;
			}
			
			p.hidePlayer(s.player);
		}
	}
	
	public static ArrayList<Spectator> getSpecs()
	{
		return spectators;
	}
	
	
	public static boolean isSpectator(Player p,boolean Mysql)
	{
		if(p == null)
		{
			return false;
		}
		
		for(Spectator s : spectators)
		{
			if(s.uuid.equals(p.getUniqueId()))
			{
				return true;
			}
		}
		
		if(Mysql)
		{
			try
			{
				PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT UUID FROM Duel_Spectators WHERE UUID = ?");
				ps.setString(1, p.getUniqueId().toString());
				
				if(ps.executeQuery().first())
				{
					return true;
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		return false;
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
	
	public static void deleteAllSpecators(int tourID,boolean sendBack)
	{
		try
		{
			PreparedStatement ps;
			if(sendBack)
			{
				ps = mysql.getConnection().prepareStatement("SELECT * FROM Duel_Spectators WHERE TournamentID = ?");
				ps.setInt(1, tourID);
				
				ResultSet rs = ps.executeQuery();
				
				while(rs.next())
				{
					Spectator s = new Spectator(rs.getString(2),UUID.fromString(rs.getString(1)),rs.getInt(3), rs.getString(4));
					s.sendBack();
				}
			}
			
			ps = mysql.getConnection().prepareStatement("DELETE FROM Duel_Spectators WHERE TournamentID = ?");
			ps.setInt(1, tourID);
			
			ps.executeUpdate();
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void addSpectatorArray(Spectator s)
	{
		for(int i = 0;i<spectators.size();i++)
		{
			if(spectators.get(i).uuid.equals(s.uuid))
			{
				return;
			}
		}
		
		spectators.add(s);
		
		if(needsSpecCheckStart)
		{
			checkSpectators();
			needsSpecCheckStart = false;
		}
		
	}
	
	public static Spectator checkSpectator(Player p)
	{
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT * FROM Duel_Spectators WHERE UUID = ?");
			ps.setString(1, p.getUniqueId().toString());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				Spectator s = new Spectator(p, rs.getInt(3), rs.getString(4));
				
				return s;
			}
			
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static void setSpecatorMode(Player p)
	{
		p.setHealth(p.getMaxHealth());
		if(!p.getGameMode().equals(GameMode.CREATIVE))
			p.setGameMode(GameMode.CREATIVE);
		
		
		for(Player p1 : Bukkit.getServer().getOnlinePlayers())
		{
			p1.hidePlayer(p);
		}
	}
	
	static public void updateSpectateInvs(Player p)
	{
        p.getInventory().clear();
		
		// Inventories
		Duel d = DuelManager.getFirstDuel();
		int roundsPos = 8;
		if(d!=null)
		{	
			int invPos = 0;
			ItemStack is;
			LeatherArmorMeta im;
			ArrayList<String> lore = new ArrayList<>();
			lore.add(ChatColor.YELLOW + "RECHTS" + ChatColor.WHITE + "-Klick um das Inventar anzuschauen");
			
			for(int i = 0;i<d.getAlive1().size() && invPos < 36;i++)
			{
				is = new ItemStack(Material.LEATHER_HELMET);
				im =  (LeatherArmorMeta) is.getItemMeta();
				im.setColor(Color.RED);
				im.setDisplayName(ChatColor.RED + d.getAlive1().get(i).getDisplayName());
				im.setLore(lore);
				is.setItemMeta(im);
				
				p.getInventory().setItem(invPos, is);
				
				invPos++;
			}
			
			for(int i = 0;i<d.getAlive2().size() && invPos < 36;i++)
			{
				is = new ItemStack(Material.LEATHER_HELMET);
				im =  (LeatherArmorMeta) is.getItemMeta();
				im.setColor(Color.BLUE);
				im.setDisplayName(ChatColor.BLUE + d.getAlive2().get(i).getDisplayName());
				im.setLore(lore);
				is.setItemMeta(im);
				
				p.getInventory().setItem(invPos, is);
				
				invPos++;
			}
			
			if(invPos > 8)
				roundsPos = invPos;
		}
		
		ItemStack rounds = new ItemStack(Material.GLOWSTONE_DUST);
		{
			ItemMeta im = rounds.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "Runden");
			rounds.setItemMeta(im);
		}
		
		if(roundsPos < 36)
			p.getInventory().setItem(roundsPos, rounds);
		
	}
	
	private static void setupSpectateItems(Player p,int id)
	{
		updateSpectateInvs(p);
		// Menus
		MenuManager.setDuelsMenu(p, new DuelsMenu(p,id,true));
		
	}
	
	private static boolean tournIsInMySQL(int id)
	{
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT ID FROM Duel_Tournaments WHERE ID = ?");
			ps.setInt(1, id);
			
			if(ps.executeQuery().first())
			{
				return true;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static void addSpectator(Spectator s,boolean tele,boolean thisServer)
	{
		if(!tournIsInMySQL(s.tournamentID))
		{
			s.sendBack();
		}
		
		uploadSpectator(s);
		
		Bukkit.getScheduler().runTask(MainClass.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				setSpecatorMode(s.player);
			}
		});
		
		
		setupSpectateItems(s.player,s.tournamentID);
		
		if(tele)
		{
			Bukkit.getScheduler().runTask(MainClass.getInstance(), new Runnable() {
				
				@Override
				public void run()
				{
					teleportToDuel(s,thisServer);	
				}
			});
		}
			
		
		addSpectatorArray(s);
		
		if(needsSpecCheckStart)
		{
			checkSpectators();
			needsSpecCheckStart = false;
		}
	}
	
	public static void uploadSpectator(Spectator s)
	{
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT TournamentID FROM Duel_Spectators WHERE UUID = ?");
			ps.setString(1, s.player.getUniqueId().toString());
			
			if(ps.executeQuery().first())
			{
				return;
			}
			
			ps = mysql.getConnection().prepareStatement("INSERT INTO Duel_Spectators (UUID,Name,TournamentID,HomeServer) VALUES (?,?,?,?)");
			ps.setString(1, s.uuid.toString());
			ps.setString(2, s.name);
			ps.setInt(3, s.tournamentID);
			ps.setString(4, s.homeServer);
			
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private static void teleportToDuel(Spectator s,boolean thisServer)
	{
		Duel d = DuelManager.getFirstDuel();
		if(d == null && thisServer)
		{
			return;
		}
		
		if(d == null)
		{
			Tournament t = TournamentManager.getTournamentFromMySQL(null, s.tournamentID, "null", new Kit(s.player,1,false),5,10);
			if(t == null)
			{
				Bukkit.getScheduler().runTaskLaterAsynchronously(plugin,new CheckSpecDuelRun(s, plugin), 20*2);
			}
			else
			{
				if(t.getRounds().isEmpty())
				{
					Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new CheckSpecTourRun(s, t), 20*2);
				}
				else
				{
					teleportToDuelServer(s, t);
				}
			}	
		}
		else
		{
			realTeleport(d, s.player);
		}
	}
	
	public static boolean isSpectator(TourPlayer p)
	{
		for(int i = 0;i<spectators.size();i++)
		{
			if(spectators.get(i).player.getUniqueId().equals(p.uuid))
			{
				return true;
			}
		}
		
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT UUID FROM Duel_Spectators WHERE UUID = ?");
			ps.setString(1, p.uuid.toString());
			
			if(ps.executeQuery().first())
			{
				return true;
			}
		
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static void removeSpectator(TourPlayer p)
	{	
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("DELETE FROM Duel_Spectators WHERE UUID = ?");
			ps.setString(1, p.uuid.toString());
			
			ps.executeUpdate();
			
			removeSpectatorArray(p);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	
	public static boolean isOnServer(UUID uuid)
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
	
	private static void teleportToDuelServer(Spectator s,Tournament t)
	{
		if(isOnServer(s.player.getUniqueId()))
		{
			return;
		}
		
		String server = t.getRounds().get(0).getServer();
		leftBySpectator.add(s.player.getUniqueId());
		Messenger.sendMessage(s.player, "BungeeCord", "Connect", server);
	}
	
	
	private static void realTeleport(Duel d,Player p)
	{
		Map map = d.getMap();
		
		if(map == null)
		{
			Location loc = WaitingHouse.getWaitngSpawn();
			if(loc != null)
				p.teleport(loc);
			
			return;
		}
		
		if(!map.isLoaded())
		{
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
				
				@Override
				public void run()
				{
					int iterations = 0;
					do
					{
						iterations++;
						
						if(iterations == 3)
						{
							return;
						}
						
						if(map.isLoaded())
						{
							Location mid = map.getMid();
							
							p.teleport(mid);
							return;
						}
						
						try
						{
							Thread.sleep(2000);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
							return;
						}
						
					}while(!map.isLoaded());
				}
			}, 20*2);
		}
		else
		{
			Location mid = map.getMid();
			
			p.teleport(mid);
		}
	}
	

	public static void playerLeaves(Player p,int tournamentID)
	{
		Tournament t = getTournamentFromMySQL(p, tournamentID, "null", new Kit(p,1,false),5,10);
		
		if(t != null)
		{
			t.removePlayer(new TourPlayer(p.getName(), p.getUniqueId()));
			
			ArrayList<TourPlayer> rS = t.getRoundSkipper(t.isQualiRound() ? t.getCurentQualiRound() : t.getRoundLevel(),t.isQualiRound());
			if(rS != null)
			{
				for(int i = 0;i<rS.size();i++)
				{
					if(rS.get(i).uuid.equals(p.getUniqueId()))
					{
						rS.remove(i);
						if(rS.isEmpty())
							rS = null;
						
						t.setRoundSkipper(rS,t.isQualiRound() ? t.getCurentQualiRound() : t.getRoundLevel(),t.isQualiRound());
						break;
					}
				}
			}
			
			TournamentManager.updateTournament(t, false);
		}
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
	
	private static boolean isSpectatorMySQL(Spectator s)
	{
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT UUID FROM Duel_Spectators WHERE UUID = ?");
			ps.setString(1, s.player.getUniqueId().toString());
			
			if(ps.executeQuery().first())
			{
				return true;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static Spectator isSpectatorMySQL(Player p)
	{
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT * FROM Duel_Spectators WHERE UUID = ?");
			ps.setString(1, p.getUniqueId().toString());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				Spectator s = new Spectator(p, rs.getInt(3), rs.getString(4));
				
				return s;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void checkSpectators()
	{
		specCheckID = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
			
			@Override
			public void run()
			{
				for(Player p : Bukkit.getServer().getOnlinePlayers())
				{
					if(isSpectator(p, false))
					{
						continue;
					}
					
					Spectator s = isSpectatorMySQL(p);
					
					if(s != null && p.getHealth() != 0.0)
					{
						addSpectator(s,true,false);
						continue;
					}
					
				}
				
				if(spectators.isEmpty())
				{
					needsSpecCheckStart = true;
					Bukkit.getScheduler().cancelTask(specCheckID);
					return;
				}
				
				for(int i = 0;i<spectators.size();i++)
				{
					Spectator s = spectators.get(i);
					if(!isSpectatorMySQL(s))
					{
						removeSpectator(s.player);
						i--;
						continue;
					}
					
					Tournament t = null;
					
					if(needsSpecUpdate || TournamentManager.getTournament(s.tournamentID) == null)
					{
						t = TournamentManager.getTournamentFromMySQL(null, s.tournamentID, "null", new Kit(s.player,1,false),6,10);
						if(t == null)
						{
							s.sendBack();
						}
						else
						{
							if(t.getRounds().isEmpty())
							{
								continue;
							}
							else
							{
								if(t.getRounds().get(0).getServer() != null && t.getRounds().get(0).getServer().equals(plugin.serverName))
								{
									if(isOnServer(s.player.getUniqueId()))
									{
										Duel d = DuelManager.getFirstDuel();
										if(d != null)
										{
											Map map = d.getMap();
											if(map != null)
											{
												Location loc = map.getMid();
												
												if(!loc.getWorld().equals(s.player.getWorld()))
												{
													Bukkit.getScheduler().runTask(plugin, new Runnable() {
														
														@Override
														public void run()
														{
															s.player.teleport(loc);
														}
													});
												}
												else
												{
													s.player.teleport(loc);
												}
											}
											continue;
										}
									}
									else
									{
										continue;
									}
								}
								
								String server = t.getRounds().get(0).getServer();
								leftBySpectator.add(s.player.getUniqueId());
								Messenger.sendMessage(s.player, "BungeeCord", "Connect", server);
							}
						}
					}
					
					if(t != null)
					{
						TournamentManager.deleteTournamentArray(t);
					}
				}
				
				needsSpecUpdate = false;
				
				
			}
		}, 20*2, 20*2).getTaskId();
	}
	
	
	public static Tournament getTournamentFromMySQL(Player p,int id,String homeServer,Kit kit,int mode,int time)
	{
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT * FROM Duel_Tournaments WHERE ID = ?");
			ps.setInt(1, id);
			
			ResultSet rs = ps.executeQuery();
			
			Tournament t = null;
			while(rs.next())
			{
				// Players
				ArrayList<ArrayList<TourPlayer>> players = new ArrayList<>();
				
				String uuids = rs.getString(1),names = rs.getString(4);
				
				if(!uuids.equals("NO_PLAYERS"))
				{
					String[] playerUUIDs = splitString('|', uuids);
					
					// UUIDs
					for(int i = 0;i<playerUUIDs.length;i++)
					{
						String[] teamUUIDs = playerUUIDs[i].split(";");
						players.add(new ArrayList<>());
						for(int j = 0;j<teamUUIDs.length;j++)
						{
							players.get(i).add(new TourPlayer("null",UUID.fromString(teamUUIDs[j])));
						}
					}
					
					
					// Names
					String[] playerNames = splitString('|', names);
					
					for(int i = 0;i<playerNames.length;i++)
					{
						String[] teamNames = playerNames[i].split(";");
						for(int j = 0;j<teamNames.length;j++)
						{
							players.get(i).get(j).name = teamNames[j];
						}
					}
				}
				
				
				// Rounds
				ArrayList<Round> rounds = new ArrayList<>();
				String roundStr = rs.getString(3);
				
				if(!roundStr.equals("NO_ROUNDS"))
				{
					String[] roundsStr = roundStr.split("\n");
					
					for(int i = 0;i<roundsStr.length;i++)
					{
						rounds.add(Round.fromString(roundsStr[i],plugin,homeServer,kit,false,mode,time));
					}
				}
				
				// Losers
				ArrayList<ArrayList<TourPlayer>> loser = new ArrayList<>();
				String loserStr = rs.getString(5);
				
				if(!loserStr.equals("NO_LOSER"))
				{
					String[] loserArray = loserStr.split("\n");
					
					for(int i = 0;i<loserArray.length;i++)
					{
						String[] uuidsAndNames = loserArray[i].split("\\|");
						String[] teamUuids = uuidsAndNames[0].split(";");
						String[] teamNames = uuidsAndNames[1].split(";");
						
						loser.add(new ArrayList<>());
						
						for(int j = 0;j<teamUuids.length;j++)
						{
							loser.get(i).add(new TourPlayer(teamNames[j], UUID.fromString(teamUuids[j])));
						}
					}
				}
				
				// RoundSkipper
				HashMap<Integer,ArrayList<TourPlayer>> roundSkipperQuali = new HashMap<>();
				HashMap<Integer,ArrayList<TourPlayer>> roundSkipperKo = new HashMap<>();
				String roundSkipperStr = rs.getString(6);
				
				if(!roundSkipperStr.equals("NO_ROUND_SKIPPER"))
				{
					boolean quali;
					int level = -1;
					String[] RSuuids;
					String[] RSnames;
					String[] one = roundSkipperStr.split("}");
					for(int i = 0;i<one.length;i++)
					{
						String[] two = one[i].split("\\|");
						quali = two[0].equals("1");
						level = Integer.valueOf(two[1]);
						RSuuids = two[2].split(";");
						RSnames = two[3].split(";");
						
						if(quali)
							roundSkipperQuali.put(level, new ArrayList<>());
						else
							roundSkipperKo.put(level, new ArrayList<>());
						
						for(int j = 0;j<RSuuids.length;j++)
						{
							if(quali)
							{
								roundSkipperQuali.get(level).add(new TourPlayer(RSnames[j],UUID.fromString(RSuuids[j])));
							}
							else
							{
								roundSkipperKo.get(level).add(new TourPlayer(RSnames[j],UUID.fromString(RSuuids[j])));
							}
						}	
					}
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
				
				//AllRounds
				String allRoundsStr = rs.getString(10);
				ArrayList<Round> allRounds = new ArrayList<>();
				
				if(!allRoundsStr.equals("NO_ROUNDS"))
				{
					String[] allRoundStr = allRoundsStr.split("\n");
					
					for(int i = 0;i<allRoundStr.length;i++)
					{
						allRounds.add(Round.fromString(allRoundStr[i], plugin, homeServer, kit,true,mode,time));
					}
				}
				
				// Kit Mode
				int kitMode = rs.getInt(11);
				
				// Tournament Creation
				t = new Tournament(p,id,plugin,homeServer,kit,mode,kitMode,time);
				
				// Qualification Rounds
				t.decodeQualiRounds(rs.getString(12));
				
				for(int i = 0;i<players.size();i++)
				{
					t.addContenstant(players.get(i), false);
				}
				for(int i = 0;i<rounds.size();i++)
				{
					t.addRound(rounds.get(i));
				}
				for(int i = 0;i<loser.size();i++)
				{
					t.lose(loser.get(i));
				}
				t.setRoundSkipper(roundSkipperQuali,true);
				t.setRoundSkipper(roundSkipperKo, false);
				if(servers != null)
				{
					for(int i = 0;i<servers.length;i++)
					{
						t.addServer(servers[i]);
					}
				}
				for(int i = 0;i<arenas.length;i++)
				{
					t.addArena(arenas[i]);
				}
				t.setMaxRoundLevel(maxRoundLevel);
				t.setAllRounds(allRounds);
				
				for(int i = 0;i<tournaments.size();i++)
				{
					if(t.getID() == id)
					{
						tournaments.remove(i);
						break;
					}
				}
				
				tournaments.add(t);
				
				return t;
				
			}
			
			return t;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Tournament getTournament(int id)
	{
		for(int i = 0;i<tournaments.size();i++)
		{
			if(tournaments.get(i).getID() == id)
			{
				return tournaments.get(i);
			}
		}
		
		return null;
	}
	
	
	
	public static void deleteTournament(Tournament t,boolean message)
	{
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("DELETE FROM Duel_Tournaments WHERE ID = ?");
			ps.setInt(1, t.getID());
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		int ID = t.getID();
		
		tournaments.remove(t);
		
		deleteAllSpecators(ID,true);
	}
	
	public static void deleteTournamentArray(Tournament t)
	{
		tournaments.remove(t);
		
		needsSpecUpdate = true;
	}
	
	public static Tournament getTournament(Player p)
	{
		for(int i = 0;i<tournaments.size();i++)
		{
			for(int u = 0;u<tournaments.get(i).getContestants().size();u++)
			{
				for(int j = 0;j<tournaments.get(i).getContestants().get(u).size();j++)
				{
					if(tournaments.get(i).getContestants().get(u).get(j).uuid.equals(p.getUniqueId()))
					{
						return tournaments.get(i);
					}
				}
				
			}
		}
		
		return null;
	}
	
	public static boolean checkIfRoundFinished(int id)
	{
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT Rounds FROM Duel_Tournaments WHERE ID = ?");
			ps.setInt(1, id);
			
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				if(rs.getString(1).equals("NO_ROUNDS"))
				{
					return true;
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean updateTournament(Tournament t,boolean ifNotExists)
	{
		if(!mysql.isConnected())
		{
			return false;
		}
		
		try
		{
			PreparedStatement ps;
			
			if(ifNotExists)
			{
				ps = mysql.getConnection().prepareStatement("SELECT Rounds FROM Duel_Tournaments WHERE ID = ?");
				ps.setInt(1, t.getID());
				
				ResultSet rs = ps.executeQuery();
				
				while(rs.next())
				{
					if(!rs.getString(1).equals("NO_ROUNDS"))
					{
						return false;
					}
				}
			}
			
			ps = mysql.getConnection().prepareStatement("UPDATE Duel_Tournaments SET Contestants = ?,Rounds = ?,Names = ?,Loser = ?,RoundSkipper = ?,Servers = ?,AllRounds = ?,QualiRounds = ? WHERE ID = ?");
			ps.setString(1, t.contestantsToString());
			ps.setString(2, t.roundsToString());
			ps.setString(3, t.contestantNamesToString());
			ps.setString(4, t.loserToString());
			ps.setString(5, t.roundSkipperToString());
			ps.setString(6, t.serversToString());
			ps.setString(7, t.allRoundsToString());
			ps.setString(8, t.getQualiPoints());
			ps.setInt(9, t.getID());
			
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		
		return true;
	}

	public static void removeSpectator(Player p)
	{	
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("DELETE FROM Duel_Spectators WHERE UUID = ?");
			ps.setString(1, p.getUniqueId().toString());
			
			ps.executeUpdate();
			
			removeSpectatorArray(p);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static Tournament getFirstTournament()
	{
		if(!tournaments.isEmpty())
		{
			return tournaments.get(0);
		}
		
		return null;
	}
	
	public static void pushSpectatorsBack()
	{
		if(getSpecs().isEmpty())
			return;
		
		Duel d = DuelManager.getFirstDuel();
		if(d == null)
			return;
		
		ArrayList<Player> duelPlayers = new ArrayList<>();
		
		duelPlayers.addAll(d.getP1());
		duelPlayers.addAll(d.getP2());
		
		double minDistance = 5.0;
		double pushStrength = 1.0;
		
		
		for(int i = 0;i<duelPlayers.size();i++)
		{
			for(int j = 0;j < getSpecs().size();j++)
			{
				if(duelPlayers.get(i).getUniqueId().equals(getSpecs().get(j).player.getUniqueId()))
					continue;
				
				if(duelPlayers.get(i).getLocation().distance(getSpecs().get(j).player.getLocation()) < minDistance)
				{
					// Push Back
					
					Vector dir = duelPlayers.get(i).getLocation().toVector().subtract(getSpecs().get(j).player.getLocation().toVector());
					
					dir = dir.normalize();
					
					dir.multiply(-pushStrength);
					
					getSpecs().get(j).player.setVelocity(dir);
				}
			}
		}
	}
}
