package at.kingcraft.OnevsOne_arena.Kits;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ChooseKit
{
	public int myNumber;
	public int otherNumber;
	public Player player;
	public Player otherPlayer = null;
	public OfflinePlayer otherOPlayer = null;
	
	public ChooseKit(Player p,Player otherPlayer,int myNumber,int otherNumber)
	{
		this.otherPlayer = otherPlayer;
		this.myNumber = myNumber;
		this.otherNumber = otherNumber;
		player = p;
	}
	public ChooseKit(Player p,OfflinePlayer otherPlayer,int myNumber,int otherNumber)
	{
		this.otherOPlayer = otherPlayer;
		this.myNumber = myNumber;
		this.otherNumber = otherNumber;
		player = p;
	}
	
	@Override
	public String toString()
	{
		return (otherPlayer != null ? otherPlayer.getUniqueId().toString() + ";" + otherNumber + ";" : otherOPlayer != null ? otherOPlayer.getUniqueId().toString() + ";" + otherNumber + ";" : "") + myNumber;
	}
	
	public static ChooseKit fromString(Player p,String str)
	{
		String[] parts = str.split(";");
		
		ChooseKit ck = new ChooseKit(p, null, 0, Integer.MAX_VALUE);
		
		if(parts.length == 3)
		{
			ck.otherOPlayer = Bukkit.getOfflinePlayer(UUID.fromString(parts[0]));
			ck.otherNumber = Integer.valueOf(parts[1]);
			ck.myNumber = Integer.valueOf(parts[2]);
		}
		else
		{
			ck.myNumber = Integer.valueOf(parts[0]);
		}
		
		return ck;
	}
}
