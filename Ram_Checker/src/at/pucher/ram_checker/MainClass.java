package at.pucher.ram_checker;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import at.pucher.ram_checker.Commands.RamCommand;
import net.md_5.bungee.api.ChatColor;

public class MainClass extends JavaPlugin implements Listener
{
	public static String prefix;
	private static MainClass instance;
	public static boolean ramstop;
	public static double ramserver;
	public static double ramplayer;
	public static double ramtrigger;
	public static int ramchecktime;
	public static int ramshutdowntime;
	public static int ramcheckamount;
	private static boolean enabled = false;
	
	private void setupCommands()
	{
		getCommand("ram").setExecutor(new RamCommand());
	}
	
	private void setupConfig()
	{
		saveDefaultConfig();
		
		/*this.getConfig().options().header("Einstellungen fuer das Ram-Plugin");
		
		this.getConfig().addDefault("ramstop", true);
		this.getConfig().addDefault("ramserver", 64.0);
		this.getConfig().addDefault("ramplayer", 128.0);
		this.getConfig().addDefault("ramtrigger", 0.0);
		this.getConfig().addDefault("ramchecktime", 5);
		this.getConfig().addDefault("ramshutdowntime", 60);
		this.getConfig().addDefault("ramcheckamount", 5);
		
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();*/
		
		
		setValues();
	}
	
	@Override
	public void onEnable()
	{
		super.onEnable();
		
		prefix = ChatColor.AQUA + "[" + getName() + "] ";
		instance = this;
		
		setupConfig();
		setupCommands();
		
		this.getServer().getPluginManager().registerEvents(this, this);
		
		
		
		for(@SuppressWarnings("unused") Player p : Bukkit.getOnlinePlayers())
		{
			RamClass.addPlayerRam();
		}
		
		RamClass.curPlayerRam += ramserver;
		if(ramstop)
			RamClass.startChecking();
		
		System.out.println(ChatColor.stripColor(prefix) + "Succesfully enabled!");
		enabled = true;
	}

	public static MainClass getInstance()
	{
		return instance;
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		
		for(@SuppressWarnings("unused") Player p : Bukkit.getOnlinePlayers())
		{
			RamClass.removePlayerRam();
		}
		
		RamClass.endChecking();
		
		System.out.println(ChatColor.stripColor(prefix) + "Succesfully disabled!");
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		RamClass.addPlayerRam();
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e)
	{
		RamClass.removePlayerRam();
	}
	
	private static void setValues()
	{
		if(enabled)
		{
			RamClass.curPlayerRam = 0.0;
			RamClass.endChecking();
		}
			
		
		ramstop = instance.getConfig().getBoolean("ramstop");
		ramserver = instance.getConfig().getDouble("ramserver");
		ramplayer = instance.getConfig().getDouble("ramplayer");
		ramtrigger = instance.getConfig().getDouble("ramtrigger");
		ramchecktime = instance.getConfig().getInt("ramchecktime");
		ramshutdowntime = instance.getConfig().getInt("ramshutdowntime");
		ramcheckamount = instance.getConfig().getInt("ramcheckamount");
		
		if(enabled)
		{
			RamClass.curPlayerRam += ramserver;
			for(@SuppressWarnings("unused") Player p : Bukkit.getOnlinePlayers())
			{
				RamClass.addPlayerRam();
			}
			
			if(ramstop)
			{
				RamClass.startChecking();
			}
		}
	}
	
	public static void ReloadConfig()
	{
		instance.reloadConfig();
		setValues();
	}
	
	public static double round(double value, int places)
	{
	    if (places < 0) throw new IllegalArgumentException();
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	
}
