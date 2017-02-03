package at.Kingcraft.OnevsOne_lobby.Kits;

import org.bukkit.inventory.ItemStack;

public class KitItem {

	private int slot;
	private ItemStack item;
	private boolean equipment;
	
	public KitItem(int slot,ItemStack item,boolean isEquipment)
	{
		this.slot = slot;
		this.item = item;
		this.equipment = isEquipment;
	}

	public int getSlot() {
		return slot;
	}

	public ItemStack getItem() {
		return item;
	}
	
	public boolean isEquipment()
	{
		return equipment;
	}

}
