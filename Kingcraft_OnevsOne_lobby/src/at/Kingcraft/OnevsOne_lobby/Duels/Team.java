package at.Kingcraft.OnevsOne_lobby.Duels;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Sounds;
import at.Kingcraft.OnevsOne_lobby.Items.Items;
import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import at.Kingcraft.OnevsOne_lobby.Scoreboard.MyScoreboardManager;
import at.Kingcraft.OnevsOne_lobby.Special.MenuManager;
import at.Kingcraft.OnevsOne_lobby.Tournaments.Tournament;
import at.Kingcraft.OnevsOne_lobby.Tournaments.TournamentManager;
import at.Kingcraft.OnevsOne_lobby.WaitingSnake.RankedQueue;
import net.md_5.bungee.api.ChatColor;

public class Team
{
	private ArrayList<Player> players;
	private ArrayList<UUID> playersUUID;
	private Player leader;
	private int id;
	private MainClass plugin;
	
	public Team(Player p,int id,MainClass plugin)
	{
		players = new ArrayList<Player>();
		playersUUID = new ArrayList<UUID>();
		setLeader(p);
		this.id = id;
		this.plugin = plugin;
		
		addPlayer(p,false);
	}
	
	public int getID()
	{
		return id;
	}
	
	public Player getLeader()
	{
		return leader;
	}
	
	public void setLeader(Player p)
	{	
		for(int i = 0;i<players.size();i++)
		{
			ItemStack apple = new ItemStack(Material.GOLDEN_APPLE);
			ItemMeta im = apple.getItemMeta();
			im.setDisplayName(ChatColor.GOLD + "Ranked");
			ArrayList<String> lore = new ArrayList<>();
			lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Comming Soon..");
			im.setLore(lore);
			apple.setItemMeta(im);
			
			players.get(i).getInventory().setItem(4, apple);
			
			if(leader != null && !leader.getUniqueId().equals(p.getUniqueId()))
			{
				players.get(i).sendMessage(Messages.isNewLeader(p.getDisplayName()));
			}
		}
		
		boolean needsSBU = leader != null;
		
		leader = p;
		//FFA-AppleMaterial mat = Material.getMaterial(Items.ffaItemMaterial);
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
		
		if(needsSBU)
		{
			for(int i = 0;i<players.size();i++)
			{
				MyScoreboardManager.updateScoreboard(players.get(i));
			}
		}
		
	}
	
	public ArrayList<Player> getPlayers()
	{
		return players;
	}
	
	public void reload()
	{
		for(int i = 0;i<players.size();i++)
		{
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(p.getUniqueId().equals(players.get(i).getUniqueId()))
				{
					players.remove(i);
					players.add(i, p);
				}
			}
		}
	}
	
	public void addPlayer(Player p, boolean message)
	{
		if(players.contains(p))
			return;
		
		if(message)
		{
			for(int i = 0;i<players.size();i++)
			{
				players.get(i).sendMessage(Messages.teamJoin(p.getDisplayName()));
				players.get(i).playSound(players.get(i).getLocation(), Sounds.teamJoin, Sounds.teamJoinVolume, Sounds.DEFAULT_PITCH);
			}
		}
		
		Tournament leaderTour = TournamentManager.getTournament(leader);
		
		players.add(p);
		playersUUID.add(p.getUniqueId());
		plugin.getWaitingSnake().removePlayer(p, false, true);
		if(RankedQueue.isInUpload(p) || RankedQueue.isInMySQL(p))
		{
			RankedQueue.removePlayer(p);
			MenuManager.getRankedMenu(p).toogleLeaveItem();
		}
		
		
		if(leaderTour != null)
		{
			leaderTour.removeContestants(getPlayers(), true, true, true);
		}
		
		
		if(!leader.getUniqueId().equals(p.getUniqueId()))
		{
			Tournament t = TournamentManager.getTournament(p);
			if(t != null)
			{
				ArrayList<Player> p1 = new ArrayList<>();
				p1.add(p);
				t.removeContestants(p1, true, true,true);	
			}
		}
		
		
		ArrayList<TeamEnquiry> te = TeamEnquiryManager.getEnquiries(leader);
		for(int i = 0;i<te.size();i++)
		{
			TeamEnquiryManager.setupEnquiryMenuTeam(te.get(i).getReciever());
		}
		
		ArrayList<Challenge> cs = ChallangeManager.getChallenges(leader);
		
		for(int i = 0;i<cs.size();i++)
		{
			int role = ChallangeManager.getRole(leader, cs.get(i));
			if(role == Challenge.IS_CHALLANGED)
			{
				cs.get(i).addPlayer(p, false);
			}
			else if(role == Challenge.IS_CHALLANGER)
			{
				cs.get(i).addPlayer(p, true);
			}
			
		}
		
		MenuManager.getSettingMenu(p).createTeamMenu();
		
		for(int i = 0;i<players.size();i++)
		{
			
			if(MenuManager.getSettingMenu(players.get(i)).getTeamMenu() == null)
			{
				
			}
			else
			{
				MenuManager.getSettingMenu(players.get(i)).getTeamMenu().setMembers(this);
			}
		}
		
		if(plugin.getWaitingSnake().isIn(leader))
		{
			p.sendMessage(Messages.waitingSnakeJoin);
			plugin.getWaitingSnake().removePlayer(leader, true,false);
			plugin.getWaitingSnake().addPlayer(leader,false);
		}
			
		
		for(int i = 0;i<players.size();i++)
		{
			MyScoreboardManager.updateScoreboard(players.get(i));
		}
		
	}
	
	public boolean removePlayer(Player p,boolean message,boolean kick)
	{
		return removePlayer(p.getUniqueId(),message,kick);
	}
	
	public boolean removePlayer(UUID u,boolean message,boolean kick)
	{
		Player p = null;
		
		for(int i = 0;i<playersUUID.size();i++)
		{
			if(playersUUID.get(i).equals(u))
			{
				p = players.get(i);
				
				MenuManager.getSettingMenu(p).deleteTeamMenu();
				
				if(kick)
				{
					p.sendMessage(Messages.teamKicked);
				}
				
				Tournament t = TournamentManager.getTournament(leader);
				if(t != null)
				{
					t.removeContestants(getPlayers(), true, true,true);
				}
				
				playersUUID.remove(i);
				players.remove(i);
				plugin.getWaitingSnake().removePlayer(p, false, true);
				
				break;
			}
		}
		
		if(p == null)
			return true;
		
			if(!KitManager.isKitPlayer(p))
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
		
			for(int i = 0;i<players.size();i++)
			{
				if(message)
				{
					if(kick)
					{
						players.get(i).sendMessage(Messages.teamKickOther(p.getDisplayName()));
					}
					else
					{
						players.get(i).sendMessage(Messages.teamLeaveOther(p.getDisplayName()));
					}
					
				}
				
				if(MenuManager.getSettingMenu(players.get(i)) != null && MenuManager.getSettingMenu(players.get(i)).getTeamMenu() != null)
					MenuManager.getSettingMenu(players.get(i)).getTeamMenu().setMembers(this);
			}
		
		
		ArrayList<Challenge> cs = ChallangeManager.getChallenges(leader);
		for(int i = 0;i<cs.size();i++)
		{
			cs.get(i).removePlayer(p);
		}
		
		if(!players.isEmpty() && plugin.getWaitingSnake().isIn(leader))
		{
			plugin.getWaitingSnake().removePlayer(leader, true, false);
			if(players.size() > 1)
				plugin.getWaitingSnake().addPlayer(leader, false);
		}
		
		
		if(players.size() < 2)
		{
			TeamEnquiryManager.deleteEnquiries(leader,true);
			if(message)
			{
				if(!players.isEmpty())
					players.get(0).sendMessage(Messages.teamDeleteAmount);
			}
			
			if(!players.isEmpty())
			{
				ItemStack apple = new ItemStack(Material.GOLDEN_APPLE);
				ItemMeta im = apple.getItemMeta();
				im.setDisplayName(ChatColor.GOLD + "Ranked");
				ArrayList<String> lore = new ArrayList<>();
				lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Comming Soon..");
				im.setLore(lore);
				apple.setItemMeta(im);
				
				players.get(0).getInventory().setItem(4, apple);
			}
			
			if(!players.isEmpty() && MenuManager.getSettingMenu(players.get(0)) != null)
				MenuManager.getSettingMenu(players.get(0)).deleteTeamMenu();
			
			MyScoreboardManager.updateScoreboard(p);
			
			return false;
		}
		
		if(u.equals(leader.getUniqueId()) && players.size() > 1)
		{
				setLeader(players.get(0));
				if(message)
				{
					players.get(0).sendMessage(Messages.newTeamLeader);
				}
				
		}
		
		
		for(int i = 0;i<players.size();i++)
		{
			MyScoreboardManager.updateScoreboard(players.get(i));
		}
		
		MyScoreboardManager.updateScoreboard(p);
		
		return true;
		
	}
	
	public boolean isPartOf(Player p)
	{
		for(int i = 0;i<playersUUID.size();i++)
		{
			if(playersUUID.get(i).equals(p.getUniqueId()))
			{
				return true;
			}
		}
		if(leader.getUniqueId().equals(p.getUniqueId()))
		{
			return true;
		}
		
		return false;
	}
}
