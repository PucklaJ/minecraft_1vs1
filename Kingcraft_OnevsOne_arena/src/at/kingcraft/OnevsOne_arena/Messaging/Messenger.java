package at.kingcraft.OnevsOne_arena.Messaging;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import at.kingcraft.OnevsOne_arena.MainClass;
import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_arena.Duels.DuelManager;
import at.kingcraft.OnevsOne_arena.Listener.DuelListener;
import at.kingcraft.OnevsOne_arena.Tournaments.TournamentManager;

public class Messenger implements PluginMessageListener {

	private static JavaPlugin plugin;
	
	public static void setup(JavaPlugin plugin)
	{
		Messenger.plugin = plugin;
	}
	
	private String getChatFromForward(ByteArrayDataInput in)
	{
		short len = in.readShort();
		byte[] msgbytes = new byte[len];
		in.readFully(msgbytes);

		try
		{
			DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
			
			DuelListener.addLobbyServerChat(msgin.readUTF());
			return Messages.chatFromLobby(msgin.readUTF() + msgin.readUTF(), msgin.readUTF()); // Read the data in the same way you wrote it
		}
		catch(IOException e)
		{
			
		}
		
		return null;
	}
	
	private TournamentMessage getMessageFromTournament(ByteArrayDataInput in)
	{
		short len = in.readShort();
		byte[] msgbytes = new byte[len];
		in.readFully(msgbytes);

		try
		{
			DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
			
			TournamentMessage tm = new TournamentMessage();
			tm.server = msgin.readUTF();
			String size = msgin.readUTF();
			tm.message = Messages.tournamentCreate(msgin.readUTF(),msgin.readUTF(),size);
			
			return tm;
			
		}
		catch(IOException e)
		{
			
		}
		
		return null;
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] msg)
	{
		ByteArrayDataInput in = ByteStreams.newDataInput(msg);
		
		String subChannel = in.readUTF();
		
		
		if(channel.equals("BungeeCord"))
		{
			if(subChannel.equals("GetServer"))
			{
				((MainClass)plugin).serverName = in.readUTF();
			}
			else if(subChannel.equals("Chat"))
			{
				Bukkit.broadcastMessage(getChatFromForward(in));
			}
			else if(subChannel.equals("Tournament"))
			{
				Duel d = DuelManager.getFirstDuel();
				if(d != null)
				{
					TournamentMessage tm = getMessageFromTournament(in);
					for(int i = 0;i<d.getP1().size();i++)
					{
						if(d.getHomeServer(d.getP1().get(i)).equals(tm.server))
						{
							d.getP1().get(i).sendMessage(tm.message);
						}
					}
					
					for(int i = 0;i<d.getP2().size();i++)
					{
						if(d.getHomeServer(d.getP2().get(i)).equals(tm.server))
						{
							d.getP2().get(i).sendMessage(tm.message);
						}
					}
					
					for(int i = 0;i<d.getSpectators().size();i++)
					{
						if(d.getSpectators().get(i).homeServer.equals(tm.server))
						{
							d.getSpectators().get(i).player.sendMessage(tm.message);
						}
					}
					
					for(int i = 0;i<TournamentManager.getSpecs().size();i++)
					{
						if(TournamentManager.getSpecs().get(i).homeServer.equals(tm.server))
						{
							TournamentManager.getSpecs().get(i).player.sendMessage(tm.message);
						}
					}
				}
			}
		}
	}
	
	public static boolean sendMessage(Player p,String plugin,String subChannel,String args)
	{
		String[] argss = new String[1];
		argss[0] = args;
		
		return sendMessage(p,plugin,subChannel,argss);
	}
	
	public static boolean sendMessage(Player p,String plugin,String subChannel,String[] args)
	{
		ByteArrayDataOutput bo = ByteStreams.newDataOutput();
		if(bo == null)
			return false;
		bo.writeUTF(subChannel);
		
		
		if(args != null)
		{
			for(int i = 0;i<args.length;i++)
			{
				if(bo != null)
				bo.writeUTF(args[i]);
			}
		}
		
		
		if(p == null)
		{
			if(Bukkit.getServer().getOnlinePlayers().isEmpty())
			{
				return false;
			}
			p=(Player) Bukkit.getServer().getOnlinePlayers().toArray()[0];
			if(p == null)
			{
				return false;
			}
		}
		
		if(bo != null)
			p.sendPluginMessage(MainClass.getInstance(), plugin, bo.toByteArray());
		
		return true;
	}

}
