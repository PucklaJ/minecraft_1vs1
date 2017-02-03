package at.Kingcraft.OnevsOne_lobby.Items;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import net.md_5.bungee.api.ChatColor;

public class Items
{
	private static FileConfiguration config;
	private static File file;
	
	public static String challengeItemMaterial;
	public static String challengeItemName;
	public static ArrayList<String> challengeItemLore;
	
	public static String ffaItemMaterial;
	public static String ffaItemName;
	public static ArrayList<String> ffaItemLore;
	
	public static String challengeSkullName;
	public static ArrayList<String> challengeSkullLore;
	
	public static String settingsItemMaterial;
	public static String settingsItemName;
	public static ArrayList<String> settingsItemLore;
	
	public static String kitSettingsItemMaterial;
	public static String kitSettingsItemName;
	public static ArrayList<String> kitSettingsItemLore;
	
	public static String mapSettingsItemMaterial;
	public static String mapSettingsItemName;
	public static ArrayList<String> mapSettingsItemLore;
	
	public static String waitingSnakeSettingsItemMaterial;
	public static String waitingSnakeSettingsItemName;
	public static ArrayList<String> waitingSnakeSettingsItemLore;
	
	public static String tournamentSettingsItemMaterial;
	public static String tournamentSettingsItemName;
	public static ArrayList<String> tournamentSettingsItemLore;
	
	public static String teamSettingsItemTeamMaterial;
	public static String teamSettingsItemNoTeamMaterial;
	public static String teamSettingsItemName;
	public static ArrayList<String> teamSettingsItemLore;
	
	public static String menuWallMaterial;
	public static short menuWallDurability;
	
	public static String menuBackMaterial;
	public static short menuBackDurability;
	public static String menuBackName;
	
	public static String spectateMaterial;
	public static short spectateDurability;
	public static String spectateName;
	public static ArrayList<String> spectateLore;
	
	public static void setup()
	{
		file = new File("plugins/" + MainClass.getInstance().getName() + "/items.yml");
		config = YamlConfiguration.loadConfiguration(file);
		
		
		addDefaults();
		setValues();
	}

	@SuppressWarnings("unchecked")
	private static void setValues()
	{
		challengeItemMaterial = setMaterial(config.getString("challenge-item-material"));
		challengeItemName = config.getString("challenge-item-name");
		challengeItemLore = (ArrayList<String>) config.get("challenge-item-lore");
		setArray(challengeItemLore);
		
		ffaItemMaterial = setMaterial(config.getString("ffa-item-material"));
		ffaItemName = config.getString("ffa-item-name");
		ffaItemLore = (ArrayList<String>) config.get("ffa-item-lore");
		setArray(ffaItemLore);
		
		challengeSkullName = config.getString("challenge-skull-name");
		challengeSkullLore = (ArrayList<String>) config.get("challenge-skull-lore");
		setArray(challengeSkullLore);
		
		settingsItemMaterial = setMaterial(config.getString("settings-item-material"));
		settingsItemName = config.getString("settings-item-name");
		settingsItemLore = (ArrayList<String>) config.get("settings-item-lore");
		setArray(settingsItemLore);
		
		kitSettingsItemMaterial = setMaterial(config.getString("kit-settings-item-material"));
		kitSettingsItemName = config.getString("kit-settings-item-name");
		kitSettingsItemLore = (ArrayList<String>) config.get("kit-settings-item-lore");
		setArray(kitSettingsItemLore);
		
		mapSettingsItemMaterial = setMaterial(config.getString("map-settings-item-material"));
		mapSettingsItemName = config.getString("map-settings-item-name");
		mapSettingsItemLore = (ArrayList<String>) config.get("map-settings-item-lore");
		setArray(mapSettingsItemLore);
		
		waitingSnakeSettingsItemMaterial = setMaterial(config.getString("waiting-snake-settings-item-material"));
		waitingSnakeSettingsItemName = config.getString("waiting-snake-settings-item-name");
		waitingSnakeSettingsItemLore = (ArrayList<String>) config.get("waiting-snake-settings-item-lore");
		setArray(waitingSnakeSettingsItemLore);
		
		tournamentSettingsItemMaterial = setMaterial(config.getString("tournament-settings-item-material"));
		tournamentSettingsItemName = config.getString("tournament-settings-item-name");
		tournamentSettingsItemLore = (ArrayList<String>) config.get("tournament-settings-item-lore");
		setArray(tournamentSettingsItemLore);
		
		teamSettingsItemTeamMaterial = setMaterial(config.getString("team-settings-item-team-material"));
		teamSettingsItemNoTeamMaterial = setMaterial(config.getString("team-settings-item-no-team-material"));
		teamSettingsItemName = config.getString("team-settings-item-name");
		teamSettingsItemLore = (ArrayList<String>) config.get("team-settings-item-lore");
		setArray(teamSettingsItemLore);
		
		menuWallMaterial = setMaterial(config.getString("menu-wall-material"));
		menuWallDurability = (short) config.getInt("menu-wall-durability");
		
		menuBackMaterial = setMaterial(config.getString("menu-back-material"));
		menuBackDurability = (short) config.getInt("menu-back-durability");
		menuBackName = config.getString("menu-back-name");
		
		spectateMaterial = setMaterial(config.getString("spectate-material"));
		spectateDurability = (short) config.getInt("spectate-durability");
		spectateName = config.getString("spectate-name");
		spectateLore = (ArrayList<String>) config.get("spectate-lore");
		setArray(spectateLore);
	}
	
	private static void setArray(ArrayList<String> ar)
	{
		if(!ar.isEmpty() && (ar.get(0) == null || ar.get(0).equals("")))
		{
			ar.clear();
		}
	}
	
	private static String setMaterial(String str)
	{
		Material mat = Material.getMaterial(str);
		if(mat == null)
		{
			str = "PAPER";
		}
		
		return str;
	}

	private static void addDefaults()
	{
		challengeItemMaterial = "DIAMOND_SWORD";
		challengeItemName = ChatColor.RED + "Gegner herausfordern";
		challengeItemLore = new ArrayList<>();
		challengeItemLore.add(ChatColor.YELLOW + "LINKS" + ChatColor.WHITE + "-Klick auf SPIELER: Herausfordern");
		challengeItemLore.add(ChatColor.YELLOW + "RECHTS" + ChatColor.WHITE + "-Klick auf SPIELER: Team-Anfrage");
		
		ffaItemMaterial = "APPLE";
		ffaItemName = ChatColor.YELLOW + "Party-FFA";
		ffaItemLore = new ArrayList<>();
		ffaItemLore.add(ChatColor.YELLOW + "RECHTS" + ChatColor.WHITE + "-Klick: Starte FFA mit Team");
		
		challengeSkullName = ChatColor.RED + "Herausforderungen";
		challengeSkullLore = new ArrayList<String>();
		challengeSkullLore.add(ChatColor.WHITE + "Zeigt alle " + ChatColor.YELLOW + " HERAUSFORDERUNGEN");
		
		settingsItemMaterial = "REDSTONE_COMPARATOR";
		settingsItemName = ChatColor.YELLOW + "Einstellungen";
		settingsItemLore = new ArrayList<>();
		settingsItemLore.add(ChatColor.YELLOW + "RECHTS" +ChatColor.WHITE + "-Klick: oeffnet die Einstellungen");
		
		kitSettingsItemMaterial = "WORKBENCH";
		kitSettingsItemName = ChatColor.YELLOW + "Kit-Einstellungen";
		kitSettingsItemLore = new ArrayList<String>();
		kitSettingsItemLore.add("");
		
		mapSettingsItemMaterial = "PAPER";
		mapSettingsItemName = ChatColor.YELLOW + "Map-Einstellungen";
		mapSettingsItemLore = new ArrayList<>();
		mapSettingsItemLore.add("");
		
		waitingSnakeSettingsItemMaterial = "GOLD_SWORD";
		waitingSnakeSettingsItemName = ChatColor.YELLOW + "Warteschlange-Einstellungen";
		waitingSnakeSettingsItemLore = new ArrayList<>();
		waitingSnakeSettingsItemLore.add("");
		
		tournamentSettingsItemMaterial = "EMERALD";
		tournamentSettingsItemName = ChatColor.YELLOW + "Turnier-Einstellungen";
		tournamentSettingsItemLore = new ArrayList<>();
		tournamentSettingsItemLore.add("");
		
		teamSettingsItemTeamMaterial = "STORAGE_MINECART";
		teamSettingsItemNoTeamMaterial = "MINECART";
		teamSettingsItemName = ChatColor.YELLOW + "Team-Einstellungen";
		teamSettingsItemLore = new ArrayList<>();
		teamSettingsItemLore.add("");
		
		menuWallMaterial = "STAINED_GLASS_PANE";
		menuWallDurability = 15;
		
		menuBackMaterial = "SPRUCE_DOOR_ITEM";
		menuBackDurability = 0;
		menuBackName = "Zurueck";
		
		spectateMaterial = "COMPASS";
		spectateDurability = 0;
		spectateName = ChatColor.YELLOW + "Spectaten";
		spectateLore = new ArrayList<>();
		
		config.addDefault("challenge-item-material", challengeItemMaterial);
		config.addDefault("challenge-item-name", challengeItemName);
		config.addDefault("challenge-item-lore", challengeItemLore);
		
		config.addDefault("ffa-item-material", ffaItemMaterial);
		config.addDefault("ffa-item-name", ffaItemName);
		config.addDefault("ffa-item-lore", ffaItemLore);
		
		config.addDefault("challenge-skull-name", challengeSkullName);
		config.addDefault("challenge-skull-lore", challengeSkullLore);
		
		config.addDefault("settings-item-material", settingsItemMaterial);
		config.addDefault("settings-item-name", settingsItemName);
		config.addDefault("settings-item-lore", settingsItemLore);
		
		config.addDefault("kit-settings-item-material", kitSettingsItemMaterial);
		config.addDefault("kit-settings-item-name", kitSettingsItemName);
		config.addDefault("kit-settings-item-lore", kitSettingsItemLore);
		
		config.addDefault("map-settings-item-material", mapSettingsItemMaterial);
		config.addDefault("map-settings-item-name", mapSettingsItemName);
		config.addDefault("map-settings-item-lore", mapSettingsItemLore);
		
		config.addDefault("waiting-snake-settings-item-material", waitingSnakeSettingsItemMaterial);
		config.addDefault("waiting-snake-settings-item-name", waitingSnakeSettingsItemName);
		config.addDefault("waiting-snake-settings-item-lore", waitingSnakeSettingsItemLore);
		
		config.addDefault("tournament-settings-item-material", tournamentSettingsItemMaterial);
		config.addDefault("tournament-settings-item-name", tournamentSettingsItemName);
		config.addDefault("tournament-settings-item-lore", tournamentSettingsItemLore);
		
		config.addDefault("team-settings-item-team-material", teamSettingsItemTeamMaterial);
		config.addDefault("team-settings-item-no-team-material", teamSettingsItemNoTeamMaterial);
		config.addDefault("team-settings-item-name", teamSettingsItemName);
		config.addDefault("team-settings-item-lore", teamSettingsItemLore);
		
		config.addDefault("menu-wall-material", menuWallMaterial);
		config.addDefault("menu-wall-durability", menuWallDurability);
		
		config.addDefault("menu-back-material", menuBackMaterial);
		config.addDefault("menu-back-durability", menuBackDurability);
		config.addDefault("menu-back-name", menuBackName);
		
		config.addDefault("spectate-material", spectateMaterial);
		config.addDefault("spectate-durability", spectateDurability);
		config.addDefault("spectate-name", spectateName);
		config.addDefault("spectate-lore", spectateLore);
		
		config.options().copyDefaults(true);
		
		try
		{
			config.save(file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
	}
	
}
