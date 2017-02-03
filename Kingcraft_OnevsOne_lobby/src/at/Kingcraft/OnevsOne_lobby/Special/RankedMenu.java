package at.Kingcraft.OnevsOne_lobby.Special;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import at.Kingcraft.OnevsOne_lobby.Kits.Kit;
import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;
import at.Kingcraft.OnevsOne_lobby.WaitingSnake.RankedQueue;
import net.md_5.bungee.api.ChatColor;

public class RankedMenu extends Menu
{
	private static final int KIT1_POS = 11;
	private static final int KIT2_POS = 13;
	private static final int KIT3_POS = 15;
	private boolean inRanked = false; 
	
	public RankedMenu(Player owner)
	{
		super(owner, 27, "Ranked", null);
	}
	
	@Override
	protected void setInventoryContents()
	{
		inRanked = true;
		
		toogleLeaveItem();
	}
	
	private void toogleLeaveItem()
	{
		inRanked = !inRanked;
		
		if(!inRanked)
		{
			for(int i = 0;i<inventory.getSize();i++)
			{
				inventory.setItem(i, new ItemStack(Material.AIR));
			}
			
			Kit kit1 = KitManager.getPreKit(15);
			Kit kit2 = KitManager.getPreKit(16);
			Kit kit3 = KitManager.getPreKit(17);
			
			inventory.setItem(KIT1_POS, kit1.getSymbol(true));
			inventory.setItem(KIT2_POS, kit2.getSymbol(true));
			inventory.setItem(KIT3_POS, kit3.getSymbol(true));
		}
		else
		{
			ItemStack leave = new ItemStack(Material.INK_SACK,1,(short)1);
			ItemMeta im = leave.getItemMeta();
			im.setDisplayName(ChatColor.RED + "Warteschlange verlassen");
			leave.setItemMeta(im);
			
			for(int i = 0;i<inventory.getSize();i++)
			{
				inventory.setItem(i, leave);
			}
		}
	}

	@Override
	public void onClick(int slot, ClickType ct)
	{
		int kit = -1;
		
		if(ct.isLeftClick())
		{
			if(!inRanked)
			{
				switch(slot)
				{
				case KIT1_POS:
					kit = 0;
					break;
				case KIT2_POS:
					kit = 1;
					break;
				case KIT3_POS:
					kit = 2;
					break;
				}
				
				if(kit != -1)
				{
					RankedQueue.addPlayer(owner, kit);
					toogleLeaveItem();
					close();
					return;
				}
			}
			else
			{
				RankedQueue.removePlayer(owner);
				toogleLeaveItem();
				close();
			}
		}	
	}
}
