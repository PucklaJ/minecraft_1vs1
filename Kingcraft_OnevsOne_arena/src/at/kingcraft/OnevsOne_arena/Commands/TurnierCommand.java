package at.kingcraft.OnevsOne_arena.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_arena.Duels.DuelManager;
import at.kingcraft.OnevsOne_arena.Menus.MenuManager;
import at.kingcraft.OnevsOne_arena.Messaging.Messages;
import net.md_5.bungee.api.ChatColor;

public class TurnierCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		if(args.length == 0)
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.RED + "Only for Players!");
				return true;
			}
			
			Player p = (Player)sender;
			
			Duel d = DuelManager.getFirstDuel();
			
			if(d==null)
			{
				p.sendMessage(Messages.thereIsNoDuel);
				return true;
			}
			
			if(!d.isTournament())
			{
				p.sendMessage(Messages.thereIsNoTournament);
				return true;
			}
			
			MenuManager.getDuelsMenu(p).open();
			
			return true;
		}
		
		return false;
	}

}
