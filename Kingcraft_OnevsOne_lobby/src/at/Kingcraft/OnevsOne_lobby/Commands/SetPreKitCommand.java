package at.Kingcraft.OnevsOne_lobby.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;
import at.Kingcraft.OnevsOne_lobby.Special.KitSettingMenu;
import net.md_5.bungee.api.ChatColor;

public class SetPreKitCommand implements CommandExecutor {

	private static final int MAX_PRE_KITS = 28;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only for Players");
			return true;
		}
		
		Player me = (Player)sender;
		
		if(args.length == 1)
		{
			try
			{
				int number = Integer.valueOf(args[0]);
				
				if(number < 1 || number > MAX_PRE_KITS)
				{
					me.sendMessage(ChatColor.RED + "Number has to be between " + ChatColor.BLUE + "1-" + MAX_PRE_KITS);
					return true;
				}
				
				KitManager.addPreKitSetter(me, number);
			}
			catch(NumberFormatException e)
			{
				me.sendMessage(ChatColor.BLUE + args[0] + ChatColor.RED + " isn't a proper number");
			}
			
			return true;
		}
		else if(args.length == 2)
		{
			if(args[1].equalsIgnoreCase("set"))
			{
				try
				{
					int number = Integer.valueOf(args[0]);
					
					if(number < 1 || number > MAX_PRE_KITS)
					{
						me.sendMessage(ChatColor.RED + "Number has to be between " + ChatColor.BLUE + "1-" + MAX_PRE_KITS);
						return true;
					}
					
					KitSettingMenu ksm = KitManager.getDifPreKitSettingsMenu(me);
					
					if(ksm == null)
					{
						KitManager.addDifPreKitSettingsMenu(me);
						ksm = KitManager.getDifPreKitSettingsMenu(me);
					}
					
					ksm.open(KitManager.getPreKit(number));
					
				}
				catch(NumberFormatException e)
				{
					me.sendMessage(ChatColor.BLUE + args[0] + ChatColor.RED + " isn't a proper number");
				}
				
				return true;
			}
		}
		
		return false;
	}

}
