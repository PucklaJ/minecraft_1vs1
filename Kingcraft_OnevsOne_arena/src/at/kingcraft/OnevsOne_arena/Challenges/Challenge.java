package at.kingcraft.OnevsOne_arena.Challenges;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.Kits.Kit;

public class Challenge {
	private ArrayList<UUID> challengers;
	private ArrayList<UUID> challenged;
	public String arenaName;
	public static final int IS_CHALLANGED = 0;
	public static final int IS_CHALLANGER = 1;
	public static final int NO_ID = 3;
	public static final int NO_ROLE = 4;
	public int ID;
	private String[] serverName;
	private int joined1 = 0;
	private int joined2 = 0;
	private Kit kit;
	private int tournament;
	private String arenaServer;
	private int mode;
	private int time;
	
	
	public Challenge(ArrayList<UUID> challenger1, ArrayList<UUID> challenged1,String map, int id,String[] serverName,Kit kit,int tournament,String arenaServer,int mode,int time)
	{
		challengers = new ArrayList<UUID>();
		challenged = new ArrayList<UUID>();
		arenaName = map;
		this.kit = kit;
		this.serverName = new String[serverName.length];
		for(int i = 0;i<serverName.length;i++)
		{
			this.serverName[i] = serverName[i];
		}
		
		for(int i=0;i<challenger1.size();i++)
		{
			challengers.add(challenger1.get(i));
		}
		
		for(int i=0;i<challenged1.size();i++)
		{
			challenged.add(challenged1.get(i));
		}
		
		setID(id);
		
		this.tournament = tournament;
		this.arenaServer = arenaServer;
		this.mode = mode;
		this.time = time;
	}
	
	public int getMode()
	{
		return mode;
	}
	
	public Kit getKit()
	{
		return kit;
	}
	
	public String getArenaServer()
	{
		return arenaServer;
	}
	
	public boolean is1Joined()
	{
		return joined1 == challengers.size();
	}
	
	public boolean is2Joined()
	{
		return joined2 == challenged.size();
	}
	
	public String getPreviousServer(int i)
	{
		return serverName[i];
	}
	
	public boolean isTournament()
	{
		return tournament > -1 ? true : false;
	}
	
	public int getTournamentID()
	{
		return tournament;
	}
	
	public void addJoin(int i)
	{
		if(i == 1)
		{
			joined1++;
		}
		else if(i == 2)
		{
			joined2++;
		}
	}
	
	public void setID(int i)
	{
		ID = i;
	}
	
	private ArrayList<Player> getPlayers(ArrayList<UUID> uis)
	{
		ArrayList<Player> players = new ArrayList<Player>();
		
		for(Player p : Bukkit.getServer().getOnlinePlayers())
		{
			for(int i = 0;i<uis.size();i++)
			{
				if(p.getUniqueId().toString().equals(uis.get(i).toString()))
				{
					players.add(p);
				}
			}
		}
		
		return players;
	}
	
	public ArrayList<Player> getChallenged()
	{
		return getPlayers(challenged);
		
	}
	
	public ArrayList<UUID> getChallengersUUID()
	{
		return challengers;
	}
	
	public ArrayList<UUID> getChallengedUUID()
	{
		return challenged;
	}
	
	public ArrayList<Player> getChallengers()
	{
		return getPlayers(challengers);
	}
	
	public int getTime()
	{
		return time;
	}
}
