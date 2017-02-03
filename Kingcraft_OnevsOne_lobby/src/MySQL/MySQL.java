package MySQL;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


public class MySQL
{
	private Connection conn;
	private String host;
	private String port;
	private String database;
	private String username;
	private String password;
	private JavaPlugin plugin;
	private static MySQL instance;
	private BukkitTask checkConRun;
	
	public void connect() throws SQLException
	{
		try
		{
			conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database,username,password);
			System.out.println("[MySQL] Connected");
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.out.println("[MySQL] Connection failed");
			throw e;
		}
	}
	
	public void disconnect()
	{
		if(!isConnected())
		{
			return;
		}
		try
		{
			conn.close();
			System.out.println("[MySQL] disconnected");
			if(checkConRun != null)
			{
				Bukkit.getScheduler().cancelTask(checkConRun.getTaskId());
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private File getFile()
	{
		return new File("plugins/" + plugin.getName(),"mysql.yml");
	}
	
	private FileConfiguration getFileConfiguration()
	{
		return YamlConfiguration.loadConfiguration(getFile());
	}
	
	public Connection getConnection()
	{
		return conn;
	}
	
	public MySQL(JavaPlugin plugin) throws SQLException
	{	
		this.plugin = plugin;
		
		FileConfiguration cfg = getFileConfiguration();
		cfg.addDefault("host", "localhost");
		cfg.addDefault("port", "3306");
		cfg.addDefault("database", "minecraft_onevsone");
		cfg.addDefault("username", "root");
		cfg.addDefault("password", "sophlybylo69pw");
		
		
		
		cfg.options().copyDefaults(true);
		try {
		
			cfg.save(getFile());
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		host = cfg.getString("host");
		port = cfg.getString("port");
		database = cfg.getString("database");
		username = cfg.getString("username");
		password = cfg.getString("password");
		
		connect();
		
		instance = this;
		checkConRun = null;
		
		if(isConnected())
		{
			checkConRun = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new CheckConnectionRun(this), 20*60*5, 20*60*5);
		}
	}
	
	public static MySQL getInstance()
	{
		return instance;
	}
	
	public boolean isConnected()
	{
		return conn != null;
	}
}
