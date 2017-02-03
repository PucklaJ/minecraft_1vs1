package at.kingcraft.OnevsOne_arena.Tournaments;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.MainClass;
import at.kingcraft.OnevsOne_arena.Messaging.Messenger;

public class Spectator
{
	public Player player;
	public int tournamentID;
	public String homeServer;
	public String name;
	public UUID uuid;
	
	public Spectator(Player player,int tID,String hServer)
	{
		this.name = player.getName();
		this.tournamentID = tID;
		this.homeServer = hServer;
		this.uuid = player.getUniqueId();
		this.player = player;
	}
	
	public Spectator(String name,UUID uuid,int tID,String hServer)
	{
		this.name = name;
		this.tournamentID = tID;
		this.homeServer = hServer;
		this.uuid = uuid;
		player = getPlayer(uuid);
	}
	
	private Player getPlayer(UUID uuid)
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(p.getUniqueId().equals(uuid))
			{
				return p;
			}
		}
		
		return null;
	}
	
	public void sendBack()
	{
		Bukkit.getScheduler().runTaskLater(MainClass.getInstance(), new Runnable() {
			
			@Override
			public void run()
			{
				if(player != null)
				{
					Messenger.sendMessage(player, "BungeeCord", "Connect", homeServer);
				}
				else
				{
					String[] args = new String[2];
					args[0] = name;
					args[1] = homeServer;
					Messenger.sendMessage(null, "BungeeCord", "ConnectOther", args);
				}
				
			}
		}, 20*2);
		
	}
}
