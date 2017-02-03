package at.kingcraft.OnevsOne_setup.Commands;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.kingcraft.OnevsOne_setup.MainClass;
import at.kingcraft.OnevsOne_setup.Maps.Map;
import at.kingcraft.OnevsOne_setup.Maps.MapManager;
import net.md_5.bungee.api.ChatColor;

public class MapCommand implements CommandExecutor {

	private Location startPoint;
	private Location endPoint;
	private boolean Glass;
	private String bMat;
	private byte bData;
	private boolean inside;
	private Location firstBlock;
	private Location midBlock;
	@SuppressWarnings("unused")
	private MainClass plugin;
	private String symbol;
	private short symbolData;
	private int borderHeight;
	private String builder;
	
	public MapCommand(MainClass plugin) {
		this.plugin = plugin;
		symbol = "";
		symbolData = 0;
		borderHeight = 20;
		bData = (byte)0;
		builder = "Server";
	} 
	
	private String getMapname(String[] args)
	{
		String name = "";
		
		for(int i = 1;i<args.length;i++)
		{
			name += args[i];
		}
		
		return name;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only for Players");
			return true;
		}
		
		
		Player me = (Player) sender;
		
		if(args.length >= 2)
		{
			if(args[0].equalsIgnoreCase("startpoint"))
			{
				return startpoint(me,getMapname(args),true,"GLASS",false);
			}
			else if(args[0].equalsIgnoreCase("setbuilder"))
			{
				return setbuilder(me, getMapname(args));
			}
		}
		
		if(args.length == 2) // start <mapname> | startpoint <mapname> | load <mapname> 
		{
			if(args[0].equalsIgnoreCase("load"))
			{
				return load(me,args);
			}
			else if(args[0].equalsIgnoreCase("clear"))
			{
				return clear(me,args);
			}
			else if(args[0].equalsIgnoreCase("setsymbol"))
			{
				symbol = args[1];
				if(Material.getMaterial(symbol) == null)
				{
					me.sendMessage(ChatColor.RED + "Can't define Material from " + ChatColor.BLUE + symbol + ChatColor.RED + "\nIf you don't know the name:\n\t Search on spigotmc.org");
					symbol = "";
				}
				else
				{
					me.sendMessage(ChatColor.YELLOW + "Symbol defined");
				}
				
				return true;
				
			}
			else if(args[0].equalsIgnoreCase("setsymboldata"))
			{
				symbolData = Short.valueOf(args[1]);
				me.sendMessage(ChatColor.YELLOW + "SymbolData defined");
				return true;
			}
			else if(args[0].equalsIgnoreCase("help"))
			{
				int page = Integer.valueOf(args[1]);
				onHelp(me,page);
				return true;
			}
			else if(args[0].equalsIgnoreCase("setbordermat"))
			{
				Material mat = Material.getMaterial(args[1]);
				if(mat == null)
				{
					me.sendMessage(ChatColor.BLUE + args[1] + ChatColor.RED + " isn't a proper Material\nSearch on spigotmc.org for the proper one");
					mat = Material.GLASS;
				}
				else
				{
					me.sendMessage(ChatColor.YELLOW + "Border Material set to " + ChatColor.BLUE + mat.toString());
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("setborderdata"))
			{
				try
				{
					bData = Byte.valueOf(args[1]);
					me.sendMessage(ChatColor.YELLOW + "Borderdata set to " + ChatColor.BLUE + bData);
				}
				catch(NumberFormatException e)
				{
					me.sendMessage(ChatColor.RED + "Can't convert " + ChatColor.BLUE + args[1] + ChatColor.RED + " to Byte");
				}
				
				return true;
				
			}
			else if(args[0].equalsIgnoreCase("setborderheight"))
			{
				try
				{
					borderHeight = Integer.valueOf(args[1]);
					me.sendMessage(ChatColor.YELLOW + "Border Height set to " + ChatColor.BLUE + borderHeight);
				}
				catch(NumberFormatException e)
				{
					me.sendMessage(ChatColor.BLUE + args[1] + ChatColor.RED + " isn't a proper number");
					borderHeight = 20;
				}
				
				return true;
			}
			else if(args[0].equalsIgnoreCase("finish") && (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")))
			{
				return finish(me,args[1].equalsIgnoreCase("true"),MapManager.getCurrentMap(me));
			}
			
		}
		else if(args.length == 1) // end | endpoint | write | setspawn1 | setspawn2 | setmid | finish
		{
			if(args[0].equalsIgnoreCase("end"))
			{
				return end(me,Glass,bMat,bData,inside,symbol,symbolData,borderHeight,builder);
			}
			else if(args[0].equalsIgnoreCase("write"))
			{
				return write(me);
			}
			else if(args[0].equalsIgnoreCase("finish"))
			{
				return finish(me,false,MapManager.getCurrentMap(me));
			}
			else if(args[0].equalsIgnoreCase("endpoint"))
			{
				return endpoint(me);
			}
			else if(args[0].equalsIgnoreCase("setspawn1"))
			{
				return setspawn(me,1);
			}
			else if(args[0].equalsIgnoreCase("setspawn2"))
			{
				return setspawn(me,2);
			}
			else if(args[0].equalsIgnoreCase("setmid"))
			{
				return setMid(me);
			}
			else if(args[0].equalsIgnoreCase("reload"))
			{
				return reload(me);
			}
			else if(args[0].equalsIgnoreCase("setborder"))
			{
				if(Glass)
				{
					me.sendMessage(ChatColor.YELLOW + "Border switched off");
				}
				else
				{
					me.sendMessage(ChatColor.YELLOW + "Border switched on");
				}
				
				Glass = !Glass;
				return true;
			}
			else if(args[0].equalsIgnoreCase("setborderinside"))
			{
				if(inside)
				{
					me.sendMessage(ChatColor.YELLOW + "Border is outside");
				}
				else
				{
					me.sendMessage(ChatColor.YELLOW + "Border is inside");
				}
				
				inside = !inside;
				
				return true;
			}
				
				
		}
		
		onHelp(me,1);
		return true;
	}
	
	private boolean setbuilder(Player me,String name)
	{
		builder = name;
		
		me.sendMessage(ChatColor.YELLOW + "Builder defined");
		
		return true;
	}
	
	private boolean clear(Player me, String[] args)
	{
		ArrayList<Map> clearMaps = MapManager.getMaps();
		
		int numMaps = 0;
		
		for(int i = 0;i<clearMaps.size();i++)
		{
			if(clearMaps.get(i).getName().equals(args[1]))
			{
				numMaps++;
				clearMaps.get(i).clear();
				MapManager.deleteMap(i);
				i--;
			}
		}
		
		if(numMaps == 0)
		{
			me.sendMessage(ChatColor.RED +  "No Maps with name " + ChatColor.BLUE + args[1]);
			return true;
		}
		
		MapManager.reloadMapConfig();
		
		me.sendMessage(ChatColor.DARK_BLUE + "[" + args[1] + "] " + ChatColor.BLUE + "" + numMaps + "" + ChatColor.YELLOW + " Maps deleted from config and MySQL");
		
		
		
		return true;
	}

	private boolean setspawn(Player me,int num)
	{
		
		MapManager.setSpawn(me,roundLocation(me.getLocation()), num);
		
		me.sendMessage(ChatColor.YELLOW + "Spawn" + num + " defined");
		
		return true;
	}
	
	private boolean setMid(Player me)
	{
		
		midBlock = roundLocation(me.getLocation());
		
		me.sendMessage(ChatColor.YELLOW + "Mid defined");
		
		return true;
	}
	
	private Location roundLocation(Location arg0)
	{
		Location loc = arg0.clone();
		
		loc.setX((double)loc.getBlockX());
		loc.setY((double)loc.getBlockY());
		loc.setZ((double)loc.getBlockZ());
		
		if(loc.getX() > arg0.getX())
		{
			loc.setX(loc.getX()-0.5);
		}
		else if(loc.getX() < arg0.getX())
		{
			loc.setX(loc.getX()+0.5);
		}
		
		if(loc.getZ() > arg0.getZ())
		{
			loc.setZ(loc.getZ()-0.5);
		}
		else if(loc.getZ() < arg0.getZ())
		{
			loc.setZ(loc.getZ()+0.5);
		}
		
		return loc;
	}
	
	private boolean write(Player me)
	{
		if(!MapManager.writeBlocks(me))
		{
			me.sendMessage(ChatColor.RED + "No Map Choosen");
			return true;
		}
		return true;
	}
	
	private boolean end(Player me,boolean glass,String material,byte bDatab,boolean inside,String symbol,short symbolData,int borderHeight,String builder)
	{
		me.sendMessage(ChatColor.BLUE + MapManager.getCurrentMap(me) + ChatColor.YELLOW + " is saving");
		
		if(MapManager.getCurrentMap(me).equals(""))
		{
			me.sendMessage(ChatColor.RED + "No Map choosen");
			return true;
		}
		
		me.sendMessage(ChatColor.YELLOW + "Construction of " + ChatColor.BLUE + MapManager.getCurrentMap(me) + ChatColor.YELLOW + " completed");
		
		
		try
		{
			if(midBlock == null)
			{
				midBlock = firstBlock;
				me.sendMessage(ChatColor.RED + "You have to define the mid!");
				me.sendMessage(ChatColor.RED + "Otherwise the mid won't work correctly");
			}
			
			
			MapManager.end(me,midBlock,glass,material,bDatab,inside,symbol,symbolData,borderHeight,builder);
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		
		return true;
	}
	
	private boolean finish(Player me,boolean build,String mapStr)
	{
		if(startPoint == null || endPoint == null)
		{
			me.sendMessage(ChatColor.RED + "Please define start- and endpoint first");
			return true;
		}
		
		if(symbol.equals(""))
		{
			me.sendMessage(ChatColor.RED + "No Symbol defined\nPaper will be used");
		}
		
		int x=(int)startPoint.getX(),
				y=(int)startPoint.getY(),
				z=(int)startPoint.getZ();
			
			int maxx = (int)endPoint.getX(),
				maxy = (int)endPoint.getY(),
				maxz = (int)endPoint.getZ();
			
			int minx = (int)startPoint.getX(),
				miny = (int)startPoint.getY(),
				minz = (int)startPoint.getZ();
			
			boolean firstNotAir = false;
			
				
				
				for(z=minz;minz > maxz ? z > maxz : z < maxz;)
				{
					for(y=miny;miny > maxy ? y > maxy : y < maxy;)
					{
						for(x=minx;minx > maxx ? x > maxx : x < maxx;)
						{
							Block b = me.getWorld().getBlockAt(new Location(me.getWorld(),(double)x,(double)y,(double)z));
				
							if(b.getType() != Material.AIR)
							{
								if(!firstNotAir)
								{
									firstNotAir = true;
									firstBlock = b.getLocation();
								}
								
								
								
								MapManager.onPlaceBlock(me,b);
							}
								
							
							if(minx > maxx)
							{
								x--;
							}
							else if(minx < maxx)
							{
								x++;
							}
						}
						if(miny > maxy)
						{
							y--;
						}
						else if(miny < maxy)
						{
							y++;
						}
					}
					if(minz > maxz)
					{
						z--;
					}
					else if(minz < maxz)
					{
						z++;
					}
				}
				me.performCommand("map end");
				
				if(build)
				{
					me.sendMessage(ChatColor.YELLOW + "Map is Loading");
					Map map = MapManager.loadMap(mapStr,midBlock);
					me.sendMessage(ChatColor.YELLOW + "Map has loaded");
					MapManager.addMap(map);
					
					if(map == null || map.getBlocks() == null)
					{
						me.sendMessage(ChatColor.BLUE + mapStr + ChatColor.RED + " not found or Error");
						return true;
					}
					
					me.sendMessage(ChatColor.YELLOW + "Map is being placed");
					
					// Platzieren
					map.reload(me,false);
					
					
					MapManager.writeToMapConfig(map);
				}
				
		return true;
	}
	
	
	private boolean load(Player me,String args[])
	{
		
				
		me.sendMessage(ChatColor.YELLOW + "Map is loading");
		Map map = MapManager.loadMap(args[1],me.getLocation());
		me.sendMessage(ChatColor.YELLOW + "Map has loaded");
		MapManager.addMap(map);
		
		if(map == null || map.getBlocks() == null)
		{
			me.sendMessage(ChatColor.BLUE + args[1] + ChatColor.RED + " not found or Error");
			return true;
		}
		
		me.sendMessage(ChatColor.YELLOW + "Map is being placed");
		
		// Platzieren
		map.reload(me,false);
		
		
		MapManager.writeToMapConfig(map);
			
		
		
		return true;
	
	}
	
	private boolean startpoint(Player me,String mapname,boolean border,String bMaterial,boolean inside)
	{
		Glass = border;
		bMat = bMaterial;
		this.inside = inside;
		midBlock = null;
		startPoint = null;
		endPoint = null;
		
		if(MapManager.getCurrentMap(me) != null || (MapManager.getCurrentMap(me) != null  && !MapManager.getCurrentMap(me).equals("")))
		{
			me.performCommand("map end");
		}
		
		if(!MapManager.start(mapname,me))
		{
			me.sendMessage(ChatColor.BLUE + mapname + ChatColor.YELLOW + " will be deleted");
			MapManager.delete(me,mapname);
			
		}
		startPoint = me.getLocation();
		me.sendMessage(ChatColor.YELLOW + "Construction of " + ChatColor.BLUE + mapname + ChatColor.YELLOW + " started");
		
		return true;
	}
	
	private boolean reload(Player me)
	{
		int size = MapManager.getMaps().size();
		if(size == 0)
		{
			me.sendMessage(ChatColor.RED + "No Maps placed");
			return true;
		}
		ArrayList<Map> maps = MapManager.getMaps();
		
		for(int i = 0;i<maps.size();i++)
		{
			maps.get(i).reload(me,true);
		}
		
		
		return true;
	}
	
	private boolean endpoint(Player me)
	{
		endPoint = me.getLocation();
		
		me.sendMessage(ChatColor.YELLOW + "Endpoint defined");
			
		return true;
	}

	private void onHelp(Player me,int page)
	{
		me.sendMessage(ChatColor.GREEN + "-----" + ChatColor.AQUA + "Hilfe" + ChatColor.GREEN + "-----");
		if(page == 1)
		{
			me.sendMessage(ChatColor.BLUE + "/map setspawn1" + ChatColor.WHITE + " - Setzt ersten Spawn");
			me.sendMessage(ChatColor.BLUE + "/map setspawn2" + ChatColor.WHITE + " - Setzt zweiten Spawn");
			me.sendMessage(ChatColor.BLUE + "/map setmid" + ChatColor.WHITE + " - Setzt die Mitte");
			me.sendMessage(ChatColor.BLUE + "/map setsymbol <material>" + ChatColor.WHITE + " - Setzt das Symbol, das bei der Mapauswahl angezeigt wird");
			me.sendMessage(ChatColor.BLUE + "/map setsymboldata <short>" + ChatColor.WHITE + " - Bestimmt verschiedene Dinge des Symbols wie:\nz.B. bei Glass die Farbe\nbei Grass welches Grass es sein soll");
			me.sendMessage(ChatColor.BOLD + "" + ChatColor.BLUE + "/map startpoint <mapname...>" + ChatColor.WHITE + " - Setzt den Startpunkt fuer <mapname...>");
		}
		else if(page == 2)
		{
			me.sendMessage(ChatColor.BLUE + "/map setborder" + ChatColor.WHITE + " - Schaltet Border ein bzw. aus");
			me.sendMessage(ChatColor.BLUE + "/map setborderinside" + ChatColor.WHITE + " - Setzt Border innerhalb bzw. ausserhalb");
			me.sendMessage(ChatColor.BLUE + "/map setborderdata <byte>" + ChatColor.WHITE + " - Setzt die Data des Borderblocks z.B. Farbe");
			me.sendMessage(ChatColor.BLUE + "/map setbordermat <material>" + ChatColor.WHITE + " - Setzt Bordermaterial");
			me.sendMessage(ChatColor.BLUE + "/map setborderheight <number>" + ChatColor.WHITE + " - Setzt Höhe der Border");
		}
		else if(page == 3)
		{
			me.sendMessage(ChatColor.BLUE + "/map reload" + ChatColor.WHITE + " - Ladet alle Maps neu");
			me.sendMessage(ChatColor.BLUE + "/map endpoint" + ChatColor.WHITE + " - Setzt den Endpunkt");
			me.sendMessage(ChatColor.BLUE + "/map finish [true|false]" + ChatColor.WHITE + " - Schreibt die Map in eine Datei\n - [true|false] bestimmt ob die Map sofort platziert wird");
			me.sendMessage(ChatColor.BLUE + "/map load <mapname>" + ChatColor.WHITE + " - Platziert eine <mapname> beim Spieler");
			me.sendMessage(ChatColor.BLUE + "/map clear <mapname>" + ChatColor.WHITE + " - Loescht alle Maps mit dem Namen <mapname> ");
		}
		else
		{
			onHelp(me,1);
			return;
		}
		
		if(page == 1)
		{
			me.sendMessage(ChatColor.BLUE + "/map help 2" + ChatColor.WHITE + " - für mehr");
		}
		else if(page == 2)
		{
			me.sendMessage(ChatColor.BLUE + "/map help 3" + ChatColor.WHITE + " - für mehr");
		}
		else if(page == 3)
		{
			me.sendMessage(ChatColor.BLUE + "/map help 1" + ChatColor.WHITE + " - für mehr");
		}
		
		
	}
}
