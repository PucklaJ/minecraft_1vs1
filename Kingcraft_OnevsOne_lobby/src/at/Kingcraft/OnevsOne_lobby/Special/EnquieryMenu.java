package at.Kingcraft.OnevsOne_lobby.Special;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Duels.ChallangeManager;
import at.Kingcraft.OnevsOne_lobby.Duels.Challenge;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamEnquiry;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamEnquiryManager;
import at.Kingcraft.OnevsOne_lobby.Items.Items;
import at.Kingcraft.OnevsOne_lobby.Tournaments.Tournament;
import at.Kingcraft.OnevsOne_lobby.Tournaments.TournamentManager;

public class EnquieryMenu extends Menu {
	private ArrayList<ItemStack> challenged;
	private ArrayList<ItemStack> challengers;
	private ArrayList<ItemStack> teamSender;
	private ArrayList<ItemStack> teamReciever;
	private ArrayList<ItemStack> startingTour;
	private final int BUTTON_STEP = 3;
	private int challengersOffset = 0;
	private int challengedOffset = 0;
	private int teamSOffset = 0;
	private int teamROffset = 0;
	private int tourOffset = 0;
	private static int CHERS_POS;
	private static int CHED_POS;
	private static int TEAMS_POS;
	private static int TEAMR_POS;
	private static final ItemStack nothing = new ItemStack(Material.AIR);
	public static final int CHERS = 0;
	public static final int CHED = 1;
	public static final int TEAMS = 7;
	public static final int TEAMR = 8;
	public static final int TOUR = 15;
	public static final int NOTHING = 2; 
	public static final int CHERS_NEXT = 3;
	public static final int CHERS_PREV = 4;
	public static final int CHED_NEXT = 5;
	public static final int CHED_PREV = 6;
	public static final int TEAMS_NEXT = 9;
	public static final int TEAMS_PREV = 10;
	public static final int TEAMR_NEXT = 11;
	public static final int TEAMR_PREV = 12;
	public static final int TOUR_NEXT = 13;
	public static final int TOUR_PREV = 14;
	public static int TOUR_POS;
	private boolean teamChanged = false;
	private boolean challChanged = false;
	private boolean tourChanged = false;
	
	public EnquieryMenu(Player p,MainClass plugin)
	{
		super(p,54,plugin.getConfig().getString("Items.Enquiries.Text"),null,plugin);
		
		challenged = new ArrayList<ItemStack>();
		challengers = new ArrayList<ItemStack>();
		teamSender = new ArrayList<ItemStack>();
		teamReciever = new ArrayList<ItemStack>();
		startingTour = new ArrayList<>();
		
		
		CHERS_POS = plugin.getConfig().getInt("Items.Enquiries.ChallOUT.Slot")+2*9;
		CHED_POS = plugin.getConfig().getInt("Items.Enquiries.ChallIN.Slot")+2*9;
		TEAMS_POS = plugin.getConfig().getInt("Items.Enquiries.TeamOUT.Slot")+2*9;
		TEAMR_POS = plugin.getConfig().getInt("Items.Enquiries.TeamIN.Slot")+2*9;
		TOUR_POS = plugin.getConfig().getInt("Items.Enquiries.Tournaments.Slot")+2*9;
		
		setStartingTournaments(TournamentManager.getTournaments());
	}
	
	@Override
	protected void setInventoryContents(MainClass plugin)
	{
		ItemStack[] contents = new ItemStack[9*6];
		
		ItemStack glass = new ItemStack(Material.getMaterial(Items.menuWallMaterial),1,Items.menuWallDurability);
		{
			ItemMeta meta = glass.getItemMeta();
			meta.setDisplayName(" ");
			glass.setItemMeta(meta);
		}
		ItemStack ChallsIn = new ItemStack(Material.getMaterial(plugin.getConfig().getString("Items.Enquiries.ChallIN.Material")));
		{
			ItemMeta meta = ChallsIn.getItemMeta();
			meta.setDisplayName(plugin.getConfig().getString("Items.Enquiries.ChallIN.Text"));
			ChallsIn.setItemMeta(meta);
		}
		ItemStack ChallsOut = new ItemStack(Material.getMaterial(plugin.getConfig().getString("Items.Enquiries.ChallOUT.Material")));
		{
			ItemMeta meta = ChallsOut.getItemMeta();
			meta.setDisplayName(plugin.getConfig().getString("Items.Enquiries.ChallOUT.Text"));
			ChallsOut.setItemMeta(meta);
		}
		ItemStack TeamIn = new ItemStack(Material.getMaterial(plugin.getConfig().getString("Items.Enquiries.TeamIN.Material")));
		{
			ItemMeta meta = TeamIn.getItemMeta();
			meta.setDisplayName(plugin.getConfig().getString("Items.Enquiries.TeamIN.Text"));
			TeamIn.setItemMeta(meta);
		}
		ItemStack TeamOut = new ItemStack(Material.getMaterial(plugin.getConfig().getString("Items.Enquiries.TeamOUT.Material")));
		{
			ItemMeta meta = TeamOut.getItemMeta();
			meta.setDisplayName(plugin.getConfig().getString("Items.Enquiries.TeamOUT.Text"));
			TeamOut.setItemMeta(meta);
		}
		ItemStack Tournaments = new ItemStack(Material.getMaterial(plugin.getConfig().getString("Items.Enquiries.Tournaments.Material")));
		{
			ItemMeta meta = Tournaments.getItemMeta();
			meta.setDisplayName(plugin.getConfig().getString("Items.Enquiries.Tournaments.Text"));
			Tournaments.setItemMeta(meta);
		}
		ItemStack buttonNEXT = new ItemStack(Material.WOOD_BUTTON);
		{
			ItemMeta meta = buttonNEXT.getItemMeta();
			meta.setDisplayName("+");
			buttonNEXT.setItemMeta(meta);
		}
		ItemStack buttonPREV = new ItemStack(Material.WOOD_BUTTON);
		{
			ItemMeta meta = buttonPREV.getItemMeta();
			meta.setDisplayName("-");
			buttonPREV.setItemMeta(meta);
		}
		
		contents[plugin.getConfig().getInt("Items.Enquiries.ChallOUT.Slot")] = ChallsOut;  // First Row
		contents[1] = glass;
		contents[plugin.getConfig().getInt("Items.Enquiries.ChallIN.Slot")] = ChallsIn;
		contents[3] = glass;
		contents[plugin.getConfig().getInt("Items.Enquiries.TeamOUT.Slot")] = TeamOut;
		contents[5] = glass;
		contents[plugin.getConfig().getInt("Items.Enquiries.TeamIN.Slot")] = TeamIn;
		contents[7] = glass;
		contents[plugin.getConfig().getInt("Items.Enquiries.Tournaments.Slot")] = Tournaments;
		contents[9] = buttonPREV; 	// Second Row
		contents[10] = glass;
		contents[11] = buttonPREV;
		contents[12] = glass;
		contents[13] = buttonPREV;
		contents[14] = glass;
		contents[15] = buttonPREV;
		contents[16] = glass;
		contents[17] = buttonPREV;
		contents[18] = new ItemStack(Material.AIR); // Third Row
		contents[19] = glass;
		contents[20] = new ItemStack(Material.AIR);
		contents[21] = glass;
		contents[22] = new ItemStack(Material.AIR);
		contents[23] = glass;
		contents[24] = new ItemStack(Material.AIR);
		contents[25] = glass;
		contents[26] = new ItemStack(Material.AIR);
		contents[27] = new ItemStack(Material.AIR); // Fourth Row
		contents[28] = glass;
		contents[29] = new ItemStack(Material.AIR);
		contents[30] = glass;
		contents[31] = new ItemStack(Material.AIR);
		contents[32] = glass;
		contents[33] = new ItemStack(Material.AIR);
		contents[34] = glass;
		contents[35] = new ItemStack(Material.AIR);
		contents[36] = new ItemStack(Material.AIR); // Fifth Row
		contents[37] = glass;
		contents[38] = new ItemStack(Material.AIR);
		contents[39] = glass;
		contents[40] = new ItemStack(Material.AIR);
		contents[41] = glass;
		contents[42] = new ItemStack(Material.AIR);
		contents[43] = glass;
		contents[44] = new ItemStack(Material.AIR);
		contents[45] = buttonNEXT; // Sixth Row
		contents[46] = glass;
		contents[47] = buttonNEXT;
		contents[48] = glass;
		contents[49] = buttonNEXT;
		contents[50] = glass;
		contents[51] = buttonNEXT;
		contents[52] = glass;
		contents[53] = buttonNEXT;
		
		inventory.setContents(contents);
	}
	
	public void setStartingTournaments(ArrayList<Tournament> tournaments)
	{
		startingTour.clear();
		
		for(int i = 0;i<tournaments.size();i++)
		{
			startingTour.add(tournaments.get(i).toItemStack());
		}
		
		tourChanged = true;
	}
	
	@Override
	public void onClick(int slot,ClickType ct)
	{
		ItemStack is = inventory.getItem(slot);
		if(is == null)
		{
			return;
		}
		if(is.getType() == Material.SKULL_ITEM)
		{
			int whichSkull = getWhichSkull(slot);
			if(whichSkull == CHED)
			{
				if(ct.isLeftClick())
					owner.performCommand("1vs1 " + is.getItemMeta().getDisplayName());
				else if(ct.isRightClick())
					owner.performCommand("refuse " + is.getItemMeta().getDisplayName());
					
			}
			else if(whichSkull == CHERS)
			{
				owner.performCommand("refuse " + is.getItemMeta().getDisplayName());
			}
			else if(whichSkull == TEAMS)
			{
				owner.performCommand("team refuse " + is.getItemMeta().getDisplayName());
			}
			else if(whichSkull == TEAMR)
			{
				if(ct.isLeftClick())
					owner.performCommand("team " + is.getItemMeta().getDisplayName());
				else if(ct.isRightClick())
					owner.performCommand("team refuse " + is.getItemMeta().getDisplayName());
			}
			else if(whichSkull == TOUR)
			{
				if(ct.isLeftClick())
				{
					owner.performCommand("turnier join " + ((SkullMeta)is.getItemMeta()).getOwner());
				}
				else if(ct.isRightClick() && ct.isShiftClick())
				{
					Player p = null;
					for(Player p1 : Bukkit.getOnlinePlayers())
					{
						if(p1.getDisplayName().equals(((SkullMeta)is.getItemMeta()).getOwner()))
						{
							p = p1;
							break;
						}
					}
					
					if(p != null)
					{
						MenuManager.getTournamentViewMenu(owner, true).loadTournament(p);
						MenuManager.getTournamentViewMenu(owner, false).open();
					}
				}
				else if(ct.isRightClick())
				{
					owner.performCommand("turnier leave");
				}
			}
		}
		else if(is.getType() == Material.WOOD_BUTTON)
		{
				int slot1 = getWhichButton(slot);
				
				if(slot1 == CHED_NEXT)
				{
					this.goNEXT(CHED);
				}
				else if(slot1 == CHED_PREV)
				{
					this.goPREV(CHED);
				}
				else if(slot1 == CHERS_NEXT)
				{
					this.goNEXT(CHERS);
				}
				else if(slot1 == CHERS_PREV)
				{
					this.goPREV(CHERS);
				}
				else if(slot1 == TEAMS_NEXT)
				{
					this.goNEXT(TEAMS);
				}
				else if(slot1 == TEAMS_PREV)
				{
					this.goPREV(TEAMS);
				}
				else if(slot1 == TEAMR_NEXT)
				{
					this.goNEXT(TEAMR);
				}
				else if(slot1 == TEAMR_PREV)
				{
					this.goPREV(TEAMR);
				}
				else if(slot1 == TOUR_NEXT)
				{
					this.goNEXT(TOUR);
				}
				else if(slot1 == TOUR_PREV)
				{
					this.goPREV(TOUR);
				}
				
		}
	}
	
	private ItemStack setSkull(ArrayList<Player> other)
	{
		ItemStack is = new ItemStack(Material.SKULL_ITEM,1,(short)3);
		SkullMeta smeta = (SkullMeta)is.getItemMeta();
		
		smeta.setOwner(other.isEmpty() ? "null" : other.get(0).getDisplayName());
		smeta.setDisplayName(other.isEmpty() ? "null" : other.get(0).getDisplayName());
		
		ArrayList<String> lore = new ArrayList<String>();
		
		for(int i = 1;i<other.size();i++)
		{
			lore.add(other.get(i).getDisplayName());
		}
		
		smeta.setLore(lore);
		is.setItemMeta(smeta);
		return is;
	}
	
	public void setChallenges(ArrayList<Challenge> cs)
	{
		challengers.clear();
		challenged.clear();
		for(int u = 0;u<cs.size();u++)
		{
			Challenge c = cs.get(u);
			int role = ChallangeManager.getRole(owner, c);
			
			if(role == Challenge.IS_CHALLANGER)
			{	
				challengers.add(setSkull(c.getChallenged()));
			}
			else if(role == Challenge.IS_CHALLANGED)
			{
				challenged.add(setSkull(c.getChallengers()));
			}
		}
		challChanged = true;
	}

	public void setTeamEnquiries(ArrayList<TeamEnquiry> te)
	{
		teamSender.clear();
		teamReciever.clear();
		for(int i = 0;i<te.size();i++)
		{
			TeamEnquiry tey = te.get(i);
			int role = tey.getRole(owner);
			
			if(role == TeamEnquiryManager.SENDER)
			{
				ArrayList<Player> other = new ArrayList<Player>();
				other.add(tey.getReciever());
				teamSender.add(setSkull(other));
			}
			else if(role == TeamEnquiryManager.RECIEVER)
			{
				if(tey.getPSender() != null)
				{
					ArrayList<Player> other = new ArrayList<Player>();
					other.add(tey.getPSender());
					teamReciever.add(setSkull(other));
				}
				else if(tey.getTSender() != null)
				{
					teamReciever.add(setSkull(tey.getTSender().getPlayers()));
				}
			}
		}
		
		teamChanged = true;
	}
	
	@Override
	public void open()
	{
		updateInventory();
		super.open();
	}
	
	public void goNEXT(int which)
	{
		if(which == CHERS)
		{
			challChanged = true;
			challengersOffset += BUTTON_STEP;
			if(challengersOffset>challengers.size()-1)
			{
				challengersOffset-=BUTTON_STEP;
			}
			else
			{
				updateInventory();
				owner.openInventory(inventory);
			}
			
		}
		else if(which == CHED)
		{
			challChanged = true;
			challengedOffset += BUTTON_STEP;
			if(challengedOffset>challengers.size()-1)
			{
				challengedOffset-=BUTTON_STEP;
			}
			else
			{
				updateInventory();
				owner.openInventory(inventory);
			}
		}
		else if(which == TEAMS)
		{
			teamChanged = true;
			teamSOffset += BUTTON_STEP;
			if(teamSOffset>teamSender.size()-1)
			{
				teamSOffset-=BUTTON_STEP;
			}
			else
			{
				updateInventory();
				owner.openInventory(inventory);
			}
		}
		else if(which == TEAMR)
		{
			teamChanged = true;
			teamROffset += BUTTON_STEP;
			if(teamROffset>teamReciever.size()-1)
			{
				teamROffset-=BUTTON_STEP;
			}
			else
			{
				updateInventory();
				owner.openInventory(inventory);
			}
		}
		else if(which == TOUR)
		{
			tourChanged = true;
			tourOffset += BUTTON_STEP;
			if(tourOffset>startingTour.size()-1)
			{
				tourOffset-=BUTTON_STEP;
			}
			else
			{
				updateInventory();
				owner.openInventory(inventory);
			}
		}
		
		
	}
	public void goPREV(int which)
	{
		if(which == CHERS)
		{
			challChanged = true;
			challengersOffset -= BUTTON_STEP;
			if(challengersOffset < 0)	
			{
				challengersOffset = 0;
			}
			else
			{
				updateInventory();
				owner.openInventory(inventory);
			}
			
		}
		else if(which == CHED)
		{
			challChanged = true;
			challengedOffset -= BUTTON_STEP;
			if(challengedOffset < 0)	
			{
				challengedOffset = 0;
			}
			else
			{
				updateInventory();
				owner.openInventory(inventory);
			}
		
		}
		else if(which == TEAMS)
		{
			teamChanged = true;
			teamSOffset -= BUTTON_STEP;
			if(teamSOffset<0)
			{
				teamSOffset=0;
			}
			else
			{
				updateInventory();
				owner.openInventory(inventory);
			}
		}
		else if(which == TEAMR)
		{
			teamChanged = true;
			teamROffset -= BUTTON_STEP;
			if(teamROffset<0)
			{
				teamROffset=0;
			}
			else
			{
				updateInventory();
				owner.openInventory(inventory);
			}
		}
		else if(which == TOUR)
		{
			tourChanged = true;
			tourOffset -= BUTTON_STEP;
			if(tourOffset<0)
			{
				tourOffset=0;
			}
			else
			{
				updateInventory();
				owner.openInventory(inventory);
			}
		}
		
		
		
	}
	
	public void updateInventory()
	{
		// clear Challengers and Challenged Positions
		if(challChanged)
		{
			for(int i = CHERS_POS;i<CHERS_POS+(9*3);i+=9)
			{
				inventory.setItem(i,nothing);
			}
			
			for(int i = CHED_POS;i<CHED_POS+(9*3);i+=9)
			{
				inventory.setItem(i,nothing);
			}
			
			for(int i = challengersOffset;i<challengersOffset+3 && i<challengers.size();i++)
			{
				int slot = (i-challengersOffset)*9+CHERS_POS;
				inventory.setItem(slot, challengers.get(i));
			}
			
			for(int i = challengedOffset;i<challengedOffset+3 && i<challenged.size();i++)
			{
				int slot = (i-challengedOffset)*9+CHED_POS;
				inventory.setItem(slot, challenged.get(i));
			}
			
			challChanged = false;
		}
		
		if(teamChanged)
		{
			// clear TeamS and TeamR Positions
			for(int i = TEAMS_POS;i<TEAMS_POS+(9*3);i+=9)
			{
				inventory.setItem(i,nothing);
			}
			
			for(int i = TEAMR_POS;i<TEAMR_POS+(9*3);i+=9)
			{
				inventory.setItem(i,nothing);
			}
			
			
			for(int i = teamSOffset;i<teamSOffset+3 && i<teamSender.size();i++)
			{
				int slot = (i-teamSOffset)*9+TEAMS_POS;
				inventory.setItem(slot, teamSender.get(i));
			}
			
			for(int i = teamROffset;i<teamROffset+3 && i<teamReciever.size();i++)
			{
				int slot = (i-teamROffset)*9+TEAMR_POS;
				inventory.setItem(slot, teamReciever.get(i));
			}
			
			teamChanged = false;
		}
		
		if(tourChanged)
		{
			// clear TeamS and TeamR Positions
			for(int i = TOUR_POS;i<TOUR_POS+(9*3);i+=9)
			{
				inventory.setItem(i,nothing);
			}
			
			
			for(int i = tourOffset;i<tourOffset+3 && i<startingTour.size();i++)
			{
				int slot = (i-tourOffset)*9+TOUR_POS;
				inventory.setItem(slot, startingTour.get(i));
			}
			
			tourChanged = false;
		}
		
	}
	
	public static int getWhichSkull(int slot)
	{
		if(slot == CHERS_POS ||
		   slot == CHERS_POS+9 ||
		   slot == CHERS_POS+2*9)
		{
			return CHERS;
		}
		else if(slot == CHED_POS ||
				slot == CHED_POS+9 ||
				slot == CHED_POS+2*9)
		{
			return CHED;
		}
		else if(slot == TEAMS_POS ||
				slot == TEAMS_POS+9 ||
				slot == TEAMS_POS+2*9)
		{
			return TEAMS;
		}
		else if(slot == TEAMR_POS ||
				slot == TEAMR_POS+9 ||
				slot == TEAMR_POS+2*9)
		{
			return TEAMR;
		}
		else if(slot == TOUR_POS ||
				slot == TOUR_POS+9 ||
				slot == TOUR_POS+2*9)
		{
			return TOUR;
		}
		
		return NOTHING;
	}
	
	public static int getWhichButton(int slot)
	{
		if(slot == CHERS_POS-9)
		{
			return CHERS_PREV;
		}
		else if(slot == CHERS_POS+3*9)
		{
			return CHERS_NEXT;
		}
		else if(slot == CHED_POS-9)
		{
			return CHED_PREV;
		}
		else if(slot == CHED_POS+3*9)
		{
			return CHED_NEXT;
		}
		else if(slot == TEAMS_POS-9)
		{
			return TEAMS_PREV;
		}
		else if(slot == TEAMS_POS+3*9)
		{
			return TEAMS_NEXT;
		}
		else if(slot == TEAMR_POS-9)
		{
			return TEAMR_PREV;
		}
		else if(slot == TEAMR_POS+3*9)
		{
			return TEAMR_NEXT;
		}
		else if(slot == TOUR_POS+3*9)
		{
			return TOUR_NEXT;
		}
		else if(slot == TOUR_POS-9)
		{
			return TOUR_PREV;
		}
		
		return NOTHING;
	}
}

