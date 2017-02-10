package at.kingcraft.OnevsOne_arena;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import MySQL.MySQL;
import at.kingcraft.OnevsOne_arena.Challenges.ChallangeManager;
import at.kingcraft.OnevsOne_arena.Commands.EndmatchCommand;
import at.kingcraft.OnevsOne_arena.Commands.FixCommand;
import at.kingcraft.OnevsOne_arena.Commands.GiveUpCommand;
import at.kingcraft.OnevsOne_arena.Commands.StatsCommand;
import at.kingcraft.OnevsOne_arena.Commands.TurnierCommand;
import at.kingcraft.OnevsOne_arena.Commands.WaitingHouseCommand;
import at.kingcraft.OnevsOne_arena.Duels.DuelManager;
import at.kingcraft.OnevsOne_arena.Duels.StatisticsManager;
import at.kingcraft.OnevsOne_arena.Kits.Kit;
import at.kingcraft.OnevsOne_arena.Listener.DuelListener;
import at.kingcraft.OnevsOne_arena.Menus.MenuManager;
import at.kingcraft.OnevsOne_arena.Messaging.Messages;
import at.kingcraft.OnevsOne_arena.Messaging.Messenger;
import at.kingcraft.OnevsOne_arena.Scoreboard.MyScoreboardManager;
import at.kingcraft.OnevsOne_arena.Tournaments.TournamentManager;
import at.kingcraft.OnevsOne_arena.Waiting.WaitingHouse;
import at.kingcraft.OnevsOne_arena.WaitingSnake.Settings;
import net.md_5.bungee.api.ChatColor;

public class MainClass extends JavaPlugin{
	private MySQL mysql;
	private String prefix;
	public String serverName = "";
	private int sqlTask;
	private static MainClass instance;
	
	@Override
	public void onDisable()
	{
		super.onDisable();
		
		Bukkit.getScheduler().cancelTask(sqlTask);
		
		OnlinePlayers.deleteFromMySQL();
		
		deleteFromMySQL();
		
		for(Player p : Bukkit.getOnlinePlayers())
		{
			MenuManager.deleteDuelsMenu(p);
		}
		
		System.out.println(prefix + " Successfully disabled");
	}
	
	private void deleteFromMySQL()
	{
		try
		{
			if(mysql.isConnected() && serverName != null)
			{
				PreparedStatement ps = mysql.getConnection().prepareStatement("DELETE FROM Duel_Servers WHERE Name = ?");
				ps.setString(1, serverName);
				ps.executeUpdate();
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public MySQL getMySQL()
	{
		return mysql;
	}

	private void setupCommands()
	{
		this.getCommand("giveup").setExecutor(new GiveUpCommand(this));
		getCommand("setwaitinghouse").setExecutor(new WaitingHouseCommand());
		getCommand("fix").setExecutor(new FixCommand());
		getCommand("endmatch").setExecutor(new EndmatchCommand());
		getCommand("stats").setExecutor(new StatsCommand());
		getCommand("turnier").setExecutor(new TurnierCommand());
	}
	
	private void sendServerToMySQL(String server,int free)
	{
		try
		{
			// Ask if already exists
			
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT * FROM Duel_Servers WHERE name = ?");
			ps.setString(1, server);
			ResultSet rs = ps.executeQuery();
			if(!rs.first())
			{
				ps = mysql.getConnection().prepareStatement("INSERT INTO Duel_Servers (Name,free) VALUES (?,?)");
				ps.setString(1, server);
				ps.setInt(2, free);
				ps.executeUpdate();
			}
			
			
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static MainClass getInstance()
	{
		return instance;
	}
	
	private void setupConfig() {
		this.getConfig().options().header("Einstellungen fuer OnevsOne plugin");
		
		this.getConfig().addDefault("Scoreboard.Header", ChatColor.LIGHT_PURPLE + "madebylo.net");
		this.getConfig().addDefault("Scoreboard.Linebreak.Prefix", ChatColor.GRAY + "");
		this.getConfig().addDefault("Scoreboard.Linebreak.String","-");
		this.getConfig().addDefault("Scoreboard.Timer.Prefix", ChatColor.YELLOW + "");
		this.getConfig().addDefault("Scoreboard.Timer.Suffix", "");
		this.getConfig().addDefault("Scoreboard.Map.Prefix", ChatColor.YELLOW + "Map: " + ChatColor.BLUE);
		this.getConfig().addDefault("Scoreboard.Map.Suffix", "");
		
		this.getConfig().addDefault("Teams.Team1.Prefix", ChatColor.RED + "");
		this.getConfig().addDefault("Teams.Team1.Suffix", "");
		this.getConfig().addDefault("Teams.Team2.Prefix", ChatColor.BLUE + "");
		this.getConfig().addDefault("Teams.Team2.Suffix", "");
		
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();

		Messages.setup();
		Sounds.setup();
		
		System.out.println("[" + getName() +  "] Successfully setup config.yml");
	}
	
	@Override
	public void onEnable()
	{
		try
		{
			super.onEnable();
		}
		catch(IllegalArgumentException e)
		{
			
		}
		
		
		instance = this;
		
		prefix = "[" + getName() + "]";
		
		try
		{
			mysql = MySQL.getInstance() != null ? MySQL.getInstance() : new MySQL(this);
		}
		catch (SQLException e)
		{
			
		}
		
		setupConfig();
		
		// Setup Messaging System
		Messenger.setup(this);
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new Messenger());
		
		// Register Listeners
		this.getServer().getPluginManager().registerEvents(new DuelListener(this), this);
		OnlinePlayers.setup();
		MyScoreboardManager.setup();
		
		
		
		
		setupCommands();
		
		DuelManager.setup(this);
		WaitingHouse.setup(this);
		Kit.setupKits(mysql);
		Settings.setup(mysql);
		TournamentManager.setup(mysql,this);
		ChallangeManager.setup(this);
		MenuManager.setup();
		StatisticsManager.setup();
		
		// Send every 5 minutes server to mysql
		
		sqlTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			
			@Override
			public void run()
			{
				if(mysql.isConnected() && serverName != null)
				{
					sendServerToMySQL(serverName, getServer().getOnlinePlayers().size() == 0 ? 1 : 0);
				}
				
			}
		}, 0, 20*60*5).getTaskId();
		
		
		long pushFrequenzy = 1;
		
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			
			@Override
			public void run() {
				DuelManager.pushSpectatorsBack();
				TournamentManager.pushSpectatorsBack();
			}
		}, pushFrequenzy,pushFrequenzy);
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			
			@Override
			public void run()
			{
				ChallangeManager.checkPlayers();	
			}
		}, 20*5, 20*5);
	}

}
