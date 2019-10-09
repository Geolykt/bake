package de.geolykt.bake;

import de.geolykt.bake.Bake_Auxillary;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main operating class
 * Almost all functions are called in here 
 * <ul><li>
 * 1.4.0: Reworked perms <br>
 * 1.4.0: Reworked standard loot. <br>
 * 1.4.0: Reworked Commands Usage. <br>
 * 1.4.0: Added possibility to only reward those who have contributed (active by default). <br>
 * 1.4.0: Added possibility to add lores <br>
 * 1.4.0: Added possibility to add enchantments <br>
 * 1.4.0: Added possibility to change the display name <br></li><li>
 * 1.4.1: Merged bake spigot 1.13/1.14 and spigot 1.12 versions (compatible with 1.12 AND 1.13/1.14)<br>
 * 1.4.1: Added config version<br>
 * 1.4.1: Changed the way the chat config system works, it has now an entire config line allocated for multiple ingame lines. <br>
 * 1.4.1: Added config parameter "bake.general.noMeddle", if set to true, the config file won't be altered by the plugin in any way. <br>
 * </li></ul>
 * 
 * @version 4 (Exclusion Update)
 * @author Geolykt
 *
 */
public class Bake extends JavaPlugin {

	public int BakeProgress = 0;
	private  HashMap<UUID, Boolean> Reminded= new HashMap<UUID, Boolean>();
//	protected static BakeCode Code;
	
	@Override
	public void onEnable () {
		
		if (!getConfig().getBoolean("bake.general.noMeddle", false)) {
		
			getConfig().addDefault("bake.general.configVersion", 3);
		
			// Config Convert Process
			if (getConfig().getInt("bake.general.configVersion") > 3) {
				//Notify User
				getServer().getLogger().log(Level.WARNING, "The config version is newer than it should be! The plugin will try to run normal, but it might break  the config file!");
				//the code can't do anything here, pray that it will work anyway.
			}
		
			//Try to detect whether the config was used in a pre-bake 1.4.1 environment
			if (getConfig().getInt("bake.general.slots", -1) != -1) {
				//Notify User
				getServer().getLogger().log(Level.WARNING, "The config version is older than it should be! The plugin will try to update the config, but it may look strange or not work at all.");
				//TODO Check whether this was done correctly
				String s = "";
				for (int i = 0; i < getConfig().getInt("bake.general.chatslots"); i++) {
					s += getConfig().getString("bake.chat.progress." + i, "") + "%NEWLINE%";
					getConfig().set("bake.chat.progress." + i, "");
				}
				s = Bake_Auxillary.NewConfig(s);
				getConfig().addDefault("bake.chat.progress2", s);
				s = "";
				for (int i = 0; i < getConfig().getInt("bake.general.chatslots"); i++) {
					s += getConfig().getString("bake.chat.contr." + i, "") + "%NEWLINE%";
					getConfig().set("bake.chat.contr." + i, "");
				}
				s = Bake_Auxillary.NewConfig(s);
				getConfig().addDefault("bake.chat.contr2", s);
				s = "";
				for (int i = 0; i < getConfig().getInt("bake.general.chatslots"); i++) {
					s += getConfig().getString("bake.chat.global.contr." + i, "") + "%NEWLINE%";
					getConfig().set("bake.chat.global.contr." + i, "");
				}
				s = Bake_Auxillary.NewConfig(s);
				getConfig().addDefault("bake.chat.global.contr2", s);
				s = "";
				for (int i = 0; i < getConfig().getInt("bake.general.chatslots"); i++) {
					s += getConfig().getString("bake.chat.finish." + i, "") + "%NEWLINE%";
					getConfig().set("bake.chat.finish." + i, "");
				}
				s = Bake_Auxillary.NewConfig(s);
				getConfig().addDefault("bake.chat.finish2", s);
				s = null;
		}
		
			//General Stuff
			getConfig().addDefault("bake.wheat_Required", 1000);
			getConfig().addDefault("bake.general.slots", 5);
			getConfig().addDefault("bake.general.remember", true);
			getConfig().addDefault("bake.general.deleteRemembered", true);
			getConfig().addDefault("bake.general.noMeddle", false);
		
			getConfig().addDefault("bake.award.maximum", 3);
			//Loot
			getConfig().addDefault("bake.award.slot.0", "DIAMOND");
			getConfig().addDefault("bake.chances.slot.0", 0.5);
			getConfig().addDefault("bake.amount.slot.0", 2);
			getConfig().addDefault("bake.lore.slot.0", ChatColor.LIGHT_PURPLE + "Thank you for participating!");
			getConfig().addDefault("bake.name.slot.0", ChatColor.BLUE + "A DIAMOND");
			getConfig().addDefault("bake.enchantment.slot.0", "unbreaking@5");
			getConfig().addDefault("bake.award.slot.1", "CAKE");
			getConfig().addDefault("bake.chances.slot.1", 1);
			getConfig().addDefault("bake.amount.slot.1", 1);
			getConfig().addDefault("bake.lore.slot.1", ChatColor.LIGHT_PURPLE + "Thank you for participating! | Here! Have a cake!");
			getConfig().addDefault("bake.name.slot.1", ChatColor.RED + "YUMMY!");
			getConfig().addDefault("bake.enchantment.slot.1", "unbreaking@5|mending@1");
			getConfig().addDefault("bake.award.slot.2", "NETHER_STAR");
			getConfig().addDefault("bake.chances.slot.2", 0.05);
			getConfig().addDefault("bake.amount.slot.2", 1);
			getConfig().addDefault("bake.lore.slot.2", ChatColor.LIGHT_PURPLE + "Thank you for participating!");
			getConfig().addDefault("bake.name.slot.2", ChatColor.YELLOW + "Shiny!");
			getConfig().addDefault("bake.award.slot.3", "GOLD_INGOT");
			getConfig().addDefault("bake.chances.slot.3", 0.1);
			getConfig().addDefault("bake.amount.slot.3", 3);
			getConfig().addDefault("bake.lore.slot.3", ChatColor.LIGHT_PURPLE + "Thank you for participating!");
			getConfig().addDefault("bake.award.slot.4", "COAL");
			getConfig().addDefault("bake.chances.slot.4", 0.8);
			getConfig().addDefault("bake.amount.slot.4", 16);
			getConfig().addDefault("bake.lore.slot.4", ChatColor.LIGHT_PURPLE + "Thank you for participating!");
			// CHAT
			// When players use /bake
			getConfig().addDefault("bake.chat.progress2", "========= Running Bake %VERSION%  ========== %NEWLINE% The Bake Progress is: %INTPROG% of %INTMAX% %NEWLINE% So we are %PERCENT% % done! Keep up! %NEWLINE% ========================================");
			// when players use /contibute
			getConfig().addDefault("bake.chat.contr2", "%INTPROG% was added to the project! Thanks!");
			getConfig().addDefault("bake.chat.global.contr2",ChatColor.GOLD + "%PLAYER% has contributed %INTPROG% to the bake projects! We are now a bit closer to the rewards!");
			// when the bake project is finished
			getConfig().addDefault("bake.chat.finish2", ChatColor.BOLD + "" + ChatColor.AQUA + "The bake project is finsished! Everyone gets the rewards!");
		
		
		
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		BakeProgress = (int) getConfig().get("bake.wheat_Required");
	}
	
	@Override
	public void onDisable () {
		//Empty
	}
	


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("bake")) 
		{
			
			double progressPercent = (double) (-(BakeProgress - getConfig().getInt("bake.wheat_Required")) / (getConfig().getInt("bake.wheat_Required") + 0.0)*100);
			int progress = -(BakeProgress - getConfig().getInt("bake.wheat_Required") );
			
			for (String s : getConfig().getString("bake.chat.progress2", "ERROR").split("%NEWLINE%")) {
				s = Bake_Auxillary.ReplacePlaceHolders(s, progress, getConfig().getInt("bake.wheat_Required"), progressPercent, "ERROR");
				sender.sendMessage(s);
			}
			
			return true;
		} else if (cmd.getName().equalsIgnoreCase("contribute")) {
			
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
				boolean tru = false;
				
				int amountLeft = amount;
				for (int i = 0; i < player.getInventory().getSize(); i++) {
					
					try {
						if (player.getInventory().getItem(i).getType() == Material.WHEAT) {
							amountLeft -= player.getInventory().getItem(i).getAmount();
							if (amountLeft <= 0) {
								tru = true;
								break;
							}
						}
					} catch (NullPointerException npe) {
					}
				}
				
				if (tru) {//Player has enough wheat in its inventory
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

				for (String s : getConfig().getString("bake.chat.contr2", "ERROR").split("%NEWLINE%")) {
					s = Bake_Auxillary.ReplacePlaceHolders(s, Integer.parseInt(args[0]), getConfig().getInt("bake.wheat_Required"), -1, sender.getName());
					sender.sendMessage(s);
				}

				for (String s : getConfig().getString("bake.chat.global.contr2", "ERROR").split("%NEWLINE%")) {
					s = Bake_Auxillary.ReplacePlaceHolders(s, Integer.parseInt(args[0]), getConfig().getInt("bake.wheat_Required"), -1, sender.getName());
					getServer().broadcastMessage(s);
				}
				
				// REMINDING CODEth
				if (getConfig().getBoolean("bake.general.remember")) 
				{
					if (!Reminded.containsKey(player.getUniqueId())) 
					{
					//Player has not yet participated
					Reminded.put(player.getUniqueId(), true);
					}
				}
				
				
				
				// Project finished
				if (BakeProgress <= 0) {

					for (String s : getConfig().getString("bake.chat.finish2", "ERROR").split("%NEWLINE%")) {
						s = Bake_Auxillary.ReplacePlaceHolders(s, Integer.parseInt(args[0]), getConfig().getInt("bake.wheat_Required"), -1, sender.getName());
						getServer().broadcastMessage(s);
					}
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
								
								//TODO Ensure & check usability over multiple versions
								// Source for errors: Enchantment.getByKey only exists in spigot API level 13 or higher, but not in spigot API level 12 or lower!
								// So the code will check which version the server is running on and then uses the appropriate function, this is doable as Java allows the use of invalid functions within the sourcecode as long as they don't get called.
								
								//Strip Bukkit.getBukkitVersion() to only return the Bukkit API level / Minecraft Minor Version Number under the Major.Minor.Patch format.

								
								int APILevel = Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]); //Bukkit.getBukkitVersion() returns something like 1.12.2-R0.1-SNAPSHOT
								try { //prevent stupidity of the server owner
									//API 13+
									if (APILevel >= 13) {
										enchantments.put(Enchantment.getByKey(NamespacedKey.minecraft(string.split("@")[0])), Integer.valueOf(string.split("@")[1]));
									} else if (APILevel <= 12) {//API 12 or lower (some levels might  still not work)
										//This is deprecated for Bukkit 1.13 or higher, but since it doesn't get called on these versions, it is fine
										enchantments.put(Enchantment.getByName(string.split("@")[0]), Integer.valueOf(string.split("@")[1]));
									}
								} catch (NullPointerException e) {
									getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "[BAKE] ERROR PREVENTED: Please contact an administrator, if you are an administrator, stop the server and have a deep look into the config file.");
								} catch (java.lang.IllegalArgumentException e) {
									getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "[BAKE] ERROR PREVENTED: Please contact an administrator, if you are an administrator, stop the server and have a deep look into the config file.");
								}
							}
							
							items.addUnsafeEnchantments(enchantments);
							enchantments.clear();
							//send item to the players
							for (Player players : getServer().getOnlinePlayers()) {
								if (players.getInventory().firstEmpty() == -1) {
									continue;
								}
								//Server uses setting?
								if (getConfig().getBoolean("bake.general.remember")) {
									//Check whether player has yet contributed
									if (Reminded.getOrDefault(players.getUniqueId(), false)) {
										players.getInventory().setItem(players.getInventory().firstEmpty(),items);
									}
								} else {
									players.getInventory().setItem(players.getInventory().firstEmpty(),items);
								}
							
							}
							reward_count++;
						}
						
					}
					
					BakeProgress = getConfig().getInt("bake.wheat_Required");
					if (getConfig().getBoolean("bake.general.deleteRemembered")) {
						Reminded.clear();
					}
				}
			}
			return true;
		}
		return false;
	}
}
