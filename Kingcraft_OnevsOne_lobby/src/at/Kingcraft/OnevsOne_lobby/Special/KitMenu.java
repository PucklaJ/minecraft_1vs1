package at.Kingcraft.OnevsOne_lobby.Special;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import at.Kingcraft.OnevsOne_lobby.Items.Items;
import at.Kingcraft.OnevsOne_lobby.Kits.Kit;
import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;
import net.md_5.bungee.api.ChatColor;

public class KitMenu extends Menu {
	
	protected static final int KIT_POS = 11;
	protected static final int ACTIVATE_POS = 20;
	protected static final int SETTING_POS = 38;
	protected static final int BACK_POS = 45;
	private static final short ACTIVATE_COLOR = 10;
	private static final short DEACTIVATE_COLOR = 8;
	protected Kit[] kits;
	protected KitSettingMenu kitSettingMenu;
	
	public KitMenu(Player owner,KitMainMenu parent,String name,String settingName)
	{
		super(owner,54,name,parent);
		
		kitSettingMenu = new KitSettingMenu(owner, this,settingName);
		
		reloadKits();
		setActivateToInventory();
	}

	@Override
	public void open()
	{
		reloadKits();
		super.open();
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
		
		ItemStack[] contents = inventory.getContents();
		
		for(int i = SETTING_POS;i<SETTING_POS+5;i++)
		{
			ItemStack setting = new ItemStack(Material.REDSTONE_COMPARATOR);
			ItemMeta im = setting.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "Einstellungen");
			setting.setItemMeta(im);
			contents[i] = setting;
		}
		
		
		ItemStack back = new ItemStack(Material.getMaterial(Items.menuBackMaterial),1,Items.menuBackDurability);
		{
			ItemMeta im = back.getItemMeta();
			im.setDisplayName(Items.menuBackName);
			back.setItemMeta(im);
		}
		contents[BACK_POS] = back;
		
		inventory.setContents(contents);
		
	}
	
	protected boolean isKitClick(int slot)
	{
		if(slot >= KIT_POS && slot <= KIT_POS + 4)
		{
			return true;
		}
		return false;
	}
	
	protected boolean isActivateClick(int slot)
	{
		if(slot >= ACTIVATE_POS && slot <= ACTIVATE_POS + 4)
		{
			return true;
		}
		return false;
	}
	
	protected boolean isSettingClick(int slot)
	{
		return slot >= SETTING_POS && slot <= SETTING_POS+4;
	}
	
	protected void setKitsToInventory()
	{
		for(int i = 0;i<kits.length;i++)
		{
			inventory.setItem(KIT_POS+i,kits[i].getSymbol(false));
		}
	}
	
	protected void setActivateToInventory()
	{
		boolean kitIsHere = false;
		int cKit = KitManager.getChoosenKit(owner).myNumber;
		
		
		/*if((this instanceof KitSoupMenu) && cKit >= 5)
		{
			kitIsHere = true;
			cKit -= 5;
		}
		else*/ if((this instanceof KitOwnMenu) && cKit >= 0)
		{
			kitIsHere = true;
		}
		
		for(int i = ACTIVATE_POS;i<ACTIVATE_POS+5;i++)
		{
			ItemMeta im = inventory.getItem(i).getItemMeta();
			
			inventory.getItem(i).setType(Material.INK_SACK);
				
			if(kitIsHere && i-ACTIVATE_POS == cKit)
			{		
				im.setDisplayName(ChatColor.GREEN + "Deaktivieren");
				inventory.getItem(i).setDurability(ACTIVATE_COLOR);
			}
			else
			{
				im.setDisplayName(ChatColor.RED + "Aktivieren");
				inventory.getItem(i).setDurability(DEACTIVATE_COLOR);
			}
			
			inventory.getItem(i).setItemMeta(im);
		}
	}
	

	public KitSettingMenu getKitSettingMenu()
	{
		return kitSettingMenu;
	}
	
	public void reloadKits()
	{
		setKitsToInventory();
	}
	
	public void deactivateChoosenKit()
	{
		for(int i = ACTIVATE_POS;i<ACTIVATE_POS+5;i++)
		{
			ItemStack is = inventory.getItem(i);
			if(is.getDurability() == ACTIVATE_COLOR)
			{
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(ChatColor.RED + "Aktivieren");
				is.setItemMeta(im);
				is.setDurability(DEACTIVATE_COLOR);
				
				inventory.setItem(i, is);
			}
		}
	}
	
	private void clearAllActivate()
	{
		ItemStack clear = new ItemStack(Material.INK_SACK,1,DEACTIVATE_COLOR);
		ItemMeta im = clear.getItemMeta();
		im.setDisplayName(ChatColor.RED + "Aktivieren");
		clear.setItemMeta(im);
		
		for(int i = ACTIVATE_POS;i<ACTIVATE_POS+5;i++)
		{
			inventory.setItem(i, clear);
		}
	}
	
	private void updateActivation()
	{
		clearAllActivate();
		
		int choosenKit = KitManager.getChoosenKit(owner).myNumber;
		
		if(choosenKit < 0)
		{
			return;
		}
		
		int slot = 0;
		
		if(choosenKit < 5 && this instanceof KitOwnMenu)
		{
			slot = ACTIVATE_POS + choosenKit;
		}
		/*else if(choosenKit >=5 && this instanceof KitSoupMenu)
		{
			slot = ACTIVATE_POS + choosenKit-5;
		}*/
		else
		{
			return;
		}
		
		ItemStack set = new ItemStack(Material.INK_SACK,1,ACTIVATE_COLOR);
		ItemMeta im = set.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "Deaktivieren");
		set.setItemMeta(im);
		
		for(int i = ACTIVATE_POS;i<ACTIVATE_POS+5;i++)
		{
			if(i==slot)
			inventory.setItem(i, set);
		}
		
		
	}
	
	@Override
	public void update()
	{
		updateActivation();
	}
	
	@Override
	public void onClick(int slot, ClickType ct)
	{
		if(isActivateClick(slot) && ct.isLeftClick())
		{
			 int choosenKit = KitManager.getChoosenKit(owner).myNumber;
			
		     ItemStack is1 = new ItemStack(Material.INK_SACK,1,DEACTIVATE_COLOR);
			 ItemMeta im1 = inventory.getItem(slot).getItemMeta();
			 im1.setDisplayName(ChatColor.RED + "Aktivieren");
			 is1.setItemMeta(im1);
			 
			 if(choosenKit >= 5)
			 {
				 choosenKit-=5;
			 }
			 
			 if(choosenKit >= 0)
			 {
				 inventory.setItem(choosenKit+ACTIVATE_POS,is1);
			 }
			 
			 
			 ItemStack is = new ItemStack(Material.INK_SACK,1,ACTIVATE_COLOR);
			 ItemMeta im = is.getItemMeta();
			 im.setDisplayName(ChatColor.GREEN + "Deaktivieren");
			 is.setItemMeta(im);
			 
			 inventory.setItem(slot,is);
			 
			 choosenKit = slot - ACTIVATE_POS;
			 
			 /*if(this instanceof KitSoupMenu)
			 {
				 choosenKit += 5;
			 }*/
			 
			 KitManager.updateChoosenKit(owner,null, choosenKit,Integer.MAX_VALUE);
			 KitManager.setDif(owner, false);
			 MenuManager.getSettingMenu(owner).getKitMainMenu().getKitDifMenu().update();
		}
		else if(isSettingClick(slot) && ct.isLeftClick())
		{
			kitSettingMenu.open(kits[slot-SETTING_POS]);
		}
		else if(slot == BACK_POS && ct.isLeftClick())
		{
			close();
		}
	}
	
}
