package at.Kingcraft.OnevsOne_lobby.Commands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.Duels.SpectateDuel;
import at.Kingcraft.OnevsOne_lobby.Duels.SpectateManager;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import at.Kingcraft.OnevsOne_lobby.Special.MenuManager;
import net.md_5.bungee.api.ChatColor;

public class SpectateCommand implements CommandExecutor
{

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only for Players");
			return true;
		}
		
		Player p = (Player)sender;
		
		
		if(args.length == 0)
		{
			MenuManager.getSpectateMenu(p).open();
			return true;
		}
		else if(args.length == 1)
		{
			OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
			if(op == null)
			{
				p.sendMessage(Messages.playerDoesntExists(args[0]));
				return true;
			}
			
			UUID uuid = op.getUniqueId();
			
			ArrayList<SpectateDuel> duels = SpectateManager.getSpecDuels();
			
			for(int i = 0;i<duels.size();i++)
			{
				ArrayList<UUID> u1 = duels.get(i).getP1();
				
				for(int u = 0;u<u1.size();u++)
				{
					if(u1.get(u).equals(uuid))
					{
						p.sendMessage(Messages.youAreGettingTeleported);
						duels.get(i).teleportPlayer(p);
						return true;
					}
				}
				
				ArrayList<UUID> u2 = duels.get(i).getP2();
				
				for(int u = 0;u<u2.size();u++)
				{
					if(u2.get(u).equals(uuid))
					{
						p.sendMessage(Messages.youAreGettingTeleported);
						duels.get(i).teleportPlayer(p);
						return true;
					}
				}
			}
			
			p.sendMessage(Messages.playerDoesntFight(args[0]));
			
			
			return true;
		}
		
		return false;
	}

}
