package at.kingcraft.OnevsOne_arena.Commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.MainClass;
import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_arena.Kits.Kit;
import at.kingcraft.OnevsOne_arena.Tournaments.Spectator;
import at.kingcraft.OnevsOne_arena.Tournaments.Tournament;
import at.kingcraft.OnevsOne_arena.Tournaments.TournamentManager;
import at.kingcraft.OnevsOne_setup.Maps.MapManager;

public class CheckTournamentRun
{

	private int tournamentID;
	private MainClass plugin;
	private String homeServer;
	private Kit kit;
	private Tournament tourn;
	private int roundLevel;
	private Duel d;
	
	public CheckTournamentRun(int tourID,MainClass plugin,String homeServer,Kit kit,Tournament tourn,int roundLevel,Duel d)
	{
		tournamentID = tourID;
		this.plugin = plugin;
		this.homeServer = homeServer;
		this.kit = kit;
		this.tourn = tourn;
		this.roundLevel = roundLevel;
		this.d = d;
	}
	
	public boolean run()
	{
		if(MapManager.isLoading)
		{
			return true;
		}
		
		/*if(Bukkit.getServer().getOnlinePlayers().isEmpty())
		{
			return false;
		}*/
		
		if(TournamentManager.checkIfRoundFinished(tournamentID))
		{
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new CreateNewTournRun(plugin,tourn, roundLevel, tournamentID, kit, homeServer, d), 20*3);
			return false;
		}
		else
		{
			
			Bukkit.getScheduler().runTask(plugin, new Runnable() {
				
				@Override
				public void run()
				{
					ArrayList<Player> players = d.getWinner();
					
					for(int i = 0;i<players.size();i++)
					{
						if(TournamentManager.isSpectator(players.get(i),true))
						{
							continue;
						}
						
						if(TournamentManager.isOnServer(players.get(i).getUniqueId()))
							TournamentManager.addSpectator(new Spectator(players.get(i),tournamentID,homeServer),false,false);
					}
					
					
				}
			});
			
			
			return false;
		}
	}
}
