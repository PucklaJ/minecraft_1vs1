package at.Kingcraft.OnevsOne_lobby.Special;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Items.Items;
import at.Kingcraft.OnevsOne_lobby.Kits.Kit;
import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;
import at.Kingcraft.OnevsOne_lobby.Tournaments.Tournament;
import at.Kingcraft.OnevsOne_lobby.Tournaments.TournamentManager;
import net.md_5.bungee.api.ChatColor;

public class TourSettingMenu extends Menu
{
	
	private static final int ROUND_POS = 13;
	private static final int PLAYER_POS = 17;
	private static final int KIT_POS = 11;
	private static final int TIME_POS = 15;
	private static int ROUND_NEXT_POS = ROUND_POS -9;
	private static int ROUND_PREV_POS = ROUND_POS +9;
	private static int PLAYER_NEXT_POS = PLAYER_POS-9;
	private static int PLAYER_PREV_POS = PLAYER_POS+9;
	private static int KIT_NEXT_POS = KIT_POS -9;
	private static int KIT_PREV_POS = KIT_POS +9;
	private static int TIME_NEXT_POS = TIME_POS-9;
	private static int TIME_PREV_POS = TIME_POS+9;
	private static final int BACK_POS = 9;
	private static final int MAX_ROUNDS = 7;
	private static final int MIN_ROUNDS = 1;
	private static final int ROUNDS_STEP = 2;
	private static final int MAX_TIME = 60;
	private static final int MIN_TIME = 5;
	private static final int TIME_STEP = 5;
	public static final int OWN_KITS = 0;
	public static final int ENEMY_KITS = 1;
	private int rounds;
	private int players;
	private int kit;
	private int time;
	
	public static void setup()
	{
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Duel_Tournament_Settings (UUID VARCHAR(100),Settings VARCHAR(100))");
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public TourSettingMenu(Player owner,SettingMenu parent)
	{
		super(owner,27,"Turnier-Einstellungen",parent);
	}
	
	public ItemStack settingsToItemStack()
	{
		ItemStack is = new ItemStack(Material.REDSTONE_COMPARATOR);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.YELLOW + "Konfigurationen");
		ArrayList<String> lore = new ArrayList<>();
		Kit kit = KitManager.getChoosenKitKit(owner);
		String kitName = kit.getName(true,true,false);
		lore.add(ChatColor.YELLOW + "Kit: " + ChatColor.BLUE + (this.kit == ENEMY_KITS ? "Kit der Gegner" : kitName));
		lore.add(ChatColor.YELLOW + "Spieler bis zum Start: " + ChatColor.BLUE + players);
		lore.add(ChatColor.YELLOW + "Maximale Runden: " + ChatColor.BLUE + rounds);
		lore.add(ChatColor.YELLOW + "Rundenzeit: " + ChatColor.BLUE + time);
		im.setLore(lore);
		is.setItemMeta(im);
		
		return is;
	}
	
	private void setTime(int time)
	{
		this.time = time;
		if(time > MAX_TIME)
		{
			this.time = MIN_TIME;
		}
		else if(time < MIN_TIME)
		{
			this.time = MAX_TIME; 
		}
		ItemStack is = new ItemStack(Material.WATCH,this.time);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.YELLOW + "Zeit pro Runde (Min)");
		is.setItemMeta(im);
		
		inventory.setItem(TIME_POS, is);
	}
	
	private void setKit(int mode)
	{
		kit = mode;
		ItemStack is = new ItemStack(Material.PUMPKIN);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(kit == OWN_KITS ? (ChatColor.YELLOW + "Eigenes Kit") : (ChatColor.YELLOW + "Kit der Gegner"));
		is.setItemMeta(im);
		
		inventory.setItem(KIT_POS, is);
	}
	
	@Override
	protected void setInventoryContents()
	{
		
		loadFromMySQL();
		
		ItemStack wall = new ItemStack(Material.getMaterial(Items.menuWallMaterial),1,Items.menuWallDurability);
		{
			ItemMeta im = wall.getItemMeta();
			im.setDisplayName(" ");
			wall.setItemMeta(im);
		}
		
		setInventory(wall);
		
		ItemStack[] contents = inventory.getContents();
		
		ItemStack round = new ItemStack(Material.EYE_OF_ENDER,this.rounds);
		{
			ItemMeta im = round.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "Maximale Runden");
			round.setItemMeta(im);
		}
		
		ItemStack players = new ItemStack(Material.SKULL_ITEM,1,(short)3);
		{
			ItemMeta im = players.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "Spieler bis zum Start: " + ChatColor.BLUE + "" + this.players);
			players.setItemMeta(im);
		}
		
		ItemStack prev = new ItemStack(Material.WOOD_BUTTON);
		{
			ItemMeta im = prev.getItemMeta();
			im.setDisplayName("-");
			prev.setItemMeta(im);
		}
		
		ItemStack prevPlayers = prev.clone();
		{
			ItemMeta im = prevPlayers.getItemMeta();
			im.setDisplayName("- " + this.players);
			prevPlayers.setItemMeta(im);
		}
		
		ItemStack next = new ItemStack(Material.WOOD_BUTTON);
		{
			ItemMeta im = next.getItemMeta();
			im.setDisplayName("+");
			next.setItemMeta(im);
		}
		ItemStack nextPlayers = prev.clone();
		{
			ItemMeta im = nextPlayers.getItemMeta();
			im.setDisplayName("+ " + this.players);
			nextPlayers.setItemMeta(im);
		}
		ItemStack back = new ItemStack(Material.getMaterial(Items.menuBackMaterial),1,Items.menuBackDurability);
		{
			ItemMeta im = back.getItemMeta();
			im.setDisplayName(Items.menuBackName);
			back.setItemMeta(im);
		}
		
		contents[ROUND_POS] = round;
		contents[PLAYER_POS] = players;
		contents[ROUND_PREV_POS] = prev;
		contents[ROUND_NEXT_POS] = next;
		contents[PLAYER_PREV_POS] = prevPlayers;
		contents[PLAYER_NEXT_POS] = nextPlayers;
		contents[KIT_NEXT_POS] = next;
		contents[KIT_PREV_POS] = prev;
		contents[TIME_NEXT_POS] = next;
		contents[TIME_PREV_POS] = prev;
		contents[BACK_POS] = back;
		
		inventory.setContents(contents);
		
		setTime(time);
		setKit(kit);
	}
	
	private void loadFromMySQL()
	{
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT Settings FROM Duel_Tournament_Settings WHERE UUID = ?");
			ps.setString(1, owner.getUniqueId().toString());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				String[] settings = rs.getString(1).split("\n");
				
				kit = Integer.valueOf(settings[0]);
				time = Integer.valueOf(settings[1]);
				rounds = Integer.valueOf(settings[2]);
				players = Integer.valueOf(settings[3]);
				
				return;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		rounds = 3;
		players = 200;
		time = MIN_TIME;
		kit = OWN_KITS;
	}

	public void loadToMySQL()
	{
		String setting = "";
		setting += kit + "\n";
		setting += time + "\n";
		setting += rounds + "\n";
		setting += players + "\n";
		
		try
		{
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT UUID FROM Duel_Tournament_Settings WHERE UUID = ?");
			ps.setString(1, owner.getUniqueId().toString());
			
			if(ps.executeQuery().first())
			{
				ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("UPDATE Duel_Tournament_Settings SET Settings = ? WHERE UUID = ?");
				ps.setString(1, setting);
				ps.setString(2, owner.getUniqueId().toString());
				
				ps.executeUpdate();
			}
			else
			{
				ps.close();
				ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("INSERT INTO Duel_Tournament_Settings (UUID,Settings) VALUES (?,?)");
				ps.setString(1, owner.getUniqueId().toString());
				ps.setString(2, setting);
				
				ps.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void setMaxPlayers(int amount)
	{
		players = amount;
		
		ItemMeta im = inventory.getItem(PLAYER_NEXT_POS).getItemMeta();
		im.setDisplayName("+ " + players);
		inventory.getItem(PLAYER_NEXT_POS).setItemMeta(im);
		
		im = inventory.getItem(PLAYER_PREV_POS).getItemMeta();
		im.setDisplayName("- " + players);
		inventory.getItem(PLAYER_PREV_POS).setItemMeta(im);
		
		im = inventory.getItem(PLAYER_POS).getItemMeta();
		im.setDisplayName(ChatColor.YELLOW + "Spieler bis zum Start: " + ChatColor.BLUE + "" + players);
		inventory.getItem(PLAYER_POS).setItemMeta(im);
	}
	
	private void setRounds(int rounds)
	{
		this.rounds = rounds;
		
		ItemMeta im = inventory.getItem(ROUND_POS).getItemMeta();
		inventory.getItem(ROUND_POS).setItemMeta(im);
		
		inventory.getItem(ROUND_POS).setAmount(rounds);
	}
	
	@Override
	public void onClick(int slot, ClickType ct)
	{
		boolean updateMenus = true;
		
		if(slot == BACK_POS && ct.isLeftClick())
		{
			close();
		}
		else if(slot == PLAYER_NEXT_POS && ct.isLeftClick() && ct.isShiftClick())
		{
			players += 10;
			setMaxPlayers(players);
		}
		else if(slot == PLAYER_PREV_POS && ct.isLeftClick() && ct.isShiftClick())
		{
			players-=10;
			
			if(players < Tournament.PLAYER_SIZE)
			{
				players = Tournament.PLAYER_SIZE;
			}
			
			setMaxPlayers(players);
		}
		else if(slot == PLAYER_NEXT_POS && ct.isLeftClick())
		{
			setMaxPlayers(++players);
		}
		else if(slot == PLAYER_PREV_POS && ct.isLeftClick())
		{
			players--;
			
			if(players < Tournament.PLAYER_SIZE)
			{
				players = Tournament.PLAYER_SIZE;
			}
			
			setMaxPlayers(players);
		}
		else if(slot == ROUND_NEXT_POS && ct.isLeftClick())
		{
			rounds+=ROUNDS_STEP;
			
			if(rounds > MAX_ROUNDS)
			{
				rounds = MIN_ROUNDS;
			}
			
			setRounds(rounds);
		}
		else if(slot == ROUND_PREV_POS && ct.isLeftClick())
		{
			rounds-=ROUNDS_STEP;
			
			if(rounds < MIN_ROUNDS)
			{
				rounds = MAX_ROUNDS;
			}
			
			setRounds(rounds);
		}
		else if(slot == TIME_NEXT_POS && ct.isLeftClick())
		{
			time+=TIME_STEP;
			
			setTime(time);
		}
		else if(slot == TIME_PREV_POS && ct.isLeftClick())
		{
			time -= TIME_STEP;
			setTime(time);
		}
		else if(slot == KIT_NEXT_POS && ct.isLeftClick())
		{
			kit = kit == OWN_KITS ? ENEMY_KITS : OWN_KITS;
			setKit(kit);
		}
		else if(slot == KIT_PREV_POS && ct.isLeftClick())
		{
			kit = kit == OWN_KITS ? ENEMY_KITS : OWN_KITS;
			setKit(kit);
		}
		else
		{
			updateMenus = false;
		}
		
		if(updateMenus)
		{
			Tournament t = TournamentManager.getTournament(owner);
			ArrayList<TournamentViewMenu> menus = MenuManager.getTournamentViewMenus();
			if(t != null && t.getLeader().getUniqueId().equals(owner.getUniqueId()))
			{
				for(int i = 0;i<menus.size();i++)
				{
					if(menus.get(i).isOpen())
					{
						if(menus.get(i).getViewedLeader() != null && menus.get(i).getViewedLeader().getUniqueId().equals(owner.getUniqueId()))
						menus.get(i).loadTournament(owner);
					}
				}
			}
		}
	}
	
	public int getTime()
	{
		return time;
	}
	
	public int getMaxPlayers()
	{
		return players;
	}
	
	public int getRounds()
	{
		return rounds;
	}

	public int getKitMode()
	{
		return kit;
	}

	
}
