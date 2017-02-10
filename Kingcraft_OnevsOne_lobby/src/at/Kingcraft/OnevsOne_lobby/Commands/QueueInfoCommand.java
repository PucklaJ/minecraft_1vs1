package at.Kingcraft.OnevsOne_lobby.Commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import at.Kingcraft.OnevsOne_lobby.Kits.Kit;
import at.Kingcraft.OnevsOne_lobby.Kits.KitManager;
import at.Kingcraft.OnevsOne_lobby.WaitingSnake.RankedUpload;
import at.Kingcraft.OnevsOne_lobby.WaitingSnake.Settings;
import at.Kingcraft.OnevsOne_lobby.WaitingSnake.WaitingSnakeUpload;
import net.md_5.bungee.api.ChatColor;

public class QueueInfoCommand implements CommandExecutor {

	private static final int NORMAL = 0,
							 RANKED = 1;
	
	private Object getUploads(int which)
	{
		if(which == NORMAL)
		{
			ArrayList<WaitingSnakeUpload> uploads = new ArrayList<>();
			
			try
			{
				PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM Duel_WaitingSnake_Players");
				ResultSet rs = ps.executeQuery();
				
				while(rs.next())
				{
					uploads.add(new WaitingSnakeUpload(UUID.fromString(rs.getString(1)), Settings.fromString(rs.getString(2)), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)));
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			
			return uploads;
		}
		else
		{
			ArrayList<RankedUpload> uploads = new ArrayList<>();
			
			try
			{
				PreparedStatement ps = MainClass.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM Duel_RankedQueue");
				ResultSet rs = ps.executeQuery();
				
				while(rs.next())
				{
					uploads.add(RankedUpload.fromString(rs.getString(2)));
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			
			return uploads;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl,String[] args)
	{
		if(args.length == 0)
		{
			if(cmd.getName().equals("queueinfo"))
			{
				ArrayList<WaitingSnakeUpload> uploads = (ArrayList<WaitingSnakeUpload>) getUploads(NORMAL);
				
				if(uploads.isEmpty())
				{
					sender.sendMessage(ChatColor.RED + "The queue is empty");
					return true;
				}
				
				sender.sendMessage(ChatColor.YELLOW + "--------- " + ChatColor.GREEN + "Normal " + ChatColor.YELLOW + "---------");
				
				for(int i = 0;i<uploads.size();i++)
				{
					Kit kit = KitManager.decodeMySQLKit(null, uploads.get(i).kit, false);
					sender.sendMessage(ChatColor.YELLOW + "" + (i+1) + ". Name: " + ChatColor.GREEN + uploads.get(i).name + ChatColor.YELLOW + ", Kit: " + ChatColor.BLUE + kit.getName(true, !kit.getOwnerName().equals("Server"), false) + ChatColor.YELLOW + ", Arena: " + ChatColor.BLUE + uploads.get(i).arena);
				}
			}
			else
			{
				ArrayList<RankedUpload> uploads = (ArrayList<RankedUpload>) getUploads(RANKED);
				
				if(uploads.isEmpty())
				{
					sender.sendMessage(ChatColor.RED + "The Rankedqueue is empty");
					return true;
				}
				
				sender.sendMessage(ChatColor.YELLOW + "--------- " + ChatColor.GREEN + "Ranked " + ChatColor.YELLOW + "---------");
				
				for(int i = 0;i<uploads.size();i++)
				{
					Kit kit = KitManager.getPreKit(uploads.get(i).kit+15);
					OfflinePlayer op = Bukkit.getOfflinePlayer(uploads.get(i).uuid);
					sender.sendMessage(ChatColor.YELLOW + "" + (i+1) + ". Name: " + ChatColor.GREEN + op.getName() + ChatColor.YELLOW + ", Kit: " + ChatColor.BLUE + kit.getName(true, !kit.getOwnerName().equals("Server"), false) + ChatColor.YELLOW + ", Arena: " + ChatColor.BLUE + uploads.get(i).map);
				}
			}
			
			return true;
		}
		
		return false;
	}

}
