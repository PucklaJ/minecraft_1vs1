package at.Kingcraft.OnevsOne_lobby.Tournaments;

import java.util.UUID;

import org.bukkit.Bukkit;


public class SpecRound
{
	private TourPlayer[] players;
	private int roundLevel;
	private boolean hasStarted;
	private String server;
	private String arena;
	private int tourID;
	
	public SpecRound()
	{
		setPlayers(new TourPlayer[2]);
		hasStarted = false;
	}
	
	public boolean hasStarted()
	{
		return hasStarted;
	}
	
	public static SpecRound fromString(String str)
	{
		TourPlayer[] players;
		String playerStr = "";
		
		int i;
		for(i = 0;i<str.length() && str.charAt(i) != '#';i++)
		{
			playerStr += str.charAt(i);
		}
		
		
		String[] playersArray = TournamentManager.splitString('|', playerStr);
		
		players = new TourPlayer[playersArray.length];
		
		for(int u = 0;u<playersArray.length;u++)
		{
			players[u] = new TourPlayer("null",UUID.fromString(playersArray[u]));
			players[u].name = Bukkit.getOfflinePlayer(players[u].uuid).getName();
		}
		
		String server = "",arena = "",started = "",tourID = "",roundLevel = "";
		
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
		for(i++;i<str.length();i++)
		{
			started += str.charAt(i);
		}
		
		SpecRound round = new SpecRound();
		round.setHasStarted(started.equals("1") ? true : false);
		round.setServer(server);
		round.setArena(arena);
		round.setTourID(Integer.valueOf(tourID));
		round.setRoundLevel(Integer.valueOf(roundLevel));
		round.setPlayers(players);
		
		return round;
	}

	private void setHasStarted(boolean b)
	{
		hasStarted = b;
	}

	public String getArena() {
		return arena;
	}

	public void setArena(String arena) {
		this.arena = arena;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getRoundLevel() {
		return roundLevel;
	}

	public void setRoundLevel(int roundLevel) {
		this.roundLevel = roundLevel;
	}

	public int getTourID() {
		return tourID;
	}

	public void setTourID(int tourID) {
		this.tourID = tourID;
	}

	public TourPlayer[] getPlayers() {
		return players;
	}

	public void setPlayers(TourPlayer[] players) {
		this.players = players;
	}
	
}
