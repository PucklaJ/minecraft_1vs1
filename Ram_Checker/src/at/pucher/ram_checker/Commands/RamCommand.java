package at.pucher.ram_checker.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import at.pucher.ram_checker.MainClass;
import net.md_5.bungee.api.ChatColor;

public class RamCommand implements CommandExecutor {

	private static final int KB = 0,MB=1,GB=2;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		int which = MB;
		
		if(args.length > 0)
		{
			if(args[0].equalsIgnoreCase("KB"))
			{
				which = KB;
			}
			else if(args[0].equalsIgnoreCase("MB"))
			{
				which = MB;
			}
			else if(args[0].equalsIgnoreCase("GB"))
			{
				which = GB;
			}
			else if((args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) && sender.hasPermission("command_ram.reload"))
			{
				which = -1;
				MainClass.ReloadConfig();
				
				sender.sendMessage(MainClass.prefix + ChatColor.YELLOW + "Reloaded Config!");
			}
		}
		
		if(which != -1 && sender.hasPermission("command_ram.ram"))
		{
			double used = getUsedRam();
			
			if(which == KB)
			{
				used = byteToKB(used);
			}
			else if(which == MB)
			{
				used = KBToMB(byteToKB(used));
			}
			else if(which == GB)
			{
				used = MBToGB(KBToMB(byteToKB(used)));
			}
			
			sender.sendMessage(MainClass.prefix + ChatColor.YELLOW + "Used Ram: " + ChatColor.BLUE + MainClass.round(used,4) + (which == KB ? " KB" : (which == MB ? " MB" : " GB")));
		}
		
		return true;
	}
	
	public static double byteToKB(double b)
	{
		return b/1024;
	}
	
	public static double KBToMB(double kb)
	{
		return kb/1024;
	}
	
	public static double MBToGB(double mb)
	{
		return mb/1024;
	}
	
	public static double getUsedRam()
	{
		double maxMemory = getMaxRam();
		double freeMemory = getFreeRam();
		
		return maxMemory - freeMemory;
	}
	
	public static double getMaxRam()
	{
		return KBToMB(byteToKB(Runtime.getRuntime().maxMemory()));
	}
	
	public static double getFreeRam()
	{
		return KBToMB(byteToKB(Runtime.getRuntime().freeMemory()));
	}


}
