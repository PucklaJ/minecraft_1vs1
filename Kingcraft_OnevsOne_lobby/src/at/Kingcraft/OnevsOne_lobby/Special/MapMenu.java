package at.Kingcraft.OnevsOne_lobby.Special;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import MySQL.MySQL;
import at.Kingcraft.OnevsOne_lobby.Items.Items;
import net.md_5.bungee.api.ChatColor;

public class MapMenu extends Menu{

	private ArrayList<MapSymbol> myMaps;
	private ArrayList<MapSymbol> otherMaps;
	private static MySQL mysql;
	private static ArrayList<MapSymbol> allMaps;
	private boolean needsSQLUpdate = false;
	private int myOffset;
	private int otherOffset;
	private static final int MAX_MAPS = 16;
	private static final int MY_MAPS_PREV_POS = 45;
	private static final int MY_MAPS_NEXT_POS = 48;
	private static final int OTHER_MAPS_PREV_POS = 45+5;
	private static final int OTHER_MAPS_NEXT_POS = 48+5;
	
	
	public static void setup(MySQL mysql)
	{
		MapMenu.mysql = mysql;
		allMaps = new ArrayList<>();
		if(mysql.isConnected())
		{
			try
			{
				PreparedStatement ps = mysql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Duel_Maps_Players (UUID VARCHAR(100),Choosen_Maps VARCHAR(1000))");
				ps.executeUpdate();
				ps.close();
				
				loadMaps();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		
		
		
	}
	
	public static ArrayList<MapSymbol> getAllMaps()
	{
		return allMaps;
	}
	
	public static void loadMaps()
	{
		allMaps.clear();
		if(mysql.isConnected())
		{
			try
			{
				PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT * FROM Duel_Maps");
				ResultSet rs = ps.executeQuery();
				
				while(rs.next())
				{
					allMaps.add(new MapSymbol(rs.getString(1),rs.getString(2),rs.getShort(3),rs.getString(7),rs.getInt(8),rs.getInt(9)));
				}
				ps.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static String getRandomArena(ArrayList<MapSymbol> m1,ArrayList<MapSymbol> m2)
	{
		Random r = new Random();
		ArrayList<MapSymbol> maps = new ArrayList<MapSymbol>();
		
		
		if(m1 != null)
			maps.addAll(m1);
		if(m2 != null)
			maps.addAll(m2);
		
		if(maps.size() == 0)
			maps.addAll(allMaps);
		
		if(maps.size() == 0)
		{
			return "";
		}
		
		int random = r.nextInt(maps.size());
		
		if(random < 0)
		{
			random = 0;
		}
		else if(random > maps.size()-1)
		{
			random = maps.size()-1;
		}
		
		return maps.get(random).getName();
	}
	
	public MapMenu(Player owner,SettingMenu sm)
	{
		super(owner,54,"Maps",sm);
		
		myOffset = 0;
		otherOffset = 0;
	}
	
	@Override
	protected void setInventoryContents()
	{
		ItemStack[] contents = new ItemStack[inventory.getSize()];
		
		ItemStack myMaps = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)5);
		{
			ItemMeta im = myMaps.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "Ausgewählte Maps");
			myMaps.setItemMeta(im);
		}
		ItemStack otherMaps = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)14);
		{
			ItemMeta im = otherMaps.getItemMeta();
			im.setDisplayName(ChatColor.RED + "Nicht Ausgewählte Maps");
			otherMaps.setItemMeta(im);
		}
		ItemStack middle = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)0);
		{
			ItemMeta im = middle.getItemMeta();
			im.setDisplayName(" ");
			middle.setItemMeta(im);
		}
		ItemStack back = new ItemStack(Material.getMaterial(Items.menuBackMaterial),1,Items.menuBackDurability);
		{
			ItemMeta im = back.getItemMeta();
			im.setDisplayName(Items.menuBackName);
			back.setItemMeta(im);
		}
		ItemStack next = new ItemStack(Material.WOOD_BUTTON);
		{
			ItemMeta im = next.getItemMeta();
			im.setDisplayName("+");
			next.setItemMeta(im);
		}
		ItemStack prev = new ItemStack(Material.WOOD_BUTTON);
		{
			ItemMeta im = prev.getItemMeta();
			im.setDisplayName("-");
			prev.setItemMeta(im);
		}
		
		for(int i = 0;i<4;i++)
		{
			contents[i] = myMaps.clone();
		}
		
		for(int i = 5;i<9;i++)
		{
			contents[i] = otherMaps.clone();
		}
		
		for(int i = 4;i<49;i+=9)
		{
			contents[i] = middle.clone();
		}
		
		contents[49] = back.clone();
		contents[MY_MAPS_NEXT_POS] = next;
		contents[MY_MAPS_NEXT_POS-1] = contents[MY_MAPS_PREV_POS+1] = myMaps.clone();
		contents[MY_MAPS_PREV_POS] = prev;
		contents[OTHER_MAPS_NEXT_POS] = next;
		contents[OTHER_MAPS_NEXT_POS-1] = contents[OTHER_MAPS_PREV_POS+1] = otherMaps.clone();
		contents[OTHER_MAPS_PREV_POS] = prev;
		
		inventory.setContents(contents);
		
		setMyMaps();
		setOtherMaps();
	}
	
	@Override
	public void onClick(int slot,ClickType ct)
	{
		if(slot == 49 && ct.isLeftClick())
		{
			close();
		}
		else if(isMyMapsClick(slot))
		{
			moveToOtherMaps(inventory.getItem(slot).getItemMeta().getDisplayName());
			setMyMapsItems();
			setOtherMapsItems();
		}
		else if(isOtherMapsClick(slot))
		{
			moveToMyMaps(inventory.getItem(slot).getItemMeta().getDisplayName());
			setMyMapsItems();
			setOtherMapsItems();
		}
		else if(slot == MY_MAPS_NEXT_POS && ct.isLeftClick())
		{
			myOffset+=MAX_MAPS;
			if(myOffset >= myMaps.size())
			{
				myOffset-=MAX_MAPS;
			}
			
			setMyMapsItems();
		}
		else if(slot == MY_MAPS_PREV_POS && ct.isLeftClick())
		{
			myOffset-=MAX_MAPS;
			if(myOffset < 0)
			{
				myOffset+=MAX_MAPS;
			}
			
			setMyMapsItems();
		}
		else if(slot == OTHER_MAPS_NEXT_POS && ct.isLeftClick())
		{
			otherOffset+=MAX_MAPS;
			if(otherOffset >= otherMaps.size())
			{
				otherOffset-=MAX_MAPS;
			}
			
			setOtherMapsItems();
		}
		else if(slot == OTHER_MAPS_PREV_POS && ct.isLeftClick())
		{
			otherOffset-=MAX_MAPS;
			if(otherOffset < 0)
			{
				otherOffset+=MAX_MAPS;
			}
			
			setOtherMapsItems();
		}
	}
	
	public void setMyMaps()
	{
		if(myMaps == null)
		{
			myMaps = new ArrayList<>();
		}
		myMaps.clear();
		
		// Get My Maps from MySQL
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT * FROM Duel_Maps_Players WHERE UUID = ?");
			ps.setString(1, owner.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				String[] maps = decodeDataFromMySQL(rs.getString(2));
				for(int i = 0;i<maps.length;i++)
				{
					myMaps.add(getMapSymbol(maps[i]));
				}
				
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		// Import into inventory
		setMyMapsItems();
		
	}
	
	public void setMyMapsItems()
	{
		clearMyMapsItems();
		
		int x = 9;
		int y = 1;
		for(int i = myOffset;i<myMaps.size() && (i-myOffset < MAX_MAPS);i++)
		{
			ItemStack is = new ItemStack(myMaps.get(i).getSymbol(),1,myMaps.get(i).getData());
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&',myMaps.get(i).getName()));
			ArrayList<String> lore = new ArrayList<>();
			lore.add(ChatColor.YELLOW + "Erbauer: " + ChatColor.BLUE + myMaps.get(i).getBuilder());
			lore.add(ChatColor.YELLOW + "Größe: " + ChatColor.BLUE + myMaps.get(i).getX() + "x" + myMaps.get(i).getY());
			im.setLore(lore);
			is.setItemMeta(im);
			inventory.setItem(x, is);
			
			x++;
			if(x > 12+(y-1)*9)
			{
				y++;
				x = y*9;
				if(y==5)
				{
					x++;
				}
			}
		}
	}

	public void setOtherMapsItems()
	{
		clearOtherMapsItems();
		
		int x = 9+5;
		int y = 1;
		for(int i = otherOffset;i<otherMaps.size() && (i-otherOffset < MAX_MAPS);i++)
		{
			ItemStack is = new ItemStack(otherMaps.get(i).getSymbol(),1,otherMaps.get(i).getData());
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&',otherMaps.get(i).getName()));
			ArrayList<String> lore = new ArrayList<>();
			lore.add(ChatColor.YELLOW + "Erbauer: " + ChatColor.BLUE + otherMaps.get(i).getBuilder());
			lore.add(ChatColor.YELLOW + "Größe: " + ChatColor.BLUE + otherMaps.get(i).getX() + "x" + otherMaps.get(i).getY());
			im.setLore(lore);
			is.setItemMeta(im);
			inventory.setItem(x, is);
			
			x++;
			if(x > (12+5)+(y-1)*9)
			{
				y++;
				x = y*9+5;
				if(y==5)
				{
					x++;
				}
			}
		}
	}
	
	private void clearOtherMapsItems()
	{
		int x = 14;
		int y = 1;
		while(y<5)
		{
			if(!(x == OTHER_MAPS_NEXT_POS || x == OTHER_MAPS_PREV_POS))
				inventory.setItem(x, new ItemStack(Material.AIR));
			
			x++;
			if(x > 17+(y-1)*9)
			{
				y++;
				x = y*9+5;
			}
		}
	}
	
	private void clearMyMapsItems()
	{
		int x = 9;
		int y = 1;
		while(y<5)
		{
			if(!(x == MY_MAPS_NEXT_POS || x == MY_MAPS_PREV_POS))
				inventory.setItem(x, new ItemStack(Material.AIR));
			
			x++;
			if(x > 12+(y-1)*9)
			{
				y++;
				x = y*9;
			}
		}
	}
	
	private boolean isInMyMaps(String name)
	{
		for(int i = 0;i<myMaps.size();i++)
		{
			if(myMaps.get(i).getName().equals(name))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private static MapSymbol getMapSymbol(String name)
	{
		for(int i = 0;i<allMaps.size();i++)
		{
			if(allMaps.get(i).getName().equals(name))
			{
				return allMaps.get(i);
			}
		}
		
		return new MapSymbol("null","null",(short)0,"null",0,0);
	}
	
	private static String[] decodeDataFromMySQL(String str)
	{
			ArrayList<String> uuis =  new ArrayList<String>();
		
			int i = -1;
			String uuid = "";
			while(i < str.length())
			{
				uuid = "";
				for(i++;i<str.length() && str.charAt(i)!= '|';i++)
				{
					uuid+=str.charAt(i);
				}
				
				if(uuid.length() != 0)
				{
					uuis.add(uuid);
				}
				
			}
			
			String[] strings = new String[uuis.size()];
			
			for(int u = 0;u<strings.length;u++)
			{
				strings[u] = uuis.get(u);
			}
			
			uuis.clear();
		
		
		return strings;
	}
	
	public void setOtherMaps()
	{
		if(otherMaps == null)
		{
			otherMaps = new ArrayList<>();
		}
		otherMaps.clear();
		
		for(int i = 0;i<allMaps.size();i++)
		{
			if(!isInMyMaps(allMaps.get(i).getName()))
			{
				otherMaps.add(allMaps.get(i));
			}
		}
		
		setOtherMapsItems();
	}
	
	public void writeMapsToMySQL()
	{
		if(!mysql.isConnected())
		{
			return;
		}
		if(!needsSQLUpdate)
		{
			return;
		}
		
		String myMapsSQL = "";
		
		for(int i = 0;i<myMaps.size();i++)
		{
			myMapsSQL += myMaps.get(i).getName() + "|";
		}
	
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT Choosen_Maps FROM Duel_Maps_Players WHERE UUID = ?");
			ps.setString(1, owner.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			if(!rs.first())
			{
				ps.close();
				ps = mysql.getConnection().prepareStatement("INSERT INTO Duel_Maps_Players (UUID,Choosen_Maps) VALUES (?,?)");
				ps.setString(1, owner.getUniqueId().toString());
				ps.setString(2, myMapsSQL);
				ps.executeUpdate();
				ps.close();
			}
			else
			{
				ps.close();
				ps = mysql.getConnection().prepareStatement("UPDATE Duel_Maps_Players SET Choosen_Maps = ? WHERE UUID = ?");
				ps.setString(1, myMapsSQL);
				ps.setString(2, owner.getUniqueId().toString());
				ps.executeUpdate();
				ps.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	private boolean isOtherMapsClick(int slot)
	{
		if(slot == OTHER_MAPS_NEXT_POS || slot == OTHER_MAPS_PREV_POS || slot == OTHER_MAPS_PREV_POS+1 || slot == OTHER_MAPS_NEXT_POS-1)
		{
			return false;
		}
		
		if(inventory.getItem(slot) == null || inventory.getItem(slot).getType().equals(Material.AIR))
		{
			return false;
		}
		
		int min = 13;
		int max = 18;
		for(int i = 0;i<5;i++)
		{
			if(slot > min + (i*9) && slot < max + (i*9))
			{
				return true;
			}
		}
	
		return false;
	}
	
	private boolean isMyMapsClick(int slot)
	{
		if(slot == MY_MAPS_NEXT_POS || slot == MY_MAPS_PREV_POS  || slot == MY_MAPS_PREV_POS+1 || slot == MY_MAPS_NEXT_POS-1)
		{
			return false;
		}
		
		if(inventory.getItem(slot) == null || inventory.getItem(slot).getType().equals(Material.AIR))
		{
			return false;
		}
		
		int min = 8;
		int max = 13;
		for(int i = 0;i<5;i++)
		{
			if(slot > min + (i*9) && slot < max + (i*9))
			{
				return true;
			}
		}
	
		return false;
	}
	
	private void moveToMyMaps(String name)
	{
		for(int i = 0;i<otherMaps.size();i++)
		{
			if((ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&',otherMaps.get(i).getName())).equals(name))
			{
				myMaps.add(otherMaps.get(i));
				otherMaps.remove(i);
				needsSQLUpdate = true;
				return;
			}
		}
	}
	
	private void moveToOtherMaps(String name)
	{
		for(int i = 0;i<myMaps.size();i++)
		{
			if((ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&',myMaps.get(i).getName())).equals(name))
			{
				otherMaps.add(myMaps.get(i));
				myMaps.remove(i);
				needsSQLUpdate = true;
				return;
			}
		}
	}
	
	public ArrayList<MapSymbol> getMyMaps()
	{
		return myMaps;
	}
}
