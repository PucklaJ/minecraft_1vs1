package at.Kingcraft.OnevsOne_lobby.Special;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import at.Kingcraft.OnevsOne_lobby.Duels.Team;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamManager;
import at.Kingcraft.OnevsOne_lobby.Items.Items;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import net.md_5.bungee.api.ChatColor;

public class TeamMenu extends Menu {
	private static final int PLAYER_POS = 10;
	private static final int PLAYER_NEXT_POS = 17;
	private static final int PLAYER_PREV_POS = 9;
	private static final int BACK_POS = 49;
	private static final int LEAVE_POS = 37;
	private int membersOffset = 0;
	private static final int BUTTON_STEP = 7;
	private ArrayList<ItemStack> teamMembers;
	
	public TeamMenu(Player owner,SettingMenu parent)
	{
		super(owner,54,"Team-Einstellungen",parent);
		teamMembers = new ArrayList<>();
		
		setInventoryContents();
	}
	
	@Override
	protected void setInventoryContents()
	{
		ItemStack[] contents = new ItemStack[inventory.getSize()];
		ItemStack wall = new ItemStack(Material.getMaterial(Items.menuWallMaterial),1,Items.menuWallDurability);
		{
			ItemMeta im = wall.getItemMeta();
			im.setDisplayName(" ");
			wall.setItemMeta(im);
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
		ItemStack backDoor = new ItemStack(Material.getMaterial(Items.menuBackMaterial),1,Items.menuBackDurability);
		{
			ItemMeta im = backDoor.getItemMeta();
			im.setDisplayName(Items.menuBackName);
			backDoor.setItemMeta(im);
		}
		ItemStack leave = new ItemStack(Material.INK_SACK,1,(short)1);
		{
			ItemMeta im = leave.getItemMeta();
			im.setDisplayName(ChatColor.RED + "Team verlassen");
			leave.setItemMeta(im);
		}
		
		for(int i = 0;i<9;i++)
		{
			contents[i] = wall.clone();
		}
		for(int i = 18;i<27;i++)
		{
			contents[i] = wall.clone();
		}
		for(int i = 45;i<BACK_POS;i++)
		{
			contents[i] = wall.clone();
		}
		for(int i = BACK_POS+1;i<54;i++)
		{
			contents[i] = wall.clone();
		}
		contents[27] = wall.clone();
		contents[36] = wall.clone();
		contents[35] = wall.clone();
		contents[44] = wall.clone();
		
		contents[BACK_POS] = backDoor.clone();
		contents[LEAVE_POS] = leave.clone();
		
		contents[PLAYER_NEXT_POS] = nextButton.clone();
		contents[PLAYER_PREV_POS] = prevButton.clone();
		
		inventory.setContents(contents);
	}
	
	public void open()
	{
		owner.openInventory(inventory);
	}
	
	@Override
	public void onClick(int slot,ClickType ct)
	{
		if(slot == PLAYER_NEXT_POS && ct.isLeftClick())
		{
			offsetPlayers("+");
		}
		else if(slot == PLAYER_PREV_POS && ct.isLeftClick())
		{
			offsetPlayers("-");
		}
		else if(slot == BACK_POS && ct.isLeftClick())
		{
			MenuManager.getSettingMenu(owner).open();
		}
		else if(slot == LEAVE_POS && ct.isLeftClick())
		{
			owner.performCommand("team leave");
			MenuManager.getSettingMenu(owner).open();
		}
		else if(isMemberClick(slot))
		{
			if(ct.isShiftClick() && ct.isRightClick())
			{
				String name = inventory.getItem(slot).getItemMeta().getDisplayName();
				name = name.replaceAll(ChatColor.WHITE + "","");
				if(name.contains(ChatColor.YELLOW + "✮"))
				{
					name = name.replace(ChatColor.YELLOW + "✮", "");
				}
				owner.performCommand("team kick " + name);
			}
			else if(ct.isShiftClick() && ct.isLeftClick())
			{
				Team t = TeamManager.getTeam(owner);
				
				if(t != null)
				{
					if(!t.getLeader().getUniqueId().equals(owner.getUniqueId()))
					{
						owner.sendMessage(Messages.onlyLeader);
						return;
					}
					
					String name = inventory.getItem(slot).getItemMeta().getDisplayName();
					name = name.replaceAll(ChatColor.WHITE + "","");
					if(name.contains(ChatColor.YELLOW + "✮"))
					{
						name = name.replace(ChatColor.YELLOW + "✮", "");
					}
					
					if(t.getLeader().getDisplayName().equals(name))
					{
						owner.sendMessage(Messages.youAreAlreadyLeader);
						return;
					}
					
					for(int i = 0;i<t.getPlayers().size();i++)
					{
						if(t.getPlayers().get(i).getDisplayName().equals(name))
						{
							t.setLeader(t.getPlayers().get(i));
							break;
						}
					}
					
					for(int i = 0;i<t.getPlayers().size();i++)
					{
						TeamMenu tm = MenuManager.getSettingMenu(t.getPlayers().get(i)).getTeamMenu();
						tm.clearMembers();
						tm.setMembers(t);
						tm.updateMembers();
					}
				}
			}
		}
	}
	
	private boolean isMemberClick(int slot)
	{
		return slot >= PLAYER_POS && slot < PLAYER_POS+7;
	}
	
	private void offsetPlayers(String mode)
	{
		if(mode.equals("+"))
		{
			membersOffset += BUTTON_STEP;
			if(membersOffset >= teamMembers.size())
			{
				membersOffset -= BUTTON_STEP;
			}
			else
			{
				updateMembers();
			}
		}
		else if(mode.equals("-"))
		{
			membersOffset -= BUTTON_STEP;
			if(membersOffset < 0)
			{
				membersOffset = 0;
			}
			else
			{
				updateMembers();
			}
		}
	}
	
	
	private void clearMembers()
	{
		ItemStack nothing = new ItemStack(Material.AIR);
		
		for(int i = PLAYER_POS;i<PLAYER_POS+7;i++)
		{
			inventory.setItem(i, nothing);
		}
	}
	
	public void setMembers(Team t)
	{
		teamMembers.clear();
		
		for(int i = 0;i<t.getPlayers().size();i++)
		{
			ItemStack is = new ItemStack(Material.SKULL_ITEM,1,(short)3);
			
			SkullMeta sm = (SkullMeta) is.getItemMeta();
			if(t.getPlayers().get(i).getUniqueId().equals(t.getLeader().getUniqueId()))
			{
				sm.setDisplayName(ChatColor.YELLOW + "✮" + ChatColor.WHITE + t.getPlayers().get(i).getDisplayName());
			}
			else
			{
				sm.setDisplayName(ChatColor.WHITE + t.getPlayers().get(i).getDisplayName());
			}
			
			sm.setOwner(t.getPlayers().get(i).getDisplayName());
			ArrayList<String> lore = new ArrayList<>();
			lore.add(ChatColor.YELLOW + "Shift-RECHTS-" + ChatColor.WHITE + "Klick: Spieler rauswerfen");
			lore.add(ChatColor.YELLOW + "Shift-LINKS-" + ChatColor.WHITE + "Klick: Spieler zum Leiter ernennen");
			sm.setLore(lore);
			is.setItemMeta(sm);
			
			teamMembers.add(is);
		}
		
		updateMembers();
	}
	
	private void updateMembers()
	{
		while(membersOffset >= teamMembers.size())
		{
			membersOffset -= BUTTON_STEP;
		}
		
		clearMembers();
		
		for(int i = membersOffset;i<teamMembers.size() && i-membersOffset < 7;i++)
		{
			inventory.setItem(i-membersOffset+PLAYER_POS, teamMembers.get(i));
		}
	}
	

}
