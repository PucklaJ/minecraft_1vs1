package at.kingcraft.OnevsOne_arena.Listener;

import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_arena.Duels.DuelManager;

public class RespawnDuelRun implements Runnable
{

	private Player p;
	
	public RespawnDuelRun(Player p)
	{
		this.p = p;
	}
	
	@Override
	public void run()
	{
		Duel d = DuelManager.getDuel(p);
		
		if(d != null)
		{
			int role = d.getRole(p);
			
			d.setupPlayer(p, true);
			
			if(role == Duel.P1)
			{
				p.teleport(d.getMap().getSpawn1());
			}
			else if(role ==Duel.P2)
			{
				p.teleport(d.getMap().getSpawn2());
			}
		}
	}

}
