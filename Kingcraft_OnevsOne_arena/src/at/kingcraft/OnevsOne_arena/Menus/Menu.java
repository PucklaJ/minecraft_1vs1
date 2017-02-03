package at.kingcraft.OnevsOne_arena.Menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import at.kingcraft.OnevsOne_arena.MainClass;

public class Menu {
	protected Inventory inventory;
	protected Player owner;
	protected Menu parent;
	
	public Menu(Player owner,int size,String name,Menu parent)
	{
		this.owner = owner;
		this.parent = parent;
		inventory = Bukkit.createInventory(owner, size, name);
		
		setInventoryContents();
	}
	
	public Menu(Player owner,int size,String name,Menu parent,MainClass plugin)
	{
		this.owner = owner;
		this.parent = parent;
		inventory = Bukkit.createInventory(owner, size, name);
		
		setInventoryContents(plugin);
	}
	
	protected void setInventory(ItemStack is)
	{
		for(int i = 0;i<inventory.getSize();i++)
		{
			inventory.setItem(i, is);
		}
	}
	
	public Player getOwner()
	{
		return owner;
	}
	
	protected void setInventoryContents()
	{
		
	}
	
	protected void setInventoryContents(MainClass plugin)
	{
		
	}
	
	public void onClick(int slot,ClickType ct)
	{
		
	}
	
	public void open()
	{
		owner.openInventory(inventory);
	}
	
	public boolean isOpen()
	{
		return owner.getOpenInventory().getTopInventory().getName().equals(inventory.getName());
	}
	
	public void close()
	{
		if(parent != null)
		{
			parent.open();
		}
		else
		{	
				owner.closeInventory();
		}
	}
}
