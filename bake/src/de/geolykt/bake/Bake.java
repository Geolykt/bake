package de.geolykt.bake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
 * 1.5.2: Added config parameter "bake.general.doEnchantConvert" which toggles wether to convert enchantments from 1.12 and earlier to 1.13 or later (and vice versa; will pick the correct one)<br>
 * 1.5.2: Added vault support via the "bake.award.money" config parameter
 * 1.5.2: Added config parameter "bake.award.money" which adds vault money to participants.
 * 1.5.2: The record value now uses the date of the day before the record was broken, not the date of the day when the record was broken. So it will now correctly show when the most projects were completed.
 * ?: Added placeholder: "%YESTERDAY%", which replaces the number of projects finished in the day before. <br>
 * ?: Added placeholder: "%AUTOFILL%{x}", which fills the line with the maximum amount of chars anywhere else in a line in the message<br>
 * ?: Added placeholder: "%BESTNAME%", which replaces the name of the top contributing player<br>
 * ?: Added placeholder: "%BESTSTAT%", which replaces the top amount contributed by a player over the lifetime of the server <br>
 * ?: Added placeholder: "%BEST%", which replaces the time that it took for the fastest project to complete. <br>
 * ?: Added placeholder: "%BESTDATE%", which replaces the date where the fastest project was completed. <br>
 * ?: Added placeholder: "%PARTICIPANTSTODAY%", which replaces how many participants have participated today. <br>
 * ?: Added placeholder: "%PARTICIPANTSRECORD%", which replaces how many participants have contributed at most. <br>
 * ?: Added config parameter "bake.general.permremember", if set to true, the plugin will store ALL contributors and the amount they have contributed in a flat file <br>
 * </li></ul>
 * 
 * @version 1.5.2
 * @author Geolykt
 * @since 0.0.1 - SNAPSHOT
 *
 */
public class Bake extends JavaPlugin {

	/**
	 * Whether or not to use Vault (a money and permission API), see https://github.com/MilkBowl/VaultAPI
	 * @since 1.5.2
	 */
	private boolean useVault = true;
	
	/**
	 * The economy this plugin uses.
	 * @since 1.5.2
	 */
	private Economy Eco = null;
	
	private int BakeProgress = 0; //The Progress of the project
	private byte Participants = 0; //The Participants of the current project
	private byte ParticipantsToday = 0; //The number of participants today
	private short Today = 0; //The projects finished today
	private short Times = 0; //The projects finished up to date
	private short BestAmount = 0; //The most projects finished in a day
	private Instant Last = Instant.EPOCH; //The last time a project was completed
	private Instant Record = Instant.EPOCH; //The day the most projects were finished
	private  HashMap<UUID, Boolean> Reminded= new HashMap<UUID, Boolean>(); //A HashMap that
	private  HashMap<UUID, Boolean> RemindedDay = new HashMap<UUID, Boolean>(); //A HashMap that
	
	/**
	 * API LEVEL for the bukkit server, not the plugin itself, you need that one, use the Bake Auxillary instead!
	 * @since 1.5.1
	 */
	private int API_LEVEL;
	
	/**
	 * Private Container for cached /bake messages.
	 * @since 1.5.0
	 */
	private String msgProg;
	/**
	 * Private Container for cached Contribution messages
	 * @since 1.5.0
	 */
	private String msgContr;
	/**
	 * Private Container for cached Contribution messages that need to be send out globally
	 * @since 1.5.0
	 */
	private String msgGlobContr;
	/**
	 * Private Container for cached messages when the project finishes
	 * @since 1.5.0
	 */
	private String msgFin;
	/**
	 * Private Container for cached /bakestats messages
	 * @since 1.5.0
	 */
	private String msgStats;
	
	
	@Override
	public void onEnable () {
		//Strip Bukkit.getBukkitVersion() to only return the Bukkit API level / Minecraft Minor Version Number under the Major.Minor.Patch format.
		API_LEVEL = Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]); //Bukkit.getBukkitVersion() returns something like 1.12.2-R0.1-SNAPSHOT
		
		// Configuration initialization
		saveDefaultConfig();
//		reloadConfig();
		
		//Contact bakeMetrics
		getLogger().fine("Metrics init");
		BukkitRunnable metricsRunnable = new BukkitRunnable() {
				
			@Override
			public void run() {
				getLogger().info("Enabling bake metrics...");
				if (getConfig().getBoolean("bake.firstRun", true)) {
					getConfig().set("bake.firstRun", false);
					getLogger().info("Bake uses it's own metrics server at \"https://geolykt.de/src/bake/bakeMetrics.php\". To honor privacy, it will not contact it on the first run or if \"bake.metrics.opt-out\" is set to true.");
					saveConfig();
					return;//stop the metics runnable
				}
				if (!getConfig().getBoolean("bake.metrics.opt-out", true)) {
					try {
						URI metricsServerURI = new URI("https://geolykt.de/src/bake/bakeMetrics.php?version=" + Bake_Auxillary.PLUGIN_VERSION_ID);
						URLConnection metricsServer = metricsServerURI.toURL().openConnection();
						metricsServer.connect();
						metricsServer.getInputStream().close();
					} catch (URISyntaxException | IOException e) {
						getLogger().info("An error occured while trying to send data to the metrics server. Ignoring."); // Would be strange, but don't panic
					}
					
				}
			}};
		metricsRunnable.runTaskLater(this, 1L);
		
		if (getConfig().getDouble("bake.award.money", 0.0) > 0.0) {
			if (!setupEconomy()) {
				//Not hooked into Vault.
				getLogger().warning("Vault (or an Economy plugin) was not installed or initiated too late. This is not much of a problem, but money won't be awarded.");
			}
		} else {
			//Using Vault would make no sense as no money would be sent.
			useVault = false;
		}
		
		if (!getConfig().getBoolean("bake.general.noMeddle", false)) {
		
			// Config Convert Process
			if (getConfig().getInt("bake.general.configVersion", -1) > 4) {
				//Notify User
				getServer().getLogger().log(Level.WARNING, "The config version is newer than it should be! The plugin will try to run normal, but it might break  the config file!");
				//the code can't do anything here, pray that it will work anyway.
			}
		
			// 1.4.1's %NEWLINE% is no longer supported in newer versions
			if (getConfig().getInt("bake.general.configVersion", -1) > 4) {
				getLogger().info("Updating from the 1.4.1 config version (version 3) to the 1.5.0/1.5.1 config version (version 4). You may need to restart the server for it to take effect."); 
				
				String s = getConfig().getString("bake.chat.progress2", "");
				s = Bake_Auxillary.NewConfig(s);
				getConfig().set("bake.chat.progress2", s);
				
				s = getConfig().getString("bake.chat.contr2", "");
				s = Bake_Auxillary.NewConfig(s);
				getConfig().set("bake.chat.contr2", s);
				
				s = getConfig().getString("bake.chat.global.contr2", "");
				s = Bake_Auxillary.NewConfig(s);
				getConfig().set("bake.chat.global.contr2", s);
				
				s = getConfig().getString("bake.chat.finish2", "");
				s = Bake_Auxillary.NewConfig(s);
				getConfig().set("bake.chat.finish2", s);
				saveConfig();
			}
			
			//1.5.2 Enchant conversion
			if (getConfig().getBoolean("bake.general.doEnchantConvert", true)) {
				for (int i = 0; i < getConfig().getInt("bake.general.slots", 0); i++) {
					if (API_LEVEL >= 13) { //13 or later
						getConfig().set("bake.enchantment.slot." + i, EnchantmentLib.Convert12to13(getConfig().getString("bake.enchantment.slot." + i, "NIL")));
					} else {//12 or earlier
						getConfig().set("bake.enchantment.slot." + i, EnchantmentLib.Convert13to12(getConfig().getString("bake.enchantment.slot." + i, "NIL")));
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
		BakeProgress = (int) getConfig().get("bake.wheat_Required");
	}
	
	/**
	 * This function "reads" the config file and gets all useful values from it and stores them in their respective variables.
	 * 
	 * @author Geolykt
	 * @since 1.5.0
	 * 
	 */
	private void readValues() {
		Last = Instant.parse(getConfig().getString("bake.save.last", DateTimeFormatter.ISO_INSTANT.format(Instant.EPOCH)));
		Times = (short) getConfig().getInt("bake.save.times", 0);
		if (!Last.equals(Instant.EPOCH)) {
			Today = (short) getConfig().getInt("bake.save.today", 0);
		}
		BestAmount = (short) getConfig().getInt("bake.save.record", 0);
		Participants = (byte) getConfig().getInt("bake.save.participants", 0);
		ParticipantsToday = (byte) getConfig().getInt("bake.save.participantsToday", 0);
		Record = Instant.parse(getConfig().getString("bake.save.recordtime", DateTimeFormatter.ISO_INSTANT.format(Instant.EPOCH)));
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
			getConfig().set("bake.save.times", Times);
			getConfig().set("bake.save.last", DateTimeFormatter.ISO_INSTANT.format(Last));
			getConfig().set("bake.save.today", Today);
			getConfig().set("bake.save.record", BestAmount);
			getConfig().addDefault("bake.save.participants", Participants);
			getConfig().addDefault("bake.save.participantsToday", ParticipantsToday);
			saveConfig();
		}
		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("bakestats")) 
		{
			
			double progressPercent = (double) (-(BakeProgress - getConfig().getInt("bake.wheat_Required")) / (getConfig().getInt("bake.wheat_Required") + 0.0)*100);
			int progress = -(BakeProgress - getConfig().getInt("bake.wheat_Required") );
			

		    msgStats = replaceAdvanced(getConfig().getString("bake.chat.bakestats", "ERROR"));
		    String s = msgStats;
			s = Bake_Auxillary.ReplacePlaceHolders(s, progress, getConfig().getInt("bake.wheat_Required"), progressPercent, "ERROR");
			sender.sendMessage(s);
			
			return true;
		} else if (cmd.getName().equalsIgnoreCase("bake")) 
		{

			double progressPercent = (double) (-(BakeProgress - getConfig().getInt("bake.wheat_Required")) / (getConfig().getInt("bake.wheat_Required") + 0.0)*100);
			int progress = -(BakeProgress - getConfig().getInt("bake.wheat_Required") );
			

		    msgProg = replaceAdvanced(getConfig().getString("bake.chat.progress2", "ERROR"));
		    String s = msgProg;
			s = Bake_Auxillary.ReplacePlaceHolders(s, progress, getConfig().getInt("bake.wheat_Required"), progressPercent, "ERROR");
			sender.sendMessage(s);
			return true;
			
		} else if (cmd.getName().equalsIgnoreCase("contribute")) 
		{
			
			if (!(sender instanceof Player)) {//Check if the user is really a player; would cause havoc, if not
				sender.sendMessage("This command can only be run by a player.");  //Shown if not a player
				
			} else { // if the user is a player
				
				int amount = 0;
				//Error logic (to remove any errors that could be avoided)
				try {// Retrieve the amount the player wants to donate
					amount = Integer.parseInt(args[0]);
				} catch (NumberFormatException nfe) { // in case that that's not a real number
					return false;
				} catch (ArrayIndexOutOfBoundsException e) { // in case no string was entered
					return false;
				}
				// if 'amount' is under 1
				if (amount < 1) {
					return false; //Amount is lower than 1; this would lead to an error if it is not caught
				}
				Player player = (Player) sender; 
				
				//Check whether the player has the amount of wheat in its inventory, if not, the player will be notified
				boolean hasEnoughWeat = false;
				
				int amountLeft = amount;
				for (int i = 0; i < player.getInventory().getSize(); i++) {
					
					try {
						if (player.getInventory().getItem(i).getType() == Material.WHEAT) {
							amountLeft -= player.getInventory().getItem(i).getAmount();
							if (amountLeft <= 0) {
								hasEnoughWeat = true;
								break;
							}
						}
					} catch (NullPointerException npe) {
					}
				}
				
				if (hasEnoughWeat) {//Player has enough wheat in its inventory
					amountLeft = amount;
					for (int i = 0; i < player.getInventory().getSize(); i++) {
						if (amountLeft == 0) {
							BakeProgress -= amount;
							break;
						}
						try {
							if (player.getInventory().getItem(i).getType() == Material.WHEAT) {
								if (player.getInventory().getItem(i).getAmount() > amountLeft) {
									ItemStack is = player.getInventory().getItem(i);
									is.setAmount(player.getInventory().getItem(i).getAmount() - amountLeft);
									player.getInventory().setItem(i, is);
									BakeProgress -= amount;
									break;
								} else {
									amountLeft -= player.getInventory().getItem(i).getAmount();
									player.getInventory().clear(i);
								}
							}
						} catch (NullPointerException npe) {//This exception will be thrown upon accessing empty inventory slots, this catch is required to keep the program running
							//Do Nothing
						}
						
					}
					
					
				} else {//player doesn't have enough wheat in its inventory
					player.sendMessage(ChatColor.RED + "You don't have the specified amount of wheat in your inventory");
					return true;
				}
				// Command executed

				replaceAdvancedCached();
				String s = Bake_Auxillary.ReplacePlaceHolders(msgContr, Integer.parseInt(args[0]), getConfig().getInt("bake.wheat_Required"), -1, player.getDisplayName());
				sender.sendMessage(s);

				s = Bake_Auxillary.ReplacePlaceHolders(msgGlobContr, Integer.parseInt(args[0]), getConfig().getInt("bake.wheat_Required"), -1, player.getDisplayName());
				getServer().broadcastMessage(s);
				
				// REMINDING CODEth
				if (getConfig().getBoolean("bake.general.remember")) 
				{
					if (!Reminded.containsKey(player.getUniqueId())) 
					{
						//Player has not yet participated
						Reminded.put(player.getUniqueId(), true);
						Participants++;
						if (!RemindedDay.containsKey(player.getUniqueId())) {
							ParticipantsToday++;
							RemindedDay.put(player.getUniqueId(), true);
						} else {
						}
					}
				}
				
				
				
				// Project finished
				if (BakeProgress <= 0) {
					s = msgFin;
					s = Bake_Auxillary.ReplacePlaceHolders(s, Integer.parseInt(args[0]), getConfig().getInt("bake.wheat_Required"), -1, player.getDisplayName());
					getServer().broadcastMessage(s);
					ItemStack items;
					int reward_count = 0;
					
					
					//Item reward process
					for (int i = 0; i < getConfig().getInt("bake.general.slots", 0); i++) {
						
						if ((Math.random() < getConfig().getDouble("bake.chances.slot." + i)) && (getConfig().getInt("bake.award.maximum")) > reward_count) {
							items = new ItemStack(Material.getMaterial(getConfig().getString("bake.award.slot." + i, "AIR")));
							items.setAmount(getConfig().getInt("bake.amount.slot." + i));
							ItemMeta itemM = items.getItemMeta();
							
							//LORE
							ArrayList<String> l = new ArrayList<String>();
							for (String string : getConfig().getString("bake.lore.slot." + i, "").split("\\|")) {
								l.add(string);
							}
							if (l != null) {
								itemM.setLore(l);
							}
							l.clear();
							
							itemM.setDisplayName(getConfig().getString("bake.name.slot." + i, getConfig().getString("bake.award.slot." + i, "AIR")));
							items.setItemMeta(itemM);

							
							//Enchantment
							HashMap<Enchantment,Integer> enchantments = new HashMap<Enchantment, Integer>(); 
							for (String string : getConfig().getString("bake.enchantment.slot." + i, "NIL").split("\\|")) {
								if (string.equals("NIL")) {
									break;
								}
								if (string.split("@").length < 2) {
									break;
								}
								// Source for errors: Enchantment.getByKey only exists in spigot API level 13 or higher, but not in spigot API level 12 or lower!
								// So the code will check which version the server is running on and then uses the appropriate function, this is doable as Java allows the use of invalid functions within the sourcecode as long as they don't get called.
								try { //prevent stupidity of the server owner
									//API 13+
									if (API_LEVEL >= 13) {
										enchantments.put(Enchantment.getByKey(NamespacedKey.minecraft(string.split("@")[0])), Integer.valueOf(string.split("@")[1]));
									} else if (API_LEVEL == 12) {//API 12
										//This is deprecated for Bukkit 1.13 or higher, but since it doesn't get called on these versions, it is fine
										enchantments.put(Enchantment.getByName(string.split("@")[0]), Integer.valueOf(string.split("@")[1]));
									}
									//1.12 and above -> enchantment as usual
									if (API_LEVEL >= 12) {//API 12 or higher
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
									} else if (API_LEVEL <= 11) { //API 11 or lower
										itemM.addEnchant(Enchantment.getByName(string.split("@")[0]), Integer.valueOf(string.split("@")[1]), true);
									}
								} catch (NullPointerException e) {
									getLogger().severe("Error while enchanting item: NullPointerException: it is recommended to use 1.12 enchant strings, not the 1.13 ones (default values)! If the error persists, create an issue on github or dev.bukkit.org");
									getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "[BAKE] ERROR PREVENTED: An issue occoured during the process, see the logfiles for more information");
								} catch (java.lang.IllegalArgumentException e) {
									getLogger().severe("Error while enchanting item: IllegalArgumentException: it is recommended to use 1.12 enchant strings, not the 1.13 ones (default values)! If the error persists, create an issue on github or dev.bukkit.org");
									getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "[BAKE] ERROR PREVENTED: An issue occoured during the process, see the logfiles for more information");
								}
							}
							if (API_LEVEL <= 11) {
								items.setItemMeta(itemM);
							}
							
							//send item to the players
							for (Player players : getServer().getOnlinePlayers()) {
								if (players.getInventory().firstEmpty() == -1) {
									continue;
								}
								
								
								getLogger().info(items.getEnchantments() + "");
								
								//Server uses setting?
								if (getConfig().getBoolean("bake.general.remember")) {
									//Check whether player has yet contributed
									if (Reminded.getOrDefault(players.getUniqueId(), false)) {
										players.getInventory().addItem(items);
									}
								} else {
									players.getInventory().addItem(items);
								}
							
							}
							reward_count++;
						}
						
					}
					
					if (useVault) {
						double moneyAmount = getConfig().getDouble("bake.award.money", 0.0);
						for (Player players: getServer().getOnlinePlayers()) {
							Eco.depositPlayer(players, moneyAmount);
						}
					}
					
					//Bake project finished 
					BakeProgress = getConfig().getInt("bake.wheat_Required");//reset progress
					if (getConfig().getBoolean("bake.general.deleteRemembered")) {//Clear the list of contributors
						Reminded.clear();
					}
					Times++;
					
					DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE.withLocale(Locale.UK)
							                                                   .withZone(ZoneId.systemDefault());
					//If the two ISO Local Dates are the same, then the amount of projects is increased, otherwise it will be reset to 1 (since the project got completed)
					if (format.format(Last).equals(format.format(Instant.now()))) {
						//same date
						Today++;
					} else {
						//different date. If the record is lower than what was archived the day before, then the record gets overridden with the amount of stuff done the day before
						if (Today > BestAmount) {
							if (getConfig().getBoolean("bake.general.doRecordSurpassBroadcast", true)) {
								this.getServer().broadcastMessage(Bake_Auxillary.ReplacePlaceHolders(replaceAdvanced(getConfig().getString("bake.chat.recordSurpassBroadcast", "ERROR: You should restart the server.")), Integer.parseInt(args[0]), getConfig().getInt("bake.wheat_Required"), -1, player.getDisplayName()));
								this.Record = Instant.ofEpochMilli(System.currentTimeMillis()-86400000);
							}
							BestAmount = Today;
						}
						Today = 1;
						ParticipantsToday = 0;
						RemindedDay = new HashMap<UUID,Boolean>();
					}
					Participants = 0;
					Last = Instant.now();
					saveValues();
					
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Replaces advanced placeholders and caches the messages, simpler placeholders (those who change frequently) are not changed
	 *
	 * @since 1.5.0
	 * @author Geolykt
	 */
	public void replaceAdvancedCached () {
	    msgProg = replaceAdvanced(getConfig().getString("bake.chat.progress2", "ERROR"));
	    
		msgContr = replaceAdvanced(getConfig().getString("bake.chat.contr2", "ERROR"));
		
		msgGlobContr = replaceAdvanced(getConfig().getString("bake.chat.global.contr2", "ERROR"));
		
		msgFin = replaceAdvanced(getConfig().getString("bake.chat.finish2", "ERROR"));
		
		msgStats = replaceAdvanced(getConfig().getString("bake.chat.bakestat", "ERROR"));
	}

	/**
	 * Like replaceAdvancedCached, but can be used to parse pretty much everything and it gets returned
	 * @param s The string to be parsed.
	 * @return The parsed string
	 * @since 1.5.0
	 * @author Geolykt
	 */
	public String replaceAdvanced(String s) {
		s = s.replaceAll("%TIMES%", String.valueOf(Times));
		s = s.replaceAll("%TODAY%", String.valueOf(Today));
		DateTimeFormatter format = DateTimeFormatter.RFC_1123_DATE_TIME.withLocale(Locale.UK)
													.withZone(ZoneId.systemDefault());
		s = s.replaceAll("%LAST%", format.format(Last));
		format = DateTimeFormatter.ISO_LOCAL_DATE.withLocale(Locale.UK)
				                                 .withZone(ZoneId.systemDefault());
		s = s.replaceAll("%RECORDDATE%", format.format(Record));
		s = s.replaceAll("%RECORD%", String.valueOf(BestAmount));
		s = s.replaceAll("%PARTICIPANTS%", String.valueOf(Participants));
		s = s.replaceAll("%PARTICIPANTSTODAY%", String.valueOf(ParticipantsToday));
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
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            Eco = economyProvider.getProvider();
        }
        useVault = Eco != null;
        return useVault;//Sets useVault to the inverse of whether the Economy is null, and returns it. 
    }
}
