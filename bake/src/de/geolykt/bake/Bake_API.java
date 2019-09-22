package de.geolykt.bake;

/**
 * Library Class for the bake plugin.
 * 
 * @author Geolykt
 *
 */
public class Bake_API {

	/**
	 * This function is in place to make it more easy for the plugin to parse the config file from an older version to a newer.
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
	 * This function servers the purpose of replacing the Bake reaction blocks with invidual code, useful if you want to make addons for the plugin.
	 * 
	 * @param code The BakeCode to replace
	 */
	public static void OverrideCode (BakeCode code) {
		Bake.Code = code;
		return;
	}
}
