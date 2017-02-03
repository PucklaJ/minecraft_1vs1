package at.Kingcraft.OnevsOne_lobby.Duels;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import MySQL.MySQL;
import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Messaging.Messenger;
import net.md_5.bungee.api.ChatColor;

public class SpectateDuel
{
	private ArrayList<UUID> p1;
	private ArrayList<UUID> p2;
	private String server;
	
	public SpectateDuel(ArrayList<UUID> p1,ArrayList<UUID> p2,String server)
	{
		this.p1 = (p1);
		this.p2 = (p2);
		this.server = (server);
	}

	public ArrayList<UUID> getP1()
	{
		return p1;
	}

	public ArrayList<UUID> getP2()
	{
		return p2;
	}
	
	public String getServer()
	{
		return server;
	}
	
	public void teleportPlayer(Player p)
	{
		try
		{
			PreparedStatement ps = MySQL.getInstance().getConnection().prepareStatement("INSERT INTO Duel_JoinSpectators (UUID,HomeServer) VALUES (?,?)");
			ps.setString(1, p.getUniqueId().toString());
			ps.setString(2, MainClass.getInstance().serverName);
			
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		Messenger.sendMessage(p, "BungeeCord", "Connect", server);
	}
	
	public ItemStack toItemStack()
	{
		ItemStack is = new ItemStack(Material.GOLD_BLOCK);
		ItemMeta im = is.getItemMeta();
		
		String name = "";
		
		for(int i = 0;i<p1.size();i++)
		{
			name += ChatColor.GREEN + Bukkit.getOfflinePlayer(p1.get(i)).getName() + (i+1==p1.size() ? "" : ChatColor.WHITE + ", ");
		}
		
		if(!p2.isEmpty())
		{
			name += ChatColor.WHITE + " vs. ";
			
			for(int i = 0;i<p2.size();i++)
			{
				name += ChatColor.GREEN + Bukkit.getOfflinePlayer(p2.get(i)).getName() + (i+1==p2.size() ? "" : ChatColor.WHITE + ", ");
			}
		}
		
		
		im.setDisplayName(name);
		
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "Server: " + server);
		im.setLore(lore);
		
		is.setItemMeta(im);
		
		return is;
	}
}
