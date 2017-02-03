package at.Kingcraft.OnevsOne_lobby.Duels;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import at.Kingcraft.OnevsOne_lobby.Scoreboard.MyScoreboardManager;
import at.Kingcraft.OnevsOne_lobby.Special.MenuManager;
import at.Kingcraft.OnevsOne_lobby.Special.TeamMenu;

public class TeamManager {
	private static ArrayList<Team> teams;
	private static MainClass plugin;
	private static ArrayList<Team> checkingTeams;
	private static ArrayList<UUID> checkPlayers;
	
	public static void setup(MainClass plugin)
	{
		teams = new ArrayList<Team>();
		checkingTeams = new ArrayList<>();
		checkPlayers = new ArrayList<>();
		TeamManager.plugin = plugin;
	}
	
	public static Team newTeam(Player p)
	{
		teams.add(new Team(p,getID(),plugin));
		
		return teams.get(teams.size()-1);
	}
	
	private static int getID()
	{
		Random r = new Random();
		
		int id = r.nextInt(Integer.MAX_VALUE);
		
		for(int i = 0;i<teams.size();i++)
		{
			if(teams.get(i).getID() == id)
			{
				id = r.nextInt(Integer.MAX_VALUE);
				i=-1;
			}
		}
		
		return id;
	}
	
	public static Team newTeam(Player p1,Player p2)
	{
		teams.add(new Team(p1,getID(),plugin));
		teams.get(teams.size()-1).addPlayer(p2,true);
		
		return teams.get(teams.size()-1);
	}
	
	public static boolean getsChecked(Player p)
	{
		return checkPlayers.contains(p.getUniqueId());
	}
	
	public static Team getTeam(Player p)
	{
		for(int i = 0;i<teams.size();i++)
		{
			ArrayList<Player> players = teams.get(i).getPlayers();
			for(int u = 0;u<players.size();u++)
			{
				if(players.get(u).getUniqueId().equals(p.getUniqueId()))
				{
					return teams.get(i);
				}
			}
		}
		
		return null;
	}
	
	public static void checkTeam(Player p,Team t)
	{
		if(t != null)
		{
			if(checkPlayers.contains(p.getUniqueId()))
			{
				return;
			}
			else
			{
				checkPlayers.add(p.getUniqueId());
			}
			if(checkingTeams.contains(t))
			{
				return;
			}
			checkingTeams.add(t);
			
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				
				@Override
				public void run()
				{
					ArrayList<Player> players = t.getPlayers();
					ArrayList<Player> onlinePlayers = new ArrayList<Player>();
					Player onlineLeader = null;
					
					Player p1 = reloadPlayer(t.getLeader());
					
					if(p1 != null)
					{
						onlineLeader = p1;
					}
					
					for(int i = 0;i<players.size();i++)
					{
						Player p = reloadPlayer(players.get(i));
						if(p != null)
						{
							onlinePlayers.add(p);
						}
					}
					
					if(onlineLeader == null)
					{
						if(!onlinePlayers.isEmpty())
						{
							onlineLeader = onlinePlayers.get(0);
						}
					}
					if(onlineLeader != null)
					{	
						TeamManager.deleteTeam(t, false,false);
						
						if(onlinePlayers.size() > 1)
						{
							Team t1 = TeamManager.newTeam(onlineLeader);
							for(int i = 0;i<onlinePlayers.size();i++)
							{
								t1.addPlayer(onlinePlayers.get(i),false);
								MyScoreboardManager.updateScoreboard(onlinePlayers.get(i));
							}
							for(int i = 0;i<t1.getPlayers().size();i++)
							{
								checkPlayers.remove(t1.getPlayers().get(i).getUniqueId());
							}
							
							
						}
						else
						{
							for(int i = 0;i<onlinePlayers.size();i++)
							{
								onlinePlayers.get(i).sendMessage(Messages.teamDeleteAmount);
								MyScoreboardManager.updateScoreboard(onlinePlayers.get(i));
								checkPlayers.remove(onlinePlayers.get(i).getUniqueId());
							}
							onlineLeader.getInventory().setItem(4, new ItemStack(Material.AIR));
						}
					}
					
					checkingTeams.remove(t);
					
					
				}
			}, 20*5);
		}
	}
	
	private static Player reloadPlayer(Player p)
	{
		for(Player p1 : Bukkit.getServer().getOnlinePlayers())
		{
			if(p1.getUniqueId().equals(p.getUniqueId()))
			{
				return p1;
			}
		}
		
		return null;
	}
	
	public static void deleteTeam(Team t,boolean message,boolean scrBoard)
	{
		ArrayList<Player> ps = t.getPlayers();
		for(int i = 0;i<ps.size();i++)
		{
			if(message)
				ps.get(i).sendMessage(Messages.teamDelete);
			
			if(MenuManager.getSettingMenu(ps.get(i)) == null)
			{
				continue;
			}
			
			TeamMenu tm = MenuManager.getSettingMenu(ps.get(i)).getTeamMenu();
			if(tm != null && tm.isOpen())
			{
				tm.close();
			}
		}
		teams.remove(t);
		
		if(scrBoard)
		{
			for(int i = 0;i<ps.size();i++)
				MyScoreboardManager.updateScoreboard(ps.get(i));
		}
		
	}
}
