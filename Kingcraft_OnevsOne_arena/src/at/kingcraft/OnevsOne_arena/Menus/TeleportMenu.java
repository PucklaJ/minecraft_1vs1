package at.kingcraft.OnevsOne_arena.Menus;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import at.kingcraft.OnevsOne_arena.MainClass;
import at.kingcraft.OnevsOne_arena.Duels.Duel;
import at.kingcraft.OnevsOne_arena.Duels.DuelManager;
import at.kingcraft.OnevsOne_arena.Messaging.Messenger;
import at.kingcraft.OnevsOne_arena.Tournaments.Round;
import at.kingcraft.OnevsOne_arena.Tournaments.TournamentManager;
import net.md_5.bungee.api.ChatColor;

public class TeleportMenu extends Menu {

	private ArrayList<Round> rounds;
	private static final int MAX_ROUNDS = 45;
	private static final int BACK_POS = 49;
	
	public TeleportMenu(Player owner, DuelsMenu parent) {
		super(owner, 54, "Duelle", parent);
	}
	
	public void setRounds(ArrayList<Round> rounds)
	{
		this.rounds = rounds;
	}
	
	private ItemStack roundToItemStack(Round r)
	{
		ItemStack round = new ItemStack(Material.GOLD_BLOCK);
		ItemMeta im = round.getItemMeta();
		im.setDisplayName(DuelsMenu.toString(r.getP1(), ChatColor.YELLOW) + ChatColor.WHITE + " vs. " + DuelsMenu.toString(r.getP2(), ChatColor.YELLOW));
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "Map: " + r.getArena());
		lore.add(ChatColor.GRAY + "Server: " + r.getServer());
		im.setLore(lore);
		round.setItemMeta(im);
		
		return round;
	}
	
	private void update()
	{
		for(int i = 0;i<MAX_ROUNDS;i++)
		{
			if(!(inventory.getItem(i) == null || inventory.getItem(i).getType().equals(Material.AIR)))
			{
				inventory.setItem(i, new ItemStack(Material.AIR));
			}
		}
		
		for(int i = 0;i<rounds.size() && i<MAX_ROUNDS;i++)
		{
			if(!rounds.get(i).getLoser().equals("NO_LOSER"))
				continue;
			
			inventory.setItem(i, roundToItemStack(rounds.get(i)));
		}
	}
	
	@Override
	protected void setInventoryContents()
	{
		ItemStack back = new ItemStack(Material.SPRUCE_DOOR_ITEM);
		{
			ItemMeta im = back.getItemMeta();
			im.setDisplayName("Zurueck");
			back.setItemMeta(im);
		}
		
		inventory.setItem(BACK_POS, back);
	}
	
	@Override
	public void open()
	{
		update();
		super.open();
	}
	
	private void teleportToServer(String server)
	{
		TournamentManager.leftBySpectator.add(owner.getUniqueId());
		Messenger.sendMessage(owner, "BungeeCord", "Connect", server);
	}
	
	private String getServerFromItemStack(ItemStack is)
	{
		return is.getItemMeta().getLore().get(1).replaceAll(ChatColor.GRAY + "Server: ", "");
	}
	
	@Override
	public void onClick(int slot,ClickType ct)
	{
		if(ct.isLeftClick())
		{
			if(slot == BACK_POS)
			{
				close();
			}
			else if(slot < MAX_ROUNDS)
			{
				if(!(inventory.getItem(slot) == null || inventory.getItem(slot).getType().equals(Material.AIR)))
				{
					String server = getServerFromItemStack(inventory.getItem(slot));
					if(server.equals(MainClass.getInstance().serverName))
					{
						Duel d = DuelManager.getFirstDuel();
						if(d != null)
						{
							owner.teleport(d.getMap().getMid());
						}
					}
					else
					{
						teleportToServer(server);
					}
				}
			}
		}
	}

}
