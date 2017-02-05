package at.kingcraft.OnevsOne_arena.Listener;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import MySQL.MySQL;
import at.kingcraft.OnevsOne_arena.MainClass;
import at.kingcraft.OnevsOne_arena.Sounds;
import at.kingcraft.OnevsOne_arena.Challenges.ChallangeManager;
import at.kingcraft.OnevsOne_arena.Challenges.Challenge;
import at.kingcraft.OnevsOne_arena.Commands.GiveUpCommand;
import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_arena.Duels.DuelManager;
import at.kingcraft.OnevsOne_arena.Duels.DuelSpec;
import at.kingcraft.OnevsOne_arena.Kits.Kit;
import at.kingcraft.OnevsOne_arena.Kits.KitSettings;
import at.kingcraft.OnevsOne_arena.Menus.MenuManager;
import at.kingcraft.OnevsOne_arena.Messaging.Messages;
import at.kingcraft.OnevsOne_arena.Messaging.Messenger;
import at.kingcraft.OnevsOne_arena.Tournaments.Spectator;
import at.kingcraft.OnevsOne_arena.Tournaments.Tournament;
import at.kingcraft.OnevsOne_arena.Tournaments.TournamentManager;
import at.kingcraft.OnevsOne_arena.Waiting.WaitingHouse;
import at.kingcraft.OnevsOne_arena.WaitingSnake.Settings;
import at.kingcraft.OnevsOne_setup.Maps.Map;
import at.kingcraft.OnevsOne_setup.Maps.MapManager;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("deprecation")
public class DuelListener implements Listener {
	
	private static MainClass plugin;
	private boolean firstJoin = false;
	public static ArrayList<UUID> onSpawnCalled;
	public static ArrayList<Spectator> specOnRespawn;
	public static ArrayList<UUID> canDoubleJump;
	public static HashMap<UUID,Player> lastDamager;
	public static ArrayList<String> lobbyServerToChatTo;
	public static int lastTournamentID = -1;
	
	public DuelListener(MainClass plugin)
	{
		DuelListener.plugin = plugin;
		onSpawnCalled = new ArrayList<>();
		specOnRespawn = new ArrayList<>();
		canDoubleJump = new ArrayList<>();
		lastDamager = new HashMap<>();
		lobbyServerToChatTo = new ArrayList<>();
	}
	
	
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		
		if(!firstJoin)
		{
			firstJoin = true;
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
				
				@Override
				public void run() {
				
					Messenger.sendMessage(p, "BungeeCord", "GetServer", (String[])null);
					
				}
			}, 20*1);
			
		}
		
		Settings.loadSettings(p);
		
		e.setJoinMessage("");
		
		onSpawn(p,false,true);
		
	}
	
	public static void onSpawn(Player p,boolean asynchron,boolean setHealthIfZero)
	{
		if(onSpawnCalled.contains(p.getUniqueId()))
		{
			return;
		}
		
		onSpawnCalled.add(p.getUniqueId());
		specOnRespawn.remove(p.getUniqueId());
		
		if(asynchron)
		{
			Bukkit.getScheduler().runTask(plugin, new HideAllSpecsRun(p, TournamentManager.getSpecs()));
		}
		else
		{
			TournamentManager.hideAllSpectators(p);
		}
			
		
		if(!asynchron)
		{
			unHideAllPlayers(p);
		}
		else
		{
			Bukkit.getScheduler().runTaskLater(plugin,new UnHideAllPlayersRun(p),20*2);
		}
		
		setupSpawnStats(p,asynchron,true,setHealthIfZero);
		organizeSpawn(p,asynchron);
	}
	
	private static void unHideAllPlayers(Player p)
	{
		for(Player p1 : Bukkit.getServer().getOnlinePlayers())
		{
			if(p1.getUniqueId().equals(p.getUniqueId()))
			{
				continue;
			}
			
			if(!TournamentManager.isSpectator(p,true))
			{
				p1.showPlayer(p);
			}
				
			if(!TournamentManager.isSpectator(p1,true))
			{
				p.showPlayer(p1);
			}
				
		}
	}
	
	public static void addLobbyServerChat(String server)
	{
		if(!lobbyServerToChatTo.contains(server))
		{
			lobbyServerToChatTo.add(server);
		}
	}
	
	public static void removeFromOnSpawn(UUID uuid)
	{
		onSpawnCalled.remove(uuid);
	}
	
	public static void setupSpawnStats(Player p,boolean asynchron,boolean creative,boolean setHealthIfZero)
	{
		// Setup the stats
		if(p.getHealth() != 0.0 || setHealthIfZero)
			p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);
		p.setFireTicks(0);
		p.setVelocity(new Vector(0.0, 0.0, 0.0));
		for(PotionEffect pe : p.getActivePotionEffects())
		{
			p.removePotionEffect(pe.getType());
		}
		p.setFallDistance(0);
		p.setExp(0);
		p.setLevel(0);
		if(!asynchron)
		{
			p.setFlying(false);
			p.setSprinting(false);
			if(creative && !p.getGameMode().equals(GameMode.CREATIVE))
				p.setGameMode(GameMode.CREATIVE);
		}
		else
		{
			Bukkit.getScheduler().runTask(plugin, new Runnable() {
				
				@Override
				public void run()
				{
					p.setFlying(false);
					p.setSprinting(false);
					if(creative && !p.getGameMode().equals(GameMode.CREATIVE))
						p.setGameMode(GameMode.CREATIVE);
				}
			});
		}
	}
	
	private static String getHomeServerOfSpectator(Player p)
	{	
		try
		{
			String home = "";
			
			PreparedStatement ps = MySQL.getInstance().getConnection().prepareStatement("SELECT HomeServer FROM Duel_JoinSpectators WHERE UUID = ?");
			ps.setString(1, p.getUniqueId().toString());
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				home = rs.getString(1);
			}
			
			ps = MySQL.getInstance().getConnection().prepareStatement("DELETE FROM Duel_JoinSpectators WHERE UUID = ?");
			ps.setString(1, p.getUniqueId().toString());
			
			ps.executeUpdate();
			
			return home;
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return "";
		
	}
	
	private static void organizeSpawn(Player p,boolean asynchron)
	{
		Spectator spectateID = TournamentManager.checkSpectator(p);
		if(spectateID != null)
		{
			TournamentManager.addSpectator(spectateID,true,true);
			return;
		}
		
		Challenge c;
		
		if(ChallangeManager.getAllChallenges().size() == 0)
		{
			c =  ChallangeManager.newChallenge(p);
		}
		else
		{
			c = ChallangeManager.getChallenge(p);
		}
		
		if(c == null)
		{
			// Add To Spectators if there's a duel
			
			Duel d = DuelManager.getFirstDuel();
			
			if(d!= null)
			{
				String homeServer = getHomeServerOfSpectator(p);
				
				if(homeServer.equals(""))
				{
					homeServer = d.getHomeServer(d.getP1().get(0));
				}
				
				if(d.isTournament())
				{
					if(!TournamentManager.isSpectator(p, true))
						TournamentManager.addSpectator(new Spectator(p, d.getTournamentID(), homeServer), true, true);
				}
				else
				{
					d.addSpectator(new DuelSpec(p,homeServer), true);
				}
			}
			else
			{
				p.sendMessage(ChatColor.RED + "Nothing Found");
			}
			
		}
		else
		{
				Map map = MapManager.getMap(c.arenaName);
			
				int role = ChallangeManager.getRole(p, c);
				if(role != Challenge.NO_ID || role != Challenge.NO_ROLE)
				{
					if(map == null)
					{
						p.sendMessage(Messages.thereAreNoMaps);
						
						loadMapIfNotThere(p,c);
						
						map = MapManager.getMap(c.arenaName);
						
					}
					
					
					Duel d = DuelManager.newDuel(c);
					
					if(map != null)
					{
						if(role == Challenge.IS_CHALLANGER)
						{
							//if(p.getHealth() != 0.0)
							{
								// Teleportation
								if(asynchron && !map.getSpawn1().getWorld().equals(p.getWorld()))
								{
									Bukkit.getScheduler().runTask(plugin, new TeleportPlayerRun(p, map.getSpawn1()));
								}
								else
								{
									if(!d.isFFA())
										p.teleport(map.getSpawn1());
									else
										p.teleport(map.getMid());
								}
							}
							
							d.addPlayer(p, 1,asynchron);
						}
						else if(role == Challenge.IS_CHALLANGED)
						{
							//if(p.getHealth() != 0.0)
							{
								// Teleportation
								if(asynchron && !map.getSpawn2().getWorld().equals(p.getWorld()))
								{
									Bukkit.getScheduler().runTask(plugin, new TeleportPlayerRun(p, map.getSpawn2()));
								}
								else
								{
									if(!d.isFFA())
										p.teleport(map.getSpawn2());
									else
										p.teleport(map.getMid());
								}
							}
							
							d.addPlayer(p, 2,asynchron);
						}
					}
					else
					{
						Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
							
							@Override
							public void run() {
								ChallangeManager.deleteChallenge(c.ID);
								
								if(role == Challenge.IS_CHALLANGER)
								{
									d.addPlayer(p, 1,asynchron);
								}
								else if(role == Challenge.IS_CHALLANGED)
								{
									d.addPlayer(p, 2,asynchron);
								}
								
								GiveUpCommand.teleportBackToLobby(c,p);
								DuelManager.deleteDuel(p);
								
							}
						}, 20);
						
					}
					
					return;
				}
				else
				{
					p.sendMessage(ChatColor.RED +  "No Role");
				}
				
				if(!WaitingHouse.getWaitngSpawn().getWorld().equals(p.getWorld()) && asynchron)
				{
					Bukkit.getScheduler().runTask(plugin, new Runnable() {
						
						@Override
						public void run()
						{
							p.teleport(WaitingHouse.getWaitngSpawn());	
						}
					});
				}
				else
				{
					p.teleport(WaitingHouse.getWaitngSpawn());
				}
				
		}
	}

	private static boolean loadMapIfNotThere(Player p,Challenge c)
	{
		Location loc = null;
		if(plugin.getMySQL().isConnected())
		{
			PreparedStatement ps;
			
			try
			{
				ps = plugin.getMySQL().getConnection().prepareStatement("SELECT X,Y,Z FROM Duel_Maps WHERE Name = ?");
				ps.setString(1, c.arenaName);
				ResultSet rs = ps.executeQuery();
				while(rs.next())
				{
					loc = new Location(p.getWorld(),rs.getDouble(1),rs.getDouble(2),rs.getDouble(3));
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			
		}
		
		
		Map m = MapManager.loadMap(c.arenaName, loc);
		if(loc == null)
		{
			System.out.println("[IMPORTANT] Couldn't load map " + c.arenaName);
			System.out.println("[VERY IMPORTANT] Couldn't find map in MySQL");
			return false;
		}
		if(m == null)
		{
			System.out.println("[IMPORTANT] Couldn't load map " + c.arenaName);
			System.out.println("[VERY IMPORTANT] Maybe the file isn't there");
			return false;
		}
		else
		{
			MapManager.addMap(m);
			MapManager.writeToMapConfig(m);
			m.reload(null, true);
		}
		
		return true;
		
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player p = e.getEntity();
		Duel d = DuelManager.getDuel(p);
		if(d!= null)
		{
			if(d.isSpectator(p,false))
			{
				return;
			}
			
			d.handleDeath(p,getKit(p).getSettings().contains(KitSettings.NO_KNOCKBACK) ? lastDamager.get(p.getUniqueId()) : p.getKiller(),false);
			
			e.setDeathMessage("");
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		Player p = e.getPlayer();
		Duel d = DuelManager.getDuel(p);
		
		
		if(d!= null)
		{
			if(d.isSpectator(p,false))
			{
				return;
			}
			
			if(d.isFFA())
			{
				return;
			}
			
			if(d.getMap() != null)
			{
				if(!d.isStarted())
				{
					p.setFlying(false);
					p.setSprinting(false);
					
					int role = d.getRole(p);
					
					double distance;
					Location pLoc = p.getLocation();
					
					if(role == Duel.P1)
					{
						Location loc = new Location(p.getWorld(),d.getMap().getSpawn1().getX(),d.getMap().getSpawn1().getY(),
													d.getMap().getSpawn1().getZ(),d.getMap().getSpawn1().getYaw(),d.getMap().getSpawn1().getPitch());
						
						distance = loc.distance(pLoc);
						
						if(distance >= 1.0)
							p.teleport(loc);
					}
					else if(role == Duel.P2)
					{
						Location loc = new Location(p.getWorld(),d.getMap().getSpawn2().getX(),d.getMap().getSpawn2().getY(),
													d.getMap().getSpawn2().getZ(),d.getMap().getSpawn2().getYaw(),d.getMap().getSpawn2().getPitch());
						
						
						distance = loc.distance(pLoc);
						
						if(distance >= 1.0)
							p.teleport(loc);
					}
				}
			}
			
		}
	}
	@EventHandler
	public void onBurned(BlockBurnEvent e)
	{
		ArrayList<Map> maps = MapManager.getMaps();
		
		Duel d = DuelManager.getFirstDuel();
		
		for(int i = 0;i<maps.size();i++)
		{
			if(d == null)
			{
				e.setCancelled(true);
			}
			else if(!maps.get(i).getName().equals(d.getMap().getName()))
			{
				e.setCancelled(true);
			}
			else if(!maps.get(i).isInside(e.getBlock().getLocation()))
			{
				e.setCancelled(true);
			}
		}
	}
	
	private boolean nothing(ItemStack is)
	{
		return is == null || is.getType() == null || is.getType().equals(Material.AIR);
	}
	
	private boolean hasArmor(Player p)
	{
		EntityEquipment ee = p.getEquipment();
		ItemStack boots = ee.getBoots();
		ItemStack chestplate = ee.getChestplate();
		ItemStack pants = ee.getLeggings();
		ItemStack helmet = ee.getHelmet();
		
		if(nothing(boots) && nothing(chestplate) && nothing(pants) && nothing(helmet))
		{
			return false;
		}
		
		return true;
	}
	
	@EventHandler
	public void onPlayerDamagePlayer(EntityDamageByEntityEvent e)
	{
		if(!(e.getEntity() instanceof Player))
		{
			return;
		}
		
		Player entity = (Player)e.getEntity();
		
		Duel d = DuelManager.getDuel(entity);
		if(d == null)
			return;
		
		Player damager = null;
		boolean byHand = false;
		
		if(e.getDamager() instanceof Player)
		{
			damager = (Player)e.getDamager();
			byHand = true;
		
		}
		else if(e.getDamager() instanceof Arrow)
		{
			Arrow arrow = (Arrow)e.getDamager();
			
			if(arrow.getShooter() instanceof Player)
			{
				damager = (Player)arrow.getShooter();
			}
		}
		
		if(byHand)
		{
			ItemStack is = damager.getItemInHand();
			if(is != null && is.getType() != null)
			{
				if(is.getType().equals(Material.STONE_SWORD) && !hasArmor(entity))
				{
					e.setDamage(e.getDamage()/2.0);
				}
			}
		}
		
		lastDamager.put(entity.getUniqueId(),damager);
		
		if(d.isSpectator(damager,false))
		{
			e.setCancelled(true);
			return;
		}
		
		if(d.isFFA())
		{
			return;
		}
		
		if(damager != null && TournamentManager.isSpectator(damager, false))
		{
			e.setCancelled(true);
			return;
		}
		
		
		if(damager != null && getKit(damager).getSettings().contains(KitSettings.FRIENDLY_FIRE))
		{
			return;
		}
		
		if(damager != null && !damager.getUniqueId().equals(entity.getUniqueId()))
		{
			if(d.getRole(damager) == d.getRole(entity))
			{
				e.setCancelled(true);
			}
		}
		
	}
	
	
	@EventHandler
	public void onSplashPotion(PotionSplashEvent e)
	{
		if(!(e.getEntity().getShooter() instanceof Player))
		{
			return;
		}
		
		ThrownPotion tp = e.getEntity();
		Player thrower = (Player) e.getEntity().getShooter();
		
		Duel d = DuelManager.getDuel(thrower);
		
		if(d == null)
			return;
		if(d.isFFA())
		{
			return;
		}
		
		if(getKit(thrower).getSettings().contains(KitSettings.FRIENDLY_FIRE))
		{
			return;
		}
		
		for(Entity ent : e.getAffectedEntities())
		{
			if(ent instanceof Player)
			{
				boolean isBad = false;
				for(PotionEffect pe : tp.getEffects())
				{
					if(pe.getType().equals(PotionEffectType.HARM))
					{
						isBad = true;
						break;
					}
				}
				
				if(isBad)
				{
					Player damaged = (Player) ent;
					
					if(!damaged.getUniqueId().equals(thrower.getUniqueId()) && d.getRole(thrower) == d.getRole(damaged))
					{
						e.setIntensity(damaged, 0.0);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockFade(BlockFadeEvent e)
	{
		ArrayList<Map> maps = MapManager.getMaps();
		
		Duel d = DuelManager.getFirstDuel();
		
		
		
		for(int i = 0;i<maps.size();i++)
		{
			if(d == null || d.getMap() == null || maps.get(i) == null)
			{
				e.setCancelled(true);
			}
			else if(!maps.get(i).getName().equals(d.getMap().getName()))
			{
				e.setCancelled(true);
			}
			else if(!maps.get(i).isInside(e.getBlock().getLocation()))
			{
				e.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler
	public void onLeafDissapear(LeavesDecayEvent e)
	{
		ArrayList<Map> maps = MapManager.getMaps();
		
		Duel d = DuelManager.getFirstDuel();
		
		for(int i = 0;i<maps.size();i++)
		{
			if(d == null)
			{
				e.setCancelled(true);
			}
			else if(!maps.get(i).getName().equals(d.getMap().getName()))
			{
				e.setCancelled(true);
			}
			else if(!maps.get(i).isInside(e.getBlock().getLocation()))
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onExplosion(EntityExplodeEvent e)
	{
		Duel d = DuelManager.getFirstDuel();
		
		if(d == null)
			return;
		
		Map map = d.getMap();
		
		if(map == null)
			return;
		
		for(int i = 0;i<e.blockList().size();i++)
		{
			Block b = e.blockList().get(i);
			Location loc = b.getLocation();
			if(!map.isInside(loc))
			{
				e.blockList().remove(i);
				i--;
			}
		}
		
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e)
	{
		e.setQuitMessage("");
		
		Duel d = DuelManager.getDuel(e.getPlayer());
		
		if(TournamentManager.leftBySpectator.contains(e.getPlayer().getUniqueId()))
		{
			TournamentManager.leftBySpectator.remove(e.getPlayer().getUniqueId());
			onSpawnCalled.remove(e.getPlayer().getUniqueId());
			TournamentManager.removeSpectatorArray(e.getPlayer());
			return;
		}
		else if(d == null)
		{
			TournamentManager.removeSpectator(e.getPlayer());
			if(lastTournamentID != -1)
			{
				Tournament t = TournamentManager.getTournamentFromMySQL(e.getPlayer(), lastTournamentID, "pvp-1", null, 3, 15);
				if(t!=null)
				{
					if(t.isPartOf(e.getPlayer()) && t.getRound(e.getPlayer()) == null)
					{
						TournamentManager.playerLeaves(e.getPlayer(), t.getID());
					}
				}
			}
		}
		
		if(d != null)
		{
			d.removeSpectator(e.getPlayer());
			if(!d.hasEnded())
				d.handleDeath(e.getPlayer(),null,true);
			
			Settings.getSettings(e.getPlayer()).addToWSOnJoin(false);
			Settings.getSettings(e.getPlayer()).loadToMySQL();
			
			if(d.isTournament())
			{
				TournamentManager.playerLeaves(e.getPlayer(),d.getTournamentID());
			}
		}
		
		
		
		
		onSpawnCalled.remove(e.getPlayer().getUniqueId());
		for(int i = 0;i<specOnRespawn.size();i++)
		{
			if(specOnRespawn.get(i).player.getUniqueId().equals(e.getPlayer().getUniqueId()))
			{
				specOnRespawn.remove(i);
				break;
			}
		}
		
		lastDamager.remove(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();
		
		Duel d = DuelManager.getDuel(p);
		
		if(d != null)
		{
			if(d.isTournament())
			{
				for(int i = 0;i<specOnRespawn.size();i++)
				{
					if(specOnRespawn.get(i).player.getUniqueId().equals(p.getUniqueId()))
					{
						TournamentManager.addSpectator(specOnRespawn.get(i),true,false);
						specOnRespawn.remove(i);
						return;
					}
				}
				
				Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new RespawnDuelRun(p), 10);
			}
			else
			{
				d.addSpectator(new DuelSpec(p,d.getHomeServer(p)),true);
			}
		}
		else
		{
			for(int i = 0;i<specOnRespawn.size();i++)
			{
				if(specOnRespawn.get(i).player.getUniqueId().equals(p.getUniqueId()))
				{
					TournamentManager.addSpectator(specOnRespawn.get(i),true,false);
					specOnRespawn.remove(i);
					return;
				}
			}
		}
		
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			Player p = (Player)e.getEntity();
			
			Duel d = DuelManager.getDuel(p);
			if(d!= null)
			{
				if(!d.isStarted() || d.hasEnded())
				{
					if(e.getCause().equals(DamageCause.FIRE_TICK))
					{
						p.setFireTicks(0);
					}
					e.setCancelled(true);
				}
				else
				{
					Kit kit = getKit(p);
					
					if(kit == null)
					{
						return;
					}
					
					if(e.getCause().equals(DamageCause.FALL) && kit.getSettings().contains(KitSettings.NO_FALL_DAMAGE))
					{
						e.setCancelled(true);
					}
				}
				
				return;
			}
			
			if(TournamentManager.isSpectator(p, false))
			{
				if(e.getCause().equals(DamageCause.VOID))
				{
					Duel d1 = DuelManager.getFirstDuel();
					if(d1 != null)
					{
						p.teleport(d1.getMap().getMid());
					}
				}
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockDestroy(BlockBreakEvent e)
	{	
		Block b = e.getBlock();
		Player p = e.getPlayer();
		Duel d = DuelManager.getDuel(p);
		if(d==null)
			d = DuelManager.getFirstDuel();
		
		if(d != null)
		{
			if(d.isSpectator(p,false))
			{
				e.setCancelled(true);
				return;
			}
			
			
			Map map = d.getMap();
			
			if(!map.isInside(b.getLocation()) || !d.isStarted() ||
			   !getKit(p).getSettings().contains(KitSettings.PLACE_BREAK_BLOCKS))
			{
				e.setCancelled(true);
			}
		}
	}
	
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		Block b = e.getBlock();
		Player p = e.getPlayer();
		Duel d = DuelManager.getDuel(p);
		if(d==null)
			d = DuelManager.getFirstDuel();
		
		if(d != null)
		{
			if(d.isSpectator(p,false))
			{
				e.setCancelled(true);
				return;
			}
			
			Map map = d.getMap();
			
			if(!map.isInside(b.getLocation()) || !d.isStarted() ||
			   !getKit(p).getSettings().contains(KitSettings.PLACE_BREAK_BLOCKS))
			{
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e)
	{
		Player p = e.getPlayer();
		
		if(TournamentManager.isSpectator(p, false))
		{
			e.setCancelled(true);
			return;
		}
		
		Duel d = DuelManager.getDuel(p);
		if(d==null)
			d = DuelManager.getFirstDuel();
		
		if(d!= null)
		{
			if(d.isSpectator(p,false))
			{
				e.getItemDrop().remove();
				return;
			}
			if(!d.isStarted())
			{
				e.setCancelled(true);
				return;
			}
			
			if(e.getItemDrop().getItemStack().getType().equals(Material.MUSHROOM_SOUP) && getKit(p).getSettings().contains(KitSettings.NO_SOUP_DROP))
			{
				e.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onConsume(PlayerItemConsumeEvent e)
	{
		Player p = e.getPlayer();
		
		Duel d = DuelManager.getDuel(p);
		if(d==null)
			d=DuelManager.getFirstDuel();
		
		if(d!= null)
		{	
			if(d.isSpectator(p,false) || !d.isStarted())
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		
		if(TournamentManager.isSpectator(p,false))
		{
			ItemStack is = p.getItemInHand();
			
			if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			{
				if(is != null && is.getItemMeta() != null && is.getItemMeta().getDisplayName() != null)
				{
					if(is.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Runden"))
					{
						MenuManager.getDuelsMenu(p).open();
					}
					else if(is.getType().equals(Material.LEATHER_HELMET))
					{
						String name = ChatColor.stripColor(is.getItemMeta().getDisplayName());
						for(Player p1 : Bukkit.getOnlinePlayers())
						{
							if(p1.getDisplayName().equals(name))
							{
								p.openInventory(p1.getInventory());
							}
						}
					}
				}
				
			}
			
			
			
			e.setCancelled(true);
			return;
		}
		
		Duel d = DuelManager.getDuel(p);
		if(d==null)
			d=DuelManager.getFirstDuel();
		
		if(d!= null)
		{
			if(d.isSpectator(p,false))
			{
				ItemStack is = p.getItemInHand();
				if(is != null && is.getItemMeta() != null && is.getItemMeta().getDisplayName() != null && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
				{
					if(is.getType().equals(Material.LEATHER_HELMET))
					{
						String name = ChatColor.stripColor(is.getItemMeta().getDisplayName());
						for(Player p1 : Bukkit.getOnlinePlayers())
						{
							if(p1.getDisplayName().equals(name))
							{
								p.openInventory(p1.getInventory());
							}
						}
					}
				}
				e.setCancelled(true);
			}
			
			if(!d.isStarted() || d.hasEnded())
			{
				e.setCancelled(true);
				p.updateInventory();
			}
			else
			{
				Kit kit = getKit(p);
				
				if(kit != null)
				{
					if((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) &&
						kit.getSettings().contains(KitSettings.SOUP) && p.getItemInHand().getType().equals(Material.MUSHROOM_SOUP))
					{
						soupHeal(p);
						e.setCancelled(true);
					}
				}
				
			}
			
			
			
		}
	}
	
	@EventHandler
	public void onInvetoryClick(InventoryClickEvent e)
	{
		if(!(e.getWhoClicked() instanceof Player))
		{
			return;
		}
		
		Player p = (Player) e.getWhoClicked();
		
		Inventory inv = e.getClickedInventory();
		
		if(inv == null)
		{
			return;
		}
		
		Duel d = DuelManager.getDuel(p);
		if(d==null)
			d=DuelManager.getFirstDuel();
		
		if(d != null)
		{
			if(d.isSpectator(p,false))
			{
				ItemStack is = e.getCurrentItem();
				if(is != null && is.getType() != null && is.getItemMeta() != null && is.getItemMeta().getDisplayName() != null)
				{
					if(is.getType().equals(Material.LEATHER_HELMET))
					{
						String name = ChatColor.stripColor(is.getItemMeta().getDisplayName());
						for(Player player : Bukkit.getOnlinePlayers())
						{
							if(player.getDisplayName().equals(name))
							{
								p.openInventory(player.getInventory());
							}
						}
							
					}
				}
				
				e.setCancelled(true);
			}
			
		}
		
		if(TournamentManager.isSpectator(p, false))
		{
			if(inv.getName().equals("Runden"))
			{
				MenuManager.getDuelsMenu(p).onClick(e.getSlot(), e.getClick());
			}
			else if(inv.getName().equals("Duelle"))
			{
				MenuManager.getDuelsMenu(p).getTeleMenu().onClick(e.getSlot(), e.getClick());
			}
			else
			{
				ItemStack is = e.getCurrentItem();
				if(is != null && is.getType() != null && is.getItemMeta() != null && is.getItemMeta().getDisplayName() != null)
				{
					if(is.getType().equals(Material.LEATHER_HELMET))
					{
						String name = ChatColor.stripColor(is.getItemMeta().getDisplayName());
						for(Player player : Bukkit.getOnlinePlayers())
						{
							if(player.getDisplayName().equals(name))
							{
								p.openInventory(player.getInventory());
							}
						}
							
					}
					else if(is.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Runden"))
					{
						MenuManager.getDuelsMenu(p).open();
					}
				}
			}
			
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void onPlayerFoodLose(FoodLevelChangeEvent e)
	{
		
		if(!(e.getEntity() instanceof Player))
			return;
		
		Duel d = DuelManager.getDuel((Player)e.getEntity());
			if(d != null && d.isStarted())
			{
				Kit kit = getKit((Player)e.getEntity());
				
				if(kit == null)
				{
					return;
				}
				
				if(!kit.getSettings().contains(KitSettings.HUNGER))
				{
					e.setCancelled(true);
				}
			}
	}
	
	private void soupHeal(Player p)
	{
		final int HEAL = 7;
		
		if(p.getHealth() == p.getMaxHealth())
		{
			return;
		}
		
		if(p.getHealth()+HEAL > p.getMaxHealth())
		{
			p.setHealth(p.getMaxHealth());
		}
		else
		{
			p.setHealth(p.getHealth() + HEAL);
		}
		
		p.getItemInHand().setType(Material.BOWL);
	}
	
	@EventHandler
	public void onPlayerRegenerate(EntityRegainHealthEvent e)
	{
		if(!(e.getEntity() instanceof Player))
			return;
		
		Player p = (Player)e.getEntity();
		
		Kit kit = getKit(p);
		
		if(kit == null)
		{
			return;
		}
		
		
		if(!kit.getSettings().contains(KitSettings.REGENARATION) && (e.getRegainReason().equals(RegainReason.REGEN) || e.getRegainReason().equals(RegainReason.SATIATED)))
		{
			e.setCancelled(true);
			return;
		}
		
		Duel d = DuelManager.getDuel(p);
		
		if(d!=null)
		{
			if(d.hasEnded())
			{
				e.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent e)
	{
		Duel d = DuelManager.getFirstDuel();
		
		if(d == null)
			return;
		
		if(d.getKit().getSettings().contains(KitSettings.NO_EXPLOSION_DESTRUCTION))
		{
			e.blockList().clear();
		}
	}
	
	@EventHandler
	public void instantTNTEvent(BlockPlaceEvent e)
	{
		
		Player p = e.getPlayer();
		
		Duel d = DuelManager.getDuel(p);
		
		if(d == null)
			return;
		
		if(d.isSpectator(p,false) || !d.getMap().isInside(e.getBlock().getLocation()))
		{
			e.setCancelled(true);
			return;
		}
			
		Kit kit = getKit(p);
		
		if(kit == null)
			return;
		
		if(kit.getSettings().contains(KitSettings.INSTANT_TNT) && e.getBlock().getType().equals(Material.TNT))
		{
			e.getBlock().setType(Material.AIR);
			
			TNTPrimed tnt = p.getWorld().spawn(e.getBlock().getLocation(), TNTPrimed.class);
			tnt.setFuseTicks(30);
		}
	}
	
	@EventHandler
	public void onKnockback(EntityDamageByEntityEvent e)
	{
		if(!(e.getEntity() instanceof Player))
			return;
		
		Player p = (Player)e.getEntity();
		Player p1 = null;
		
		if(e.getDamager() instanceof Player)
		{
			p1 = (Player)e.getDamager();
		}
		else if(e.getDamager() instanceof Arrow)
		{
			Arrow arrow = (Arrow) e.getDamager();
			if(arrow.getShooter() instanceof Player)
			{
				p1 = (Player) arrow.getShooter();
			}
			else
			{
				return;
			}
		}
		else
		{
			return;
		}
		
		if(TournamentManager.isSpectator(p1, false))
		{
			e.setCancelled(true);
			return;
		}
		
		Duel d = DuelManager.getDuel(p);
		
		if(d == null)
			return;
		
		if(d.isSpectator(p1,false))
		{
			e.setCancelled(true);
			return;
		}
		
		if(d.isStarted())
		{	
			Kit kit = getKit(p);
			
			if(!d.isFFA() && !p.getUniqueId().equals(p1.getUniqueId()) && d.getRole(p) == d.getRole(p1) && !kit.getSettings().contains(KitSettings.FRIENDLY_FIRE))
			{
				e.setCancelled(true);
				return;
			}
			
			
			if(kit == null)
				return;
			
			if(kit.getSettings().contains(KitSettings.NO_KNOCKBACK))
			{
				p.damage(e.getFinalDamage());
				
				if(e.getDamager() instanceof Arrow)
				{
					p1.playSound(p1.getLocation(), Sound.SUCCESSFUL_HIT, 10.0f,Sounds.DEFAULT_PITCH);
					e.getDamager().remove();
				}
				
				e.setCancelled(true);
			}
		}
		
		
	}
	
	
	public static Kit getKit(Player p)
	{
		Duel d = DuelManager.getDuel(p);
		
		if(d == null)
		{
			return null;
		}
		
		if(d.isSpectator(p,false))
		{
			return null;
		}
		
		if(d.getKit().isDifKit())
		{
			if(Kit.getDifKitNumber(p) == -1)
				return Kit.getDifKit(p);
			
			int difKit = Kit.getDifKitNumber(p);
			
			switch(difKit)
			{
			case 1: return Kit.getDifKit(p,1);
			case 2: return Kit.getDifKit(p,2);
			case 3: return Kit.getDifKit(p,3);
			default: return null;
			}
		}
		else
		{
			return d.getKit();
		}
	}
	
	@EventHandler
	public void onDropConsume(PlayerPickupItemEvent e)
	{
		Player p = e.getPlayer();
		
		if(TournamentManager.isSpectator(p, false))
		{
			e.setCancelled(true);
		}
		
		Duel d = DuelManager.getDuel(p);
		
		if(d==null)
			d=DuelManager.getFirstDuel();
		
		if(d != null)
		{
			if(d.isSpectator(p,false))
			{
				e.setCancelled(true);
			}
			else
			{
				Kit kit = getKit(p);
				if(e.getItem().getItemStack().getType().equals(Material.ARROW) && kit.getSettings().contains(KitSettings.NO_ARROW_COLLECT))
				{
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onCraft(PrepareItemCraftEvent e)
	{
		boolean canNotCraft = false;
		
		for(HumanEntity he : e.getViewers())
		{
			if(!(he instanceof Player))
			{
				continue;
			}
			
			Player p = (Player)he;
			
			Duel d = DuelManager.getDuel(p);
			
			if(d == null)
			{
				continue;
			}
			
			Kit kit = getKit(p);
			
			if(kit == null || kit.getSettings().contains(KitSettings.NO_CRAFTING))
			{
				canNotCraft = true;
				break;
			}
		}
		
		if(canNotCraft)
		{
			e.getInventory().setResult(new ItemStack(Material.AIR));
		}
	}
	
	@EventHandler
	public void onCraftResultCLick(InventoryClickEvent e)
	{
		if(!(e.getWhoClicked() instanceof Player))
		{
			return;
		}
		
		if(!e.getSlotType().equals(SlotType.RESULT))
		{
			return;
		}
		
		Player p = (Player)e.getWhoClicked();
		
		Duel d = DuelManager.getDuel(p);
		
		if(d == null)
		{
			return;
		}
		
		Kit kit = getKit(p);
		
		if(kit == null || kit.getSettings().contains(KitSettings.NO_CRAFTING))
		{
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void onDoubleJump(PlayerToggleFlightEvent e)
	{	
		Player p = e.getPlayer();
		
		if(p.getGameMode().equals(GameMode.CREATIVE))
		{
			return;
		}
		
		Duel d = DuelManager.getDuel(p);
		
		if(d == null)
		{
			return;
		}
		
		
		Kit kit = getKit(p);
		
		if(kit != null && canDoubleJump.contains(p.getUniqueId()) && kit.getSettings().contains(KitSettings.DOUBLE_JUMP))
		{	
			e.setCancelled(true);
			Vector vec = p.getVelocity().clone();
			
			vec.add(new Vector(0.0,0.8,0.0));
			
			p.setVelocity(vec);
			
			canDoubleJump.remove(p.getUniqueId());
		}
		else if(!d.isSpectator(p,false) && !TournamentManager.isSpectator(p, false))
		{
			e.setCancelled(true);
		}
	}
	
	public static String getPrefix(Player p)
	{
		if(p.hasPermission("pf.inhaber"))
		{
			return ChatColor.LIGHT_PURPLE + "";
		}
		else if(p.hasPermission("pf.admin"))
		{
			return ChatColor.DARK_RED + "";
		}
		else if(p.hasPermission("pf.dev"))
		{
			return ChatColor.AQUA + "";
		}
		else if(p.hasPermission("pf.mod"))
		{
			return ChatColor.RED + "";
		}
		else if(p.hasPermission("pf.team"))
		{
			return ChatColor.getByChar('a') + "";
		}
		else if(p.hasPermission("pf.yt"))
		{
			return ChatColor.DARK_PURPLE + "";
		}
		else if(p.hasPermission("pf.legende"))
		{
			return ChatColor.DARK_AQUA + "";
		}
		else if(p.hasPermission("pf.premium"))
		{
			return ChatColor.GOLD + "";
		}
		else if(p.hasPermission("pf.freund"))
		{
			return ChatColor.BLUE + "";
		}
		else
		{
			return ChatColor.GRAY + "";
		}
	}
	
	private void sendChatToServer(Player p,String prefix,String server,String msg)
	{
		if(p == null)
		{
			if(Bukkit.getServer().getOnlinePlayers().isEmpty())
			{
				return;
			}
			p=(Player) Bukkit.getServer().getOnlinePlayers().toArray()[0];
			if(p == null)
			{
				return;
			}
		}
		
		ByteArrayDataOutput bo = ByteStreams.newDataOutput();
		bo.writeUTF("Forward");
		
		bo.writeUTF(server);
		bo.writeUTF("Chat");
		
		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try
		{
			msgout.writeUTF(prefix);
			msgout.writeUTF(p.getDisplayName()); // You can do anything you want with msgout
			msgout.writeUTF(msg);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println("Chat Error");
			return;
		}
		
		bo.writeShort(msgbytes.toByteArray().length);
		bo.write(msgbytes.toByteArray());
		
		p.sendPluginMessage(MainClass.getInstance(), "BungeeCord", bo.toByteArray());
	}
	
	@EventHandler
	(priority = EventPriority.LOWEST)
	public void onChat(PlayerChatEvent e)
	{
		Duel d = DuelManager.getDuel(e.getPlayer());
		
		if(d!= null)
		{
			addLobbyServerChat(d.getHomeServer(e.getPlayer()));
			for(int i = 0;i<lobbyServerToChatTo.size();i++)
				sendChatToServer(e.getPlayer(),getPrefix(e.getPlayer()),lobbyServerToChatTo.get(i),e.getMessage());
		}
			
	}
	
}
