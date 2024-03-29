package at.kingcraft.OnevsOne_arena.Menus;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_arena.Duels.DuelManager;

public class MenuManager
{
	private static HashMap<UUID,DuelsMenu> duelsMenus;
	
	public static void setup()
	{
		duelsMenus = new HashMap<>();
	}
	
	public static void setDuelsMenu(Player p,DuelsMenu menu)
	{
		duelsMenus.put(p.getUniqueId(), menu);
	}
	
	public static DuelsMenu getDuelsMenu(Player p)
	{
		if(duelsMenus.get(p.getUniqueId()) == null)
		{
			Duel d = DuelManager.getDuel(p);
			if(d!=null)
			{
				duelsMenus.put(p.getUniqueId(), new DuelsMenu(p, d.getTournamentID(),false));
			}
		}
			
		return duelsMenus.get(p.getUniqueId());
	}
	
	public static void deleteDuelsMenu(Player p)
	{
		duelsMenus.remove(p.getUniqueId());
	}
}
