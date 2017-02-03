package at.Kingcraft.OnevsOne_lobby.Kits;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import MySQL.MySQL;
import at.Kingcraft.OnevsOne_lobby.Kits.KitItem;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import at.Kingcraft.OnevsOne_lobby.Scoreboard.MyScoreboardManager;
import net.md_5.bungee.api.ChatColor;

public class Kit
{
	private ArrayList<KitItem> items;
	private ArrayList<KitSettings> settings;
	private Player owner;
	private final int number;
	private String oldItemsInString;
	private String name;
	private int symbol;
	private boolean dif;
	private int id;
	private String ownerName = "Server";
	
	public Kit(Player owner,int number,boolean dif)
	{
		items = new ArrayList<>();
		settings = new ArrayList<>();
		this.owner = owner;
		this.number = number;
		oldItemsInString = "";
		name = "Kit";
		symbol = 0;
		this.dif = dif;
		if(owner!=null)
		ownerName = owner.getName();
		
		setID();
	}
	
	public int getID()
	{
		return id;
	}
	
	public String getOwnerName()
	{
		return owner == null ? ownerName : ownerName.equals(owner.getName()) ? owner.getDisplayName() : ownerName;
	}
	
	private void setID()
	{
		boolean possible = true;
		Random rand = new Random();
		
		do 
		{
			id = rand.nextInt(Integer.MAX_VALUE);
			for(int i = 0;i<KitManager.IDs.size();i++)
			{
				if(KitManager.IDs.get(i) == id)
				{
					possible = false;
					break;
				}	
			}
		}while(!possible);
		
		KitManager.IDs.add(id);
	}
	
	public ArrayList<KitSettings> getSettings()
	{
		return settings;
	}
	
	public boolean isDif()
	{
		return dif;
	}
	
	public String getKitSettings()
	{
		String Settings = "";
		
		for(int i = 0;i<getSettings().size();i++)
		{
			Settings += Messages.kitSetting(getSettings().get(i).getName()) + (i==getSettings().size()-1 ? "" : Messages.kitSettingSeparator);
		}
		
		return Settings;
	}
	
	private int settingsToInt()
	{
		int setI = 0;
		
		for(KitSettings ks : KitSettings.values())
		{
			if(settings.contains(ks))
			{
				setI |= ks.getBit();
			}
		}
		
		return setI;
	}
	
	public void setSettings(int i)
	{
		for(KitSettings ks : KitSettings.values())
		{
			if((ks.getBit() & i) == ks.getBit())
			{
				settings.add(ks);
			}
		}
	}
	
	public void toggleSetting(KitSettings ks)
	{
		if(settings.contains(ks))
		{
			settings.remove(ks);
		}
		else
		{
			settings.add(ks);
		}
	}
	
	public void setName(String name)
	{
		boolean mustUpdateSB = this.name != null && !this.name.equals(name);
		this.name = name;
		
		if(owner != null && !isDif() && mustUpdateSB && id == KitManager.getChoosenKitKit(owner).getID())
		{
			MyScoreboardManager.updateScoreboard(owner);
		}
		if(isDif() && mustUpdateSB)
		{
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(KitManager.getChoosenKitKit(p).getID() == id)
				{
					MyScoreboardManager.updateScoreboard(p);
				}
			}
		}
	}
	
	public void onlySetName(String name)
	{
		this.name = name;
	}
	
	public String getName(boolean withOwner,boolean onlyNumber,boolean white)
	{
		String str = "";
		
		if(white)
		{
			str += ChatColor.WHITE + "";
		}
		
		if(withOwner)
		{
			str += getOwnerName() + ":";
		}
		
		if(onlyNumber)
		{
			str += "" + number;
		}
		else
		{
			str += name;
		}
		
		return  str;
	}
	
	public ArrayList<KitItem> getItems()
	{
		return items;
	}
	
	public ItemStack getSymbol(boolean withSettings)
	{
		
		if(symbol > items.size()-1)
		{
			symbol = items.size()-1;
		}
		else if(symbol < 0)
		{
			symbol = 0;
		}
		
		ItemStack rv;
		
		if(items.size() == 0 || items.get(symbol) == null || items.get(symbol).getItem().getType().equals(Material.AIR))
		{
			rv = new ItemStack(Material.WOOD_SWORD);
		}
		else
		{
			rv = items.get(symbol).getItem().clone();
		}
		
		ItemMeta im = rv.getItemMeta();
		im.setDisplayName(ChatColor.WHITE + name);
		
		
		// Settings
		if(withSettings)
		{
			ArrayList<String> lore = new ArrayList<>();
			
			for(int i = 0;i<settings.size();i++)
			{
				lore.add(ChatColor.YELLOW + "- " + ChatColor.BLUE + settings.get(i).getName());
			}
			
			im.setLore(lore);
		}
		
		rv.setItemMeta(im);
		
		return rv;
	}
	
	public void addSymbol(int symbol)
	{
		this.symbol += symbol;
		if(this.symbol > items.size()-1)
		{
			this.symbol = this.symbol-items.size();
		}
		else if(this.symbol < 0)
		{
			this.symbol = items.size()+this.symbol;
		}
		if(this.symbol < 0)
		{
			this.symbol = 0;
		}
	}
	
	public void addItem(int slot,ItemStack is)
	{
		items.add(new KitItem(slot,is,false));
	}
	
	public void addEquipment(int slot,ItemStack is)
	{
		is.setAmount(1);
		items.add(new KitItem(slot,is,true));
	}
	
	public void setOldItems(String str)
	{
		oldItemsInString = str;
	}
	
	public void getItemsFromInventory(Player p)
	{
		items.clear();
		for(int i = 0;i<p.getInventory().getSize();i++)
		{
			if(p.getInventory().getItem(i) != null && !p.getInventory().getItem(i).getType().equals(Material.AIR))
			{
				addItem(i,p.getInventory().getItem(i));
			}
		}
		if(p.getEquipment().getHelmet() != null)
		{
			addEquipment(0, p.getEquipment().getHelmet());
		}
		if(p.getEquipment().getChestplate() != null)
		{
			addEquipment(1, p.getEquipment().getChestplate());
		}
		if(p.getEquipment().getLeggings() != null)
		{
			addEquipment(2, p.getEquipment().getLeggings());
		}
		if(p.getEquipment().getBoots() != null)
		{
			addEquipment(3, p.getEquipment().getBoots());
		}
	}
	
	public void loadToMySQL(MySQL mysql)
	{
		
		if(!mysql.isConnected() || oldItemsInString.equals(itemsToString()))
			return;
		
		
		System.out.println("Updated " + name + " from " + owner.getDisplayName());
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT UUID FROM Duel_Kits WHERE UUID = ?");
			ps.setString(1, owner.getUniqueId().toString());
			
			ResultSet rs = ps.executeQuery();
			
			if(!rs.first())
			{
				ps = mysql.getConnection().prepareStatement("INSERT INTO Duel_Kits (UUID,Kit1,Kit2,Kit3,Kit4,Kit5" + /*","+ "SoupKit1,SoupKit2,SoupKit3,SoupKit4,SoupKit5,"+ "DifKit,ChoosenKit" +*/") VALUES (?,?,?,?,?,?"  + /*",?,?"+ ",?,?,?,?,?"+*/")");
				ps.setString(1, owner.getUniqueId().toString());
				for(int i = 2;i<=/*11*/6;i++)
				{
					if(i == number+1)
					{
						ps.setString(i, itemsToString());
					}
					else
					{
						ps.setString(i, "null");
					}
				}
				
				/*ps.setInt(12, 1);
				ps.setInt(13, KitManager.getChoosenKit(owner).myNumber);*/
				
				ps.executeUpdate();
			}
			else
			{
				ps.close();
				String query;
				if(number < 6)
				{
					query =  "UPDATE Duel_Kits SET Kit" + number + " = '" + itemsToString() + "' WHERE UUID = '" + owner.getUniqueId().toString() + "'";
				}
				else
				{
					query = "";
				}
				/*else
				{
					query =  "UPDATE Duel_Kits SET SoupKit" + (number-5) + " = '" + itemsToString() + "' WHERE UUID = '" + owner.getUniqueId().toString() + "'";
				}*/
				
				ps = mysql.getConnection().prepareStatement(query);
				
				ps.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		oldItemsInString = itemsToString();
		
	}
	
	public void needsUpdate()
	{
		oldItemsInString = "difugb";
	}
	
	public void kitItemsToInventory(Player p)
	{
		p.getInventory().clear();
		//Equipment
		p.getEquipment().setHelmet(new ItemStack(Material.AIR));
		p.getEquipment().setChestplate(new ItemStack(Material.AIR));
		p.getEquipment().setLeggings(new ItemStack(Material.AIR));
		p.getEquipment().setBoots(new ItemStack(Material.AIR));
		
		for(int i = 0;i<items.size();i++)
		{
			if(items.get(i).isEquipment())
			{
				switch(items.get(i).getSlot())
				{
				case 0:
					p.getEquipment().setHelmet(items.get(i).getItem());
					break;
				case 1:
					p.getEquipment().setChestplate(items.get(i).getItem());
					break;
				case 2:
					p.getEquipment().setLeggings(items.get(i).getItem());
					break;
				case 3:
					p.getEquipment().setBoots(items.get(i).getItem());
					break;
				}
			}
			else
			{
				p.getInventory().setItem(items.get(i).getSlot(), items.get(i).getItem());
			}
			
		}
	}
	
	private String getColorOfLeather(ItemStack is)
	{
		if(is.getType().equals(Material.LEATHER_HELMET) || is.getType().equals(Material.LEATHER_CHESTPLATE) ||
		   is.getType().equals(Material.LEATHER_LEGGINGS) || is.getType().equals(Material.LEATHER_BOOTS))
		{
			LeatherArmorMeta lam = (LeatherArmorMeta) is.getItemMeta();
			return String.valueOf(lam.getColor().asRGB());
		}
		else
		{
			return "";
		}
	}
	
	public int getNumber()
	{
		return number;
	}
	
	public void setOwnerName(String str)
	{
		ownerName = str;
	}
	
	@SuppressWarnings("deprecation")
	public String itemsToString()
	{
		
		String strItems = "";
		
		strItems += name + "\n";
		
		strItems += ownerName + "\n";
		
		strItems += String.valueOf(symbol) + "\n";
		
		strItems += number + "\n";
		
		strItems += String.valueOf(settingsToInt()) + "\n";
		
		for(int i = 0;i<items.size();i++)
		{
			ItemStack is = items.get(i).getItem();
			
			strItems += is.getType().getId() + "(" + (items.get(i).isEquipment() ? "e" : "") + "#" + getColorOfLeather(is) + "#" + items.get(i).getSlot() + ";" + is.getAmount() + ";" + is.getDurability() + "#";
			
			Map<Enchantment,Integer> mp = !(is.getType().getId() == 403) ? is.getEnchantments() : ((EnchantmentStorageMeta)is.getItemMeta()).getStoredEnchants();
			
			Iterator<Map.Entry<Enchantment, Integer>> it = mp.entrySet().iterator();
			
			while(it.hasNext())
			{
				Map.Entry<Enchantment,Integer> pair = (Map.Entry<Enchantment, Integer>)it.next();
				strItems += pair.getKey().getId() + ";" + pair.getValue() + "|";
			}
			
			strItems += "#";
			
			strItems += (!(is.getItemMeta() == null) ? is.getItemMeta().getDisplayName() : "") + ")\n";
			
			
		}
		
		return strItems;
	}
	
	public Player getOwner()
	{
		return owner;
	}
}