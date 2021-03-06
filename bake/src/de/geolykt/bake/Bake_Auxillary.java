package de.geolykt.bake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.geolykt.bake.util.quest.BakeLootTable;

/**
 * Library Class for the bake plugin.
 * 
 * @author Geolykt
 * @since 1.4.1
 *
 */
public class Bake_Auxillary {

	/**
	 * The version of the plugin in the MAJOR.MINOR.PATCH-TYPE format.
	 * @since 1.4.1, public since 1.5.1
	 */
	public static final String PLUGIN_VERSION = "1.9.1-SNAPSHOT";
	
	/**
	 * The version of the plugin in the format used by the bakeMetrics software.
	 * <pre>
	 * 0x00=unused
	 * 0x01=1.5.1
	 * 0x02=1.5.2
	 * 0x03=1.6.0-pre Releases
	 * 0x04=1.6.0 (while designated, it remains unused due to a bug)
	 * 0x05=1.6.1
	 * 0x06=1.6.2
	 * 0x07=1.7.0
	 * 0x08=1.8.0
	 * 0x09=1.8.1
	 * 0x0a=1.9.0
	 * 0x0b=1.9.1</pre>
	 * @since 1.5.1
	 */
	public static final byte PLUGIN_VERSION_ID = 0x0b;
	
	/**
	 * A library function to get if a player can afford to lose <b>count</b> items of Material <b>material</b>.
	 * 
	 * @param player The player that looses the items
	 * @param material The sort of item to remove
	 * @param count The amount of items to remove
	 * @return whether or not the player player has more of item than count. If the player hasn't, it returns false, else true.
	 */
	public static boolean hasEnoughItems(Player player, Material material, int count) {
		//Check whether the player has the amount of wheat in its inventory, if not, the player will be notified
		boolean hasEnoughWeat = false;
		
		int amountLeft = count;
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			
			try {
				if (player.getInventory().getItem(i).getType() == material) {
					amountLeft -= player.getInventory().getItem(i).getAmount();
					if (amountLeft <= 0) {
						hasEnoughWeat = true;
						break;
					}
				}
			} catch (NullPointerException npe) {}
		}
		return hasEnoughWeat;
	}
	
	/**
	 * A library function to remove specific items at a specific count.</br><b>
	 * Does not check whether the player can afford to lose them!</b>
	 * 
	 * @param player The player that looses the items
	 * @param item The sort of item to remove
	 * @param count The amount of items to remove
	 * @since 1.6.0
	 */
	public static void removeItem(Player player, Material item, int count) {
		int amountLeft = count;
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			if (amountLeft == 0) {
				break;
			}
			try {
				if (player.getInventory().getItem(i).getType() == item) {
					if (player.getInventory().getItem(i).getAmount() > amountLeft) {
						ItemStack is = player.getInventory().getItem(i);
						is.setAmount(player.getInventory().getItem(i).getAmount() - amountLeft);
						player.getInventory().setItem(i, is);
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
	}
	
	/**
	 * Returns how many items of a given kind a player has in its inventory.
	 * 
	 * @param player The player whose inventory should be checked
	 * @param item The Material to match against
	 * @return The amount of items that match the Material the player has in its inventory.
	 * @since 1.6.0-pre3
	 */
	public static int getItemCountInInventory(Player player, Material item) {
		int amount = 0;
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			try {
				if (player.getInventory().getItem(i).getType() == item) {
					amount += player.getInventory().getItem(i).getAmount();
				}
			} catch (NullPointerException npe) {//This exception will be thrown upon accessing empty inventory slots, this catch is required to keep the program running
				//Do Nothing
			}
		}
		return amount;
	}

	/**
	 * Removes every itemstack in a player's inventory that matches the Material item and returns the amount that was removed.
	 * 
	 * @param player The player whose inventory should be checked
	 * @param item The Material to match against
	 * @return The amount of items that were removed.
	 * @since 1.6.0-pre3
	 */
	public static int removeEverythingInInventoryMatchesItem(Player player, Material item) {
		int amount = 0;
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			try {
				if (player.getInventory().getItem(i).getType() == item) {
					amount += player.getInventory().getItem(i).getAmount();
					player.getInventory().clear(i);
				}
			} catch (NullPointerException npe) {//This exception will be thrown upon accessing empty inventory slots, this catch is required to keep the program running
				//Do Nothing
			}
		}
		return amount;
	}
	
	/**
	 * Sorts in ascending order
	 * @author https://stackoverflow.com/a/2581754/10466349
	 * @since 1.6.0-pre3
	 */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
    
    /**
     * Gives the player the given Items.
     * 
     * @param p The player receiving the Item
     * @param base The base ItemStack, the "amount" variable will be overridden and is thus irrelevant.
     * @param amount the amount of times the player should be rewarded the item specified in the base.
     * @since 1.7.0
     */
    public static void givePlayerItem (Player p, ItemStack base, Integer amount) {
    	double numStacks = amount/base.getMaxStackSize() - 1;
    	base.setAmount(base.getMaxStackSize());
    	for (int i = 0; i < numStacks; i++) {
    		amount -= base.getMaxStackSize();
    		p.getInventory().addItem(base);
    	}
    	base.setAmount(amount);
    	p.getInventory().addItem(base);
    }
    

	/**
	 * Rewards the players from the given BakeLootTable. <br>
	 * Default item reward method for 1.7 onwards.
	 * 
	 * @param players A map containing all the players and their contribution.
	 * @param table The BakeLootTable that should be used.
	 * @param threshold Required to calculate the player's rewards.
	 * @return A map with the player's UUIDs mapped to their contribution of whom the delivery of rewards failed.
	 * @since 1.7.0
	 */
	public static Map<UUID,Integer> rewardPlayers(Map<UUID,Integer> players, BakeLootTable table, int threshold) {
		ItemStack is [] = new ItemStack [table.items.length];
		//Create the ItemStack and apply itemStack metadata
		for (int i = 0; i < table.items.length; i++) {
			is [i] = new ItemStack(table.items[i]);
			is [i].setItemMeta(table.itemMeta[i]);
		}
		//TODO this may be made more efficient, perhaps iterating over the onlinePlayers first? Nonetheless, the efficiency should be debated and what the most efficient approach is
		
		//Loop through items
		for (int i = 0; i < table.items.length; i++) {
			//Chance based things
			if (Math.random()<table.baseChances[i]) {
				//cycle through online players
				for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if (players.getOrDefault(onlinePlayer.getUniqueId(),0) != 0) {
						//Get how much the player is eligible on getting & send the data to the Auxiliary
						givePlayerItem(onlinePlayer, is[i], (int) Math.round(table.pool_amount[i]/Math.max(threshold/players.get(onlinePlayer.getUniqueId()),1)));
						table.commands.forEach((node) -> node.rewardPlayer(onlinePlayer));
					}
				}
			}
		}
		table.commands.forEach((node) -> node.rewardAll());
		//Remove online players from the list, offline players should remain.
		Bukkit.getOnlinePlayers().forEach((p) -> players.remove(p.getUniqueId()));
		return players;
	}

	/**
	 * Rewards the players from the given BakeLootTable. <br>
	 * Uses the default item reward method for 1.7 onwards.<br>
	 * DOES NOT REWARD MONEY
	 * 
	 * @param players A map containing all the players and their contribution.
	 * @param table The BakeLootTable that should be used.
	 * @param threshold Required to calculate the player's rewards.
	 * @param name The quest name, used for the key of the values of the map.
	 * @return A map with the player's UUIDs mapped to their contribution of whom the delivery of rewards failed.
	 * @since 1.8.1
	 */
	public static Map<UUID,Entry<String, Integer>> rewardPlayers(Map<UUID,Integer> players, BakeLootTable table, int threshold, String name) {
		players = rewardPlayers(players, table, threshold);
		Map<UUID, Entry<String, Integer>> result = new HashMap<UUID, Map.Entry<String,Integer>>();
		
		for (Entry<UUID, Integer> e: players.entrySet()) {
			result.put(e.getKey(), new java.util.AbstractMap.SimpleEntry<String, Integer>(name, e.getValue()));
		}
		return result;
	}
	
	/**
	 * Rewards a player with the given table. Does not check whether the player is online though<br>
	 * DOES NOT REWARD MONEY
	 * 
	 * @param player The target player
	 * @param table The BakeLootTable
	 * @param threshold The threshold of the reason. Used to calculate how much the player is eligible from getting
	 * @param contrib How much the player has contributed. Used to calculate how much the player is eligible from getting
	 * @since 1.7.0
	 */
	public static void rewardPlayer(Player player, BakeLootTable table, int threshold, int contrib) {
		ItemStack is [] = new ItemStack [table.items.length];
		//Create the ItemStack and apply itemStack metadata
		for (int i = 0; i < table.items.length; i++) {
			is [i] = new ItemStack(table.items[i]);
			is [i].setItemMeta(table.itemMeta[i]);
		}
		//Loop through items
		for (int i = 0; i < table.items.length; i++) {
			//Chance based things
			if (Math.random()<table.baseChances[i]) {
				//Get how much the player is eligible on getting & send the data to the Auxiliary
				Bake_Auxillary.givePlayerItem(player, is[i], (int) Math.round(table.pool_amount[i]/(threshold/contrib)));
			}
		}
	}

	/**
	 * Removes every itemStack in a player's inventory that matches the Materials items and returns the amount that was removed multiplied with their provided value. The value is then rounded to an integer.
	 * <br> Note: in 1.7.0 an issue would occur where this method would not return correct values.
	 * 
	 * @param player The player whose inventory should be checked
	 * @param matches The Materials to match against paired with their values
	 * @return The amount of items that were removed.
	 * @since 1.7.0
	 */
	public static int removeEverythingInInventoryMatchesItems(Player player, Map<Material, Double> matches) {
		int amount = 0;
		for (Entry<Material, Double> entry :matches.entrySet()) {
			amount += (int) Math.round(entry.getValue() * removeEverythingInInventoryMatchesItem(player, entry.getKey()));
		}
		return amount;
	}
	
	
}
