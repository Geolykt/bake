package de.geolykt.bake.util;

import org.bukkit.Bukkit;

/**
 * An enchantment name conversion library written for the bake plugin.
 * @author Geolykt
 * @since 1.5.2
 */
public class EnchantmentLib {

	/**
	 *  Converts the input string, if applicable, from a 1.12 (or earlier) compatible string to a 1.13 (or later) compatible string.
	 *  May not be 100% accurate, needs testing.
	 */
	public static String Convert12to13 (String enchant) {
		enchant = enchant.toUpperCase();
		
		//ARMOR ENCHANTS
		enchant = enchant.replaceAll("PROTECTION_ENVIRONMENTAL", "protection");
		enchant = enchant.replaceAll("PROTECTION_FIRE", "fire_protection");
		enchant = enchant.replaceAll("PROTECTION_FALL", "feather_falling");
		enchant = enchant.replaceAll("PROTECTION_EXPLOSIONS", "blast_protection");
		enchant = enchant.replaceAll("PROTECTION_PROJECTILE", "projectile_protection");
		enchant = enchant.replaceAll("OXYGEN", "respiration");
		enchant = enchant.replaceAll("WATER_WORKER", "aqua_affinity");
		enchant = enchant.replaceAll("THORNS", "thorns");
		enchant = enchant.replaceAll("DEPTH_STRIDER", "depth_strider");
		enchant = enchant.replaceAll("FROST_WALKER", "frost_walker");
		
		//TOOL ENCHANTS
		enchant = enchant.replaceAll("DAMAGE_ALL", "sharpness");
		enchant = enchant.replaceAll("DAMAGE_UNDEAD", "smite");
		enchant = enchant.replaceAll("DAMAGE_ARTHROPODS", "bane_of_arthropods");
		enchant = enchant.replaceAll("KNOCKBACK", "knockback");
		enchant = enchant.replaceAll("FIRE_ASPECT", "fire_aspect");
		enchant = enchant.replaceAll("LOOT_BONUS_MOBS", "looting");
		enchant = enchant.replaceAll("DIG_SPEED", "efficiency");
		enchant = enchant.replaceAll("SILK_TOUCH", "silk_touch");
		enchant = enchant.replaceAll("DURABILITY", "unbreaking");
		enchant = enchant.replaceAll("LOOT_BONUS_BLOCKS", "fortune");
		enchant = enchant.replaceAll("SWEEPING_EDGE", "sweeping");
		
		//(CROSS-)BOW ENCHANTS
		enchant = enchant.replaceAll("ARROW_DAMAGE", "power");
		enchant = enchant.replaceAll("ARROW_KNOCKBACK", "punch");
		enchant = enchant.replaceAll("ARROW_FIRE", "flame");
		enchant = enchant.replaceAll("ARROW_INFINITE", "infinity");
		enchant = enchant.replaceAll("MULTISHOT", "multishot");
		enchant = enchant.replaceAll("QUICK_CHARGE", "quick_charge");
		enchant = enchant.replaceAll("PIERCING", "piercing");
		
		//FISHING ENCHANTS
		enchant = enchant.replaceAll("LUCK", "luck_of_the_sea");
		enchant = enchant.replaceAll("LURE", "lure");
		
		//TRIDENT ENCHANTS
		enchant = enchant.replaceAll("LOYALTY", "loyalty");
		enchant = enchant.replaceAll("IMPALING", "impaling");
		enchant = enchant.replaceAll("CHANNELING", "channeling");
		enchant = enchant.replaceAll("RIPTIDE", "riptide");
		
		//MISC
		enchant = enchant.replaceAll("MENDING", "mending");
		enchant = enchant.replaceAll("BINDING_CURSE", "binding_curse");
		enchant = enchant.replaceAll("VANISHING_CURSE", "vanishing_curse");
		return enchant;
	}
	

	/**
	 *  Converts the input string, if applicable, from a 1.13 (or earlier) compatible string to a 1.12 (or later) compatible string.
	 *  May not be 100% accurate, needs testing.
	 */
	public static String Convert13to12 (String enchant) {
		enchant = enchant.toLowerCase();
		
		//ARMOR ENCHANTS
		enchant = enchant.replaceAll("protection", "PROTECTION_ENVIRONMENTAL");
		enchant = enchant.replaceAll("fire_protection", "PROTECTION_FIRE");
		enchant = enchant.replaceAll("feather_falling", "PROTECTION_FALL");
		enchant = enchant.replaceAll("blast_protection", "PROTECTION_EXPLOSIONS");
		enchant = enchant.replaceAll("projectile_protection", "PROTECTION_PROJECTILE");
		enchant = enchant.replaceAll("respiration", "OXYGEN");
		enchant = enchant.replaceAll("aqua_affinity", "WATER_WORKER");
		enchant = enchant.replaceAll("thorns", "THORNS");
		enchant = enchant.replaceAll("depth_strider", "DEPTH_STRIDER");
		enchant = enchant.replaceAll("frost_walker", "FROST_WALKER");
		
		//TOOL ENCHANTS
		enchant = enchant.replaceAll("sharpness", "DAMAGE_ALL");
		enchant = enchant.replaceAll("smite", "DAMAGE_UNDEAD");
		enchant = enchant.replaceAll("bane_of_arthropods", "DAMAGE_ARTHROPODS");
		enchant = enchant.replaceAll("knockback", "KNOCKBACK");
		enchant = enchant.replaceAll("fire_aspect", "FIRE_ASPECT");
		enchant = enchant.replaceAll("looting", "LOOT_BONUS_MOBS");
		enchant = enchant.replaceAll("efficiency", "DIG_SPEED");
		enchant = enchant.replaceAll("silk_touch", "SILK_TOUCH");
		enchant = enchant.replaceAll("unbreaking", "DURABILITY");
		enchant = enchant.replaceAll("fortune", "LOOT_BONUS_BLOCKS");
		enchant = enchant.replaceAll("sweeping", "SWEEPING_EDGE");
		
		//(CROSS-)BOW ENCHANTS
		enchant = enchant.replaceAll("power", "ARROW_DAMAGE");
		enchant = enchant.replaceAll("punch", "ARROW_KNOCKBACK");
		enchant = enchant.replaceAll("flame", "ARROW_FIRE");
		enchant = enchant.replaceAll("infinity", "ARROW_INFINITE");
		enchant = enchant.replaceAll("multishot", "MULTISHOT");
		enchant = enchant.replaceAll("quick_charge", "QUICK_CHARGE");
		enchant = enchant.replaceAll("piercing", "PIERCING");
		
		//FISHING ENCHANTS
		enchant = enchant.replaceAll("luck_of_the_sea", "LUCK");
		enchant = enchant.replaceAll("lure", "LURE");
		
		//TRIDENT ENCHANTS
		enchant = enchant.replaceAll("loyalty", "LOYALTY");
		enchant = enchant.replaceAll("impaling", "IMPALING");
		enchant = enchant.replaceAll("channeling", "CHANNELING");
		enchant = enchant.replaceAll("riptide", "RIPTIDE");
		
		//MISC
		enchant = enchant.replaceAll("mending", "MENDING");
		enchant = enchant.replaceAll("binding_curse", "BINDING_CURSE");
		enchant = enchant.replaceAll("vanishing_curse", "VANISHING_CURSE");
		return enchant;
	}
}
