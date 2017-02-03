package at.Kingcraft.OnevsOne_lobby.Special;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import at.Kingcraft.OnevsOne_lobby.Kits.Kit;
import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;

public class KitViewerMenu extends Menu
{
	private static final int KIT1_POS = 11;
	private UUID uuid = null;

	public KitViewerMenu(Player owner)
	{
		super(owner, 27, "Kits eines anderen Spielers", null);
	}
	
	private void loadKits()
	{
		if(uuid != null)
		{
			ArrayList<Kit> kits = KitManager.getKits(uuid);
			
			if(!kits.isEmpty())
			{
				for(int i = KIT1_POS;i<KIT1_POS+5;i++)
				{
					inventory.setItem(i, kits.get(i-KIT1_POS).getSymbol(true));
				}
			}
			else
			{
				close();
			}
		}
		else
		{
			close();
		}
		
	}
	
	public void setPlayer(UUID u)
	{
		uuid = u;
	}
	
	@Override
	protected void setInventoryContents()
	{
		ItemStack wall = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)15);
		{
			ItemMeta im = wall.getItemMeta();
			im.setDisplayName(" ");
			wall.setItemMeta(im);
		}
		
		setInventory(wall);
	}

	@Override
	public void onClick(int slot, ClickType ct)
	{
		if(slot >= KIT1_POS && slot <= KIT1_POS+4 && ct.isLeftClick())
		{
			String kitName = ChatColor.stripColor(inventory.getItem(slot).getItemMeta().getDisplayName());
			close();
			OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
			
			owner.performCommand("kit " + p.getName() + ":" + kitName);
		}
	}
	
	@Override
	public void open()
	{
		super.open();
		
		loadKits();
	}

}
