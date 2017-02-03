package at.kingcraft.OnevsOne_arena.Duels;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.Tournaments.TournamentManager;

public class ResetSpecDuelRun implements Runnable
{
	private Player p;
	private Duel d;
	
	public ResetSpecDuelRun(Player p,Duel d)
	{
		this.p = p;
		this.d = d;
	}

	@Override
	public void run()
	{
		TournamentManager.removeSpectator(p);
		d.setupPlayer(p, false);
		
		ArrayList<Player> p1 = d.getP1();
		ArrayList<Player> p2 = d.getP2();
		
		for(int i = 0;i<p1.size();i++)
		{
			if(!p1.get(i).getUniqueId().equals(p.getUniqueId()))
			{
				p1.get(i).showPlayer(p);
			}
		}
		
		for(int i = 0;i<p2.size();i++)
		{
			if(!p2.get(i).getUniqueId().equals(p.getUniqueId()))
			{
				p2.get(i).showPlayer(p);
			}
		}
	}

}
