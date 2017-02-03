package at.Kingcraft.OnevsOne_lobby.Commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.Duels.ChallangeManager;
import at.Kingcraft.OnevsOne_lobby.Duels.Challenge;
import at.Kingcraft.OnevsOne_lobby.Duels.Team;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamManager;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import net.md_5.bungee.api.ChatColor;

public class RefuseCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player me = null;
		if(sender instanceof Player)
		{
			me = (Player)sender;
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "Only for Players");
			return true;
		}
		
		Team t = TeamManager.getTeam(me);
		if(t != null)
		{
			if(!t.getLeader().getUniqueId().equals(me.getUniqueId()))
			{
				me.sendMessage(Messages.onlyLeaderRefuse);
				return true;
			}
		}
		
		ArrayList<Player> other = null;
		
		if(args.length > 1)
		{
			return false;
		}
		else if(args.length == 0) // Zuletzt geschickte bzw. bekomene Challenge refusen
		{
			Challenge c = ChallangeManager.getChallenge(me);
			if(c == null)
			{
				me.sendMessage(Messages.youDidntGetAnyChallenges);
				return true;
			}
			ChallangeManager.deleteChallenge(me, null, c, true, true, true,true);
			return true;
		}
		else if(args.length == 1) // Challenge mit Spieler refusen
		{
			other = new ArrayList<Player>();
			if(!refusePlayer(me, other, args))
			{
				return true;
			}
		}
		
		return true;
	}

	
	public static void refuseEverything(Player me)
	{
		if(me == null)
		{
			return;
		}
		ChallangeManager.deleteChallenges(me,null,true);
		
	}
	
	public static boolean refusePlayer(Player me, ArrayList<Player> other,String[] args)
	{
		// Anderen Spieler holen
							
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(p.getDisplayName().equals(args[0]))
			{
				other.add(p);
			}
		}
		
		// Überprüfen ob Spieler online ist
		if(other.size() == 0)
		{
			me.sendMessage(Messages.isNotOnline(args[0]));
			return false;
		}
		else // Wenn Spieler online
		{
			Challenge c = ChallangeManager.getChallenge(me, other.get(0));
			if(c == null)
			{
				c = ChallangeManager.getChallenge(other.get(0), me);
				if(c == null)
				{
					me.sendMessage(Messages.noChallenge(other.get(0).getDisplayName()));
					return true;
				}
			}
			
			ChallangeManager.deleteChallenge(me, null, c, true, true, true,true);
			
			
		}
		
		return true;
	}
}
