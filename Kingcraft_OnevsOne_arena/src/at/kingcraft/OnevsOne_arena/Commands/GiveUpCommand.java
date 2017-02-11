package at.kingcraft.OnevsOne_arena.Commands;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import MySQL.MySQL;
import at.kingcraft.OnevsOne_arena.MainClass;
import at.kingcraft.OnevsOne_arena.Sounds;
import at.kingcraft.OnevsOne_arena.Challenges.ChallangeManager;
import at.kingcraft.OnevsOne_arena.Challenges.Challenge;
import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_arena.Duels.DuelManager;
import at.kingcraft.OnevsOne_arena.Kits.Kit;
import at.kingcraft.OnevsOne_arena.Listener.DuelListener;
import at.kingcraft.OnevsOne_arena.Messaging.Messages;
import at.kingcraft.OnevsOne_arena.Messaging.Messenger;
import at.kingcraft.OnevsOne_arena.Tournaments.Round;
import at.kingcraft.OnevsOne_arena.Tournaments.Spectator;
import at.kingcraft.OnevsOne_arena.Tournaments.TourPlayer;
import at.kingcraft.OnevsOne_arena.Tournaments.Tournament;
import at.kingcraft.OnevsOne_arena.Tournaments.TournamentManager;
import at.kingcraft.OnevsOne_setup.Maps.Map;
import at.kingcraft.OnevsOne_setup.Maps.MapManager;
import net.md_5.bungee.api.ChatColor;

public class GiveUpCommand implements CommandExecutor {

	private static MainClass plugin;
	private static Map map;
	private static int sendBackID = 0;
	private static int tournamentID;
	private static Tournament tourn;
	private static ArrayList<Integer> checkTournamentID;
	private static String homeServer;
	private static Kit kit;
	private static int roundLevel;
	
	public GiveUpCommand(MainClass plugin)
	{
		GiveUpCommand.plugin = plugin;
		checkTournamentID = new ArrayList<>();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only for Players");
			return true;
		}
		if(args.length > 1 || (args.length == 1 && args[0].equalsIgnoreCase("leave")))
		{
			return false;
		}
		
		Player p = (Player) sender;
		
		
		Duel d = DuelManager.getDuel(p);
		if(d != null)
		{
			p.playSound(p.getLocation(), Sounds.giveUp, Sounds.giveUpVolume, Sounds.DEFAULT_PITCH);
			d.handleDeath(p, null,false,false);
		}
		
		return true;
	}
	
	public static void handleFinishedDuel(Duel d,Player p,Challenge c)
	{
		if(d == null)
			return;
		
		if(d.isTournament())
		{
			tourn = TournamentManager.getTournamentFromMySQL(p, d.getTournamentID(),d.getHomeServer(p),d.getKit(),d.getMaxRounds(),d.getMaxTime());
			
			if(tourn == null)
			{
				
			}
			else
			{
				roundLevel = tourn.getRoundLevel();
				int maxRoundLevel = tourn.getMaxRoundLevel();
				tournamentID = tourn.getID();
				
				if(roundLevel >= maxRoundLevel)
				{
					handleWinners(d);
				}
				
				if(p != null)
					handleLosers(d,maxRoundLevel,tourn.getRound(p));
				
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					
					@Override
					public void run()
					{
						map.reload(null);
					}
				}, 20*4);
				
				
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					
					@Override
					public void run()
					{
						DuelManager.deleteDuel();
					}
				}, 20*4);
									
				
				if(roundLevel < maxRoundLevel)
				{
					handleNextRounds(d,p);
				}
				
				TournamentManager.deleteTournamentArray(tourn);
			}
		
		}
		else
		{
			handleNormalDuel(c, p);
		}
	}
	
	public static boolean giveUp(Player p,String[] args)
	{
		// delete Duels
		
		Duel d = DuelManager.getDuel(p);
		
		if(d == null || d.hasFinished())
		{
			TournamentManager.playerLeaves(p, tournamentID);
			return true;
		}
		if(d!= null && !d.isRestarting())
		{	
			map = null;
			d.stopCountdown();
			map = d.getMap();
			
			d.endDuel(p,true);
			
			Challenge c = null;
			
			if(d.hasFinished())
			{
				// Delete Challenges
				c = ChallangeManager.getChallenge(p);
				if(c==null)
				{
					return true;
				}
				else
				{
					homeServer = c.getPreviousServer(0);
					kit = DuelListener.getKit(p);
					ChallangeManager.deleteChallenge(c.ID);
				}
			}
			
			
			if(!d.hasFinished())
			{
				d.setRestarting();
				
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					
					@Override
					public void run()
					{
						d.getMap().reload(null);
						Bukkit.getScheduler().runTaskLaterAsynchronously(plugin,new RestartDuelRun(d), 20*3);
					}
				}, 20*2);
			}
			else
			{
				/*if(d.isTournament())
				{
					tourn = TournamentManager.getTournamentFromMySQL(p, d.getTournamentID(),homeServer,kit,d.getMode(),d.getMaxTime());
					
					if(tourn == null)
					{
						
					}
					else
					{
						roundLevel = tourn.getRoundLevel();
						int maxRoundLevel = tourn.getMaxRoundLevel();
						tournamentID = tourn.getID();
						
						if(roundLevel >= maxRoundLevel)
						{
							handleWinners(d);
						}
						
						handleLosers(d,maxRoundLevel,tourn.getRound(p));
						
						Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
							
							@Override
							public void run()
							{
								map.reload(null);
							}
						}, 20*4);
						
						
						Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
							
							@Override
							public void run()
							{
								DuelManager.deleteDuel(p);
							}
						}, 20*4);
											
						
						if(roundLevel < maxRoundLevel)
						{
							handleNextRounds(d,p);
						}
						
						TournamentManager.deleteTournamentArray(tourn);
					}
				
				}
				else
				{
					handleNormalDuel(c, p);
				}*/
				
				handleFinishedDuel(d, p, c);
			}
			
			
			
			return true;
		}
		
		return true;
	}
	
	private static void addToTournamentWinners(Player p)
	{
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("INSERT INTO Duel_TournamentWinner (UUID) VALUES (?)");
			ps.setString(1, p.getUniqueId().toString());
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void handleWinners(Duel d)
	{
		ArrayList<Player> winner = d.getWinner();
		
		for(int i = 0;i<winner.size();i++)
		{
			winner.get(i).sendMessage(Messages.tournamentWin);
			winner.get(i).playSound(winner.get(i).getLocation(), Sounds.tournamentWin, Sounds.tournamentWinVolume, Sounds.DEFAULT_PITCH);
			d.updateStatistics(winner.get(i), 0, 0, 0, 0, 0, 1);
			addToTournamentWinners(winner.get(i));
		}
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable()
		{
			
			@Override
			public void run()
			{	
				for(int i = 0;i<winner.size();i++)
				{
					teleportPlayerBack(winner.get(i), getHomeServer(d, winner.get(i)));
				}
				
			}
		}, 20*3);
		
				
		
					
		sendBackID = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			
			@Override
			public void run()
			{
				if(!MapManager.isLoading)
				{
					for(int i = 0;i<tourn.getServers().size();i++)
					{
						sendBackToFreeServers(tourn.getServers().get(i));
					}
					Bukkit.getScheduler().cancelTask(sendBackID);
				}
				
			}
		}, 0, 20).getTaskId();
		
		TournamentManager.deleteTournament(tourn, false);
		tournamentID = -1;
	}
	
	private static ArrayList<TourPlayer> convert1(ArrayList<Player> p)
	{
		ArrayList<TourPlayer> tp = new ArrayList<>();
		
		for(int i = 0;i<p.size();i++)
		{
			tp.add(new TourPlayer(p.get(i).getName(),p.get(i).getUniqueId()));
		}
		
		return tp;
	}
	
	private static void handleLosers(Duel d,int maxRoundLevel,Round r)
	{
		Tournament t = TournamentManager.getTournament(d.getTournamentID());
		
		boolean isQuali = t == null ? false : t.isQualiRound();
		
			ArrayList<Player> loser = d.getLoser();
			ArrayList<TourPlayer> loser1 = convert1(loser);
			
			Round round = r != null ? r : t.getAllRound(MainClass.getInstance().serverName);
			
			if(r != null)
			{
					if(!isQuali)
					{
						for(int i = 0;i<loser.size();i++)
							loser.get(i).sendMessage(Messages.tournamentLose);
					}
					
					tourn.setAllRoundLoser(r,loser1);
					tourn.lose(loser1);
			}
			else
			{
				round.setLoser("BOTH");
				tourn.lose(loser1);
				tourn.lose(convert1(d.getWinner()));
			}
			
				
			
			if(r != null)
			{
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					
					@Override
					public void run()
					{	
						for(int i = 0;i<loser.size();i++)
						{
							if(roundLevel == maxRoundLevel)
							{
								teleportPlayerBack(loser.get(i), getHomeServer(d, loser.get(i)));
							}
							else
							{
								System.out.println("Loser Health: " + loser.get(i).getHealth());
								
								if(loser.get(i).getHealth() != 0.0)
								{
									TournamentManager.addSpectator(new Spectator(loser.get(i), tourn.getID(), getHomeServer(d,loser.get(i))),true,false);
								}
								else
								{
									Spectator s = new Spectator(loser.get(i),tourn.getID(),getHomeServer(d,loser.get(i)));
									TournamentManager.addSpectatorArray(s);
									TournamentManager.uploadSpectator(s);
									DuelListener.specOnRespawn.add(s);
								}
							}
							
						}
					}
				}, 20*1);
			}
			
			TournamentManager.updateTournament(tourn, false);
				
	}
	
	private static ArrayList<UUID> convert(ArrayList<Player> p)
	{
		ArrayList<UUID> uuids = new ArrayList<>();
		
		for(int i = 0;i<p.size();i++)
		{
			uuids.add(p.get(i).getUniqueId());
		}
		
		return uuids;
	}
	
	private static void handleNextRounds(Duel d,Player p)
	{
		
		tourn = TournamentManager.getTournamentFromMySQL(null, tourn.getID(), p!=null ? d.getHomeServer(p) : "pvp-1", d.getKit(), d.getMaxRounds(), d.getMaxTime());
		
		tourn.removeRound(p != null ? tourn.getRound(p) : tourn.getRound(MainClass.getInstance().serverName));
		
		// Update Qualification
		if(tourn.isQualiRound())
		{
			tourn.addQualiPoints(convert(d.getWinner()), 1);
		}
		
		TournamentManager.updateTournament(tourn,false);
		
		checkTournamentID.add(Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			
			@Override
			public void run()
			{
				if(!new CheckTournamentRun(tournamentID, plugin, homeServer, kit, tourn, roundLevel, d).run())
				{
					for(int i = 0;i<checkTournamentID.size();i++)
					{
						Bukkit.getScheduler().cancelTask(checkTournamentID.get(i));
					}
				}
			}
		}, 20*2, 20*2).getTaskId());
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			
			@Override
			public void run()
			{
				for(int i = 0;i<d.getWinner().size();i++)
				{
					boolean isOnline = false;
					for(Player p1: Bukkit.getOnlinePlayers())
					{
						if(p1.getUniqueId().equals(d.getWinner().get(i).getUniqueId()))
						{
							isOnline = true;
							break;
						}
					}
					
					if(!isOnline)
						continue;
					
					if(!TournamentManager.isSpectator(d.getWinner().get(i), false))
					{
						TournamentManager.addSpectator(new Spectator(d.getWinner().get(i), tourn.getID(), d.getHomeServer(d.getWinner().get(i))), true, false);
					}
				}
			}
		}, 20*1);
		
	}
	
	private static void handleNormalDuel(Challenge c,Player p)
	{
		teleportBackToLobby(c,p);
		
		if(map != null)
		{
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				
				@Override
				public void run()
				{
					map.reload(null);
					
					sendBackID = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
						
						@Override
						public void run()
						{
							if(!MapManager.isLoading)
							{
								sendBackToFreeServers();
								Bukkit.getScheduler().cancelTask(sendBackID);
							}
							
						}
					}, 0, 20).getTaskId();
					
				}
			}, 20*4);
			
		}
	}
	
	private static String getHomeServer(Duel d,Player p)
	{
		ArrayList<Player> chers = d.getChallenge().getChallengers();
		ArrayList<Player> ched = d.getChallenge().getChallenged();
		
		int u = 0;
		for(int i = 0;i<chers.size();i++)
		{
			if(chers.get(i).getUniqueId().equals(p.getUniqueId()))
			{
				return d.getChallenge().getPreviousServer(u);
			}
			u++;
		}
		for(int i = 0;i<ched.size();i++)
		{
			if(ched.get(i).getUniqueId().equals(p.getUniqueId()))
			{
				return d.getChallenge().getPreviousServer(u);
			}
			u++;
		}
		
		return "";
		
	}
	
	private static void teleportPlayerBack(Player p,String server)
	{
		String[] args = new String[1];
		args[0] = server;
		
		
		Messenger.sendMessage(p, "BungeeCord", "Connect",args);
	}
	
	public static void sendBackToFreeServers()
	{
		MySQL mysql = plugin.getMySQL();
		
		try
		{
			if(mysql.isConnected())
			{
				PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE Duel_Servers SET free = 1 WHERE Name = ?");
				ps.setString(1, plugin.serverName);
				ps.executeUpdate();
				
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Server \"" + plugin.serverName + "\" is Free");
	}
	
	public static void sendBackToFreeServers(String server)
	{
		MySQL mysql = plugin.getMySQL();
		
		try
		{
			if(mysql.isConnected())
			{
				PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE Duel_Servers SET free = 1 WHERE Name = ?");
				ps.setString(1, server);
				ps.executeUpdate();
				
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Server \"" + server + "\" is Free");
	}
	
	public static void teleportBackToLobby(Challenge c,Player p)
	{
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				Duel d = DuelManager.getDuel(p);
				if(d!=null)
				{
					d.sendBackToLobby();
					DuelManager.deleteDuel(p);
				}
				
			}
		}, 20*3);
	}

	@SuppressWarnings("unused")
	private static boolean isLeaver(Challenge c,int Role,Player p,String[] args)
	{
		int role = ChallangeManager.getRole(p, c);
		
		if((args.length == 1 && args[0].equals("leave"))  && role == Role)
		{
			return true;
		}
		
		
		return false;
	}
}
