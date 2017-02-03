package at.kingcraft.OnevsOne_arena.Tournaments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.MainClass;
import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_arena.Duels.DuelManager;
import at.kingcraft.OnevsOne_setup.Maps.Map;

public class CheckSpecDuelRun implements Runnable
{

	private Spectator s;
	private MainClass plugin;
	
	public CheckSpecDuelRun(Spectator s,MainClass plugin)
	{
		this.s = s;
		this.plugin = plugin;
	}
	
	@Override
	public void run()
	{
		Duel d = null;
		int iterations = 0;
		do
		{
			iterations++;
			d = DuelManager.getFirstDuel();
			if(d != null)
			{
				realTeleport(d, s.player);
				return;
			}
			else if(iterations == 3)
			{
				s.sendBack();
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
		}while(d == null);
		
	}
	
	private void realTeleport(Duel d,Player p)
	{
		Map map = d.getMap();
		
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
			mid.setWorld(p.getWorld());
			
			p.teleport(mid);
		}
	}

}
