package at.Kingcraft.OnevsOne_lobby.Special;

import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;

public class KitOwnMenu extends KitMenu {
	
	public KitOwnMenu(Player owner,KitMainMenu parent)
	{
		super(owner,parent,"Eigene Kits","Kit1-Einstellungen");
	}
	
	@Override
	public void reloadKits()
	{
		kits = KitManager.getKitsArray(owner);
		super.reloadKits();
	}
}
