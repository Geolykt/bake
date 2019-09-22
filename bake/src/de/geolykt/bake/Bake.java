package de.geolykt.bake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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
 * 1.4.0: Added possibility to change the display name <br>
 * </li></ul>
 * 
 * @version 4 (Exclusion Update)
 * @author Geolykt
 *
 */
public class Bake extends JavaPlugin {

	public int BakeProgress = 0;
	private  HashMap<UUID, Boolean> Reminded= new HashMap<UUID, Boolean>();
	
	@Override
	public void onEnable () {
		//General Stuff
		getConfig().addDefault("bake.wheat_Required", 1000);
		getConfig().addDefault("bake.general.slots", 5);
		getConfig().addDefault("bake.general.chatslots", 4);
		getConfig().addDefault("bake.general.remember", true);
		getConfig().addDefault("bake.general.deleteRemembered", true);
		
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
		getConfig().addDefault("bake.chat.progress.0", "=====Running Bake &VERSION; by Geolykt =====");
		getConfig().addDefault("bake.chat.progress.1", "The Bake Progress is: &INTPROG; of &INTMAX; ");
		getConfig().addDefault("bake.chat.progress.2", "So we are &PERCENT; % done! Keep up!");
		getConfig().addDefault("bake.chat.progress.3", "========================================");
		// when players use /contibute
		getConfig().addDefault("bake.chat.contr.1", "&INTPROG; was added to the project! Thanks!");
		getConfig().addDefault("bake.chat.global.contr.1",ChatColor.GOLD + "&PLAYER; has contributed &INTPROG; to the bake projects! We are now a bit closer to the rewards!");
		// when the bake project is finished
		getConfig().addDefault("bake.chat.finish.1", ChatColor.BOLD + "" + ChatColor.AQUA + "The bake project is finsished! Everyone gets the rewards!");
		
		
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		BakeProgress = (int) getConfig().get("bake.wheat_Required");
	}
	
	@Override
	public void onDisable () {
		
	}
	


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("bake")) 
		{
			
			double progressPercent = (double) (-(BakeProgress - getConfig().getInt("bake.wheat_Required")) / (getConfig().getInt("bake.wheat_Required") + 0.0)*100);
			int progress = -(BakeProgress - getConfig().getInt("bake.wheat_Required") );
			
			for (int i = 0; i < getConfig().getInt("bake.general.chatslots", 0); i++) {
				String s = getConfig().getString("bake.chat.progress." + i, "NIL");
				if (s.equals("NIL")) {
					continue;
				}
				s = s.replaceAll(" &INTPROG; ", " " + progress + " ");
				s = s.replaceAll(" &INTMAX; ", " " + getConfig().getInt("bake.wheat_Required") + " ");
				s = s.replaceAll(" &PERCENT; ", " " + String.format("%2.02f",progressPercent) + " ");
				s = s.replaceAll(" &VERSION; ", " 1.4.0 ");//TODO Adapt to newest version
				sender.sendMessage(s);
			}
			/* 
			==============================
			Legacy code (removed in 1.3.0)
			==============================
			
			sender.sendMessage(ChatColor.DARK_AQUA + "=====================================================");
			double i = (double) (-(BakeProgress - getConfig().getInt("bake.wheat_Required")) / (getConfig().getInt("bake.wheat_Required") + 0.0)*100);
			sender.sendMessage("The Bake project is " + ChatColor.DARK_GREEN + String.format("%2.02f", i) + ChatColor.RESET + "% completed.");
			sender.sendMessage( -(BakeProgress - getConfig().getInt("bake.wheat_Required") ) + "/" + getConfig().getInt("bake.wheat_Required"));
			sender.sendMessage(ChatColor.DARK_AQUA + "=====================================================");
			*/
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
				
				if (tru) {
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
						} catch (NullPointerException npe) {
							//Do Nothing
						}
						
					}
					
					
				} else {
					player.sendMessage(ChatColor.RED + "You don't have the specified amount of wheat in your inventory");
					return true;
				}
				// Command executed
				for (int i = 0; i < getConfig().getInt("bake.general.chatslots", 0); i++) {
					String s = getConfig().getString("bake.chat.contr." + i, "NIL");
					if (s.equals("NIL")) {
						continue;
					}
					s = s.replaceAll("&INTPROG;", " " + Integer.parseInt(args[0]) + " ");
					s = s.replaceAll("&PLAYER;", " " + sender.getName() + " ");
					sender.sendMessage(s);
				}
				for (int i = 0; i < getConfig().getInt("bake.general.chatslots", 0); i++) {
					String s = getConfig().getString("bake.chat.global.contr." + i, "NIL");
					if (s.equals("NIL")) {
						continue;
					}
					s = s.replaceAll("&INTPROG;", " " + Integer.parseInt(args[0]) + " ");
					s = s.replaceAll("&PLAYER;", " " + sender.getName() + " ");
					getServer().broadcastMessage(s);
				}
				
				/*
				==============================
				Legacy code (removed in 1.3.0)
				==============================
					
				player.sendMessage("Command executed successful");
				*/
				
				// REMINDING CODEth
				if (getConfig().getBoolean("bake.general.remember")) 
				{
					if (!Reminded.containsKey(player.getUniqueId())) {
					//Player has not yet participated
					Reminded.put(player.getUniqueId(), true);
				}}
				
				
				
				// Project finished
				if (BakeProgress <= 0) {
					for (int i = 0; i < getConfig().getInt("bake.general.chatslots", 0); i++) {
						String s = getConfig().getString("bake.chat.finish." + i, "NIL");
						if (s.equals("NIL")) {
							continue;
						}
						sender.sendMessage(s);
					}
					/*
					==============================
					Legacy code (removed in 1.3.0)
					==============================
			
					getServer().broadcastMessage(ChatColor.AQUA + "[BAKE] The Bake Project has been completed! Enjoy your rewards!");
					*/
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
								enchantments.put(Enchantment.getByKey(NamespacedKey.minecraft(string.split("@")[0])), Integer.valueOf(string.split("@")[1]));
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
