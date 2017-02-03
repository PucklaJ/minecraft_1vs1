package at.Kingcraft.OnevsOne_lobby.WaitingSnake;

import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;
import at.Kingcraft.OnevsOne_lobby.Special.MapMenu;
import at.Kingcraft.OnevsOne_lobby.Special.MenuManager;

public class UploadPlayer
{

	public UploadPlayer(String uuid,
			String settings,
			String serverName,
			String kit,
			String arena)
	{
		this.uuid = uuid;
		this.serverName = serverName;
		this.settings = settings;
		this.kit = kit;
		this.arena = arena;
	}
	
	public static UploadPlayer toUploadPlayer(Player p)
	{
		String uuid = p.getUniqueId().toString();
		String settings = Settings.getSettings(p).toString();
		String serverName = MainClass.getInstance().serverName;
		String kit = KitManager.getChoosenKit(p).getKit().itemsToString();
		String arena = MapMenu.getRandomArena(MenuManager.getSettingMenu(p).getMapMenu().getMyMaps(), null);
		
		return new UploadPlayer(uuid, settings, serverName, kit, arena);
	}
	
	public String uuid;
	public String settings;
	public String serverName;
	public String kit;
	public String arena;
}
