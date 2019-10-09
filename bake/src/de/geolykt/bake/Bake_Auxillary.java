package de.geolykt.bake;

/**
 * Library Class for the bake plugin.
 * 
 * @author Geolykt
 *
 */
public class Bake_Auxillary {

	private static final String PLUGIN_VERSION = "1.4.1"; 
	/**
	 * This function is in place to make it more easy for the plugin to parse the config file from an older version (config version 2) to a newer (config version 3+).
	 * 
	 * @param str String to be inserted that may not be usable by the newer code
	 * @return The new String that is usable by the newer code
	 */
	public static String NewConfig (String str) {
		str = str.replaceAll("&VERSION;", "%VERSION%");
		str = str.replaceAll("&INTPROG;", "%INTPROG%");
		str = str.replaceAll("&PERCENT;", "%PERCENT%");
		str = str.replaceAll("&PLAYER;", "%PLAYER%");
		str = str.replaceAll("&INTMAX;", "%INTMAX%");
		return str;
	}
	
	/**
	 * Replaces all placeholders (e.g.: "%PERCENT%") with a specified corresponding value. <br> Some placeholders like "%VERSION%" are replaced automatically.
	 * 
	 * 
	 * @param s The inserted string
	 * @param progress What to replace "%INTPROG%" with
	 * @param req  What to replace "%INTMAX%" with
	 * @param prog What to replace "%PERCENT%" with
	 * @param player What to replace "%PLAYER%" with
	 * @return A String in which all placeholders have been replaced.
	 */
	public static String ReplacePlaceHolders (String s, int progress, int req, double prog, String player) {
		s = s.replaceAll("%INTPROG%", "" + progress);
		s = s.replaceAll("%INTMAX%", "" + req);
		s = s.replaceAll("%PERCENT%", String.format("%2.02f",prog));
		s = s.replaceAll("%VERSION%", PLUGIN_VERSION);
		s = s.replaceAll("%PLAYER%", player);
		return s;
	}
}
