package at.kingcraft.OnevsOne_arena.Tournaments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.MainClass;
import at.kingcraft.OnevsOne_arena.Commands.GiveUpCommand;
import at.kingcraft.OnevsOne_arena.Kits.Kit;
import at.kingcraft.OnevsOne_arena.Listener.DuelListener;

public class Tournament
{
	private ArrayList<ArrayList<TourPlayer>> contestants;
	private ArrayList<Round> rounds;
	private TourPlayer leader;
	private boolean hasStarted = false;
	private int id;
	private int maxRoundLevel = -1;
	private ArrayList<String> arenas;
	private ArrayList<String> servers;
	private MainClass plugin;
	private String homeServer;
	private Kit kit;
	private HashMap<Integer,ArrayList<TourPlayer>> roundSkipperKoRounds;
	private HashMap<Integer,ArrayList<TourPlayer>> roundSkipperQualiRounds;
	private ArrayList<ArrayList<TourPlayer>> loser;
	private ArrayList<Round> allRounds;
	private int mode;
	private int kitMode;
	private int time;
	private int maxQualiRounds;
	private int curQualiRound;
	private HashMap<String,Integer> qualiPoints;
	
	public static final int NO_ARENAS = 0;
	public static final int NOT_ENOUGH_PLAYERS = 1;
	public static final int UNKNOWN_ERROR = 2;
	public static final int HAS_STARTED = 3;
	public static final int PLAYER_SIZE = 2;
	public static final int NO_SERVERS = 4;
	public static final int LAST_PLAYER = 5;
	private static final int NO_PLAYERS = 6;
	
	public Tournament(Player leader,int id,MainClass plugin,String homeServer,Kit kit,int mode,int kitMode,int time)
	{
		this.leader = leader != null ? new TourPlayer(leader.getName(),leader.getUniqueId()) : null;
		contestants = new ArrayList<>();
		this.id = id;
		rounds = new ArrayList<>();
		arenas = new ArrayList<>();
		servers = new ArrayList<>();
		loser = new ArrayList<>();
		roundSkipperKoRounds = new HashMap<>();
		roundSkipperQualiRounds = new HashMap<>();
		setAllRounds(new ArrayList<>());
		this.plugin = plugin;
		this.homeServer = homeServer;
		this.kit = kit;
		this.mode = mode;
		this.kitMode = kitMode;
		this.time = time;
		maxQualiRounds = 3;
		qualiPoints = new HashMap<>();
	}
	
	public int getKitMode()
	{
		return kitMode;
	}
	
	public int getCurentQualiRound()
	{
		return curQualiRound;
	}
	
	public ArrayList<String> getServers()
	{
		return servers;
	}
	
	public void setMaxRoundLevel(int i)
	{
		maxRoundLevel = i;
	}
	
	public int getID()
	{
		return id;
	}
	
	
	public String contestantsToString()
	{
		String cont = "";
		
		if(contestants.isEmpty())
		{
			return "NO_PLAYERS";
		}
		
		for(int i = 0;i<contestants.size();i++)
		{
			String cont1 = "";
			for(int j = 0;j<contestants.get(i).size();j++)
			{
				cont1 += contestants.get(i).get(j).uuid.toString() + (j+1 == contestants.get(i).size() ? "" : ";");
			}
			cont += cont1 + (i+1 == contestants.size() ? "" : "|");
		}
		
		return cont;
	}
	
	public String contestantNamesToString()
	{
		String cont = "";
		
		for(int i = 0;i<contestants.size();i++)
		{
			String cont1 = "";
			
			for(int j = 0;j<contestants.get(i).size();j++)
			{
				cont1 += contestants.get(i).get(j).name + (j+1 == contestants.get(i).size() ? "" : ";");
			}
			
			cont += cont1 + (i+1 == contestants.size() ? "" : "|");
		}
		
		return cont;
	}
	
	public void addServer(String server)
	{
		for(int i = 0;i<servers.size();i++)
		{
			if(servers.get(i).equals(server))
			{
				return;
			}
		}
		
		servers.add(server);
	}
	
	public void removeServer(String server)
	{
		for(int i = 0;i<servers.size();i++)
		{
			if(servers.get(i).equals(server))
			{
				servers.remove(i);
				GiveUpCommand.sendBackToFreeServers(server);
				return;
			}
		}
	}
	
	public void addArena(String arena)
	{
		for(int i = 0;i<arenas.size();i++)
		{
			if(arenas.get(i).equals(arena))
			{
				return;
			}
		}
		
		arenas.add(arena);
	}
	
	public String serversToString()
	{
		String str = "";
		
		for(int i = 0;i<servers.size();i++)
		{
			str += servers.get(i) + (i+1 == servers.size() ? "" : "|");
		}
		
		if(str.equals(""))
		{
			str = "NO_SERVERS";
		}
		
		return str;
	}
	
	
	public String roundsToString()
	{
		if(rounds.isEmpty())
		{
			return "NO_ROUNDS";
		}
		
		String roundsStr = "";
		
		for(int i = 0;i<rounds.size();i++)
		{
			roundsStr += rounds.get(i).toString() + "\n";
		}
		
		return roundsStr;
	}
	
	public String roundSkipperToString()
	{
		String str = "";
		
		Iterator<HashMap.Entry<Integer,ArrayList<TourPlayer>>> it1 = roundSkipperKoRounds.entrySet().iterator();
		Iterator<HashMap.Entry<Integer,ArrayList<TourPlayer>>> it2 = roundSkipperQualiRounds.entrySet().iterator();
		
		while(it2.hasNext())
		{
			HashMap.Entry<Integer,ArrayList<TourPlayer>> pair = it2.next();
			if(pair.getKey() == null || pair.getValue() == null)
				continue;
			
			String str1 = "";
			for(int i = 0;i<pair.getValue().size();i++)
			{
				str1 += pair.getValue().get(i).uuid + (i+1==pair.getValue().size() ? "" : ";");
			}
			String str2 = "";
			for(int i = 0;i<pair.getValue().size();i++)
			{
				str2 += pair.getValue().get(i).name + (i+1==pair.getValue().size() ? "" : ";");
			}
			
			str += "1" + "|" + pair.getKey() + "|" + str1 + "|" + str2 + "}";
		}
		while(it1.hasNext())
		{
			HashMap.Entry<Integer,ArrayList<TourPlayer>> pair = it1.next();
			if(pair.getKey() == null || pair.getValue() == null)
				continue;
			
			String str1 = "";
			for(int i = 0;i<pair.getValue().size();i++)
			{
				str1 += pair.getValue().get(i).uuid + (i+1==pair.getValue().size() ? "" : ";");
			}
			String str2 = "";
			for(int i = 0;i<pair.getValue().size();i++)
			{
				str2 += pair.getValue().get(i).name + (i+1==pair.getValue().size() ? "" : ";");
			}
			
			str += "0" + "|" + pair.getKey() + "|" + str1 + "|" + str2 + "}";
		}
		
		if(str.equals(""))
		{
			str = "NO_ROUND_SKIPPER";
		}
		
		return str;
	}
	
	private boolean is(ArrayList<TourPlayer> t1, ArrayList<TourPlayer> t2)
	{
		for(int i = 0;i<t1.size() && i<t2.size();i++)
		{
			if(!t1.get(i).uuid.equals(t2.get(i).uuid))
				return false;
		}
		
		return true;
	}
	
	public void lose(ArrayList<TourPlayer> tp)
	{
		for(int i = 0;i<loser.size();i++)
		{
			if(is(loser.get(i),tp))
			{
				return;
			}
		}
		
		loser.add(tp);
	}
	
	public String loserToString()
	{
		String str = "";
		
		for(int i = 0;i<loser.size();i++)
		{
			String str1 = "";
			String str2 = "";
			
			for(int j = 0;j<loser.get(i).size();j++)
			{
				str1 += loser.get(i).get(j).uuid.toString() + (j+1==loser.get(i).size() ? "" : ";");
				str2 += loser.get(i).get(j).name + (j+1==loser.get(i).size() ? "" : ";");
			}
			
			str += str1 + "|" + str2 + "\n";
		}
		
		if(str.equals(""))
		{
			str = "NO_LOSER";
		}
		
		return str;
	}
	
	
	
	public ArrayList<ArrayList<TourPlayer>> getLosers()
	{
		return loser;
	}
	
	private boolean isInRound(ArrayList<ArrayList<TourPlayer>> isInRound,ArrayList<TourPlayer> p)
	{
		for(int i = 0;i<isInRound.size();i++)
		{
			if(is(isInRound.get(i),p))
				return true;
		}
		return false;
	}
	
	private ArrayList<UUID> convert(ArrayList<TourPlayer> t)
	{
		ArrayList<UUID> uuids = new ArrayList<>();
		
		for(int i = 0;i<t.size();i++)
		{
			uuids.add(t.get(i).uuid);
		}
		
		return uuids;
	}
	
	public int createNewRounds(int level)
	{
		rounds.clear();
		
		boolean isQuali = isQualiRound();
		
		if(isQuali)
		{
			if(curQualiRound == maxQualiRounds)
			{
				curQualiRound = -1;
				level = 0;
			}
			else
			{
				curQualiRound++;
			}
		}
		
		ArrayList<TourPlayer> roundS = isQuali ? roundSkipperQualiRounds.get(curQualiRound == -1 ? maxQualiRounds : (curQualiRound-1)) : roundSkipperKoRounds.get(level-1);
		ArrayList<TourPlayer> prevRoundSkipper = roundS == null ? null : roundS;
		
		if(isQualiRound())
		{
			roundSkipperQualiRounds.put(curQualiRound, null);
		}
		else
		{
			roundSkipperKoRounds.put(level, null);
		}
		
		
		if(!isQuali)
		{	
			for(int i = 0;i<loser.size();i++)
			{
				removeContestant(loser.get(i));
			}
		}
		
		loser.clear();
		
		for(int i = 0;i<contestants.size();i++)
		{
			for(int j = 0;j<contestants.get(i).size();j++)
			{
				if(TournamentManager.isSpectator(contestants.get(i).get(j)))
				{
					TournamentManager.removeSpectator(contestants.get(i).get(j));
				}
				
				DuelListener.onSpawnCalled.remove(contestants.get(i).get(j).uuid);
			}
			
		}
		
		if(contestants.size() == 1)
		{
			return LAST_PLAYER;
		}
		else if(contestants.isEmpty())
		{
			// Delete Tournament from MySQL
			TournamentManager.deleteTournament(this, false);
			// Send Server back To Free
			for(int i = 0;i<servers.size();i++)
				GiveUpCommand.sendBackToFreeServers(servers.get(i));
			
			return NO_PLAYERS;
		}
		
		
		if(contestants.size() % 2 != 0)
		{
			do
			{
					int roundSkipperIndex = 0;
					roundSkipperIndex = new Random().nextInt(contestants.size());
					if(isQualiRound())
					{
						roundSkipperQualiRounds.put(curQualiRound, contestants.get(roundSkipperIndex));
					}
					else
					{
						roundSkipperKoRounds.put(level, contestants.get(roundSkipperIndex));
					}
					
				
			}while(prevRoundSkipper != null && is(isQualiRound() ? roundSkipperQualiRounds.get(curQualiRound) : roundSkipperKoRounds.get(level),prevRoundSkipper));
		}
		
		ArrayList<TourPlayer> curRoundSkippers = (isQualiRound() ? roundSkipperQualiRounds.get(curQualiRound) : roundSkipperKoRounds.get(level));
		
		if(curRoundSkippers != null)
		{
			ArrayList<TourPlayer> temp = contestants.get(contestants.size()-1);
			removeContestant(contestants.get(contestants.size()-1));
			removeContestant(curRoundSkippers);
			addContenstant(temp,false);
			addContenstant(curRoundSkippers,false);
			
			for(int i = 0;i<curRoundSkippers.size();i++)
			{
				if(!TournamentManager.isSpectator(curRoundSkippers.get(i)))
				{
					TournamentManager.uploadSpectator(new Spectator(curRoundSkippers.get(i).name,curRoundSkippers.get(i).uuid,id,homeServer));
				}
			}
			
		}
		
		try
		{
			ArrayList<ArrayList<TourPlayer>> isInRound = new ArrayList<>();
			
			int u = 0;
			for(int i = 0;i<(curRoundSkippers != null ? (contestants.size()-1) : contestants.size());i++)
			{
				if(i+1  > (curRoundSkippers != null ? (contestants.size()-1) : contestants.size())-1)
				{
					break;
				}
				
				if(isInRound(isInRound,contestants.get(i)))
					continue;
				
				int enemyIndex = i+1;
				int start = enemyIndex;
				while(is(contestants.get(i),contestants.get(enemyIndex)) || isInRound(isInRound,contestants.get(enemyIndex)))
				{	
					if(++enemyIndex > (curRoundSkippers != null ? (contestants.size()-1) : contestants.size())-1)
					{
						enemyIndex = 0;
					}
					
					if(enemyIndex == start)
						break;
				}
				int minPointsDif = Math.abs(getQualiPoints(convert(contestants.get(enemyIndex))) - getQualiPoints(convert(contestants.get(i))));
				
				
				
				for(int j = enemyIndex;j<(curRoundSkippers != null ? (contestants.size()-1) : contestants.size());j++)
				{
					int dif = Math.abs(getQualiPoints(convert(contestants.get(j))) - getQualiPoints(convert(contestants.get(i))));
					
					if(!isInRound(isInRound, contestants.get(j)) && dif  < minPointsDif)
					{
						minPointsDif = dif;
						enemyIndex = j;
					}
				}
				
				isInRound.add(contestants.get(i));
				isInRound.add(contestants.get(enemyIndex));
				Round r = new Round(contestants.get(i),contestants.get(enemyIndex),servers.get(u),getRandomArena(),id,level,plugin,homeServer,kit,false,mode,time,curQualiRound);
				
				addRound(r);
				u++;
			}
			
		}
		catch(IndexOutOfBoundsException e)
		{
			e.printStackTrace();
			return NO_SERVERS;
		}
		
		
		return -1;
	}
	
	public void addRound(Round r)
	{
		if(!arenas.contains(r.getArena()))
		{
			arenas.add(r.getArena());
		}
		addServer(r.getServer());
		
		
		
		rounds.add(r);
		
		for(int i = 0;i<allRounds.size();i++)
		{
			if(allRounds.get(i).getQualiRoundLevel() == r.getRoundLevel() && allRounds.get(i).getRoundLevel() == r.getRoundLevel() && (is(allRounds.get(i).getP1(),r.getP1()) && is(allRounds.get(i).getP2(),r.getP2())))
			{
				return;
			}
		}
		
		allRounds.add(r);
	}
	
	private String getRandomArena()
	{
		Random rand = new Random();
		
		int index = rand.nextInt(arenas.size());
		
		return arenas.get(index);
	}
	
	public ArrayList<Round> getRounds()
	{
		return rounds;
	}
	
	public int getRoundLevel()
	{
		if(rounds.isEmpty())
			return -1;
		
		return rounds.get(0).getRoundLevel();
	}
	
	public Round getRound(Player p)
	{
		if(p == null)
			return null;
		
		for(int i = 0;i<rounds.size();i++)
		{
			Round r = rounds.get(i);
			
			for(int j = 0;j<r.getP1().size();j++)
			{
				if(r.getP1().get(j).uuid.equals(p.getUniqueId()))
				{
					return r;
				}
			}
			
			for(int j = 0;j<r.getP2().size();j++)
			{
				if(r.getP2().get(j).uuid.equals(p.getUniqueId()))
				{
					return r;
				}
			}
		}
		
		return null;
	}
	
	public void addContenstant(ArrayList<TourPlayer> p,boolean message)
	{
		
		if(p == null)
		{
			return;
		}
		
		for(int i = 0;i<contestants.size();i++)
		{
			if(is(contestants.get(i),p))
			{
				return;
			}
		}
		
		if(leader == null)
		{
			leader = p.get(0);
		}
		
		contestants.add(p);
		
	}
	
	public void removePlayer(TourPlayer p)
	{	
		for(int i = 0;i<contestants.size();i++)
		{
			for(int j = 0;j<contestants.get(i).size();j++)
			{
				if(contestants.get(i).get(j).uuid.equals(p.uuid))
				{
					contestants.get(i).remove(j);
					if(contestants.get(i).isEmpty())
					{
						contestants.remove(i);
					}
					return;
				}
			}
		}	
	}
	
	public void removeContestant(ArrayList<TourPlayer> p)
	{	
		for(int i = 0;i<contestants.size();i++)
		{
			if(is(contestants.get(i),p))
			{
				contestants.remove(i);
				return;
			}
		}	
	}
	
	public void setRoundSkipper(ArrayList<TourPlayer> tp,int level,boolean quali)
	{
		if(quali)
		{
			roundSkipperQualiRounds.put(level, tp);
		}
		else
		{
			roundSkipperKoRounds.put(level, tp);
		}
		
	}
	
	public void setRoundSkipper(HashMap<Integer,ArrayList<TourPlayer>> roundS,boolean quali)
	{
		if(quali)
		{
			roundSkipperQualiRounds = roundS;
		}
		else
		{
			roundSkipperKoRounds = roundS;
		}
		
	}
	
	public void removePlayer(UUID p)
	{
		
		for(int i = 0;i<contestants.size();i++)
		{
			for(int j = 0;j<contestants.get(i).size();j++)
			{
				if(contestants.get(i).get(j).uuid.equals(p))
				{
					contestants.get(i).remove(j);
					if(contestants.get(i).isEmpty())
					{
						contestants.remove(i);
					}
					return;
				}
			}
		}
	}
	
	public void removeContestants(ArrayList<UUID> p)
	{
		for(int i = 0;i<contestants.size();i++)
		{
			boolean is = true;
			for(int j = 0;j<contestants.get(i).size() && j<p.size();j++)
			{
				if(!contestants.get(i).get(j).uuid.equals(p.get(j)))
				{
					is = false;
					break;
				}
			}
			if(is)
			{
				contestants.remove(i);
				return;
			}
		}
	}
	
	private String loserString(ArrayList<TourPlayer> loser)
	{
		String str = "";
		
		for(int i = 0;i<loser.size();i++)
		{
			str += loser.get(i).name + (i+1 == loser.size() ? "" : ";");
		}
		
		return str;
	}
	
	public void setAllRoundLoser(Round r,ArrayList<TourPlayer> loser)
	{
		r.setLoser(loserString(loser));
		
		Round aR = null;
		for(int i = 0;i<allRounds.size();i++)
		{
			if(is(allRounds.get(i).getP1(),r.getP1()) && is(allRounds.get(i).getP2(),r.getP2()))
			{
				aR = allRounds.get(i);
				aR.setLoser(loserString(loser));
			}
		}
		
		if(aR == null)
			allRounds.add(r);
	}
	
	public String allRoundsToString()
	{
		if(allRounds.isEmpty())
		{
			return "NO_ROUNDS";
		}
		
		String roundsStr = "";
		
		for(int i = 0;i<allRounds.size();i++)
		{
			roundsStr += allRounds.get(i).toString() + "|" + allRounds.get(i).getLoser() + "\n";
		}
		
		return roundsStr;
	}
	
	private int calculateMaxRoundLevel(int players)
	{
	    int start = 2;
	    int times = 1;
	    int amount = 0;

	    if(players == 2)
	    {
	        return 0;
	    }

	    while(true)
	    {
	        for(int i = 1;i<=times;i++)
	        {
	            if(players == start)
	                return amount;

	            start++;
	        }

	        times*=2;

	        amount++;
	    }
	}
	
	public int getMaxRoundLevel()
	{
		if(maxRoundLevel == -1)
		{
			maxRoundLevel = calculateMaxRoundLevel(contestants.size());
		}
		
		return maxRoundLevel;
	}
	
	public boolean hasStarted()
	{
		return hasStarted;
	}
	
	
	public int start()
	{
		for(int i = 0;i<rounds.size();i++)
		{
			rounds.get(i).start();
		}
		
		hasStarted = true;
		
		return -1;
	}
	
	public TourPlayer getLeader()
	{
		return leader;
	}
	
	public void removeRound(Round r)
	{
		rounds.remove(r);
	}
	
	public ArrayList<ArrayList<TourPlayer>> getContestants()
	{
		return contestants;
	}

	public ArrayList<Round> getAllRounds() {
		return allRounds;
	}

	public void setAllRounds(ArrayList<Round> allRounds) {
		this.allRounds = allRounds;
	}
	
	public ArrayList<TourPlayer> getRoundSkipper(int level,boolean quali)
	{
		if(quali)
		{
			return roundSkipperQualiRounds.get(level);
		}
		else
		{
			return roundSkipperKoRounds.get(level);
		}
		
	}
	
	public int getMaxQualiRounds()
	{
		return maxQualiRounds;
	}
	
	public void decodeQualiRounds(String str)
	{
		String[] qualis = str.split("#");
		
		curQualiRound = Integer.valueOf(qualis[0]);
		
		for(int i = 1;i<qualis.length;i++)
		{
			String[] points = qualis[i].split(":");
			
			qualiPoints.put(points[0], Integer.valueOf(points[1]));
		}
	}
	
	public String getQualiPoints()
	{
		String str = "";
		
		str += curQualiRound + "#";
		
		for(HashMap.Entry<String,Integer> it : qualiPoints.entrySet())
		{
			str += it.getKey() + ":" + it.getValue().toString() + "#";
		}
		
		return str.substring(0, str.length()-1);
	}
	
	public void addQualiPoints(ArrayList<UUID> u,int i)
	{
		String str = "";
		for(int j = 0;j<u.size();j++)
		{
			str += u.get(j).toString() + (j+1==u.size() ? "" : ";");
		}
		
		int pre = qualiPoints.get(str) == null ? 0 : qualiPoints.get(str);
		
		qualiPoints.put(str, pre + i);
	}
	
	public int getQualiPoints(ArrayList<UUID> u)
	{
		String str = "";
		for(int j = 0;j<u.size();j++)
		{
			str += u.get(j).toString() + (j+1==u.size() ? "" : ";");
		}
		
		return qualiPoints.get(str) == null ? 0 : qualiPoints.get(str);
	}
	
	public boolean isQualiRound()
	{
		return curQualiRound != -1;
	}
	
	public HashMap<Integer,ArrayList<TourPlayer>> getRoundSkipper(boolean quali)
	{
		return quali ? roundSkipperQualiRounds : roundSkipperKoRounds;
	}
	
	public boolean isPartOf(Player p)
	{
		for(int i = 0;i<contestants.size();i++)
		{
			for(int j = 0;j<contestants.get(i).size();j++)
			{
				if(contestants.get(i).get(j).uuid.equals(p.getUniqueId()))
				{
					return true;
				}
			}
			
		}
		
		return false;
	}
	
	public Round getRound(String server)
	{
		for(int i = 0;i<rounds.size();i++)
		{
			if(rounds.get(i).getServer().equals(server))
			{
				return rounds.get(i);
			}
		}
		
		return null;
	}
	
	public Round getAllRound(String server)
	{
		for(int i =0;i<allRounds.size();i++)
		{
			if(allRounds.get(i).getServer().equals(server))
			{
				return allRounds.get(i);
			}
		}
		
		return null;
	}
	
}
