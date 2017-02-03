package at.Kingcraft.OnevsOne_lobby.Special;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import at.Kingcraft.OnevsOne_lobby.Items.Items;
import at.Kingcraft.OnevsOne_lobby.WaitingSnake.Settings;
import net.md_5.bungee.api.ChatColor;

public class WSSettingMenu extends Menu
{
	private static final int BACK_POS = 9;
	private static final int QWS_POS = 12;
	private static final int QWS_ACTIVATE1_POS = QWS_POS-9;
	private static final int QWS_ACTIVATE2_POS = QWS_POS+9;
	private static final int KIT_POS = 6;
	private static final int KIT_PLUS_POS = KIT_POS+1;
	private static final int KIT_MINUS_POS = KIT_POS-1;
	private static final int MAP_POS = 24;
	private static final int MAP_PLUS_POS = MAP_POS+1;
	private static final int MAP_MINUS_POS = MAP_POS-1;
	private static final short ACTIVATE_COLOR = 10;
	private static final short DEACTIVATE_COLOR = 8;
	private Settings settings;
	
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
		
		ItemStack backDoor = new ItemStack(Material.getMaterial(Items.menuBackMaterial),1,Items.menuBackDurability);
		{
			ItemMeta im = backDoor.getItemMeta();
			im.setDisplayName(Items.menuBackName);
			backDoor.setItemMeta(im);
		}
		
		ItemStack qws = new ItemStack(Material.POWERED_RAIL);
		{
			ItemMeta im = qws.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "Schnell-Beitritt");
			
			ArrayList<String> lore = new ArrayList<>();
			lore.add(ChatColor.WHITE + "Fügt dich nach einem Kampf");
			lore.add(ChatColor.WHITE + "in der Warteschlange sofort");
			lore.add(ChatColor.WHITE + "wieder zu dieser hinzu");
			im.setLore(lore);
			
			qws.setItemMeta(im);
		}
		
		ItemStack activate = new ItemStack(Material.INK_SACK,1,DEACTIVATE_COLOR);
		{
			ItemMeta im = activate.getItemMeta();
			im.setDisplayName(ChatColor.RED + "Aktivieren");
			activate.setItemMeta(im);
		}
		ItemStack Plusbutton = new ItemStack(Material.WOOD_BUTTON);
		{
			ItemMeta im = Plusbutton.getItemMeta();
			im.setDisplayName("+");
			Plusbutton.setItemMeta(im);
		}
		ItemStack Minusbutton = new ItemStack(Material.WOOD_BUTTON);
		{
			ItemMeta im = Minusbutton.getItemMeta();
			im.setDisplayName("-");
			Minusbutton.setItemMeta(im);
		}
		
		contents[BACK_POS] = backDoor;
		contents[QWS_POS] = qws;
		contents[QWS_ACTIVATE1_POS] = activate;
		contents[QWS_ACTIVATE2_POS] = activate;
		contents[KIT_PLUS_POS] = Plusbutton;
		contents[KIT_MINUS_POS] = Minusbutton;
		contents[MAP_PLUS_POS] = Plusbutton;
		contents[MAP_MINUS_POS] = Minusbutton;
		
		inventory.setContents(contents);
		
		reload1();
		reload2();
		
	}
	
	private void setKitMode(int mode)
	{
		Material type;
		String name;
		ArrayList<String> lore = new ArrayList<>();
		
		switch(mode)
		{
			case Settings.NO_KIT_MODE:
				type = Material.WOOD_SWORD;
				name = ChatColor.YELLOW + "Kit egal";
				lore.add(ChatColor.WHITE + "Dir ist das Kit egal");
				lore.add(ChatColor.WHITE + "und du bekommst dein Kit");
				lore.add(ChatColor.WHITE + "oder das des anderen");
				break;
			case Settings.OWN_KIT_MODE:
				type = Material.DIAMOND_SWORD;
				name = ChatColor.YELLOW + "Eigenes Kit";
				lore.add(ChatColor.WHITE + "Du willst dein eigenes Kit");
				lore.add(ChatColor.WHITE + "Du bekommst dein Kit");
				lore.add(ChatColor.WHITE + "Wartezeiten könnten länger sein");
				break;
			default:
				type = Material.PAPER;
				name = "null";
				lore.add(ChatColor.WHITE + "null");
				break;
		}
		
		ItemStack kit = new ItemStack(type);
		ItemMeta im = kit.getItemMeta();
		im.setDisplayName(name);
		im.setLore(lore);
		kit.setItemMeta(im);
		
		inventory.setItem(KIT_POS, kit);
	}
	
	private void setMapMode(int mode)
	{
		Material type;
		String name;
		ArrayList<String> lore = new ArrayList<>();
		
		switch(mode)
		{
			case Settings.NO_MAP_MODE:
				type = Material.PAPER;
				name = ChatColor.YELLOW + "Map egal";
				lore.add(ChatColor.WHITE + "Dir ist die Map egal");
				lore.add(ChatColor.WHITE + "und du bekommst deine Map");
				lore.add(ChatColor.WHITE + "oder die des anderen");
				break;
			case Settings.OWN_MAP_MODE:
				type = Material.MAP;
				name = ChatColor.YELLOW + "Eigene Maps";
				lore.add(ChatColor.WHITE + "Du willst deine eigene Map");
				lore.add(ChatColor.WHITE + "Du bekommst deine Map");
				lore.add(ChatColor.WHITE + "Wartezeiten könnten länger sein");
				break;
			default:
				type = Material.PAPER;
				name = "null";
				lore.add(ChatColor.WHITE + "null");
				break;
		}
		
		ItemStack map = new ItemStack(type);
		ItemMeta im = map.getItemMeta();
		im.setDisplayName(name);
		im.setLore(lore);
		map.setItemMeta(im);
		
		inventory.setItem(MAP_POS, map);
	}

	private void reload1()
	{
		ItemMeta im = inventory.getItem(QWS_ACTIVATE1_POS).getItemMeta();
		if(Settings.getSettings(owner).isQuickWS())
		{
			im.setDisplayName(ChatColor.GREEN + "Deaktivieren");
			inventory.getItem(QWS_ACTIVATE1_POS).setDurability(ACTIVATE_COLOR);
		}
		else
		{
			im.setDisplayName(ChatColor.RED + "Aktivieren");
			inventory.getItem(QWS_ACTIVATE1_POS).setDurability(DEACTIVATE_COLOR);
		}
		
		inventory.getItem(QWS_ACTIVATE1_POS).setItemMeta(im);
	}
	
	private void reload2()
	{
		ItemMeta im = inventory.getItem(QWS_ACTIVATE2_POS).getItemMeta();
		if(Settings.getSettings(owner).isQuickWS())
		{
			im.setDisplayName(ChatColor.GREEN + "Deaktivieren");
			inventory.getItem(QWS_ACTIVATE2_POS).setDurability(ACTIVATE_COLOR);
		}
		else
		{
			im.setDisplayName(ChatColor.RED + "Aktivieren");
			inventory.getItem(QWS_ACTIVATE2_POS).setDurability(DEACTIVATE_COLOR);
		}
		
		inventory.getItem(QWS_ACTIVATE2_POS).setItemMeta(im);
	}
	
	@Override
	public void onClick(int slot, ClickType ct)
	{
		if(slot == BACK_POS && ct.isLeftClick())
		{
			close();
		}
		else if(slot == QWS_ACTIVATE1_POS || slot == QWS_ACTIVATE2_POS && ct.isLeftClick())
		{
			Settings set = Settings.getSettings(owner);
			Settings.getSettings(owner).setQuickWS(!set.isQuickWS());
			
			reload1();
			reload2();
		}
		else if(slot == KIT_PLUS_POS && ct.isLeftClick())
		{
			int newKitMode = settings.getRealKitMode()+1;
			if(newKitMode > 1)
			{
				newKitMode = 0;
			}
			
			settings.setKitMode(newKitMode);
			setKitMode(newKitMode);
		}
		else if(slot == KIT_MINUS_POS && ct.isLeftClick())
		{
			int newKitMode = settings.getRealKitMode() - 1;
			if(newKitMode < 0)
			{
				newKitMode = 1;
			}
			
			settings.setKitMode(newKitMode);
			setKitMode(newKitMode);
		}
		else if(slot == MAP_PLUS_POS && ct.isLeftClick())
		{
			int newMapMode = settings.getMapMode() + 1;
			if(newMapMode > 1)
			{
				newMapMode = 0;
			}
			
			settings.setMapMode(newMapMode);
			setMapMode(newMapMode);
		}
		else if(slot == MAP_MINUS_POS && ct.isLeftClick())
		{
			int newMapMode = settings.getMapMode() - 1;
			if(newMapMode < 0)
			{
				newMapMode = 1;
			}
			
			settings.setMapMode(newMapMode);
			setMapMode(newMapMode);
		}
	}
	
	@Override
	public void update()
	{
		settings = Settings.getSettings(owner);
		setKitMode(settings.getRealKitMode());
		setMapMode(settings.getMapMode());
		
		reload1();
		reload2();
	}

	public WSSettingMenu(Player owner,SettingMenu parent)
	{
		super(owner,27,"Warteschlange-Einstellungen",parent);
		
		settings = Settings.getSettings(owner);
		setKitMode(settings.getRealKitMode());
		setMapMode(settings.getMapMode());
	}
	
	
}
