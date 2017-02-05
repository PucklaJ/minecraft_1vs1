package at.kingcraft.OnevsOne_arena;

import java.io.File;
import java.io.IOException;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Sounds
{
	private static FileConfiguration config;
	private static File file;
	private static final float DEFAULT_VOL = 0.2f;
	public static final float DEFAULT_PITCH = 1.0f;
	
	public static Sound duelStart;
	public static float duelStartVolume;
	public static Sound duelTimer;
	public static float duelTimerVolume;
	public static Sound duelWin;
	public static float duelWinVolume;
	public static Sound duelLose;
	public static float duelLoseVolume;
	public static Sound tournamentWin;
	public static float tournamentWinVolume;
	public static Sound giveUp;
	public static float giveUpVolume;
	public static Sound endMatch;
	public static float endMatchVolume;
	public static Sound endMatchTimer;
	public static float endMatchTimerVolume;
	
	private static void addDefaults()
	{
		duelStart = Sound.LEVEL_UP;
		duelStartVolume = DEFAULT_VOL;
		duelTimer = Sound.ORB_PICKUP;
		duelTimerVolume = DEFAULT_VOL;
		duelWin = Sound.CAT_MEOW;
		duelWinVolume = DEFAULT_VOL;
		duelLose = Sound.CHICKEN_HURT;
		duelLoseVolume = DEFAULT_VOL;
		tournamentWin = Sound.SHEEP_IDLE;
		tournamentWinVolume = DEFAULT_VOL;
		giveUp = Sound.SHEEP_IDLE;
		giveUpVolume = DEFAULT_VOL;
		endMatch = Sound.AMBIENCE_THUNDER;
		endMatchVolume = DEFAULT_VOL;
		endMatchTimer = Sound.ORB_PICKUP;
		endMatchTimerVolume = DEFAULT_VOL;
		
		
		config.addDefault("duel-start.sound", duelStart.toString());
		config.addDefault("duel-start.volume", duelStartVolume);
		config.addDefault("duel-timer.sound", duelTimer.toString());
		config.addDefault("duel-timer.volume", duelTimerVolume);
		config.addDefault("duel-win.sound", duelWin.toString());
		config.addDefault("duel-win.volume", duelWinVolume);
		config.addDefault("duel-lose.sound", duelLose.toString());
		config.addDefault("duel-lose.volume", duelLoseVolume);
		config.addDefault("tournament-win.sound", tournamentWin.toString());
		config.addDefault("tournament-win.volume", tournamentWinVolume);
		config.addDefault("give-up.sound", giveUp.toString());
		config.addDefault("give-up.volume", giveUpVolume);
		config.addDefault("end-match.sound", endMatch.toString());
		config.addDefault("end-match.volume", endMatchVolume);
		config.addDefault("end-match-timer.sound", endMatchTimer.toString());
		config.addDefault("end-match-timer.volume", endMatchTimerVolume);
		
		config.options().copyDefaults(true);
		
		try
		{
			config.save(file);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private static void setValues()
	{
		duelStart = Sound.valueOf(config.getString("duel-start.sound"));
		duelTimer = Sound.valueOf(config.getString("duel-timer.sound"));
		duelWin = Sound.valueOf(config.getString("duel-win.sound"));
		duelLose = Sound.valueOf(config.getString("duel-lose.sound"));
		tournamentWin = Sound.valueOf(config.getString("tournament-win.sound"));
		giveUp = Sound.valueOf(config.getString("give-up.sound"));
		endMatch = Sound.valueOf(config.getString("end-match.sound"));
		endMatchTimer = Sound.valueOf(config.getString("end-match-timer.sound"));
		
		duelStartVolume = (float) config.getDouble("duel-start.volume");
		duelTimerVolume = (float) config.getDouble("duel-timer.volume");
		duelWinVolume = (float) config.getDouble("duel-win.volume");
		duelLoseVolume = (float) config.getDouble("duel-lose.volume");
		tournamentWinVolume = (float) config.getDouble("tournament-win.volume");
		giveUpVolume = (float) config.getDouble("give-up.volume");
		endMatchTimerVolume = (float) config.getDouble("end-match-timer.volume");
	}
	
	public static void setup()
	{
		file = new File("plugins/" + MainClass.getInstance().getName() + "/sounds.yml");
		config = YamlConfiguration.loadConfiguration(file);
		
		addDefaults();
		setValues();
	}
}
