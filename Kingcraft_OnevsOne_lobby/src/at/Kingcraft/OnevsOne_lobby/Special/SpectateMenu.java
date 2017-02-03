package at.Kingcraft.OnevsOne_lobby.Special;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import at.Kingcraft.OnevsOne_lobby.Arenas.ArenaManager;
import at.Kingcraft.OnevsOne_lobby.Duels.SpectateDuel;
import at.Kingcraft.OnevsOne_lobby.Duels.SpectateManager;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;

public class SpectateMenu extends Menu
{
	public static ArrayList<SpectateDuel> spectateDuels = new ArrayList<>();
	
	public SpectateMenu(Player owner)
	{
		super(owner, 54, "Spectate-Menu", null);
	}
	
	@Override
	protected void setInventoryContents()
	{
		update();
	}

	@Override
	public void onClick(int slot, ClickType ct)
	{
		if(spectateDuels.size() >= slot+1)
		{
			if(ArenaManager.getPrefix(spectateDuels.get(slot).getServer()).equals(ArenaManager.getPrefix()))
			{
				owner.sendMessage(Messages.youAreGettingTeleported);
				spectateDuels.get(slot).teleportPlayer(owner);
			}
		}
	}
	
	@Override
	public void update()
	{
		int i;
		ArrayList<SpectateDuel> duels = SpectateManager.getSpecDuels();
		
		for(i = 0;i<duels.size() && i < 54;i++)
		{
			ItemStack is = inventory.getItem(i);
			ItemStack newI = duels.get(i).toItemStack(); 
			
			if(is==null|| is.getType().equals(Material.AIR) ||is.getItemMeta()==null|| !is.getItemMeta().getDisplayName().equals(newI.getItemMeta().getDisplayName()))
				inventory.setItem(i, duels.get(i).toItemStack());
		}
		
		for(;i<54;i++)
		{
			inventory.setItem(i, new ItemStack(Material.AIR));
		}
	}

}
