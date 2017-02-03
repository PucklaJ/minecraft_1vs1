package at.kingcraft.OnevsOne_arena.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.Sounds;
import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_arena.Duels.DuelManager;
import at.kingcraft.OnevsOne_arena.Messaging.Messages;
import net.md_5.bungee.api.ChatColor;

public class EndmatchCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only for Players!");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(args.length == 0)
		{
			Duel d = DuelManager.getDuel(p);
			
			if(d==null)
			{
				p.sendMessage(Messages.haveToBeInDuel);
				return true;
			}
			
			if(d.isTournament())
			{
				p.sendMessage(Messages.thisIsntAllowedInTournament);
				return true;
			}
			
			if(!d.isStarted())
			{
				p.sendMessage(Messages.duelHasntBeenStarted);
				return true;
			}
			else if(d.hasEnded())
			{
				p.sendMessage(Messages.duelIsOver);
				return true;
			}
			else if(d.isSpectator(p, d.isTournament()))
			{
				p.sendMessage(Messages.endMatchSpectator);
				return true;
			}
			
			if(d.timerStarted())
			{
				p.sendMessage(Messages.timerHasAlreadyBeenStarted);
				return true;
			}
			
			p.sendMessage(Messages.timerStarts);
			p.playSound(p.getLocation(), Sounds.endMatch, Sounds.endMatchVolume, Sounds.DEFAULT_PITCH);
			d.activateTimer(1,0,true);
			
			return true;
		}
		
		return false;
	}

}
