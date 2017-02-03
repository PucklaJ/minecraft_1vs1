package at.kingcraft.OnevsOne_arena.Commands;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import MySQL.MySQL;
import at.kingcraft.OnevsOne_arena.MainClass;
import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_arena.Kits.Kit;
import at.kingcraft.OnevsOne_arena.Messaging.Messages;
import at.kingcraft.OnevsOne_arena.Messaging.Messenger;
import at.kingcraft.OnevsOne_arena.Tournaments.Tournament;
import at.kingcraft.OnevsOne_arena.Tournaments.TournamentManager;
import at.kingcraft.OnevsOne_setup.Maps.MapManager;

public class CreateNewTournRun implements Runnable
{
	private MainClass plugin;
	private Tournament tourn;
	private int roundLevel;
	private int tournamentID;
	private String homeServer;
	private Kit kit;
	private Duel d;
	private int sendBackID;

	public CreateNewTournRun(MainClass plugin,Tournament tourn,int roundLevel,int tourID,Kit kit,String homeServer,Duel d)
	{
		this.tourn = tourn;
		this.roundLevel = roundLevel;
		this.tournamentID = tourID;
		this.kit = kit;
		this.homeServer = homeServer;
		this.d = d;
		this.plugin = plugin;
		
	}
	
	private String getHomeServer(Duel d,Player p)
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
	
	private void teleportPlayerBack(Player p,String server)
	{
		String[] args = new String[1];
		args[0] = server;
		
		
		Messenger.sendMessage(p, "BungeeCord", "Connect",args);
	}
	
	public void sendBackToFreeServers(String server)
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
	
	public void sendBackToFreeServers()
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
	
	private void handleWinners(Duel d)
	{
		ArrayList<Player> winner = d.getWinner();
		
		for(int i = 0;i<winner.size();i++)
		{
			winner.get(i).sendMessage(Messages.tournamentWin);
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
	
	@Override
	public void run()
	{
		tourn = TournamentManager.getTournamentFromMySQL(null, tournamentID, homeServer, kit,d.getMode(),d.getMaxTime());
		
		if(tourn == null)
		{
			return;
		}
		
		int create = tourn.createNewRounds(tourn.isQualiRound() ? -1 : (roundLevel+1));
		
		if(create != -1)
		{
			switch(create)
			{
			case Tournament.LAST_PLAYER:
				handleWinners(d);
				break;
			}
		}
		else
		{
			if(TournamentManager.updateTournament(tourn,true))
			{
				tourn.start();
			}
		}
		
		TournamentManager.deleteTournamentArray(tourn);
		
		return;
	}

}
