package at.Kingcraft.OnevsOne_lobby.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.Sounds;
import at.Kingcraft.OnevsOne_lobby.Duels.ChallangeManager;
import at.Kingcraft.OnevsOne_lobby.Duels.Team;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamEnquiry;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamEnquiryManager;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamManager;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import net.md_5.bungee.api.ChatColor;

public class TeamCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only for Players");
			return true;
		}
		
		Player me = (Player)sender;
		
		if(args.length == 1)
		{
			if(args[0].equalsIgnoreCase("refuse"))
			{
				return false;
			}
			
			return enquieryCommand(me, args);
			
		}
		else if(args.length == 2)
		{
			if(args[0].equalsIgnoreCase("refuse"))
			{
				return refuse(me,args);
			}
			else if(args[0].equalsIgnoreCase("kick"))
			{
				return kick(me,args);
			}
		}
		
		
		
		
		return false;
	}
	
	private boolean kick(Player me,String[] args)
	{
		Team t = TeamManager.getTeam(me);
		if(t == null)
		{
			me.sendMessage(Messages.noTeam);
			return true;
		}
		
		if(!t.getLeader().getUniqueId().equals(me.getUniqueId()))
		{
			me.sendMessage(Messages.onlyLeaderKick);
			return true;
		}
		
		Player other = null;
		
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(p.getDisplayName().equalsIgnoreCase(args[1]))
			{
				other = p;
			}
		}
		
		if(other == null)
		{
			me.sendMessage(Messages.isNotOnline(args[1]));
			return true;
		}
		
		if(!t.isPartOf(other))
		{
			me.sendMessage(Messages.isNotYourTeam(other.getDisplayName()));
			return true;
		}
		
		if(other.getUniqueId().equals(me.getUniqueId()))
		{
			me.sendMessage(Messages.notSelfKick);
			return true;
		}
		
		if(!t.removePlayer(other, true, true))
		{
			TeamManager.deleteTeam(t, false,true);
		}
		
		return true;
	}
	
	private boolean enquieryCommand(Player me, String[] args)
	{
		Team t = TeamManager.getTeam(me);
		if(t != null)
		{
			if(args[0].equalsIgnoreCase("leave"))
			{
				if(!t.removePlayer(me,true,false))
				{
					TeamManager.deleteTeam(t,false,true);
				}
				me.sendMessage(Messages.youTeamLeave);
				return true;
			}
			
			
			// Send Enquiry
			
			// Get Player
			
			Player other = null;
			
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(p.getDisplayName().equalsIgnoreCase(args[0]))
				{
					other = p;
					break;
				}
			}
			
			if(other == null)
			{
				me.sendMessage(Messages.isNotOnline(args[0]));
				return true;
			}
			
			
			// Check if other is in a Team
			
			Team tOther = TeamManager.getTeam(other);
			if(tOther != null)
			{
				if(tOther.getID() == t.getID())
				{
					me.sendMessage(Messages.isYourTeam(other.getDisplayName()));
				}
				else
				{
					if(!t.getLeader().getUniqueId().equals(me.getUniqueId()))
					{
						me.sendMessage(Messages.onlyLeaderEnquierySend);
						return true;
					}
					me.sendMessage(Messages.isYourTeam(other.getDisplayName()));
				}
				
				return true;
			}
			if(!t.getLeader().getUniqueId().equals(me.getUniqueId()))
			{
				me.sendMessage(Messages.onlyLeaderEnquierySend);
				return true;
			}
			
			// Accept Enqiry if exists
			
			TeamEnquiry exists = TeamEnquiryManager.getEnquiry(me,other);
			if(exists != null)
			{
				int role = exists.getRole(me);
				if(role == TeamEnquiryManager.SENDER)
				{
					me.performCommand("team refuse " + other.getDisplayName());
					return true;
				}
				else if(role == TeamEnquiryManager.RECIEVER)
				{
					exists.acceptEnqiry();
					TeamEnquiryManager.deleteEnquiries(me,other,false);
				}
			}
			
			
			// Make Enquiry
			if(!t.isPartOf(other))
			{
				TeamEnquiryManager.newEnqiry(t, other);
				me.sendMessage(Messages.enquierySend(other.getDisplayName()));
				me.playSound(me.getLocation(), Sounds.sendEnquiery, Sounds.sendEnquieryVolume, Sounds.DEFAULT_PITCH);
				other.sendMessage(Messages.gotEnquiery(me.getDisplayName()));
				other.playSound(other.getLocation(), Sounds.gotEnquiery, Sounds.gotEnquieryVolume, Sounds.DEFAULT_PITCH);
			}
			else
			{
				me.sendMessage(Messages.isYourTeam(other.getDisplayName()));
			}
			
			return true;
			
		}
		else
		{
			
			// Send Enquiry
			
			// Get Player
			
			Player other = null;
			
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(p.getDisplayName().equalsIgnoreCase(args[0]))
				{
					other = p;
					break;
				}
			}
			
			if(other == null)
			{
				if(args[0].equalsIgnoreCase("leave"))
				{
					me.sendMessage(Messages.noTeam);
				}
				else
				{
					me.sendMessage(Messages.isNotOnline(args[0]));
				}
				return true;
			}
			
			// Accept Enqiry if exists
			
			TeamEnquiry exists = TeamEnquiryManager.getEnquiry(me,other);
			if(exists != null)
			{
				int role = exists.getRole(me);
				if(role == TeamEnquiryManager.SENDER)
				{
					me.performCommand("team refuse " + other.getDisplayName());
					return true;
				}
				else if(role == TeamEnquiryManager.RECIEVER)
				{
					TeamEnquiryManager.deleteEnquiries(me,other,false);
					TeamEnquiryManager.deleteEnquiriesIn(other, true);
					ChallangeManager.deleteChallenges(me,other,true);
					ChallangeManager.deleteChallenges(other,me,true);
					
					exists.acceptEnqiry();
				}
				return true;
			}
			
			// Check if other is in a Team
			
			Team tOther = TeamManager.getTeam(other);
			if(tOther != null)
			{
				me.sendMessage(Messages.isYourTeam(other.getDisplayName()));
				return true;
			}
			
			
			
			TeamEnquiryManager.newEnqiry(me, other);
			me.sendMessage(Messages.enquierySend(other.getDisplayName()));
			other.sendMessage(Messages.gotEnquiery(me.getDisplayName()));
			
			return true;
		}
	}

	private boolean refuse(Player me, String[] args)
	{
		Player other = null;
		
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(p.getDisplayName().equalsIgnoreCase(args[1]))
			{
				other = p;
				break;
			}
		}
		
		if(other == null)
		{
			me.sendMessage(Messages.isNotOnline(args[1]));
			return true;
		}
		int role = TeamEnquiryManager.exists(me, other);
		
		if(role == TeamEnquiryManager.SENDER)
		{
			TeamEnquiryManager.deleteEnquiry(me,other);
			me.sendMessage(Messages.enquieryRefuseSender);
			other.sendMessage(Messages.enquieryRefuseSenderOther(me.getDisplayName()));
		}
		else if(role == TeamEnquiryManager.RECIEVER)
		{
			TeamEnquiryManager.deleteEnquiry(me,other);
			me.sendMessage(Messages.enquieryRefuseReciever);
			other.sendMessage(Messages.enquieryRefuseRecieverOther(me.getDisplayName()));
		}
		else
		{
			me.sendMessage(Messages.noEnquiery(other.getDisplayName()));
		}
		
		return true;
	}
}
