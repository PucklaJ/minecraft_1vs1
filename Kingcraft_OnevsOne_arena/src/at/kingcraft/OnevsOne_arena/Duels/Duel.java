package at.kingcraft.OnevsOne_arena.Duels;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import MySQL.MySQL;
import at.kingcraft.OnevsOne_arena.MainClass;
import at.kingcraft.OnevsOne_arena.Sounds;
import at.kingcraft.OnevsOne_arena.Challenges.ChallangeManager;
import at.kingcraft.OnevsOne_arena.Challenges.Challenge;
import at.kingcraft.OnevsOne_arena.Commands.GiveUpCommand;
import at.kingcraft.OnevsOne_arena.Kits.ChooseKit;
import at.kingcraft.OnevsOne_arena.Kits.Kit;
import at.kingcraft.OnevsOne_arena.Kits.KitSettings;
import at.kingcraft.OnevsOne_arena.Listener.DuelListener;
import at.kingcraft.OnevsOne_arena.Messaging.Messages;
import at.kingcraft.OnevsOne_arena.Messaging.Messenger;
import at.kingcraft.OnevsOne_arena.Scoreboard.MyScoreboardManager;
import at.kingcraft.OnevsOne_arena.Tournaments.Spectator;
import at.kingcraft.OnevsOne_arena.Tournaments.Tournament;
import at.kingcraft.OnevsOne_arena.Tournaments.TournamentManager;
import at.kingcraft.OnevsOne_arena.Waiting.WaitingHouse;
import at.kingcraft.OnevsOne_setup.Maps.Map;
import at.kingcraft.OnevsOne_setup.Maps.MapManager;
import de.xAdler.Title;
import de.xAdler.TitleAPI.Colors;
import net.md_5.bungee.api.ChatColor;

public class Duel {
	
	public static final int P1 = 0;
	public static final int P2 = 1;
	public static final int NO = 2;
	private static final int START_TIMER = 3;
	private static final int RANKED = -5;
	private ArrayList<Player> p1;
	private ArrayList<Player> p2;
	private ArrayList<Player> alive1;
	private ArrayList<Player> alive2;
	private ArrayList<Player> winner,loser;
	private BukkitTask waitCD;
	private BukkitTask startCD;
	private int waitTime = 10;
	private int cdTime = START_TIMER;
	private int maxP1Size;
	private int maxP2Size;
	public final int id;
	private boolean started = false;
	private boolean ended = false;
	private Map map;
	private Challenge c;
	int countdown = 3;
	private Duel instance;
	private int mode;
	private int p1Wins,p2Wins;
	boolean hasFinished;
	boolean isRestarting;
	private int maxWins;
	private int canJumpID;
	private boolean ffa;
	private ArrayList<DuelSpec> spectators;
	private int timeSec;
	private int timeMin;
	private int maxTimeMin;
	private int timeCountdownID = -1;
	private boolean timer = false;
	private int kitMode;
	public static final int OWN_KITS = 0;
	public static final int ENEMY_KITS = 1;
	private Kit kit = null;
	private ArrayList<UUID> leftPlayers;
	
	public Duel(Challenge c,JavaPlugin plugin,int mode)
	{
		p1 = new ArrayList<Player>();
		p2 = new ArrayList<Player>();
		alive1 = new ArrayList<Player>();
		alive2 = new ArrayList<Player>();
		spectators = new ArrayList<>();
		leftPlayers = new ArrayList<>();
		this.c = c;
		this.mode = mode;
		ffa = c.getTournamentID() == -2;
		
		if(ffa)
			cdTime = 10;
		
		this.id = c.ID;
		map = MapManager.getMap(c.arenaName);
		
		maxP1Size = c.getChallengersUUID().size();
		maxP2Size = c.getChallengedUUID().size();
		
		waitingCountdown(plugin);
		
		this.instance = this;
		
		timeSec = 0;
		timeMin = 0;
		maxTimeMin = c.getTime();
		
		if(isTournament())
		{
			TournamentManager.getTournamentFromMySQL(null, getTournamentID(), c.getPreviousServer(0), getKit(),mode,timeMin);
			kitMode = TournamentManager.getTournament(c.getTournamentID()).getKitMode();
		}
		else
		{
			kitMode = -1;
		}
		
		p1Wins = p2Wins = 0;
		hasFinished = false;
		isRestarting = false;
		canJumpID = -1;
		
		calculateMaxWins();
		timeCountdown();
		
	}
	
	public void activateTimer(int min,int sec,boolean msg)
	{
		timeMin = min;
		timeSec = sec;
		if(timeMin < 0)
		{
			timeMin = 0;
		}
		if(timeSec < 0)
		{
			timeSec = 0;
		}
		else if(timeSec > 60)
		{
			timeSec = 60;
		}
		
		if(timeMin == 0 && timeSec < 20)
		{
			timeSec = 20;
		}
		
		if(msg)
		{
			for(int i =0;i<p1.size();i++)
			{
				p1.get(i).sendMessage(Messages.nearestOneWins);
				p1.get(i).sendMessage(Messages.oneMinuteLeft);
			}
			
			for(int i =0;i<p2.size();i++)
			{
				p2.get(i).sendMessage(Messages.nearestOneWins);
				p2.get(i).sendMessage(Messages.oneMinuteLeft);
			}
		}
		
		MyScoreboardManager.updateScoreboard();
			
		
		timer = true;
	}
	
	public boolean timerStarted()
	{
		return timer;
	}
	
	private void onTimeOut()
	{	
		double minLength = alive1.get(0).getLocation().distance(map.getMid());
		UUID minU = alive1.get(0).getUniqueId();
		
		for(int i = 0;i<alive1.size();i++)
		{
			if(alive1.get(i).getLocation().distance(map.getMid()) < minLength)
			{
				minLength = alive1.get(i).getLocation().distance(map.getMid());
				minU = alive1.get(i).getUniqueId();
			}
		}
		
		for(int i = 0;i<alive2.size();i++)
		{
			if(alive2.get(i).getLocation().distance(map.getMid()) < minLength)
			{
				minLength = alive2.get(i).getLocation().distance(map.getMid());
				minU = alive2.get(i).getUniqueId();
			}
		}
		
		for(int i = 0;i<alive1.size();i++)
		{
			if(!minU.equals(alive1.get(i).getUniqueId()))
			{
				handleDeath(alive1.get(i),null,false,false);
			}
		}
		
		for(int i = 0;i<alive2.size();i++)
		{
			if(!minU.equals(alive2.get(i).getUniqueId()))
			{
				handleDeath(alive2.get(i),null,false,false);
			}
		}
	}
	
	public void timeCountdown()
	{
		timeCountdownID = Bukkit.getScheduler().runTaskTimerAsynchronously(MainClass.getInstance(), new Runnable() {
			
			@Override
			public void run()
			{
					if(!isStarted() || hasEnded())
						return;
				
					if(timer)
					{
						if(--timeSec < 0)
						{
							if(--timeMin < 0)
							{
								onTimeOut();
								
								return;
							}
							
							timeSec = 59;
						}
					}
					else
					{
						if(++timeSec == 60)
						{
							timeMin++;
							timeSec = 0;
						}
					}
					
					if(timer)
					{
						if(timeMin == 0)
						{
							if(timeSec == 10 || timeSec <= 5)
							{
								for(int i = 0;i<p1.size();i++)
								{
									p1.get(i).playSound(p1.get(i).getLocation(), Sounds.endMatchTimer, Sounds.endMatchTimerVolume, Sounds.DEFAULT_PITCH);
								}
								for(int i = 0;i<p2.size();i++)
								{
									p2.get(i).playSound(p2.get(i).getLocation(), Sounds.endMatchTimer, Sounds.endMatchTimerVolume, Sounds.DEFAULT_PITCH);
								}
							}
						}
					}
					
					
					
					Bukkit.getScheduler().runTask(MainClass.getInstance(), new Runnable() {
						
						@Override
						public void run() {
							MyScoreboardManager.updateScoreboard();
							
						}
					});
			}
				
		}, 20, 20).getTaskId();
	}
	
	public int getTimeSec()
	{
		return timeSec;
	}
	
	public int getTimeMin()
	{
		return timeMin;
	}
	
	public int getMaxRounds()
	{
		return mode;
	}
	
	private void calculateMaxWins()
	{
		maxWins = mode/2;
		
		if(maxWins*2 < mode)
		{
			maxWins++;
		}
	}
	
	private boolean allAlive()
	{
		for(int i = 0;i<p1.size();i++)
		{
			if(p1.get(i).getHealth() == 0.0)
			{
				return false;
			}
		}
		
		for(int i = 0;i<p2.size();i++)
		{
			if(p2.get(i).getHealth() == 0.0)
			{
				return false;
			}
		}
		
		return true;
	}
	

	private void revive(Player p)
	{
		p.spigot().respawn();
	}
	
	private void waitingCountdown(JavaPlugin plugin)
	{
		Bukkit.getScheduler().runTask(MainClass.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				MyScoreboardManager.resetScoreboard();	
			}
		});
		
		
		
		waitCD = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			
			@Override
			public void run()
			{	
				if(!P1isOnline() && !P2isOnline())
				{
					waitTime = -1;
				}

				if(p1.size() == maxP1Size && P1isOnline() && p2.size() == maxP2Size && P2isOnline() && allAlive())
				{
					if(map == null)
					{
						map = MapManager.getMap(c.arenaName);
						if(map == null)
						{
							for(int i = 0;i<p1.size();i++)
							{
								p1.get(i).sendMessage(Messages.arenaNotThere);
							}
							for(int i = 0;i<p2.size();i++)
							{
								p2.get(i).sendMessage(Messages.arenaNotThere);
							}
							
							Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
								
								@Override
								public void run()
								{
									sendBackToLobby();
									GiveUpCommand.sendBackToFreeServers();
									
								}
							}, 20*5);
						}
					}
					Bukkit.getScheduler().cancelTask(waitCD.getTaskId());
					if(map != null)
					{
						MyScoreboardManager.updateScoreboard();
						if(p1Wins == 0 && p2Wins == 0)
						{
							sendEnemyAndKitMessages();
							
							Bukkit.getScheduler().runTaskLater(MainClass.getInstance(), new Runnable() {
								
								@Override
								public void run() {
									startCountdown(plugin);
									
								}
							}, 20*3);
						}
						else
						{
							startCountdown(plugin);
						}
						
					}
					
					
				}
				else
				{
					for(int i = 0;i<p1.size();i++)
					{
						if(waitTime <= 5)
							p1.get(i).sendMessage(Messages.waitingForOtherPlayers(String.valueOf(waitTime)));
						if(p1.get(i).getHealth() == 0.0)
						{
							revive(p1.get(i));
						}
					}
					
					for(int i = 0;i<p2.size();i++)
					{
						if(waitTime <= 5)
							p2.get(i).sendMessage(Messages.waitingForOtherPlayers(String.valueOf(waitTime)));
						if(p2.get(i).getHealth() == 0.0)
						{
							revive(p2.get(i));
						}
					}
					
					if(p1.size() > 0 || p2.size() > 0)
						waitTime--;
				}
				
				if(waitTime < 0)
				{
					for(Entity e : map.getMid().getWorld().getEntities())
					{
						if(!(e instanceof Player))
							e.remove();
					}
					
					
					Bukkit.getScheduler().cancelTask(waitCD.getTaskId());
					
						if(!allOnline())
						{
							endDuel(null,false);
							stopCountdown();
							Challenge c = getChallenge();
							ChallangeManager.deleteChallenge(c.ID);
							GiveUpCommand.handleFinishedDuel(DuelManager.getDuel(id), null, c);
						}
						else
						{
							if(isTournament())
							{
								if(P1isOnline() || P2isOnline())
								{
									endDuel(null,true);
									stopCountdown();
									Challenge c = getChallenge();
									ChallangeManager.deleteChallenge(c.ID);
									GiveUpCommand.handleFinishedDuel(DuelManager.getDuel(id), null, c);
								}
							}
							else
							{
								Player p = getRandomPlayer();
								GiveUpCommand.giveUp(p,null);
							}
						}
				}
				
			}
		}, 0, 20*1);
	}
	
	private boolean allOnline()
	{
		for(int i = 0;i<c.getChallengersUUID().size();i++)
		{
			boolean found = false;
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(p.getUniqueId().equals(c.getChallengersUUID().get(i)))
				{
					found = true;
					break;
				}	
			}
			
			if(!found)
			{
				return false;
			}
		}
		
		for(int i = 0;i<c.getChallengedUUID().size();i++)
		{
			boolean found = false;
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(p.getUniqueId().equals(c.getChallengedUUID().get(i)))
				{
					found = true;
					break;
				}
			}
			
			if(!found)
			{
				return false;
			}
		}
		
		return true;
	}
	
	public boolean hasEnded()
	{
		return ended;
	}
	
	public boolean hasFinished()
	{
		return hasFinished;
	}
	
	private Player getRandomPlayer()
	{
		ArrayList<Player> players = new ArrayList<>();
		players.addAll(p1);
		players.addAll(p2);
		
		for(int i = 0;i<players.size();i++)
		{
			if(players.get(i).getHealth() == 0.0)
			{
				return players.get(i);
			}
		}
		
		Random rand = new Random();
		int index = rand.nextInt(players.size());
		return players.get(index);
	}
	
	private static String getPreKitUUID(int number)
	{
		if(number >= 0)
		{
			return "null";
		}
		else if(number >= -7)
		{
			return "PreKits1";
		}
		else if(number >= -14)
		{
			return "PreKits2";
		}
		else if(number >= -21)
		{
			return "PreKits3";
		}
		else if(number >= -28)
		{
			return "PreKits4";
		}
		else
		{
			return "null";
		}
	}
	
	private static String getPreKitName(int number)
	{
		if(number >= 0)
		{
			return "Kit"+number;
		}
		else if(number >= -7)
		{
			return "Kit" + (-number-7*0);
		}
		else if(number >= -14)
		{
			return "Kit" + (-number-7*1);
		}
		else if(number >= -21)
		{
			return "Kit" + (-number-7*2);
		}
		else if(number >= -28)
		{
			return "Kit" + (-number-7*3);
		}
		else
		{
			return "null";
		}
	}
	
	private static Kit getKit(UUID u,int number)
	{
		try
		{
			String qry = number >= 0 ? ("SELECT Kit" + (number+1) + " FROM Duel_Kits WHERE UUID = ?") : ("SELECT " + getPreKitName(number) + " FROM Duel_Kits WHERE UUID = '" + getPreKitUUID(number) + "'");
			
			PreparedStatement ps = MySQL.getInstance().getConnection().prepareStatement(qry);
			
			if(number >= 0)
				ps.setString(1, u.toString());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				Kit kit = Kit.decodeMySQLKit(null, rs.getString(1));
				return kit;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public int getKitMode()
	{
		return kitMode;
	}
	
	private void setupKit(Player p)
	{
		if(c.getKit().isDifKit())
		{
			kit = Kit.getDifKit(p);
			if(kit != null)
				kit.kitItemsToInventory(p);
			else
			{
				kit = new Kit(p,1,true);
				ItemStack is = new ItemStack(Material.PAPER);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName("null");
				is.setItemMeta(im);
				kit.addItem(0, is);
				
				kit.kitItemsToInventory(p);
			}
		}
		else
		{
			if(kit != null)
			{
				kit.kitItemsToInventory(p);
			}
			else
			{
				if(kitMode == -1 || kitMode == OWN_KITS)
				{
					kit = c.getKit();
					c.getKit().kitItemsToInventory(p);
				}
				else if(kitMode == ENEMY_KITS)
				{
					Random rand = new Random();
					int team = rand.nextInt(2) == 0 ? P1 : P2;
					int player = team == P1 ? rand.nextInt(c.getChallengersUUID().size()) : rand.nextInt(c.getChallengedUUID().size());
					int kitNum = 0;
					UUID kitU = null;
					
					ChooseKit ckit = Kit.loadChoosenKit(team == P1 ? c.getChallengersUUID().get(player) : c.getChallengedUUID().get(player));
					
					if(ckit != null)
					{
						kitU = ckit.otherOPlayer != null ? ckit.otherOPlayer.getUniqueId() : (team == P1 ? c.getChallengersUUID().get(player) : c.getChallengedUUID().get(player));
						kitNum = ckit.otherOPlayer != null ? ckit.otherNumber : ckit.myNumber;
					}
					else
					{
						kitU = (team == P1 ? c.getChallengersUUID().get(player) : c.getChallengedUUID().get(player));
						kitNum = rand.nextInt(5);
					}
					
					kit = getKit(kitU,kitNum);
					
					if(kit == null)
					{
						kit = c.getKit();
					}
					
					kit.kitItemsToInventory(p);
				}
				else
				{
					kit = c.getKit();
					kit.kitItemsToInventory(p);
				}
			}
		}
	}
	
	public void setupPlayer(Player p,boolean asynchron)
	{
		if(isSpectator(p,false))
		{
			return;
		}
		
		if(!asynchron)
		{
			if(!p.getGameMode().equals(GameMode.SURVIVAL))
				p.setGameMode(GameMode.SURVIVAL);
		}
		else
		{
			Bukkit.getScheduler().runTask(MainClass.getInstance(), new Runnable() {
				
				@Override
				public void run()
				{
					if(!p.getGameMode().equals(GameMode.SURVIVAL))
						p.setGameMode(GameMode.SURVIVAL);
				}
			});
		}
		
		setupKit(p);
		
		if(!asynchron)
		{
			p.setAllowFlight(false);
			
			Kit kit = DuelListener.getKit(p);
			
			if(kit.getSettings().contains(KitSettings.DOUBLE_JUMP))
			{
				p.setAllowFlight(true);
			}
		}
		else
		{
			Bukkit.getScheduler().runTask(MainClass.getInstance(),new Runnable() {
				
				@Override
				public void run()
				{
					p.setAllowFlight(false);
					if(DuelListener.getKit(p).getSettings().contains(KitSettings.DOUBLE_JUMP))
					{
						p.setAllowFlight(true);
					}	
				}
			});
		}
		
		
		if(maxP1Size != 0 || maxP2Size != 0)
		{
			Bukkit.getScheduler().runTaskLater(MainClass.getInstance(), new Runnable() {
				
				@Override
				public void run()
				{
					if(map != null)
					{
						Vector lookToMid = map.getMid().toVector().subtract(p.getEyeLocation().toVector());
						
						Location loc = p.getLocation().clone();
						loc.setDirection(lookToMid.normalize());
						
						p.teleport(loc);
					}
				}
			},10);
		}
		else
		{
			Vector lookToMid = map.getMid().toVector().subtract(p.getEyeLocation().toVector());
			
			Location loc = p.getLocation().clone();
			loc.setDirection(lookToMid.normalize());
			
			p.teleport(loc);
		}
		
	}

	public void sendBackToLobby()
	{
		for(int i = 0;i<p1.size();i++)
		{
			String[] args = new String[1];
			Challenge c = getChallenge();
			
			
			int u = 0;
			for(Player p : c.getChallengers())
			{
				if(p.getUniqueId().equals(p1.get(i).getUniqueId()))
				{
					args[0] = c.getPreviousServer(u);
				}
				u++;
			}
			for(Player p : c.getChallenged())
			{
				if(p.getUniqueId().equals(p1.get(i).getUniqueId()))
				{
					args[0] = c.getPreviousServer(u);
				}
				u++;
			}
			if(args[0] == null || args[0] == "")
			{
				args[0] = c.getPreviousServer(0);
			}
			
			
			Messenger.sendMessage(p1.get(i), "BungeeCord", "Connect", args);
		}
		
		for(int i = 0;i<p2.size();i++)
		{
			String[] args = new String[1];
			Challenge c = getChallenge();
			
			
			int u = 0;
			for(Player p : c.getChallengers())
			{
				if(p.getUniqueId().equals(p2.get(i).getUniqueId()))
				{
					args[0] = c.getPreviousServer(u);
				}
				u++;
			}
			for(Player p : c.getChallenged())
			{
				if(p.getUniqueId().equals(p2.get(i).getUniqueId()))
				{
					args[0] = c.getPreviousServer(u);
				}
				u++;
			}
			if(args[0] == null || args[0] == "")
			{
				args[0] = c.getPreviousServer(0);
			}
			
			
			Messenger.sendMessage(p2.get(i), "BungeeCord", "Connect", args);
		}
		
		// Spectators
		for(int i = 0;i<spectators.size();i++)
		{
			if(!isPartOf(spectators.get(i).player))
				spectators.get(i).sendBack();
		}
		
		spectators.clear();
	}
	
	private ArrayList<Player> getEnimies(Player p)
	{
		ArrayList<Player> enimies = new ArrayList<>();
		int role = getRole(p);
		if(role == NO)
			return enimies;
		
		if(!ffa)
		{
			return role == P1 ? p2 : p1;
		}
		else
		{
			for(int i = 0;i<p1.size();i++)
			{
				if(!p1.get(i).getUniqueId().equals(p.getUniqueId()))
					enimies.add(p1.get(i));
			}
			for(int i = 0;i<p2.size();i++)
			{
				if(!p2.get(i).getUniqueId().equals(p.getUniqueId()))
					enimies.add(p2.get(i));
			}
		}
		
		return enimies;
	}
	
	private boolean P1isOnline()
	{
		
		for(int i = 0;i<p1.size();i++)
		{
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(p.getUniqueId().equals(p1.get(i).getUniqueId()))
					return true;
			}
		}
		
		return false;
	}
	
	private boolean P2isOnline()
	{
		if(p2.isEmpty())
			return true;
			
		for(int i = 0;i<p2.size();i++)
		{
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(p.getUniqueId().equals(p2.get(i).getUniqueId()))
					return true;
			}
		}
		
		return false;
	}
	
	private void sendEnemyAndKitMessages()
	{
		
		Tournament t = isTournament() ?  TournamentManager.getTournamentFromMySQL(p1.get(0), c.getTournamentID(), getHomeServer(p1.get(0)), getKit(), this.kitMode, maxTimeMin) : null;
		
		for(int i = 0;i<p1.size();i++)
		{
			ArrayList<Player> enimies = getEnimies(p1.get(i));
			
			String against = "";
			int elo = 0;
			
			for(int u = 0;u<enimies.size();u++)
			{
				against += enimies.get(u).getDisplayName() + (u==enimies.size()-1 ? "" : "; ");
				elo += getELO(enimies.get(u));
			}
			
			if(ffa)
				p1.get(i).sendMessage(Messages.youAreFightingAgainstFFA(against));
			else if(getTournamentID() == RANKED)
				p1.get(i).sendMessage(Messages.youAreFightingAgainstRanked(against, elo));
			else
				p1.get(i).sendMessage(Messages.youAreFightingAgainst(against));
			
			Kit kit = DuelListener.getKit(p1.get(i));
			
			p1.get(i).sendMessage(Messages.yourKit(kit.getName(true,!(kit.getOwnerName().equals("Server") || kit.getOwnerName().equals("Verschieden"))),kit.getKitSettings()));
			
			if(t!= null)
			{
				if(t.getCurentQualiRound() == 1)
				{
					p1.get(i).sendMessage(Messages.qualificationPhaseStarts);
				}
				else if(t.getCurentQualiRound() == -1 && t.getRound(p1.get(i)).getRoundLevel() == 0)
				{
					p1.get(i).sendMessage(Messages.koPhaseStarts);
				}
			}
		}
		
		for(int i = 0;i<p2.size();i++)
		{
			ArrayList<Player> enimies = getEnimies(p2.get(i));
			
			String against = "";
			int elo = 0;
			
			for(int u = 0;u<enimies.size();u++)
			{
				against += enimies.get(u).getDisplayName() + (u==enimies.size()-1 ? "" : "; ");
				elo += getELO(enimies.get(u));
			}
			
			if(ffa)
				p2.get(i).sendMessage(Messages.youAreFightingAgainstFFA(against));
			else if(getTournamentID() == RANKED)
				p2.get(i).sendMessage(Messages.youAreFightingAgainstRanked(against, elo));
			else
				p2.get(i).sendMessage(Messages.youAreFightingAgainst(against));
			
			Kit kit = DuelListener.getKit(p2.get(i));
			
			p2.get(i).sendMessage(Messages.yourKit(kit == null ? "1" : kit.getName(true,!(kit.getOwnerName().equals("Server") || kit.getOwnerName().equals("Verschieden"))),kit == null ? "" : kit.getKitSettings()));
			
			if(t!= null)
			{
				if(t.getCurentQualiRound() == 1)
				{
					p2.get(i).sendMessage(Messages.qualificationPhaseStarts);
				}
				else if(t.getCurentQualiRound() == -1 && t.getRound(p2.get(i)).getRoundLevel() == 0)
				{
					p2.get(i).sendMessage(Messages.koPhaseStarts);
				}
			}
		}
	}
	
	private void startCountdown(JavaPlugin plugin)
	{
		if(!ffa)
		{
			Bukkit.getScheduler().runTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					
					for(int i = 0;i<p1.size();i++)
					{
						p1.get(i).setWalkSpeed(0.0f);
					}
					for(int i = 0;i<p2.size();i++)
					{
						p2.get(i).setWalkSpeed(0.0f);
					}
					
				}
			});
		}
		
		if(!isTournament())
		{
			for(int i = 0;i<spectators.size();i++)
			{
				updateSpecInvs(spectators.get(i).player);
			}
		}
		else
		{
			for(int i = 0;i<TournamentManager.getSpecs().size();i++)
			{
				TournamentManager.updateSpectateInvs(TournamentManager.getSpecs().get(i).player);
			}
		}
		
		for(int i = 0;i<p1.size();i++)
		{
			DuelListener.specOnRespawn.remove(p1.get(i).getUniqueId());
		}
		for(int i = 0;i<p2.size();i++)
		{
			DuelListener.specOnRespawn.remove(p2.get(i).getUniqueId());
		}
		
		
		countdown = 2;
		startCD = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,new Runnable() {
			
			@Override
			public void run() {
				if(!map.isLoaded())
				{
					for(int i = 0;i<p1.size();i++)
					{
						p1.get(i).sendMessage(Messages.arenaIsLoading);
						p1.get(i).teleport(WaitingHouse.getWaitngSpawn());
					}
					
					for(int i = 0;i<p2.size();i++)
					{
						p2.get(i).sendMessage(Messages.arenaIsLoading);
						p2.get(i).teleport(WaitingHouse.getWaitngSpawn());
					}
					
					while(true)
					{
						if(map.isLoaded())
						{
							for(int i = 0;i<p1.size();i++)
							{
								p1.get(i).sendMessage(Messages.arenaHasLoaded);
								p1.get(i).teleport(WaitingHouse.getWaitngSpawn());
							}
							
							for(int i = 0;i<p2.size();i++)
							{
								p2.get(i).sendMessage(Messages.arenaHasLoaded);
								p2.get(i).teleport(WaitingHouse.getWaitngSpawn());
							}
							break;
						}
						
						try
						{
							Thread.sleep(500);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
					
					for(int i = 0;i<p1.size();i++)
					{
						p1.get(i).teleport(map.getSpawn1());
					}
					
					for(int i = 0;i<p2.size();i++)
					{
						p2.get(i).teleport(map.getSpawn2());
					}
					
				}
				if(cdTime <= 0)
				{
					for(int i = 0;i<p1.size();i++)
					{
						p1.get(i).sendMessage(Messages.duelStarts);
						p1.get(i).playSound(p1.get(i).getLocation(), Sounds.duelStart, Sounds.duelStartVolume, Sounds.DEFAULT_PITCH);
					}
					
					for(int i = 0;i<p2.size();i++)
					{
						p2.get(i).sendMessage(Messages.duelStarts);
						p2.get(i).playSound(p2.get(i).getLocation(), Sounds.duelStart, Sounds.duelStartVolume, Sounds.DEFAULT_PITCH);
					}
					
					if(c.getTime() != -1)
					{
						Bukkit.getScheduler().runTask(MainClass.getInstance(), new Runnable() {
							
							@Override
							public void run() {
								activateTimer(c.getTime(),0,false);
							}
						});
						
					}
					
					Bukkit.getScheduler().runTask(plugin, new Runnable() {
						
						@Override
						public void run()
						{
							if(!ffa)
							{
								for(int i = 0;i<p1.size();i++)
								{
									for(Player p : Bukkit.getOnlinePlayers())
									{
										if(p.getUniqueId().equals(p1.get(i)))
											continue;
										
										if(!isSpectator(p,true))
										{
											p1.get(i).showPlayer(p);
										}
										else
										{
											p1.get(i).hidePlayer(p);
										}
											
										if(!isSpectator(p1.get(i),true))
										{
											p.showPlayer(p1.get(i));
										}
										else
										{
											p.hidePlayer(p1.get(i));
										}
											
									}
								}
								
								for(int i = 0;i<p2.size();i++)
								{
									for(Player p : Bukkit.getOnlinePlayers())
									{
										if(p.getUniqueId().equals(p2.get(i)))
											continue;
										
										if(!isSpectator(p,true))
										{
											p2.get(i).showPlayer(p);
										}
										else
										{
											p2.get(i).hidePlayer(p);
										}
											
										if(!isSpectator(p2.get(i),true))
										{
											p.showPlayer(p2.get(i));
										}
										else
										{
											p.hidePlayer(p2.get(i));
										}
											
									}
								}
							}
							else
							{
								for(int i = 0;i<p1.size();i++)
								{
									for(Player p : Bukkit.getOnlinePlayers())
									{
										if(p1.get(i).getUniqueId().equals(p.getUniqueId()))
										{
											continue;
										}
										
										if(!isSpectator(p,true))
										{
											p1.get(i).showPlayer(p);
										}
										else
										{
											p1.get(i).hidePlayer(p);
										}
											
										if(!isSpectator(p1.get(i),true))
										{
											p.showPlayer(p1.get(i));
										}
										else
										{
											p.hidePlayer(p1.get(i));
										}
											
									}
								}
							}
							
							
							
							
						}
					});
					
					
					if(!ffa)
					{
						Bukkit.getScheduler().runTask(plugin, new Runnable() {
							
							@Override
							public void run() {
								for(int i = 0;i<p1.size();i++)
								{
									p1.get(i).setWalkSpeed(0.2f);
								}
								for(int i = 0;i<p2.size();i++)
								{
									p2.get(i).setWalkSpeed(0.2f);
								}
								
							}
						});
					}
					
					
					started = true;
					
					countdown = 1;
					Bukkit.getScheduler().cancelTask(startCD.getTaskId());
					
					canJumpID = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
						
						@Override
						public void run()
						{
							for(int i = 0;i<alive1.size();i++)
							{
								if(!alive1.get(i).getWorld().getBlockAt(alive1.get(i).getLocation().add(0.0, -1.0, 0.0)).getType().equals(Material.AIR) && !DuelListener.canDoubleJump.contains(alive1.get(i).getUniqueId()))
								{
									DuelListener.canDoubleJump.add(alive1.get(i).getUniqueId());
								}
							}
							
							for(int i = 0;i<alive2.size();i++)
							{
								if(!alive2.get(i).getWorld().getBlockAt(alive2.get(i).getLocation().add(0.0, -1.0, 0.0)).getType().equals(Material.AIR) && !DuelListener.canDoubleJump.contains(alive2.get(i).getUniqueId()))
								{
									DuelListener.canDoubleJump.add(alive2.get(i).getUniqueId());
								}
							}
						}
					}, 0, 10).getTaskId();
					
					return;
				}
				
				for(int i = 0;i<p1.size();i++)
				{
					if(isSpectator(p1.get(i), true))
					{
						if(isTournament())
						{
							TournamentManager.removeSpectator(p1.get(i));
						}
						else
						{
							removeSpectator(p1.get(i));
						}
						
						setupPlayer(p1.get(i), true);
					}
				}
				
				for(int i = 0;i<p2.size();i++)
				{
					if(isSpectator(p2.get(i), true))
					{
						if(isTournament())
						{
							TournamentManager.removeSpectator(p2.get(i));
						}
						else
						{
							removeSpectator(p2.get(i));
						}
						
						setupPlayer(p2.get(i),true);
					}
				}
				
				if(!P1isOnline())
				{
					p2Wins = maxWins;
					endDuel(p1.get(0),true);
					stopCountdown();
					Challenge c = ChallangeManager.getChallenge(p1.get(0));
					ChallangeManager.deleteChallenge(c.ID);
					GiveUpCommand.handleFinishedDuel(DuelManager.getDuel(id), p1.get(0), c);
					return;
				}
				else if(!P2isOnline())
				{
					p1Wins = maxWins;
					endDuel(p2.get(0),true);
					stopCountdown();
					Challenge c = ChallangeManager.getChallenge(p2.get(0));
					ChallangeManager.deleteChallenge(c.ID);
					GiveUpCommand.handleFinishedDuel(DuelManager.getDuel(id), p2.get(0), c);
					return;
				}
				
				
				for(int i = 0;i<p1.size();i++)
				{
					//p1.get(i).sendMessage(Messages.duelStartsTimer(String.valueOf(cdTime)));
					if(p1.get(i).getGameMode().equals(GameMode.CREATIVE))
					{
						Bukkit.getScheduler().runTask(plugin,new ResetSpecDuelRun(p1.get(i), instance));
					}
					p1.get(i).playSound(p1.get(i).getLocation(), Sounds.duelTimer, Sounds.duelTimerVolume, Sounds.DEFAULT_PITCH);
				}
				
				for(int i = 0;i<p2.size();i++)
				{
					//p2.get(i).sendMessage(Messages.duelStartsTimer(String.valueOf(cdTime)));
					if(p2.get(i).getGameMode().equals(GameMode.CREATIVE))
					{
						Bukkit.getScheduler().runTask(plugin,new ResetSpecDuelRun(p2.get(i), instance));
					}
					p2.get(i).playSound(p2.get(i).getLocation(), Sounds.duelTimer, Sounds.duelTimerVolume, Sounds.DEFAULT_PITCH);
				}
				
				cdTime--;
				
				
			}
		} ,0,20*1);
	}
	
	public void stopCountdown()
	{
		if(countdown == 3)
		{
			Bukkit.getScheduler().cancelTask(waitCD.getTaskId());
		}
		else if(countdown == 2)
		{
			Bukkit.getScheduler().cancelTask(startCD.getTaskId());
		}
		
		Bukkit.getScheduler().cancelTask(canJumpID);
		Bukkit.getScheduler().cancelTask(timeCountdownID);
	}
	

	public Challenge getChallenge()
	{
		return c;
	}
	
	public Kit getKit()
	{
		return kit;
	}
	
	public int getTournamentID()
	{
		return c.getTournamentID();
	}
	
	private void handleWinner(Player p)
	{
		p.sendMessage(Messages.youHaveWon);
		p.playSound(p.getLocation(), Sounds.duelWin, Sounds.duelWinVolume, Sounds.DEFAULT_PITCH);
		DuelListener.removeFromOnSpawn(p.getUniqueId());
		updateStatistics(p, 0, 0, 1, 0,1,0);
	}
	
	private void handleLoser(Player p)
	{
		p.sendMessage(Messages.youHaveLost);
		p.playSound(p.getLocation(), Sounds.duelLose, Sounds.duelLoseVolume, Sounds.DEFAULT_PITCH);
		DuelListener.removeFromOnSpawn(p.getUniqueId());
		updateStatistics(p, 0, 0, 0, 1,1,0);
	}
	
	public static int getELO(Player p)
	{
		return getELO(p.getUniqueId());
	}
	
	public static int getELO(UUID u)
	{
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT ELO FROM Duel_RankedELO WHERE UUID = ? LIMIT 1");
			ps.setString(1, u.toString());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				return rs.getInt(1);
			}
			
			ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("INSERT INTO Duel_RankedELO (UUID,ELO) VALUES(?,100)");
			ps.setString(1, u.toString());
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return 100;
	}
	
	private void addELO(Player p,int elo)
	{
		int curElo = getELO(p);
		
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("UPDATE Duel_RankedELO SET ELO = ? WHERE UUID = ?");
			ps.setInt(1, curElo+elo);
			ps.setString(2, p.getUniqueId().toString());
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private int calculateELO(int elo1,int elo2)
	{
		if(elo1 > elo2) // Der mit mehr ELO gewinnt
		{
			double elo2mapped = (double)elo2 / (double)elo1 * 3000.0;
			
			double percent2 = (1.0/30.0) * elo2mapped;
			double deltaPercent = 100.0 - percent2;
			
			double elo = (10.0 - 10.0 * (deltaPercent/100.0));
			
			if((double)(int)elo < elo)
			{
				return ((int)elo)+1;
			}
			
			return (int)elo;
		}
		else if(elo1 < elo2) // Der mit weniger ELO gewinnt
		{
			double elo1mapped = (double)elo1 / (double)elo2 * 3000.0;
			
			double percent1 = (1.0/150.0)*elo1mapped;
			double deltaPercent = 20.0 - percent1;
			
			double elo = (10.0 + (double)elo2 * (deltaPercent/100.0));
			
			if((double)(int)elo < elo)
			{
				return ((int)elo)+1;
			}
			
			return (int)elo;
		}
		else 
		{
			return 10;
		}
	}
	
	public void endDuel(Player p,boolean playerOnline)
	{
		if(ended)
			return;
		
		for(int i = 0;i<spectators.size();i++)
		{
			boolean found = false;
			for(Player p1 : Bukkit.getOnlinePlayers())
			{
				if(p1.getUniqueId().equals(spectators.get(i).player.getUniqueId()))
					found=true;
			}
			
			if(!found)
			{
				spectators.remove(i);
				i--;
			}
				
		}
		
		for(int i = 0;i<spectators.size();i++)
		{
			if(isPartOf(spectators.get(i).player.getUniqueId()))
			{
				removeSpectator(spectators.get(i).player);
				i--;
			}
		}
		
		if(playerOnline)
		{
			for(int i = 0;i<p1.size();i++)
			{
				if(p1.get(i).getWalkSpeed() == 0.0f)
					p1.get(i).setWalkSpeed(0.2f);
			}
			
			for(int i = 0;i<p2.size();i++)
			{
				if(p2.get(i).getWalkSpeed() == 0.0f)
					p2.get(i).setWalkSpeed(0.2f);
			}
			
			if(ffa)
			{
				if(p!=null)
				{
					handleWinner(alive1.get(0));
					
					ArrayList<Player> Loser = new ArrayList<>();
					
					for(int i = 0;i<p1.size();i++)
					{
						if(!p1.get(i).getUniqueId().equals(alive1.get(0).getUniqueId()))
						{
							handleLoser(p1.get(i));
							Loser.add(p1.get(i));
						}
					}
					
					p1Wins++;
					winner = alive1;
					loser = Loser;
				}
				else
				{
					double minLength = map.getMid().distance(p1.get(0).getLocation());
					Player win = p1.get(0);
					
					for(int i = 0;i<p1.size();i++)
					{
						if(map.getMid().distance(p1.get(i).getLocation()) < minLength)
						{
							minLength = map.getMid().distance(p1.get(i).getLocation());
							win = p1.get(i);
						}
					}
					
					ArrayList<Player> Loser = new ArrayList<>();
					
					handleWinner(win);
					for(int i = 0;i<p1.size();i++)
					{
						if(!p1.get(i).getUniqueId().equals(win.getUniqueId()))
						{
							handleLoser(p1.get(i));
							Loser.add(p1.get(i));
						}
					}
					p1Wins++;
					ArrayList<Player> Winner = new ArrayList<>();
					Winner.add(win);
					winner = Winner;
					loser = Loser;
				}
					
			}
			else
			{
				if(p!=null)
				{
					if(getRole(p) == P1)
					{
						for(int i = 0;i<p2.size();i++)
						{
							handleWinner(p2.get(i));
						}
						p2Wins++;
						for(int i = 0;i<p1.size();i++)
						{
							handleLoser(p1.get(i));
						}
						
						winner = p2;
						loser = p1;
					}
					else if(getRole(p) == P2)
					{
						for(int i = 0;i<p1.size();i++)
						{
							handleWinner(p1.get(i));
						}
						p1Wins++;
						for(int i = 0;i<p2.size();i++)
						{
							handleLoser(p2.get(i));
						}
						
						winner = p1;
						loser = p2;
					}
				}
				else
				{
					double minLength = map.getMid().distance(p1.get(0).getLocation());
					int winner = P1;
					//Player win = p1.get(0);
					
					for(int i = 0;i<p1.size();i++)
					{
						if(map.getMid().distance(p1.get(i).getLocation()) < minLength)
						{
							minLength = map.getMid().distance(p1.get(i).getLocation());
							winner = P1;
							//win = p1.get(i);
						}
					}
					for(int i = 0;i<p2.size();i++)
					{
						if(map.getMid().distance(p2.get(i).getLocation()) < minLength)
						{
							minLength = map.getMid().distance(p2.get(i).getLocation());
							winner = P2;
							//win = p2.get(i);
						}
					}
					
					if(winner == P1)
					{
						for(int i = 0;i<p1.size();i++)
						{
							handleWinner(p1.get(i));
						}
						for(int i = 0;i<p2.size();i++)
						{
							handleLoser(p2.get(i));
						}
						p1Wins++;
						this.winner = p1;
						this.loser = p2;
					}
					else if(winner == P2)
					{
						for(int i = 0;i<p2.size();i++)
						{
							handleWinner(p2.get(i));
						}
						for(int i = 0;i<p1.size();i++)
						{
							handleLoser(p1.get(i));
						}
						p2Wins++;
						this.winner = p2;
						this.loser = p1;
					}
					
				}
				
			}
			
			boolean onlineCount = false;
			for(int i = 0;i<p1.size();i++)
			{
				if(!leftPlayers.contains(p1.get(i).getUniqueId()))
				{
					onlineCount = true;
				}
			}
			
			if(!onlineCount)
			{
				p2Wins = maxWins;
			}
			else
			{
				onlineCount = false;
				for(int i = 0;i<p2.size();i++)
				{
					if(!leftPlayers.contains(p2.get(i).getUniqueId()))
					{
						onlineCount = true;
					}
				}
			}
			
			if(!onlineCount)
			{
				p1Wins = maxWins;
			}
		}
		
		ended = true;
		
		leftPlayers.clear();
		
		if(!playerOnline || p1Wins >= maxWins || p2Wins >= maxWins)
		{
			if(playerOnline && getTournamentID() == RANKED)
			{
				int elo1 = 0;
				int elo2 = 0;
				
				for(int i = 0;i<getWinner().size();i++)
					elo1 += getELO(getWinner().get(i));
				
				for(int i = 0;i<getLoser().size();i++)
					elo2 += getELO(getLoser().get(i));
				
				int difElo = calculateELO(elo1, elo2);
				
				Title mainWin=new Title(),mainLose=new Title();
				
				mainWin.addText("+",Colors.GOLD);
				mainLose.addText("-",Colors.GRAY);
				
				Title winTitle = new Title();
				winTitle.addText(difElo+"",Colors.BLUE).addText(" ELO",Colors.YELLOW);
				
				Title loseTitle = new Title();
				loseTitle.addText(difElo+"",Colors.BLUE).addText(" ELO",Colors.YELLOW);
				
				mainWin.setTiming(20, 40, 20);
				mainLose.setTiming(20, 40, 20);
				
				mainWin.setSubTitle(winTitle);
				mainLose.setSubTitle(loseTitle);
				
				for(int i = 0;i<getWinner().size();i++)
				{
					addELO(getWinner().get(i), difElo);
					getWinner().get(i).sendMessage(Messages.gotELO(difElo));
					mainWin.send(getWinner().get(i));
				}
				
				for(int i = 0;i<getLoser().size();i++)
				{
					addELO(getLoser().get(i),-difElo);
					getLoser().get(i).sendMessage(Messages.lostELO(difElo));
					mainLose.send(getLoser().get(i));
				}
			}
			
			SpectateManager.deleteFromMySQL(c.ID);
			hasFinished = true;
		}
	}
	
	public void setRestarting()
	{
		isRestarting = true;
	}
	
	public void restart()
	{
		ended = false;
		waitTime = 10;
		cdTime = START_TIMER;
		started = false;
		timer = false;
		timeMin = c.getTime();
		timeSec = 0;
		
		
		for(int i = 0;i<p1.size();i++)
		{
			boolean alive = false;
			for(int u = 0;u<alive1.size();u++)
			{
				if(alive1.get(u).getUniqueId().equals(p1.get(i).getUniqueId()))
				{
					alive = true;
				}
			}
			
			if(!alive)
			{
				alive1.add(p1.get(i));
			}
			
			if(p1.get(i).getHealth() != 0.0)
			{
				setupPlayer(p1.get(i), true);
				DuelListener.setupSpawnStats(p1.get(i), true,false,true);
			}
				
			
			
			p1.get(i).teleport(map.getSpawn1());
		}
		for(int i = 0;i<p2.size();i++)
		{
			boolean alive = false;
			for(int u = 0;u<alive2.size();u++)
			{
				if(alive2.get(u).getUniqueId().equals(p2.get(i).getUniqueId()))
				{
					alive = true;
				}
			}
			
			if(!alive)
			{
				alive2.add(p2.get(i));
			}
			
			if(p2.get(i).getHealth() != 0.0)
			{
				setupPlayer(p2.get(i), true);
				DuelListener.setupSpawnStats(p2.get(i),true,false,true);
			}
			
			
			
			p2.get(i).teleport(map.getSpawn2());
		}
		
		timeCountdown();
		waitingCountdown(MainClass.getInstance());
		
		isRestarting = false;
		
	}
	
	public ArrayList<Player> getP1()
	{
		return p1;
	}
	
	public ArrayList<Player> getP2()
	{
		return p2;
	}
	
	public ArrayList<Player> getWinner()
	{
		return winner;
	}
	
	public ArrayList<Player> getLoser()
	{
		return loser;
	}
	
	public Map getMap()
	{
		return map;
	}
	
	public int getRole(Player p)
	{
		for(int i = 0;i<p1.size();i++)
		{
			if(p1.get(i).getUniqueId().equals(p.getUniqueId()))
			{
				return P1;
			}
		}
		for(int i = 0;i<p2.size();i++)
		{
			if(p2.get(i).getUniqueId().equals(p.getUniqueId()))
			{
				return P2;
			}
		}
		
		return NO;
	}
	
	public boolean isStarted()
	{
		return started;
	}

	public boolean isTournament()
	{
		return c.isTournament();
	}
	
	public boolean isPartOf(Player p)
	{
		if(p == null)
		{
			return false;
		}
		
		for(int i = 0;i<p1.size();i++)
		{
			if(p1.get(i).getUniqueId().equals(p.getUniqueId()))
			{
				return true;
			}
		}
		
		for(int i = 0;i<p2.size();i++)
		{
			if(p2.get(i).getUniqueId().equals(p.getUniqueId()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isPartOf(UUID p)
	{
		for(int i = 0;i<p1.size();i++)
		{
			if(p1.get(i).getUniqueId().equals(p))
			{
				return true;
			}
		}
		
		for(int i = 0;i<p2.size();i++)
		{
			if(p2.get(i).getUniqueId().equals(p))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isFFA()
	{
		return ffa;
	}
	
	public String getHomeServer(Player p1)
	{
		if(p1 == null)
			return "pvp-1";
		
		String hs = "";
		Challenge c = getChallenge();
		
		
		int u = 0;
		for(Player p : c.getChallengers())
		{
			if(p.getUniqueId().equals(p1.getUniqueId()))
			{
				hs = c.getPreviousServer(u);
			}
			u++;
		}
		for(Player p : c.getChallenged())
		{
			if(p.getUniqueId().equals(p1.getUniqueId()))
			{
				hs = c.getPreviousServer(u);
			}
			u++;
		}
		if(hs == null || hs == "")
		{
			hs = c.getPreviousServer(0);
		}
		
		return hs;
	}
	
	public static double round(double value, int places)
	{
	    if (places < 0) throw new IllegalArgumentException();
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public void updateStatistics(Player p,int k,int d,int w,int l,int pl,int tw)
	{
		try
		{
			PreparedStatement ps = MySQL.getInstance().getConnection().prepareStatement("SELECT Statistics FROM Duel_Statistics WHERE UUID = ?");
			ps.setString(1, p.getUniqueId().toString());
			
			ResultSet rs = ps.executeQuery();
			
			
			if(rs.first())
			{
				Statistics stats = Statistics.fromString(rs.getString(1));
				stats.setKills(stats.getKills()+k);
				stats.setDeaths(stats.getDeaths()+d);
				stats.setWins(stats.getWins()+w);
				stats.setLoses(stats.getLoses()+l);
				stats.setPlays(stats.getPlays()+pl);
				stats.setTournamentWins(stats.getTournamentWins()+tw);
				ps.close();
				ps = MySQL.getInstance().getConnection().prepareStatement("UPDATE Duel_Statistics SET Statistics = ? WHERE UUID = ?");
				ps.setString(1, stats.toString());
				ps.setString(2, p.getUniqueId().toString());
				ps.executeUpdate();
			}
			else
			{
				ps.close();
				ps = MySQL.getInstance().getConnection().prepareStatement("INSERT INTO Duel_Statistics (UUID,Statistics) VALUES (?,?)");
				ps.setString(1, p.getUniqueId().toString());
				ps.setString(2, new Statistics(k, d, w, l, pl,tw,100).toString());
				ps.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList<Player> getAlive1()
	{
		return alive1;
	}
	
	public ArrayList<Player> getAlive2()
	{
		return alive2;
	}
	
	private int getAmountInInventory(Player p,Material type)
	{
		Inventory inv = p.getInventory();
		
		int counter = 0;
		
		for(int i = 0;i<inv.getSize();i++)
		{
			ItemStack is = inv.getItem(i);
			if(is != null && is.getType() != null && is.getType().equals(type))
			{
				counter += is.getAmount();
			}
		}
		
		return counter;
	}
	
	private int getAmountRecrafts(Player p)
	{
		return Math.min(Math.min(getAmountInInventory(p, Material.BOWL), getAmountInInventory(p, Material.BROWN_MUSHROOM)), getAmountInInventory(p, Material.RED_MUSHROOM));
	}
	
	public void handleDeath(Player p,Player killer,boolean leave,boolean die)
	{
		if(killer != null)
		{	
			String name = "";
			double health = 0.0;
			boolean againstOne = false;
			boolean isSoup = DuelListener.getKit(killer).getSettings().contains(KitSettings.SOUP);
			int soups = 0;
			int recrafts = 0;
			
			if(ffa)
			{
				if(alive1.size()-1 > 1)
				{
					for(int i = 0;i<alive1.size();i++)
					{
						if(alive1.get(i).getUniqueId().equals(p.getUniqueId()))
							continue;
						
						name += Messages.playerInHasLifeLeft(alive1.get(i).getDisplayName()) + (i+1==alive1.size() ? "" : Messages.seperatorInHasLifeLfeft);
						health += alive1.get(i).getHealth()/2.0;
						
						if(isSoup)
						{
							soups += getAmountInInventory(alive1.get(i), Material.MUSHROOM_SOUP);
							recrafts += getAmountRecrafts(alive1.get(i));
						}
					}
				}
				else
				{
					for(int i = 0;i<alive1.size();i++)
					{
						if(alive1.get(i).getUniqueId().equals(p.getUniqueId()))
							continue;
						
						name = alive1.get(i).getDisplayName();
						health = alive1.get(i).getHealth()/2.0;
						if(isSoup)
						{
							soups += getAmountInInventory(alive1.get(i), Material.MUSHROOM_SOUP);
							recrafts += getAmountRecrafts(alive1.get(i));
						}
					}
					
					againstOne = true;
				}
				
			}
			else
			{
				int role = getRole(p);
				
				if(role == P1)
				{
					if(alive2.size() > 1)
					{
						for(int i = 0;i<alive2.size();i++)
						{
							name += Messages.playerInHasLifeLeft(alive2.get(i).getDisplayName()) + (i+1==alive2.size() ? "" : Messages.seperatorInHasLifeLfeft);
							health += alive2.get(i).getHealth()/2.0;
							if(isSoup)
							{
								soups += getAmountInInventory(alive2.get(i), Material.MUSHROOM_SOUP);
								recrafts += getAmountRecrafts(alive2.get(i));
							}
						}
					}
					else
					{
						name = alive2.get(0).getDisplayName();
						health = alive2.get(0).getHealth()/2.0;
						if(isSoup)
						{
							soups += getAmountInInventory(alive2.get(0), Material.MUSHROOM_SOUP);
							recrafts += getAmountRecrafts(alive2.get(0));
						}
						againstOne = true;
					}
					
				}
				else if(role == P2)
				{
					if(alive1.size() > 1)
					{
						for(int i = 0;i<alive1.size();i++)
						{
							name += Messages.playerInHasLifeLeft(alive1.get(i).getDisplayName()) + (i+1==alive1.size() ? "" : Messages.seperatorInHasLifeLfeft);
							health += alive1.get(i).getHealth()/2.0;
							if(isSoup)
							{
								soups += getAmountInInventory(alive1.get(i), Material.MUSHROOM_SOUP);
								recrafts += getAmountRecrafts(alive1.get(i));
							}
						}
					}
					else
					{
						name = alive1.get(0).getDisplayName();
						health = alive1.get(0).getHealth()/2.0;
						if(isSoup)
						{
							soups += getAmountInInventory(alive1.get(0), Material.MUSHROOM_SOUP);
							recrafts += getAmountRecrafts(alive1.get(0));
						}
						againstOne = true;
					}
				}
			}
			
			if(againstOne)
				p.sendMessage(Messages.hasLifeLeft(name,round(health, 1)+""));
			else
				p.sendMessage(Messages.hasLifeLeftTeam(name, round(health,1)));
			if(isSoup)
			{
				if(againstOne)
					p.sendMessage(Messages.soupsLeft(name,soups,recrafts));
				else
					p.sendMessage(Messages.soupsLeftTeam(name, soups, recrafts));
			}
		}
		
		if(die)
			updateStatistics(p,0, 1, 0, 0, 0, 0);
		if(killer != null)
			updateStatistics(killer, 1, 0, 0, 0, 0, 0);
		
		int death = -1;
		
		for(int i = 0;i<alive1.size();i++)
		{
			if(alive1.get(i).getUniqueId().equals(p.getUniqueId()))
			{
				alive1.remove(i);
				if((ffa && alive1.size() == 1) || alive1.size() == 0)
				{
					death = P1;
					break;
				}
				death = NO;
				break;
			}
		}
		
		if(!ffa)
		{
			for(int i = 0;i<alive2.size();i++)
			{
				if(alive2.get(i).getUniqueId().equals(p.getUniqueId()))
				{
					alive2.remove(i);
					if(alive2.size() == 0)
					{
						death = P2;
						break;
					}
					death = NO;
					break;
				}
			}
		}
		
		if(death == -1)
			death = NO;
		
		if(!isTournament())
		{
			if(p.getHealth() != 0.0 && death == NO)
				addSpectator(new DuelSpec(p,getHomeServer(p)),false);
		}
		else
		{
			if(p.getHealth() == 0.0)
			{
				Spectator s = new Spectator(p,getTournamentID(),getHomeServer(p));
				TournamentManager.addSpectatorArray(s);
				TournamentManager.uploadSpectator(s);
				DuelListener.specOnRespawn.add(s);
			}
			else if(death == NO)
			{
				TournamentManager.addSpectator(new Spectator(p, getTournamentID(), getHomeServer(p)), true, true);
			}
		}
		
		
		if(death == Duel.P1 || death == Duel.P2)
		{
			String[] args = new String[0];
			GiveUpCommand.giveUp(p, args);
		}
		else
		{
			if(!isTournament())
			{
				for(int i = 0;i<spectators.size();i++)
				{
					updateSpecInvs(spectators.get(i).player);
				}
			}
			else
			{
				for(int i = 0;i<TournamentManager.getSpecs().size();i++)
				{
					TournamentManager.updateSpectateInvs(TournamentManager.getSpecs().get(i).player);
				}
			}
		}
		
		if(leave)
		{
			if(!leftPlayers.contains(p.getUniqueId()))
				leftPlayers.add(p.getUniqueId());
		}
		
	}
	
	private void setupDamageTicks(Player p,int i)
	{
		if(i == 1)
		{
			if(ffa)
			{
				Kit kit1 = DuelListener.getKit(p);
				
				for(int j = 0;j<p1.size();j++)
				{
					if(kit1.getSettings().contains(KitSettings.NO_HIT_DELAY))
					{
						p1.get(i).setMaximumNoDamageTicks(0);
					}
					
					Kit kit = DuelListener.getKit(p1.get(j));
					if(kit.getSettings().contains(KitSettings.NO_HIT_DELAY))
					{
						p.setMaximumNoDamageTicks(0);
					}
				}
				
			}
			else
			{
				for(int j = 0;j<p2.size();j++)
				{
					Kit kit = DuelListener.getKit(p2.get(j));
					if(kit.getSettings().contains(KitSettings.NO_HIT_DELAY))
					{
						p.setMaximumNoDamageTicks(0);
						break;
					}
				}
				
				if(DuelListener.getKit(p).getSettings().contains(KitSettings.NO_HIT_DELAY))
				{
					for(int j = 0;j<p2.size();j++)
					{
						p2.get(j).setMaximumNoDamageTicks(0);
					}
				}
			}
			
		}
		else if(i == 2)
		{
			for(int j = 0;j<p1.size();j++)
			{
				Kit kit = DuelListener.getKit(p1.get(j));
				if(kit.getSettings().contains(KitSettings.NO_HIT_DELAY))
				{
					p.setMaximumNoDamageTicks(0);
					break;
				}
			}
			
			if(DuelListener.getKit(p).getSettings().contains(KitSettings.NO_HIT_DELAY))
			{
				for(int j = 0;j<p1.size();j++)
				{
					p1.get(j).setMaximumNoDamageTicks(0);
				}
			}
		}
	}
	
	public void addPlayer(Player p,int i,boolean asynchron)
	{
		if(i == 1)
		{
			p1.add(p);
			alive1.add(p);
		}
		else if(i == 2)
		{
			p2.add(p);
			alive2.add(p);
		}
		
		if(p.getHealth() != 0.0)
			setupPlayer(p,asynchron);
		
		p.setMaximumNoDamageTicks(20);
		
		ArrayList<Player> players = new ArrayList<>();
		players.addAll(c.getChallengers());
		players.addAll(c.getChallenged());
		
		for(int u = 0;u<players.size();u++)
		{
			DuelListener.onSpawn(players.get(u),asynchron,false);
		}
		
		if(map != null && !map.isLoaded())
		{
			p.sendMessage(Messages.mapLoads(map.getName()));
		}
		
		if(!asynchron)
		{
			MyScoreboardManager.updateScoreboard();
			setupDamageTicks(p, i);
		}
		else
			Bukkit.getScheduler().runTask(MainClass.getInstance(), new Runnable() {
				
				@Override
				public void run() {
					MyScoreboardManager.updateScoreboard();
					setupDamageTicks(p, i);
				}
			});
	}

	public boolean isRestarting()
	{
		return isRestarting;
	}
	
	public boolean isAlive(Player p)
	{
		for(int i = 0;i<alive1.size();i++)
		{
			if(alive1.get(i).getUniqueId().equals(p.getUniqueId()))
				return true;
		}
		
		for(int i = 0;i<alive2.size();i++)
		{
			if(alive2.get(i).getUniqueId().equals(p.getUniqueId()))
				return true;
		}
		
		return false;
	}
	
	private ArrayList<ChatColor> getColorsFromString(String str)
	{
		ArrayList<ChatColor> chars = new ArrayList<>();
		
		boolean readChar = false;
		
		for(int i = 0;i<str.length();i++)
		{
			if(readChar)
			{
				chars.add(ChatColor.getByChar(str.charAt(i)));
				readChar = false;
			}
			else if(str.charAt(i) == '§')
			{
				readChar = true;
			}
		}
		
		return chars;
	}
	
	private Color chatColorToColor(ChatColor cc)
	{
		switch(cc)
		{
		case AQUA: return Color.AQUA;
		case BLACK: return Color.BLACK;
		case BLUE: return Color.BLUE;
		case DARK_AQUA: return Color.fromRGB(23, 153, 181);
		case DARK_BLUE: return Color.fromRGB(0,0,139);
		case DARK_GRAY: return Color.fromRGB(169, 169, 169);
		case DARK_GREEN: return Color.fromRGB(0, 100, 0);
		case DARK_PURPLE: return Color.fromRGB(128, 0, 128);
		case DARK_RED: return Color.fromRGB(139,0,0);
		case GOLD: return Color.fromRGB(255, 215, 0);
		case GRAY: return Color.GRAY;
		case GREEN: return Color.LIME;
		case LIGHT_PURPLE: return Color.FUCHSIA;
		case RED: return Color.RED;
		case WHITE: return Color.WHITE;
		case YELLOW: return Color.YELLOW;
		default: return null;
		}
		
	}
	
	private ChatColor getLastColor(ArrayList<ChatColor> cc)
	{
		if(cc == null)
			return null;
		
		for(int i = cc.size()-1;i>=0;i--)
		{
			if(!(cc.get(i).equals(ChatColor.ITALIC) ||
				cc.get(i).equals(ChatColor.BOLD) ||
				cc.get(i).equals(ChatColor.MAGIC) ||
				cc.get(i).equals(ChatColor.RESET) ||
				cc.get(i).equals(ChatColor.STRIKETHROUGH) ||
				cc.get(i).equals(ChatColor.UNDERLINE)))
			{
				return cc.get(i);
			}
		}
		
		return null;
	}
	
	private void updateSpecInvs(Player p)
	{
		p.getInventory().clear();
		
		int invPos = 0;
		ItemStack is;
		LeatherArmorMeta im;
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.YELLOW + "RECHTS" + ChatColor.WHITE + "-Klick um das Inventar anzuschauen");
		
		ChatColor team1Color = getLastColor(getColorsFromString(MainClass.getInstance().getConfig().getString("Teams.Team1.Prefix")));
		ChatColor team2Color = getLastColor(getColorsFromString(MainClass.getInstance().getConfig().getString("Teams.Team2.Prefix")));
		
		Color t1Color;
		Color t2Color;
		if(team1Color == null || team2Color == null)
		{
			t1Color = Color.RED;
			t2Color = Color.BLUE;
		}
		else
		{
			t1Color = chatColorToColor(team1Color);
			t2Color = chatColorToColor(team2Color);
		}
		
		for(int i = 0;i<alive1.size() && invPos < 36;i++)
		{
			is = new ItemStack(Material.LEATHER_HELMET);
			im =  (LeatherArmorMeta) is.getItemMeta();
			im.setColor(t1Color);
			im.setDisplayName((team1Color == null ? ChatColor.RED : team1Color) + alive1.get(i).getDisplayName());
			im.setLore(lore);
			is.setItemMeta(im);
			
			p.getInventory().setItem(invPos, is);
			
			invPos++;
		}
		
		for(int i = 0;i<alive2.size() && invPos < 36;i++)
		{
			is = new ItemStack(Material.LEATHER_HELMET);
			im =  (LeatherArmorMeta) is.getItemMeta();
			im.setColor(t2Color);
			im.setDisplayName((team2Color == null ? ChatColor.BLUE : team2Color) + alive2.get(i).getDisplayName());
			im.setLore(lore);
			is.setItemMeta(im);
			
			p.getInventory().setItem(invPos, is);
			
			invPos++;
		}
		
		Inventory inv = p.getOpenInventory().getTopInventory();
		
		if(inv != null)
		{
			Player holder = (Player)inv.getHolder();
			if(!isAlive(holder))
			{
				p.closeInventory();
			}
		}
	}
	
	private void setupSpecItems(Player p)
	{
		p.getInventory().clear();
		p.getEquipment().clear();
		
		updateSpecInvs(p);
	}
	
	public boolean isSpectator(Player p,boolean tournament)
	{
		if(p == null)
		{
			return false;
		}
		for(int i = 0;i<spectators.size();i++)
		{
			if(spectators.get(i).player.getUniqueId().equals(p.getUniqueId()))
			{
				return true;
			}
		}
		
		if(isTournament() && tournament && TournamentManager.isSpectator(p, false))
		{
			return true;
		}
		
		return false;
	}
	
	public void removeSpectator(Player p)
	{
		for(int i = 0;i<spectators.size();i++)
		{
			if(spectators.get(i).player.getUniqueId().equals(p.getUniqueId()))
			{
				spectators.remove(i);
				return;
			}
		}
		
		for(Player p1 : Bukkit.getOnlinePlayers())
		{
			p1.showPlayer(p);
		}
	}
	
	public void addSpectator(DuelSpec ds,boolean teleport)
	{
		if(isSpectator(ds.player,false))
			return;
		
		spectators.add(ds);
		
		Bukkit.getScheduler().runTask(MainClass.getInstance(), new Runnable() {
			
			@Override
			public void run()
			{
				ds.player.setGameMode(GameMode.CREATIVE);
				
				for(Player p1 : Bukkit.getOnlinePlayers())
				{
					if(!p1.getUniqueId().equals(ds.player.getUniqueId()))
					{
						p1.hidePlayer(ds.player);
					}
				}
				
				for(int i = 0;i<spectators.size();i++)
				{
					ds.player.hidePlayer(spectators.get(i).player);
				}
				
				MyScoreboardManager.updateScoreboard();
			}
		});
		
		setupSpecItems(ds.player);
		
		if(teleport)
		{
			Bukkit.getScheduler().runTaskLater(MainClass.getInstance(), new Runnable() {
				
				@Override
				public void run()
				{
					ds.player.teleport(map.getMid());	
				}
			},20*1);
		}
		
		DuelListener.canDoubleJump.remove(ds.player.getUniqueId());
	}
	
	public ArrayList<DuelSpec> getSpectators()
	{
		return spectators;
	}

	public int getMaxTime()
	{
		return maxTimeMin;
	}
}
