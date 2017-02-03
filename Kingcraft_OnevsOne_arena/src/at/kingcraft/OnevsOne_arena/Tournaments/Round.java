package at.kingcraft.OnevsOne_arena.Tournaments;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.MainClass;
import at.kingcraft.OnevsOne_arena.Challenges.ChallangeManager;
import at.kingcraft.OnevsOne_arena.Duels.SpectateManager;
import at.kingcraft.OnevsOne_arena.Kits.Kit;
import at.kingcraft.OnevsOne_arena.Listener.DuelListener;
import at.kingcraft.OnevsOne_arena.Messaging.Messenger;


public class Round
{
	private ArrayList<TourPlayer> p1;
	private ArrayList<TourPlayer> p2;
	private String server;
	private String arena;
	private boolean hasStarted = false;
	private int tourID;
	private int roundLevel;
	private String homeServer;
	private Kit kit;
	private String loser;
	private int mode;
	private int time;
	private int qualiRoundLevel;
	
	public Round(ArrayList<TourPlayer> p1,ArrayList<TourPlayer> p2,String server,String arena,int tourID,int roundLevel,MainClass plugin,String homeServer,Kit kit,boolean allRound,int mode,int time,int qualiRoundLevel)
	{
		this.p1 = p1;
		this.p2 = p2;
		
		this.server = server;
		this.arena = arena;
		this.tourID = tourID;
		this.roundLevel = roundLevel;
		this.homeServer = homeServer;
		this.kit = kit;
		this.mode = mode;
		this.time = time;
		this.qualiRoundLevel = qualiRoundLevel;
		loser = "NO_LOSER";
	}
	
	public int getQualiRoundLevel()
	{
		return qualiRoundLevel;
	}
	
	public void setLoser(String loser)
	{
		this.loser = loser;
	}
	
	public String getLoser()
	{
		return loser;
	}
	
	public ArrayList<TourPlayer> getP1()
	{
		return p1;
	}
	
	public ArrayList<TourPlayer> getP2()
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
	
	public String getServer()
	{
		return server;
	}
	
	public void start()
	{
		teleportPlayers();
		
		hasStarted = true;
	}
	
	private Player getPlayer(UUID uuid)
	{
		for(Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if(p.getUniqueId().equals(uuid))
			{
				return p;
			}
		}
		
		return null;
	}
	
	private void teleportPlayers()
	{
		String[] serverNames = new String[p1.size() + p2.size()];
		for(int i = 0;i<serverNames.length;i++)
		{
			serverNames[i] = homeServer;
		}
		
		ArrayList<UUID> chersUUID = new ArrayList<>(),chedUUID = new ArrayList<>();
		ArrayList<String> chersName = new ArrayList<>(),chedName = new ArrayList<>();
		
		for(int i = 0;i<p1.size();i++)
		{
			chersUUID.add(p1.get(i).uuid);
			chersName.add(p1.get(i).name);
		}
		
		for(int i = 0;i<p2.size();i++)
		{
			chedUUID.add(p2.get(i).uuid);
			chedName.add(p2.get(i).name);
		}
		
		int id = ChallangeManager.getChallengeID();
		ChallangeManager.sendChallengeToMySQL(chersUUID, chedUUID, chersName, serverNames, chedName,id, arena, server, kit.itemsToString(),tourID,mode,time);
		
		for(int i = 0;i<p1.size();i++)
		{
			Player p = server.equals(MainClass.getInstance().serverName) ? getPlayer(p1.get(i).uuid) : null;
			
			if(p == null)
			{
				String[] args = new String[2];
				args[1] = server;
				args[0] = p1.get(i).name;
				Messenger.sendMessage(null, "BungeeCord", "ConnectOther", args);
			}
			else
			{
				DuelListener.onSpawn(p,true,false);	
			}
		}
		for(int i = 0;i<p2.size();i++)
		{
			Player p = server.equals(MainClass.getInstance().serverName) ? getPlayer(p2.get(i).uuid) : null;
			
			if(p == null)
			{
				String[] args = new String[2];
				args[1] = server;
				args[0] = p2.get(i).name;
				Messenger.sendMessage(null, "BungeeCord", "ConnectOther", args);
			}
			else
			{
				DuelListener.onSpawn(p,true,false);	
			}
		}
		
		ArrayList<UUID> p11 = new ArrayList<>();
		for(int i = 0;i<p1.size();i++)
		{
			p11.add(p1.get(i).uuid);
		}
		ArrayList<UUID> p21 = new ArrayList<>();
		for(int i = 0;i<p2.size();i++)
		{
			p21.add(p2.get(i).uuid);
		}
		SpectateManager.uploadToMySQL(p11, p21, MainClass.getInstance().serverName,id,true);
		
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
			round += p1.get(i).uuid.toString() + (i+1 == p1.size() ? "" : ";");
		}
		round += "|";
		for(int i = 0;i<p2.size();i++)
		{
			round += p2.get(i).uuid.toString() + (i+1 == p2.size() ? "" : ";");
		}
		
		round += "#";
		
		round += server + "#";
		
		round += arena + "#";
		
		round += String.valueOf(tourID) + "#";
		
		round += String.valueOf(roundLevel) + "#";
		
		round += String.valueOf(qualiRoundLevel) + "#";
		
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
	
	private static TourPlayer get(UUID u)
	{
		String name = "null";
		OfflinePlayer op = Bukkit.getOfflinePlayer(u);
		if(op != null)
		{
			name = op.getName();
		}
		
		return new TourPlayer(name,u);
	}
	
	public static Round fromString(String str,MainClass plugin,String homeServer,Kit kit,boolean allRound,int mode,int time)
	{
		ArrayList<TourPlayer> p1 = new ArrayList<>();
		ArrayList<TourPlayer> p2 = new ArrayList<>();
		String playerStr = "";
		
		int i;
		for(i = 0;i<str.length() && str.charAt(i) != '#';i++)
		{
			playerStr += str.charAt(i);
		}
		
		
		String[] playersArray = TournamentManager.splitString('|', playerStr);
		for(int j = 0;j<playersArray.length;j++)
		{
			String[] players1 = playersArray[j].split(";");
			for(int k = 0;k<players1.length;k++)
			{
				if(j==0)
				{
					p1.add(get(UUID.fromString(players1[k])));
				}
				else
				{
					p2.add(get(UUID.fromString(players1[k])));
				}
			}
		}
		
		String server = "",arena = "",started = "",tourID = "",roundLevel = "",loser = "",qualiRound="";
		
		for(i++;i<str.length() && str.charAt(i) != '#';i++)
		{
			server += str.charAt(i);
		}
		for(i++;i<str.length() && str.charAt(i) != '#';i++)
		{
			arena += str.charAt(i);
		}
		for(i++;i<str.length() && str.charAt(i) != '#';i++)
		{
			tourID += str.charAt(i);
		}
		for(i++;i<str.length() && str.charAt(i) != '#';i++)
		{
			roundLevel += str.charAt(i);
		}
		for(i++;i<str.length() && str.charAt(i) != '#';i++)
		{
			qualiRound += str.charAt(i);
		}
		for(i++;i<str.length() && (allRound ? (str.charAt(i) != '|') : true);i++)
		{
			started += str.charAt(i);
		}
		if(allRound)
		{
			for(i++;i<str.length();i++)
			{
				loser += str.charAt(i);
			}
		}
		
		
		Round round = new Round(p1,p2,server,arena,Integer.valueOf(tourID),Integer.valueOf(roundLevel),plugin,homeServer,kit,allRound,mode,time,Integer.valueOf(qualiRound));
		round.setHasStarted(started.equals("1") ? true : false);
		if(allRound)round.setLoser(loser);
		
		return round;
	}
}
