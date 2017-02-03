package at.Kingcraft.OnevsOne_lobby.Commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import at.Kingcraft.OnevsOne_lobby.Stats.Statistics;
import at.Kingcraft.OnevsOne_lobby.Stats.StatisticsManager;
import net.md_5.bungee.api.ChatColor;

public class StatsCommand implements CommandExecutor
{

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{	
		Player p = null;
		
		if(sender instanceof Player)
		{
			p = (Player) sender;
		}
		
		if(args.length == 0)
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.RED + "You don't have stats");
				return true;
			}
			else if(!p.hasPermission("command.stats.view"))
			{
				p.sendMessage(Messages.noPermissionStatsView);
				return true;
			}
			
			Statistics stats = StatisticsManager.getStatistics(p,true);
			
			p.sendMessage(Messages.yourStatistics);
			stats.send(p);
			
			return true;
		}
		else if(args.length == 1)
		{
			if(args[0].equalsIgnoreCase("reset") && !p.hasPermission("command.stats.reset"))
			{
				p.sendMessage(Messages.noPermissionStatsReset);
				return true;
			}
			
			Player p1 = null;
			OfflinePlayer op = null;
			Statistics stats = null;
			
			for(Player p2 : Bukkit.getOnlinePlayers())
			{
				if(p2.getDisplayName().equals(args[0]))
				{
					p1 = p2;
					break;
				}
			}
			
			if(p1 == null)
			{
				if((sender instanceof Player) && args[0].equalsIgnoreCase("reset"))
				{
					Statistics.reset(p,p.getUniqueId());
				}
				else
				{
					op = Bukkit.getOfflinePlayer(args[0]);
					if(op == null)
					{
						sender.sendMessage(Messages.playerDoesntExists(args[0]));
						return true;
					}
					else
					{
						stats = StatisticsManager.getStatistics(op.getUniqueId(),true);
					}
				}
			}
			else
			{
				stats = StatisticsManager.getStatistics(p1,true);
			}
			
			if(stats != null)
			{
				sender.sendMessage(Messages.statisticsOf(args[0]));
				stats.send(sender);
			}
			
			
			if(op != null)
			{
				StatisticsManager.deleteStatistics(op.getUniqueId());
			}
			
			return true;
		}
		else if(args.length == 2)
		{
			if(args[0].equalsIgnoreCase("reset"))
			{
				if(!p.hasPermission("command.stats.resetother"))
				{
					p.sendMessage(Messages.noPermissionStatsResetOther);
					return true;
				}
				
				Player p1 = null;
				
				for(Player p2 : Bukkit.getOnlinePlayers())
				{
					if(p2.getDisplayName().equals(args[1]))
					{
						p1 = p2;
						break;
					}	
				}
				
				if(p1 == null)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
					if(op == null)
					{
						p.sendMessage(Messages.playerDoesntExists(args[1]));
						return true;
					}
					else
					{
						Statistics.reset(p, op.getUniqueId());
						return true;
					}
				}
				else
				{
					Statistics.reset(p, p1.getUniqueId());
					return true;
				}
			}
		}
		
		
		return false;
	}

}
