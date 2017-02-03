package at.Kingcraft.OnevsOne_lobby.Duels;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;

public class Challenge {
	private ArrayList<UUID> challengers;
	private ArrayList<UUID> challenged;
	public static final int IS_CHALLANGED = 0;
	public static final int IS_CHALLANGER = 1;
	public static final int NO_ID = 3;
	public static final int NO_ROLE = 4;
	public int ID;
	private String[] serverName;
	private int time = -1;
	
	
	public Challenge(ArrayList<Player> challenger1, ArrayList<Player> challenged1, int id,String[] serverName,int time)
	{
		challengers = new ArrayList<UUID>();
		challenged = new ArrayList<UUID>();
		this.serverName = new String[serverName.length];
		
		for(int i = 0;i<serverName.length;i++)
		{
			this.serverName[i] = serverName[i];
		}
		
		
		if(challenger1 != null)
		{
			for(int i=0;i<challenger1.size();i++)
			{
				challengers.add(challenger1.get(i).getUniqueId());
			}
		}
		
		if(challenged1 != null)
		{
			for(int i=0;i<challenged1.size();i++)
			{
				challenged.add(challenged1.get(i).getUniqueId());
			}
		}
		
		this.time = time;
		
		setID(id);
	}
	
	public int getTime()
	{
		return time;
	}
	
	public String getServer(int i)
	{
		return serverName[i];
	}
	
	public void setID(int i)
	{
		ID = i;
	}
	
	public void removePlayer(Player p)
	{
		ArrayList<Player> challengers1 = getChallengers();
		ArrayList<Player> challenged1 = getChallenged();
		for(int i = 0;i<challengers1.size();i++)
		{
			if(challengers1.get(i).getUniqueId().equals(p.getUniqueId()))
			{
				challengers.remove(i);
				
				if(challengers.size() == 0)
				{
					
					ChallangeManager.deleteChallenge(null,null,ID, true,false);
				}
					for(int u = 0;u<challenged1.size();u++)
					{
						if(!challenged1.get(u).getUniqueId().equals(p.getUniqueId()))
							challenged1.get(u).sendMessage(Messages.challengeLeave(p.getDisplayName()));
						ChallangeManager.setupSkull(challenged1.get(u),challengers.size() == 0 ? challenged1.get(u) : challengers1.get(0));
					}
					for(int u = 0;u<challengers1.size();u++)
					{
						if(!challengers1.get(i).getUniqueId().equals(p.getUniqueId()))
							challengers1.get(u).sendMessage(Messages.challengeLeave(p.getDisplayName()));
						ChallangeManager.setupSkull(challengers1.get(u),challengers.size() == 0 ? challengers1.get(u) : challenged1.get(0));
					}
				
				
				
				
				return;
			}
		}
		
		
		for(int i = 0;i<challenged1.size();i++)
		{
			if(challenged1.get(i).getUniqueId().equals(p.getUniqueId()))
			{
				challenged.remove(i);
				
				if(challenged.size() == 0)
				{
					ChallangeManager.deleteChallenge(null,null,ID, true,false);
				}
				
					for(int u = 0;u<challenged1.size();u++)
					{
						if(!challenged1.get(u).getUniqueId().equals(p.getUniqueId()))
							challenged1.get(u).sendMessage(Messages.challengeLeave(p.getDisplayName()));
						ChallangeManager.setupSkull(challenged1.get(u), challengers.size() == 0 ? challenged1.get(u) : challengers1.get(0));
					}
					for(int u = 0;u<challengers1.size();u++)
					{
						if(!challengers1.get(u).getUniqueId().equals(p.getUniqueId()))
							challengers1.get(u).sendMessage(Messages.challengeLeave(p.getDisplayName()));
						ChallangeManager.setupSkull(challengers1.get(u), challengers.size() == 0 ? challengers1.get(u) : challenged1.get(0));
					}
				
				
				
				
				
				return;
			}
		}
		
		ChallangeManager.setupSkull(p, p);
	}

	public void addPlayer(Player p,boolean chers)
	{
		if(chers)
		{
			challengers.add(p.getUniqueId());
		}
		else
		{
			challenged.add(p.getUniqueId());
		}
		
		ArrayList<Player> challenged1 = getChallenged();
		ArrayList<Player> challengers1 = getChallengers();
		
		for(int u = 0;u<challenged1.size();u++)
		{
			if(!challenged1.get(u).getUniqueId().equals(p.getUniqueId()))
				challenged1.get(u).sendMessage(Messages.challengeJoin(p.getDisplayName()));
			ChallangeManager.setupSkull(challenged1.get(u), p);
		}
		for(int u = 0;u<challengers1.size();u++)
		{
			if(!challengers1.get(u).getUniqueId().equals(p.getUniqueId()))
				challengers1.get(u).sendMessage(Messages.challengeJoin(p.getDisplayName()));
			ChallangeManager.setupSkull(challengers1.get(u), p);
		}
	}
	
	private ArrayList<Player> getPlayers(ArrayList<UUID> uis )
	{
		ArrayList<Player> players = new ArrayList<Player>();
		
			for(int i = 0;i<uis.size();i++)
			{
				for(Player p : Bukkit.getServer().getOnlinePlayers())
				{
					if(p.getUniqueId().equals(uis.get(i)))
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
}
