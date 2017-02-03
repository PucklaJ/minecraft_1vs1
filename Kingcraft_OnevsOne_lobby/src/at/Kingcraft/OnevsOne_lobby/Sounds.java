package at.Kingcraft.OnevsOne_lobby;

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
	public static final float DEFAULT_PITCH = 0.2f;
	
	public static Sound gotChallenge;
	public static float gotChallengeVolume;
	public static Sound sendChallenge;
	public static float sendChallengeVolume;
	public static Sound otherTookChallenge;
	public static float otherTookChallengeVolume;
	public static Sound tookChallenge;
	public static float tookChallengeVolume;
	public static Sound kitPlaceJoin;
	public static float	kitPlaceJoinVolume;
	public static Sound kitPlaceLeave;
	public static float	kitPlaceLeaveVolume;
	public static Sound gotEnquiery;
	public static float	gotEnquieryVolume;
	public static Sound sendEnquiery;
	public static float	sendEnquieryVolume;
	public static Sound teamJoin;
	public static float	teamJoinVolume;
	public static Sound tournamentJoin;
	public static float	tournamentJoinVolume;
	public static Sound tournamentCreate;
	public static float	tournamentCreateVolume;
	
	
	private static void addDefaults()
	{
		gotChallenge = Sound.ARROW_HIT;
		gotChallengeVolume = DEFAULT_VOL;
		sendChallenge = Sound.BAT_DEATH;
		sendChallengeVolume = DEFAULT_VOL;
		otherTookChallenge = Sound.PIG_DEATH;
		otherTookChallengeVolume = DEFAULT_VOL;
		tookChallenge = Sound.CREEPER_HISS;
		tookChallengeVolume = DEFAULT_VOL;
		kitPlaceJoin = Sound.HORSE_SKELETON_HIT;
		kitPlaceJoinVolume = DEFAULT_VOL;
		kitPlaceLeave = Sound.GHAST_SCREAM;
		kitPlaceLeaveVolume = DEFAULT_VOL;
		gotEnquiery = Sound.LAVA_POP;
		gotEnquieryVolume = DEFAULT_VOL;
		sendEnquiery = Sound.BAT_LOOP;
		sendEnquieryVolume = DEFAULT_VOL;
		teamJoin = Sound.VILLAGER_YES;
		teamJoinVolume = DEFAULT_VOL;
		tournamentJoin = Sound.ENDERDRAGON_GROWL;
		tournamentJoinVolume = DEFAULT_VOL;
		tournamentCreate = Sound.ENDERDRAGON_GROWL;
		tournamentCreateVolume = DEFAULT_VOL;
		
		config.addDefault("got-challenge.sound", gotChallenge.toString());
		config.addDefault("got-challenge.volume", gotChallengeVolume);
		config.addDefault("send-challenge.sound", sendChallenge.toString());
		config.addDefault("send-challenge.volume", sendChallengeVolume);
		config.addDefault("other-took-challenge.sound", otherTookChallenge.toString());
		config.addDefault("other-took-challenge.volume", otherTookChallengeVolume);
		config.addDefault("took-challenge.sound", tookChallenge.toString());
		config.addDefault("took-challenge.volume", tookChallengeVolume);
		config.addDefault("kit-place-join.sound", kitPlaceJoin.toString());
		config.addDefault("kit-place-join.volume", kitPlaceJoinVolume);
		config.addDefault("kit-place-leave.sound", kitPlaceLeave.toString());
		config.addDefault("kit-place-leave.volume", kitPlaceLeaveVolume);
		config.addDefault("got-enquiery.sound", gotEnquiery.toString());
		config.addDefault("got-enquiery.volume", gotEnquieryVolume);
		config.addDefault("send-enquiery.sound", sendEnquiery.toString());
		config.addDefault("send-enquiery.volume", sendEnquieryVolume);
		config.addDefault("team-join.sound", teamJoin.toString());
		config.addDefault("team-join.volume", teamJoinVolume);
		config.addDefault("tournament-join.sound", tournamentJoin.toString());
		config.addDefault("tournament-join.volume", tournamentJoinVolume);
		config.addDefault("tournament-create.sound", tournamentCreate.toString());
		config.addDefault("tournament-create.volume", tournamentCreateVolume);
		
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
		gotChallenge = Sound.valueOf(config.getString("got-challenge.sound"));
		gotChallengeVolume = (float) config.getDouble("got-challenge.volume");
		sendChallenge = Sound.valueOf(config.getString("send-challenge.sound"));
		sendChallengeVolume = (float) config.getDouble("send-challenge.volume");
		otherTookChallenge = Sound.valueOf(config.getString("other-took-challenge.sound"));
		otherTookChallengeVolume = (float) config.getDouble("other-took-challenge.volume");
		tookChallenge = Sound.valueOf(config.getString("took-challenge.sound"));
		tookChallengeVolume = (float) config.getDouble("took-challenge.volume");
		kitPlaceJoin = Sound.valueOf(config.getString("kit-place-join.sound"));
		kitPlaceJoinVolume = (float) config.getDouble("kit-place-join.volume");
		kitPlaceLeave = Sound.valueOf(config.getString("kit-place-leave.sound"));
		kitPlaceLeaveVolume = (float) config.getDouble("kit-place-leave.volume");
		gotEnquiery = Sound.valueOf(config.getString("got-enquiery.sound"));
		gotEnquieryVolume = (float) config.getDouble("got-enquiery.volume");
		sendEnquiery = Sound.valueOf(config.getString("send-enquiery.sound"));
		sendEnquieryVolume = (float) config.getDouble("send-enquiery.volume");
		teamJoin = Sound.valueOf(config.getString("team-join.sound"));
		teamJoinVolume = (float) config.getDouble("team-join.volume");
		tournamentJoin = Sound.valueOf(config.getString("tournament-join.sound"));
		tournamentJoinVolume = (float) config.getDouble("tournament-join.volume");
		tournamentCreate = Sound.valueOf(config.getString("tournament-create.sound"));
		tournamentCreateVolume = (float) config.getDouble("tournament-create.volume");
	}
	
	public static void setup()
	{
		file = new File("plugins/" + MainClass.getInstance().getName() + "/sounds.yml");
		config = YamlConfiguration.loadConfiguration(file);
		
		addDefaults();
		setValues();
	}
}
