package at.Kingcraft.OnevsOne_lobby.Special;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import at.Kingcraft.OnevsOne_lobby.Tournaments.Tournament;
import at.Kingcraft.OnevsOne_lobby.Tournaments.TournamentManager;

public class TournamentViewMenu extends Menu
{	
	private static final int MAX_PLAYERS = 36;
	private static final int CONFIG_POS = 45;
	private static final int PLUS_POS = 53;
	private static final int MINUS_POS = 52;
	private int offset = 0;
	private Player viewedLeader = null;
	
	public TournamentViewMenu(Player owner) {
		super(owner, 54, "Turnier", null);
	}
	
	@Override
	protected void setInventoryContents()
	{
		ItemStack wall = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)15);
		{
			ItemMeta im = wall.getItemMeta();
			im.setDisplayName(" ");
			wall.setItemMeta(im);
		}
		
		ItemStack white = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)0);
		{
			ItemMeta im = white.getItemMeta();
			im.setDisplayName(" ");
			white.setItemMeta(im);
		}
		
		for(int i = 45;i<54;i++)
		{
			inventory.setItem(i, wall);
		}
		
		for(int i = 36;i<45;i++)
		{
			inventory.setItem(i, white);
		}
		
		ItemStack plus = new ItemStack(Material.WOOD_BUTTON);
		{
			ItemMeta im = plus.getItemMeta();
			im.setDisplayName("+");
			plus.setItemMeta(im);
		}
		
		ItemStack minus = new ItemStack(Material.WOOD_BUTTON);
		{
			ItemMeta im = minus.getItemMeta();
			im.setDisplayName("-");
			minus.setItemMeta(im);
		}
		
		inventory.setItem(PLUS_POS,plus);
		inventory.setItem(MINUS_POS,minus);
		
	}
	
	public void loadTournament(Player leader)
	{
		viewedLeader = leader;
		Tournament t = TournamentManager.getTournament(leader);
		TourSettingMenu tsm = MenuManager.getSettingMenu(leader).getTourSettingMenu();
		
		if(offset > t.getContestants().size()-1)
		{
			offset -= MAX_PLAYERS;
		}
		
		for(int i = offset;i<t.getContestants().size() && i-offset < MAX_PLAYERS;i++)
		{
			ItemStack is = t.contestantToItemStack(i);
			if(inventory.getItem(i) == null || inventory.getItem(i).getType().equals(Material.AIR) || !((SkullMeta)inventory.getItem(i).getItemMeta()).getOwner().equals(((SkullMeta)is.getItemMeta()).getOwner()))
				inventory.setItem(i-offset, is);
		}
		
		for(int i = t.getContestants().size()-offset;i<MAX_PLAYERS;i++)
		{
			inventory.setItem(i, new ItemStack(Material.AIR));
		}
		
		
		inventory.setItem(CONFIG_POS, tsm.settingsToItemStack());
	}
	
	public Player getViewedLeader()
	{
		return viewedLeader;
	}

	@Override
	public void onClick(int slot, ClickType ct)
	{
		if(slot == PLUS_POS && ct.isLeftClick())
		{
			offset += MAX_PLAYERS;
			
			loadTournament(viewedLeader);
		}
		else if(slot == MINUS_POS && ct.isLeftClick())
		{
			offset -= MAX_PLAYERS;
			if(offset < 0)
			{
				offset = 0;
			}
			
			loadTournament(viewedLeader);
		}
		else if(slot == CONFIG_POS && ct.isLeftClick())
		{
			Tournament t = TournamentManager.getTournament(owner);
			if(t==null)
			{
				owner.sendMessage(Messages.noTournament);
				return;
			}
			
			if(!t.getLeader().getUniqueId().equals(owner.getUniqueId()))
			{
				owner.sendMessage(Messages.onlyLeader);
				return;
			}
			
			MenuManager.getSettingMenu(owner).getTourSettingMenu().open();
		}
	}

}
