package at.Kingcraft.OnevsOne_lobby.Kits;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import net.md_5.bungee.api.ChatColor;

public enum KitSettings
{
	NO_FALL_DAMAGE (setSymbol(0),1 << 0,false,"Kein Fallschaden"),
	PLACE_BREAK_BLOCKS (setSymbol(1),1 << 1,true,"Blöcke (ab)bauen"),
	HUNGER (setSymbol(2),1 << 2,false,"Hunger"),
	REGENARATION(setSymbol(3),1 << 3,true,"Regeneration"),
	NO_EXPLOSION_DESTRUCTION(setSymbol(4),1 << 4,false,"Keine Zerstörung durch Explosionen"),
	INSTANT_TNT(setSymbol(5),1 << 5,false,"Sofort TNT"),
	NO_KNOCKBACK(setSymbol(6),1 << 6,false,"Kein Rückstoß"),
	FRIENDLY_FIRE(setSymbol(7),1 << 7,false,"Teamschaden"),
	NO_CRAFTING(setSymbol(8),1 << 8,false,"Kein Crafting"),
	DOUBLE_JUMP(setSymbol(9),1 << 9,false,"Doppelsprung"),
	SOUP(setSymbol(10),1 << 10,false,"Suppen-Heilung"),
	NO_SOUP_DROP(setSymbol(11),1 << 11,false,"Keine Suppen fallenlassen"),
	NO_HIT_DELAY(setSymbol(12),1 << 12,false,"Keine Schlagverzögerung"),
	NO_ARROW_COLLECT(setSymbol(13),1 << 13,false,"Keine Pfeile einsammeln"),
	MORE_PARTICLES(setSymbol(14),1 << 14,false,"Mehr Partikel");
	
	private ItemStack symbol;
	private final int BIT;
	private boolean defaultActive;
	private final String name;
	
	KitSettings(ItemStack is,int bit,boolean dA,String name)
	{
		symbol = is;
		BIT = bit;
		defaultActive = dA;
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ItemStack getSymbol()
	{
		return symbol;
	}
	
	public int getBit()
	{
		return BIT;
	}
	
	public boolean isDefaultActive()
	{
		return defaultActive;
	}
	
	private static ItemStack setSymbol(int i)
	{
		ItemStack is = new ItemStack(Material.PAPER);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("null");
		
		ArrayList<String> lore = new ArrayList<>();
		
		switch(i)
		{
			case 0:
				is.setType(Material.IRON_BOOTS);
				im.setDisplayName(ChatColor.YELLOW + "Kein Fallschaden");
				lore.add(ChatColor.WHITE + "Bestimmt, ob man Fallschaden");
				lore.add(ChatColor.WHITE + "bekommt oder nicht");
				im.setLore(lore);
				is.setItemMeta(im);
				break;
			case 1:
				is.setType(Material.GRASS);
				im.setDisplayName(ChatColor.YELLOW + "Blöcke abbauen/platzieren");
				lore.add(ChatColor.WHITE + "Bestimmt, ob man Blöcke");
				lore.add(ChatColor.WHITE + "abbauen und platzieren kann");
				im.setLore(lore);
				is.setItemMeta(im);
				break;
			case 2:
				is.setType(Material.COOKED_BEEF);
				im.setDisplayName(ChatColor.YELLOW + "Hunger");
				lore.add(ChatColor.WHITE + "Bestimmt, ob man wie normal");
				lore.add(ChatColor.WHITE + "Hunger verliert oder nicht");
				im.setLore(lore);
				is.setItemMeta(im);
				break;
			case 3:
				
				Potion potion = new Potion(PotionType.REGEN);
				ItemStack is1 = potion.toItemStack(1);
				im.setDisplayName(ChatColor.YELLOW + "Regeneration");
				lore.add(ChatColor.WHITE + "Bestimmt, ob Leben normal");
				lore.add(ChatColor.WHITE + "regeneriert wird oder nicht");
				im.setLore(lore);
				is1.setItemMeta(im);
				
				return is1;
			case 4:
				is.setType(Material.RECORD_11);
				im.setDisplayName(ChatColor.YELLOW + "Keine Blockzerstörung durch TNT");
				lore.add(ChatColor.WHITE + "Bestimmt, ob TNT die Arena");
				lore.add(ChatColor.WHITE + "zerstören kann oder nicht");
				im.setLore(lore);
				is.setItemMeta(im);
				break;
			case 5:
				is.setType(Material.TNT);
				im.setDisplayName(ChatColor.YELLOW + "TNT-Sofort-Explosion");
				lore.add(ChatColor.WHITE + "Bestimmt, ob TNT sofort nach");
				lore.add(ChatColor.WHITE + "dem Platzieren explodieren soll");
				im.setLore(lore);
				is.setItemMeta(im);
				break;
			case 6:
				is.setType(Material.STICK);
				is.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
				im.setDisplayName(ChatColor.YELLOW + "Kein Knockback");
				lore.add(ChatColor.WHITE + "Bestimmt, ob man bei einem");
				lore.add(ChatColor.WHITE + "zurückgeworfen wird oder nicht");
				im.setLore(lore);
				is.setItemMeta(im);
				break;
			case 7:
				is.setType(Material.BLAZE_POWDER);
				im.setDisplayName(ChatColor.YELLOW + "Friendly Fire");
				lore.add(ChatColor.WHITE + "Bestimmt, ob man Teammitglieder");
				lore.add(ChatColor.WHITE + "Schaden zufügen kann");
				im.setLore(lore);
				is.setItemMeta(im);
				break;
			case 8:
				is.setType(Material.WORKBENCH);
				im.setDisplayName(ChatColor.YELLOW + "Kein Crafting");
				lore.add(ChatColor.WHITE + "Bestimmt, ob man craften kann");
				im.setLore(lore);
				is.setItemMeta(im);
				break;
			case 9:
				is.setType(Material.FEATHER);
				im.setDisplayName(ChatColor.YELLOW + "Doppel Sprung");
				lore.add(ChatColor.WHITE + "Fügt einen Doppel-Sprung hinzu");
				im.setLore(lore);
				is.setItemMeta(im);
				break;
			case 10:
				is.setType(Material.MUSHROOM_SOUP);
				im.setDisplayName(ChatColor.YELLOW + "Suppen-Heilung");
				lore.add(ChatColor.WHITE + "Aktiviert Suppen-Heilung");
				im.setLore(lore);
				is.setItemMeta(im);
				break;
			case 11:
				is.setType(Material.BOWL);
				im.setDisplayName(ChatColor.YELLOW + "Keine Suppen fallenlassen");
				lore.add(ChatColor.WHITE + "Verhindert, dass man Suppen fallenlässt");
				im.setLore(lore);
				is.setItemMeta(im);
				break;
			case 12:
				is.setType(Material.QUARTZ);
				im.setDisplayName(ChatColor.YELLOW + "Keine Schlagverzögerung");
				lore.add(ChatColor.WHITE + "Schaltet die Schlagverzögerung aus");
				im.setLore(lore);
				is.setItemMeta(im);
				break;
			case 13:
				is.setType(Material.ARROW);
				im.setDisplayName(ChatColor.YELLOW + "Keine Pfeile aufsammeln");
				lore.add(ChatColor.WHITE + "Bestimmt, ob man Pfeile aufsammeln kann");
				im.setLore(lore);
				is.setItemMeta(im);
				break;
			case 14:
				is.setType(Material.ENCHANTED_BOOK);
				im.setDisplayName(ChatColor.YELLOW + "Mehr Partikel");
				lore.add(ChatColor.WHITE + "Spawnt mehr Partikel bei");
				lore.add(ChatColor.WHITE + "Schlägen, Krits und Pfeilen");
				im.setLore(lore);
				is.setItemMeta(im);
				break;
		}
		
		
		
		return is;
	}
}
