package at.Kingcraft.OnevsOne_lobby.Special;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import at.Kingcraft.OnevsOne_lobby.WaitingSnake.RankedQueue;
import net.md_5.bungee.api.ChatColor;

public class TopMenu extends Menu
{
	
	public TopMenu(Player owner)
	{
		super(owner, 27, "Top-Spieler", null);
	}
	
	@Override
	protected void setInventoryContents()
	{
		ArrayList<OfflinePlayer> players = RankedQueue.getTopPlayers();
		
		for(int i = 0;i<10;i++)
		{
			int slot = -1;
			
			if(i == 0)
				slot = 4;
			else
				slot = i+17;
			
			ItemStack is = new ItemStack(Material.SKULL_ITEM,1,(short)3);
			SkullMeta im = (SkullMeta) is.getItemMeta();
			ArrayList<String> lore = new ArrayList<>();
			
			if(i<players.size() && players.get(i).getName() != null && !players.get(i).getName().equals("null"))
			{
				im.setOwner(players.get(i).getName());
				im.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + players.get(i).getName());
				lore.add(ChatColor.YELLOW + "ELO: " + ChatColor.BLUE + RankedQueue.getELO(players.get(i).getUniqueId()));
			}
			else
			{
				im.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE +  "Spieler");
				lore.add(ChatColor.YELLOW + "ELO: " + ChatColor.BLUE + 100);
			}
			
			im.setLore(lore);
			is.setItemMeta(im);
			
			inventory.setItem(slot, is);
		}
	}

	@Override
	public void onClick(int slot, ClickType ct)
	{
		if(ct.isLeftClick())
		{
			ItemStack is = inventory.getItem(slot);
			if(is != null && is.getType().equals(Material.SKULL_ITEM))
			{
				SkullMeta im = (SkullMeta) is.getItemMeta();
				if(im.getOwner() != null && !im.getOwner().equals("null"))
					owner.performCommand("stats " + im.getOwner());
			}
		}
	}

}
