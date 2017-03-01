package at.kingcraft.OnevsOne_arena.Duels;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import at.kingcraft.OnevsOne_arena.Challenges.Challenge;

public class DuelManager {

	private static JavaPlugin plugin;
	
	private static ArrayList<Duel> duels = new ArrayList<Duel>();
	
	public static Duel getFirstDuel()
	{	
		if(duels.size() == 0)
			return null;
		
		return duels.get(0);
	}
	
	public static ArrayList<Duel> getDuels()
	{
		return duels;
	}
	
	public static Duel newDuel(Challenge c)
	{
		Duel d = getDuel(c.ID);
		if(d == null)
		{
			System.out.println("[DuelManager] Creating new Duel " + c.ID);
			
			duels.add(new Duel(c, plugin,c.getMode()));
			return duels.get(duels.size()-1);
		}
		
		return d;
		
		
	}
	
	public static Duel getDuel(Player p)
	{
		for(int i = 0;i<duels.size();i++)
		{
			if(duels.get(i).isPartOf(p))
			{
				return duels.get(i);
			}
		}
		
		return null;
	}
	
	public static Duel getDuel(int id)
	{
		for(int i = 0;i<duels.size();i++)
		{
			if(duels.get(i).id == id)
			{
				return duels.get(i);
			}
		}
		
		return null;
	}
	
	public static ArrayList<Player> deleteDuel(Player p)
	{
		Duel d = getDuel(p);
		if(d != null)
		{
			System.out.println("[DuelManager] delete Duel of " + (p == null ? "null" : p.getDisplayName()));
			
			d.endDuel(p,true);
			
			ArrayList<Player> rv = new ArrayList<Player>();
			
			Challenge c = d.getChallenge();
			
			ArrayList<Player> challengers = c.getChallengers();
			ArrayList<Player> challenged = c.getChallenged();
			
			for(int i = 0;i<challengers.size();i++)
			{
					rv.add(challengers.get(i));
			}
			
			for(int i = 0;i<challenged.size();i++)
			{
					rv.add(challenged.get(i));
			}
			
			duels.remove(d);
			return rv;
		}
		
		return null;
		
		
	}
	
	public static void deleteDuel()
	{
		if(!duels.isEmpty())
		{
			duels.remove(0);
		}
	}
	
	public static void pushSpectatorsBack()
	{	
		Duel d = getFirstDuel();
		if(d == null || d.getSpectators().isEmpty())
			return;
		
		ArrayList<Player> duelPlayers = new ArrayList<>();
		
		duelPlayers.addAll(d.getP1());
		duelPlayers.addAll(d.getP2());
		
		double minDistance = 5.0;
		double pushStrength = 1.0;
		
		
		for(int i = 0;i<duelPlayers.size();i++)
		{
			for(int j = 0;j < d.getSpectators().size();j++)
			{
				if(d.getSpectators().get(j).player.hasPermission("no_push_back") || duelPlayers.get(i).getUniqueId().equals(d.getSpectators().get(j).player.getUniqueId()))
					continue;
				
				if(duelPlayers.get(i).getLocation().distance(d.getSpectators().get(j).player.getLocation()) < minDistance)
				{
					// Push Back
					
					Vector dir = duelPlayers.get(i).getLocation().toVector().subtract(d.getSpectators().get(j).player.getLocation().toVector());
					
					dir = dir.normalize();
					
					dir.multiply(-pushStrength);
					
					d.getSpectators().get(j).player.setVelocity(dir);
				}
			}
		}
	}
	
	public static void deleteDuel(int id)
	{
		for(int i = 0;i<duels.size();i++)
		{
			if(duels.get(i).id == id)
			{
				duels.remove(i);
				return;
			}
		}
	}
	
	public static void setup(JavaPlugin mainClass) {
		plugin = mainClass;
		
	}
	
}
