package at.Kingcraft.OnevsOne_lobby.Special;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Items.Items;
import at.Kingcraft.OnevsOne_lobby.Kits.Kit;
import at.Kingcraft.OnevsOne_lobby.Kits.KitSettings;
import at.Kingcraft.OnevsOne_lobby.Special.AnvilGUI.AnvilClickEvent;
import at.Kingcraft.OnevsOne_lobby.Special.AnvilGUI.AnvilClickEventHandler;
import at.Kingcraft.OnevsOne_lobby.Special.AnvilGUI.AnvilSlot;
import net.md_5.bungee.api.ChatColor;

public class KitSettingMenu extends Menu implements AnvilClickEventHandler
{
	
	private static final int SYMBOL_NEXT_POS = 13,
							 SYMBOL_PREV_POS = 11,
							 NAME_POS = 15,
							 EFFECT_POS = 28,
							 EFFECT_ACTIVATE_POS = 37,
							 BACK_POS = 45,
							 BUTTON_STEP = 7,
							 EFFECT_PREV_POS = EFFECT_POS-1,
							 EFFECT_NEXT_POS = EFFECT_POS+7;
	private static final short ACTIVATE_COLOR = 10,DEACTIVATE_COLOR = 8;
	private AnvilGUI nameGUI;
	
	private ItemStack symbol;
	private Kit openedKit;
	private int settingsOffset = 0;
	
	public KitSettingMenu(Player owner,KitMenu parent,String name)
	{
		super(owner, 54, name, parent);
		nameGUI = new AnvilGUI(owner, this);
	}
	
	private void setSettingsSymbols()
	{
		ItemStack nothing = new ItemStack(Material.AIR);
		
		for(int i = EFFECT_POS;i<EFFECT_POS+7;i++)
		{
			inventory.setItem(i, nothing);
		}
		
		for(int i = EFFECT_POS;i<EFFECT_POS+7 && (i-EFFECT_POS+settingsOffset)<KitSettings.values().length;i++)
		{
			inventory.setItem(i, KitSettings.values()[i-EFFECT_POS+settingsOffset].getSymbol());
		}
	}
	
	private void loadSettings(ArrayList<KitSettings> settings)
	{
		for(int i = EFFECT_ACTIVATE_POS;i<EFFECT_ACTIVATE_POS+7;i++)
		{
			ItemMeta im = inventory.getItem(i).getItemMeta();
			
			inventory.getItem(i).setDurability(DEACTIVATE_COLOR);
			im.setDisplayName(ChatColor.RED + "Aktivieren");
			
			inventory.getItem(i).setItemMeta(im);
		}
		
		for(int i = EFFECT_ACTIVATE_POS;i<EFFECT_ACTIVATE_POS+7 && (i-EFFECT_ACTIVATE_POS+settingsOffset)<KitSettings.values().length;i++)
		{
			ItemMeta im = inventory.getItem(i).getItemMeta();
			if(settings.contains(KitSettings.values()[i-EFFECT_ACTIVATE_POS+settingsOffset]))
			{
				inventory.getItem(i).setDurability(ACTIVATE_COLOR);
				im.setDisplayName(ChatColor.GREEN + "Deaktivieren");
			}
			else
			{
				inventory.getItem(i).setDurability(DEACTIVATE_COLOR);
				im.setDisplayName(ChatColor.RED + "Aktivieren");
			}
			
			inventory.getItem(i).setItemMeta(im);
		}
	}
	
	@Override
	protected void setInventoryContents()
	{
		{
			ItemStack wall = new ItemStack(Material.getMaterial(Items.menuWallMaterial),1,Items.menuWallDurability);
			ItemMeta im = wall.getItemMeta();
			im.setDisplayName(" ");
			wall.setItemMeta(im);
			
			setInventory(wall);
		}
		
		ItemStack nextButton = new ItemStack(Material.WOOD_BUTTON);
		{
			ItemMeta im = nextButton.getItemMeta();
			im.setDisplayName("+");
			nextButton.setItemMeta(im);
		}
		
		ItemStack prevButton = new ItemStack(Material.WOOD_BUTTON);
		{
			ItemMeta im = prevButton.getItemMeta();
			im.setDisplayName("-");
			prevButton.setItemMeta(im);
		}
		
		symbol = new ItemStack(Material.WOOD_SWORD);
		
		ItemStack nameSign = new ItemStack(Material.SIGN);
		{
			ItemMeta im = nameSign.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "Namen ändern");
			nameSign.setItemMeta(im);
		}
		
		ItemStack activate = new ItemStack(Material.INK_SACK,1,DEACTIVATE_COLOR);
		{
			ItemMeta im = activate.getItemMeta();
			im.setDisplayName(ChatColor.RED + "Aktivieren");
			activate.setItemMeta(im);
		}
		
		ItemStack nothing = new ItemStack(Material.AIR);
		
		ItemStack backDoor = new ItemStack(Material.getMaterial(Items.menuBackMaterial),1,Items.menuBackDurability);
		{
			ItemMeta im = backDoor.getItemMeta();
			im.setDisplayName(Items.menuBackName);
			backDoor.setItemMeta(im);
		}
		
		
		ItemStack[] contents = inventory.getContents();
		
		contents[SYMBOL_NEXT_POS] = nextButton;
		contents[SYMBOL_PREV_POS] = prevButton;
		contents[12] = symbol;
		contents[NAME_POS] = nameSign;
		contents[EFFECT_PREV_POS] = prevButton;
		contents[EFFECT_NEXT_POS] = nextButton;
		
		for(int i = EFFECT_POS;i<EFFECT_POS+7;i++)
		{
			contents[i] = nothing;
		}
		for(int i = EFFECT_ACTIVATE_POS;i<EFFECT_ACTIVATE_POS+7;i++)
		{
			contents[i] = activate;
		}
		contents[BACK_POS] = backDoor;
		
		inventory.setContents(contents);
		
		setSettingsSymbols();
	}


	private boolean isActivateClick(int slot)
	{
		return slot >= EFFECT_ACTIVATE_POS && slot <= EFFECT_ACTIVATE_POS+6;
	}
	

	private void addEffectOffset(int offset)
	{
		settingsOffset+=offset;
		
		boolean changed = true;
		
		if(settingsOffset >= KitSettings.values().length)
		{
			settingsOffset-=offset;
			changed = false;
		}
		else if(settingsOffset < 0)
		{
			settingsOffset-=offset;
			changed = false;
		}
		
		if(changed)
		{
			setSettingsSymbols();
			loadSettings(openedKit.getSettings());
		}
		
		
	}
	
	@Override
	public void onClick(int slot, ClickType ct)
	{
		if(slot == BACK_POS && ct.isLeftClick())
		{
			close();
		}
		else if(isActivateClick(slot) && ct.isLeftClick())
		{
			if(slot - EFFECT_ACTIVATE_POS +settingsOffset > KitSettings.values().length-1)
			{
				return;
			}
			
			short durability = inventory.getItem(slot).getDurability();
			if(durability == ACTIVATE_COLOR)
			{
				inventory.getItem(slot).setDurability(DEACTIVATE_COLOR);
				ItemMeta im = inventory.getItem(slot).getItemMeta();
				im.setDisplayName(ChatColor.RED + "Aktivieren");
				inventory.getItem(slot).setItemMeta(im);
			}
			else
			{
				inventory.getItem(slot).setDurability(ACTIVATE_COLOR);
				ItemMeta im = inventory.getItem(slot).getItemMeta();
				im.setDisplayName(ChatColor.GREEN + "Deaktivieren");
				inventory.getItem(slot).setItemMeta(im);
			}
			
			openedKit.toggleSetting(KitSettings.values()[slot-EFFECT_ACTIVATE_POS+settingsOffset]);
		}
		else if(slot == SYMBOL_NEXT_POS && ct.isLeftClick())
		{
			openedKit.addSymbol(1);
			symbol = openedKit.getSymbol(false);
			inventory.setItem(12, symbol);
		}
		else if(slot == SYMBOL_PREV_POS && ct.isLeftClick())
		{
			openedKit.addSymbol(-1);
			symbol = openedKit.getSymbol(false);
			inventory.setItem(12, symbol);
		}
		else if(slot == NAME_POS && ct.isLeftClick())
		{
			ItemStack is = openedKit.getSymbol(false);
			nameGUI.setSlot(AnvilSlot.INPUT_LEFT, is);
			nameGUI.open();
		}
		else if(slot == EFFECT_PREV_POS && ct.isLeftClick())
		{
			addEffectOffset(-BUTTON_STEP);
		}
		else if(slot == EFFECT_NEXT_POS && ct.isLeftClick())
		{
			addEffectOffset(BUTTON_STEP);
		}
	}
	
	public Kit getOpenedKit()
	{
		return openedKit;
	}
	
	public void open(Kit kit)
	{
		openedKit = kit;
		symbol = kit.getSymbol(false);
		inventory.setItem(12, symbol);
		settingsOffset = 0;
		setSettingsSymbols();
		loadSettings(kit.getSettings());
		
		super.open();
	}

	@Override
	public void onAnvilClick(AnvilClickEvent e)
	{
		if(e.getSlot() == AnvilSlot.OUTPUT)
		{
			e.setWillClose(true);
			e.setWillDestroy(false);
			
			if(!e.getName().equals(""))
				openedKit.setName(e.getName());
			
			symbol = openedKit.getSymbol(false).clone();
			ItemMeta im = symbol.getItemMeta();
			im.setDisplayName(openedKit.getName(false,false,true));
			symbol.setItemMeta(im);
			inventory.setItem(12, symbol);
			
			Bukkit.getScheduler().runTaskLaterAsynchronously(MainClass.getInstance(), new Runnable()
			{
				
				@Override
				public void run()
				{
					open();
				}
			}, 1);
		}
		else
		{
			e.setWillClose(false);
			e.setWillDestroy(false);
		}
	}
}
