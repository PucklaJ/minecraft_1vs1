package at.Kingcraft.OnevsOne_lobby.Scoreboard;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Duels.Team;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamManager;
import at.Kingcraft.OnevsOne_lobby.Kits.Kit;
import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;
import at.Kingcraft.OnevsOne_lobby.Lobby.OnlinePlayers;
import at.Kingcraft.OnevsOne_lobby.Tournaments.Tournament;
import at.Kingcraft.OnevsOne_lobby.Tournaments.TournamentManager;
import at.Kingcraft.OnevsOne_lobby.WaitingSnake.RankedQueue;

public class MyScoreboardManager
{
	private static ScoreboardManager sBoardMgr;
	private static final int MAX_LENGTH = 40;
	private static final String lineBreak = "%lb%";
	
	public static void setup()
	{
		sBoardMgr = Bukkit.getScoreboardManager();
		updateScoreboards();
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
	
	public static void updateScoreboard(Player p)
	{
		Scoreboard sb = sBoardMgr.getNewScoreboard();
		
		ArrayList<String> lines = new ArrayList<>();
		
		FileConfiguration config = MainClass.getInstance().getConfig();
		
		Objective obj = sb.registerNewObjective("Header", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(config.getString("Scoreboard.Header"));
		addLine(lines,obj,lineBreak);
		
		//otherName = KitManager.getChoosenKit(p).otherPlayer != null ? KitManager.getChoosenKit(p).otherPlayer.getDisplayName() + ":" : KitManager.getChoosenKit(p).otherOPlayer != null ? KitManager.getChoosenKit(p).otherOPlayer.getName() + ":" : "";
		Kit kit = KitManager.getChoosenKitKit(p);
		if(kit == null)
			return;
		
		addLine(lines,obj,(kit.isDif() ? config.getString("Scoreboard.Kit.Prefix.Different") : (config.getString("Scoreboard.Kit.Prefix.Normal"))) + kit.getName(true,!(kit.getOwnerName().equals("Server") || kit.getOwnerName().equals("Verschieden")),false) + (kit.isDif() ? config.getString("Scoreboard.Kit.Suffix.Different") : config.getString("Scoreboard.Kit.Suffix.Normal")));
		addLine(lines,obj,lineBreak);
		// ELO
		addLine(lines,obj,config.getString("Scoreboard.ELO.Prefix") + RankedQueue.getELO(p) + config.getString("Scoreboard.ELO.Suffix"));
		
		
		Team t = TeamManager.getTeam(p);
		
		if(t != null)
		{
			addLine(lines,obj,lineBreak);
			
			addLine(lines,obj,config.getString("Scoreboard.Team.Title"));
			
			for(int i = 0;i<t.getPlayers().size();i++)
			{
				if(t.getPlayers().get(i).getUniqueId().equals(t.getLeader().getUniqueId()))
				{
					addLine(lines,obj,config.getString("Scoreboard.Team.Prefix.Leader") + t.getPlayers().get(i).getDisplayName() + config.getString("Scoreboard.Team.Suffix.Leader"));
				}
				else
				{
					addLine(lines,obj,config.getString("Scoreboard.Team.Prefix.Normal") + t.getPlayers().get(i).getDisplayName() + config.getString("Scoreboard.Team.Suffix.Normal"));
				}
			}
		}
		
		ArrayList<Tournament> tournaments = TournamentManager.getTournaments();
		
		if(!tournaments.isEmpty())
		{
			addLine(lines,obj,lineBreak);
			addLine(lines,obj,config.getString("Scoreboard.Tournament.Title"));
			String team;
			
			int max = config.getInt("Scoreboard.Tournament.Max");
			
			for(int i = 0;i<tournaments.size() && i < max;i++)
			{
				team = tournaments.get(i).getTeamSize() + "vs" + tournaments.get(i).getTeamSize();
				
				team = " "+team;
				
				addLine(lines,obj,config.getString("Scoreboard.Tournament.Text").replaceAll("%leader%", tournaments.get(i).getLeader().getDisplayName()).replaceAll("%size%", tournaments.get(i).getTeamSize() + "").replaceAll("%players%", tournaments.get(i).getContestants().size() + ""));
			}
			
			if(tournaments.size() > config.getInt("Scoreboard.Tournament.Max"))
			{
				addLine(lines,obj,config.getString("Scoreboard.Tournament.MaxReached"));
			}
		}
		
		addLine(lines,obj,lineBreak);
		addLine(lines,obj,config.getString("Scoreboard.Player.Prefix") + OnlinePlayers.getNumOnline() + config.getString("Scoreboard.Player.Suffix"));
		
		
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
		
		p.setScoreboard(sb);
	}
	
	private static void updateScoreboards()
	{
		Bukkit.getScheduler().runTaskTimerAsynchronously(MainClass.getInstance(), new Runnable() {
			
			@Override
			public void run()
			{
				for(Player p : Bukkit.getOnlinePlayers())
				{
					p.setScoreboard(p.getScoreboard());
				}
			}
				
		}, 20, 20);
	}
		
}
