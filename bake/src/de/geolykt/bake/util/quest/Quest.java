package de.geolykt.bake.util.quest;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Holds a quest.
 * @author Geolykt
 * @since 1.7.0
 */
public class Quest {

	/**
	 * Defines which action needs to be done in order for the quest to complete
	 * @author Geolykt
	 * @since 1.7.0
	 */
	public enum QuestType {
		INVALID,
		CONTRIBUTION
	}

	private String name = "";
	
	private QuestType questType = QuestType.INVALID;
	
	private int requirement_left;
	
	public Map<Material, Double> matches;
	
	private YamlConfiguration config;
	
	private Instant begun;

	/**
	 * Loads a quest from the specified file
	 * @param questCfg The file from which the quest should be loaded.
	 * @param questNameID The unique identifier for the plugin
	 * @throws NoSuchElementException, if the questType is invalid.
	 * @since 1.7.0
	 */
	public Quest(YamlConfiguration questCfg, String questNameID) {
		config = questCfg;
		this.name = questNameID;
		if (config.getString("quests." + name + ".type", "N/A").equalsIgnoreCase("contribution")) {
			questType = QuestType.CONTRIBUTION;
		} else {
			Bukkit.getLogger().severe("Invalid quest type:" + config.getString("quests." + name + ".type", "N/A"));
			throw new NoSuchElementException("The current build of bake does not support the specified quest type!");
		}
		setRequirement_left(config.getInt("quests." + name + ".threshold", 2147483647));
		
		//Set the Matches
		matches = new HashMap<Material, Double>();
		for (String s : config.getStringList("quests." + name + ".material")) {
			matches.put(Material.getMaterial(s.toUpperCase(Locale.ROOT).split("=")[0]), Double.valueOf(s.split("=")[1]));
		}
		
		begun = Instant.now();
	}

	/**
	 * Loads a quest from the specified file
	 * @param questCfg The file from which the quest should be loaded.
	 * @param questNameID The unique identifier for the plugin
	 * @param began The specified Instant the quest should have started, useful when storing and saving this quest externally.
	 * @throws NoSuchElementException, if the questType is invalid.
	 * @since 1.8.0
	 */
	public Quest(YamlConfiguration questCfg, String questNameID, Instant began) {
		this(questCfg,questNameID);
		begun = began;
	}
	
	public String getName() {
		return name;
	}

	public QuestType getQuestType() {
		return questType;
	}

	/**
	 * Returns the required effort that is LEFT
	 * @return the required effort that is LEFT
	 */
	public int getRequirement_left() {
		return requirement_left;
	}

	/**
	 * Manually sets the required effort that is LEFT
	 * @param newRequirement_left The required effort that is LEFT
	 */
	public void setRequirement_left(int newRequirement_left) {
		this.requirement_left = newRequirement_left;
	}
	
	/**
	 * Adds effort to the project which results in the "requirement_left" variable to DECREASE. <br>
	 * Positive values mean less work than before is required, negative mean that more work is required.<br>
	 * @param change The amount that should change
	 */
	public void addEffort (int change) {
		this.requirement_left -= change;
	}
	
	/**
	 * @return The default effort that is required without any effort put in
	 */
	public int getThreshold () {
		return config.getInt("quests." + name + ".threshold", 2147483647);
	}
	
	/**
	 * Returns the lootable of the given quest
	 * @param APILevel The highest Bukkit API Level to access.
	 * @return The lootTalbe of this quest
	 */
	public BakeLootTable getLoot(int APILevel) {
		return new BakeLootTable(config, "quests." + name + ".rewards", APILevel);
	}
	
	/**
	 * Returns a string that is the name of the successor quest name of the current quest,
	 * @return The child quest
	 * @since 1.8.0
	 */
	public List<String> getSuccessors() {
		return config.getStringList("quests." + name + ".childNode");
	}
	
	/**
	 * Returns when the Quest began.
	 * @return An Instant which should be roughly the equilavent when the quest started.
	 */
	public Instant getQuestBeginningInstant() {
		return begun;
	}
}
