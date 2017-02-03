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
import net.md_5.bungee.api.ChatColor;

public class KitDifferentMenu extends Menu
{
	
	private static final int KIT1_POS = 20;
	private static final int KIT2_POS = 22;
	private static final int KIT3_POS = 24;
	private static final int BACK_POS = 45;
	private static Kit kit1;
	private static Kit kit2;
	private static Kit kit3;
	private static final short ACTIVATE_COLOR = 10;
	private static final short DEACTIVATE_COLOR = 8;
	private static final int SET_DIF_POS = 53;
	private static final int CAT_POS = 4;
	private static final int CAT_PLUS_POS = CAT_POS+1;
	private static final int CAT_MINUS_POS = CAT_POS-1;
	
	public KitDifferentMenu(Player owner,KitMainMenu parent)
	{
		super(owner,54,"Verschiedene Kits",parent);
		updateActivate();
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
		
		contents[KIT1_POS] = kit1.getSymbol(true);
		contents[KIT2_POS] = kit2.getSymbol(true);
		contents[KIT3_POS] = kit3.getSymbol(true);
		
		ItemStack activate = new ItemStack(Material.INK_SACK,1,DEACTIVATE_COLOR);
		{
			ItemMeta im = activate.getItemMeta();
			im.setDisplayName(ChatColor.RED + "Aktivieren");
			activate.setItemMeta(im);
		}
		ItemStack back = new ItemStack(Material.getMaterial(Items.menuBackMaterial),1,Items.menuBackDurability);
		{
			ItemMeta im = back.getItemMeta();
			im.setDisplayName(Items.menuBackName);
			back.setItemMeta(im);
		}
		ItemStack cat = new ItemStack(Material.FISHING_ROD);
		{
			ItemMeta im = cat.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "Verschiedene:1");
			cat.setItemMeta(im);
		}
		ItemStack plus = new ItemStack(Material.WOOD_BUTTON);
		{
			ItemMeta im = plus.getItemMeta();
			im.setDisplayName(ChatColor.GRAY + "Coming Soon...");
			plus.setItemMeta(im);
		}
		ItemStack minus = new ItemStack(Material.WOOD_BUTTON);
		{
			ItemMeta im = minus.getItemMeta();
			im.setDisplayName(ChatColor.GRAY + "Coming Soon...");
			minus.setItemMeta(im);
		}
		
		contents[KIT1_POS+9] = activate;
		contents[KIT2_POS+9] = activate;
		contents[KIT3_POS+9] = activate;
		contents[CAT_POS] = cat;
		contents[CAT_PLUS_POS] = plus;
		contents[CAT_MINUS_POS] = minus;
		contents[BACK_POS] = back;
		
		inventory.setContents(contents);
		
		updateActivation();
	}

	public static void setupKits()
	{
		kit1 = KitManager.getDifKit(1);
		kit2 = KitManager.getDifKit(2);
		kit3 = KitManager.getDifKit(3);
	}
	
	private void updateActivation()
	{
		ItemStack is = new ItemStack(Material.INK_SACK);
		ItemMeta im = is.getItemMeta();
		if(KitManager.getDif(owner))
		{
			im.setDisplayName(ChatColor.GREEN + "Verschiedene:1 deaktivieren");
			is.setDurability(ACTIVATE_COLOR);
		}
		else
		{
			im.setDisplayName(ChatColor.RED + "Verschiedene:1 aktivieren");
			is.setDurability(DEACTIVATE_COLOR);
		}
		
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.WHITE + "Stellt ein, ob man Verschiedene Kits");
		lore.add(ChatColor.WHITE + "oder die anderen Kits verwendet");
		im.setLore(lore);
		is.setItemMeta(im);
		
		inventory.setItem(SET_DIF_POS, is);
	}
	
	@Override
	public void onClick(int slot, ClickType ct)
	{
		if(slot == BACK_POS && ct.isLeftClick())
		{
			close();
		}
		else if(slot == KIT1_POS+9 && ct.isLeftClick())
		{
			KitManager.setChoosenDifKit(owner, 1);
			updateActivate();
		}
		else if(slot == KIT2_POS+9 && ct.isLeftClick())
		{
			KitManager.setChoosenDifKit(owner, 2);
			updateActivate();
		}
		else if(slot == KIT3_POS+9 && ct.isLeftClick())
		{
			KitManager.setChoosenDifKit(owner, 3);
			updateActivate();
		}
		else if(slot == SET_DIF_POS && ct.isLeftClick())
		{
			KitManager.setDif(owner, !KitManager.getDif(owner));
			updateActivation();
		}
	}
	
	@Override
	public void open()
	{
		inventory.setItem(KIT1_POS, kit1.getSymbol(true));
		inventory.setItem(KIT2_POS, kit2.getSymbol(true));
		inventory.setItem(KIT3_POS, kit3.getSymbol(true));
		
		super.open();
	}
	
	@Override
	public void update()
	{
		inventory.setItem(KIT1_POS,kit1.getSymbol(true));
		inventory.setItem(KIT2_POS,kit2.getSymbol(true));
		inventory.setItem(KIT3_POS,kit3.getSymbol(true));
		
		updateActivate();
		updateActivation();
	}
	
	private void setActivate(ItemStack is)
	{
		is.setType(Material.INK_SACK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "Deaktivieren");
		is.setDurability(ACTIVATE_COLOR);
		is.setItemMeta(im);
	}
	
	private void setDeactivate(ItemStack is)
	{
		is.setType(Material.INK_SACK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RED + "Aktivieren");
		is.setDurability(DEACTIVATE_COLOR);
		is.setItemMeta(im);
	}
	
	private void updateActivate()
	{
		int cKit = KitManager.getChoosenDifKit(owner);
		int activatePos;
		if(cKit == 1)
		{
			activatePos = KIT1_POS+9;
		}
		else if(cKit == 2)
		{
			activatePos = KIT2_POS+9;
		}
		else if(cKit == 3)
		{
			activatePos = KIT3_POS+9;
		}
		else
		{
			activatePos = KIT1_POS+9;
		}
		
		for(int i = KIT1_POS+9;i<=KIT3_POS+9;i+=2)
		{
			ItemStack is = new ItemStack(Material.INK_SACK);
			if(i == activatePos)
			{
				setActivate(is);
			}
			else
			{
				setDeactivate(is);
			}
			inventory.setItem(i, is);
		}
	}

	
}
