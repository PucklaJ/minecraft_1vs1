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

public class KitPreMenu extends Menu
{
	/*private static final int KIT_POS1 = 10,
							 KIT_POS2 = 28,
							 ACTIVATE1 = KIT_POS1+9,
							 ACTIVATE2 = KIT_POS2+9,
							 BACK_POS = 49,
							 CATEGORY_POS=4;
	private static final short ACTIVATE_COLOR = 10,DEACTIVATE_COLOR = 8;
	private static ItemStack activate=null,deactivate=null;
	private static ItemStack soup=null,normal=null;
	
	private int choosenSlot = -1;
	private boolean currentCategory = true; // true == normal false == soup*/
	
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
		/*if(activate == null)
		{
			activate = new ItemStack(Material.INK_SACK,1,ACTIVATE_COLOR);
			{
				ItemMeta im = activate.getItemMeta();
				im.setDisplayName(ChatColor.GREEN + "Deaktivieren");
				activate.setItemMeta(im);
			}
		}
		if(deactivate == null)
		{
			deactivate = new ItemStack(Material.INK_SACK,1,DEACTIVATE_COLOR);
			{
				ItemMeta im = deactivate.getItemMeta();
				im.setDisplayName(ChatColor.RED + "Aktivieren");
				deactivate.setItemMeta(im);
			}
		}
		if(soup == null)
		{
			soup = new ItemStack(Material.MUSHROOM_SOUP);
			{
				ItemMeta im = soup.getItemMeta();
				im.setDisplayName(ChatColor.YELLOW + "Soup Kits");
				soup.setItemMeta(im);
			}
		}
		if(normal == null)
		{
			normal = new ItemStack(Material.STONE_SWORD);
			{
				ItemMeta im = normal.getItemMeta();
				im.setDisplayName(ChatColor.YELLOW + "Normale Kits");
				normal.setItemMeta(im);
			}
		}
		
		ItemStack wall = new ItemStack(Material.getMaterial(Items.menuWallMaterial),1,Items.menuWallDurability);
		{
			ItemMeta im = wall.getItemMeta();
			im.setDisplayName(" ");
			wall.setItemMeta(im);
		}
		
		setInventory(wall);
		
		ItemStack back = new ItemStack(Material.getMaterial(Items.menuBackMaterial),1,Items.menuBackDurability);
		{
			ItemMeta im = back.getItemMeta();
			im.setDisplayName(Items.menuBackName);
			back.setItemMeta(im);
		}
		
		inventory.setItem(BACK_POS, back);
		inventory.setItem(CATEGORY_POS, soup);
		
		currentCategory = true;
		
		setActivate1();
		setActivate2();*/
		
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
	
	/*@Override
	public void open()
	{
		setKit1();
		setKit2();
		super.open();
	}*/
	
	/*private boolean isActivate1Click(int slot)
	{
		return slot >= ACTIVATE1 && slot < ACTIVATE1+7;
	}
	
	private boolean isActivate2Click(int slot)
	{
		return slot >= ACTIVATE2 && slot < ACTIVATE2+7;
	}
	
	private void setActivate1()
	{
		int choosenKit = KitManager.getChoosenKit(owner).myNumber;
		int slot = -1;
		
		if(choosenKit >= 0 || (choosenKit < -14 && currentCategory) || (choosenKit >= -14 && !currentCategory))
			slot = -1;
		else if(choosenKit >= -7)
			slot = choosenKit*-1-1+ACTIVATE1;
		else if(choosenKit >= -14)
			slot = -1;
		else if(choosenKit >= -21)
			slot = choosenKit*-1-15+ACTIVATE1;
		else if(choosenKit >= -28)
			slot = -1;
		
		for(int i = ACTIVATE1;i<ACTIVATE1+7;i++)
		{
			inventory.setItem(i,slot == i ? activate : deactivate);
		}
		
		choosenSlot = slot;
	}
	
	private void setActivate2()
	{
		int choosenKit = KitManager.getChoosenKit(owner).myNumber;
		int slot = -1;
		
		if(choosenKit >= 0 || (choosenKit < -14 && currentCategory) || (choosenKit >= -14 && !currentCategory))
			slot = -1;
		else if(choosenKit >= -7)
			slot = -1;
		else if(choosenKit >= -14)
			slot = choosenKit*-1-8+ACTIVATE2;
		else if(choosenKit >= -21)
			slot = -1;
		else if(choosenKit >= -28)
			slot = choosenKit*-1-22+ACTIVATE2;
		
		for(int i = ACTIVATE2;i<ACTIVATE2+7;i++)
		{
			inventory.setItem(i,slot != -1 ? (slot == i ? activate : deactivate) : deactivate);
		}
		
		choosenSlot = slot;
	}
	
	private void setKit1()
	{
		for(int i = KIT_POS1,kit=currentCategory ? 1 : 15;i<KIT_POS1+7;i++)
		{
			inventory.setItem(i, KitManager.getPreKit(kit).getSymbol(true));
			kit++;
		}
	}
	
	private void setKit2()
	{
		for(int i = KIT_POS2,kit=currentCategory ? 8 : 22;i<KIT_POS2+7;i++)
		{
			inventory.setItem(i, KitManager.getPreKit(kit).getSymbol(true));
			kit++;
		}
	}

	public void deactivateChoosenKit()
	{
		if(choosenSlot != -1)
			setDeactivate(inventory.getItem(choosenSlot));
	}
	
	private void setActivate(ItemStack is)
	{
		is.setDurability(ACTIVATE_COLOR);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "Deaktivieren");
		is.setItemMeta(im);
	}
	
	private void setDeactivate(ItemStack is)
	{
		is.setDurability(DEACTIVATE_COLOR);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RED + "Aktivieren");
		is.setItemMeta(im);
	}
	
	private void deactivateActive()
	{
		for(int i = ACTIVATE1;i<ACTIVATE1+7;i++)
		{
			if(inventory.getItem(i).getDurability() == ACTIVATE_COLOR)
			{
				setDeactivate(inventory.getItem(i));
			}
		}
		
		for(int i = ACTIVATE2;i<ACTIVATE2+7;i++)
		{
			if(inventory.getItem(i).getDurability() == ACTIVATE_COLOR)
			{
				setDeactivate(inventory.getItem(i));
			}
		}
	}
	
	private void toogleCategory()
	{
		currentCategory = !currentCategory;
		
		if(!currentCategory)
		{
			inventory.setItem(CATEGORY_POS, normal);
		}
		else
		{
			inventory.setItem(CATEGORY_POS, soup);
		}
		
		setKit1();
		setKit2();
		setActivate1();
		setActivate2();
	}*/
	
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
		/*if(isActivate1Click(slot) && ct.isLeftClick())
		{
			deactivateActive();
			
			setActivate(inventory.getItem(slot));
			KitManager.updateChoosenKit(owner,null, (slot-ACTIVATE1+1+(currentCategory ? 0 : 14))*-1,Integer.MAX_VALUE);
			if(choosenSlot != -1 && choosenSlot != slot)
				setDeactivate(inventory.getItem(choosenSlot));
			choosenSlot = slot;
			
		}
		else if(isActivate2Click(slot) && ct.isLeftClick())
		{
			deactivateActive();
			setActivate(inventory.getItem(slot));
			KitManager.updateChoosenKit(owner,null, (slot-ACTIVATE2+8+(currentCategory ? 0 : 14))*-1,Integer.MAX_VALUE);
			if(choosenSlot != -1 && choosenSlot != slot)
				setDeactivate(inventory.getItem(choosenSlot));
			choosenSlot = slot;
		}
		else if(slot == CATEGORY_POS && ct.isLeftClick())
		{
			toogleCategory();
		}
		else if(slot == BACK_POS && ct.isLeftClick())
		{
			close();
		}*/
		
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
			}
			else if(isSoupClick(slot))
			{
				KitManager.setChoosenKit(owner, null, -((slot-SOUP_KIT_POS)/9+10+1), Integer.MAX_VALUE);
				KitManager.setDif(owner, false);
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
			}
		}
	}
}
