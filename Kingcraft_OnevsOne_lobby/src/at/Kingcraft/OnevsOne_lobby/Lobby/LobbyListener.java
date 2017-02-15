package at.Kingcraft.OnevsOne_lobby.Lobby;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSkeleton;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftZombie;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Arenas.ArenaManager;
import at.Kingcraft.OnevsOne_lobby.Commands.ForceQueueCommand;
import at.Kingcraft.OnevsOne_lobby.Commands.RefuseCommand;
import at.Kingcraft.OnevsOne_lobby.Duels.ChallangeManager;
import at.Kingcraft.OnevsOne_lobby.Duels.Team;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamEnquiryManager;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamManager;
import at.Kingcraft.OnevsOne_lobby.Items.Items;
import at.Kingcraft.OnevsOne_lobby.Kits.Kit;
import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messenger;
import at.Kingcraft.OnevsOne_lobby.Scoreboard.MyScoreboardManager;
import at.Kingcraft.OnevsOne_lobby.Special.MenuManager;
import at.Kingcraft.OnevsOne_lobby.Special.SettingMenu;
import at.Kingcraft.OnevsOne_lobby.Stats.Statistics;
import at.Kingcraft.OnevsOne_lobby.Stats.StatisticsManager;
import at.Kingcraft.OnevsOne_lobby.Tournaments.Tournament;
import at.Kingcraft.OnevsOne_lobby.Tournaments.TournamentManager;
import at.Kingcraft.OnevsOne_lobby.WaitingSnake.RankedQueue;
import at.Kingcraft.OnevsOne_lobby.WaitingSnake.Settings;
import de.xAdler.Title;
import de.xAdler.TitleAPI.Colors;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("deprecation")
public class LobbyListener implements Listener {
	
	private static MainClass plugin;
	private Location spawn;
	private boolean firstSpawn = false;
	public static ArrayList<UUID> leftByDuel;
	public static ArrayList<UUID> leftByWS;
	public static ArrayList<UUID> leftByTour;
	private static Location minKitPlace;
	private static Location maxKitPlace;
	private static Location minNormalPlace;
	private static Location maxNormalPlace;
	private ArrayList<Player> onKitTriggerPlayers;
	private ArrayList<Player> onNormalTriggerPlayers;
	public static HashMap<UUID,Integer> tournamentRoundSkipper;
	public static ArrayList<UUID> ovoCmdBlock;
	public static ArrayList<UUID> kitViewer;
	
	private boolean checkLobby(Player p)
	{
		return true;
	}
	
	public LobbyListener(MainClass mainClass) {
		plugin = mainClass;
		spawn = new Location(Bukkit.getWorld(plugin.getConfig().getString("World.Name")),
				plugin.getConfig().getDouble("World.spawn.x"),
				plugin.getConfig().getDouble("World.spawn.y"),
				plugin.getConfig().getDouble("World.spawn.z"),
				(float)plugin.getConfig().getDouble("World.spawn.yaw"),
				(float)plugin.getConfig().getDouble("World.spawn.pitch"));
		
		leftByDuel = new ArrayList<UUID>();
		leftByWS = new ArrayList<UUID>();
		leftByTour = new ArrayList<UUID>();
		onKitTriggerPlayers = new ArrayList<>();
		onNormalTriggerPlayers = new ArrayList<>();
		tournamentRoundSkipper = new HashMap<>();
		ovoCmdBlock = new ArrayList<>();
		kitViewer = new ArrayList<>();
		
		setKitPlace();
		setNormalPlace();
	}
	
	private boolean isInKitPlace(Player p)
	{
		Location loc = p.getLocation();
		
		if(!loc.getWorld().equals(minKitPlace.getWorld()))
			return false;
		
		// AABB
				if(loc.getX() >= minKitPlace.getX() && loc.getX() <= maxKitPlace.getX() &&
				   loc.getY() >= minKitPlace.getY() && loc.getY() <= maxKitPlace.getY() &&
				   loc.getZ() >= minKitPlace.getZ() && loc.getZ() <= maxKitPlace.getZ() )
				{
					return true;
				}
				
				
				return false;
	}
	
	private boolean isInNormalPlace(Player p)
	{
		Location loc = p.getLocation();
		
		if(!loc.getWorld().equals(minNormalPlace.getWorld()))
			return false;
		
		// AABB
				if(loc.getX() >= minNormalPlace.getX() && loc.getX() <= maxNormalPlace.getX() &&
				   loc.getY() >= minNormalPlace.getY() && loc.getY() <= maxNormalPlace.getY() &&
				   loc.getZ() >= minNormalPlace.getZ() && loc.getZ() <= maxNormalPlace.getZ() )
				{
					return true;
				}
				
				
				return false;
	}
	
	public static void setKitPlace()
	{
		double x1,y1,z1,x2,y2,z2;
		
		x1 = plugin.getConfig().getDouble("Kits.Place.x1");
		y1 = plugin.getConfig().getDouble("Kits.Place.y1");
		z1 = plugin.getConfig().getDouble("Kits.Place.z1");
		x2 = plugin.getConfig().getDouble("Kits.Place.x2");
		y2 = plugin.getConfig().getDouble("Kits.Place.y2");
		z2 = plugin.getConfig().getDouble("Kits.Place.z2");
		
		minKitPlace = new Location(Bukkit.getWorld(plugin.getConfig().getString("Kits.Place.World")),
								   Math.min(x1, x2),
							       Math.min(y1, y2),
							       Math.min(z1, z2));
		
		maxKitPlace = new Location(Bukkit.getWorld(plugin.getConfig().getString("Kits.Place.World")),
								   Math.max(x1, x2),
							       Math.max(y1, y2),
							       Math.max(z1, z2));
	}
	
	public static void setNormalPlace()
	{
		double x1,y1,z1,x2,y2,z2;
		
		x1 = plugin.getConfig().getDouble("Kits.Place.Normal.x1");
		y1 = plugin.getConfig().getDouble("Kits.Place.Normal.y1");
		z1 = plugin.getConfig().getDouble("Kits.Place.Normal.z1");
		x2 = plugin.getConfig().getDouble("Kits.Place.Normal.x2");
		y2 = plugin.getConfig().getDouble("Kits.Place.Normal.y2");
		z2 = plugin.getConfig().getDouble("Kits.Place.Normal.z2");
		
		minNormalPlace = new Location(Bukkit.getWorld(plugin.getConfig().getString("Kits.Place.Normal.World")),
								   Math.min(x1, x2),
							       Math.min(y1, y2),
							       Math.min(z1, z2));
		
		maxNormalPlace = new Location(Bukkit.getWorld(plugin.getConfig().getString("Kits.Place.Normal.World")),
								   Math.max(x1, x2),
							       Math.max(y1, y2),
							       Math.max(z1, z2));
	}
	
 	public static void setupSpawnItems(Player p,boolean first,boolean tournament)
	{
		
 		p.setGameMode(GameMode.ADVENTURE);
 		
		p.getInventory().clear();
		
		if(!tournament)
		{
			// Sword
			// For challenging other players 
			Material challengeItem = Material.getMaterial(Items.challengeItemMaterial);
			
			if(challengeItem == null)
			{
				System.out.println("[" + plugin.getName() + "] Fehler bei ChallengeItem! (Vielleicht falsches Material!)" );
				challengeItem = Material.DIAMOND_SWORD;
				return;
			}
			
			ItemStack item = new ItemStack(challengeItem);
			ItemMeta meta = item.getItemMeta();
			
			meta.setDisplayName(Items.challengeItemName);
			
			meta.setLore(Items.challengeItemLore);
			item.setItemMeta(meta);
			
			p.getInventory().setItem(0, item);
			
			//Head
			// For showing challenges
			ChallangeManager.setupSkull(p, p,first);
			
			//FFA-Apple
			Team t = TeamManager.getTeam(p);
			if(t != null && t.getLeader().getUniqueId().equals(p.getUniqueId()))
			{
				Material mat = Material.getMaterial(Items.ffaItemMaterial);
				if(mat == null)
				{
					mat = Material.APPLE;
				}
				ItemStack ffaapple = new ItemStack(mat);
				ItemMeta im = ffaapple.getItemMeta();
				im.setDisplayName(Items.ffaItemName);
				im.setLore(Items.ffaItemLore);
				ffaapple.setItemMeta(im);
				
				p.getInventory().setItem(4, ffaapple);
			}
			else
			{
				ItemStack apple = new ItemStack(Material.GOLDEN_APPLE);
				ItemMeta im = apple.getItemMeta();
				im.setDisplayName(ChatColor.GOLD + "Ranked");
				ArrayList<String> lore = new ArrayList<>();
				lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Comming Soon..");
				im.setLore(lore);
				apple.setItemMeta(im);
				
				p.getInventory().setItem(4, apple);
			}
			
			// Kompass für Spectaten
			{
				ItemStack is = new ItemStack(Material.getMaterial(Items.spectateMaterial),1,Items.spectateDurability);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(Items.spectateName);
				im.setLore(Items.spectateLore);
				is.setItemMeta(im);
				
				p.getInventory().setItem(MainClass.getInstance().getConfig().getInt("Items.Spectate.HotbarPosition"), is);
			}
		}
		else
		{
			// Ghast Tear
			{
				ItemStack is = new ItemStack(Material.GHAST_TEAR);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(ChatColor.YELLOW + "Turnier-Informationen");
				ArrayList<String> lore = new ArrayList<>();
				lore.add(ChatColor.YELLOW + "RECHTS" + ChatColor.WHITE + "-Klick für Turnierinformationen");
				im.setLore(lore);
				is.setItemMeta(im);
				p.getInventory().setItem(MainClass.getInstance().getConfig().getInt("Items.Spectate.HotbarPosition"), is);
			}
			
			// Leave Item
			{
				ItemStack is = new ItemStack(Material.INK_SACK,1,(short)1);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(ChatColor.RED + "Turnier verlassen");
				ArrayList<String> lore = new ArrayList<>();
				lore.add(ChatColor.YELLOW + "RECHTS" + ChatColor.WHITE + "-Klick um das Turnier zu verlassen");
				im.setLore(lore);
				is.setItemMeta(im);
				p.getInventory().setItem(1, is);
			}
			
			// Start Item
			{
				Bukkit.getScheduler().runTaskLater(MainClass.getInstance(), new Runnable() {
					
					@Override
					public void run()
					{
						Tournament t = TournamentManager.getTournament(p);
						
						if(t != null && t.getLeader().getUniqueId().equals(p.getUniqueId()))
						{
							ItemStack is = new ItemStack(Material.INK_SACK,1,(short)10);
							ItemMeta im = is.getItemMeta();
							im.setDisplayName(ChatColor.GREEN + "Turnier starten");
							ArrayList<String> lore = new ArrayList<>();
							lore.add(ChatColor.YELLOW + "RECHTS" + ChatColor.WHITE + "-Klick um das Turnier zu starten");
							im.setLore(lore);
							is.setItemMeta(im);
							p.getInventory().setItem(0, is);
						}
						
					}
				}, 1);
			}
		}
		
		
		//Equipment
		p.getEquipment().setHelmet(new ItemStack(Material.AIR));
		p.getEquipment().setChestplate(new ItemStack(Material.AIR));
		p.getEquipment().setLeggings(new ItemStack(Material.AIR));
		p.getEquipment().setBoots(new ItemStack(Material.AIR));
		
		
		//SettingItem
		MenuManager.giveSettingItem(p);
		MenuManager.addSettingMenu(p);
		

		MyScoreboardManager.updateScoreboard(p);
		
	}
 	
 	private boolean isTournamentWinner(Player p)
 	{
 		try
 		{
 			boolean rv;
 			
			PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM Duel_TournamentWinner WHERE UUID = ?");
			ps.setString(1, p.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			
			if(rs.first())
			{
				rv = true;
			}
			else
			{
				rv = false;
			}
			
			ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("DELETE FROM Duel_TournamentWinner WHERE UUID = ?");
			ps.setString(1, p.getUniqueId().toString());
			ps.executeUpdate();
			
			return rv;
		}
 		catch (SQLException e) 
 		{
			e.printStackTrace();
		}
 		
 		return false;
 	}
 	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		if(!firstSpawn)
		{
			firstSpawn = true;
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
				
				@Override
				public void run()
				{
					// Add Server to ArenaManager
					boolean getServersWorked = Messenger.sendMessage(null, "BungeeCord", "GetServers", new String[0]);
					if(!getServersWorked)
					{
						firstSpawn = false;
					}
					
					boolean getServerName = Messenger.sendMessage(null, "BungeeCord", "GetServer", new String[0]);
					if(!getServerName)
					{
						firstSpawn = false;
					}
					Bukkit.getScheduler().runTask(plugin, new Runnable() {
						
						@Override
						public void run()
						{
							plugin.getKitSkeleton().reload();
							plugin.getWaitingEntity().reload();
						}
					});
					
					
				}
			}, 20*2);
		}
		Player p = e.getPlayer();
		
		KitManager.setChoosenKit(p,null,0,Integer.MAX_VALUE);
		KitManager.setChoosenDifKit(p, 1);
		KitManager.setDif(p, false);
		KitManager.addToNormal(p);
		KitManager.loadKits(p);
		
		Settings.loadSettings(p);
		
		e.setJoinMessage(ChatColor.GREEN + p.getDisplayName() + ChatColor.YELLOW + " joined the game");
		
		setupSpawnItems(p,true,false); // Adds Challenge Sword ++
				
		// setup Position
		p.teleport(spawn);
		
		// Setup FoodLevel
		p.setFoodLevel(20);
		
		// Setup XP
		p.setLevel(0);
		p.setExp(0);
		
		TeamManager.checkTeam(p,TeamManager.getTeam(p));
		
		
		// TODO startServer hinzufügen
		
		if(plugin.getServer().getOnlinePlayers().size() == 1 || plugin.getServer().getOnlinePlayers().size() == 0)
		{
			plugin.reloadEntities();
		}
		
		if(Settings.getSettings(p).addToWSOnJoin())
		{
			System.out.println("Add " + p.getDisplayName() + " to WaitingSnake");
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				
				@Override
				public void run() {
					if(Settings.getSettings(p).addToWSOnJoin())
						plugin.getWaitingSnake().addPlayer(p,true);
				}
			}, 20*5);
			
		}
		
		if(leftByTour.contains(p.getUniqueId()))
		{
			leftByTour.remove(p.getUniqueId());
		}
		
		if(isTournamentWinner(p))
		{
			Title title = new Title();
			title.addText("Gratulation",Colors.GOLD);
			title.setTiming(10, 80, 30);
			
			Title subTitle = new Title();
			subTitle.addText("Du hast das Turnier gewonnen",Colors.YELLOW);
			
			title.setSubTitle(subTitle);
			
			title.send(p);
			//p.playSound(p.getLocation(), Sounds.tournamentWin, Sounds.tournamentWinVolume, Sounds.DEFAULT_PITCH);
			
				
					for(int i = 0;i<20;i++)
					{
						Bukkit.getScheduler().runTaskLater(MainClass.getInstance(), new Runnable() {
							
							@Override
							public void run() {
								Location loc = p.getLocation().clone();
								spawnFireWork(loc.add(loc.getDirection().multiply(3.0)));
							}
						}, 10*(i+1));
						
					}
			
           
		}
	}
	
	public static void spawnFireWork(Location loc)
	{
		Random rand = new Random();
		Firework fw = (Firework) loc.getWorld().spawn(loc.add(rand.nextDouble() * 3.0 - 1.5, rand.nextDouble() * 3.0 - 1.5, rand.nextDouble() * 3.0 - 1.5), Firework.class);
        FireworkMeta fwm = fw.getFireworkMeta();
       
        //Our random generator
        Random r = new Random();   

        //Get the type
        int rt = r.nextInt(5) + 1;
        Type type = Type.BALL;       
        if (rt == 1) type = Type.BALL;
        if (rt == 2) type = Type.BALL_LARGE;
        if (rt == 3) type = Type.BURST;
        if (rt == 4) type = Type.CREEPER;
        if (rt == 5) type = Type.STAR;
       
        //Get our random colours   
        int rc1 = r.nextInt(256);
        int gc1 = r.nextInt(256);
        int bc1 = r.nextInt(256);
        int rc2 = r.nextInt(256);
        int gc2 = r.nextInt(256);
        int bc2 = r.nextInt(256);
        Color c1 = Color.fromRGB(rc1,gc1,bc1);
        Color c2 = Color.fromRGB(rc2,gc2,bc2);
       
        //Create our effect with this
        FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(c1).withFade(c2).with(type).trail(true).build();
        
        fwm.clearEffects();
       
        //Then apply the effect to the meta
        fwm.addEffect(effect);
       
        //Generate some random power and set it
        int rp = r.nextInt(3) + 1;
        rp = 0;
        
        try {
			Field f = fwm.getClass().getDeclaredField("power");
			f.setAccessible(true);
			try {
				f.set(fwm,rp);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
       
        //Then apply this to our rocket
        fw.setFireworkMeta(fwm);
		
		/*Firework fw = (Firework) loc.getWorld().spawn(loc, Firework.class);
        FireworkEffect effect = FireworkEffect.builder().trail(true).flicker(false).withColor(Color.BLUE).with(Type.CREEPER).build();
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.clearEffects();
        fwm.addEffect(effect);
        Field f;
        try {
                f = fwm.getClass().getDeclaredField("power");
                f.setAccessible(true);
                f.set(fwm, -2);
        } catch (NoSuchFieldException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
       
       
        fw.setFireworkMeta(fwm);*/
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e)
	{		
		Player p = e.getPlayer();
		
		boolean leftBy1vs1 = leftByDuel.contains(p.getUniqueId());
		boolean leftByWS_b =  leftByWS.contains(p.getUniqueId()) || (plugin.getWaitingSnake().isIn(p) && !plugin.getWaitingSnake().isInMySQL(p));
		boolean leftByTour_b = leftByTour.contains(p.getUniqueId());
		boolean leftByFight = leftBy1vs1 || leftByWS_b || leftByTour_b;
		
		// Refuse every TeamEnquiry
		TeamEnquiryManager.deleteEnquiries(p,true);
		
		
		if(!leftByFight)
		{
			Team t = TeamManager.getTeam(p);
			if(t != null)
			{
				if(!t.removePlayer(p,true,false))
				{
					TeamManager.deleteTeam(t, false,true);
				}
			}
			
			
			plugin.getWaitingSnake().removePlayer(p,true,false);
			
			e.setQuitMessage(ChatColor.GREEN + p.getDisplayName() + ChatColor.YELLOW +  " left the game");
			
		}
		else
		{
			plugin.getWaitingSnake().removePlayer(p,false,false);
			
			e.setQuitMessage("");
		}
		
		if(leftBy1vs1)
		{
			leftByDuel.remove(p.getUniqueId());
		}
		
		if(leftByWS_b)
		{
			leftByWS.remove(p.getUniqueId());
		}
		else
		{
			if(Settings.getSettings(p).addToWSOnJoin())
			{
				Settings.getSettings(p).addToWSOnJoin(false);
			}
		}
		
		if(!leftByTour_b)
		{
			Tournament t = null;
			
			do
			{
				t = TournamentManager.getTournament(p);
				
				if(t != null)
				{
					Team team = TeamManager.getTeam(p);
					ArrayList<Player> players;
					if(team != null)
					{
						players = team.getPlayers();
					}
					else
					{
						players = new ArrayList<>();
						players.add(p);
					}
					t.removeContestants(players, true,true,true);
				}
			}while(t != null);
			
			if(tournamentRoundSkipper.get(p.getUniqueId()) != null)
			{
				int tourID = tournamentRoundSkipper.get(p.getUniqueId());
				
				TournamentManager.removeRoundSkipper(p,tourID);
				
				tournamentRoundSkipper.remove(p.getUniqueId());
			}
		}
		else
		{
			leftByTour.remove(p.getUniqueId());
		}
		
		ovoCmdBlock.remove(p.getUniqueId());
		
		
		// Jede Challenge Refusen
		
		RefuseCommand.refuseEverything(p);
		
		MenuManager.getSettingMenu(p).getMapMenu().writeMapsToMySQL();
		
		if(onKitTriggerPlayers.contains(p))
		{
			onKitTriggerPlayers.remove(p);
		}
		if(onNormalTriggerPlayers.contains(p))
		{
			onNormalTriggerPlayers.remove(p);
		}
		
		KitManager.removeFromKit(p);
		KitManager.removeFromNormal(p);
		
		ArrayList<Kit> kits = KitManager.getKits(p);
		
		if(kits != null)
		{
			for(int i = 0;i<kits.size();i++)
			{
				kits.get(i).loadToMySQL(plugin.getMySQL());
			}
		}
		
		KitManager.uploadDifKit(p);
		KitManager.uploadChoosenKit(p);
		KitManager.deleteKits(p);
		
		
		Settings.getSettings(p).loadToMySQL();
		Settings.removeSettings(p);
		
		MenuManager.getSettingMenu(p).getTourSettingMenu().loadToMySQL();
		MenuManager.deleteSettingMenu(p);
		MenuManager.deleteKitViewerMenu(p);
		MenuManager.deleteSpectateMenu(p);
		MenuManager.deleteTournamentViewMenu(p);
		MenuManager.deleteRankedMenu(p);
		MenuManager.deleteTopMenu(p);
		
		StatisticsManager.deleteStatistics(p);
		
		KitManager.removeDifKitSettingsMenu(p);
		kitViewer.remove(p.getUniqueId());
		
		if(p.hasPermission("command.forcequeue"))
		{
			ForceQueueCommand.remove(p.getUniqueId());
		}
		
		RankedQueue.removePlayer(p);
	}
	
	private static boolean isBanedItem(ItemStack is)
	{
		if(is.getType().equals(Material.SAPLING) || is.getType().equals(Material.BONE) || (is.getType().equals(Material.INK_SACK) && is.getDurability() == (short)15) ||
		   is.getType().toString().contains("MINECART"))
		{
			return true;
		}
		
		return false;
	}
	
	@EventHandler
	(priority = EventPriority.HIGHEST)
	public void onInventory(InventoryClickEvent e)
	{
		if(!checkLobby((Player)e.getWhoClicked()))
		{
			return;
		}
		if(e.getWhoClicked() instanceof Player)
		{
			
			if(e.getClickedInventory() == null)
			{
				return;
			}
			
			Player p = (Player)e.getWhoClicked();
			
			
			
			String settingText = Items.settingsItemName;
			
			Inventory inv = e.getClickedInventory();
			
			if(inv.getName().equalsIgnoreCase(plugin.getConfig().getString("Items.Enquiries.Text")))
			{
				ChallangeManager.getEnquiryMenu(p).onClick(e.getSlot(), e.getClick());
				// Cancel if its in the Enquire Menu
				e.setCancelled(true);
			}
			else if(inv.getName().equals("Einstellungen"))
			{
				MenuManager.getSettingMenu(p).onClick(e.getSlot(), e.getClick());
				e.setCancelled(true);
			}
			else if(inv.getName().equals("Maps"))
			{
				MenuManager.getSettingMenu(p).getMapMenu().onClick(e.getSlot(), e.getClick());
				e.setCancelled(true);
			}
			else if(inv.getName().equals("Team-Einstellungen"))
			{
				MenuManager.getSettingMenu(p).getTeamMenu().onClick(e.getSlot(), e.getClick());
				e.setCancelled(true);
			}
			else if(inv.getName().equals("Kit-Einstellungen"))
			{
				MenuManager.getSettingMenu(p).getKitMainMenu().onClick(e.getSlot(), e.getClick());
				e.setCancelled(true);
			}
			else if(inv.getName().equals("Eigene Kits"))
			{
				MenuManager.getSettingMenu(p).getKitMainMenu().getKitOwnMenu().onClick(e.getSlot(), e.getClick());
				e.setCancelled(true);
			}
			else if(inv.getName().equals("Kit1-Einstellungen"))
			{
				MenuManager.getSettingMenu(p).getKitMainMenu().getKitOwnMenu().getKitSettingMenu().onClick(e.getSlot(), e.getClick());
				e.setCancelled(true);
			}
			else if(inv.getName().equals("Verschiedene Kits"))
			{
				MenuManager.getSettingMenu(p).getKitMainMenu().getKitDifMenu().onClick(e.getSlot(), e.getClick());
				e.setCancelled(true);
			}
			else if(inv.getName().equals("Warteschlange-Einstellungen"))
			{
				MenuManager.getSettingMenu(p).getWSMenu().onClick(e.getSlot(), e.getClick());
				e.setCancelled(true);
			}
			else if(inv.getName().equals("Turnier-Einstellungen"))
			{
				MenuManager.getSettingMenu(p).getTourSettingMenu().onClick(e.getSlot(), e.getClick());
				e.setCancelled(true);
			}
			else if(inv.getName().equals("Verschiedene Kit Einstellungen"))
			{
				KitManager.getDifPreKitSettingsMenu(p).onClick(e.getSlot(),e.getClick());
				e.setCancelled(true);
			}
			else if(inv.getName().equals("Vorgegebene Kits"))
			{
				MenuManager.getSettingMenu(p).getKitMainMenu().getKitPreMenu().onClick(e.getSlot(), e.getClick());
				e.setCancelled(true);
			}
			else if(inv.getName().equals("Kits eines anderen Spielers"))
			{
				MenuManager.getKitViewerMenu(p).onClick(e.getSlot(), e.getClick());
				e.setCancelled(true);
			}
			else if(inv.getName().equals("Spectate-Menu"))
			{
				MenuManager.getSpectateMenu(p).onClick(e.getSlot(), e.getClick());
				e.setCancelled(true);
			}
			else if(inv.getName().equals("Turnier"))
			{
				MenuManager.getTournamentViewMenu(p, false).onClick(e.getSlot(), e.getClick());
				e.setCancelled(true);
			}
			else if(inv.getName().startsWith("Ranked"))
			{
				MenuManager.getRankedMenu(p).onClick(e.getSlot(), e.getClick());
				e.setCancelled(true);
			}
			else if(inv.getName().equals("Top-Spieler"))
			{
				MenuManager.getTopMenu(p).onClick(e.getSlot(), e.getClick());
				e.setCancelled(true);
			}
			else if(e.getCurrentItem().getType() != Material.AIR &&
					e.getCurrentItem().getItemMeta() != null &&
					e.getCurrentItem().getItemMeta().getDisplayName() != null &&
					(e.getCurrentItem().getItemMeta().getDisplayName().equals(Items.challengeItemName) ||
					 e.getCurrentItem().getItemMeta().getDisplayName().equals(Items.challengeSkullName) ||
					 e.getCurrentItem().getItemMeta().getDisplayName().equals(settingText) ||
					 e.getCurrentItem().getItemMeta().getDisplayName().equals(Items.ffaItemName) ||
					 e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Ranked") ||
					 e.getCurrentItem().getItemMeta().getDisplayName().equals(Items.spectateName) ||
					 e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Turnier-Informationen") ||
					 e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Turnier verlassen") ||
					 e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Turnier starten")))
			{
				e.setCancelled(true);
			}
			
			if(KitManager.isKitPlayer(p))
			{
				if(e.getCursor() != null && isBanedItem(e.getCursor()))
				{
					p.sendMessage(Messages.youCanNotUseThisItem);
					e.setCancelled(true);
				}
				
				return;
			}
			
		}
		
	}
	
	
	private boolean isEquipment(Material mat)
	{
		return mat.equals(Material.CHAINMAIL_CHESTPLATE) || mat.equals(Material.DIAMOND_CHESTPLATE) ||
			   mat.equals(Material.GOLD_CHESTPLATE) || mat.equals(Material.IRON_CHESTPLATE) ||
			   mat.equals(Material.LEATHER_CHESTPLATE) || mat.equals(Material.CHAINMAIL_HELMET) ||
			   mat.equals(Material.CHAINMAIL_LEGGINGS) || mat.equals(Material.CHAINMAIL_BOOTS) ||
			   mat.equals(Material.DIAMOND_HELMET) || mat.equals(Material.DIAMOND_LEGGINGS) ||
			   mat.equals(Material.DIAMOND_BOOTS) || mat.equals(Material.GOLD_HELMET) ||
			   mat.equals(Material.GOLD_LEGGINGS) || mat.equals(Material.GOLD_BOOTS) ||
			   mat.equals(Material.IRON_HELMET) || mat.equals(Material.IRON_LEGGINGS) ||
			   mat.equals(Material.IRON_BOOTS) || mat.equals(Material.LEATHER_HELMET) ||
			   mat.equals(Material.LEATHER_LEGGINGS) || mat.equals(Material.LEATHER_BOOTS);
	}
	
	@EventHandler
	(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent e)
	{
		if(!checkLobby(e.getPlayer()))
		{
			return;
		}
		
		Action a = e.getAction();
		
		if(KitManager.isKitPlayer(e.getPlayer()))
		{
			e.setCancelled(true);
			
			if(a == Action.RIGHT_CLICK_BLOCK)
			{
				Block b = e.getClickedBlock();
				
				Material mat = b.getType();
				
				if(!(mat.equals(Material.WORKBENCH) || mat.equals(Material.FURNACE) ||
					 mat.equals(Material.BURNING_FURNACE) || mat.equals(Material.ENCHANTMENT_TABLE) ||
					 mat.equals(Material.ANVIL)) || e.getPlayer().isSneaking())
				{
					e.setCancelled(true);
				}
				else
				{
					e.setCancelled(false);
				}
			}
			if((a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) && e.getPlayer().isSneaking() && isEquipment(e.getPlayer().getItemInHand().getType()))
			{
				e.setCancelled(false);
			}
			
			return;
		}
		
		if(kitViewer.contains(e.getPlayer().getUniqueId()))
		{
			e.setCancelled(true);
			return;
		}
		
		if(a == Action.RIGHT_CLICK_BLOCK || a == Action.RIGHT_CLICK_AIR)
		{
			ItemStack item = e.getPlayer().getItemInHand();
			
			if(item.getType() != null && item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null)
			{
				if(item.getItemMeta().getDisplayName().equals(Items.challengeSkullName))
				{
					e.setCancelled(true);
					ChallangeManager.getEnquiryMenu(e.getPlayer()).open();
					
					if(item.getAmount() == 0)
					{
						item.setAmount(1);
					}
				}
				else if(item.getItemMeta().getDisplayName().equals(Items.settingsItemName))
				{
					MenuManager.getSettingMenu(e.getPlayer()).open();
					e.setCancelled(true);
				}
				else if(item.getItemMeta().getDisplayName().equals(Items.ffaItemName))
				{
					e.getPlayer().performCommand("ffa");
					e.setCancelled(true);
				}
				else if(item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Ranked"))
				{
					MenuManager.getRankedMenu(e.getPlayer()).open();
					e.setCancelled(true);
				}
				else if(item.getItemMeta().getDisplayName().equals(Items.spectateName))
				{
					e.getPlayer().performCommand("spectate");
					e.setCancelled(true);
				}
				else if(item.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Turnier-Informationen"))
				{
					e.getPlayer().performCommand("turnier");
					e.setCancelled(true);
				}
				else if(item.getItemMeta().getDisplayName().equals(ChatColor.RED + "Turnier verlassen"))
				{
					e.getPlayer().performCommand("turnier leave");
					e.setCancelled(true);
				}
				else if(item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Turnier starten"))
				{
					e.getPlayer().performCommand("start 0");
					e.setCancelled(true);
				}
				
			}
			
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		Inventory inv = e.getInventory();
		
		if(inv != null && inv.getName().equals("Verschiedene Kit Einstellungen"))
		{
			Kit kit = KitManager.getDifPreKitSettingsMenu((Player) e.getPlayer()).getOpenedKit();
			
			if(kit.isDif())
				KitManager.uploadDifKit(kit.getNumber());
			else
				KitManager.uploadPreKit(kit.getNumber());
			
			ArrayList<SettingMenu> menus = MenuManager.getAllSettingsMenus();
			
			for(int i = 0;i<menus.size();i++)
			{
				menus.get(i).getKitMainMenu().getKitDifMenu().update();
				menus.get(i).getKitMainMenu().getKitPreMenu().update();
			}
			
		}
	}
	
	@EventHandler
	public void onBlockDestroy(BlockBreakEvent e)
	{
		if(!checkLobby(e.getPlayer()))
		{
			return;
		}
		e.setCancelled(!e.getPlayer().hasPermission("world.destroy")); // Cancel Break when hasn't permission
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		e.setCancelled(!e.getPlayer().hasPermission("world.place"));
	}
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e)
	{
		if(e.getEntity() == null || e.getEntity().getLocation() == null || plugin == null || plugin.getWaitingEntity() == null || plugin.getWaitingEntity().getBukkitEntity() == null || plugin.getWaitingEntity().getBukkitEntity().getLocation() == null)
		{
			return;
		}
		
		if(e.getEntity() instanceof Player)
		{
			return;
		}
		else if(e.getEntity().getLocation().equals(plugin.getWaitingEntity().getBukkitEntity().getLocation()))
		{
			return;
		}
		else if(e.getEntity().getLocation().equals(plugin.getKitSkeleton().getBukkitEntity().getLocation()))
		{
			return;
		}
		
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onSwordRightClickPlayer(PlayerInteractEntityEvent e)
	{
		if(e.getRightClicked() instanceof Player)
		{
			Player me = e.getPlayer();
			Player other = (Player)e.getRightClicked();
			
			ItemStack is = me.getItemInHand();
			if(is != null && is.getType() != null && is.getType().equals(Material.getMaterial(Items.challengeItemMaterial)) &&
			   is.getItemMeta() != null && is.getItemMeta().getDisplayName() != null && is.getItemMeta().getDisplayName().equals(Items.challengeItemName))
			{
				me.performCommand("team " + other.getDisplayName());
			}
		}
		else if(e.getRightClicked() instanceof CraftZombie)
		{
			MenuManager.getSettingMenu(e.getPlayer()).getWSMenu().open();
		}
		else if(e.getRightClicked() instanceof CraftSkeleton)
		{
			Kit kit = KitManager.getChoosenKitKit(e.getPlayer());
			if(kit.isDif() || kit.getOwnerName().equals("Server"))
			{
				kit = KitManager.getKits(e.getPlayer()).get(0);
			}
			
			MenuManager.getSettingMenu(e.getPlayer()).getKitMainMenu().getKitOwnMenu().getKitSettingMenu().open(kit);
		}
	}
	
	@EventHandler
	public void onInteractWithWaiting(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() instanceof Player)
		{
			Player me = (Player)e.getDamager();
			
			if(e.getEntity().getLocation().equals(plugin.getWaitingEntity().getBukkitEntity().getLocation()))
			{
				ItemStack hand = me.getItemInHand();
			
				if(hand != null && hand.getItemMeta() != null && hand.getItemMeta().getDisplayName() != null && hand.getItemMeta().getDisplayName().equals(Items.challengeItemName))
				{
					me.performCommand("warteschlange");
				}
				
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	(priority = EventPriority.HIGHEST)
	public void onInteractWithPlayer(EntityDamageByEntityEvent e)
	{
		
		Player me = null;
		if(e.getDamager() instanceof Player)
		{
			
			me = (Player)e.getDamager();
			
			if(!checkLobby(me))
			{
				return;
			}
			
			ItemStack hand = me.getItemInHand();
			if(hand == null)
			{
				e.setCancelled(true);
				return;
			}
			ItemMeta meta = hand.getItemMeta();
			if(meta == null)
			{
				e.setCancelled(true);
				return;
			}
			String name = meta.getDisplayName();
			
			if(name == null)
			{
				e.setCancelled(true);
				return;
			}
			
			if(name.equalsIgnoreCase(Items.challengeItemName))
			{
				if(e.getEntity() instanceof Player)
				{
					
					Player other = (Player)e.getEntity();
					
					me.performCommand("1vs1 " + other.getDisplayName());
					
					
				}
			}

			e.setCancelled(true);
			
		}
		
		
	}
	
	@EventHandler
	public void onFoodLose(FoodLevelChangeEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			e.setCancelled(true);
		}
		
	}
	
	 @EventHandler
	 public void onDrop(PlayerDropItemEvent e)
	 {
		 Player p = e.getPlayer();
		 if(!checkLobby(p))
		 {
			return;
		 }
		 ItemStack item = e.getItemDrop().getItemStack();
		 
		 if(KitManager.isKitPlayer(p))
		 {
			 if(isBanedItem(item))
			 {
				 p.sendMessage(Messages.youCanNotUseThisItem);
				 e.getItemDrop().remove();
			 }
			 
			 return;
		 }
		 
		 if(item != null && item.getType() != null && item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null)
		 {
			 if(item.getItemMeta().getDisplayName().equals(Items.challengeItemName))
			 {
					ChallangeManager.getEnquiryMenu(p).open();
			 }
			 else if(item.getItemMeta().getDisplayName().equals(Items.challengeSkullName))
			 {
				 ChallangeManager.getEnquiryMenu(p).open();
				 if(item.getAmount() == 0)
				 {
					 item.setAmount(1);
				 }
			 }
		 }
		 
		 e.setCancelled(true);
	 }
	 
	 @EventHandler
	 public void onPlayerDeath(PlayerDeathEvent e)
	 {
		 if(!checkLobby(e.getEntity()))
		 {
			 return;
		 }
		 
		 e.setKeepInventory(true);
		 
		 Player p = e.getEntity();
		 
		 p.setHealth(p.getMaxHealth());
		 p.teleport(spawn);
		 
	 }

	 @EventHandler
	 public void onPlayerMove(PlayerMoveEvent e)
	 {
		 Player p = e.getPlayer();
		 
		 if(isInKitPlace(p) && !onKitTriggerPlayers.contains(p))
		 {
			 onKitTriggerPlayers.add(p);
			 KitManager.setKitMode(p);
		 }
		 else if(!isInKitPlace(p) && onKitTriggerPlayers.contains(p))
		 {
			 onKitTriggerPlayers.remove(p);
		 }
		 
		 if(isInNormalPlace(p) && !onNormalTriggerPlayers.contains(p))
		 {
			onNormalTriggerPlayers.add(p);
			KitManager.setNormalMode(p);
		 }
		 else if(!isInNormalPlace(p) && onNormalTriggerPlayers.contains(p))
		 {
			 onNormalTriggerPlayers.remove(p);
		 }
		 
		 Location from = e.getFrom().clone();
		 Location to = e.getTo().clone();
		 
		 boolean xyzMove = false;
		 
		 if(to.subtract(from).length() > 0.1)
		 {
			 xyzMove = true;
		 }
		 
		 if(xyzMove && kitViewer.contains(p.getUniqueId()))
		 {
			 kitViewer.remove(p.getUniqueId());
			 setupSpawnItems(p, false,TournamentManager.getTournament(p) != null);
		 }
		 
	 }
	 
	 @EventHandler
	 public void onPlayerDamage(EntityDamageEvent e)
	 {
		 if(!(e.getCause().equals(DamageCause.ENTITY_ATTACK) && e.getEntity() instanceof Player))
			 e.setCancelled(true);
	 }
	 
	 @EventHandler
	 public void onSneak(PlayerToggleSneakEvent e)
	 {
		 Player p = e.getPlayer();
		 
		 if(p.isSneaking() && Settings.getSettings(p).addToWSOnJoin())
		 {
			 p.sendMessage(Messages.quickJoinCancelled);
			 plugin.getWaitingSnake().removePlayer(p, true,true);
			 Settings.getSettings(p).addToWSOnJoin(false);
			 Settings.getSettings(p).loadToMySQL();
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
				msgout.writeUTF(plugin.serverName);
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
	 
	@EventHandler
	 public void onChat(PlayerChatEvent e)
	 {
		if(Statistics.statsResetter.get(e.getPlayer().getUniqueId()) != null)
		{
			if(e.getMessage().equalsIgnoreCase("ja"))
			{
				StatisticsManager.realReset(Statistics.statsResetter.get(e.getPlayer().getUniqueId()));
				
				if(!e.getPlayer().getUniqueId().equals(Statistics.statsResetter.get(e.getPlayer().getUniqueId())))
					e.getPlayer().sendMessage(Messages.statsResetOther(Bukkit.getOfflinePlayer(Statistics.statsResetter.get(e.getPlayer().getUniqueId())).getName()));
				
				Statistics.statsResetter.remove(e.getPlayer().getUniqueId());
			}
			else if(e.getMessage().equalsIgnoreCase("nein"))
			{
				if(e.getPlayer().getUniqueId().equals(Statistics.statsResetter.get(e.getPlayer().getUniqueId())))
					e.getPlayer().sendMessage(Messages.statsNotReset);
				else
					e.getPlayer().sendMessage(Messages.statsNotResetOther(Bukkit.getOfflinePlayer(Statistics.statsResetter.get(e.getPlayer().getUniqueId())).getName()));
				Statistics.statsResetter.remove(e.getPlayer().getUniqueId());
			}
			else
			{
				e.getPlayer().sendMessage(Messages.statsWrongInput);
			}
			
			e.setCancelled(true);
			return;
		}
		
		ArrayList<String> servers = ArenaManager.getServers(0);
		
		for(int i = 0;i<servers.size();i++)
			sendChatToServer(e.getPlayer(),getPrefix(e.getPlayer()), servers.get(i), e.getMessage());
	 }
	
	@EventHandler
	public void onBow(EntityShootBowEvent e)
	{
		e.setCancelled(true);
	}

}
