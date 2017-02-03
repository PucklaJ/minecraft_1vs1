package at.Kingcraft.OnevsOne_lobby.WaitingSnake;

import java.util.UUID;

public class RankedUpload
{
	public UUID uuid;
	public int elo;
	public int kit;
	public String map;
	public String homeServer;
	
	public RankedUpload(UUID uuid,int elo,int kit,String map,String homeServer)
	{
		this.uuid = uuid;
		this.elo = elo;
		this.kit = kit;
		this.map = map;
		this.homeServer = homeServer;
	}
	
	@Override
	public String toString()
	{
		return uuid.toString() + ";" + elo + ";" + kit + ";" + map + ";" + homeServer;
	}
	
	public static RankedUpload fromString(String str)
	{
		String[] str1 = str.split(";");
		
		return new RankedUpload(UUID.fromString(str1[0]), Integer.valueOf(str1[1]), Integer.valueOf(str1[2]),str1[3],str1[4]);
	}
}
