package de.geolykt.bake.util.BakeData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.geolykt.bake.Bake;
import de.geolykt.bake.Bake_Auxillary;
import de.geolykt.bake.util.quest.Quest;

/**
 * An abstract template class for chat handling when certain actions are done.
 * @since 1.6.0
 * @author Geolykt
 *
 */
public abstract class BakeData {

	protected Bake bakeInstance;
	
	public YamlConfiguration QuestCfg;
	
	/**
	 * Keeps in mind how much has been contributed, rests every shutdown of the server, but does not reset when a project is finished.<br>
	 * This may vary from implementation to implementation
	 * @since 1.6.0-pre4
	 */
	protected int totalContrib = 0;
	
	/**
	 * <b> Changed in 1.7.0 to a HashMap<UUID, Integer>!</b>
	 * Stores who has contributed how much in the current project.<br>
	 * PlayerUUID -> Has contributed how much?<br>
	 * If greater to 0, then the player has contributed, if 0 or below then the player hasn't contributed.<br>
	 * It is recommended to interpret null or missing values as a 0 or below.<br>
	 * @since 1.6.0.<b> Changed in 1.7.0 to a HashMap<UUID, Integer>!</b>
	 */
	public HashMap<UUID, Integer> projectReminderList = new HashMap<UUID, Integer>();
	
	/**
	 * Stores who has contributed on the current day.<br>
	 * PlayerUUID -> Has contributed?<br>
	 * If true, then the player has contributed, if false then the player hasn't contributed.<br>
	 * It is recommended to interpret null or missing values as a false.<br>
	 * @since 1.6.0
	 */
	public HashMap<UUID, Boolean> dayReminderList = new HashMap<UUID, Boolean>();
	
	/**
	 * <b> Changed in 1.7.0 to a HashMap<UUID, Integer>!</b>
	 * Stores who has contributed how much in the current project.<br>
	 * PlayerUUID -> Has contributed how much?<br>
	 * If greater to 0, then the player has contributed, if 0 or below then the player hasn't contributed.<br>
	 * It is recommended to interpret null or missing values as a 0 or below.<br>
	 * Stores who has contributed but not yet recieved its rewards. This may be due to the player not having inventory space or logging off.
	 * @since 1.6.0-pre1, <b> Changed in 1.7.0 to a HashMap<UUID, Integer>!</b>
	 */
	public HashMap<UUID, Integer> notRewarded = new HashMap<UUID, Integer>();
	
	/**
	 * Adds the contribution, which results in the BakeProgress value to DECREASE! <br>
	 * Does not do further tests, like whether the Project is finished due to the operation
	 * @param amount The amount to ADD
	 */
	public void addContribution(int amount) {
		activeQuest.addEffort(amount);
	}
	
	/**
	 * The Participants of the current project
	 * @since 1.7.0
	 */
	protected byte Participants = 0;
	
	/**
	 * <b>UNUSED</b> <br>
	 * The number of participants today
	 * @since 1.7.0
	 * @deprecated Use is unknown
	 */
	protected byte ParticipantsToday = 0;
	
	/**
	 * Sets the number of today's participants
	 * @param p
	 * @since 1.7.0
	 */
	public void setParticipantsToday (byte p) {
		ParticipantsToday = p;
	}
	
	/**
	 * The projects finished today
	 * @since 1.7.0
	 */
	protected short Today = 0;
	
	/**
	 * Sets the number of the project's participants
	 * @param p new value
	 * @since 1.7.0
	 */
	public void setParticipantCount (byte p) {
		Participants = p;
	}
	
	/**
	 * The projects finished up to date
	 * @since 1.7.0
	 */
	protected short Times = 0;
	
	/**
	 * Manually sets the requirements that are LEFT in order for the quest to complete.
	 * @param bakeProgress
	 * @since 1.7.0
	 */
	public void setBakeProgress(int bakeProgress) {
		activeQuest.setRequirement_left(bakeProgress);
	}

	/**
	 * @param today the today to set
	 */
	public void setProjectsFinishedToday(short today) {
		Today = today;
	}

	/**
	 * @param times the times to set
	 */
	public void setTimes(short times) {
		Times = times;
	}

	/**
	 * @param bestAmount the bestAmount to set
	 */
	public void setBestAmount(short bestAmount) {
		BestAmount = bestAmount;
	}

	/**
	 * @param last the last to set
	 */
	public void setLastCompletion(Instant last) {
		Last = last;
	}

	/**
	 * @param record the record to set
	 */
	public void setRecord(Instant record) {
		Record = record;
	}

	/**
	 * The most projects finished in a day
	 * @since 1.7.0
	 */
	protected short BestAmount = 0;
	
	/**
	 * The last time a project was completed
	 * @since 1.7.0
	 */
	protected Instant Last = Instant.EPOCH;
	
	/**
	 * The day the most projects were finished
	 * @since 1.7.0
	 */
	protected Instant Record = Instant.EPOCH;
	
	/**
	 * @since 1.6.0
	 * @param plugin The instance of the bake plugin.
	 */
	public BakeData(Bake plugin) {
		bakeInstance = plugin;
	}
	
	/**
	 * Called when a specific amount (amount) of an item is donated, checks whether the player is eligible have already been done and the items already removed.<br>
	 * Does not change the progress in the base application! <br>
	 * 
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
	 * Called when something that is not a player calls /bake.
	 * @param sender The commandSender that called the action
	 * @since 1.8.0
	 * @throws IllegalArgumentException When the sender is of the Player instance
	 */
	public void onBakeCommandByNonPlayer(CommandSender sender) {
		if (sender instanceof Player) {
			throw new IllegalArgumentException("Invalid command sender. The command sender is a player while the method does not expect it to be a player.");
		}
		String s = this.bakeInstance.StringParser.BakeCommandString;
		s = bakeInstance.StringParser.getFormattedTooltip(activeQuest.getRawTooltip(), "");
		s = this.bakeInstance.StringParser.replaceFrequent(s, "");
		sender.sendMessage(s);
	}
	
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
		return activeQuest.getThreshold() - getRemaining();
	}

	/**
	 * Note: while in 1.6.x this is the same thing as calling the base instance directly, it will be the only way of getting this value that is consistent across 1.6.x and 1.7.x
	 * @since 1.6.0
	 * @return The remaining progress left to complete the current project.
	 */
	public int getRemaining() {
		return activeQuest.getRequirement_left();
	}
	
	/**
	 * Note: while in 1.6.x this is the same thing as calling the base instance directly, it will be the only way of getting this value that is consistent across 1.6.x and 1.7.x
	 * @return The highest amount archived.
	 * @since 1.6.0
	 */
	public int getRecordAmount() {
		return BestAmount;
	}
	
	/**
	 * Note: while in 1.6.x this is the same thing as calling the base instance directly, it will be the only way of getting this value that is consistent across 1.6.x and 1.7.x
	 * @return The date of the last completion of the project.
	 * @since 1.6.0
	 */
	public Instant getLastCompletion() {
		return Last;
	}
	
	/**
	 * Note: while in 1.6.x this is the same thing as calling the base instance directly, it will be the only way of getting this value that is consistent across 1.6.x and 1.7.x
	 * @return The amount of participants on the current bake project.
	 * @since 1.6.0
	 */
	public int getParticipantAmount() {
		return Participants;
	}
	

	/**
	 * Note: while in 1.6.x this is the same thing as calling the base instance directly, it will be the only way of getting this value that is consistent across 1.6.x and 1.7.x
	 * @return The day the most projects were completed (that is the theory, but is not the actual case)
	 * @since 1.6.0
	 */
	public Instant getRecordDate() {
		return Record;
	}
	

	/**
	 * Note: while in 1.6.x this is the same thing as calling the base instance directly, it will be the only way of getting this value that is consistent across 1.6.x and 1.7.x
	 * @return The amount of projects that were completed today.
	 * @since 1.6.0
	 */
	public int getProjectsFinishedToday() {
		return Today;
	}

	/**
	 * Note: while in 1.6.x this is the same thing as calling the base instance directly, it will be the only way of getting this value that is consistent across 1.6.x and 1.7.x
	 * @return The amount of projects finished in the lifetime.
	 * @since 1.6.0
	 */
	public int getOverallCompletionAmount() {
		return Times;
	}
	
	/**
	 * Note: while in 1.6.x this is the same thing as calling the base instance directly, it will be the only way of getting this value that is consistent across 1.6.x and 1.7.x
	 * @return The amount of participants that have participated today.
	 * @since 1.6.0
	 */
	public int getParticipantAmountToday() {
		return ParticipantsToday;
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
	 * <b> IMPORTANT: </b> <br>
	 * Following Bake <b>1.7.0</b>, this method is the backbone of the plugin and handles almost all background stuff.<br>
	 * This includes: resetting the requirements, awarding the rewards and handling other stuff.<br>
	 * <hr>
	 * Per default called just after the isFinished() function
	 * @since 1.6.0-pre4, last revision: 1.7.0
	 */
	public void onFinish() {
		this.notRewarded.putAll(Bake_Auxillary.rewardPlayers(this.projectReminderList, activeQuest.getLoot(Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1])), activeQuest.getThreshold()));
		newQuest();
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

	/**
	 * @since 1.6.2
	 * @param sender The sender
	 * @param command The command
	 * @param alias The alias used
	 * @param args Arguments passed
	 * @return A list with all the possible completions.
	 */
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<String>();
	}


	/**
	 * The currently active quest
	 * @since 1.7.0
	 */
	public Quest activeQuest = null;
	
	/**
	 * Creates a new quest and resets the "activeQuest" variable
	 * @since 1.7.0, last revision: 1.8.0
	 */
	public void newQuest() {
		List <String> quests;
		if (activeQuest == null) {
			quests = QuestCfg.getStringList("quests.names");
		} else {
			quests = activeQuest.getSuccessors();
			if (quests == null) {
				quests = QuestCfg.getStringList("quests.names");
			}
		}
		bakeInstance.getLogger().info("[BAKE] Choosing new quest. Quests available: " + quests.toString());
		int questID = (int) Math.round(Math.random()*(quests.size()-1));
		activeQuest = new Quest(QuestCfg, quests.get(questID));
	}

	/**
	 * Creates a new quest with the specified name. If the quest was not found, it defaults to the default newQuest() without arguments.
	 * @param name the name of the quest
	 * @since 1.8.0
	 */
	public void newQuest(String name) {
		//Prevent NullPointerExcpetions
		if (name == null) {
			bakeInstance.getLogger().info("[Bake] Null name for new generated quest. Falling back to default quest selection.");
			newQuest();
			return;
		}
		
		if (QuestCfg.getString("quests." + name + ".type", "N/A").equals("N/A")) {
			bakeInstance.getLogger().info("[Bake] Invalid name (" + name + ") for new generated quest. Falling back to default quest selection.");
			//Invalid quest name
			newQuest();
		} else {
			//Valid quest name
			activeQuest = new Quest(QuestCfg, name);
		}
	}
}
