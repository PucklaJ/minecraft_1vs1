package at.kingcraft.OnevsOne_arena.Duels;

import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.Messaging.Messenger;

public class DuelSpec
{
	public Player player;
	public String homeServer;
	
	public DuelSpec(Player p,String hs)
	{
		player = p;
		homeServer = hs;
	}
	
	public void sendBack()
	{
		Messenger.sendMessage(player, "BungeeCord", "Connect", homeServer);
	}
}
