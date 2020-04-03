package de.geolykt.bake.util;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import de.geolykt.bake.Bake;
import de.geolykt.bake.Bake_Auxillary;

/**
 * String utillity class for the Bake Plugin. Not really recommended to be used by other plugins though.
 * This class mostly is used to format placeholders and other stuff, maybe it will be used when bake will support PAPI someday.
 * @author Geolykt
 * @since 1.6.0
 *
 */
public class StringUtils {
	
	private Bake bakeInstance;
	
	public StringUtils(Bake plugin) {
		bakeInstance = plugin;
		/*init raw strings*/
		BakeCommandRaw = bakeInstance.getConfig().getString("bake.chat.progress2", "N/A");
		BakeStatCommandRaw = bakeInstance.getConfig().getString("bake.chat.bakestats", "N/A");
		BakeContributionRaw_Global = bakeInstance.getConfig().getString("bake.chat.global.contr2", "N/A");
		BakeContributionRaw_Sender = bakeInstance.getConfig().getString("bake.chat.contr2", "N/A");
		BakeFinishRaw = bakeInstance.getConfig().getString("bake.chat.finish2", "N/A");
		BakeRecordRaw = bakeInstance.getConfig().getString("bake.chat.recordSurpassBroadcast", "N/A");
		leaderboard_main = bakeInstance.getConfig().getString("bake.chat.leaderboard.mid", "N/A");
		leaderboard_pre = bakeInstance.getConfig().getString("bake.chat.leaderboard.pre", "N/A");
		leaderboard_post = bakeInstance.getConfig().getString("bake.chat.leaderboard.post", "N/A");
	}

	/**
	 * Used for the /bake command. Raw string
	 * @since 1.6.0
	 */
	public String BakeCommandRaw;
	
	/**
	 * Sent to the contributor upon contribution. Raw string
	 * @since 1.6.0
	 */
	public String BakeContributionRaw_Sender;
	
	/**
	 * Sent to every online player upon contribution. Raw string
	 */
	public String BakeContributionRaw_Global;
	
	/**
	 * Sent to every online player upon the finishing of a project. Raw string
	 */
	public String BakeFinishRaw;
	
	/**
	 * Used for the /bakestats command. Raw string
	 */
	public String BakeStatCommandRaw;

	/**
	 * Done when the record is broken. Raw String.
	 */
	public String BakeRecordRaw;
	
	/**
	 * Used BEFORE the main leaderboard part is printed. <b>Does not make use of placeholders.</b>
	 * @since 1.6.0-pre3
	 */
	public String leaderboard_pre;
	
	/**
	 * The main leaderboard part. <b>Does not make use of placeholders.</b> The first %s is the player, the second the amount. Order of placeholders cannot be changed.
	 * @since 1.6.0-pre3
	 */
	public String leaderboard_main;
	
	/**
	 * Used AFTER the main leaderboard part is printed. <b>Does not make use of placeholders.</b>
	 * @since 1.6.0-pre3
	 */
	public String leaderboard_post;
	//-------------------------------------------------------------

	/**
	 * Used for the /bake command.
	 * @since 1.6.0
	 */
	public String BakeCommandString;
	
	/**
	 * Sent to the contributor upon contribution.
	 * @since 1.6.0
	 */
	public String BakeContributionString_Sender;
	
	/**
	 * Sent to every online player upon contribution.
	 */
	public String BakeContributionString_Global;
	
	/**
	 * Sent to every online player upon the finishing of a project.
	 */
	public String BakeFinishString;
	
	/**
	 * Used for the /bakestats command.
	 */
	public String BakeStatCommandString;
	
	/**
	 * Done when the record is broken
	 */
	public String BakeRecordString;
	
	
	/**
	 * reparses the cached strings based on the raw strings.
	 * @since 1.6.0
	 * @author Geolykt
	 */
	public void cacheStrings () {
		BakeCommandString = replaceString(BakeCommandRaw);
		BakeContributionString_Global = replaceString(BakeContributionRaw_Global);
		BakeContributionString_Sender = replaceString(BakeContributionRaw_Sender);
		BakeFinishString = replaceString(BakeFinishRaw);
		BakeStatCommandString = replaceString(BakeStatCommandRaw);
		BakeRecordString = replaceString(BakeRecordRaw);
	}
	
	/**
	 * Parses a specific string based on its placeholders and the values these placeholders should be.
	 * Only parses "complex" placeholders (those which don't change often)
	 * @return A string based on the input string
	 * @since 1.6.0
	 * @author Geolykt
	 * 
	 */
	public String replaceString(String s) {
		s = s.replaceAll("%VERSION%", Bake_Auxillary.PLUGIN_VERSION);
		s = s.replaceAll("%TIMES%", String.valueOf(bakeInstance.DataHandle.getOverallCompletionAmount()));
		s = s.replaceAll("%TODAY%", String.valueOf(bakeInstance.DataHandle.getProjectsFinishedToday()));
		DateTimeFormatter format = DateTimeFormatter.RFC_1123_DATE_TIME.withLocale(Locale.UK)
													.withZone(ZoneId.systemDefault());
		s = s.replaceAll("%LAST%", format.format(bakeInstance.DataHandle.getLastCompletion()));
		format = DateTimeFormatter.ISO_LOCAL_DATE.withLocale(Locale.UK)
				                                 .withZone(ZoneId.systemDefault());
		s = s.replaceAll("%RECORDDATE%", format.format(bakeInstance.DataHandle.getRecordDate()));
		s = s.replaceAll("%RECORD%", String.valueOf(bakeInstance.DataHandle.getRecordAmount()));
		s = s.replaceAll("%PARTICIPANTS%", String.valueOf(bakeInstance.DataHandle.getParticipantAmount()));
		s = s.replaceAll("%PARTICIPANTSTODAY%", String.valueOf(bakeInstance.DataHandle.getParticipantAmountToday()));
		return s;
	}
	
	/**
	 * Parses a string based on its placeholders and the values these placeholders should be.
	 * Only parses "frequent" placeholder (those who change frequently, as opposed to "complex" placeholders)
	 * @return A string based on the input string
	 * @since 1.6.0
	 * @param s The input String
	 * @param player The string that replaces %PLAYER%, can be empty
	 */
	public String replaceFrequent(String s, String player) {
		int required = bakeInstance.getConfig().getInt("bake.wheat_Required", -1);
		
		
		s = s.replaceAll("%INTPROG%", String.valueOf(bakeInstance.DataHandle.getProgress()));
		s = s.replaceAll("%INTMAX%", "" + String.valueOf(required));
		s = s.replaceAll("%PERCENT%", String.format("%2.02f",(double) (-(bakeInstance.DataHandle.getRemaining() - required) / (required + 0.0)*100)));
		s = s.replaceAll("%PLAYER%", player);
		return s;
	}
}
