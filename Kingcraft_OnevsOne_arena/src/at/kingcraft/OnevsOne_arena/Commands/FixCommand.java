package at.kingcraft.OnevsOne_arena.Commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_arena.Duels.DuelManager;
import at.kingcraft.OnevsOne_arena.Messaging.Messages;
import net.md_5.bungee.api.ChatColor;

public class FixCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only for Players!");
			return true;
		}
		
		if(args.length != 0)
		{
			return false;
		}
		
		Player me = (Player) sender;
		
		Duel d = DuelManager.getDuel(me);
		
		if(d == null)
		{
			me.sendMessage(Messages.haveToBeInDuel);
			return true;
		}
		
		Location loc = me.getLocation();
		
		loc.setY(loc.getY()+1.0);
		
		me.teleport(loc);
		
		me.sendMessage(Messages.fixed);
		
		return true;
	}

}