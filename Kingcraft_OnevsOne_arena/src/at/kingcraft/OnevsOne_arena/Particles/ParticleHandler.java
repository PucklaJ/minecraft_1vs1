package at.kingcraft.OnevsOne_arena.Particles;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.inventivetalent.particle.ParticleEffect;

import at.kingcraft.OnevsOne_arena.Kits.Kit;
import at.kingcraft.OnevsOne_arena.Kits.KitSettings;
import at.kingcraft.OnevsOne_arena.Listener.DuelListener;
import at.kingcraft.OnevsOne_setup.MainClass;

public class ParticleHandler implements Listener
{
	private static ArrayList<Arrow> arrows;
	private static ArrayList<UUID> canPerformCrititcal;
	
	
	public ParticleHandler()
	{
		arrows = new ArrayList<>();
		canPerformCrititcal = new ArrayList<>();
		
		Bukkit.getScheduler().runTaskTimer(MainClass.getInstance(), new Runnable() {
			
			@Override
			public void run()
			{
				checkArrows();
			}
		}, 1, 1);
	}
	
	private void checkArrows()
	{
		Random r = new Random();
		
		for(int i = 0;i<arrows.size();i++)
		{
			for(int j = 0;j<5;j++)
			{
				ParticleEffect.VILLAGER_HAPPY.send(Bukkit.getOnlinePlayers(),arrows.get(i).getLocation(), r.nextDouble()*0.25 - 0.125, r.nextDouble()*0.25 - 0.125, r.nextDouble()*0.25 - 0.125,r.nextDouble()*0.25 - 0.125, 2);
			}
		}
	}
	
	@EventHandler
	public void onBow(EntityShootBowEvent e)
	{
		if(e.getEntity() instanceof Player && e.getProjectile() instanceof Arrow)
		{
			Player p = (Player)e.getEntity();
			
			Kit kit = DuelListener.getKit(p);
			
			if(kit != null)
			{
				if(kit.getSettings().contains(KitSettings.MORE_PARTICLES))
				{
					arrows.add((Arrow)e.getProjectile());
				}
			}
		}
	}
	
	@EventHandler
	public void onHitGround(ProjectileHitEvent e)
	{
		if(e.getEntity() instanceof Arrow)
		{
			for(int i = 0;i<arrows.size();i++)
			{
				if(arrows.get(i).getEntityId() == e.getEntity().getEntityId())
				{
					arrows.remove(i);
					break;
				}
			}
		}
	}
	
	private boolean hasBlindness(Player p)
	{
		for(PotionEffect pe : p.getActivePotionEffects())
		{
			if(pe.getType().equals(PotionEffectType.BLINDNESS))
				return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("deprecation")
	private boolean isCritical(Player p)
	{
		return !p.isOnGround() && p.getVelocity().getY() < 0.0 && canPerformCrititcal.contains(p.getUniqueId()) && !p.isInsideVehicle() && !hasBlindness(p);
	}
	
	@EventHandler
	public void ladder(PlayerMoveEvent e)
	{
		Player p = e.getPlayer();
		if(p.getLocation().getBlock().getType().equals(Material.LADDER) || p.getLocation().getBlock().getType().equals(Material.VINE) || p.getLocation().getBlock().getType().equals(Material.WATER))
		{
			canPerformCrititcal.remove(p.getUniqueId());
		}
		else
		{
			if(!canPerformCrititcal.contains(p.getUniqueId()))
				canPerformCrititcal.add(p.getUniqueId());
		}
	}
	
	@EventHandler
	public void onPlayerDamagePlayer(EntityDamageByEntityEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			Player damager = null;
			
			if(e.getDamager() instanceof Player)
			{
				damager = (Player)e.getDamager();
			}
			
			if(damager != null)
			{
				Kit kit = DuelListener.getKit(damager);
				if(kit != null && kit.getSettings().contains(KitSettings.MORE_PARTICLES))
				{
					Random r = new Random();
					
					if(isCritical(damager))
					{
						for(int j = 0;j<10;j++)
						{
							ParticleEffect.CRIT.send(Bukkit.getOnlinePlayers(),e.getEntity().getLocation().clone().add(new Vector(0.0,1.0,0.0)), r.nextDouble() - 0.5, r.nextDouble()*0.5, r.nextDouble() - 0.5,0.5,3);
						}
					}
					else
					{
						for(int j = 0;j<10;j++)
						{
							ParticleEffect.HEART.send(Bukkit.getOnlinePlayers(),e.getEntity().getLocation().clone().add(new Vector(0.0,1.0,0.0)), r.nextDouble()*0.3 - 0.15, r.nextDouble()*0.5, r.nextDouble()*0.3 - 0.15,0.05,3);
						}
					}
				}
			}
		}
	}

}
