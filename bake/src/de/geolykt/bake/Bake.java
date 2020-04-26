package de.geolykt.bake;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.geolykt.bake.util.EnchantmentLib;
import de.geolykt.bake.util.Leaderboard;
import de.geolykt.bake.util.MeticsClass;
import de.geolykt.bake.util.StringUtils;
import de.geolykt.bake.util.BakeData.BakeData;
import de.geolykt.bake.util.BakeData.GlobalBake;
import de.geolykt.bake.util.BakeData.LocalBake;
import net.milkbowl.vault.economy.Economy;

/**
 * The main operating class
 * Almost all functions are called in here 
 * <ul><li>
 * 1.4.1: Merged bake spigot 1.13/1.14 and spigot 1.12 versions (compatible with 1.12 AND 1.13/1.14)<br>
 * 1.4.1: Added config version<br>
 * 1.4.1: Changed the way the chat config system works, it has now an entire config line allocated for multiple ingame lines. <br>
 * 1.4.1: Added config parameter "bake.general.noMeddle", if set to true, the config file won't be altered by the plugin in any way. <br></li><li>
 * 1.5.0: Added placeholder: "%TIMES%", which replaces the amount of times the project has been completed, stored in the config. <br>
 * 1.5.0: Added placeholder: "%TODAY%", which replaces how many projects were completed today. <br>
 * 1.5.0: Added placeholder: "%RECORD%", which replaces how many projects were completed on the day where the most projects were completed. <br>
 * 1.5.0: Added placeholder: "%PARTICIPANTS%", which replaces how many participants have participated in the ongoing project. <br>
 * 1.5.0: Added placeholder: "%LEFT%", which replaces the wheat that is left until the project is completed. <br> 
 * 1.5.0: Added placeholder: "%LAST%", which replaces the time and date when the last project got finished. <br>
 * 1.5.0: Added placeholder: "%RECORDDATE%", which replaces the date where the most records were done. <br>
 * 1.5.0: Added config parameter "bake.general.cnfgStore", if set to true, the plugin is allowed to store values inside the config (ignoring noMeddle) else some functions might not work properly. Note: the plugin will use it anyway, but it will not set default values. It might gain more meaning in future updates<br>
 * 1.5.0: Added config parameter "bake.general.doRecordSurpassBroadcast", by default set to true, if true, it will broadcast a message when the previous record was broken.<br>
 * 1.5.0: The Public int "BakeProgress" in class "Bake" is now a private int, if your plugin used the value, please change that <br>
 * 1.5.0: A broadcast will usually be done (if not disabled via setting) when the Record gets broken. <br>
 * 1.5.0: Added command: "/bakestats", which is just a bit like /bake, but has the intended use with statistics surrounding the bake project form all the way since 1.5.0 (or a newer version) was implemented on the server. <br>
 * 1.5.1: Added metrics <br>
 * 1.5.1: Added 1.8  - 1.11 support <br>
 * 1.5.1: Code cleanup <br>
 * 1.5.2: Now automatically converts enchantment names from pre-1.13 to  post 1.12.2 and vice versa (as long as its required). <br>
 * 1.5.2: Added config parameter "bake.general.doEnchantConvert" which toggles whether to convert enchantment from 1.12 and earlier to 1.13 or later (and vice versa; will pick the correct one) <br>
 * 1.5.2: Added vault support via the "bake.award.money" config parameter <br>
 * 1.5.2: Added config parameter "bake.award.money" which adds vault money to participants. <br>
 * 1.5.2: The record value now uses the date of the day before the record was broken, not the date of the day when the record was broken. So it will now correctly show when the most projects were completed. <br></li><li>
 * 1.6.0-pre1: Completely reworked on how the code is structured. <br>
 * 1.6.0-pre1: Reworked the slot system. <br>
 * 1.6.0-pre1: 1.6.0-pre1 is <b>NOT</b> config compatible with 1.5.2 or earlier! <br>
 * 1.6.0-pre1: Fixed that everyone would get extra money, regardless of reward policy <br>
 * 1.6.0-pre1: Fixed a potential bug where the program wouldn't run correctly without Vault even though it is a recommended dependency.<br>
 * 1.6.0-pre2: Redid the enchantment system, hopefully removing some pesky bugs<br>
 * 1.6.0-pre2: Minor fixes with the record chat being broken<br>
 * 1.6.0-pre3: Added a leaderboard with the /baketop command<br>
 * 1.6.0-pre3: Now catching previously uncaught exception that would occur when the Internet connection is not as intended.<br>
 * 1.6.0-pre3: Added config parameter "bake.chat.leaderboard.post", controlling the /baketop command<br>
 * 1.6.0-pre3: Added config parameter "bake.chat.leaderboard.pre", controlling the /baketop command<br>
 * 1.6.0-pre3: Added config parameter "bake.chat.leaderboard.mid", controlling the /baketop command<br>
 * 1.6.0-pre3: Added config parameter "bake.general.useLeaderboard", controlling whether the /baketop command can be used<br>
 * 1.6.0-pre3: Added the ability to contribute as much wheat as possible in one go via "/contribute max"<br>
 * 1.6.0-pre3: Fixed an API bug where the parameter "material" in function "Bake_Auxillary#hasEnoughItems" would not work as intended.<br>
 * 1.6.0-pre4: Fixed that everyone would get extra money, regardless of reward policy (Again)<br>
 * 1.6.0-pre4: Implemented the globalBake Playing mode, which enable multiple servers to cooperate with each other, at least somewhat<br>
 * 1.6.0-pre4: Fixed issue with the record not saving at all<br>
 * 1.6.0-pre4: Fixed a crash that would occur when a player on /baketop is offline<br>
 * 1.6.0-pre4: Fixed a bug where the ranking would not show up correctly and stay at 1 (is that even a bug?)<br>
 * 1.6.0-pre4: Fixed a bug where the contribution remembering setting would be inverted, resulting in unexpected behavior<br>
 * 1.6.0-pre4: Added an alias to the "/contribute max" command: "/contribute all"<br>
 * 1.6.0-pre4: Implemented a feature where players that have contributed but were offline when the project finished would be rewarded as soon as they rejoin. (Can be toggled via bake.general.rewardLater)<br>
 * 1.6.0-pre5: Improved debugging when Vault economies don't work as intended<br>
 * 1.6.1: Added the Bake administrator control panel, used to cheat the system<br>
 * 1.6.1: Updated default strings<br>
 * 1.6.1: Fixed that the metrics wouldn't run at all<br>
 * 1.6.1: Now autocompletes on Tab <br>
 * 1.6.2: Changed the way the plugin reacts to an incompatible config version<br>
 * 1.6.2: Added bStats<br>
 * 1.6.2: Converted to maven<br>
 * ?: Added placeholder: "%YESTERDAY%", which replaces the number of projects finished in the day before. <br>
 * ?: Added placeholder: "%AUTOFILL%{x}", which fills the line with the maximum amount of chars anywhere else in a line in the message<br>
 * ?: Added placeholder: "%BESTNAME%", which replaces the name of the top contributing player<br>
 * ?: Added placeholder: "%BESTSTAT%", which replaces the top amount contributed by a player over the lifetime of the server <br>
 * ?: Added placeholder: "%BEST%", which replaces the time that it took for the fastest project to complete. <br>
 * ?: Added placeholder: "%BESTDATE%", which replaces the date where the fastest project was completed. <br>
 * ?: Added placeholder: "%PARTICIPANTSTODAY%", which replaces how many participants have participated today. <br>
 * ?: Added placeholder: "%PARTICIPANTSRECORD%", which replaces how many participants have contributed at most. <br>
 * </li></ul>
 * 
 * @author Geolykt
 * @since 0.0.1 - SNAPSHOT
 *
 */
public class Bake extends JavaPlugin {
	/**
	 * API LEVEL for the bukkit server, not the plugin itself, you need that one, use the Bake Auxillary instead!
	 * @since 1.5.1
	 */
	private int API_LEVEL;
	
	/**
	 * Utillity Class for parsing Bake Placeholders.
	 * @since 1.6.0
	 */
	public StringUtils StringParser = null;
	
	/**
	 * Utillity Class for handling chat and data
	 */
	public BakeData DataHandle = null;

	/**
	 * Whether or not to use Vault (a money and permission API), see https://github.com/MilkBowl/VaultAPI
	 * @since 1.5.2
	 */
	private boolean useVault = true;
	
	/**
	 * The vault economy this plugin uses.
	 * @since 1.5.2
	 */
	private Economy Eco = null;
	
	/**
	 * Utility Class for the Leaderboard
	 * @since 1.6.0-pre3
	 */
	public Leaderboard lbHandle = null;

	public boolean useLeaderboard = false;
	
	@Override
	public void onEnable () {
		
		Bukkit.getPluginManager().registerEvents(new BakeEventListener(this, getConfig().getString("bake.chat.welcomeBack", "N/A")), this);
		
		lbHandle = new Leaderboard(this);
		useLeaderboard = true;
		
		//Strip Bukkit.getBukkitVersion() to only return the Bukkit API level / Minecraft Minor Version Number under the Major.Minor.Patch format.
		API_LEVEL = Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]); //Bukkit.getBukkitVersion() returns something like 1.12.2-R0.1-SNAPSHOT
		
		// Configuration initialization
		saveDefaultConfig();

		MeticsClass metricsRunnable = new MeticsClass();
		if (getConfig().getBoolean("bake.firstRun", true)) {
			metricsRunnable.State = 0x01;
			getConfig().set("bake.firstRun", false);
			saveConfig();
		} else if (getConfig().getBoolean("bake.metrics.opt-out", true)) {
			metricsRunnable.State = 0x02;
		} else {
			metricsRunnable.State = 0x00;
			metricsRunnable.plugin = this;
		}
		metricsRunnable.runTaskLater(this, 1L);
		
		if (getConfig().getDouble("bake.award.money", 0.0) > 0.0) {
			if (!setupEconomy()) {
				//Not hooked into Vault.
				getLogger().warning(ChatColor.YELLOW + "Vault (or an Economy plugin) was not installed or initiated too late. This is not much of a problem, but money won't be awarded.");
			}
		} else {
			//Using Vault would make no sense as no money would be sent.
			useVault = false;
		}
		
		if (!getConfig().getBoolean("bake.general.noMeddle", false)) {
		
			// Config Convert Process
			if (getConfig().getInt("bake.general.configVersion", -1) > 5) {
				//Notify User
				getLogger().log(Level.WARNING, ChatColor.YELLOW + "The config version is newer than it should be! The plugin will try to run normal, but it might break  the config file!");
				//the code can't do anything here, pray that it will work anyway.
			} else if (getConfig().getInt("bake.general.configVersion", -1) < 5) {
				//Stricly incompatible version (due to the award system completly being reworked, would be too tedious to create an autopatcher.
				getLogger().severe(ChatColor.DARK_RED + "The config version for bake is below the expected value of 5, this means it is stricly incompatible. Update the config manually!");
				new BukkitRunnable() {public void run() {getServer().broadcastMessage(ChatColor.GOLD + "[BAKE]" + ChatColor.DARK_RED + "Configuration Version outdated. Consider updating it manually for the time being.");}}.runTask(this);
			}
			
			//1.5.2 Enchant conversion
			if (getConfig().getBoolean("bake.general.doEnchantConvert", true)) {
				for (int i = 0; i < getConfig().getInt("bake.general.slots", 0); i++) {
					if (API_LEVEL >= 13) { //13 or later
						getConfig().set("bake.award." + i + ".enchantment", EnchantmentLib.Convert12to13(getConfig().getString("bake.award." + i + ".enchantment", "")));
					} else {//12 or earlier
						getConfig().set("bake.award." + i + ".enchantment", EnchantmentLib.Convert13to12(getConfig().getString("bake.award." + i + ".enchantment", "")));
					}
				}
			}
			
			saveConfig();
		}
		
		//Store values
		if (getConfig().getBoolean("bake.general.cnfgStore", true)) {
			readValues();
			getConfig().addDefault("bake.save.times", 0);
			getConfig().addDefault("bake.save.last", DateTimeFormatter.ISO_INSTANT.format(Instant.EPOCH));
			getConfig().addDefault("bake.save.recordtime", DateTimeFormatter.ISO_INSTANT.format(Instant.EPOCH));
			getConfig().addDefault("bake.save.record", 0);
			getConfig().addDefault("bake.save.today", 0);
			getConfig().addDefault("bake.save.participants", 0);
			getConfig().addDefault("bake.save.participantsToday", 0);
			
			
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		DataHandle.setBakeProgress(getConfig().getInt("bake.wheat_Required",99999999));

		StringParser = new StringUtils(this);
		if (getConfig().getBoolean("bake.gbake.enable", false)) {
			DataHandle = new GlobalBake(this,getConfig().getString("bake.gbake.update_server", "localhost"), getConfig().getString("bake.gbake.update_client", "localhost"), getConfig().getLong("bake.gbake.interval", 1000l), getConfig().getString("bake.chat.gBakeRefresh", "N/A"));
		} else {
			DataHandle = new LocalBake(this);
		}
		if (!getConfig().getBoolean("bake.general.useLeaderboard", true)) {
			useLeaderboard = false;
		}
		
		
		StringParser.cacheStrings();

		lbHandle.load();
		
		//Creates Quest
		this.saveResource("quests.yml", false);
	}
	
	/**
	 * This function "reads" the config file and gets all useful values from it and stores them in their respective variables.
	 * 
	 * @author Geolykt
	 * @since 1.5.0
	 * 
	 */
	private void readValues() {
		DataHandle.setLastCompletion(Instant.parse(getConfig().getString("bake.save.last", DateTimeFormatter.ISO_INSTANT.format(Instant.EPOCH))));
		if (!DataHandle.getLastCompletion().equals(Instant.EPOCH)) {
			DataHandle.setProjectsFinishedToday((short) getConfig().getInt("bake.save.today", 0));
		}
		DataHandle.setTimes((short) getConfig().getInt("bake.save.times", 0));
		DataHandle.setBestAmount((short) getConfig().getInt("bake.save.record", 0));
		DataHandle.setParticipantCount((byte) getConfig().getInt("bake.save.participants", 0));
		DataHandle.setParticipantsToday((byte) getConfig().getInt("bake.save.participantsToday", 0));
		DataHandle.setRecord(Instant.parse(getConfig().getString("bake.save.recordtime", DateTimeFormatter.ISO_INSTANT.format(Instant.EPOCH))));
	}

	@Override
	public void onDisable () {
		saveValues();
	}
	


	/**
	 * This function writes specific values in the config file (<u>if permitted</u>) for storage and later use.
	 * 
	 * @author Geolykt
	 * @since 1.5.0
	 * 
	 */
	private void saveValues() {
		if (getConfig().getBoolean("bake.general.cnfgStore", true)) {
			getConfig().set("bake.save.times", DataHandle.getOverallCompletionAmount());
			getConfig().set("bake.save.last", DateTimeFormatter.ISO_INSTANT.format(DataHandle.getLastCompletion()));
			getConfig().set("bake.save.recordtime", DateTimeFormatter.ISO_INSTANT.format(DataHandle.getRecordDate()));
			getConfig().set("bake.save.today", DataHandle.getProjectsFinishedToday());
			getConfig().set("bake.save.record", DataHandle.getRecordAmount());
			getConfig().set("bake.save.all", DataHandle.getTotalContributed());
			getConfig().set("bake.save.participants", DataHandle.getParticipantAmount());
			getConfig().set("bake.save.participantsToday", DataHandle.getParticipantAmountToday());
			saveConfig();
		}
		lbHandle.save();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("bakestats")) 
		{
			if (sender instanceof Player) {
				DataHandle.onBakestatsCommand((Player)sender);
			} else {
				sender.sendMessage("Following update 1.6.0, you must be a player to call that command");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("bake")) 
		{
			if (args.length != 0) {
				if (args[0].equalsIgnoreCase("admin")) {
					if (sender.hasPermission("bake.command.admin")) {
						DataHandle.adminCP(args, sender);
					} else {
						sender.sendMessage(ChatColor.RED + "[BAKE] You don't have sufficent permissions to use said command.");
					}
				} else if (args[0].equals("version")) {
					sender.sendMessage(ChatColor.AQUA + "Bake uses version " + Bake_Auxillary.PLUGIN_VERSION + " with implementation " + DataHandle.getImplementationName());
				} else if (args[0].equals("stop")) {
					if (sender.hasPermission("bake.command.admin")) {
						getServer().broadcastMessage(ChatColor.DARK_RED + "[Bake] Shutting down...");
						getPluginLoader().disablePlugin(this);
					} else {
						sender.sendMessage(ChatColor.RED + "[BAKE] You don't have sufficent permissions to use said command.");
					}
				} else {
					sender.sendMessage("Subcommand unknown.");
				}
			} else if (sender instanceof Player) {
				DataHandle.onBakeCommand((Player)sender);
			} else {
				sender.sendMessage("Following update 1.6.0, you must be a player to call that command");
			}
			return true;
			
		} else if (cmd.getName().equalsIgnoreCase("baketop"))
		{

			if (!useLeaderboard) {
				sender.sendMessage(getConfig().getString("bake.chat.leaderboard.unavail", "N/A"));
				return true;
			}
			sender.sendMessage(StringParser.leaderboard_pre);
			for (int i = 1; (i <= 11) && (i <= lbHandle.lbMap.size()); i++) {
				sender.sendMessage(String.format(StringParser.leaderboard_main , i, Bukkit.getOfflinePlayer((UUID) lbHandle.SortedMap.keySet().toArray()[lbHandle.SortedMap.size()-i]).getName(),ChatColor.DARK_RED + "" + lbHandle.lbMap.get(lbHandle.SortedMap.keySet().toArray()[lbHandle.SortedMap.size()-i])));
			}
			sender.sendMessage(StringParser.leaderboard_post);
			return true;
			
		} else if (cmd.getName().equalsIgnoreCase("contribute")) 
		{
			
			if (!(sender instanceof Player)) {//Check if the user is really a player; would cause havoc, if not
				sender.sendMessage("This command can only be run by a player.");  //Shown if not a player
				return true;
			} else { // if the user is a player
				
				int amount = 0;
				Player player = (Player) sender; 
				if (args.length > 0) {
					if (args[0].equals("max") || args[0].equals("all")) {
						amount = Bake_Auxillary.removeEverythingInInventoryMatchesItem(player, Material.WHEAT);
						DataHandle.addContribution(amount);
						lbHandle.update(player.getUniqueId(), amount);
					} else {
						//Error logic (to remove any errors that could be avoided)
						try {// Retrieve the amount the player wants to donate
							amount = Integer.parseInt(args[0]);
						} catch (NumberFormatException nfe) { // in case that that's not a real number
							return false;
						}
						// if 'amount' is under 1
						if (amount < 1) {
							return false; //Amount is lower than 1; this would lead to an error if it is not caught
						}
						//Check whether the player has the amount of wheat in its inventory, if not, the player will be notified
						if (Bake_Auxillary.hasEnoughItems(player,Material.WHEAT, amount)) {//Player has enough wheat in its inventory
							Bake_Auxillary.removeItem(player, Material.WHEAT, amount);
							DataHandle.addContribution(amount);
							lbHandle.update(player.getUniqueId(), amount);
						} else {//player doesn't have enough wheat in its inventory
							player.sendMessage(ChatColor.RED + "You don't have the specified amount of "+ Material.WHEAT.toString() + " in your inventory");
							return true;
						}
					}
				}
				
				// REMINDING CODEth
				if (getConfig().getBoolean("bake.general.remember")) 
				{
					if (!DataHandle.projectReminderList.containsKey(player.getUniqueId())) 
					{
						//Player has not yet participated
						//Handeld by onContribution() after 1.7
//						DataHandle.projectReminderList.put(players.getUniqueId(), false); (This is should be handeled via the BakeData.onContribute() method)

						DataHandle.setParticipantCount((byte) (DataHandle.getParticipantAmount()+1));
						if (!DataHandle.dayReminderList.containsKey(player.getUniqueId())) {
							DataHandle.setParticipantsToday((byte) (DataHandle.getParticipantAmountToday()+1));
							DataHandle.dayReminderList.put(player.getUniqueId(), true);
						}
					}
				}
				
				DataHandle.onContribution(amount, player);
				
				// Command executed
				StringParser.cacheStrings();
				
				String s = StringParser.BakeContributionString_Sender;
				s = s.replaceAll("%INTPROG%", String.valueOf(amount));
				s = StringParser.replaceFrequent(s, player.getDisplayName());
				player.sendMessage(s);

				s = StringParser.BakeContributionString_Global;
				s = s.replaceAll("%INTPROG%", String.valueOf(amount));
				s = StringParser.replaceFrequent(s, player.getDisplayName());
				getServer().broadcastMessage(s);
				
				if (DataHandle.isFinished()) {
					forceFinish(player.getDisplayName());
				}
				StringParser.cacheStrings();
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Rewards ONLINE players that have been marked in the DataHandle.projectReminderList HashMap via the default set reward mechanisms & removes players that have been rewarded from that list afterwards. <br>
	 * Does not reward OFFLINE players that have been marked in the DataHandle.projectReminderList HashMap, but removes it from the Map and inserts them in the DataHandle.notRewarded HashMap.
	 * Note: the automatic removal feature is prone to not work
	 * @since 1.6.0-pre4
	 */
	public void doItemRewardProcess () {
		//Item reward process
		ArrayList<ItemStack> rewards = rollLootTable();
		
		//Trim the Array to not overshoot the maximum
		if (rewards.size()>getConfig().getInt("bake.award.maximum", 0)) {
			for (int i = rewards.size(); i > getConfig().getInt("bake.award.maximum", 0); i--) {
				rewards.remove(i);
			}
		}
		
		if (getConfig().getBoolean("bake.general.remember")) {
			double moneyAmount = 0.0;
			if (useVault) {
				moneyAmount = getConfig().getDouble("bake.award.money", 0.0);
			}
			for (UUID playerUUID : DataHandle.projectReminderList.keySet()) {
				if (!Bukkit.getOfflinePlayer(playerUUID).isOnline() && (DataHandle.projectReminderList.getOrDefault(playerUUID, -1) > 0)) {
					DataHandle.notRewarded.add(playerUUID);
					DataHandle.projectReminderList.remove(playerUUID);
				}
			}
			for (Player players : getServer().getOnlinePlayers()) {
				if (DataHandle.projectReminderList.getOrDefault(players.getUniqueId(), -1) > 0) {

					for (ItemStack stack : rewards) {
						players.getInventory().addItem(stack);
					}
					if (useVault) {
						try {
							Eco.depositPlayer(players, moneyAmount);
						} catch (Exception e) {
							useVault = false;
							e.printStackTrace();
							getLogger().severe("[BAKE] Totally not a mistake on my part. You should dispute with your economy plugin!");
						}
					}
//					DataHandle.projectReminderList.put(players.getUniqueId(), false); (This is should be handeled via the BakeData.onContribute() method)
					DataHandle.projectReminderList.remove(players.getUniqueId());
				}
			}
		} else {
			for (ItemStack stack : rewards) {
				//send item to the players
				for (Player players : getServer().getOnlinePlayers()) {
					if (players.getInventory().firstEmpty() == -1) {
						continue;
					}
					players.getInventory().addItem(stack);
				}
			}
			if (useVault) {
				double moneyAmount = getConfig().getDouble("bake.award.money", 0.0);
				for (Player players: getServer().getOnlinePlayers()) {
					Eco.depositPlayer(players, moneyAmount);
				}
			}
		}

	}

	/**
	 * Like replaceAdvancedCached, but can be used to parse pretty much everything and it gets returned
	 * @param s The string to be parsed.
	 * @return The parsed string
	 * @since 1.5.0
	 * @author Geolykt
	 * @deprecated will be removed in the future
	 */
	public String replaceAdvanced(String s) {
		s = s.replaceAll("%TIMES%", String.valueOf(DataHandle.getOverallCompletionAmount()));
		s = s.replaceAll("%TODAY%", String.valueOf(DataHandle.getProjectsFinishedToday()));
		DateTimeFormatter format = DateTimeFormatter.RFC_1123_DATE_TIME.withLocale(Locale.UK)
													.withZone(ZoneId.systemDefault());
		s = s.replaceAll("%LAST%", format.format(DataHandle.getLastCompletion()));
		format = DateTimeFormatter.ISO_LOCAL_DATE.withLocale(Locale.UK)
				                                 .withZone(ZoneId.systemDefault());
		s = s.replaceAll("%RECORDDATE%", format.format(DataHandle.getRecordDate()));
		s = s.replaceAll("%RECORD%", String.valueOf(DataHandle.getRecordAmount()));
		s = s.replaceAll("%PARTICIPANTS%", String.valueOf(DataHandle.getParticipantAmount()));
		s = s.replaceAll("%PARTICIPANTSTODAY%", String.valueOf(DataHandle.getParticipantAmountToday()));
		return s;
	}

	/**
	 * Sets up the economy.
	 * 
	 * @return false if economy not found, true if it was found.
	 * @since 1.5.2
	 * @author Geolykt
	 */
	private boolean setupEconomy() {
        try {
        	@SuppressWarnings("unused") //It's actually used
			Class<?> clazz = net.milkbowl.vault.economy.Economy.class;
        	RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        	if (economyProvider != null) {
        		Eco = economyProvider.getProvider();
        	}
        } catch (NoClassDefFoundError e) {
        	useVault = false;
        	Eco = null;
        	return false;
        }
        useVault = (Eco != null);
        return useVault;//Sets useVault to the inverse of whether the Economy is null, and returns it. 
    }
	
	/**
	 * Rolls the Loot table (defined in the config) and returns the all the items that have been rolled.
	 * @return All the rolled itemstacks
	 * @since 1.6.0
	 * @deprecated Will not be used in 1.7 and should not be used at that point forward.
	 */
	public ArrayList<ItemStack> rollLootTable() {
		int amount_entries = getConfig().getInt("bake.general.slots", 0);
		ArrayList<ItemStack> ItemStacks = new ArrayList<ItemStack>();
		for (int i = 0; i < amount_entries; i++) {
			if (Math.random() <= getConfig().getDouble("bake.award."+i+".chance", 0.0)) {
				ItemStack items = new ItemStack(Material.getMaterial(getConfig().getString("bake.award."+i+".type", "AIR")));
				items.setAmount(getConfig().getInt("bake.award."+i+".count"));
				ItemMeta itemM = items.getItemMeta();
				
				//LORE
				ArrayList<String> l = new ArrayList<String>();
				for (String string : getConfig().getString("bake.award."+i+".lore", "").split("\\|")) {
					l.add(string);
				}
				if (l != null) {
					itemM.setLore(l);
				}
				l.clear();
				
				String itemName = getConfig().getString("bake.award."+i+".display_name", "");
				if (!itemName.equals("")) {
					itemM.setDisplayName(itemName);
				}
				items.setItemMeta(itemM);

				
				//Enchantment
				HashMap<Enchantment,Integer> enchantments = new HashMap<Enchantment, Integer>(); 
				
				for (String string : getConfig().getString("bake.award."+i+".enchantment", "").split("\\|")) {
					if (string.equals("NIL") || string.equals("")) {
						break;
					}
					if (string.split("@").length < 2) {//Not following the "Enchantment@Level" convention
						break;
					}
					
					try {
						if (API_LEVEL > 11) {
							enchantments.put(EnchantmentLib.getEnchantmentFromString(string.split("@")[0], API_LEVEL), Integer.valueOf(string.split("@")[1]));
						} else {
							itemM.addEnchant(Enchantment.getByName(string.split("@")[0]), Integer.valueOf(string.split("@")[1]), true);
						}
					} catch (NumberFormatException e) {
						getLogger().severe("Error while enchanting item: Number format issue, skipping line. Please make sure everything is using the 'enchantment@level' format.");
						break;
					} catch (java.lang.IllegalArgumentException e) {
						getLogger().severe("Error while enchanting item: IllegalArgumentException: (" + e.getLocalizedMessage() + ") it is recommended to use 1.12 enchant strings, not the 1.13 ones (default values)! If the error persists, create an issue on github or dev.bukkit.org");
						getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "[BAKE] ERROR PREVENTED: An issue occoured during the process, see the logfiles for more information (" + e.getLocalizedMessage() + ")");
						break;
					} 
					
				}

				if (API_LEVEL >= 12) {//API 12 or higher

					//1.12 and above -> enchantment as usual
					try {
						items.addUnsafeEnchantments(enchantments);
					} catch (IllegalArgumentException e) {
						if (API_LEVEL <= 12) {
							this.getLogger().severe("Something went wrong while enchanting an item. Contact the plugin's developer or check your configurations (are the entries legal for 1.12, because default values will always be faulty for 1.12; check the plugin's page (https://dev.bukkit.org/projects/bake) for more information on to solve this issue)");
						} else {
							this.getLogger().severe("Something went wrong while enchanting an item. Contact the plugin's developer or check your configurations (are the enchantments really existing? Perhaps they are misspelt.)");
						}
					}
					enchantments.clear();
				//1.11 and below -> enchantment via metadata
				} else { //API 11 or lower
					items.setItemMeta(itemM);
				}
				
				ItemStacks.add(items);
			}
		}
		return ItemStacks;
	}
	
	/**
	 * Force-finishes the project ignoring its requirements. Rewards are handed out as usual.<br>
	 * DOES NOT RESET Requirements.
	 * @since 1.6.0-pre4
	 * @param playername Used to replace the %PLAYER% placeholder
	 */
	public void forceFinish(String playername) {

		// Project finished
		DataHandle.onFinish();
			
		//Finish Message
		String s = StringParser.BakeFinishString;
		s = StringParser.replaceFrequent(s, playername);
		getServer().broadcastMessage(s);
			
		doItemRewardProcess();
				
		//Bake project finished 
		if (getConfig().getBoolean("bake.general.deleteRemembered")) {//Clear the list of contributors
			DataHandle.projectReminderList.clear();
		}
		DataHandle.setTimes((short) (DataHandle.getProjectsFinishedToday() + 1));
				
		DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE.withLocale(Locale.UK)
						                                                   .withZone(ZoneId.systemDefault());
			
		//If the two ISO Local Dates are the same, then the amount of projects is increased, otherwise it will be reset to 1 (since the project got completed)
		if (format.format(DataHandle.getLastCompletion()).equals(format.format(Instant.now()))) {
		//same date
			DataHandle.setProjectsFinishedToday((short) DataHandle.getProjectsFinishedToday());
		} else {
			//different date. 
			forceRecordSurpassCheck(playername);
			DataHandle.setProjectsFinishedToday((short) 1);
			DataHandle.setParticipantsToday((byte) 0x00);
			DataHandle.dayReminderList.clear();
		}
		DataHandle.setParticipantCount((byte) 0x00);
		DataHandle.setLastCompletion(Instant.now());
		saveValues();	
	}
	
	/**
	 * Used to forcefully reward a player through default algorithms. Note: some differences between the two scripts may exist, so they aren't really 100% the same-
	 * @since 1.6.0-pre4
	 * @param player The player to receive the rewards
	 */
	public void rewardPlayer(Player player) {

		//Item reward process
		ArrayList<ItemStack> rewards = rollLootTable();
		
		//Trim the Array to not overshoot the maximum
		if (rewards.size()>getConfig().getInt("bake.award.maximum", 0)) {
			for (int i = rewards.size(); i > getConfig().getInt("bake.award.maximum", 0); i--) {
				rewards.remove(i);
			}
		}
		for (ItemStack stack : rewards) {
			player.getInventory().addItem(stack);
		}
		if (useVault) {
			try {
				Eco.depositPlayer(player, getConfig().getDouble("bake.award.money", 0.0));
			} catch (Exception e) {
				useVault = false;
				e.printStackTrace();
				getLogger().severe("[BAKE] Totally not a mistake on my part. You should dispute with your economy plugin!");
			}
		}
	}
	
	/**
	 * Forces a check whether the record was broken or not and takes appropriate actions
	 * @since 1.6.0-pre4
	 * @param playername Replaces %player%
	 */
	public void forceRecordSurpassCheck(String playername) {
		//If the record is lower than what was archived the day before, then the record gets overridden with the amount of stuff done the day before
		if (DataHandle.getProjectsFinishedToday() > DataHandle.getRecordAmount()) {
			if (getConfig().getBoolean("bake.general.doRecordSurpassBroadcast", true)) {
				Bukkit.broadcastMessage(StringParser.replaceFrequent(StringParser.BakeRecordString, playername));
			}
			DataHandle.setRecord(DataHandle.getLastCompletion());
			DataHandle.setBestAmount((short) DataHandle.getProjectsFinishedToday());
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		if (command.getName().equalsIgnoreCase("bake")) {
			if (args.length == 1) {//So no ArrayIndexOutOfBounds Exceptions would occur
				if (args[0].contains("admin")) {
					return DataHandle.onTabComplete(sender, command, alias, args);
				} else {
					List<String> list = new ArrayList<String>();
					list.add("admin");
					list.add("version");
					list.add("stop");
					return list;
					
				}
			}
		} else if (command.getName().equalsIgnoreCase("contribute")) {
			List<String> list = new ArrayList<String>();
			list.add("max");
			list.add("1");
			list.add("all");
			return list;
		}
		return super.onTabComplete(sender, command, alias, args);
	}

	public Callable<String> metricsWheatAmount() {
		int allTime = getConfig().getInt("bake.save.all", -1);
		if (allTime < 1) {
			return () -> "0";
		} else if (allTime < 200){
			return () -> "1-199";
		} else if (allTime < 2000){
			return () -> "200-1999";
		} else if (allTime <= 5000){
			return () -> "2000-5000";
		} else if (allTime <= 20000){
			return () -> "5001-20000";
		} else if (allTime <= 50000) {
			return () -> "20001-50000";
		} else  {
			return () -> ">50000";
		}
	}
}
