package at.Kingcraft.OnevsOne_lobby.Special;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Duels.Team;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamManager;
import at.Kingcraft.OnevsOne_lobby.Items.Items;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import net.md_5.bungee.api.ChatColor;

public class SettingMenu extends Menu {
	
	private MapMenu mapMenu;
	private TeamMenu teamMenu;
	private KitMainMenu kitMainMenu;
	private WSSettingMenu wsSettings;
	private TourSettingMenu tourSM;
	private static int MAP_POS;
	private static final int TEAM_POS = 40;
	private static final int CLOSE_POS = 49;
	private static final int KIT_POS = 11;
	private static final int WS_POS = 34;
	private static final int TOUR_POS = 28;
	private static final int CHAT_POS = 15;
	
	public SettingMenu(Player owner,MainClass plugin)
	{
		super(owner, 54, "Einstellungen",null,plugin);
		mapMenu = new MapMenu(owner,this);
		kitMainMenu = new KitMainMenu(owner,this);
		wsSettings = new WSSettingMenu(owner,this);
		tourSM = new TourSettingMenu(owner,this);
		this.owner = owner;
	}
	
	public void createTeamMenu()
	{
		if(teamMenu == null)
		{
			teamMenu = new TeamMenu(owner,this);
			
		}
		inventory.getItem(TEAM_POS).setType(Material.getMaterial(Items.teamSettingsItemTeamMaterial));
	}
	
	public void deleteTeamMenu()
	{
		if(teamMenu != null)
		{
			inventory.getItem(TEAM_POS).setType(Material.getMaterial(Items.teamSettingsItemNoTeamMaterial));
		}
	}
	
	private void setOutlineOfInventory(ItemStack is,ItemStack[] contents)
	{
		for(int i = 0;i<9;i++)
		{
			contents[i] = is.clone();
		}
		for(int i = 9;i<45;i+=9)
		{
			contents[i] = is.clone();
		}
		for(int i = 45;i<54;i++)
		{
			contents[i] = is.clone();
		}
		for(int i = 17;i<53;i+=9)
		{
			contents[i] = is.clone();
		}
	}
	
	@Override
	protected void setInventoryContents(MainClass plugin)
	{
		ItemStack[] contents = new ItemStack[inventory.getSize()];
		{
			ItemStack is = new ItemStack(Material.getMaterial(Items.menuWallMaterial),1,Items.menuWallDurability);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(" ");
			is.setItemMeta(im);
			
			setOutlineOfInventory(is, contents);
		}
		
		MAP_POS = plugin.getConfig().getInt("Items.Settings.Maps.Slot");
		
		contents[MAP_POS] = new ItemStack(Material.getMaterial(Items.mapSettingsItemMaterial));
		{
			ItemMeta im = contents[MAP_POS].getItemMeta();
			im.setDisplayName(Items.mapSettingsItemName);
			im.setLore(Items.mapSettingsItemLore);
			contents[MAP_POS].setItemMeta(im);
		}
		
		contents[TEAM_POS] = new ItemStack(Material.getMaterial(Items.teamSettingsItemNoTeamMaterial));
		{
			ItemMeta im = contents[TEAM_POS].getItemMeta();
			im.setDisplayName(Items.teamSettingsItemName);
			im.setLore(Items.teamSettingsItemLore);
			contents[TEAM_POS].setItemMeta(im);
		}
		
		contents[CLOSE_POS] = new ItemStack(Material.getMaterial(Items.menuBackMaterial),1,Items.menuBackDurability);
		{
			ItemMeta im = contents[CLOSE_POS].getItemMeta();
			im.setDisplayName(Items.menuBackName);
			contents[CLOSE_POS].setItemMeta(im);
		}
		
		contents[KIT_POS] = new ItemStack(Material.getMaterial(Items.kitSettingsItemMaterial));
		{
			ItemMeta im = contents[KIT_POS].getItemMeta();
			im.setDisplayName(Items.kitSettingsItemName);
			im.setLore(Items.kitSettingsItemLore);
			contents[KIT_POS].setItemMeta(im);
		}
		
		contents[WS_POS] = new ItemStack(Material.getMaterial(Items.waitingSnakeSettingsItemMaterial));
		{
			ItemMeta im = contents[WS_POS].getItemMeta();
			im.setDisplayName(Items.waitingSnakeSettingsItemName);
			im.setLore(Items.waitingSnakeSettingsItemLore);
			contents[WS_POS].setItemMeta(im);
		}
		
		ItemStack tour = new ItemStack(Material.getMaterial(Items.tournamentSettingsItemMaterial));
		{
			ItemMeta im = tour.getItemMeta();
			im.setDisplayName(Items.tournamentSettingsItemName);
			im.setLore(Items.tournamentSettingsItemLore);
			tour.setItemMeta(im);
		}
		contents[TOUR_POS] = tour;
		
		ItemStack chat = new ItemStack(Material.BOOK_AND_QUILL);
		{
			ItemMeta im = chat.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "Chat-Einstellungen");
			ArrayList<String> lore = new ArrayList<>();
			lore.add(ChatColor.GRAY + "Coming Soon...");
			im.setLore(lore);
			chat.setItemMeta(im);
		}
		contents[CHAT_POS] = chat;
		
		
		inventory.setContents(contents);
	}
	
	public MapMenu getMapMenu()
	{
		return mapMenu;
	}
	
	public TourSettingMenu getTourSettingMenu()
	{
		return tourSM;
	}
	
	public TeamMenu getTeamMenu()
	{
		return teamMenu;
	}
	
	public KitMainMenu getKitMainMenu()
	{
		return kitMainMenu;
	}
	
	public WSSettingMenu getWSMenu()
	{
		return wsSettings;
	}
	
	@Override
	public void onClick(int slot,ClickType ct)
	{
		if(slot == MAP_POS)
		{
			if(ct.isLeftClick())
			{
				mapMenu.open();
			}
		}
		else if(slot == TEAM_POS && ct.isLeftClick())
		{
			if(teamMenu != null)
			{
				Team t = TeamManager.getTeam(owner);
				if(t == null)
				{
					owner.sendMessage(Messages.needTeam);
				}
				else
				{
					teamMenu.open();
				}
			}
			else
			{
				owner.sendMessage(Messages.needTeam);
			}
		}
		else if(slot == CLOSE_POS && ct.isLeftClick())
		{
			close();
		}
		else if(slot == KIT_POS && ct.isLeftClick())
		{
			kitMainMenu.open();
		}
		else if(slot == WS_POS && ct.isLeftClick())
		{
			wsSettings.open();
		}
		else if(slot == TOUR_POS && ct.isLeftClick())
		{
			tourSM.open();
		}
	}
}
