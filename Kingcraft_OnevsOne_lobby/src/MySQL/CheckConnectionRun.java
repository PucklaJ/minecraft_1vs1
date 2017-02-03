package MySQL;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CheckConnectionRun implements Runnable {

	private MySQL mysql;
	private int conCounter;
	
	public CheckConnectionRun(MySQL mysql)
	{
		this.mysql = mysql;
		conCounter = 0;
	}
	
	@Override
	public void run()
	{
		System.out.println("[MySQL] Running Check Run");
		
		if(!mysql.isConnected())
		{
			return;
		}
		
		try
		{
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT * FROM Duel_Servers");
			ps.executeQuery();
		}
		catch (SQLException e)
		{
			boolean connected = false;
			while(!connected && conCounter < 3)
			{
				System.out.println("[MySQL] has automatically disconnected");
				mysql.disconnect();
				
				connected = true;
				
				try
				{
					mysql.connect();
				}
				catch (SQLException e1)
				{
					connected = false;
					conCounter++;
				}
			}
			
			if(!connected)
			{
				System.out.println("[MySQL] Couldn't connect after trying 3 times");
			}
			else
			{
				System.out.println("[MySQL] Could connect after automatically disconnecting");
			}
		}
	}

}
