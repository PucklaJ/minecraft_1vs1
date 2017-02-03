package at.kingcraft.OnevsOne_arena.Listener;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.Tournaments.Spectator;

public class HideAllSpecsRun implements Runnable
{

	private Player p;
	private ArrayList<Spectator> specs;
	
	public HideAllSpecsRun(Player p,ArrayList<Spectator> specs)
	{
		super();
		this.p = p;
		this.specs = specs;
	}
	
	private void hideAllSpectators()
	{
		for(Spectator s : specs)
		{
			if(s.player.getUniqueId().equals(p.getUniqueId()))
			{
				continue;
			}
			
			
			p.hidePlayer(s.player);
		}
	}
	
	@Override
	public void run()
	{
		hideAllSpectators();
	}

}
