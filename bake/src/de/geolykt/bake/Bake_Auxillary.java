package de.geolykt.bake;

/**
 * Library Class for the bake plugin.
 * 
 * @author Geolykt
 * @since 1.4.1
 *
 */
public class Bake_Auxillary {

	/**
	 * The version of the plugin in the MAJOR.MINOR.PATCH format.
	 * @since 1.4.1, public since 1.5.1
	 */
	public static final String PLUGIN_VERSION = "1.5.1";
	
	/**
	 * This function is in place to make it more easy for the plugin to parse the config file from an older version (config version 2) to a newer (config version 3+).
	 * 
	 * @param str String to be inserted that may not be usable by the newer code
	 * @return The new String that is usable by the newer code
	 * @since 1.4.1
	 */
	public static String NewConfig (String str) {
		// pre-1.4.1 -> post 1.4.0
		str = str.replaceAll("&VERSION;", "%VERSION%");
		str = str.replaceAll("&INTPROG;", "%INTPROG%");
		str = str.replaceAll("&PERCENT;", "%PERCENT%");
		str = str.replaceAll("&PLAYER;", "%PLAYER%");
		str = str.replaceAll("&INTMAX;", "%INTMAX%");
		// 1.4.1 -> post 1.4.1
		str = str.replaceAll("%NEWLINE%", "\n");
		return str;
	}
	
	/**
	 * Replaces basic placeholders (e.g.: "%PERCENT%") with a specified corresponding value. <br> Some placeholders like "%VERSION%" are replaced automatically. <br>
	 * NOTE: This function is candidate for deprecation
	 * 
	 * 
	 * @param s The inserted string
	 * @param progress What to replace "%INTPROG%" with
	 * @param req  What to replace "%INTMAX%" with
	 * @param prog What to replace "%PERCENT%" with
	 * @param player What to replace "%PLAYER%" with
	 * @return A String in which all placeholders have been replaced.
	 * @since 1.4.1
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
	 * returns the length of the longest String in an array
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
}
