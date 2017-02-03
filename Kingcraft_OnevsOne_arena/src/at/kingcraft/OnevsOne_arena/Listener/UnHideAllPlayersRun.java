package at.kingcraft.OnevsOne_arena.Listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.Tournaments.TournamentManager;

public class UnHideAllPlayersRun implements Runnable
{

	private Player p;
	
	public UnHideAllPlayersRun(Player p)
	{
		super();
		this.p = p;
	}
	
	private void unHideAllPlayers()
	{
		for(Player p1 : Bukkit.getServer().getOnlinePlayers())
		{
			if(p1.getUniqueId().equals(p.getUniqueId()))
			{
				continue;
			}
			
			if(!TournamentManager.isSpectator(p,true))
			{
				p1.showPlayer(p);
			}
				
			if(!TournamentManager.isSpectator(p1,true))
			{
				p.showPlayer(p1);
			}
				
		}
	}
	
	@Override
	public void run()
	{
		unHideAllPlayers();
	}

}
