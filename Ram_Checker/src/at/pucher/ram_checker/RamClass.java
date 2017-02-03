package at.pucher.ram_checker;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import static at.pucher.ram_checker.Commands.RamCommand.byteToKB;
import static at.pucher.ram_checker.Commands.RamCommand.KBToMB;

import at.pucher.ram_checker.Commands.RamCommand;
import net.md_5.bungee.api.ChatColor;

public class RamClass {

	private static BukkitTask checkTask;
	private static int tooMuchTimes = 0;
	private static boolean cancel = false;
	public static double curPlayerRam = 0.0;
	private static boolean hasStarted = false;
	

	public static void startChecking()
	{
		if(hasStarted)
			return;
		
		hasStarted = true;
		checkTask = Bukkit.getScheduler().runTaskTimerAsynchronously(MainClass.getInstance(), new Runnable() {
			
			@Override
			public void run()
			{
					if(!checkRam())
					{
						Bukkit.broadcastMessage(MainClass.prefix + ChatColor.RED + "Server uses too much RAM!");
						tooMuchTimes++;
						if(tooMuchTimes == MainClass.ramcheckamount)
						{
							Bukkit.broadcastMessage(MainClass.prefix + ChatColor.RED + "Server shuts down in "+ MainClass.ramshutdowntime/20 +" Seconds");
							Bukkit.getScheduler().cancelTask(checkTask.getTaskId());
							
							Bukkit.getScheduler().runTaskLaterAsynchronously(MainClass.getInstance(), new Runnable()
							{
								
								@Override
								public void run()
								{
									Bukkit.getServer().shutdown();
								}
							},MainClass.ramshutdowntime);
						}
						else if(cancel)
						{
							return;
						}
					}
					else
					{
						tooMuchTimes = 0;
					}
			}
		},MainClass.ramchecktime,MainClass.ramchecktime);
	}
	
	private static boolean checkRam()
	{
		if(!MainClass.ramstop)
			return true;
		
		double usedRam = KBToMB(byteToKB(RamCommand.getUsedRam()));
		
		if(curPlayerRam - usedRam <= MainClass.ramtrigger)
		{
			return false;
		}
		
		return true;
	}
	
	public static void addPlayerRam()
	{
		curPlayerRam += MainClass.ramplayer;
	}
	
	public static void removePlayerRam()
	{
		curPlayerRam -= MainClass.ramplayer;
	}
	
	public static void endChecking()
	{
		if(!hasStarted)
			return;
		
		cancel = true;
		hasStarted = false;
		Bukkit.getScheduler().cancelTask(checkTask.getTaskId());
	}
}
