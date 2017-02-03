package at.Kingcraft.OnevsOne_lobby.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.Duels.ChallangeManager;
import at.Kingcraft.OnevsOne_lobby.Duels.Team;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamManager;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import net.md_5.bungee.api.ChatColor;

public class FFACommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only for Players");
			return true;
		}
		
		Player me = (Player)sender;
		
		if(args.length == 0)
		{
			Team t = TeamManager.getTeam(me);
			if(t==null)
			{
				me.sendMessage(Messages.needTeam);
				return true;
			}
			
			if(TeamManager.getsChecked(me))
			{
				me.sendMessage(Messages.teamRecreate);
				return true;
			}
			
			if(!t.getLeader().getUniqueId().equals(me.getUniqueId()))
			{
				me.sendMessage(Messages.onlyLeader);
				return true;
			}
			
			ChallangeManager.startFFA(t);
			return true;
		}
		
		return false;
	}

}
