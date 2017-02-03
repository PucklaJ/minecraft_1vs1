package at.Kingcraft.OnevsOne_lobby.Messaging;

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

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Arenas.ArenaManager;

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
			String prefix = msgin.readUTF();
			String player = msgin.readUTF();
			String msg = msgin.readUTF();
			
			System.out.println("Player: " + player);
			System.out.println("Prefix: " + prefix + "Prefix");
			System.out.println("Message: " + msg);
			
			return Messages.chatFromArena(prefix + player,msg); // Read the data in the same way you wrote it
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
			if(subChannel.equals("GetServers"))
			{
				String[] serverList;
				serverList = in.readUTF().split(", ");
				
				ArenaManager.updateServers(serverList);
			}
			else if(subChannel.equals("GetServer"))
			{
				((MainClass)plugin).serverName = in.readUTF();
			}
			else if(subChannel.equals("Chat"))
			{
				System.out.println("Got Message");
				Bukkit.broadcastMessage(getChatFromForward(in));
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
		bo.writeUTF(subChannel);
		
		if(args != null && args.length != 0)
		{
			for(int i = 0;i<args.length;i++)
			{
				bo.writeUTF(args[i]);
			}
		}
		
		if(p == null)
		{
			if(Messenger.plugin.getServer().getOnlinePlayers().isEmpty())
			{
				return false;
			}
			p=(Player) Messenger.plugin.getServer().getOnlinePlayers().toArray()[0];
			if(p == null)
			{
				return false;
			}
		}
		
		
		p.sendPluginMessage(Messenger.plugin, plugin, bo.toByteArray());
		
		return true;
	}

}
