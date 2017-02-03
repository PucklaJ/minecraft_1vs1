package at.Kingcraft.OnevsOne_lobby.Special;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import at.Kingcraft.OnevsOne_lobby.Items.Items;
import net.md_5.bungee.api.ChatColor;

public class KitMainMenu extends Menu {
	
	private static final int OWN_POS = 11;
	//private static final int SOUP_POS = 12;
	private static final int DIF_POS = 13;
	private static final int VOR_POS = 15;
	private static final int BACK_POS = 18;
	private KitOwnMenu kitOwnMenu;
	//private KitSoupMenu kitSoupMenu;
	private KitDifferentMenu kitDifMenu;
	private KitPreMenu kitPreMenu;
	
	public KitMainMenu(Player owner,SettingMenu parent)
	{
		super(owner,27,"Kit-Einstellungen",parent);
		kitOwnMenu = new KitOwnMenu(owner,this);
		//kitSoupMenu = new KitSoupMenu(owner,this);
		kitDifMenu = new KitDifferentMenu(owner,this);
		kitPreMenu = new KitPreMenu(owner, this);
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
		
		{
			ItemStack is = new ItemStack(Material.SKULL_ITEM,1,(short)3);
			SkullMeta sm = (SkullMeta) is.getItemMeta();
			sm.setOwner(owner.getDisplayName());
			sm.setDisplayName(ChatColor.YELLOW + "Eigene Kits");
			is.setItemMeta(sm);
			inventory.setItem(OWN_POS,is);
		}
		/*{
			ItemStack is = new ItemStack(Material.MUSHROOM_SOUP);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "Soup Kits");
			is.setItemMeta(im);
			inventory.setItem(SOUP_POS, is);
		}*/
		{
			ItemStack is = new ItemStack(Material.NETHER_STAR);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "Verschiedene Kits");
			is.setItemMeta(im);
			inventory.setItem(DIF_POS, is);
		}
		{
			ItemStack is = new ItemStack(Material.ANVIL);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "Vorgegebene Kits");
			is.setItemMeta(im);
			inventory.setItem(VOR_POS, is);
		}
		{
			ItemStack is = new ItemStack(Material.getMaterial(Items.menuBackMaterial),1,Items.menuBackDurability);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(Items.menuBackName);
			is.setItemMeta(im);
			inventory.setItem(BACK_POS, is);
		}
		
	}

	@Override
	public void onClick(int slot, ClickType ct)
	{
		if(slot == OWN_POS && ct.isLeftClick())
		{
			kitOwnMenu.open();
		}
		/*else if(slot == SOUP_POS && ct.isLeftClick())
		{
			//kitSoupMenu.open();
		}*/
		else if(slot == DIF_POS && ct.isLeftClick())
		{
			kitDifMenu.open();
		}
		else if(slot == VOR_POS && ct.isLeftClick())
		{
			kitPreMenu.open();
		}
		else if(slot == BACK_POS && ct.isLeftClick())
		{
			close();
		}
	}
	
	public KitOwnMenu getKitOwnMenu()
	{
		return kitOwnMenu;
	}
	
	/*public KitSoupMenu getKitSoupMenu()
	{
		return kitSoupMenu;
	}*/
	
	public KitDifferentMenu getKitDifMenu()
	{
		return kitDifMenu;
	}

	public KitPreMenu getKitPreMenu()
	{
		return kitPreMenu;
	}

}
