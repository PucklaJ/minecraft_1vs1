package at.Kingcraft.OnevsOne_lobby.Kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class PreKitsTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args)
	{
		if(cmd.getName().equals("kit") && args.length > 0 && sender instanceof Player)
		{	
			Player p = (Player) sender;
			String kitname = "";
			
			for(int i = 0;i<args.length;i++)
			{
				kitname += args[i] + (i+1==args.length ? "" : " ");
			}
			
			ArrayList<String> kitNames = new ArrayList<>();
			
			ArrayList<Kit> kits = KitManager.getAllKits(p);
			
			for(int i = 0;i<kits.size();i++)
			{
				String name = kits.get(i).getName(false, false, false);
				if(name.length() >= kitname.length() && name.substring(0, kitname.length()).equalsIgnoreCase(kitname))
					kitNames.add(name);
			}
			
			return kitNames;
		}
		
		return null;
	}

}
