package de.geolykt.bake.util.BakeData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.geolykt.bake.Bake;

/**
 * An abstract template class for chat handling when certain actions are done.
 * @since 1.6.0
 * @author Geolykt
 *
 */
public abstract class BakeData {

	protected Bake bakeInstance;
	
	/**
	 * Keeps in mind how much has been contributed, rests every shutdown of the server, but does not reset when a project is finished.<br>
	 * This may vary from implementation to implementation
	 * @since 1.6.0-pre4
	 */
	protected int totalContrib = 0;
	
	/**
	 * Stores who has contributed in the current project.<br>
	 * PlayerUUID -> Has contributed?<br>
	 * If true, then the player has contributed, if false then the player hasn't contributed.<br>
	 * It is recommended to interpret null or missing values as a false.<br>
	 * @since 1.6.0
	 */
	public HashMap<UUID, Boolean> projectReminderList = new HashMap<UUID, Boolean>();
	
	/**
	 * Stores who has contributed on the current day.<br>
	 * PlayerUUID -> Has contributed?<br>
	 * If true, then the player has contributed, if false then the player hasn't contributed.<br>
	 * It is recommended to interpret null or missing values as a false.<br>
	 * @since 1.6.0
	 */
	public HashMap<UUID, Boolean> dayReminderList = new HashMap<UUID, Boolean>();
	
	/**
	 * Stores who has contributed but not yet recieved its rewards. This may be due to the player not having inventory space or logging off.
	 * @since 1.6.0-pre1
	 */
	public ArrayList<UUID> notRewarded = new ArrayList<UUID>();
	
	/**
	 * @since 1.6.0
	 * @param plugin The instance of the bake plugin.
	 */
	public BakeData(Bake plugin) {
		notRewarded = new ArrayList<UUID>();
		bakeInstance = plugin;
	}
	
	/**
	 * Called when a specific amount (amount) of an item is donated, checks wether the player is elegible have already been done and the items already removed.
	 * @param amount The amount that was donated.
	 * @param player 
	 */
	public abstract void onContribution(int amount, Player player);
	
	/**
	 * Called when a player calls /bake.
	 * @param player The player that called the command
	 */
	public abstract void onBakeCommand(Player player);
	
	/**
	 * Called when a player calls /bakestats
	 * @param player The player that called the command
	 */
	public abstract void onBakestatsCommand(Player player);
	
	/**
	 * Get the stuff that has already been done
	 * @return The progress of the current project.
	 * @since 1.6.0
	 */
	public int getProgress() {
		return bakeInstance.getConfig().getInt("bake.wheat_Required", -1) - getRemaining();
	}

	/**
	 * Note: while in 1.6.x this is the same thing as calling the base instance directly, it will be the only way of getting this value that is consistent across 1.6.x and 1.7.x
	 * @since 1.6.0
	 * @return The remaining progress left to complete the current project.
	 */
	public int getRemaining() {
		return bakeInstance.BakeProgress;
	}
	
	/**
	 * Note: while in 1.6.x this is the same thing as calling the base instance directly, it will be the only way of getting this value that is consistent across 1.6.x and 1.7.x
	 * @return The highest amount archived.
	 * @since 1.6.0
	 */
	public int getRecordAmount() {
		return bakeInstance.BestAmount;
	}
	
	/**
	 * Note: while in 1.6.x this is the same thing as calling the base instance directly, it will be the only way of getting this value that is consistent across 1.6.x and 1.7.x
	 * @return The date of the last completion of the project.
	 * @since 1.6.0
	 */
	public Instant getLastCompletion() {
		return bakeInstance.Last;
	}
	
	/**
	 * Note: while in 1.6.x this is the same thing as calling the base instance directly, it will be the only way of getting this value that is consistent across 1.6.x and 1.7.x
	 * @return The amount of participants on the current bake project.
	 * @since 1.6.0
	 */
	public int getParticipantAmount() {
		return bakeInstance.Participants;
	}
	

	/**
	 * Note: while in 1.6.x this is the same thing as calling the base instance directly, it will be the only way of getting this value that is consistent across 1.6.x and 1.7.x
	 * @return The day the most projects were completed (that is the theory, but is not the actual case)
	 * @since 1.6.0
	 */
	public Instant getRecordDate() {
		return bakeInstance.Record;
	}
	

	/**
	 * Note: while in 1.6.x this is the same thing as calling the base instance directly, it will be the only way of getting this value that is consistent across 1.6.x and 1.7.x
	 * @return The amount of projects that were completed today.
	 * @since 1.6.0
	 */
	public int getProjectsFinishedToday() {
		return bakeInstance.Today;
	}

	/**
	 * Note: while in 1.6.x this is the same thing as calling the base instance directly, it will be the only way of getting this value that is consistent across 1.6.x and 1.7.x
	 * @return The amount of projects finished in the lifetime.
	 * @since 1.6.0
	 */
	public int getOverallCompletionAmount() {
		return bakeInstance.Times;
	}
	
	/**
	 * Note: while in 1.6.x this is the same thing as calling the base instance directly, it will be the only way of getting this value that is consistent across 1.6.x and 1.7.x
	 * @return The amount of participants that have participated today.
	 * @since 1.6.0
	 */
	public int getParticipantAmountToday() {
		return bakeInstance.ParticipantsToday;
	}
	
	/**
	 * @since 1.6.0-pre4
	 * @return The total amount of wheat that has been contributed, only resets when the server shuts down (will be changed in future updates). Note: it will no longer be reset
	 */
	public int getTotalContributed() {
		return totalContrib;
	}
	
	/**
	 * @since 1.6.0-pre4
	 * Sets the total amount of wheat that has been contributed, which only resets when the server shuts down (will be changed in future updates). Not additive.
	 */
	protected void setTotalContributed(int newVal) {
		totalContrib = newVal;
	}

	/**
	 * Keeps track of when the project is finished
	 * @since 1.6.0-pre4
	 * @return true if the project is finished, false if it is not. 
	 */
	public boolean isFinished() {
		if (getRemaining() <= 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Supplementary Function that is called when the project is finished. Does not handle things like Rewards, but resets requirements and can be used for other things.<br>
	 * Per default called just after the isFinished() function
	 * @since 1.6.0-pre4
	 */
	public void onFinish() {
		bakeInstance.BakeProgress = bakeInstance.getConfig().getInt("bake.wheat_Required", -1);
	}

	/**
	 * Handle function for the Administrator Control Panel for the Bake Plugin. Allows admins to cheat the game.
	 * @param args Arguments passed, args[0] should be set to "admin"
	 * @param sender The sender that sent the request, the sender in question should have the required permissions to use the Control Panel.
	 * @since 1.6.1
	 */
	public void adminCP(String[] args, CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "This is subcommand is unimplemented. You may want to update the add-ons to a version supporting Bake 1.6.1 or later.");
		
	}
	
	/**
	 * Override highly recommended as it will be used from 1.6.1 onwards for /bake version
	 * @return The name of the implementation, or "unknown" if the implementation doesn't support this method
	 */
	public String getImplementationName() {
		return "unknown";
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<String>();
	}
}
