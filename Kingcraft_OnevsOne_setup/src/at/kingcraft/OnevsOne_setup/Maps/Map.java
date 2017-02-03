package at.kingcraft.OnevsOne_setup.Maps;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Torch;
import org.bukkit.scheduler.BukkitTask;

import at.kingcraft.OnevsOne_setup.MainClass;
import net.md_5.bungee.api.ChatColor;

public class Map
{
	private ArrayList<SaveBlock> blocks;
	private ArrayList<SaveBlock> laterPlaceBlocks;
	private Location[] spawns;
	private Location firstBlock;
	private Location lastBlock;
	private String name;
	private Location[] corners = new Location[8];
	private Location[] insideCorners = new Location[2];
	private boolean loadedInsideCorners = false;
	private boolean loadedCorners = false;
	private MainClass plugin;
	private BukkitTask Reload = null;
	private int ReloadIteration = 0;
	private boolean glass;
	private String boarderMat;
	private byte boarderData;
	private boolean inside;
	private Location mid;
	private String symbol;
	private short symbolData;
	private boolean loaded;
	private int borderHeight;
	private ArrayList<SaveBlock> buildBlocks;
	private ArrayList<SaveBlock> breakedBlocks;
	private String builder;
	private int sizeX;
	private int sizeY;
	
	public static boolean isPlaceLater(Material mat)
	{
		return mat.equals(Material.REDSTONE_WIRE) || mat.equals(Material.TORCH) || mat.equals(Material.ACACIA_DOOR) ||
				mat.equals(Material.BIRCH_DOOR) || mat.equals(Material.SPRUCE_DOOR) || mat.equals(Material.DARK_OAK_DOOR) ||
				mat.equals(Material.JUNGLE_DOOR) || mat.equals(Material.WOODEN_DOOR) || mat.equals(Material.WOOD_DOOR) ||
				mat.equals(Material.FIRE) || mat.equals(Material.YELLOW_FLOWER) || mat.equals(Material.LONG_GRASS) ||
				mat.equals(Material.LADDER) || mat.equals(Material.REDSTONE_TORCH_ON) || mat.equals(Material.REDSTONE_TORCH_OFF);
	}
	
	public Map(MainClass plugin,ArrayList<SaveBlock> saveBlocks,Location loc,Location[] spawns,String name,boolean glass,String bMat,byte boarderData,boolean inside,String symbol,short symbolData,int borderHeight,String builder)
	{
		blocks = new ArrayList<SaveBlock>();
		laterPlaceBlocks = new ArrayList<SaveBlock>();
		this.glass = glass;
		boarderMat = bMat;
		this.boarderData = boarderData;
		this.inside = inside;
		this.mid = loc.clone();
		firstBlock = loc;
		this.symbol = symbol;
		this.symbolData = symbolData;
		loaded = true;
		this.borderHeight = borderHeight;
		this.builder = builder;
		
		this.plugin = plugin;
		
		
		
		for(int i = 0;i<saveBlocks.size();i++)
		{
			if(isPlaceLater(saveBlocks.get(i).mat))
			{
				laterPlaceBlocks.add(new SaveBlock(saveBlocks.get(i).x + loc.getX(),
						 saveBlocks.get(i).y + loc.getY(),
						 saveBlocks.get(i).z + loc.getZ(),
						 saveBlocks.get(i).data,
						 saveBlocks.get(i).mat));
			}
			else
			{
				blocks.add(new SaveBlock(saveBlocks.get(i).x + loc.getX(),
									 saveBlocks.get(i).y + loc.getY(),
									 saveBlocks.get(i).z + loc.getZ(),
									 saveBlocks.get(i).data,
									 saveBlocks.get(i).mat));
			}
			
			
		}
		
		calculateFirstBlock();
		calculateLastBlock();
		loadCorners(corners);
		loadInsideCorners(insideCorners);
		
		sizeX = Math.abs((int) corners[7].toVector().subtract(corners[0].toVector()).getX());
		sizeY = Math.abs((int) corners[7].toVector().subtract(corners[0].toVector()).getY());
		
		this.spawns = new Location[2];
		
		this.spawns[0] = new Location(mid.getWorld(),0.0,0.0,0.0);
		this.spawns[0].setX(spawns[0].getX()+loc.getX());
		this.spawns[0].setY(spawns[0].getY()+loc.getY());
		this.spawns[0].setZ(spawns[0].getZ()+loc.getZ());
		
		this.spawns[1] = new Location(mid.getWorld(),0.0,0.0,0.0);
		this.spawns[1].setX(spawns[1].getX()+loc.getX());
		this.spawns[1].setY(spawns[1].getY()+loc.getY());
		this.spawns[1].setZ(spawns[1].getZ()+loc.getZ());
		
		this.name = name;
		
		buildBlocks = new ArrayList<>();
		breakedBlocks = new ArrayList<>();
		
	}
	
	public String getBuilder()
	{
		return builder;
	}
	
	@SuppressWarnings("deprecation")
	public void buildBlock(Block b)
	{
		Location loc = b.getLocation();
		SaveBlock block = new SaveBlock(loc.getX(), loc.getY(), loc.getZ(), b.getData(), b.getType());
		
		buildBlocks.add(block);
	}
	
	@SuppressWarnings("deprecation")
	public void breakBlock(Block b)
	{
		Location loc = b.getLocation();
		SaveBlock block = new SaveBlock(loc.getX(), loc.getY(), loc.getZ(), b.getData(), b.getType());
		
		breakedBlocks.add(block);
	}

	public ItemStack getSymbol()
	{
		Material mat = Material.getMaterial(symbol);
		if(mat == null)
		{
			mat = Material.PAPER;
		}
		return new ItemStack(mat,1,symbolData);
	}
	
	public Location getSpawn1()
	{
		return spawns[0];
	}
	
	public Location getSpawn2()
	{
		return spawns[1];
	}
	
	public Location getBlock0()
	{
		SaveBlock block0 = blocks.get(0);
		
		return new Location(firstBlock.getWorld(), block0.x, block0.y, block0.z);
	}
	
	public String getName()
	{
		return name;
	}
	
	public Location getMid()
	{
		return mid;
	}
	
	public ArrayList<SaveBlock> getBlocks()
	{
		return blocks;
	}
	
	private void clearEntities()
	{
		// Clear all Entities
				for(Entity e :firstBlock.getWorld().getEntities())
				{
					if(!(e instanceof Player))
					{
						e.remove();
					}
				}
	}
	
	public void clear()
	{
		
		clearEntities();
		
		
		setBlocksBetween(corners[0], corners[6], Material.AIR, (byte)0);
	}
	
	private void calculateFirstBlock()
	{
		double minx,miny,minz;
		
		minx = blocks.get(0).x;
		miny = blocks.get(0).y;
		minz = blocks.get(0).z;
		
		for(int i =0;i<blocks.size();i++)
		{
			if(blocks.get(i).x < minx)
			{
				minx = blocks.get(i).x;
			}
			if(blocks.get(i).y < miny)
			{
				miny = blocks.get(i).y;
			}
			if(blocks.get(i).z < minz)
			{
				minz = blocks.get(i).z;
			}
		}
		
		for(int i =0;i<laterPlaceBlocks.size();i++)
		{
			if(laterPlaceBlocks.get(i).x < minx)
			{
				minx = laterPlaceBlocks.get(i).x;
			}
			if(laterPlaceBlocks.get(i).y < miny)
			{
				miny = laterPlaceBlocks.get(i).y;
			}
			if(laterPlaceBlocks.get(i).z < minz)
			{
				minz = laterPlaceBlocks.get(i).z;
			}
		}
		
		firstBlock = new Location(firstBlock.getWorld(), minx, miny, minz);
		
	}
	
	private void calculateLastBlock()
	{
		double maxx,maxy,maxz;
		
		maxx = blocks.get(0).x;
		maxy = blocks.get(0).y;
		maxz = blocks.get(0).z;
		
		for(int i =0;i<blocks.size();i++)
		{
			if(blocks.get(i).x > maxx)
			{
				maxx = blocks.get(i).x;
			}
			if(blocks.get(i).y > maxy)
			{
				maxy = blocks.get(i).y;
			}
			if(blocks.get(i).z > maxz)
			{
				maxz = blocks.get(i).z;
			}
		}
		
		for(int i =0;i<laterPlaceBlocks.size();i++)
		{
			if(laterPlaceBlocks.get(i).x > maxx)
			{
				maxx = laterPlaceBlocks.get(i).x;
			}
			if(laterPlaceBlocks.get(i).y > maxy)
			{
				maxy = laterPlaceBlocks.get(i).y;
			}
			if(laterPlaceBlocks.get(i).z > maxz)
			{
				maxz = laterPlaceBlocks.get(i).z;
			}
		}
		
		lastBlock = new Location(firstBlock.getWorld(), maxx, maxy, maxz);
		
	}
	
	private void loadCorners(Location[] corners)
	{
		if(loadedCorners)
		{
			return;
		}
		
		World world = firstBlock.getWorld();
		
		
		// Define all corners
		for(int i = 0;i<corners.length;i++)
		{
			double firstX,firstY,firstZ,lastX,lastY,lastZ;
			firstX = Math.floor(firstBlock.getX());
			firstY = Math.floor(firstBlock.getY());
			firstZ = Math.floor(firstBlock.getZ());
			lastX = Math.floor(lastBlock.getX());
			lastY = Math.floor(lastBlock.getY());
			lastZ = Math.floor(lastBlock.getZ());
			switch(i)
			{
			case 0:
				corners[i] = new Location(world, firstX, firstY,firstZ);
				break;
			case 1:
				corners[i] = new Location(world, firstX, firstY, lastZ);
				break;
			case 2:
				corners[i] = new Location(world, firstX, lastY, lastZ);
				break;
			case 3:
				corners[i] = new Location(world, firstX, lastY, firstZ);
				break;
			case 4:
				corners[i] = new Location(world,lastX , firstY, firstZ);
				break;
			case 5:
				corners[i] = new Location(world,lastX ,firstY , lastZ);
				break;
			case 6:
				corners[i] = new Location(world, lastX, lastY,lastZ);
				break;
			case 7:
				corners[i] = new Location(world, lastX, lastY, firstZ);
				break;
			}
		}
		
			corners[2].setY(corners[2].getY()+borderHeight);
			corners[3].setY(corners[3].getY()+borderHeight);
			corners[6].setY(corners[6].getY()+borderHeight);
			corners[7].setY(corners[7].getY()+borderHeight);
		
		
		// Offset all corners once
			if(!inside)
			{
				corners[0].setZ(corners[0].getZ()-1.0);
				corners[1].setZ(corners[1].getZ()+1.0);
				corners[2].setZ(corners[2].getZ()+1.0);
				corners[3].setZ(corners[3].getZ()-1.0);
				corners[4].setZ(corners[4].getZ()-1.0);
				corners[5].setZ(corners[5].getZ()+1.0);
				corners[6].setZ(corners[6].getZ()+1.0);
				corners[7].setZ(corners[7].getZ()-1.0);
			
			
				corners[0].setX(corners[0].getX()-1.0);
				corners[1].setX(corners[1].getX()-1.0);
				corners[2].setX(corners[2].getX()-1.0);
				corners[3].setX(corners[3].getX()-1.0);
				corners[4].setX(corners[4].getX()+1.0);
				corners[5].setX(corners[5].getX()+1.0);
				corners[6].setX(corners[6].getX()+1.0);
				corners[7].setX(corners[7].getX()+1.0);
			}
			
			
		
			corners[0].setY(corners[0].getY()-1.0);
			corners[1].setY(corners[1].getY()-1.0);
			corners[4].setY(corners[4].getY()-1.0);
			corners[5].setY(corners[5].getY()-1.0);
	
		loadedCorners = true;
	}
	
	public void loadGlassBorder()
	{

		Material mat = Material.getMaterial(boarderMat);
		if(mat == null)
		{
			System.out.println("Couldn' load Material: \"" + boarderMat + "\"\nIf you don't know the name:\n\t Search on spigotmc.org");
			mat = Material.GLASS;
		}
		
		// Loop through blocks between corners and set to glass
		if(!inside)
		{
			setBlocksBetween(corners[0], corners[2], mat, boarderData);
			setBlocksBetween(corners[4], corners[6], mat, boarderData);
			setBlocksBetween(corners[0], corners[7], mat, boarderData);
			setBlocksBetween(corners[1], corners[6], mat, boarderData);
			setBlocksBetween(corners[0], corners[5], mat, boarderData);
			setBlocksBetween(corners[3], corners[6], mat, boarderData);
		}
		else
		{
			setBlocksBetweenBlock(corners[0], corners[2], mat, boarderData);
			setBlocksBetweenBlock(corners[4], corners[6], mat, boarderData);
			setBlocksBetweenBlock(corners[0], corners[7], mat, boarderData);
			setBlocksBetweenBlock(corners[1], corners[6], mat, boarderData);
			setBlocksBetweenBlock(corners[0], corners[5], mat, boarderData);
			setBlocksBetweenBlock(corners[3], corners[6], mat, boarderData);
		}
		
		
		
		
	}
	
	@SuppressWarnings("deprecation")
	private void setBlocksBetweenBlock(Location loc1,Location loc2,Material mat,byte data)
	{
		World world = loc1.getWorld();
		
		double x=loc1.getX(),
			   y=loc1.getY(),
			   z=loc1.getZ();
		
		double maxx = loc2.getX(),
			   maxy = loc2.getY(),
			   maxz = loc2.getZ();
		
		double minx = x,
			   miny = y,
			   minz = z;
		
		// Block für Block durchgehen zwischen ersten und letzten Block
			
			
			for(z=minz;minz > maxz ? z >= maxz : z <= maxz;)
			{
				for(y=miny;miny > maxy ? y >= maxy : y <= maxy;)
				{
					for(x=minx;minx > maxx ? x >= maxx : x <= maxx;)
					{
						Block b = world.getBlockAt(new Location(world,x,y,z));
						if(b.getType().equals(Material.AIR))
						{
							b.setType(mat);
							b.setData(data);
						}
						
							
						
						if(minx > maxx)
						{
							x--;
						}
						else if(minx < maxx)
						{
							x++;
						}
						else
						{
							break;
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
					else
					{
						break;
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
				else
				{
					break;
				}
			}
	}
	
	
	@SuppressWarnings("deprecation")
	private void setBlocksBetween(Location loc1,Location loc2,Material mat,byte data)
	{
		World world = loc1.getWorld();
		
		double x=loc1.getX(),
			   y=loc1.getY(),
			   z=loc1.getZ();
		
		double maxx = loc2.getX(),
			   maxy = loc2.getY(),
			   maxz = loc2.getZ();
		
		double minx = x,
			   miny = y,
			   minz = z;
		
		// Block für Block durchgehen zwischen ersten und letzten Block
			
			
			for(z=minz;minz > maxz ? z >= maxz : z <= maxz;)
			{
				for(y=miny;miny > maxy ? y >= maxy : y <= maxy;)
				{
					for(x=minx;minx > maxx ? x >= maxx : x <= maxx;)
					{
						Block b = world.getBlockAt(new Location(world,x,y,z));
						if(!b.getType().equals(mat) || b.getData() != data)
						{
							b.setType(mat);
							b.setData(data);
						}
						
							
						
						if(minx > maxx)
						{
							x--;
						}
						else if(minx < maxx)
						{
							x++;
						}
						else
						{
							break;
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
					else
					{
						break;
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
				else
				{
					break;
				}
			}
	}

	@SuppressWarnings("deprecation")
	private void placeLaterBlocks()
	{
		World world = firstBlock.getWorld();
		for(int i = 0;i<laterPlaceBlocks.size();i++)
		{
			Location loc = new Location(world,laterPlaceBlocks.get(i).x,laterPlaceBlocks.get(i).y,laterPlaceBlocks.get(i).z);
			Block b = world.getBlockAt(loc);
			
			if(!b.getType().equals(laterPlaceBlocks.get(i).mat) || b.getData() != laterPlaceBlocks.get(i).data)
			{
				b.setType(laterPlaceBlocks.get(i).mat);
				b.setData(laterPlaceBlocks.get(i).data);
				
				if(b.getType().equals(Material.TORCH))
				{
					BlockFace tFace = getBlockFaceforTorch(b);
					if(tFace != null)
					{
						System.out.println("[" + getName() + "] Torch Face: " + tFace.toString()); 
						
						Torch t = (Torch)b.getState().getData();
						t.setFacingDirection(tFace);
						b.getState().update();
					}
				}
				
			}
			
		}
	}
	
	private boolean loadInsideCorners(Location[] corners)
	{
		if(loadedInsideCorners)
		{
			return false;
		}
		
		
		
			World world = firstBlock.getWorld();
		
		// Define all corners
					double firstX,firstY,firstZ,lastX,lastY,lastZ;
							firstX = Math.floor(firstBlock.getX());
							firstY = Math.floor(firstBlock.getY());
							firstZ = Math.floor(firstBlock.getZ());
							lastX = Math.floor(lastBlock.getX());
							lastY = Math.floor(lastBlock.getY());
							lastZ = Math.floor(lastBlock.getZ());
					
					insideCorners[0] = new Location(world, firstX, firstY,firstZ);
				
					/*insideCorners[1] = new Location(world, firstX, firstY, lastZ);
					
					insideCorners[2] = new Location(world, firstX, lastY, lastZ);
				
					insideCorners[3] = new Location(world, firstX, lastY, firstZ);
						
					
					insideCorners[4] = new Location(world,lastX , firstY, firstZ);
						
					
					insideCorners[5] = new Location(world,lastX ,firstY , lastZ);*/
					
					
					insideCorners[1] = new Location(world, lastX, lastY,lastZ);
						
					
					//insideCorners[7] = new Location(world, lastX, lastY, firstZ);
						
				
				
					//insideCorners[2].setY(insideCorners[2].getY()+19.0);
					//insideCorners[3].setY(insideCorners[3].getY()+19.0);
					insideCorners[1].setY(insideCorners[1].getY()+borderHeight-1);
					//insideCorners[7].setY(insideCorners[7].getY()+19.0);
					
					if(glass)
					{
						insideCorners[0].setY(insideCorners[0].getY()+1.0);
						/*insideCorners[1].setY(insideCorners[1].getY()+1.0);
						insideCorners[4].setY(insideCorners[4].getY()+1.0);
						insideCorners[5].setY(insideCorners[5].getY()+1.0);*/
						
						
						
						if(inside)
						{
							// Get one inside Z
						
							insideCorners[0].setZ(insideCorners[0].getZ()+1.0);
							/*insideCorners[3].setZ(insideCorners[3].getZ()+1.0);
							insideCorners[4].setZ(insideCorners[4].getZ()+1.0);
							insideCorners[7].setZ(insideCorners[7].getZ()+1.0);
							insideCorners[1].setZ(insideCorners[1].getZ()-1.0);
							insideCorners[2].setZ(insideCorners[2].getZ()-1.0);
							insideCorners[5].setZ(insideCorners[5].getZ()-1.0);*/
							insideCorners[1].setZ(insideCorners[1].getZ()-1.0);
							
							// get one inside X
							
							insideCorners[0].setX(insideCorners[0].getX()+1.0);
							/*insideCorners[1].setX(insideCorners[1].getX()+1.0);
							insideCorners[2].setX(insideCorners[2].getX()+1.0);
							insideCorners[3].setX(insideCorners[3].getX()+1.0);
							insideCorners[4].setX(insideCorners[4].getX()-1.0);
							insideCorners[5].setX(insideCorners[5].getX()-1.0);*/
							insideCorners[1].setX(insideCorners[1].getX()-1.0);
							//insideCorners[7].setX(insideCorners[7].getX()-1.0);
						}
						
					}
	
				
				return true;
	}
	
	public boolean isInside(Location loc)
	{
		// AABB
		if(loc.getX() >= insideCorners[0].getX() && loc.getX() <= insideCorners[1].getX() &&
		   loc.getY() >= insideCorners[0].getY() && loc.getY() <= insideCorners[1].getY() &&
		   loc.getZ() >= insideCorners[0].getZ() && loc.getZ() <= insideCorners[1].getZ() )
		{
			return true;
		}
		
		
		return false;
	}
	
	public Location getFirstBlock()
	{
		return firstBlock;
	}
	
	public Location getLastBlock()
	{
		return lastBlock;
	}
	
	@SuppressWarnings("unused")
	private void clearAllObsidian()
	{
		World world = corners[0].getWorld();
		
		double x=corners[0].getX(),
			   y=corners[0].getY(),
			   z=corners[0].getZ();
		
		double maxx = corners[6].getX(),
			   maxy = corners[6].getY(),
			   maxz = corners[6].getZ();
		
		double minx = x,
			   miny = y,
			   minz = z;
		
		// Block für Block durchgehen zwischen ersten und letzten Block
			
			
			for(z=minz;minz > maxz ? z >= maxz : z <= maxz;)
			{
				for(y=miny;miny > maxy ? y >= maxy : y <= maxy;)
				{
					for(x=minx;minx > maxx ? x >= maxx : x <= maxx;)
					{
						Block b = world.getBlockAt(new Location(world,x,y,z));
						
						if(b.getType().equals(Material.OBSIDIAN))
						{
							b.setType(Material.AIR);
						}
						
						if(minx > maxx)
						{
							x--;
						}
						else if(minx < maxx)
						{
							x++;
						}
						else
						{
							break;
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
					else
					{
						break;
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
				else
				{
					break;
				}
			}
	}
	
	@SuppressWarnings("unused")
	private void clearAllCobblestone()
	{
		World world = corners[0].getWorld();
		
		double x=corners[0].getX(),
			   y=corners[0].getY(),
			   z=corners[0].getZ();
		
		double maxx = corners[6].getX(),
			   maxy = corners[6].getY(),
			   maxz = corners[6].getZ();
		
		double minx = x,
			   miny = y,
			   minz = z;
		
		// Block für Block durchgehen zwischen ersten und letzten Block
			
			
			for(z=minz;minz > maxz ? z >= maxz : z <= maxz;)
			{
				for(y=miny;miny > maxy ? y >= maxy : y <= maxy;)
				{
					for(x=minx;minx > maxx ? x >= maxx : x <= maxx;)
					{
						Block b = world.getBlockAt(new Location(world,x,y,z));
						
						if(b.getType().equals(Material.COBBLESTONE))
						{
							b.setType(Material.AIR);
						}
						
						if(minx > maxx)
						{
							x--;
						}
						else if(minx < maxx)
						{
							x++;
						}
						else
						{
							break;
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
					else
					{
						break;
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
				else
				{
					break;
				}
			}
	}
	
	@SuppressWarnings("deprecation")
	private boolean isSolid(Block b)
	{
		return net.minecraft.server.v1_8_R3.Block.getById(b.getTypeId()).getMaterial().isSolid();
	}
	
	private BlockFace getBlockFaceforTorch(Block b)
	{
		Block b1;
		
		
		for(int i = 0;i<BlockFace.values().length;i++)
		{
			if(BlockFace.values()[i].equals(BlockFace.UP))
				continue;
			
			b1 = b.getRelative(BlockFace.values()[i]);
			if(b1 != null && isSolid(b1))
			{
				switch(BlockFace.values()[i])
				{
				case DOWN:case NORTH:case SOUTH:case EAST:case WEST:
					return BlockFace.values()[i].getOppositeFace();
				default:continue;
				}
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public boolean reload(Player me,boolean clear)
	{
		if(Reload != null)
		{
			return false;
		}
		
		loaded = false;
		
		World world = firstBlock.getWorld();
		if(clear)
			clear();
		
		Reload = Bukkit.getScheduler().runTaskTimer(plugin, new ReloadRun(world) {
			
			@Override
			public void run()
			{
				
				for(int start = 0;ReloadIteration<blocks.size() && ReloadIteration<start+100;ReloadIteration++)
				{
					if(!(ReloadIteration<blocks.size())) // Wenn fertig mit MapBlöcke
					{
						Bukkit.getScheduler().cancelTask(reloadFinished(me));
					}
					
					MapManager.isLoading = true;
				
					Location loc = new Location(world,blocks.get(ReloadIteration).x,blocks.get(ReloadIteration).y,blocks.get(ReloadIteration).z);
					Block b = myWorld.getBlockAt(loc);
					
					if(!b.getType().equals(blocks.get(ReloadIteration).mat) || b.getData() != blocks.get(ReloadIteration).data)
					{
						b.setType(blocks.get(ReloadIteration).mat);
						b.setData(blocks.get(ReloadIteration).data);
					}
					
					
					start++;
				}
				
				if(!(ReloadIteration<blocks.size())) // Wenn fertig mit MapBlöcke
				{
					Bukkit.getScheduler().cancelTask(reloadFinished(me));
				}
				
				
			}
		}, 0, 10);
		
			
		
		
		
		
		return true;
		
	}
	
	@SuppressWarnings("deprecation")
	public boolean reload(Player me)
	{	
		MapManager.isLoading = true;
		loaded = false;
		
		clearEntities();
		//clearAllLiquids();
		//clearAllCobblestone();
		//clearAllObsidian();
		loadGlassBorder();
		
			for(int i = 0;i<buildBlocks.size();i++)
			{
				
				World world = firstBlock.getWorld();
				
				Location loc = new Location(world,buildBlocks.get(i).x,buildBlocks.get(i).y,buildBlocks.get(i).z);
				
					if(!world.getBlockAt(loc).getType().equals(Material.AIR))
						world.getBlockAt(loc).setType(Material.AIR);
				
			}
			
			for(int i = 0;i<blocks.size();i++)
			{
				World world = firstBlock.getWorld();
	
					Location loc = new Location(world,blocks.get(i).x,blocks.get(i).y,blocks.get(i).z);
					if(!world.getBlockAt(loc).getType().equals(blocks.get(i).mat) || !(world.getBlockAt(loc).getData() == blocks.get(i).data))
					{
						world.getBlockAt(loc).setType(blocks.get(i).mat);
						world.getBlockAt(loc).setData(blocks.get(i).data);
					}
				
			}
			
			for(int i = 0;i<laterPlaceBlocks.size();i++)
			{
				World world = firstBlock.getWorld();
				
				Location loc = new Location(world,laterPlaceBlocks.get(i).x,laterPlaceBlocks.get(i).y,laterPlaceBlocks.get(i).z);
				if(!world.getBlockAt(loc).getType().equals(laterPlaceBlocks.get(i).mat) || !(world.getBlockAt(loc).getData() == laterPlaceBlocks.get(i).data))
				{
					world.getBlockAt(loc).setType(laterPlaceBlocks.get(i).mat);
					world.getBlockAt(loc).setData(laterPlaceBlocks.get(i).data);
				}
					
			}
		
			clearEntities();
		
		
		if(me != null)
		{
			me.sendMessage(ChatColor.BLUE + name + ChatColor.YELLOW + " has been loaded");
		}
		
		System.out.println("[" + MapManager.plugin.getName() + "] " + "loaded " + name);
		
		breakedBlocks.clear();
		buildBlocks.clear();
		
		loaded = true;
		MapManager.isLoading = false;
		
		return true;
		
	}
	
	
	@SuppressWarnings("unused")
	private void clearAllLiquids()
	{
		World world = corners[0].getWorld();
		
		double x=corners[0].getX(),
			   y=corners[0].getY(),
			   z=corners[0].getZ();
		
		double maxx = corners[6].getX(),
			   maxy = corners[6].getY(),
			   maxz = corners[6].getZ();
		
		double minx = x,
			   miny = y,
			   minz = z;
		
		// Block für Block durchgehen zwischen ersten und letzten Block
			
			
			for(z=minz;minz > maxz ? z >= maxz : z <= maxz;)
			{
				for(y=miny;miny > maxy ? y >= maxy : y <= maxy;)
				{
					for(x=minx;minx > maxx ? x >= maxx : x <= maxx;)
					{
						Block b = world.getBlockAt(new Location(world,x,y,z));
						
						if(b.getType().equals(Material.STATIONARY_WATER) || b.getType().equals(Material.WATER) ||
						   b.getType().equals(Material.LAVA) || b.getType().equals(Material.STATIONARY_LAVA))
						{
							b.setType(Material.AIR);
						}
						
						if(minx > maxx)
						{
							x--;
						}
						else if(minx < maxx)
						{
							x++;
						}
						else
						{
							break;
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
					else
					{
						break;
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
				else
				{
					break;
				}
			}
	}
	
	public boolean isLoaded()
	{
		return loaded;
	}
	
	private int reloadFinished(Player me)
	{
		clearEntities();
		
		placeLaterBlocks();
		if(glass)
			loadGlassBorder();
		
		int id = Reload.getTaskId();
		Reload = null;
		ReloadIteration = 0;
		
		
		System.out.println("[" + MapManager.plugin.getName() + "] " + "loaded " + name);
		
		if(me != null)
		{
			me.sendMessage(ChatColor.BLUE + name + ChatColor.YELLOW + " has been placed");
		}
		
		buildBlocks.clear();
		breakedBlocks.clear();
		
		MapManager.isLoading = false;
		
		loaded = true;
		
		return id;
	}

	public int getSizeX()
	{
		return sizeX;
	}

	public int getSizeY()
	{
		return sizeY;
	}
}
