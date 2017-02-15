package at.kingcraft.OnevsOne_setup.Maps;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import MySQL.MySQL;
import at.kingcraft.OnevsOne_setup.MainClass;
import net.md_5.bungee.api.ChatColor;

public class MapManager {
	
	private static HashMap<UUID,HashMap<String,ArrayList<Block>>> buildingMaps = new HashMap<>();
	private static HashMap<String,Location[]> spawns = new HashMap<String,Location[]>();
	private static ArrayList<Map> maps = new ArrayList<Map>();
	private static HashMap<UUID,String> currentMap = new HashMap<>();
	private static Player currentPlayer = null;
	private static File saveMapFile;
	private static String mapPath = "plugins/";
	public static MainClass plugin;
	public static boolean isLoading = false;
	private static MySQL mysql; 
	
	@SuppressWarnings("deprecation")
	private static void writeToFile(Player p,Location midBlock,boolean glass,String material,byte bData,boolean inside,String symbol,short symbolData,int borderHeight,String builder) throws IOException
	{
		FileWriter fw = new FileWriter(mapPath + currentMap.get(p.getUniqueId()) + ".mcmap");
		BufferedWriter bw = new BufferedWriter(fw);
		
		// Write Settings
		bw.write("#OnevsOne Map: " + currentMap.get(p.getUniqueId()) + "\n");
		bw.write("glassborder: " + (glass ? "true" : "false") + "\n");
		bw.write("borderMaterial: " + material + "\n");
		bw.write("borderData: " + bData + "\n");
		bw.write("borderInside: " + (inside ? "true" : "false") + "\n");
		bw.write("symbol: " + symbol + "\n");
		bw.write("symbolData: " + symbolData + "\n");
		bw.write("borderHeight: " + borderHeight + "\n");
		bw.write("builder: " + builder + "\n");
		
		bw.write(";\n");
		
		
		// Write Spawns
		for(int i = 0;i<2;i++)
		{
			Location loc = spawns.get(currentMap.get(p.getUniqueId()))[i];
			if(loc != null)
			{
				bw.write("spawn" + (i+1) + "("  + loc.getWorld().getName() + ";" + (loc.getX()-midBlock.getX()) + ";" + (loc.getY()-midBlock.getY()) + ";" + (loc.getZ()-midBlock.getZ()) + ";" + loc.getYaw() + ";" + loc.getPitch() + ")\n"); 
				
			}
			else
			{
				if(currentPlayer != null)
				{
					currentPlayer.sendMessage(ChatColor.RED + "You have to set spawn" + (i+1));
					currentPlayer.sendMessage(ChatColor.RED + "Otherwhise the spawn won't work correctly");
				}
			}
		}
		
		bw.write(";\n");
		
		for(int i =0;i<buildingMaps.get(p.getUniqueId()).get(currentMap.get(p.getUniqueId())).size();i++)
		{
			Block b = buildingMaps.get(p.getUniqueId()).get(currentMap.get(p.getUniqueId())).get(i);
			Location l = b.getLocation();
			bw.write((b.getType()) + "(" + (l.getX()-midBlock.getX()) + ";" + (l.getY()-midBlock.getY()) + ";" + (l.getZ()-midBlock.getZ()) + ";" + (int)b.getData() + ")\n");
		}
		
		
		
		bw.close();
	
	}
	
	private static Map readFromFile(String map,Location startLoc)
	{
		try
		{
			FileReader fr = new FileReader(new File(mapPath + map + ".mcmap"));
			BufferedReader br = new BufferedReader(fr);
			
			ArrayList<SaveBlock> ab = new ArrayList<SaveBlock>();
			Location[] spawns = new Location[2];
			
			String line = "line";
			String type = "";
			String world = "";
			String x="",y="",z="";
			String yaw="",pitch="";
			String data="";
			String settingname="";
			String setting = "";
			boolean glass = true;
			String material = "";
			byte bData = (byte)0;
			boolean inside = false;
			String symbol = "";
			short symbolData = 0;
			int borderHeight = 20;
			String builder = "Server";
			
			line = br.readLine(); // Reads header comment
			
			int readMode = 0; // Switches between settingreading(0),spawnreading (1),blockreading (2) 
			
			while(line != null && line.length() > 0)
			{
				line = br.readLine();
				if(line != null && line.length() > 0)
				{
					int i;
					if(line.charAt(0) == ';')
					{
						readMode++; // Switches to next Mode
						line = br.readLine(); // "Deletes" line ";\n"
					}
						
					if(readMode == 2)
					{
						if(line != null)
						{
							type = "";
							x = "";
							y = "";
							z = "";
							data="";
								for(i =0;line.charAt(i) != '(';i++) // reads Type
								{
									type += line.charAt(i);
								}
								
								for(i++;line.charAt(i) != ';';i++) // reads x
								{
									x+=line.charAt(i);
								}
								for(i++;line.charAt(i) != ';';i++) // reads y
								{
									y+=line.charAt(i);
								}
								for(i++;line.charAt(i) != ';';i++) // reads z
								{
									z+=line.charAt(i);
								}
								for(i++;line.charAt(i) != ')';i++) // reads z
								{
									data+=line.charAt(i);
								}
								
								ab.add(new SaveBlock(Double.valueOf(x).doubleValue(),
													 Double.valueOf(y).doubleValue(),
													 Double.valueOf(z).doubleValue(),
													 Byte.valueOf(data).byteValue(),
													 Material.getMaterial(type)));
						}
						
					}
					else if(readMode == 1)
					{
						type = "";
						world = "";
						x = "";
						y = "";
						z = "";
						yaw = "";
						pitch = "";
							for(i =0;line.charAt(i) != '(';i++) // reads Spawn
							{
								type += line.charAt(i);
							}
							for(i++;line.charAt(i) != ';';i++) // reads x
							{
								world+=line.charAt(i);
							}
							
							for(i++;line.charAt(i) != ';';i++) // reads x
							{
								x+=line.charAt(i);
							}
							for(i++;line.charAt(i) != ';';i++) // reads y
							{
								y+=line.charAt(i);
							}
							for(i++;line.charAt(i) != ';';i++) // reads z
							{
								z+=line.charAt(i);
							}
							for(i++;line.charAt(i) != ';';i++) // reads yaw
							{
								yaw+=line.charAt(i);
							}
							for(i++;line.charAt(i) != ')';i++) // reads pitch
							{
								pitch+=line.charAt(i);
							}
							
							if(type.equalsIgnoreCase("spawn1"))
							{
								spawns[0] = new Location(Bukkit.getWorld(world),Double.valueOf(x).doubleValue(),
										                      Double.valueOf(y).doubleValue(),
										                      Double.valueOf(z).doubleValue(),
										                      (float)Double.valueOf(yaw).doubleValue(),
										                      (float)Double.valueOf(pitch).doubleValue());
							}
							else if(type.equalsIgnoreCase("spawn2"))
							{
								spawns[1] = new Location(Bukkit.getWorld(world),Double.valueOf(x).doubleValue(),
									                          Double.valueOf(y).doubleValue(),
									                          Double.valueOf(z).doubleValue(),
										                      (float)Double.valueOf(yaw).doubleValue(),
										                      (float)Double.valueOf(pitch).doubleValue());
							}
							
					}
					else if(readMode == 0) // Setting reading
					{
						settingname = "";
						setting = "";
						for(i = 0;line.charAt(i) != ':';i++)
						{
							settingname+= line.charAt(i);
						}
						for(i+=2;i<line.length();i++) // Skips space
						{
							setting += line.charAt(i);
						}
						
						if(settingname.equals("glassborder"))
						{
							glass = setting.equals("true") ? true : false;
						}
						else if(settingname.equals("borderMaterial"))
						{
							material = setting;
						}
						else if(settingname.equals("borderInside"))
						{
							inside = setting.equals("true") ? true : false;
						}
						else if(settingname.equals("symbol"))
						{
							symbol = setting;
						}
						else if(settingname.equals("symbolData"))
						{
							symbolData = Short.valueOf(setting);
						}
						else if(settingname.equals("borderData"))
						{
							bData = Byte.valueOf(setting);
						}
						else if(settingname.equalsIgnoreCase("borderHeight"))
						{
							borderHeight = Integer.valueOf(setting);
						}
						else if(settingname.equalsIgnoreCase("builder"))
						{
							builder = setting;
						}
						
					}
					
				}
			}
			
			
			br.close();
			
			
			
			return new Map(plugin,ab, startLoc, spawns,map,glass,material,bData,inside,symbol,symbolData,borderHeight,builder);
			
		}
		catch (FileNotFoundException e)
		{
			return null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static void reloadMapConfig()
	{
		// Clear config
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(saveMapFile));
			bw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		for(int i = 0;i<maps.size();i++)
		{
			writeToMapConfig(maps.get(i));
		}
	}
	
	private static void deleteMapFromMySQL(Map map)
	{
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("DELETE FROM Duel_Maps WHERE Name = ?");
			ps.setString(1, map.getName());
			
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	
	public static void deleteMap(int arrayIndex)
	{
		Map map = maps.get(arrayIndex);
		
		deleteMapFromMySQL(map);
		
		maps.remove(arrayIndex);
	}
	
	
	private static void readFromMapConfig(ArrayList<Map> newMaps)
	{
		try
		{
			FileReader fr = new FileReader(saveMapFile);
			BufferedReader br = new BufferedReader(fr);
			
			String line = "";
			String mapname;
			String worldname;
			String x,y,z;
			
			
			while(line != null)
			{
				line = br.readLine();
				
				if(line != null)
				{
					int i;
					
					mapname = worldname = x = y = z = "";
					
					for(i=0;line.charAt(i) != '(';i++)
					{
						mapname += line.charAt(i);
					}
					for(i++;line.charAt(i) != ';';i++)
					{
						worldname += line.charAt(i);
					}
					for(i++;line.charAt(i) != ';';i++)
					{
						x += line.charAt(i);
					}
					for(i++;line.charAt(i) != ';';i++)
					{
						y += line.charAt(i);
					}
					for(i++;line.charAt(i) != ')';i++)
					{
						z += line.charAt(i);
					}
					
					// Completely read one line
					
					Location start = new Location(plugin.getServer().getWorld(worldname),
												  Double.valueOf(x).doubleValue(),
											      Double.valueOf(y).doubleValue(),
										    	  Double.valueOf(z).doubleValue());
					
					Map newMap = loadMap(mapname, start);
					
					if(newMap != null)
					{
						newMaps.add(newMap);
					}
					else
					{
						System.out.println("[" + plugin.getName() + "] Couldn't load map: " + mapname);
					}
				}
				
			}
			
			// Finished with reading
			br.close();
			
			
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void writeToMapConfig(Map map)
	{
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(saveMapFile,true));
			
			Location midBlock = map.getMid();
			
			bw.write(map.getName() + "(" + midBlock.getWorld().getName() + ";" + midBlock.getX() + ";" + midBlock.getY() + ";" + midBlock.getZ() + ")\n");
			
			bw.close();
			
			writeToMySQL(map);
		}
		catch
		(FileNotFoundException e)
		{
			
			e.printStackTrace();
		}
		catch
		(IOException e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	public static ArrayList<Map> getMaps(String mapname)
	{
		ArrayList<Map> newMaps = new ArrayList<Map>(); 
		for(int i = 0;i<maps.size();i++)
		{
			if(maps.get(i) == null || maps.get(i).getName() == null)
			{
				continue;
			}
			
			if(maps.get(i).getName().equals(mapname))
			{
				newMaps.add(maps.get(i));
			}
		}
		
		return newMaps;
	}
	
	private static void writeToMySQL(Map map)
	{
		if(!mysql.isConnected())
		{
			return;
		}
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT * FROM Duel_Maps WHERE Name = ?");
			ps.setString(1, map.getName());
			ResultSet rs = ps.executeQuery();
			if(!rs.first())
			{
				ps.close();
				ps = mysql.getConnection().prepareStatement("INSERT INTO Duel_Maps (Name,Symbol,SymbolData,X,Y,Z,Builder,SizeX,SizeY) VALUES (?,?,?,?,?,?,?,?,?)");
				ps.setString(1, map.getName());
				ps.setInt(3, (int)map.getSymbol().getDurability());
				ps.setString(2, map.getSymbol().getType().toString());
				ps.setDouble(4, map.getMid().getX());
				ps.setDouble(5, map.getMid().getY());
				ps.setDouble(6, map.getMid().getZ());
				ps.setString(7, map.getBuilder());
				ps.setInt(8, map.getSizeX());
				ps.setInt(9, map.getSizeY());
				ps.executeUpdate();
				ps.close();
			}
			else if(!rs.getString(1).equals(map.getSymbol()) || !(rs.getInt(3) == (int)map.getSymbol().getDurability()))
			{
				ps.close();
				ps = mysql.getConnection().prepareStatement("UPDATE Duel_Maps SET Symbol = ?,SymbolData = ?,Builder = ? WHERE Name = ?");
				ps.setString(1, map.getSymbol().getType().toString());
				ps.setInt(2, (int)map.getSymbol().getDurability());
				ps.setString(3, map.getBuilder());
				ps.setString(4, map.getName());
				ps.executeUpdate();
				ps.close();
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void setup(MainClass plugin,MySQL mysql)
	{
		MapManager.plugin = plugin;
		MapManager.mysql = mysql;
		saveMapFile = new File("plugins/" + plugin.getName(), "maps.config");
		mapPath += plugin.getName() + "/";
		if(!saveMapFile.exists())
		{
			try
			{
				saveMapFile.createNewFile();
			}
			catch (IOException e)
			{
				
				e.printStackTrace();
			}
		}
		
		// Initialize MySQL
		
		if(mysql.isConnected())
		{
			try
			{
				PreparedStatement ps = mysql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Duel_Maps (Name VARCHAR(100),Symbol VARCHAR(100),SymbolData INT(100),X DOUBLE,Y DOUBLE,Z DOUBLE,Builder VARCHAR(100),SizeX INT(255),SizeY INT(255))");
				ps.executeUpdate();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		
		readFromMapConfig(maps);
	}
	
	public static void addMap(Map map)
	{
		maps.add(map);
	}
	
	public static ArrayList<Map> getMaps()
	{
		return maps;
	}
	
	public static Map getMap(String name)
	{
		for(int i = 0;i<maps.size();i++)
		{
			if(maps.get(i).getName().equals(name))
			{
				return maps.get(i);
			}
		}
		
		return null;
	}
	
	public static boolean start(String map,Player p)
	{
		
		if(buildingMaps.get(p.getUniqueId()) != null && buildingMaps.get(p.getUniqueId()).get(map) != null)
		{
			currentMap.put(p.getUniqueId(), map);
			return false;
		}
		buildingMaps.put(p.getUniqueId(), new HashMap<>());
		buildingMaps.get(p.getUniqueId()).put(map, new ArrayList<Block>());
		spawns.put(map, new Location[2]);
		
		spawns.get(map)[0] = new Location(p.getWorld(), 0.0, 0.0, 0.0,0.0f,0.0f);
		spawns.get(map)[1] = new Location(p.getWorld(), 0.0, 0.0, 0.0,0.0f,0.0f);
		
		currentMap.put(p.getUniqueId(), map);
		
		currentPlayer = p;
		
		return true;
	}
	
	public static boolean delete(Player p,String map)
	{
		ArrayList<Block> ab = buildingMaps.get(p.getUniqueId()).get(map);
		if( ab == null)
		{
			return false;
		}
		
		ab.clear();
		
		return true;
	}
	
	public static boolean onPlaceBlock(Player p,Block b)
	{
		if(currentMap.get(p.getUniqueId()).equals(""))
		{
			return false;
		}
		ArrayList<Block> ab = buildingMaps.get(p.getUniqueId()).get(currentMap.get(p.getUniqueId()));
		if(ab == null)
		{
			return false;
		}
		ab.add(b);
		return true;
	}
	
	public static boolean onBlockDestroy(Player p,Location l)
	{
		if(currentMap.get(p.getUniqueId()) == null || currentMap.get(p.getUniqueId()).equals(""))
		{
			return false;
		}
		
		for(int i =0;i<buildingMaps.get(p.getUniqueId()).get(currentMap.get(p.getUniqueId())).size();i++)
		{
			if(buildingMaps.get(p.getUniqueId()).get(currentMap.get(p.getUniqueId())).get(i).getLocation().equals(l))
			{
				buildingMaps.get(p.getUniqueId()).get(currentMap.get(p.getUniqueId())).remove(i);
			}
		}
		
		return true;
	}
	
	public static boolean writeBlocks(Player p)
	{
		if(currentMap.get(p.getUniqueId()) == null || currentMap.get(p.getUniqueId()).equals(""))
		{
			return false;
		}
		
		for(Block b : buildingMaps.get(p.getUniqueId()).get(currentMap.get(p.getUniqueId())))
		{
			p.sendMessage("Placed Block: " + ChatColor.BLUE + b.getType());
		}
		
		
		return true;
	}
	
	public static Map loadMap(String map,Location startLoc)
	{
		return readFromFile(map,startLoc);
	}
	
	public static void  end(Player p,Location firstBlock,boolean glass,String material,byte bData,boolean inside,String symbol,short symbolData,int borderHeight,String builder) throws IOException
	{
		writeToFile(p,firstBlock,glass,material,bData,inside,symbol,symbolData,borderHeight,builder);
		currentMap.put(p.getUniqueId(), "");
		currentPlayer = null;
	}
	
	public static void setSpawn(Player p,Location loc,int n)
	{
		if(spawns.get(currentMap.get(p.getUniqueId())) == null)
		{
			spawns.put(currentMap.get(p.getUniqueId()), new Location[2]);
		}
		
		spawns.get(currentMap.get(p.getUniqueId()))[n-1] = loc;
	}
	
	public static String getCurrentMap(Player p)
	{
		return currentMap.get(p.getUniqueId());
	}
	
	
}
