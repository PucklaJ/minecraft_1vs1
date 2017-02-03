package at.kingcraft.OnevsOne_setup;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import at.kingcraft.OnevsOne_setup.Maps.Map;
import at.kingcraft.OnevsOne_setup.Maps.MapManager;

public class MapListener implements Listener
{
	@EventHandler
	(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent e)
	{	
		if(e.isCancelled())
		{
			return;
		}
		
		if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE) && !e.getPlayer().hasPermission("world.place"))
		{
			e.setCancelled(true);
			return;
		}
		
		ArrayList<Map> maps = MapManager.getMaps();
		
		for(int i = 0;i<maps.size();i++)
		{
			if(maps.get(i).isInside(e.getBlock().getLocation()))
			{	
				maps.get(i).buildBlock(e.getBlock());
				return;
			}
		}
	}
	
	@EventHandler
	(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent e)
	{
		if(e.isCancelled())
		{
			return;
		}
		
		if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE) && !e.getPlayer().hasPermission("world.destroy"))
		{
			e.setCancelled(true);
			return;
		}
		
		ArrayList<Map> maps = MapManager.getMaps();
		
		for(int i = 0;i<maps.size();i++)
		{
			if(maps.get(i).isInside(e.getBlock().getLocation()))
			{	
				maps.get(i).breakBlock(e.getBlock());
				return;
			}
		}
		
	}
	
	@EventHandler
	(priority = EventPriority.HIGHEST)
	public void onFireSpread(BlockIgniteEvent e)
	{
		ArrayList<Map> maps = MapManager.getMaps();
		
		for(int i = 0;i<maps.size();i++)
		{
			if(maps.get(i).isInside(e.getBlock().getLocation()))
			{	
				maps.get(i).buildBlock(e.getBlock());
				return;
			}
		}
	}
	
	@EventHandler
	(priority = EventPriority.HIGHEST)
	public void onWaterLavaPlace(PlayerInteractEvent e)
	{
		if(e.isCancelled())
			return;
		
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			ItemStack is = e.getPlayer().getItemInHand();
			if(is != null && is.getType() != null && (is.getType().equals(Material.WATER_BUCKET) || is.getType().equals(Material.LAVA_BUCKET)))
			{
				ArrayList<Map> maps = MapManager.getMaps();
				
				for(int i = 0;i<maps.size();i++)
				{
					if(maps.get(i).isInside(e.getClickedBlock().getLocation()))
					{
						maps.get(i).buildBlock(e.getClickedBlock().getRelative(e.getBlockFace()));
					}
				}
			}
		}
	}
	
	@EventHandler
	(priority = EventPriority.HIGHEST)
	public void onLavaWaterTransform(BlockFromToEvent e)
	{
			ArrayList<Map> maps = MapManager.getMaps();
			for(int i = 0;i<maps.size();i++)
			{
				if(maps.get(i).isInside(e.getToBlock().getLocation()))
				{
					maps.get(i).buildBlock(e.getToBlock());
				}
				if(maps.get(i).isInside(e.getBlock().getLocation()))
				{
					maps.get(i).buildBlock(e.getBlock());
				}
			}
	}
}
