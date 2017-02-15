package at.kingcraft.OnevsOne_arena.Scoreboard;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import at.kingcraft.OnevsOne_arena.MainClass;
import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_arena.Duels.DuelManager;
import at.kingcraft.OnevsOne_setup.Maps.Map;
import net.md_5.bungee.api.ChatColor;

public class MyScoreboardManager
{
	private static ScoreboardManager sBoardMgr;
	private static final int MAX_LENGTH = 40;
	private static final String lineBreak = "%lb%";
	private static Scoreboard sb = null;
	//private static Scoreboard buffer = null;
	//private static boolean whichScoreboard = true; // true == sb, false == buffer
	
	public static void setup()
	{
		sBoardMgr = Bukkit.getScoreboardManager();
	}
	
	private static void addLine(ArrayList<String> lines ,Objective obj, String line)
	{
		line = line.substring(0,line.length() > MAX_LENGTH ? MAX_LENGTH : line.length());
		
		if(!line.equals(lineBreak))
		{
			int spaces = -1;
			boolean isThere = false;
			do
			{
				isThere = false;
				
				for(int i = 0;i<=spaces;i++)
				{
					line += " ";
				}
				
				for(int i = 0;i<lines.size();i++)
				{
					if(lines.get(i).equals(line))
					{
						isThere = true;
						break;
					}
				}
				spaces++;
			}while(isThere);
		}
		
		
		lines.add(line);
		
		if(!line.equals(lineBreak))
			obj.getScore(line).setScore(0);
	}
	
	private static String generateLineBreak(int maxLength)
	{
		FileConfiguration config = MainClass.getInstance().getConfig();
		
		String str = config.getString("Scoreboard.Linebreak.Prefix");
		for(int i = 0;i<maxLength;i++)
		{
			str += config.getString("Scoreboard.Linebreak.String");
		}
		
		return str;
	}
	
	public static Scoreboard getScoreboard()
	{
		return sb;
	}
	
	public static void resetScoreboard()
	{
		sb = null;
		
		for(Player p : Bukkit.getOnlinePlayers())
		{
			p.setScoreboard(sBoardMgr.getNewScoreboard());
		}
	}
	
	@SuppressWarnings("deprecation")
	private static Scoreboard createScoreboard(Scoreboard board)
	{
		Duel d = DuelManager.getFirstDuel();
		if(d==null)
			return null;
		
		if(board == null)
		board = sBoardMgr.getNewScoreboard();
		
		ArrayList<String> lines = new ArrayList<>();
		
		FileConfiguration config = MainClass.getInstance().getConfig();
		Objective obj;
		if(board.getObjective(DisplaySlot.SIDEBAR) != null)
		{
			obj = board.getObjective(DisplaySlot.SIDEBAR);
			obj.unregister();
		}
		
		obj = board.registerNewObjective("Header", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(config.getString("Scoreboard.Header"));
		
		addLine(lines,obj,lineBreak);
		
		
		String min = "" + d.getTimeMin();
		if(min.length() < 2)
			min = "0"+min;
		String sec = d.getTimeSec()+"";
		if(sec.length()<2)
			sec="0"+sec;
		
		addLine(lines,obj,MainClass.getInstance().getConfig().getString("Scoreboard.Timer.Prefix") + min + ":" + sec + MainClass.getInstance().getConfig().getString("Scoreboard.Timer.Suffix"));
		
		Map map = d.getMap();
		
		addLine(lines,obj,MainClass.getInstance().getConfig().getString("Scoreboard.Map.Prefix") + (map == null ? "null" :  ChatColor.translateAlternateColorCodes('&',map.getName())) + MainClass.getInstance().getConfig().getString("Scoreboard.Map.Suffix"));
		int maxLength = 0;
		for(int i = 0;i<lines.size();i++)
		{
			if(lines.get(i).length() > maxLength)
				maxLength = lines.get(i).length();
			if(!lines.get(i).equals(lineBreak))
				obj.getScore(lines.get(i)).setScore(lines.size()-i-1);
		}
		String lb = generateLineBreak(maxLength-2);
		
		int timesLb = 0;
		for(int i = 0;i<lines.size();i++)
		{
			if(lines.get(i).equals(lineBreak))
			{
				String str = lb;
				for(int u = 0;u<timesLb;u++)
				{
					str += " ";
				}
				
				obj.getScore(str).setScore(lines.size()-i-1);
				
				timesLb++;
			}
		}
		
		// Teams
		Team t1 = board.getTeam("t1");
		if(t1 == null)
			t1 = board.registerNewTeam("t1");
		for(int i = 0;i<d.getP1().size();i++)
		{
			boolean in = false;
			for(OfflinePlayer op : t1.getPlayers())
			{
				if(op.getUniqueId().equals(d.getP1().get(i).getUniqueId()))
					in = true;
			}
			if(!in)
				t1.addPlayer(d.getP1().get(i).getPlayer());
		}
		
		t1.setPrefix(MainClass.getInstance().getConfig().getString("Teams.Team1.Prefix"));
		t1.setSuffix(MainClass.getInstance().getConfig().getString("Teams.Team1.Suffix"));
		
		Team t2 = board.getTeam("t2");
		if(t2 == null)
			t2 = board.registerNewTeam("t2");
		for(int i = 0;i<d.getP2().size();i++)
		{
			boolean in = false;
			for(OfflinePlayer op : t2.getPlayers())
			{
				if(op.getUniqueId().equals(d.getP2().get(i).getUniqueId()))
					in = true;
			}
			if(!in)
				t2.addPlayer(d.getP2().get(i).getPlayer());
		}
		
		t2.setPrefix(MainClass.getInstance().getConfig().getString("Teams.Team2.Prefix"));
		t2.setSuffix(MainClass.getInstance().getConfig().getString("Teams.Team2.Suffix"));
		
		return board;
	}
	
	public static void updateScoreboard()
	{
			sb = createScoreboard(sb);
			
			if(sb != null)
			{
				for(Player p : Bukkit.getOnlinePlayers())
				{
					if(p.getHealth()!=0.0)
						p.setScoreboard(sb);
				}
			}
	}
}
