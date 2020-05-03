package de.geolykt.bake.util.quest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.geolykt.bake.util.EnchantmentLib;

/**
 * Holds a bake lootTable
 * @author Geolykt
 * @since 1.7.0
 */
public class BakeLootTable {

	public Material items [];
	public Integer pool_amount [];
	public Float baseChances [];
	public ItemMeta itemMeta [];
	
	/**
	 * Loads the BakeLootTable from the given path of the given config file
	 * @param config The configuration
	 * @param path The path the loot table is located under.
	 * @param APILevel The highest Bukkit API level the method is allowed to execute.
	 */
	@SuppressWarnings("deprecation")
	public BakeLootTable(YamlConfiguration config, String path, int APILevel) {
		
		int length = config.getStringList(path + ".items").size();
		items = new Material [length];
		pool_amount = new Integer[length];
		itemMeta = new ItemMeta[length];
		baseChances = new Float[length];
		
		List<String> itemList = config.getStringList(path + ".items"); 
		
		for (int i = 0; i < length; i++) {
			baseChances[i] = (float) config.getDouble(path + "." + itemList.get(i) + ".baseChance", 0.0);
			pool_amount[i] = config.getInt(path + "." + itemList.get(i) + ".pool", 0);
			items[i] = Material.getMaterial(itemList.get(i));
			ItemMeta meta = (new ItemStack(items[i])).getItemMeta();
			

			//LORE
			ArrayList<String> lore = new ArrayList<String>();
			for (String string : config.getString(path + "." + itemList.get(i) + ".lore", "").split("\\|")) {
				lore.add(string);
			}
			if (lore != null) {
				meta.setLore(lore);
			}
			lore.clear();
			
			//Display name
			String itemName = config.getString(path + "." + itemList.get(i) + ".display_name", "");
			if (!itemName.equals("")) {
				meta.setDisplayName(itemName);
			}

			
			//Enchantment
			for (String string : config.getString(path + "." + itemList.get(i) + ".enchantment", "").split("\\|")) {
				if (string.equals("NIL") || string.equals("")) {
					break;
				}
				if (string.split("@").length < 2) {//Not following the "Enchantment@Level" convention
					break;
				}
				if (APILevel > 12) {
					//1.13 and above -> Enchantment by Key
					meta.addEnchant(Enchantment.getByKey(NamespacedKey.minecraft(EnchantmentLib.Convert12to13(string.split("@")[0]))), Integer.valueOf(string.split("@")[1]), true);
				} else {
					//1.12 and below -> Enchantment by Name
					meta.addEnchant(Enchantment.getByName(EnchantmentLib.Convert13to12(string.split("@")[0])), Integer.valueOf(string.split("@")[1]), true);
				}
			}
			itemMeta[i] = meta;
		}
	}
}
