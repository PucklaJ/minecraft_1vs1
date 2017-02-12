package at.Kingcraft.OnevsOne_lobby.WaitingSnake;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftZombie;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.World;

public class WaitingEntity extends EntityZombie
{
	
	private ArmorStand am;
	private boolean needsReload;
	private Location loc;
	
	public WaitingEntity(World world,Location loc)
	{
		super(world);
		this.loc = loc;
		
		needsReload = true;
		
		reload();
	}
	
	
	@Override
	public void move(double d0, double d1, double d2)
	{
		super.move(0.0, 0.0, 0.0);
	}
	
	public void reloadChunk()
	{
		if(!needsReload)
		{
			world.addEntity(this);
		}
	}
	
	public void reload()
	{
		if(needsReload && !Bukkit.getServer().getOnlinePlayers().isEmpty())
		{
			setLocation(loc.getX(),loc.getY(), loc.getZ(), loc.getYaw(),loc.getPitch());
			
			ItemStack bHead = new ItemStack(Material.SKULL_ITEM, 1,(short)3);
			{
				SkullMeta im = (SkullMeta) bHead.getItemMeta();
				im.setOwner("Gannicus");
				bHead.setItemMeta(im);
			}
			
			
			((CraftZombie)this.getBukkitEntity()).getEquipment().setHelmet(bHead);
			((CraftZombie)this.getBukkitEntity()).getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
			((CraftZombie)this.getBukkitEntity()).getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
			((CraftZombie)this.getBukkitEntity()).getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
			((CraftZombie)this.getBukkitEntity()).getEquipment().setItemInHand(new ItemStack(Material.IRON_SWORD));
			
			am = (ArmorStand)Bukkit.getWorld(MainClass.getInstance().getConfig().getString("World.Name")).spawn(loc, ArmorStand.class);
			
			am.setVisible(false);
			am.setCustomName(ChatColor.YELLOW + "Warteschlange");
			am.setCustomNameVisible(true);
			am.setGravity(false);
			
			
			world.addEntity(this);
			
			needsReload = false;
		}
	}
	
	@Override
	public void makeSound(String s, float f, float f1)
	{
		
	}
}
