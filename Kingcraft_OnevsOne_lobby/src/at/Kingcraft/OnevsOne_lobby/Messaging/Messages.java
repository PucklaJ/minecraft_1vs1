package at.Kingcraft.OnevsOne_lobby.Messaging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import at.Kingcraft.OnevsOne_lobby.MainClass;
import net.md_5.bungee.api.ChatColor;

public class Messages
{
	private static FileConfiguration config;
	private static File file;
	public static String minKitSet;
	public static String maxKitSet;
	public static String teamRecreate;
	public static String onlyLeaderChallenges;
	private static String isNotOnline;
	public static String notSelf;
	public static String challAlreadyExists;
	private static String isYourTeam;
	private static String otherTookChall;
	public static String tookChall;
	public static String noFreeArena;
	public static String onlyLeaderRefuse;
	private static String noChallenge;
	public static String noTeam;
	public static String onlyLeaderKick;
	private static String isNotYourTeam;
	public static String notSelfKick;
	public static String youTeamLeave;
	public static String onlyLeaderEnquierySend;
	public static String enquieryAlreadyExists;
	private static String enquierySend;
	private static String gotEnquiery;
	public static String enquieryRefuseSender;
	private static String enquieryRefuseSenderOther;
	public static String enquieryRefuseReciever;
	private static String enquieryRefuseRecieverOther;
	private static String noEnquiery;
	public static String onlyLeader;
	public static String noTournament;
	public static String onlyLeaderTourStart;
	public static String alreadyTourStart;
	public static String notEnoughArenas;
	public static String tooLessContestants;
	private static String noTournamentOther;
	private static String noTournamentID;
	private static String noRightID;
	public static String onlyLeaderWaitingSnake;
	private static String sendChallenge;
	private static String gotChallenge;
	public static String challengeRefuseSender;
	private static String challengeRefuseSenderOther;
	public static String challengeRefuseReciever;
	private static String challengeLeave;
	private static String challengeJoin;
	private static String teamJoin;
	public static String waitingSnakeJoin;
	public static String teamKicked;
	private static String teamKickOther;
	private static String teamLeaveOther;
	public static String teamDeleteAmount;
	public static String newTeamLeader;
	private static String teamJoinYou;
	public static String teamDelete;
	private static String kitPlaceJoin;
	public static String kitPlaceLeave;
	public static String quickJoinCancelled;
	public static String needTeam;
	public static String isYourTournament;
	private static String tournamentJoinOther;
	private static String tournamentJoin;
	public static String tournamentLeave;
	private static String tournamentLeaveOther;
	public static String newTournamentLeader;
	public static String startCancelled;
	public static String skipRound;
	public static String tournamentStarts;
	private static String tournamentStartsTimer;
	public static String alreadyTournament;
	public static String tournamentCreated;
	public static String tournamentIsFull;
	public static String tournamentDelete;
	public static String waitingSnakeLeave;
	private static String yourKit;
	private static String noKitFound;
	public static String noKitCommandInKitPlace;
	public static String youAreAlreadyLeader;
	private static String isNewLeader;
	public static String youCanNotUseThisItem;
	private static String noPlayerFoundWithName;
	private static String kitOfPlayer;
	private static String playerHasNoKitsWithName;
	private static String playerDoesntExists;
	private static String playerDoesntHaveKits;
	public static String thereArentAnyMaps;
	public static String youDidntGetAnyChallenges;
	public static String youAreGettingTeleported;
	private static String playerDoesntFight;
	public static String yourStatistics;
	private static String statisticsOf;
	public static String tournamentHasntBeenStarted;
	public static String tournamentStartHasBeenCancelled;
	private static String isNoNumber;
	private static String stats;
	public static String tournamentMaxPlayersHasBeenReached;
	private static String tournamentYouMustHaveTeamSize;
	public static String tournamentNoPermissionCreate;
	public static String tournamentNoPermissionJoin;
	private static String tournamentOtherCreate;
	private static String chatFromArena;
	public static String reallyStatsReset;
	private static String reallyStatsResetOther;
	public static String statsReset;
	private static String statsResetOther;
	public static String statsNotReset;
	private static String statsNotResetOther;
	public static String statsWrongInput;
	private static String kitSetting;
	public static String kitSettingSeparator;
	public static String noPermissionStatsView;
	public static String noPermissionStatsReset;
	public static String noPermissionStatsResetOther;
	public static String addToRankedQueue;
	public static String removeFromRankedQueue;
	private static String kitCommandInKitPlace;
	private static String forceQueueAdd;
	public static String forceQueueRemove;
	public static String forceQueueSelf;
	
	private static void addDefaults()
	{
		minKitSet = ChatColor.YELLOW + "MinKit gesetzt";
		maxKitSet = ChatColor.YELLOW + "MaxKit gesetzt";
		teamRecreate = ChatColor.RED + "Dein Team wird gerade neu erstellt! Bitte warten..";
		onlyLeaderChallenges = ChatColor.RED + "Nur der Leiter kann Herausforderungen annehmen/senden";
		isNotOnline = ChatColor.GREEN + "%player%" + ChatColor.RED + " ist nicht online";
		notSelf = ChatColor.RED + "Du kannst dir selbst keine Herausforderung schicken";
		challAlreadyExists = ChatColor.RED + "Challenge existiert bereits";
		isYourTeam = ChatColor.GREEN + "%player%" + ChatColor.RED + " gehört zu deinem Team";
		otherTookChall = ChatColor.GREEN + "%player%" + ChatColor.YELLOW + " hat angenommen";
		tookChall = ChatColor.YELLOW + "Challenge angenommen";
		noFreeArena = ChatColor.RED + "Es ist keine Arena frei";
		onlyLeaderRefuse = ChatColor.RED + "Nur der Leiter kann Herausforderungen ablehnen";
		noChallenge = ChatColor.RED + "Du bist in keiner Herausforderung mit " + ChatColor.GREEN + "%player%";
		noTeam = ChatColor.RED + "Du bist in keinem Team";
		onlyLeaderKick = ChatColor.RED + "Nur der Leiter kann Spieler kicken";
		isNotYourTeam = ChatColor.GREEN + "%player%" + ChatColor.RED + " ist nicht Teil deines Teams";
		notSelfKick = ChatColor.RED + "Du kannst dich nicht selbst kicken";
		youTeamLeave = ChatColor.YELLOW + "Du hast das Team verlassen";
		onlyLeaderEnquierySend = ChatColor.RED + "Nur der Leiter kann Anfragen versenden";
		enquieryAlreadyExists = ChatColor.RED + "Anfrage existiert bereits";
		enquierySend = ChatColor.YELLOW + "Team Anfrage an " + ChatColor.GREEN + "%player%" + ChatColor.YELLOW + " gesendet";
		gotEnquiery = ChatColor.GREEN + "%player%" + ChatColor.YELLOW + " hat dir eine Team Anfrage gesendet";
		enquieryRefuseSender = ChatColor.YELLOW + "Team Anfrage zurückgezogen";
		enquieryRefuseSenderOther = ChatColor.GREEN + "%player%" + " hat die Team Anfrage zurückgezogen";
		enquieryRefuseReciever = ChatColor.YELLOW + "Team Anfrage abgelehnt";
		enquieryRefuseRecieverOther = ChatColor.GREEN + "%player%" + " hat die Team Anfrage abgelehnt";
		noEnquiery = ChatColor.YELLOW + "Du hast keine Team Anfrage mit " + ChatColor.GREEN + "%player%";
		onlyLeader = ChatColor.RED + "Das kann nur der Leiter";
		noTournament = ChatColor.RED + "Du bist in keinem Turnier";
		onlyLeaderTourStart = ChatColor.RED + "Nur der Leiter kann das Turnier starten";
		alreadyTourStart = ChatColor.RED + "Du hast das Turnier schon gestartet";
		notEnoughArenas = ChatColor.RED + "Es sind nicht genug Arenen frei";
		tooLessContestants = ChatColor.RED + "Das Turnier hat zu wenige Teilnehmer";
		noTournamentOther = ChatColor.GREEN + "%player%" + ChatColor.RED + " ist in keinem Turnier";
		noTournamentID = ChatColor.RED + "Es gibt kein Turnier mit der ID " + ChatColor.BLUE + "%ID%";
		noRightID = ChatColor.BLUE + "%ID%" + ChatColor.RED + " ist keine passende ID";
		onlyLeaderWaitingSnake = ChatColor.RED + "Nur der Leiter kann die Warteschlange benutzen";
		sendChallenge = ChatColor.YELLOW + "Challenge an " + ChatColor.GREEN + "%player%" + ChatColor.YELLOW + " gesendet (Kit: " + ChatColor.BLUE + "%kit%" + ChatColor.YELLOW  + ")";
		gotChallenge = ChatColor.GREEN + "%player%" + ChatColor.YELLOW + " hat dir eine Challenge geschickt (Kit: " + ChatColor.BLUE + "%kit%" + ChatColor.YELLOW + ")";
		challengeRefuseSender = ChatColor.YELLOW + "Challenge zurueckgezogen";
		challengeRefuseSenderOther = ChatColor.GREEN + "%player%" + ChatColor.YELLOW + " hat die Challenge abgelehnt";
		challengeRefuseReciever = ChatColor.YELLOW + "Herausforderung abgelehnt";
		challengeLeave = ChatColor.GREEN + "%player%" + ChatColor.YELLOW + " hat die Herausforderung verlassen";
		challengeJoin = ChatColor.GREEN + "%player%" + ChatColor.YELLOW + " ist hinzu gestoßen";
		teamJoin = ChatColor.GREEN + "%player%" + ChatColor.YELLOW + " ist dem Team beigetreten";
		waitingSnakeJoin = ChatColor.YELLOW + "Zu Warteschlange hinzugefügt";
		teamKicked = ChatColor.YELLOW + "Du wurdest aus dem Team geworfen";
		teamKickOther = ChatColor.GREEN + "%player%" + ChatColor.YELLOW + " wurde aus dem Team geworfen";
		teamLeaveOther = ChatColor.GREEN + "%player%" + ChatColor.YELLOW + " hat das Team verlassen";
		teamDeleteAmount = ChatColor.RED + "Team wird aufgrund der Spieleranzahl aufgelöst";
		newTeamLeader = ChatColor.YELLOW + "Du bist jetzt der neue Teamleiter";
		teamJoinYou = ChatColor.YELLOW + "Du bist dem Team von " + ChatColor.GREEN + "%player%" + ChatColor.YELLOW + " beigetreten";
		teamDelete = ChatColor.YELLOW + "Das Team wurde aufgelöst";
		kitPlaceJoin = ChatColor.YELLOW + "Du kannst jetzt deine Kits einstellen; Kit: " + ChatColor.BLUE + "%kit%";
		kitPlaceLeave = ChatColor.YELLOW + "Du hast den Kit-Ort verlassen";
		quickJoinCancelled = ChatColor.YELLOW + "Schnell-Beitritt abgebrochen";
		needTeam = ChatColor.RED + "Du musst in einem Team sein";
		isYourTournament = ChatColor.RED + "Du bist bereits Teil dieses Turniers";
		tournamentJoinOther = ChatColor.GREEN + "%player%" + ChatColor.YELLOW + " hat das Turnier betreten";
		tournamentJoin = ChatColor.YELLOW + "Du hast das Turnier von " + ChatColor.GREEN + "%player%" + ChatColor.YELLOW + " betreten";
		tournamentLeave = ChatColor.YELLOW + "Du hast das Turnier verlassen";
		tournamentLeaveOther = ChatColor.GREEN + "%player%" + ChatColor.YELLOW + " hat das Turnier verlassen";
		newTournamentLeader = ChatColor.YELLOW + "Du bist jetzt der neue Turnierleiter";
		startCancelled = ChatColor.RED + "Start wurde abgebrochen";
		skipRound = ChatColor.YELLOW + "Du überspringst eine Runde";
		tournamentStarts = ChatColor.YELLOW + "Turnier startet!";
		tournamentStartsTimer = ChatColor.YELLOW + "Turnier startet in " + ChatColor.BLUE + "%seconds%" + ChatColor.YELLOW + " Sekunden";
		alreadyTournament = ChatColor.RED + "Du bist schon in einem Turnier";
		tournamentCreated = ChatColor.YELLOW + "Turnier erstellt";
		tournamentIsFull = ChatColor.RED + "Das Turnier ist voll";
		tournamentDelete = ChatColor.YELLOW + "Turnier wurde aufgelöst";
		waitingSnakeLeave = ChatColor.YELLOW + "Von Warteschlange entfernt";
		yourKit = ChatColor.YELLOW + "Dein Kit: " + ChatColor.BLUE + "%name%\n" + ChatColor.YELLOW + "Einstellungen: %settings%";
		noKitFound = ChatColor.RED + "Kein Kit mit dem Namen " + ChatColor.BLUE + "%name% " + ChatColor.RED + " gefunden";
		noKitCommandInKitPlace = ChatColor.RED + "Du kannst diesen Command hier nicht ausführen";
		youAreAlreadyLeader = ChatColor.RED + "Du bist schon der Leiter";
		isNewLeader = ChatColor.GREEN + "%player% " + ChatColor.YELLOW + "ist der neue Team-Leiter";
		youCanNotUseThisItem = ChatColor.RED + "Du darfst dieses Item nicht verwenden verwenden";
		noPlayerFoundWithName = ChatColor.RED + "Keinen Spieler und kein Kit gefunden mit dem Namen " + ChatColor.BLUE + "%player%";
		kitOfPlayer = ChatColor.YELLOW + "Kit von " + ChatColor.GREEN + "%player%";
		playerHasNoKitsWithName = ChatColor.GREEN + "%player%" + ChatColor.RED + " hat keine Kits mit dem Namen " + ChatColor.BLUE + "%kit%";
		playerDoesntExists = ChatColor.RED + "Der Spieler " + ChatColor.BLUE + "%player%" + ChatColor.RED + " existiert nicht";
		playerDoesntHaveKits = ChatColor.GREEN + "%player%" + ChatColor.RED + " hat keine Kits";
		thereArentAnyMaps = ChatColor.RED + "Es gibt zur Zeit keine Maps";
		youDidntGetAnyChallenges = ChatColor.RED + "Du hast keine Herausforderung bekommen";
		youAreGettingTeleported = ChatColor.YELLOW + "Du wirst zum Kampf teleportiert";
		playerDoesntFight = ChatColor.RED + "Der Spieler " +  ChatColor.GREEN + "%player%" + ChatColor.RED +  " kämpft gerade nicht";
		yourStatistics = ChatColor.YELLOW + "--- " + ChatColor.GREEN +  "Deine Statistiken" + ChatColor.YELLOW + " ---";
		statisticsOf = ChatColor.YELLOW + "Statistiken von " + ChatColor.GREEN + "%player%";
		tournamentHasntBeenStarted = ChatColor.RED + "Das Turnier wurde nicht gestartet";
		tournamentStartHasBeenCancelled = ChatColor.YELLOW + "Der Start wurde abgebrochen";
		isNoNumber = ChatColor.BLUE + "%number%" + ChatColor.RED + " is keine Zahl";
		stats = ChatColor.YELLOW + "Kills:       " + ChatColor.BLUE + "%kills%" + "\n" + ChatColor.YELLOW + "Tode:        " + ChatColor.BLUE + "%deaths%" + "\n" + ChatColor.YELLOW + "K/T:         " + ChatColor.BLUE + "%kd%" + "\n" + ChatColor.YELLOW + "Spiele:      " + ChatColor.BLUE + "%plays%" + "\n" + ChatColor.YELLOW + "Siege:       " + ChatColor.BLUE + "%wins%" + "\n" + ChatColor.YELLOW + "Niederlagen: " + ChatColor.BLUE + "%loses%" + "\n" + ChatColor.YELLOW + "Wahrscheinlichkeit: " + ChatColor.BLUE + "%winsperplays%" + "\n" + ChatColor.YELLOW + "Gewonnene Turniere: " + ChatColor.BLUE + "%tournaments%";
		tournamentMaxPlayersHasBeenReached = ChatColor.YELLOW + "Die maximale Spieleranzahl wurde erreicht";
		tournamentYouMustHaveTeamSize = ChatColor.RED + "Du musst eine Teamgröße von " + ChatColor.BLUE + "%teamsize%" + ChatColor.RED + " haben";
		tournamentNoPermissionCreate = ChatColor.RED + "Du hast kein Recht ein Turnier zu erstellen";
		tournamentNoPermissionJoin = ChatColor.RED + "Du hast kein Recht einem Turnier beizutreten";
		tournamentOtherCreate = ChatColor.GREEN + "%player% " + ChatColor.YELLOW + "hat ein Turnier (%size%vs%size%) erstellt";
		chatFromArena = "%player%: " + ChatColor.WHITE + "%message%";
		reallyStatsReset = ChatColor.RED + "Willst du wirklich deine Statistiken zurücksetzten? (ja/nein)";
		statsReset = ChatColor.YELLOW + "Deine Statistiken wurden zurückgesetzt";
		statsNotReset = ChatColor.YELLOW + "Deine Statsitiken wurden nicht zurückgesetzt";
		reallyStatsResetOther = ChatColor.RED + "Willst du wirklich die Statistiken von " + ChatColor.GREEN + "%player% " + ChatColor.RED + "zurücksetzten? (ja/nein)";
		statsResetOther = ChatColor.YELLOW + "Die Statistiken von" + ChatColor.GREEN + "%player% " + ChatColor.YELLOW + "wurden zurückgesetzt";
		statsNotResetOther = ChatColor.YELLOW + "Die Statsitiken von" + ChatColor.GREEN  + "%player%" + ChatColor.YELLOW + " wurden nicht zurückgesetzt";
		statsWrongInput = ChatColor.RED + "Du musst ja oder nein eingeben";
		kitSetting = ChatColor.GOLD + "%setting%";
		kitSettingSeparator = ChatColor.GRAY + "; ";
		noPermissionStatsView = ChatColor.RED + "Du hast keine Rechte Statistiken anzuschauen";
		noPermissionStatsReset = ChatColor.RED + "Du hast keine Rechte deine Statistiken zurückzusetzen";
		noPermissionStatsResetOther = ChatColor.RED + "Du hast keine Rechte die Statistiken von jemand anderem zurückzusetzen";
		addToRankedQueue = ChatColor.YELLOW + "Du wurdest zur Ranked Warteschlange hinzugefügt";
		removeFromRankedQueue = ChatColor.YELLOW + "Du wurdest von der Ranked Warteschlange entfernt";
		kitCommandInKitPlace = ChatColor.YELLOW + "Das Kit " + ChatColor.BLUE + "%kit% " + ChatColor.YELLOW + "wird als Vorlage verwendet";
		forceQueueAdd = ChatColor.YELLOW + "You are forecqueueing " + ChatColor.GREEN + "%player%";
		forceQueueRemove = ChatColor.YELLOW + "You have been deleted from the forcequeue";
		forceQueueSelf = ChatColor.RED + "You cannot forcequeue yourself";
		
		config.addDefault("min-kit-set", minKitSet);
		config.addDefault("max-kit-set", maxKitSet);
		config.addDefault("team-recreate", teamRecreate);
		config.addDefault("only-leader-challenges", onlyLeaderChallenges);
		config.addDefault("is-not-online", isNotOnline);
		config.addDefault("not-send-self", notSelf);
		config.addDefault("challenge-already-exists", challAlreadyExists);
		config.addDefault("is-your-team", isYourTeam);
		config.addDefault("other-took-challenge", otherTookChall);
		config.addDefault("took-challenge", tookChall);
		config.addDefault("no-free-arena", noFreeArena);
		config.addDefault("only-leader-refuse", onlyLeaderRefuse);
		config.addDefault("no-challenge", noChallenge);
		config.addDefault("no-team", noTeam);
		config.addDefault("only-leader-kick", onlyLeaderKick);
		config.addDefault("is-not-your-team", isNotYourTeam);
		config.addDefault("not-self-kick", notSelfKick);
		config.addDefault("you-team-leave", youTeamLeave);
		config.addDefault("only-leader-enquiery-send", onlyLeaderEnquierySend);
		config.addDefault("enquiery-already-exists", enquieryAlreadyExists);
		config.addDefault("enquiery-send", enquierySend);
		config.addDefault("got-enquiery", gotEnquiery);
		config.addDefault("enquiery-refuse-sender", enquieryRefuseSender);
		config.addDefault("enquiery-refuse-sender-other", enquieryRefuseSenderOther);
		config.addDefault("enquiery-refuse-reciever", enquieryRefuseReciever);
		config.addDefault("enquiery-refuse-reciever-other", enquieryRefuseRecieverOther);
		config.addDefault("no-enquiery", noEnquiery);
		config.addDefault("only-leader", onlyLeader);
		config.addDefault("no-tournament", noTournament);
		config.addDefault("only-leader-tournament-start", onlyLeaderTourStart);
		config.addDefault("already-tournament-start", alreadyTourStart);
		config.addDefault("not-enough-arenas", notEnoughArenas);
		config.addDefault("too-less-contestants", tooLessContestants);
		config.addDefault("no-tournament-other", noTournamentOther);
		config.addDefault("no-tournament-id", noTournamentID);
		config.addDefault("no-right-id", noRightID);
		config.addDefault("only-leader-waitingsnake", onlyLeaderWaitingSnake);
		config.addDefault("send-challenge", sendChallenge);
		config.addDefault("got-challenge", gotChallenge);
		config.addDefault("challenge-refuse-sender", challengeRefuseSender);
		config.addDefault("challenge-refuse-sender-other", challengeRefuseSenderOther);
		config.addDefault("challenge-refuse-reciever", challengeRefuseReciever);
		config.addDefault("challenge-leave", challengeLeave);
		config.addDefault("challenge-join", challengeJoin);
		config.addDefault("team-join", teamJoin);
		config.addDefault("waitingsnake-join", waitingSnakeJoin);
		config.addDefault("team-kicked", teamKicked);
		config.addDefault("team-kick-other", teamKickOther);
		config.addDefault("team-leave-other", teamLeaveOther);
		config.addDefault("team-delete-amount", teamDeleteAmount);
		config.addDefault("new-team-leader", newTeamLeader);
		config.addDefault("team-join-you", teamJoinYou);
		config.addDefault("team-delete", teamDelete);
		config.addDefault("kit-place-join", kitPlaceJoin);
		config.addDefault("kit-place-leave", kitPlaceLeave);
		config.addDefault("quick-join-cancelled", quickJoinCancelled);
		config.addDefault("need-team", needTeam);
		config.addDefault("is-your-tournament", isYourTournament);
		config.addDefault("tournament-join-other", tournamentJoinOther);
		config.addDefault("tournament-join", tournamentJoin);
		config.addDefault("tournament-leave", tournamentLeave);
		config.addDefault("tournament-leave-other", tournamentLeaveOther);
		config.addDefault("new-tournament-leader", newTournamentLeader);
		config.addDefault("start-cancelled", startCancelled);
		config.addDefault("skip-round", skipRound);
		config.addDefault("tournament-starts", tournamentStarts);
		config.addDefault("tournament-starts-timer", tournamentStartsTimer);
		config.addDefault("already-tournament", alreadyTournament);
		config.addDefault("tournament-created", tournamentCreated);
		config.addDefault("tournament-is-full", tournamentIsFull);
		config.addDefault("tournament-delete", tournamentDelete);
		config.addDefault("waitingsnake-leave", waitingSnakeLeave);
		config.addDefault("your-kit", yourKit);
		config.addDefault("no-kit-found", noKitFound);
		config.addDefault("no-kit-command-in-kit-place", noKitCommandInKitPlace);
		config.addDefault("you-are-already-leader", youAreAlreadyLeader);
		config.addDefault("is-new-leader", isNewLeader);
		config.addDefault("you-can-not-use-this-item", youCanNotUseThisItem);
		config.addDefault("no-player-found-with-name", noPlayerFoundWithName);
		config.addDefault("kit-of-player", kitOfPlayer);
		config.addDefault("player-has-no-kits-with-name", playerHasNoKitsWithName);
		config.addDefault("player-doesnt-exists", playerDoesntExists);
		config.addDefault("player-doesnt-have-kits", playerDoesntHaveKits);
		config.addDefault("there-arent-any-maps", thereArentAnyMaps);
		config.addDefault("you-didnt-get-any-challenges", youDidntGetAnyChallenges);
		config.addDefault("you-are-getting-teleported", youAreGettingTeleported);
		config.addDefault("player-doesnt-fight", playerDoesntFight);
		config.addDefault("your-statistics", yourStatistics);
		config.addDefault("statistics-of", statisticsOf);
		config.addDefault("tournament-hasnt-been-started", tournamentHasntBeenStarted);
		config.addDefault("tournament-start-has-been-cancelled", tournamentStartHasBeenCancelled);
		config.addDefault("is-no-number", isNoNumber);
		config.addDefault("stats", stats);
		config.addDefault("tournament-max-players-has-been-reached", tournamentMaxPlayersHasBeenReached);
		config.addDefault("tournament-you-must-have-team-size", tournamentYouMustHaveTeamSize);
		config.addDefault("tournament-no-permission-create", tournamentNoPermissionCreate);
		config.addDefault("tournament-no-permission-join", tournamentNoPermissionJoin);
		config.addDefault("tournament-other-create", tournamentOtherCreate);
		config.addDefault("chat-from-arena", chatFromArena);
		config.addDefault("really-stats-reset", reallyStatsReset);
		config.addDefault("stats-reset", statsReset);
		config.addDefault("stats-not-reset", statsNotReset);
		config.addDefault("really-stats-reset-other", reallyStatsResetOther);
		config.addDefault("stats-reset-other", statsResetOther);
		config.addDefault("stats-not-reset-other", statsNotResetOther);
		config.addDefault("stats-wrong-input", statsWrongInput);
		config.addDefault("kit-setting", kitSetting);
		config.addDefault("kit-setting-separator", kitSettingSeparator);
		config.addDefault("no-permission-stats-view", noPermissionStatsView);
		config.addDefault("no-permission-stats-reset", noPermissionStatsReset);
		config.addDefault("no-permission-stats-reset-other", noPermissionStatsResetOther);
		config.addDefault("add-to-ranked-queue", addToRankedQueue);
		config.addDefault("remove-from-ranked-queue", removeFromRankedQueue);
		config.addDefault("kit-command-in-kit-place", kitCommandInKitPlace);
		config.addDefault("forcequeue-add", forceQueueAdd);
		config.addDefault("forcequeue-remove", forceQueueRemove);
		config.addDefault("forcequeue-self", forceQueueSelf);
		
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
	
	public static String isNotOnline(String player)
	{
		return isNotOnline.replaceAll("%player%", player);
	}
	
	public static String isYourTeam(String player)
	{
		return isYourTeam.replaceAll("%player%", player);
	}
	
	public static String otherTookChall(String player)
	{
		return otherTookChall.replaceAll("%player%", player);
	}
	
	public static String noChallenge(String player)
	{
		return noChallenge.replaceAll("%player%", player);
	}
	
	public static String isNotYourTeam(String player)
	{
		return isNotYourTeam.replaceAll("%player%", player);
	}
	
	public static String enquierySend(String player)
	{
		return enquierySend.replaceAll("%player%", player);
	}
	
	public static String gotEnquiery(String player)
	{
		return gotEnquiery.replaceAll("%player%", player);
	}
	
	public static String enquieryRefuseSenderOther(String player)
	{
		return enquieryRefuseSenderOther.replaceAll("%player%", player);
	}
	
	public static String enquieryRefuseRecieverOther(String player)
	{
		return enquieryRefuseRecieverOther.replaceAll("%player%", player);
	}
	
	public static String noEnquiery(String player)
	{
		return noEnquiery.replaceAll("%player%", player);
	}
	
	public static String noTournamentOther(String player)
	{
		return noTournamentOther.replaceAll("%player%", player);
	}
	
	public static String noTournamentID(String id)
	{
		return noTournamentID.replaceAll("%ID%", id);
	}
	
	public static String noRightID(String id)
	{
		return noRightID.replaceAll("%ID%", id);
	}
	
	public static String sendChallenge(String player,String kit)
	{
		return sendChallenge.replaceAll("%player%", player).replaceAll("%kit%", kit);
	}
	
	public static String gotChallenge(String player,String kit)
	{
		return gotChallenge.replaceAll("%player%", player).replaceAll("%kit%", kit);
	}
	
	public static String challengeRefuseSenderOther(String player)
	{
		return challengeRefuseSenderOther.replaceAll("%player%", player);
	}
	
	public static String challengeLeave(String player)
	{
		return challengeLeave.replaceAll("%player%", player);
	}
	
	public static String challengeJoin(String player)
	{
		return challengeJoin.replaceAll("%player%", player);
	}
	
	public static String teamJoin(String player)
	{
		return teamJoin.replaceAll("%player%", player);
	}
	
	public static String teamKickOther(String player)
	{
		return teamKickOther.replaceAll("%player%", player);
	}
	
	public static String teamLeaveOther(String player)
	{
		return teamLeaveOther.replaceAll("%player%", player);
	}
	
	public static String teamJoinYou(String player)
	{
		return teamJoinYou.replaceAll("%player%", player);
	}
	
	public static String tournamentJoinOther(ArrayList<Player> player)
	{
		String plStr = "";
		
		for(int i = 0;i<player.size();i++)
		{
			plStr += player.get(i).getDisplayName() + (i+1==player.size() ? "" : ", ");
		}
		
		return tournamentJoinOther.replaceAll("%player%", plStr);
	}
	
	public static String tournamentJoin(String player)
	{
		return tournamentJoin.replaceAll("%player%", player);
	}
	
	public static String tournamentLeaveOther(ArrayList<Player> player)
	{
		String plStr = "";
		
		for(int i = 0;i<player.size();i++)
		{
			plStr += player.get(i).getDisplayName() + (i+1==player.size() ? "" : ", ");
		}
		
		return tournamentLeaveOther.replaceAll("%player%", plStr);
	}
	
	public static String tournamentStartsTimer(String seconds)
	{
		return tournamentStartsTimer.replaceAll("%seconds%", seconds);
	}
	
	public static String kitPlaceJoin(String kit)
	{
		return kitPlaceJoin.replaceAll("%kit%", kit);
	}
	
	public static String yourKit(String name,String settings)
	{
		return yourKit.replaceAll("%name%", name).replaceAll("%settings%", settings);
	}
	
	public static String noKitFound(String name)
	{
		return noKitFound.replaceAll("%name%", name);
	}
	
	public static String isNewLeader(String player)
	{
		return isNewLeader.replaceAll("%player%", player);
	}
	
	public static String noPlayerFoundWithName(String player)
	{
		return noPlayerFoundWithName.replaceAll("%player%", player);
	}
	
	public static String kitOfPlayer(String player)
	{
		return kitOfPlayer.replaceAll("%player%", player);
	}
	
	public static String playerHasNoKitsWithName(String player,String kit)
	{
		return playerHasNoKitsWithName.replaceAll("%player%", player).replaceAll("%kit%", kit);
	}
	
	public static String playerDoesntExists(String player)
	{
		return playerDoesntExists.replaceAll("%player%", player);
	}
	
	public static String playerDoesntHaveKits(String player)
	{
		return playerDoesntHaveKits.replaceAll("%player%", player);
	}
	
	public static String playerDoesntFight(String player)
	{
		return playerDoesntFight.replaceAll("%player%", player);
	}
	
	public static String statisticsOf(String player)
	{
		return statisticsOf.replaceAll("%player%", player);
	}
	
	public static String isNoNumber(String number)
	{
		return isNoNumber.replaceAll("%number%", number);
	}
	
	public static String stats(int kills,int deaths, double kd, int plays, int wins, int loses, double winsperplays, int tournamentWins,int elo)
	{
		return stats.replaceAll("%kills%", kills + "").replaceAll("%deaths%", deaths + "").replaceAll("%kd%", kd + "").replaceAll("%plays%", plays+"").replaceAll("%wins%", wins + "").replaceAll("%loses%", loses + "").replaceAll("%winsperplays%", winsperplays + "").replaceAll("%tournaments%", tournamentWins + "").replaceAll("%elo%",elo+"");
	}
	
	public static String tournamentYouMustHaveTeamSize(int TeamSize)
	{
		return tournamentYouMustHaveTeamSize.replaceAll("%teamsize%", TeamSize+"");
	}
	
	public static String tournamentOtherCreate(String player,int size)
	{
		return tournamentOtherCreate.replaceAll("%player%", player).replaceAll("%size%", size+"");
	}
	
	public static String chatFromArena(String player,String message)
	{
		return chatFromArena.replaceAll("%player%", player).replaceAll("%message%", message);
	}
	
	public static String kitSetting(String setting)
	{
		return kitSetting.replaceAll("%setting%", setting);
	}
	
	public static String reallyStatsResetOther(String player)
	{
		return reallyStatsResetOther.replaceAll("%player%",player);
	}
	public static String statsResetOther(String player)
	{
		return statsResetOther.replaceAll("%player%",player);
	}
	public static String statsNotResetOther(String player)
	{
		return statsNotResetOther.replaceAll("%player%",player);
	}
	public static String kitCommandInKitPlace(String kit)
	{
		return kitCommandInKitPlace.replaceAll("%kit%", kit);
	}
	
	public static String forceQueueAdd(String player)
	{
		return forceQueueAdd.replaceAll("%player%", player);
	}
	
	private static void setValues()
	{
		minKitSet = config.getString("min-kit-set");
		maxKitSet = config.getString("max-kit-set");
		teamRecreate = config.getString("team-recreate");
		onlyLeaderChallenges = config.getString("only-leader-challenges");
		isNotOnline = config.getString("is-not-online");
		notSelf = config.getString("not-send-self");
		challAlreadyExists = config.getString("challenge-already-exists");
		isYourTeam = config.getString("is-your-team");
		otherTookChall = config.getString("other-took-challenge");
		tookChall = config.getString("took-challenge");
		noFreeArena = config.getString("no-free-arena");
		onlyLeaderRefuse = config.getString("only-leader-refuse");
		noChallenge = config.getString("no-challenge");
		noTeam = config.getString("no-team");
		onlyLeaderKick = config.getString("only-leader-kick");
		isNotYourTeam = config.getString("is-not-your-team");
		notSelfKick = config.getString("not-self-kick");
		youTeamLeave = config.getString("you-team-leave");
		onlyLeaderEnquierySend = config.getString("only-leader-enquiery-send");
		enquieryAlreadyExists = config.getString("enquiery-already-exists");
		enquierySend = config.getString("enquiery-send");
		gotEnquiery = config.getString("got-enquiery");
		enquieryRefuseSender = config.getString("enquiery-refuse-sender");
		enquieryRefuseSenderOther = config.getString("enquiery-refuse-sender-other");
		enquieryRefuseReciever = config.getString("enquiery-refuse-reciever");
		enquieryRefuseRecieverOther = config.getString("enquiery-refuse-reciever-other");
		noEnquiery = config.getString("no-enquiery");
		onlyLeader = config.getString("only-leader");
		noTournament = config.getString("no-tournament");
		onlyLeaderTourStart = config.getString("only-leader-tournament-start");
		alreadyTourStart = config.getString("already-tournament-start");
		notEnoughArenas = config.getString("not-enough-arenas");
		tooLessContestants = config.getString("too-less-contestants");
		noTournamentOther = config.getString("no-tournament-other");
		noTournamentID = config.getString("no-tournament-id");
		noRightID = config.getString("no-right-id");
		onlyLeaderWaitingSnake = config.getString("only-leader-waitingsnake");
		sendChallenge = config.getString("send-challenge");
		gotChallenge = config.getString("got-challenge");
		challengeRefuseSender = config.getString("challenge-refuse-sender");
		challengeRefuseSenderOther = config.getString("challenge-refuse-sender-other");
		challengeRefuseReciever = config.getString("challenge-refuse-reciever");
		challengeLeave = config.getString("challenge-leave");
		challengeJoin = config.getString("challenge-join");
		teamJoin = config.getString("team-join");
		waitingSnakeJoin = config.getString("waitingsnake-join");
		teamKicked = config.getString("team-kicked");
		teamKickOther = config.getString("team-kick-other");
		teamLeaveOther = config.getString("team-leave-other");
		teamDeleteAmount = config.getString("team-delete-amount");
		newTeamLeader = config.getString("new-team-leader");
		teamJoinYou = config.getString("team-join-you");
		teamDelete = config.getString("team-delete");
		kitPlaceJoin = config.getString("kit-place-join");
		kitPlaceLeave = config.getString("kit-place-leave");
		quickJoinCancelled = config.getString("quick-join-cancelled");
		needTeam = config.getString("need-team");
		isYourTournament = config.getString("is-your-tournament");
		tournamentJoinOther = config.getString("tournament-join-other");
		tournamentJoin = config.getString("tournament-join");
		tournamentLeave = config.getString("tournament-leave");
		tournamentLeaveOther = config.getString("tournament-leave-other");
		newTournamentLeader = config.getString("new-tournament-leader");
		startCancelled = config.getString("start-cancelled");
		skipRound = config.getString("skip-round");
		tournamentStarts = config.getString("tournament-starts");
		tournamentStartsTimer = config.getString("tournament-starts-timer");
		alreadyTournament = config.getString("already-tournament");
		tournamentCreated = config.getString("tournament-created");
		tournamentIsFull = config.getString("tournament-is-full");
		tournamentDelete = config.getString("tournament-delete");
		waitingSnakeLeave = config.getString("waitingsnake-leave");
		yourKit = config.getString("your-kit");
		noKitFound = config.getString("no-kit-found");
		noKitCommandInKitPlace = config.getString("no-kit-command-in-kit-place");
		youAreAlreadyLeader = config.getString("you-are-already-leader");
		isNewLeader = config.getString("is-new-leader");
		youCanNotUseThisItem = config.getString("you-can-not-use-this-item");
		noPlayerFoundWithName = config.getString("no-player-found-with-name");
		kitOfPlayer = config.getString("kit-of-player");
		playerHasNoKitsWithName = config.getString("player-has-no-kits-with-name");
		playerDoesntExists = config.getString("player-doesnt-exists");
		playerDoesntHaveKits = config.getString("player-doesnt-have-kits");
		thereArentAnyMaps = config.getString("there-arent-any-maps");
		youDidntGetAnyChallenges = config.getString("you-didnt-get-any-challenges");
		youAreGettingTeleported = config.getString("you-are-getting-teleported");
		playerDoesntFight = config.getString("player-doesnt-fight");
		yourStatistics = config.getString("your-statistics");
		statisticsOf = config.getString("statistics-of");
		tournamentHasntBeenStarted = config.getString("tournament-hasnt-been-started");
		tournamentStartHasBeenCancelled = config.getString("tournament-start-has-been-cancelled");
		isNoNumber = config.getString("is-no-number");
		tournamentMaxPlayersHasBeenReached = config.getString("tournament-max-players-has-been-reached");
		tournamentYouMustHaveTeamSize = config.getString("tournament-you-must-have-team-size");
		tournamentNoPermissionCreate = config.getString("tournament-no-permission-create");
		tournamentNoPermissionJoin = config.getString("tournament-no-permission-join");
		tournamentOtherCreate = config.getString("tournament-other-create");
		reallyStatsReset = config.getString("really-stats-reset");
		statsReset = config.getString("stats-reset");
		statsNotReset = config.getString("stats-not-reset");
		reallyStatsResetOther = config.getString("really-stats-reset-other");
		statsResetOther = config.getString("stats-reset-other");
		statsNotResetOther = config.getString("stats-not-reset-other");
		statsWrongInput = config.getString("stats-wrong-input");
		kitSetting = config.getString("kit-setting");
		kitSettingSeparator = config.getString("kit-setting-separator");
		chatFromArena = config.getString("chat-from-arena");
		noPermissionStatsView = config.getString("no-permission-stats-view");
		noPermissionStatsReset = config.getString("no-permission-stats-reset");
		noPermissionStatsResetOther = config.getString("no-permission-stats-reset-other");
		addToRankedQueue = config.getString("add-to-ranked-queue");
		removeFromRankedQueue = config.getString("remove-from-ranked-queue");
		stats = config.getString("stats");
		kitCommandInKitPlace = config.getString("kit-command-in-kit-place");
		forceQueueAdd = config.getString("forcequeue-add");
		forceQueueRemove = config.getString("forcequeue-remove");
		forceQueueSelf = config.getString("forcequeue-self");
	}
	
	public static void setup()
	{
		file = new File("plugins/" + MainClass.getInstance().getName() + "/messages.yml");
		config = YamlConfiguration.loadConfiguration(file);
		
		
		addDefaults();
		setValues();
	}
}
