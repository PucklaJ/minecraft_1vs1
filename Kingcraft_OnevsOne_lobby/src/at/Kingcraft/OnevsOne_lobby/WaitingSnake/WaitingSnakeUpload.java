package at.Kingcraft.OnevsOne_lobby.WaitingSnake;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;


public class WaitingSnakeUpload
{
	public UUID uuid;
	public Settings set;
	public String serverName;
	public String kit;
	public String arena;
	public String team;
	public String name;
	public OfflinePlayer op;
	
	public WaitingSnakeUpload(UUID uuid,Settings set,String serverName,String kit,String arena,String team,String name)
	{
		this.uuid = uuid;
		this.set = set;
		this.serverName = serverName;
		this.kit = kit;
		this.arena = arena;
		this.team = team;
		this.name = name;
		
		op = Bukkit.getOfflinePlayer(uuid);
	}
	
	public WaitingSnakeUpload clone()
	{
		return new WaitingSnakeUpload(uuid, set, serverName, kit, arena, team, name);
	}
}
