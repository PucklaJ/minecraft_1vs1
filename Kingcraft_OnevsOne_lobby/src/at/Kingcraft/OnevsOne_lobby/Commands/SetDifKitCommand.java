package at.Kingcraft.OnevsOne_lobby.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.Kits.Kit;
import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;
import at.Kingcraft.OnevsOne_lobby.Special.KitSettingMenu;
import net.md_5.bungee.api.ChatColor;

public class SetDifKitCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only for Players");
			return true;
		}
		
		Player me = (Player) sender;
		
		if(args.length == 1)
		{
			if(args[0].equals("3") ||args[0].equals("2") ||args[0].equals("1"))
			{
				KitManager.addDifKitSetter(me, Integer.valueOf(args[0]));
				return true;
			}
		}
		else if(args.length == 2)
		{
			if(args[0].equals("3") ||args[0].equals("2") ||args[0].equals("1"))
			{
				if(args[1].equals("set"))
				{
					KitSettingMenu ksm = KitManager.getDifPreKitSettingsMenu(me);
					
					if(ksm == null)
					{
						KitManager.addDifPreKitSettingsMenu(me);
						ksm = KitManager.getDifPreKitSettingsMenu(me);
					}
					
					Kit kit = KitManager.getDifKit(Integer.valueOf(args[0]));
					
					if(kit == null)
					{
						me.sendMessage(ChatColor.RED + "You have to define it first");
						return true;
					}
					
					ksm.open(kit);
					
					return true;
				}
			}
		}
		
		return false;
	}

}
