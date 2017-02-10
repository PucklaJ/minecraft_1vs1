package at.Kingcraft.OnevsOne_lobby.Special;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import at.Kingcraft.OnevsOne_lobby.Items.Items;
import at.Kingcraft.OnevsOne_lobby.Kits.Kit;
import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;
import at.Kingcraft.OnevsOne_lobby.Lobby.LobbyListener;

public class KitPreMenu extends Menu
{
	private static final int KIT1_POS = 10;
	private static final int KIT2_POS = KIT1_POS+9;
	private static final int SOUP_KIT_POS = 16;
	private static final int RANKED_KIT1_POS = 37;
	private static final int RANKED_KIT2_POS = RANKED_KIT1_POS+2;
	private static final int RANKED_KIT3_POS = RANKED_KIT2_POS+2;
	
	public KitPreMenu(Player owner,KitMainMenu parent)
	{
		super(owner,54,"Vorgegebene Kits",parent);
	}
	
	private void loadNormKits()
	{
		ArrayList<Kit> kits = KitManager.getPreKits();
		
		for(int i = KIT1_POS;i<KIT1_POS+5;i++)
		{
			inventory.setItem(i, kits.get(i-KIT1_POS).getSymbol(true));
		}
		
		for(int i = KIT2_POS;i<KIT2_POS+5;i++)
		{
			inventory.setItem(i, kits.get(i-KIT2_POS+5).getSymbol(true));
		}
	}
	
	private void loadSoupKits()
	{
		ArrayList<Kit> kits = KitManager.getPreKits();
		
		for(int i = SOUP_KIT_POS;i<SOUP_KIT_POS+36;i+=9)
		{
			inventory.setItem(i, kits.get((i-SOUP_KIT_POS)/9+10).getSymbol(true));
		}
	}
	
	private void loadRankedKits()
	{
		ArrayList<Kit> kits = KitManager.getPreKits();
		
		for(int i = RANKED_KIT1_POS;i<RANKED_KIT3_POS+1;i+=2)
		{
			inventory.setItem(i, kits.get((i-RANKED_KIT1_POS)/2+14).getSymbol(true));
		}
	}
	
	@Override
	protected void setInventoryContents()
	{
		ItemStack wall = new ItemStack(Material.getMaterial(Items.menuWallMaterial),1,Items.menuWallDurability);
		{
			ItemMeta im = wall.getItemMeta();
			im.setDisplayName(" ");
			wall.setItemMeta(im);
		}
		
		setInventory(wall);
		
		update();
		
	}
	
	@Override
	public void update()
	{
		loadNormKits();
		loadSoupKits();
		loadRankedKits();
	}
	
	private boolean isNormClick(int slot)
	{
		if((slot >= KIT1_POS && slot < KIT1_POS+5) || (slot >= KIT2_POS && slot < KIT2_POS+5))
		{
			return true;
		}
		
		return false;
	}
	
	private boolean isSoupClick(int slot)
	{
		for(int i = 0;i<36;i+=9)
		{
			if(slot == SOUP_KIT_POS+i)
				return true;
		}
		
		return false;
	}
	
	private boolean isRankedClick(int slot)
	{
		if(slot==RANKED_KIT1_POS || slot==RANKED_KIT2_POS || slot==RANKED_KIT3_POS)
			return true;
		return false;
	}
	
	@Override
	public void onClick(int slot, ClickType ct)
	{
		if(ct.isLeftClick())
		{
			if(isNormClick(slot))
			{
				if(slot-KIT1_POS < 5)
				{
					KitManager.setChoosenKit(owner, null, -(slot-KIT1_POS+1), Integer.MAX_VALUE);	
				}
				else
				{
					KitManager.setChoosenKit(owner, null, -(slot-KIT2_POS+5+1), Integer.MAX_VALUE);
				}
				
				KitManager.setDif(owner, false);
				
				Kit kit = KitManager.getChoosenKitKit(owner);
				kit.kitItemsToInventory(owner);
				
				if(!LobbyListener.kitViewer.contains(owner.getUniqueId()))
					LobbyListener.kitViewer.add(owner.getUniqueId());
			}
			else if(isSoupClick(slot))
			{
				KitManager.setChoosenKit(owner, null, -((slot-SOUP_KIT_POS)/9+10+1), Integer.MAX_VALUE);
				KitManager.setDif(owner, false);
				
				Kit kit = KitManager.getChoosenKitKit(owner);
				kit.kitItemsToInventory(owner);
				
				if(!LobbyListener.kitViewer.contains(owner.getUniqueId()))
					LobbyListener.kitViewer.add(owner.getUniqueId());
			}
			else if(isRankedClick(slot))
			{
				if(slot == RANKED_KIT1_POS)
				{
					KitManager.setChoosenKit(owner, null, -(slot-RANKED_KIT1_POS+14+1), Integer.MAX_VALUE);
				}
				else if(slot == RANKED_KIT2_POS)
				{
					KitManager.setChoosenKit(owner, null, -(slot-RANKED_KIT2_POS+15+1), Integer.MAX_VALUE);
				}
				else
				{
					KitManager.setChoosenKit(owner, null, -(slot-RANKED_KIT3_POS+16+1), Integer.MAX_VALUE);
				}
				
				KitManager.setDif(owner, false);
				
				Kit kit = KitManager.getChoosenKitKit(owner);
				kit.kitItemsToInventory(owner);
				
				if(!LobbyListener.kitViewer.contains(owner.getUniqueId()))
					LobbyListener.kitViewer.add(owner.getUniqueId());
			}
		}
	}
}
