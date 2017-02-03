package at.Kingcraft.OnevsOne_lobby.Tournaments;

import java.util.UUID;

public class TourPlayer
{
	public String name;
	public UUID uuid;
	
	public TourPlayer(String name,UUID uuid)
	{
		this.name = name;
		this.uuid = uuid;
	}
}
