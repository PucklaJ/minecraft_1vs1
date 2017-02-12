package at.kingcraft.OnevsOne_arena.Menus;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_arena.Duels.DuelManager;
import at.kingcraft.OnevsOne_arena.Kits.Kit;
import at.kingcraft.OnevsOne_arena.Tournaments.Round;
import at.kingcraft.OnevsOne_arena.Tournaments.TourPlayer;
import at.kingcraft.OnevsOne_arena.Tournaments.Tournament;
import at.kingcraft.OnevsOne_arena.Tournaments.TournamentManager;
import net.md_5.bungee.api.ChatColor;

public class DuelsMenu extends Menu
{
	
	private static final int KO_POS = 10;
	private static final int QUALI_POS  = 1;
	private static final int QUALI_MINUS_POS = 0;
	private static final int QUALI_PLUS_POS = 8;
	private static final int KO_MINUS_POS1 = 9;
	private static final int KO_MINUS_POS2 = 18;
	private static final int KO_PLUS_POS1 = 17;
	private static final int KO_PLUS_POS2 = 26;
	private static final int CONFIG_POS = 27;
	
	private int tourID;
	private ArrayList<Round> rounds;
	private HashMap<Integer,ArrayList<TourPlayer>> roundSkipperQuali;
	private HashMap<Integer,ArrayList<TourPlayer>> roundSkipperKo;
	private ArrayList<ItemStack> qualiItems;
	private ArrayList<ItemStack> koItems;
	private int qualiOffset = 0;
	private int koOffset = 0;
	private TeleportMenu teleMenu;
	private int highestRound = 0;
	private boolean highestRoundIsQuali = true;
	private boolean spectator;
	private Tournament tournament;
	private Kit kit;
	
	public DuelsMenu(Player owner,int id,boolean spectator)
	{
		super(owner,36,"Runden",null);
		tourID = id;
		this.spectator = spectator;
		rounds = new ArrayList<>();
		qualiItems = new ArrayList<>();
		koItems = new ArrayList<>();
		
		if(spectator)
			teleMenu = new TeleportMenu(owner, this);
		
		setupConfig();
	}
	
	public TeleportMenu getTeleMenu()
	{
		return teleMenu;
	}
	
	private void setupConfig()
	{
		ItemStack config = new ItemStack(Material.REDSTONE_COMPARATOR);
		{
			Duel d = DuelManager.getFirstDuel();
			tournament = TournamentManager.getTournamentFromMySQL(null, tourID, "pvp-1", d == null ? null : d.getKit(), d == null ? 0 : d.getMaxRounds(), d == null ? 0 : d.getMaxTime());
			if(d!=null)
				kit = d.getKit();
			
			ItemMeta im = config.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Konfigurationen");
			
			String kitName,time,rounds;
			if(tournament == null)
			{
				System.out.println("Tournament is null");
				kitName = "null";
				time = "0";
				rounds = "0";
			}
			else
			{
				time = tournament.getTime() + "";
				rounds = tournament.getMaxRounds()+"";
				
				if(tournament.getKitMode() == Duel.ENEMY_KITS)
				{
					kitName = "Kit der Gegner";
				}
				else
				{
					if(kit != null)
					{
						kitName = kit.getName(true, !kit.getOwnerName().equals("Server"));
					}
					else
					{
						System.out.println("Kit is null");
						kitName = "null";
					}
				}
			}
			
			ArrayList<String> lore = new ArrayList<>();
			lore.add("");
			lore.add(ChatColor.YELLOW + "Kit: " + ChatColor.BLUE + kitName);
			lore.add(ChatColor.YELLOW + "Zeit: " + ChatColor.BLUE + time  + ChatColor.YELLOW + " Minuten");
			lore.add(ChatColor.YELLOW + "Runden: " + ChatColor.BLUE + rounds);
			
			im.setLore(lore);
			config.setItemMeta(im);
		}
		
		inventory.setItem(CONFIG_POS, config);
	}
	
	@Override
	protected void setInventoryContents()
	{
		
		ItemStack prev = new ItemStack(Material.WOOD_BUTTON);
		{
			ItemMeta im = prev.getItemMeta();
			im.setDisplayName("-");
			prev.setItemMeta(im);
		}
		ItemStack next = new ItemStack(Material.WOOD_BUTTON);
		{
			ItemMeta im = next.getItemMeta();
			im.setDisplayName("+");
			next.setItemMeta(im);
		}
		
		
		inventory.setItem(QUALI_MINUS_POS, prev);
		inventory.setItem(QUALI_PLUS_POS, next);
		inventory.setItem(KO_MINUS_POS1, prev);
		inventory.setItem(KO_MINUS_POS2, prev);
		inventory.setItem(KO_PLUS_POS1, next);
		inventory.setItem(KO_PLUS_POS2, next);
	}
	
	private boolean isQualiClick(int slot)
	{
		if(slot >= QUALI_POS && slot < QUALI_POS+7)
			return true;
		
		return false;
	}
	
	private boolean isKoClick(int slot)
	{
		if(slot >= KO_POS && slot < KO_POS+7)
			return true;
		else if(slot >= KO_POS+9 && slot < KO_POS+9+7)
			return true;
		
		return false;
	}
	
	private int getRoundLevelFromItemStack(ItemStack is,boolean quali)
	{
		if(is == null || is.getType().equals(Material.AIR))
			return -1;
		
		String name = "";
		ItemMeta im = is.getItemMeta();
		name = im.getDisplayName();
		
		if(quali)
		{
			return Integer.valueOf(name.replaceAll(ChatColor.YELLOW + "Qualifikation ", ""));
		}
		else
		{
			return (Integer.valueOf(name.replaceAll(ChatColor.YELLOW + "Runde ", ""))-1);
		}
	}
	
	private ArrayList<Round> getQualiRounds(ArrayList<Round> rounds,int level)
	{
		ArrayList<Round> qualiRounds = new ArrayList<>();
		
		for(int i = 0;i<rounds.size();i++)
		{
			if(rounds.get(i).getRoundLevel() == -1 && rounds.get(i).getQualiRoundLevel() == level)
				qualiRounds.add(rounds.get(i));
		}
		
		return qualiRounds;
	}
	
	private ArrayList<Round> getKoRounds(ArrayList<Round> rounds,int level)
	{
		ArrayList<Round> koRounds = new ArrayList<>();
		
		for(int i = 0;i<rounds.size();i++)
		{
			if(rounds.get(i).getRoundLevel() != -1 && rounds.get(i).getRoundLevel() == level)
				koRounds.add(rounds.get(i));
		}
		
		return koRounds;
	}

	@Override
	public void onClick(int slot, ClickType ct)
	{
		if(ct.isLeftClick())
		{
			if(slot == QUALI_PLUS_POS)
			{
				qualiOffset+=7;
				setQualiItems();
			}
			else if(slot == QUALI_MINUS_POS)
			{
				qualiOffset-=7;
				setQualiItems();
			}
			else if(slot == KO_PLUS_POS1 || slot == KO_PLUS_POS2)
			{
				koOffset += 14;
				setKoItems();
			}
			else if(slot == KO_MINUS_POS1 || slot == KO_MINUS_POS2)
			{
				koOffset -= 14;
				setKoItems();
			}
			else if(isQualiClick(slot))
			{
				if(spectator && highestRoundIsQuali && getRoundLevelFromItemStack(inventory.getItem(slot), true) == highestRound)
				{
					teleMenu.setRounds(getQualiRounds(rounds, highestRound));
					teleMenu.open();
				}
			}
			else if(isKoClick(slot))
			{
				if(spectator && !highestRoundIsQuali && getRoundLevelFromItemStack(inventory.getItem(slot), false) == highestRound)
				{
					teleMenu.setRounds(getKoRounds(rounds, highestRound));
					teleMenu.open();
				}
			}
		}
		
	}
	
	private void setQualiItems()
	{
		if(qualiOffset > qualiItems.size()-1)
		{
			qualiOffset-=7;
		}
		if(qualiOffset < 0)
		{
			qualiOffset = 0;
		}
		
		for(int i = QUALI_POS;i<7+QUALI_POS;i++)
		{
			inventory.setItem(i, new ItemStack(Material.AIR));
		}
		
		for(int i = qualiOffset;i<qualiItems.size()&&(i-qualiOffset)<7;i++)
		{
			inventory.setItem(QUALI_POS+i-qualiOffset, qualiItems.get(i));
		}
	}
	
	private void setKoItems()
	{
		if(koOffset > qualiItems.size()-1)
		{
			koOffset-=14;
		}
		if(koOffset < 0)
		{
			koOffset = 0;
		}
		
		for(int i = KO_POS;i<7+KO_POS;i++)
		{
			inventory.setItem(i, new ItemStack(Material.AIR));
		}
		for(int i = KO_POS+9;i<KO_POS+9+7;i++)
		{
			inventory.setItem(i, new ItemStack(Material.AIR));
		}
		
		for(int i = koOffset;i<koItems.size()&&(i-koOffset)<7;i++)
		{
			inventory.setItem(KO_POS+i-qualiOffset, koItems.get(i));
		}
		for(int i = koOffset+14;i<koItems.size()&&(i-(koOffset+14))<7;i++)
		{
			inventory.setItem(KO_POS+9+i-(koOffset+14), koItems.get(i));
		}
	}
	
	private void loadRounds()
	{
		Tournament t = TournamentManager.getTournamentFromMySQL(null, tourID, "null", null, 0, 0);
		
		if(t != null)
		{
			rounds.clear();
			
			for(int i = 0;i<t.getAllRounds().size();i++)
			{
				rounds.add(t.getAllRounds().get(i));
			}
		}
		
		roundSkipperQuali = t.getRoundSkipper(true);
		roundSkipperKo = t.getRoundSkipper(false);
	}
	
	public static String toString(ArrayList<TourPlayer> tp,ChatColor nameColor)
	{
		String str = "";
		
		for(int i = 0;i<tp.size();i++)
		{
			str += nameColor + tp.get(i).name + (i+1==tp.size() ? "" : (ChatColor.WHITE + ", "));
		}
		
		return str;
	}
	
	public static String toString(ArrayList<TourPlayer> tp,String loser)
	{
		String loserString = "";
		for(int i = 0;i<tp.size();i++)
		{
			loserString += tp.get(i).name + (i+1==tp.size() ? "" : ";");
		}
		
		boolean isLoser = loser.equals(loserString);
		
		String str = "";
		
		for(int i = 0;i<tp.size();i++)
		{
			str += (isLoser ? ChatColor.RED : ChatColor.GREEN) + tp.get(i).name + (i+1==tp.size() ? "" : (ChatColor.WHITE + ", "));
		}
		
		return str;
	}
	
	
	private ItemStack roundToItemStack(ArrayList<Round> rounds,boolean quali,int level)
	{
		ItemStack round = new ItemStack(quali ? Material.SLIME_BALL : Material.SNOW_BALL,quali ? level : (level+1));
		{
			ItemMeta im = round.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + ((quali ? "Qualifikation " : "Runde ") + (quali ? level : (level+1))));
			
			ArrayList<String> lore = new ArrayList<>();
			
			for(int i = 0;i<rounds.size();i++)
			{
				String loser = rounds.get(i).getLoser();
				
				String text = "";
				
				if(loser.equals("NO_LOSER"))
				{
					text = toString(rounds.get(i).getP1(),ChatColor.YELLOW) + " " + ChatColor.WHITE + "vs. " + toString(rounds.get(i).getP2(),ChatColor.YELLOW);
				}
				else if(loser.equals("BOTH"))
				{
					text = toString(rounds.get(i).getP1(),ChatColor.RED) + " " + ChatColor.WHITE + "vs. " + toString(rounds.get(i).getP2(),ChatColor.RED);
				}
				else
				{
					text = toString(rounds.get(i).getP1(),loser) + " " + ChatColor.WHITE + "vs. " + toString(rounds.get(i).getP2(),loser);
				}
				
				lore.add(text);
			}
			
			HashMap<Integer,ArrayList<TourPlayer>> rs;
			if(quali)
			{
				rs = roundSkipperQuali;
			}
			else
			{
				rs = roundSkipperKo;
			}
			
			if(rs.get(level) != null)
			{
				ArrayList<TourPlayer> roundS = rs.get(level);
				
				lore.add(toString(roundS,ChatColor.GREEN) + " " + ChatColor.WHITE + "vs. " + ChatColor.RED + "-");
			}
			
			im.setLore(lore);
			
			round.setItemMeta(im);
		}
		
		return round;
	}
	
	private void setQuali()
	{
		ArrayList<Round> qualiRounds = new ArrayList<>();
		
		for(int i = 0;i<rounds.size();i++)
		{
			if(rounds.get(i).getRoundLevel() == -1)
				qualiRounds.add(rounds.get(i));
		}
		
		HashMap<Integer,ArrayList<Round>> qualiRoundsLevel = new HashMap<>();
		
		for(int i = 0;i<qualiRounds.size();i++)
		{
			if(qualiRoundsLevel.get(qualiRounds.get(i).getQualiRoundLevel()) == null)
			{
				ArrayList<Round> temp = new ArrayList<>();
				temp.add(qualiRounds.get(i));
				qualiRoundsLevel.put(qualiRounds.get(i).getQualiRoundLevel(), temp);
			}
			else
			{
				qualiRoundsLevel.get(qualiRounds.get(i).getQualiRoundLevel()).add(qualiRounds.get(i));
			}
		}
		
		ArrayList<ItemStack> qualiRoundsItems = new ArrayList<>();
		
		for(int i = 1;true;i++)
		{
			ArrayList<Round> tempRounds = qualiRoundsLevel.get(i);
			if(tempRounds == null)
			{
				break;
			}
			
			highestRound = i;
				
			qualiRoundsItems.add(roundToItemStack(tempRounds, true, i));
		}
		
		qualiItems = qualiRoundsItems;
		
		setQualiItems();
	}
	
	private void setKO()
	{
		ArrayList<Round> koRounds = new ArrayList<>();
		
		for(int i = 0;i<rounds.size();i++)
		{
			if(rounds.get(i).getRoundLevel() != -1)
			{
				koRounds.add(rounds.get(i));
				highestRoundIsQuali = false;
			}	
		}
		
		HashMap<Integer,ArrayList<Round>> koRoundsLevel = new HashMap<>();
		
		for(int i = 0;i<koRounds.size();i++)
		{
			if(koRoundsLevel.get(koRounds.get(i).getRoundLevel()) == null)
			{
				ArrayList<Round> temp = new ArrayList<>();
				temp.add(koRounds.get(i));
				koRoundsLevel.put(koRounds.get(i).getRoundLevel(), temp);
			}
			else
			{
				koRoundsLevel.get(koRounds.get(i).getRoundLevel()).add(koRounds.get(i));
			}
		}
		
		ArrayList<ItemStack> koRoundsItems = new ArrayList<>();
		
		for(int i = 0;true;i++)
		{
			ArrayList<Round> tempRounds = koRoundsLevel.get(i);
			if(tempRounds == null)
				break;
			
			highestRound = i;
			
			koRoundsItems.add(roundToItemStack(tempRounds, false, i));
		}
		
		koItems = koRoundsItems;
		
		setKoItems();
	}
	
	@Override
	public void open()
	{
		update();
		super.open();
	}
	
	private void update()
	{
		loadRounds();
		setQuali();
		setKO();
	}
}
