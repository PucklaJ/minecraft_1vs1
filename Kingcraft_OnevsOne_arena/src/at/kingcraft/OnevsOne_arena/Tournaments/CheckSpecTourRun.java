package at.kingcraft.OnevsOne_arena.Tournaments;

import at.kingcraft.OnevsOne_arena.Kits.Kit;
import at.kingcraft.OnevsOne_arena.Messaging.Messenger;

public class CheckSpecTourRun implements Runnable
{

	private Spectator s;
	private Tournament t;
	private int iterations = 0;
	
	public CheckSpecTourRun(Spectator s,Tournament t)
	{
		this.s = s;
		this.t = t;
	}
	
	private void teleportToDuelServer()
	{
		String server = t.getRounds().get(0).getServer();
		TournamentManager.leftBySpectator.add(s.player.getUniqueId());
		Messenger.sendMessage(s.player, "BungeeCord", "Connect", server);
	}
	
	@Override
	public void run()
	{
		if(iterations == 3)
		{
			s.sendBack();
			return;
		}
		
		t = TournamentManager.getTournamentFromMySQL(null, s.tournamentID, "null", new Kit(s.player,1,false),5,10);
		if(t == null)
		{
			s.sendBack();
			return;
		}
		else
		{
			if(t.getRounds().isEmpty())
			{
				iterations++;
				try
				{
					Thread.sleep(2000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
					return;
				}
				
				return;
			}
			else
			{
				teleportToDuelServer();
				return;
			}
		}
		
		
		
		
	}

}
