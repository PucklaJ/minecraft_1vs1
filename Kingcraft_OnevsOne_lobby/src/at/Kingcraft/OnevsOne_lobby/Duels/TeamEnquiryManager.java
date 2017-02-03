package at.Kingcraft.OnevsOne_lobby.Duels;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.Messaging.Messages;
import at.Kingcraft.OnevsOne_lobby.Special.EnquieryMenu;

public class TeamEnquiryManager
{
	public static final int SENDER = 0;
	public static final int RECIEVER = 1;
	public static final int NOTHING = 2;
	private static ArrayList<TeamEnquiry> teamEnquiris;
	
	public static void setup()
	{
		teamEnquiris = new ArrayList<TeamEnquiry>();
	}
	
	public static void newEnqiry(Player p1,Player p2)
	{
		teamEnquiris.add(new TeamEnquiry(p1, p2));
		
		setupEnquiryMenuTeam(p1);
		setupEnquiryMenuTeam(p2);
	}
	
	public static void setupEnquiryMenuTeam(Player p)
	{
		EnquieryMenu em = ChallangeManager.getEnquiryMenu(p);
		
		em.setTeamEnquiries(getEnquiries(p));
		
		em.updateInventory();
	}
	
	public static void newEnqiry(Team t,Player p)
	{
		teamEnquiris.add(new TeamEnquiry(t, p));
		
		setupEnquiryMenuTeam(t.getLeader());
		setupEnquiryMenuTeam(p);
	}
	
	public static void deleteEnquiriesIn(Player p,boolean message)
	{
		for(int i = 0;i<teamEnquiris.size();i++)
		{
			TeamEnquiry tey = teamEnquiris.get(i);
			Player pSender = tey.getPSender();
			Team tSender = tey.getTSender();
			
			if(tey.getRole(p) == TeamEnquiryManager.RECIEVER)
			{
				deleteEnquiry(tey);
				if(pSender != null)
				{
					if(message)
					pSender.sendMessage(Messages.enquieryRefuseRecieverOther(p.getDisplayName()));
					setupEnquiryMenuTeam(pSender);
				}
				else if(tSender != null)
				{
					if(message)
					tSender.getLeader().sendMessage(Messages.enquieryRefuseRecieverOther(p.getDisplayName()));
					setupEnquiryMenuTeam(tSender.getLeader());
				}
			}
		}
		
		setupEnquiryMenuTeam(p);
	}
	
	public static void deleteEnquiries(Player p,boolean message)
	{
		ArrayList<TeamEnquiry> te = getEnquiries(p);
		for(int i = 0;i<te.size();i++)
		{
			TeamEnquiry tey = te.get(i);
			Player pSender = tey.getPSender();
			Team tSender = tey.getTSender();
			
			deleteEnquiry(tey);
			if(pSender != null)
			{
				if(message)
				pSender.sendMessage(Messages.enquieryRefuseRecieverOther(p.getDisplayName()));
				setupEnquiryMenuTeam(pSender);
			}
			else if(tSender != null)
			{
				if(message)
				tSender.getLeader().sendMessage(Messages.enquieryRefuseRecieverOther(p.getDisplayName()));
				setupEnquiryMenuTeam(tSender.getLeader());
			}
			
			tey.getReciever().sendMessage(Messages.enquieryRefuseRecieverOther(p.getDisplayName()));
			setupEnquiryMenuTeam(tey.getReciever());
			
		}
		
		setupEnquiryMenuTeam(p);
	}
	
	public static void deleteEnquiry(Player p1,Player p2)
	{
		ArrayList<TeamEnquiry> te1 = getEnquiries(p1);
		
		for(int i = 0;i<te1.size();i++)
		{
			if(te1.get(i).getReciever().getUniqueId().equals(p2.getUniqueId()))
			{
				deleteEnquiry(te1.get(i));
				setupEnquiryMenuTeam(p1);
				setupEnquiryMenuTeam(p2);
				return;
			}
		}
		
		ArrayList<TeamEnquiry> te2 = getEnquiries(p2);
		
		for(int i = 0;i<te2.size();i++)
		{
			if(te2.get(i).getReciever().getUniqueId().equals(p1.getUniqueId()))
			{
				deleteEnquiry(te2.get(i));
				setupEnquiryMenuTeam(p1);
				setupEnquiryMenuTeam(p2);
				return;
			}
		}
	}
	
	public static void deleteEnquiry(TeamEnquiry te)
	{
		teamEnquiris.remove(te);
	}
	
	public static void deleteEnquiries(Player p,Player other,boolean message)
	{
		ArrayList<TeamEnquiry> te = getEnquiries(p);
		for(int i = 0;i<te.size();i++)
		{
			TeamEnquiry tey = te.get(i);
			Player pSender = tey.getPSender();
			Team tSender = tey.getTSender();
			
			deleteEnquiry(tey);
			
			if(pSender != null && !pSender.getUniqueId().equals(other.getUniqueId()))
			{
				if(message)
				pSender.sendMessage(Messages.enquieryRefuseRecieverOther(p.getDisplayName()));
				setupEnquiryMenuTeam(pSender);
			}
			else if(tSender != null && !tSender.isPartOf(other))
			{
				if(message)
				tSender.getLeader().sendMessage(Messages.enquieryRefuseRecieverOther(p.getDisplayName()));
				setupEnquiryMenuTeam(tSender.getLeader());
			}
			
			if(!tey.getReciever().getUniqueId().equals(other.getUniqueId()))
			{
				if(message)
				tey.getReciever().sendMessage(Messages.enquieryRefuseRecieverOther(p.getDisplayName()));
				setupEnquiryMenuTeam(tey.getReciever());
			}
			
			
		}
		
		setupEnquiryMenuTeam(p);
		setupEnquiryMenuTeam(other);
	}
	
	public static int exists(Player p1,Player p2)
	{
		ArrayList<TeamEnquiry> te1 = getEnquiries(p1);
		
		for(int i = 0;i<te1.size();i++)
		{
			if(te1.get(i).getReciever().getUniqueId().equals(p2.getUniqueId()))
			{
				return SENDER;
			}
		}
		
		ArrayList<TeamEnquiry> te2 = getEnquiries(p2);
		
		for(int i = 0;i<te2.size();i++)
		{
			if(te2.get(i).getReciever().getUniqueId().equals(p1.getUniqueId()))
			{
				return RECIEVER;
			}
		}
		
		return NOTHING;
	}
	
	public static TeamEnquiry getEnquiry(Player p1,Player p2)
	{
		
		ArrayList<TeamEnquiry> te1 = getEnquiries(p1);
		
		for(int i = 0;i<te1.size();i++)
		{
			if(te1.get(i).getReciever().getUniqueId().equals(p2.getUniqueId()))
			{
				return te1.get(i);
			}
		}
		
		ArrayList<TeamEnquiry> te2 = getEnquiries(p2);
		
		for(int i = 0;i<te2.size();i++)
		{
			if(te2.get(i).getReciever().getUniqueId().equals(p1.getUniqueId()))
			{
				return te2.get(i);
			}
		}
		
		return null;
	}
	
	public static ArrayList<TeamEnquiry> getEnquiries(Player p)
	{
		ArrayList<TeamEnquiry> te = new ArrayList<>();
		for(int i = 0;i<teamEnquiris.size();i++)
		{
			TeamEnquiry tey = teamEnquiris.get(i);
			Player sender = tey.getPSender();
			Team sender1 = tey.getTSender();
			if(sender != null && sender.getUniqueId().equals(p.getUniqueId()))
			{
				te.add(tey);
			}
			else if(sender1 != null && sender1.isPartOf(p))
			{
				te.add(tey);
			}
			else if(tey.getReciever().getUniqueId().equals(p.getUniqueId()))
			{
				te.add(tey);
			}
		}
		
		return te;
	}
}
