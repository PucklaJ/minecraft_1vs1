package at.Kingcraft.OnevsOne_lobby.Tournaments;

import java.util.ArrayList;

public class SpecTournament
{
	private ArrayList<TourPlayer> contestants;
	private ArrayList<SpecRound> rounds;
	private ArrayList<TourPlayer> losers;
	private ArrayList<String> servers;
	private TourPlayer roundSkipper;
	private int maxRoundLevel;
	private ArrayList<String> arenas;
	
	public SpecTournament()
	{
		setContestants(new ArrayList<>());
		setRounds(new ArrayList<>());
		setLosers(new ArrayList<>());
		setServers(new ArrayList<>());
		setArenas(new ArrayList<>());
	}

	public ArrayList<SpecRound> getRounds() {
		return rounds;
	}

	public void setRounds(ArrayList<SpecRound> rounds) {
		this.rounds = rounds;
	}

	public ArrayList<TourPlayer> getLosers() {
		return losers;
	}

	public void setLosers(ArrayList<TourPlayer> losers) {
		this.losers = losers;
	}

	public ArrayList<String> getServers() {
		return servers;
	}

	public void setServers(ArrayList<String> servers) {
		this.servers = servers;
	}

	public ArrayList<TourPlayer> getContestants() {
		return contestants;
	}

	public void setContestants(ArrayList<TourPlayer> contestants) {
		this.contestants = contestants;
	}

	public TourPlayer getRoundSkipper() {
		return roundSkipper;
	}

	public void setRoundSkipper(TourPlayer roundSkipper) {
		this.roundSkipper = roundSkipper;
	}

	public void setServers(String[] servers2)
	{
		ArrayList<String> newServers = new ArrayList<>();
		
		for(int i = 0;i<servers2.length;i++)
		{
			newServers.add(servers2[i]);
		}
		
		this.servers = newServers;
		
	}

	public int getMaxRoundLevel() {
		return maxRoundLevel;
	}

	public void setMaxRoundLevel(int maxRoundLevel) {
		this.maxRoundLevel = maxRoundLevel;
	}

	public ArrayList<String> getArenas() {
		return arenas;
	}

	public void setArenas(ArrayList<String> arenas) {
		this.arenas = arenas;
	}

	public void setArenas(String[] arenas2) {
		ArrayList<String> newServers = new ArrayList<>();
		
		for(int i = 0;i<arenas2.length;i++)
		{
			newServers.add(arenas2[i]);
		}
		
		this.arenas = newServers;
		
	}
}
