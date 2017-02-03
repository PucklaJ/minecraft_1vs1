package at.Kingcraft.OnevsOne_lobby.Duels;



import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;

public class TeamEnquiry
{
	private Team tSender;
	private Player reciever;
	private Player pSender;
	
	public TeamEnquiry(Player sender,Player reciever)
	{
		pSender = sender;
		this.reciever = reciever;
	}
	
	public TeamEnquiry(Team sender,Player reciever)
	{
		tSender = sender;
		this.reciever = reciever;
	}
	
	public Player getPSender()
	{
		return pSender;
	}
	
	public Team getTSender()
	{
		return tSender;
	}
	
	public Player getReciever()
	{
		return reciever;
	}
	
	public int getRole(Player p)
	{
		if((pSender != null && pSender.getUniqueId().equals(p.getUniqueId())) || (tSender != null && tSender.isPartOf(p)))
		{
			return TeamEnquiryManager.SENDER;
		}
		else if(reciever.getUniqueId().equals(p.getUniqueId()))
		{
			return TeamEnquiryManager.RECIEVER;
		}
		else
		{
			return TeamEnquiryManager.NOTHING;
		}
	}
	
	
	public void acceptEnqiry()
	{
		if(tSender != null)
		{
			tSender.addPlayer(reciever,true);
			reciever.sendMessage(Messages.teamJoinYou(tSender.getLeader().getDisplayName()));
		}
		else if(pSender != null)
		{
			
			Team t = TeamManager.getTeam(pSender);
			if(t != null)
			{
				t.addPlayer(reciever,true);
			}
			else
			{
				TeamManager.newTeam(pSender, reciever);
			}
			reciever.sendMessage(Messages.teamJoinYou(pSender.getDisplayName()));
			
		}
		
		
	}
}
