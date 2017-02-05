package at.Kingcraft.OnevsOne_lobby.Commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.Sounds;
import at.Kingcraft.OnevsOne_lobby.Arenas.ArenaManager;
import at.Kingcraft.OnevsOne_lobby.Duels.ChallangeManager;
import at.Kingcraft.OnevsOne_lobby.Duels.Challenge;
import at.Kingcraft.OnevsOne_lobby.Duels.DuelManager;
import at.Kingcraft.OnevsOne_lobby.Duels.SpectateManager;
import at.Kingcraft.OnevsOne_lobby.Duels.Team;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamManager;
import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;
import at.Kingcraft.OnevsOne_lobby.Lobby.LobbyListener;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import at.Kingcraft.OnevsOne_lobby.Special.MapMenu;
import at.Kingcraft.OnevsOne_lobby.Special.MenuManager;
import net.md_5.bungee.api.ChatColor;

public class OnevsOneCommand implements CommandExecutor {


	public OnevsOneCommand() {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{

		Player me = null;

		if (sender instanceof Player)
		{
			me = (Player) sender;
		
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "Only for Players");
			return true;
		}
		
		if(LobbyListener.ovoCmdBlock.contains(me.getUniqueId()))
		{
			return true;
		}

		if(args.length == 0)
		{
			// Öffnet Herausforderungs GUI
			ChallangeManager.getEnquiryMenu(me).open();
			return true;
		}
		else if (args.length == 1)
		{
			return commandWithPlayer(me, args);
		}

		return false;
	}
	
	private boolean commandWithPlayer(Player me,String[] args)
	{
		if(TeamManager.getsChecked(me))
		{
			me.sendMessage(Messages.teamRecreate);
			return true;
		}
		
		Team t = TeamManager.getTeam(me);
		if(t != null)
		{
			if(!t.getLeader().getUniqueId().equals(me.getUniqueId()))
			{
				me.sendMessage(Messages.onlyLeaderChallenges);
				return true;
			}
		}
		
		Player other = null;
		for (Player ps : Bukkit.getServer().getOnlinePlayers()) {
			if (ps.getDisplayName().equals(args[0]))
				other = ps;
		}

		if (other == null) {
			me.sendMessage(Messages.isNotOnline(args[0]));
			return true;
		}

		// Spieler ist online
		
		if(me.getUniqueId().equals(other.getUniqueId()))
		{
			me.sendMessage(Messages.notSelf);
			return true;
		}
		
		// Überprüfen ob Challenge existend
		if (ChallangeManager.getChallenge(me, other) != null)
		{
			me.performCommand("refuse " + other.getDisplayName());
		}
		else
		{
			sendChallenge(me, other);
		}

		return true;
	}
	
	private void sendChallenge(Player me,Player other)
	{			
		Challenge chall = null;
		// Check if me is in team
		Team t = TeamManager.getTeam(me);
		Team t1 = TeamManager.getTeam(other);
		if(t != null && t1 != null)
		{
			if(t.getID() == t1.getID())
			{
				me.sendMessage(Messages.isYourTeam(other.getDisplayName()));
				return;
			}
			chall = ChallangeManager.sendChallenge(t.getPlayers(), t1.getPlayers());
		}
		else if(t != null && t1 == null)
		{
			ArrayList<Player> chads = new ArrayList<Player>();
			chads.add(other);
			chall = ChallangeManager.sendChallenge(t.getPlayers(), chads);
		}
		else if(t == null && t1 != null)
		{
			ArrayList<Player> chers = new ArrayList<Player>();
			chers.add(me);
			chall = ChallangeManager.sendChallenge(chers, t1.getPlayers());
		}
		else
		{
			chall = ChallangeManager.sendChallenge(me, other);
		}
		
		

		
		// Überprüfen welche Rolle ich habe
		int role = ChallangeManager.getRole(me, chall);

		// Bei Challenged: Challenge annehmen
		if (role == Challenge.IS_CHALLANGED)
		{
			takeChallenge(chall.getChallenged(), chall.getChallengers(), chall);
		}
		else
		{
			ChallangeManager.tellChallenge(chall.getChallengers(), chall.getChallenged());
		}
	}
	
	private void takeChallenge(ArrayList<Player> me,ArrayList<Player> other,Challenge chall)
	{
		
		for(int i = 0;i<other.size();i++)
		{
			other.get(i).sendMessage(Messages.otherTookChall(me.get(0).getDisplayName()));
			other.get(i).playSound(other.get(i).getLocation(), Sounds.otherTookChallenge, Sounds.otherTookChallengeVolume, Sounds.DEFAULT_PITCH);
		}
		
		for(int i = 0;i<me.size();i++)
		{
			me.get(i).sendMessage(Messages.tookChall);
			me.get(i).playSound(me.get(i).getLocation(), Sounds.tookChallenge, Sounds.tookChallengeVolume, Sounds.DEFAULT_PITCH);
		}

		
			// Zur Arena
			 String server = ArenaManager.giveArena();
			 
			 if(server.length() != 0)
			 { 
				 	String arena = MapMenu.getRandomArena(MenuManager.getSettingMenu(me.get(0)).getMapMenu().getMyMaps(),
				 										  MenuManager.getSettingMenu(other.get(0)).getMapMenu().getMyMaps());
				 	
				 	if(arena.equals(""))
				 	{
				 		for(int i = 0;i<me.size();i++)
						{
				    			me.get(i).sendMessage(Messages.thereArentAnyMaps);
						}
						for(int i = 0;i<other.size();i++)
						{
								other.get(i).sendMessage(Messages.thereArentAnyMaps);
						}
						
						return;
				 	}
				 	
				 	int error = DuelManager.sendDuelToSQL(chall,server,arena,KitManager.getDif(other.get(0)),-1,true,1);
				    if(error != 1)
				    {
				    	for(int i = 0;i<me.size();i++)
						{
				    			me.get(i).sendMessage(ChatColor.RED + "[MySQL] Error: " + error);
						}
						for(int i = 0;i<other.size();i++)
						{
								other.get(i).sendMessage(ChatColor.RED + "[MySQL] Error: " + error);
						}
						return;
				    }
				    
				    SpectateManager.uploadToMySQL(other, me, server,chall.ID);
				    
				    for(int i = 0;i<other.size();i++)
					{
				    	KitManager.uploadDifKit(other.get(i));
				    	ArenaManager.teleportToArena(other.get(i), server,true);
					}
				    for(int i = 0;i<me.size();i++)
					{
				    	KitManager.uploadDifKit(me.get(i));
				    	ArenaManager.teleportToArena(me.get(i), server,true);
					}
			 }
			 else
			 {
				for(int i = 0;i<other.size();i++)
				{
			    	other.get(i).sendMessage(Messages.noFreeArena);
				}
			    for(int i = 0;i<me.size();i++)
				{
			    	me.get(i).sendMessage(Messages.noFreeArena);
				}
			 }
			  
			 

	}

}
