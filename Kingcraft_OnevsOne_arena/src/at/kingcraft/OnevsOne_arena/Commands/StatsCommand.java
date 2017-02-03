package at.kingcraft.OnevsOne_arena.Commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.Duels.Statistics;
import at.kingcraft.OnevsOne_arena.Duels.StatisticsManager;
import at.kingcraft.OnevsOne_arena.Messaging.Messages;
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
			
			Statistics stats = StatisticsManager.getStatistics(p,true);
			
			p.sendMessage(Messages.yourStatistics);
			stats.send(p);
			
			return true;
		}
		else if(args.length == 1)
		{	
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
		
		
		return false;
	}

}

