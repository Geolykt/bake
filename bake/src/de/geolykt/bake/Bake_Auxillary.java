package de.geolykt.bake;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Library Class for the bake plugin.
 * 
 * @author Geolykt
 * @since 1.4.1
 *
 */
public class Bake_Auxillary {

	/**
	 * The version of the plugin in the MAJOR.MINOR.PATCH.ANNOTATION format.
	 * @since 1.4.1, public since 1.5.1
	 */
	public static final String PLUGIN_VERSION = "1.6.1";
	
	/**
	 * The version of the plugin in the format used by the bakeMetrics software.
	 * 0x0=unused
	 * 0x1=1.5.1
	 * 0x2=1.5.2
	 * 0x3=1.6.0-pre Releases
	 * 0x4=1.6.0 (unused?)
	 * 0x5=1.6.1
	 * @since 1.5.1
	 */
	public static final byte PLUGIN_VERSION_ID = 0x5;
	
	/**
	 * Replaces basic placeholders (e.g.: "%PERCENT%") with a specified corresponding value. <br> Some placeholders like "%VERSION%" are replaced automatically. <br>
	 * 
	 * 
	 * @param s The inserted string
	 * @param progress What to replace "%INTPROG%" with
	 * @param req  What to replace "%INTMAX%" with
	 * @param prog What to replace "%PERCENT%" with
	 * @param player What to replace "%PLAYER%" with
	 * @return A String in which all placeholders have been replaced.
	 * @since 1.4.1
	 * @deprecated  Will be removed in 1.7. Use stringUtils instead.
	 */
	public static String ReplacePlaceHolders (String s, Object progress, int req, double prog, String player) {
		s = s.replaceAll("%INTPROG%", progress.toString());
		s = s.replaceAll("%INTMAX%", "" + req);
		s = s.replaceAll("%PERCENT%", String.format("%2.02f",prog));
		s = s.replaceAll("%VERSION%", PLUGIN_VERSION);
		s = s.replaceAll("%PLAYER%", player);
		return s;
	}
	
	/**
	 * returns the length of the longest String in an array.
	 * @deprecated Not used and thus not tested in recent versions. Will be used for 1.6 (hopefully)
	 * TODO Use this
	 * 
	 * @param s Array of strings to be looked for
	 * @return The length of the longest String in the array
	 * @since 1.5.0
	 */
	public static int getLongest (String [] s) {
		int i = 0;
		for (String string : s) {
			if (string.length() > i) {
				i = string.length();
			}
		}
		return i;
	}

	/**
	 * A library function to get if a player can afford to lose <b>count</b> items of Material <b>material</b>.
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
	 * Does not check whether the player can afford to lose them!</B>
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
	 * 
	 * @param <K>
	 * @param <V>
	 * @param map
	 * @author https://stackoverflow.com/a/2581754/10466349
	 * @return
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
}
