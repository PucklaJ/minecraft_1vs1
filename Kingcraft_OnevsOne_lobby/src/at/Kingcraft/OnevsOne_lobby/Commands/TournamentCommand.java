package at.Kingcraft.OnevsOne_lobby.Commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.Duels.Team;
import at.Kingcraft.OnevsOne_lobby.Duels.TeamManager;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import at.Kingcraft.OnevsOne_lobby.Special.MenuManager;
import at.Kingcraft.OnevsOne_lobby.Tournaments.Tournament;
import at.Kingcraft.OnevsOne_lobby.Tournaments.TournamentManager;
import net.md_5.bungee.api.ChatColor;

public class TournamentCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only for Players!");
			return true;
		}
		
		Player me = (Player) sender;
		
		Team team = TeamManager.getTeam(me);
		
		if(args.length == 0)
		{
			Tournament t = TournamentManager.getTournament(me);
			if(t==null)
			{
				me.sendMessage(Messages.noTournament);
				return true;
			}
			
			MenuManager.getTournamentViewMenu(me, true).loadTournament(t.getLeader());
			MenuManager.getTournamentViewMenu(me, false).open();
		}
		else if(args.length == 1)
		{
			if(args[0].equalsIgnoreCase("create"))
			{
				if(team != null && !team.getLeader().getUniqueId().equals(me.getUniqueId()))
				{
					me.sendMessage(Messages.onlyLeader);
					return true;
				}
				else if(!me.hasPermission("command.turnier.create"))
				{
					me.sendMessage(Messages.tournamentNoPermissionCreate);
					return true;
				}
				TournamentManager.createTournament(me);
			}
			else if(args[0].equalsIgnoreCase("start"))
			{
				return me.performCommand("turnier start 10");
			}
			else if(args[0].equalsIgnoreCase("leave"))
			{
				if(team != null && !team.getLeader().getUniqueId().equals(me.getUniqueId()))
				{
					me.sendMessage(Messages.onlyLeader);
					return true;
				}
				Tournament t = TournamentManager.getTournament(me);
				
				if(t == null)
				{
					me.sendMessage(Messages.noTournament);
					return true;
				}
				
				ArrayList<Player> players;
				if(team != null)
				{
					players = team.getPlayers();
				}
				else
				{
					players = new ArrayList<>();
					players.add(me);
				}
				
				t.removeContestants(players, true,true,true);
			}
			else if(args[0].equalsIgnoreCase("cancel"))
			{
				Tournament t = TournamentManager.getTournament(me);
				
				if(t == null)
				{
					me.sendMessage(Messages.noTournament);
					return true;
				}
				
				if(!t.getLeader().getUniqueId().equals(me.getUniqueId()))
				{
					me.sendMessage(Messages.onlyLeader);
					return true;
				}
				
				if(!t.cancelStartcountdown())
				{
					me.sendMessage(Messages.tournamentHasntBeenStarted);
				}
				else
				{
					for(int i = 0;i<t.getContestants().size();i++)
						for(int j = 0;j<t.getContestants().get(i).size();j++)
							t.getContestants().get(i).get(j).sendMessage(Messages.tournamentStartHasBeenCancelled);
				}
				
				return true;
			}
			else
			{
				return false;
			}
		}
		else if(args.length == 2)
		{
			if(args[0].equalsIgnoreCase("join"))
			{
				if(team != null && !team.getLeader().getUniqueId().equals(me.getUniqueId()))
				{
					me.sendMessage(Messages.onlyLeader);
					return true;
				}
				else if(!me.hasPermission("command.turnier.join"))
				{
					me.sendMessage(Messages.tournamentNoPermissionJoin);
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
					me.sendMessage(Messages.isNotOnline(args[0]));
					return true;
				}
				
				if(!TournamentManager.joinTournament(me, other))
				{
					me.sendMessage(Messages.noTournamentOther(other.getDisplayName()));
					return true;
				}
			}
			/*else if(args[0].equalsIgnoreCase("spectate"))
			{
				try
				{
					if(!TournamentManager.addSpectator(me, Integer.valueOf(args[1])))
					{
						me.sendMessage(Messages.noTournamentID(args[1]));
					}
				}
				catch(NumberFormatException e)
				{
					me.sendMessage(Messages.noRightID(args[1]));
				}
				
				return true;
				
			}*/
			else if(args[0].equalsIgnoreCase("start"))
			{

				int time = 10;
				try
				{
					time = Integer.valueOf(args[1]);
				}
				catch(IllegalArgumentException e)
				{
					me.sendMessage(Messages.isNoNumber(args[1]));
					return true;
				}
				
				
				Tournament t = TournamentManager.getTournament(me);
				
				if(t== null)
				{
					me.sendMessage(Messages.noTournament);
					return true;
				}
				else if(!t.getLeader().getUniqueId().equals(me.getUniqueId()))
				{
					me.sendMessage(Messages.onlyLeaderTourStart);
					return true;
				}
				
				
				
				switch(t.start(time))
				{
				case Tournament.HAS_STARTED:
					me.sendMessage(Messages.alreadyTourStart);
					break;
				case Tournament.NO_ARENAS:
					me.sendMessage(Messages.notEnoughArenas);
					break;
				case Tournament.NOT_ENOUGH_PLAYERS:
					me.sendMessage(Messages.tooLessContestants);
					break;
				case Tournament.UNKNOWN_ERROR:
					me.sendMessage(ChatColor.RED + "Unknown Error");
					break;
				default:
					break;
				}
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
		
		return true;
	}

}
