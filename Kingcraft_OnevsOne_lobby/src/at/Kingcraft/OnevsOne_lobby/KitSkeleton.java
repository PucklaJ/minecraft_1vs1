package at.Kingcraft.OnevsOne_lobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSkeleton;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.World;

public class KitSkeleton extends EntitySkeleton
{
	private ArmorStand am;
	private Location loc;
	private boolean needsReload;
	
	public KitSkeleton(World world,Location loc)
	{
		super(world);
		this.loc = loc;
		
		needsReload = true;
		
		reload();
		
	}
	
	@Override
	public void move(double d0,double d1, double d2)
	{
		super.move(0.0, 0.0, 0.0);
	}
	
	@Override
	public void makeSound(String s,float f,float f1)
	{
		
	}
	
	public void reloadChunk()
	{
		if(!needsReload)
		{
			getBukkitEntity().getLocation().getChunk().unload(true, false);
			getBukkitEntity().getLocation().getChunk().load();
			
			world.addEntity(this);
		}
	}
	
	public void reload()
	{
		if(needsReload && !Bukkit.getServer().getOnlinePlayers().isEmpty())
		{
			setLocation(loc.getX(),loc.getY(), loc.getZ(), loc.getYaw(),loc.getPitch());
			
			
			((CraftSkeleton)this.getBukkitEntity()).getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
			((CraftSkeleton)this.getBukkitEntity()).getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
			((CraftSkeleton)this.getBukkitEntity()).getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
			((CraftSkeleton)this.getBukkitEntity()).getEquipment().setItemInHand(new ItemStack(Material.BOW));
			
			am = (ArmorStand)Bukkit.getWorld(MainClass.getInstance().getConfig().getString("World.Name")).spawn(loc, ArmorStand.class);
			
			am.setVisible(false);
			am.setCustomName(ChatColor.YELLOW + "Kit-Einstellungen");
			am.setCustomNameVisible(true);
			am.setGravity(false);
			
			world.addEntity(this);
			
			needsReload = false;
		}
	}
	

}
