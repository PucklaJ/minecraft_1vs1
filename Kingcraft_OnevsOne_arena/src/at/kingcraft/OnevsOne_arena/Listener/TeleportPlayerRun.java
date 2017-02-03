package at.kingcraft.OnevsOne_arena.Listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportPlayerRun implements Runnable {

	private Player p;
	private Location map;
	
	public TeleportPlayerRun(Player p,Location map)
	{
		this.p = p;
		this.map = map;
	}
	
	@Override
	public void run()
	{
		p.teleport(map);
	}
	

}
