package at.Kingcraft.OnevsOne_lobby.Tournaments;

import java.util.ArrayList;
import java.util.Random;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Arenas.ArenaManager;
import at.Kingcraft.OnevsOne_lobby.Duels.Challenge;
import at.Kingcraft.OnevsOne_lobby.Duels.DuelManager;
import at.Kingcraft.OnevsOne_lobby.Duels.SpectateManager;
import at.Kingcraft.OnevsOne_lobby.Kits.Kit;
import at.Kingcraft.OnevsOne_lobby.Lobby.LobbyListener;
import at.Kingcraft.OnevsOne_lobby.Special.MenuManager;
import net.md_5.bungee.api.ChatColor;

public class Round
{
	private ArrayList<Player> p1;
	private ArrayList<Player> p2;
	private String server;
	private String arena;
	private boolean hasStarted = false;
	private int tourID;
	private int roundLevel;
	private Kit kit;
	private int mode;
	
	public Round(ArrayList<Player> p1,ArrayList<Player> p2,String server,String arena,int tourID,int roundLevel,Kit kit,int mode)
	{
		this.p1 = p1;
		this.p2 = p2;
		
		this.server = server;
		this.arena = arena;
		this.tourID = tourID;
		this.roundLevel = roundLevel;
		this.kit = kit;
		this.mode = mode;
	}
	
	public ArrayList<Player> getP1()
	{
		return p1;
	}
	
	public ArrayList<Player> getP2()
	{
		return p2;
	}
	
	public void setServer(String server)
	{
		this.server = server;
	}
	
	public String getArena()
	{
		return arena;
	}
	
	public void start()
	{
		String[] serverNames = new String[p1.size()+p2.size()];
		for(int i = 0;i<serverNames.length;i++)
			serverNames[i] = MainClass.getInstance().serverName;
		
		int id = new Random().nextInt(Integer.MAX_VALUE);
		
		Tournament t = TournamentManager.getTournament(tourID);
		
		if(t==null)
		{
			System.out.println("Tournament = null Error");
			return;
		}
		
		Challenge c = new Challenge(p1,p2,id,serverNames,MenuManager.getSettingMenu(t.getLeader()).getTourSettingMenu().getTime());
		
		if(DuelManager.sendDuelToSQL(c,kit, server,arena,false,tourID,mode) != 1)
		{
			for(int i = 0;i<p1.size();i++)
			{
				p1.get(i).sendMessage(ChatColor.RED + "MySQL Error");
			}
			for(int i = 0;i<p2.size();i++)
			{
				p2.get(i).sendMessage(ChatColor.RED + "MySQL Error");
			}
			return;
		}
		
		for(int i = 0;i<p1.size();i++)
		{
			if(!ArenaManager.teleportToArena(p1.get(i), server, false))
			{
				p1.get(i).sendMessage(ChatColor.RED + "Teleportation Error!");
				return;
			}
			
			LobbyListener.leftByTour.add(p1.get(i).getUniqueId());
		}
		
		for(int i = 0;i<p2.size();i++)
		{
			if(!ArenaManager.teleportToArena(p2.get(i), server, false))
			{
				p2.get(i).sendMessage(ChatColor.RED + "Teleportation Error!");
				return;
			}
			
			LobbyListener.leftByTour.add(p2.get(i).getUniqueId());
		}
		
		SpectateManager.uploadToMySQL(p1, p2, server,c.ID);
		
		hasStarted = true;
	}
	
	public void setHasStarted(boolean b)
	{
		hasStarted = b;
	}
	
	public boolean hasStarted()
	{
		return hasStarted;
	}
	
	@Override
	public String toString()
	{
		String round = "";
		
		for(int i = 0;i<p1.size();i++)
		{
			round += p1.get(i).getUniqueId().toString() + (i+1 == p1.size() ? "" : ";");
		}
		
		round += "|";
		
		for(int i = 0;i<p2.size();i++)
		{
			round += p2.get(i).getUniqueId().toString() + (i+1 == p2.size() ? "" : ";");
		}
		
		round += "#";
		
		round += server + "#";
		
		round += arena + "#";
		
		round += String.valueOf(tourID) + "#";
		
		round += String.valueOf(roundLevel) + "#";
		
		round += "1#";
		
		round += hasStarted ? "1" : "0";
		
		return round;
	}
	
	public void setRoundLevel(int i)
	{
		roundLevel = i;
	}
	
	public int getRoundLevel()
	{
		return roundLevel;
	}
}
