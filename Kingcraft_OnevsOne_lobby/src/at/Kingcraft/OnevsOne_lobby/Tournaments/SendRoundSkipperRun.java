package at.Kingcraft.OnevsOne_lobby.Tournaments;

import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.Messaging.Messenger;

public class SendRoundSkipperRun implements Runnable
{
	Player p;
	String server;
	
	public SendRoundSkipperRun(Player p,String server)
	{
		this.p = p;
		this.server = server;
	}
	
	@Override
	public void run()
	{
		Messenger.sendMessage(p, "BungeeCord", "Connect", server);
	}

}
