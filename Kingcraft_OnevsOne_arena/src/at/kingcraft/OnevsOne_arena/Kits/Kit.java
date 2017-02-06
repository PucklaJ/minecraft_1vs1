package at.kingcraft.OnevsOne_arena.Kits;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import MySQL.MySQL;
import at.kingcraft.OnevsOne_arena.Messaging.Messages;

public class Kit
{
	private ArrayList<KitItem> items;
	private Player owner;
	boolean needsMySQLUpdate = false;
	private final int number;
	private String itemsInString;
	private String name;
	private int symbol;
	private static MySQL mysql;
	private boolean dif;
	private ArrayList<KitSettings> settings;
	private static HashMap<UUID,Integer> difKitNumbers;
	private String ownerName="Server";
	
	public Kit(Player owner,int number,boolean dif)
	{
		items = new ArrayList<>();
		this.owner = owner;
		this.number = number;
		itemsInString = "";
		name = "Kit" + number;
		symbol = 0;
		this.dif = dif;
		settings = new ArrayList<KitSettings>();
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public ArrayList<KitSettings> getSettings()
	{
		return settings;
	}
	
	public void setDifKit(boolean b)
	{
		dif = b;
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
	
	public String getName(boolean withOwner,boolean onlyNumber)
	{
		String str = "";
		
		if(ownerName.equals("Server"))
		{
			withOwner = false;
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
	
	public boolean isSoup()
	{
		return number > 5 && number < 11;
	}
	
	public ItemStack getSymbol()
	{
		if(items.size() == 0 || items.get(symbol) == null || items.get(symbol).getItem().getType().equals(Material.AIR))
		{
			return new ItemStack(Material.WOOD_SWORD);
		}
		return items.get(symbol).getItem();
	}
	
	public void setSymbol(int symbol)
	{
		this.symbol = symbol;
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
		itemsInString = str;
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
		
		if(!itemsInString.equals(itemsToString()))
		{
			needsMySQLUpdate = true;
		}
		else
		{
			needsMySQLUpdate = false;
		}
	}
	
	public static Kit getDifKit(Player p)
	{
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT DifKit FROM Duel_Kits WHERE UUID = ?");
			ps.setString(1, p.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				int kit;
				
				int cdk = rs.getInt(1);
				
				if((cdk & 1) == 1  && (cdk & 2) == 2)
				{
					kit = 3;
				}
				else if(!((cdk & 1) == 1)  && (cdk & 2) == 2)
				{
					kit = 2;
				}
				else if((cdk & 1) == 1  && !((cdk & 2) == 2))
				{
					kit = 1;
				}
				else
				{
					kit = 1;
				}
				
				setDifKit(p,kit);
				
				switch(kit)
				{
					case 1: return getDifKit(p,1);
					case 2: return getDifKit(p,2);
					case 3: return getDifKit(p,3);
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static int getDifKitNumber(Player p)
	{
		if(difKitNumbers.get(p.getUniqueId()) == null)
		{
			return -1;
		}
		else
		{
			return difKitNumbers.get(p.getUniqueId());
		}
	}
	
	private static void setDifKit(Player p,int i)
	{
		difKitNumbers.put(p.getUniqueId(),i);
	}
	
	public boolean isDifKit()
	{
		return dif;
	}
	
	@SuppressWarnings("deprecation")
	public static Kit decodeMySQLKit(Player p,String str)
	{
		if(str.equals("null"))
		{
			return null;
		}
		else if(str.equals("§diff§"))
		{
			return getDifKit(p);
		}
		String[] kitStr = str.split("\n");
		
		Kit kit = new Kit(p,Integer.valueOf(kitStr[3]),false);
		
		String Name = kitStr[0];
		
		int symbol = Integer.valueOf(kitStr[2]);
		
		kit.setName(Name);
		kit.setSymbol(symbol);
		kit.setSettings(Integer.valueOf(kitStr[4]));
		kit.setOwnerName(kitStr[1]);
		
		for(int i = 5;i<kitStr.length;i++)
		{
			if(kitStr[i].equals("null") || kitStr[i].length() == 0)
				continue;
			
			
			String type = "";
			String amount = "";
			String durability = "";
			ArrayList<String> enchantments = new ArrayList<String>();
			ArrayList<String> enchantmentsInt = new ArrayList<String>();
			String name = "";
			String slot = "";
			boolean isEquip = false;
			String leatherColor = "";
			
			int j = 0;
			for(;kitStr[i].charAt(j) != '(';j++)
			{
				type += kitStr[i].charAt(j);
			}
			for(j++;kitStr[i].charAt(j) != '#';j++)
			{
				isEquip = true;
			}
			for(j++;kitStr[i].charAt(j) != '#';j++)
			{
				leatherColor += kitStr[i].charAt(j);
			}
			for(j++;kitStr[i].charAt(j) != ';';j++)
			{
				slot += kitStr[i].charAt(j);
			}
			for(j++;kitStr[i].charAt(j) != ';';j++)
			{
				amount+=kitStr[i].charAt(j);
			}
			for(j++;kitStr[i].charAt(j) != '#';j++)
			{
				durability+=kitStr[i].charAt(j);
			}
			for(j++;kitStr[i].charAt(j) != '#';j++)
			{
				String enchantments1 = "";
				String enchantmentsInt1 = "";
				for(;kitStr[i].charAt(j) != ';';j++)
				{
					enchantments1 += kitStr[i].charAt(j);
				}
				for(j++;kitStr[i].charAt(j) != '|';j++)
				{
					enchantmentsInt1 += kitStr[i].charAt(j);
				}
				
				enchantments.add(enchantments1);
				enchantmentsInt.add(enchantmentsInt1);
			}
			
			for(j++;kitStr[i].charAt(j) != ')';j++)
			{
				name += kitStr[i].charAt(j);
			}
			
			Material mat = Material.getMaterial(Integer.valueOf(type));
			ItemStack is = new ItemStack(mat,Integer.valueOf(amount),Short.valueOf(durability));
			if(!name.equals("null"))
			{
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(name);
				is.setItemMeta(im);
			}
			if(!leatherColor.equals(""))
			{
				LeatherArmorMeta lam = (LeatherArmorMeta) is.getItemMeta();
				lam.setColor(Color.fromRGB(Integer.valueOf(leatherColor)));
				is.setItemMeta(lam);
			}
			
			for(int u = 0;u<enchantments.size();u++)
			{
				Enchantment ench = Enchantment.getById(Integer.valueOf(enchantments.get(u)));
				
				try
				{
					if(is.getType().getId() != 403)
					{
						is.addUnsafeEnchantment(ench, Integer.valueOf(enchantmentsInt.get(u)));
					}
					else
					{
						EnchantmentStorageMeta esm = (EnchantmentStorageMeta) is.getItemMeta();
						esm.addStoredEnchant(ench, Integer.valueOf(enchantmentsInt.get(u)), true);
						is.setItemMeta(esm);
					}
					
				}
				catch(IllegalArgumentException e)
				{
					System.out.println("Enchantment " + ench.getName() + " not combatible with " + is.getType().toString());
					continue;
				}
				
			}
			
			if(isEquip)
			{
				kit.addEquipment(Integer.valueOf(slot), is);
			}
			else
			{
				kit.addItem(Integer.valueOf(slot), is);
			}
			
			
			
		}
		
		return kit;
	}
	
	public void setOwnerName(String string)
	{
		ownerName = string;
	}
	
	public String getOwnerName()
	{
		return owner == null ? ownerName :  ownerName.equals(owner.getName()) ? owner.getDisplayName() : ownerName;
	}

	public static void setupKits(MySQL mysql)
	{
		
		Kit.mysql = mysql;
		
		difKitNumbers = new HashMap<>();
	}
	
	public static Kit getDifKit(Player p,int number)
	{
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT Kit" + number + " FROM Duel_Kits WHERE UUID = ?");
			ps.setString(1, "Settings");
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				String str = rs.getString(1);
				
				Kit kit = decodeMySQLKit(p, str);
				if(kit == null)
				{
					kit = new Kit(p,number,true);
				}
				kit.setDifKit(true);
				
				return kit;
			}
		
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
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
	
	@SuppressWarnings("deprecation")
	public String itemsToString()
	{
		
		String strItems = "";
		
		strItems += name + "\n";
		
		strItems += ownerName +"\n";
		
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
	
	public static ChooseKit loadChoosenKit(UUID p)
	{
		try
		{
			PreparedStatement ps = MySQL.getInstance().getConnection().prepareStatement("SELECT ChoosenKit FROM Duel_Kits WHERE UUID = ?");
			ps.setString(1, p.toString());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				ChooseKit kit = ChooseKit.fromString(null, rs.getString(1));
				
				return kit;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}

	public int getNumber()
	{
		return number;
	}
}
