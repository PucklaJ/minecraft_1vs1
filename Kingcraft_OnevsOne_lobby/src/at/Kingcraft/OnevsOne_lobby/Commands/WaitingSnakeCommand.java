package at.Kingcraft.OnevsOne_lobby.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.Duels.Team;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamManager;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import at.Kingcraft.OnevsOne_lobby.WaitingSnake.WaitingSnake;
import net.md_5.bungee.api.ChatColor;

public class WaitingSnakeCommand implements CommandExecutor {

	private WaitingSnake ws;
	
	public WaitingSnakeCommand(WaitingSnake ws)
	{
		this.ws = ws;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only for Players");
			return true;
		}
		
		Player me = (Player) sender;
		
		if(TeamManager.getsChecked(me))
		{
			me.sendMessage(Messages.teamRecreate);
		}
		
		Team t = TeamManager.getTeam(me);
		if(t != null)
		{
			if(!t.getLeader().getUniqueId().equals(me.getUniqueId()))
			{
				me.sendMessage(Messages.onlyLeaderWaitingSnake);
				return true;
			}
		}
		
		if(args.length != 0)
		{
			return false;
		}
		
		if(ws.isIn(me))
		{
			ws.removePlayer(me,true,true);
		}
		else
		{
			ws.addPlayer(me,true);
		}
		
		
		
		return true;
	}

}
