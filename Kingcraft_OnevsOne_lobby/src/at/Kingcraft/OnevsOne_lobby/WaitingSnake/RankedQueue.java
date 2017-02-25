package at.Kingcraft.OnevsOne_lobby.WaitingSnake;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Sounds;
import at.Kingcraft.OnevsOne_lobby.Arenas.ArenaManager;
import at.Kingcraft.OnevsOne_lobby.Commands.ForceQueueCommand;
import at.Kingcraft.OnevsOne_lobby.Duels.DuelManager;
import at.Kingcraft.OnevsOne_lobby.Kits.Kit;
import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import at.Kingcraft.OnevsOne_lobby.Special.MapMenu;
import at.Kingcraft.OnevsOne_lobby.Special.MenuManager;

public class RankedQueue
{
	private static ArrayList<RankedUpload> playersToUpload;
	private static ArrayList<RankedUpload> playersInQueue;
	private static final int MAX_ELO_DIF = 1000,
							 MAX_WEEK = 10,
							 MAX_DAY = 3,
							 MAX_HOUR = 1;
	
	public static void setup()
	{
		playersToUpload = new ArrayList<>();
		playersInQueue = new ArrayList<>();
		
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Duel_RankedQueue (UUID VARCHAR(100),Player VARCHAR(10000))");
			ps.executeUpdate();
			ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Duel_RankedELO (UUID VARCHAR(100),ELO INT(255))");
			ps.executeUpdate();
			
			ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Duel_ForceQueue (UUID VARCHAR(100), Player VARCHAR(100))");
			ps.executeUpdate();
			
			ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Duel_RankedTimes (UUID1 VARCHAR(100),UUID2 VARCHAR(100),Week INT(10),Day INT(3), Hour INT(1),LastWeek DATE,LastDay DATE,LastHour DATE,LastWeekMinutes INT(24),LastDayMinutes INT(24),LastHourMinutes INT(24))");
			ps.executeUpdate();
			
			ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Duel_RankedLastFights (UUID VARCHAR(100),Last1 VARCHAR(100),Last2 VARCHAR(100),Last3 VARCHAR(100),Last4 VARCHAR(100),Last5 VARCHAR(100))");
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(MainClass.getInstance(), new Runnable() {
			
			@Override
			public void run()
			{
				update();
			}
		}, 20*2, 20*2);
	}
	
	private static void uploadPlayers()
	{
		while(!playersToUpload.isEmpty())
		{
			try
			{
				PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("INSERT INTO Duel_RankedQueue (UUID,Player) VALUES (?,?)");
				ps.setString(1, playersToUpload.get(0).uuid.toString());
				ps.setString(2, playersToUpload.get(0).toString());
				ps.executeUpdate();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			
			playersToUpload.remove(0);
		}
	}
	
	private static int[] getPlayTimes(RankedUpload ru1, RankedUpload ru2)
	{
		int[] times = new int[3];
		
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT Week,Day,Hour FROM Duel_RankedTimes WHERE (UUID1 = ? AND UUID2 = ?) OR (UUID1 = ? AND UUID2 = ?) LIMIT 1");
			ps.setString(1, ru1.uuid.toString());
			ps.setString(2, ru2.uuid.toString());
			ps.setString(3, ru2.uuid.toString());
			ps.setString(4, ru1.uuid.toString());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				times[0] = rs.getInt(1);
				times[1] = rs.getInt(2);
				times[2] = rs.getInt(3);
				
				return times;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		times[0] = times[1] = times[2] = 0;
		
		return times;
	}
	
	private static ArrayList<UUID> getLastPlayed(RankedUpload ru)
	{
		ArrayList<UUID> uuids = new ArrayList<>();
		
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM Duel_RankedLastFights WHERE UUID = ? LIMIT 1");
			ps.setString(1, ru.uuid.toString());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				for(int i = 2;i<=6;i++)
				{
					String str = rs.getString(i);
					if(str != null)
						uuids.add(UUID.fromString(str));
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return uuids;
	}
	
	private static ArrayList<RankedUpload> getFightable(RankedUpload ru)
	{
		ArrayList<RankedUpload> fightable = new ArrayList<>();
		int[] times;
		ArrayList<UUID> lastPlayed = null;
		
		for(int i = 0;i<playersInQueue.size();i++)
		{
			if(playersInQueue.get(i).uuid.equals(ru.uuid))
				continue;
			
			times = getPlayTimes(ru, playersInQueue.get(i));
			lastPlayed = getLastPlayed(ru);
			
			if(times[0] < MAX_WEEK && times[1] < MAX_DAY && times[2] < MAX_HOUR && !lastPlayed.contains(playersInQueue.get(i).uuid))
				fightable.add(playersInQueue.get(i));
		}
		
		return fightable;
	}
	
	private static void removeFromQueue(RankedUpload ru)
	{
		for(int i = 0;i<playersInQueue.size();i++)
		{
			if(playersInQueue.get(i).uuid.equals(ru.uuid))
			{
				playersInQueue.remove(i);
			}
		}
	}
	
	private static void matchPlayers()
	{
		HashMap<UUID, UUID> forceQueue = ForceQueueCommand.getForceQueued();
		
		for(int i = 0;i<playersInQueue.size() && !ArenaManager.getServers(1).isEmpty();i++)
		{
			UUID force = forceQueue.get(playersInQueue.get(i).uuid);
			
			if(force != null)
			{
				RankedUpload forceUpload = new RankedUpload(force,getELO(force),playersInQueue.get(i).kit,playersInQueue.get(i).map,playersInQueue.get(i).homeServer);
				
				removePlayerFromMySQL(playersInQueue.get(i));
				
				makeDuel(playersInQueue.get(i), forceUpload);
				
				forceQueue.remove(playersInQueue.get(i).uuid);
				playersInQueue.remove(i);
				ForceQueueCommand.remove(force);
				
				continue;
			}
			
			ArrayList<RankedUpload> fightable = getFightable(playersInQueue.get(i));
			
			RankedUpload min = getLowestDifference(playersInQueue.get(i),fightable);
			
			if(min != null)
			{
				removePlayerFromMySQL(min);
				removePlayerFromMySQL(playersInQueue.get(i));
				
				makeDuel(min, playersInQueue.get(i));
				
				RankedUpload ru = playersInQueue.get(i);
				
				removeFromQueue(min);
				removeFromQueue(ru);
				i=0;
			}
		}
	}
	
	private static void loadPlayers()
	{
		playersInQueue.clear();
		
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT Player FROM Duel_RankedQueue");
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				playersInQueue.add(RankedUpload.fromString(rs.getString(1)));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private static RankedUpload getLowestDifference(RankedUpload ru, ArrayList<RankedUpload> fightable)
	{
		if(fightable.size() == 0)
		{
			return null;
		}
		
		int minDif = -1;
		RankedUpload min = null;
		
		for(int i = 0;i<fightable.size();i++)
		{
			int dif = Math.abs(ru.elo-fightable.get(i).elo);
			if(dif > MAX_ELO_DIF)
				continue;
			
			if(dif < minDif || min == null)
			{
				minDif = dif;
				min = fightable.get(i);
			}
		}
		
		return min;
		
	}
	
	private static WaitingSnakeUpload toWaitingSnakeUpload(RankedUpload ru)
	{
		WaitingSnakeUpload wsu = new WaitingSnakeUpload(ru.uuid, null, ru.homeServer, "null", ru.map, "null", "null");
		
		return wsu;
	}
	
	public static boolean isInMySQL(Player p)
	{
		return isInMySQL(p.getUniqueId());
	}
	
	public static boolean isInMySQL(UUID uuid)
	{
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT UUID FROM Duel_RankedQueue WHERE UUID = ?");
			ps.setString(1, uuid.toString());
			
			return ps.executeQuery().first();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	private static void setDatesAndTimes(RankedUpload ru1,RankedUpload ru2,Date week,Date day,Date hour,int weekT,int dayT, int hourT,int weekM,int dayM,int hourM)
	{
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("UPDATE Duel_RankedTimes SET Week = ?,Day = ?,Hour = ?,LastWeek = ?,LastDay = ?,LastHour = ?,LastWeekMinutes = ?,LastDayMinutes = ?,LastHourMinutes = ? WHERE (UUID1 = ? AND UUID2 = ?) OR (UUID1 = ? OR UUID2 = ?)");
			ps.setInt(1, weekT);
			ps.setInt(2, dayT);
			ps.setInt(3, hourT);
			ps.setDate(4, week);
			ps.setDate(5, day);
			ps.setDate(6, hour);
			ps.setString(7, ru1.uuid.toString());
			ps.setInt(8, weekM);
			ps.setInt(9, dayM);
			ps.setInt(10, hourM);
			ps.setString(11, ru2.uuid.toString());
			ps.setString(12, ru2.uuid.toString());
			ps.setString(13, ru1.uuid.toString());
			
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private static void setLastPlayed(RankedUpload ru,ArrayList<UUID> last)
	{
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT UUID FROM Duel_RankedLastFights WHERE UUID = ?");
			ps.setString(1, ru.uuid.toString());
			
			if(ps.executeQuery().first())
			{
				String qry = "UPDATE Duel_RankedLastFights SET ";
				
				for(int i = 0;i<last.size();i++)
				{
					qry += "Last" + (i+1) + " = '" + last.get(i).toString() + "'" + (i+1 == last.size() ? "" : ",");
				}
				
				ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement(qry);
				ps.executeUpdate();
			}
			else
			{
				ps.close();
				
				String qry = "INSERT INTO Duel_RankedLastFights (UUID,";
				
				for(int i = 0;i<last.size();i++)
				{
					qry += "Last" + (i+1) + (i+1==last.size() ? ") VALUES (?," : ",");
				}
				
				for(int i = 0;i<last.size();i++)
				{
					qry += "'" + last.get(i).toString() + "'" + (i+1 == last.size() ? ") " : ",");
				}
				
				ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement(qry);
				ps.setString(1, ru.uuid.toString());
				
				ps.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private static void setLastPlayed(RankedUpload ru1,RankedUpload ru2)
	{
		ArrayList<UUID> lastPlayed = getLastPlayed(ru1);
		
		if(lastPlayed.isEmpty())
		{
			lastPlayed.add(ru2.uuid);
		}
		else
		{
			if(lastPlayed.size() < 5)
				lastPlayed.add(lastPlayed.get(lastPlayed.size()-1));
			
			for(int i = lastPlayed.size()-(lastPlayed.size() < 5 ? 2 : 1);i>0;i--)
			{
				lastPlayed.set(i, lastPlayed.get(i-1));
			}
			
			lastPlayed.set(0, ru2.uuid);
		}	
		
		setLastPlayed(ru1, lastPlayed);
	}
	
	private static int getCurrentDayMinutes()
	{
		java.util.Date date = new java.util.Date();
		
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		
		return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
	}
	
	private static int[] getMinutes(RankedUpload p1,RankedUpload p2)
	{
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT LastWeekMinutes,LastDayMinutes,LastHourMinutes FROM Duel_RankedTimes WHERE (UUID1 = ? AND UUID2 = ?) OR (UUID1 = ? AND UUID2 = ?) LIMIT 1");
			ps.setString(1, p1.uuid.toString());
			ps.setString(2, p2.uuid.toString());
			ps.setString(3, p2.uuid.toString());
			ps.setString(4, p1.uuid.toString());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				int weeks,days,hours;
				
				weeks = rs.getInt(1);
				days = rs.getInt(2);
				hours = rs.getInt(3);
				
				int[] min = new int[3];
				
				min[0] = weeks;
				min[1] = days;
				min[2] = hours;
				
				return min;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static void updateDatesAndLastPlayed(RankedUpload ru1,RankedUpload ru2)
	{
		Date[] dates = getDates(ru1,ru2);
		Date curDate = Date.valueOf(LocalDate.now());
		int[] times = getPlayTimes(ru1, ru2);
		int minutes = getCurrentDayMinutes();
		int[] playMinutes = getMinutes(ru1, ru2);
		
		
		if(dates == null)
		{
			try
			{
				PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("INSERT INTO Duel_RankedTimes (UUID1,UUID2,Week,Day,Hour,LastWeek,LastDay,LastHour,LastWeekMinutes,LastDayMinutes,LastHourMinutes) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
				ps.setString(1, ru1.uuid.toString());
				ps.setString(2, ru2.uuid.toString());
				ps.setInt(3, 1);
				ps.setInt(4, 1);
				ps.setInt(5, 1);
				ps.setDate(6, curDate);
				ps.setDate(7, curDate);
				ps.setDate(8, curDate);
				ps.setInt(9, minutes);
				ps.setInt(10, minutes);
				ps.setInt(11, minutes);
				
				ps.executeUpdate();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			setDatesAndTimes(ru1,ru2,dates[0] == null ? curDate : dates[0],dates[1] == null ? curDate : dates[1],dates[2] == null ? curDate : dates[2],times[0]+1,times[1]+1,times[0]+1,dates[0] == null ? playMinutes[0] : minutes,dates[1] == null ? playMinutes[1] : minutes,dates[2] == null ? playMinutes[2] : minutes);
			setLastPlayed(ru1, ru2);
			setLastPlayed(ru2, ru1);
		}
	}
	
	private static void makeDuel(RankedUpload ru1, RankedUpload ru2)
	{	
		WaitingSnakeUpload wsu1 = toWaitingSnakeUpload(ru1);
		WaitingSnakeUpload wsu2 = toWaitingSnakeUpload(ru2);
		
		ArrayList<WaitingSnakeUpload> wsu11 = new ArrayList<>();
		ArrayList<WaitingSnakeUpload> wsu12 = new ArrayList<>();
		wsu11.add(wsu1);
		wsu12.add(wsu2);
		
		String arenaServer = "";
		Kit kit = null;
		String arena = "";
		
		String[] serverNames = new String[2];
		serverNames[0] = wsu1.serverName;
		serverNames[1] = wsu2.serverName;
		
		switch(new Random().nextInt(2))
		{
		case 0:
			arena = ru1.map;
			break;
		case 1:
			arena = ru2.map;
			break;
		}
		
		kit = KitManager.getPreKit(15 + ru1.kit);
		
		arenaServer = ArenaManager.getServers(1).get(0);
		
		if(DuelManager.sendDuelToSQL(wsu11, wsu12, new Random().nextInt(Integer.MAX_VALUE),-5, null, kit.itemsToString(), serverNames, arenaServer, arena, 1, -1) != 1)
		{
			System.out.println("[Ranked] Error while creating Duel");
			return;
		}
		
		OfflinePlayer op1,op2;
		
		op1 = Bukkit.getOfflinePlayer(ru1.uuid);
		op2 = Bukkit.getOfflinePlayer(ru2.uuid);
		
		if(!ArenaManager.teleportToArena(op1.getName(),arenaServer))
		{
			System.out.println("[Ranked] Error while teleporting " + op1.getName());
			return;
		}
		
		if(!ArenaManager.teleportToArena(op2.getName(), arenaServer))
		{
			System.out.println("[Ranked] Error while teleporting " + op2.getName());
			return;
		}
		
		updateDatesAndLastPlayed(ru1, ru2);
	}
	
	private static void removePlayerFromMySQL(RankedUpload ru)
	{
		removePlayerFromMySQL(ru.uuid);
	}
	
	private static void removePlayerFromMySQL(UUID uuid)
	{
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("DELETE FROM Duel_RankedQueue WHERE UUID = ?");
			ps.setString(1, uuid.toString());
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private static void update()
	{
		uploadPlayers();
		loadPlayers();
		updatePlayTimes();
		matchPlayers();
	}
	
	private static double millisecondsToSeconds(long ms)
	{
		return (double)ms/1000.0;
	}
	
	private static double secondsToMinutes(double s)
	{
		return s / 60.0;
	}
	
	private static double minutesToHours(double m)
	{
		return m / 60.0;
	}
	
	private static double hoursToDays(double h)
	{
		return h / 24.0;
	}
	
	private static double daysToWeeks(double d)
	{
		return d / 7.0;
	}
	
	private static double milliSecondsToMinutes(long ms)
	{
		return secondsToMinutes(millisecondsToSeconds(ms));
	}
	
	private static double millisecondsToDays(long ms)
	{
		return hoursToDays(minutesToHours(milliSecondsToMinutes(ms)));
	}
	
	private static double millisecondsToWeeks(long ms)
	{
		return daysToWeeks(millisecondsToDays(ms));
	}
	
	private static Date[] getDates(RankedUpload p1,RankedUpload p2)
	{
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT LastWeek,LastDay,LastHour FROM Duel_RankedTimes WHERE (UUID1 = ? AND UUID2 = ?) OR (UUID1 = ? AND UUID2 = ?) LIMIT 1");
			ps.setString(1, p1.uuid.toString());
			ps.setString(2, p2.uuid.toString());
			ps.setString(3, p2.uuid.toString());
			ps.setString(4, p1.uuid.toString());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				Date weeks,days,hours;
				
				weeks = rs.getDate(1);
				days = rs.getDate(2);
				hours = rs.getDate(3);
				
				Date[] dates = new Date[3];
				
				dates[0] = weeks;
				dates[1] = days;
				dates[2] = hours;
				
				return dates;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static void resetTimes(RankedUpload p1,RankedUpload p2,boolean weeks,boolean days,boolean hours)
	{
		if(weeks || days || hours)
		{
			try
			{
				String qry = "";
				
				qry = "UPDATE Duel_RankedTimes SET ";
				
				if(weeks)
				{
					qry += "Week = 0,LastWeek = CAST(NULL AS DATE),LastWeekMinutes = CAST(NULL AS INT)" + (days || hours ? "," : " ");
				}
				if(days)
				{
					qry += "Day = 0,LastDay = CAST(NULL AS DATE),LastDayMinutes = CAST(NULL AS INT)" + (hours ? "," : " ");
				}
				if(hours)
				{
					qry += "Hour = 0,LastHour = CAST(NULL AS DATE),LastHourMinutes = CAST(NULL AS INT) ";
				}
				
				qry += "WHERE (UUID1 = ? AND UUID2 = ?) OR (UUID1 = ? AND UUID2 = ?)";
				
				System.out.println("---- Query for reseting ----");
				System.out.println(qry);
				
				PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement(qry);
				ps.setString(1, p1.uuid.toString());
				ps.setString(2, p2.uuid.toString());
				ps.setString(3, p2.uuid.toString());
				ps.setString(4, p1.uuid.toString());
				
				ps.executeUpdate();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	private static int minutesToMilliSeconds(int minutes)
	{
		return minutes * 60 * 1000;
	}
	
	private static void updatePlayTimes()
	{
		RankedUpload p1,p2;
		Date curDate = Date.valueOf(LocalDate.now());
		double weeksDif=0.0,daysDif=0.0,minutesDif=0.0;
		int curMinutes = getCurrentDayMinutes();
		long curMS = curDate.getTime() + minutesToMilliSeconds(curMinutes);
		long weekMS,dayMS,minutesMS;
		
		for(int i = 0;i<playersInQueue.size();i++)
		{
			for(int j = i+1;j<playersInQueue.size();j++)
			{
				minutesDif = daysDif = weeksDif = 0.0;
				
				p1 = playersInQueue.get(i);
				p2 = playersInQueue.get(j);
				
				Date[] dates = getDates(p1, p2);
				int[] minutes = getMinutes(p1,p2);
				
				if(dates != null)
				{
					weekMS = dates[0] == null ? -1 : dates[0].getTime() + minutes[0];
					dayMS = dates[1] == null ? -1 : dates[1].getTime() + minutes[1];
					minutesMS = dates[2] == null ? -1 : dates[2].getTime() + minutes[2];
					
					if(weekMS != -1)
						weeksDif = millisecondsToWeeks(curMS - weekMS);
					if(dayMS != -1)
						daysDif = millisecondsToDays(curMS - dayMS);
					if(minutesMS != -1)
						minutesDif = milliSecondsToMinutes(curMS - minutesMS);
					
					resetTimes(p1, p2, weeksDif > 1.0, daysDif > 1.0, minutesDif > 60.0);
				}
			}
		}
	}
	
	public static int getELO(Player p)
	{
		return getELO(p.getUniqueId());
	}
	
	public static int getELO(UUID uuid)
	{
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT ELO FROM Duel_RankedELO WHERE UUID = ? LIMIT 1");
			ps.setString(1, uuid.toString());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				return rs.getInt(1);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return 100;
	}
	
	public static boolean isInUpload(Player p)
	{
		for(int i = 0;i<playersToUpload.size();i++)
		{
			if(playersToUpload.get(i).uuid.equals(p.getUniqueId()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static void addPlayer(Player p,int kit)
	{
		if(isInUpload(p) || isInMySQL(p))
			return;
		
		String map;
		
		map = MapMenu.getRandomArena(MenuManager.getSettingMenu(p).getMapMenu().getMyMaps(), null);
		
		RankedUpload ru = new RankedUpload(p.getUniqueId(), getELO(p), kit, map, MainClass.getInstance().serverName);
		
		playersToUpload.add(ru);
		
		p.sendMessage(Messages.addToRankedQueue);
		p.playSound(p.getLocation(), Sounds.rankedJoin, Sounds.rankedJoinVolume, Sounds.DEFAULT_PITCH);
	}
	
	public static void removePlayer(Player p)
	{
		for(int i = 0;i<playersToUpload.size();i++)
		{
			if(playersToUpload.get(i).uuid.equals(p.getUniqueId()))
			{
				playersToUpload.remove(i);
				return;
			}
		}
		
		removePlayerFromMySQL(p.getUniqueId());
		
		p.sendMessage(Messages.removeFromRankedQueue);
		p.playSound(p.getLocation(), Sounds.rankedLeave, Sounds.rankedLeaveVolume, Sounds.DEFAULT_PITCH);
	}
	
	public static ArrayList<OfflinePlayer> getTopPlayers()
	{
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM Duel_RankedELO ORDER BY ELO DESC LIMIT 10");
			ResultSet rs = ps.executeQuery();
			
			ArrayList<OfflinePlayer> players = new ArrayList<>();
			
			while(rs.next())
			{
				OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString(1)));
				players.add(op);
			}
			
			return players;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return new ArrayList<>();
	}
}
