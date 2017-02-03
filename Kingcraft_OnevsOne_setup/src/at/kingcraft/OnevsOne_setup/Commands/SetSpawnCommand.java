package at.kingcraft.OnevsOne_setup.Commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class SetSpawnCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only for Players");
			return true;
		}
		if(args.length != 0)
		{
			return false;
		}
		
		Player me = (Player)sender;
		Location loc = me.getLocation();
		me.getWorld().setSpawnLocation((int)loc.getX(),(int)loc.getY(),(int)loc.getZ());
		
		me.sendMessage(ChatColor.YELLOW + "Set spawn to: " + ChatColor.BLUE + (int)loc.getX() + " " + (int)loc.getY() + " " + (int)loc.getZ());
		
		
		
		return true;
	}

}
