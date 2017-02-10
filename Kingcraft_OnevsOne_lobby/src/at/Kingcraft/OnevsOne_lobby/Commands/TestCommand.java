package at.Kingcraft.OnevsOne_lobby.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messenger;
import net.md_5.bungee.api.ChatColor;

public class TestCommand implements CommandExecutor {

	private MainClass plugin = null;
	
	public TestCommand(MainClass plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p=null;
		
		if(args.length > 0)
			return false;
		
		if(sender instanceof Player)
		{
			p = (Player)sender;
			p.sendMessage(ChatColor.YELLOW + "Hallo, " + ChatColor.GREEN + p.getDisplayName());
		
			String[] argsS = new String[1];
			argsS[0] = "pvparena-1";
			Messenger.sendMessage(p, "BungeeCord", "Connect", argsS);
		}
		
		sender.sendMessage(ChatColor.YELLOW + "Plugin " + ChatColor.GREEN + "[" + ChatColor.GREEN + plugin.getName() + ChatColor.GREEN + "]" + ChatColor.YELLOW + " erfolgreich installiert");
		
		return true;
	}

}
