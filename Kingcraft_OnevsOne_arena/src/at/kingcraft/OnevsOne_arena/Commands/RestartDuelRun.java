package at.kingcraft.OnevsOne_arena.Commands;

import org.bukkit.Bukkit;

import at.kingcraft.OnevsOne_arena.MainClass;
import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_setup.Maps.MapManager;

public class RestartDuelRun implements Runnable
{

	private Duel d;
	private int restartID;
	
	public RestartDuelRun(Duel d)
	{
		this.d = d;
	}
	
	@Override
	public void run()
	{
		restartID = Bukkit.getScheduler().runTaskTimerAsynchronously(MainClass.getInstance(), new Runnable() {
			
			@Override
			public void run()
			{
				if(!MapManager.isLoading)
				{
					d.restart();
					Bukkit.getScheduler().cancelTask(restartID);
				}
			}
		}, 0, 10).getTaskId();
	}

}
