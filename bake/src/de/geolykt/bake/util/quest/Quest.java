package de.geolykt.bake.util.quest;

import java.util.ArrayList;
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
	
	private YamlConfiguration config;
	
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
	 * Returns an ArrayList of Materials that are accepted by this Quest. Donating/destroying (depending on the questType) these Materials should count towards the effort.
	 * @return An ArrayList of Materials
	 */
	public ArrayList<Material> getAcceptedMaterialsList () {
		ArrayList<Material> listOfAcceptedMaterials = new ArrayList<Material>();
		for (String s : config.getStringList("quest." + name + ".material")) {
			listOfAcceptedMaterials.add(Material.getMaterial(s.split("=")[0]));
		}
		return listOfAcceptedMaterials;
	}
	
	/**
	 * Returns the worth of a material as a float.
	 * @return The worth of the material as a float.
	 */
	public float getMaterialWorth(Material m) {
		String materialName = m.toString();
		for (String s : config.getStringList("quest." + name + ".material")) {
			if (s.split("=")[0].equalsIgnoreCase(materialName)) {
				return Float.parseFloat(s.split("=")[1]);
			}
		}
		return 0.0f;
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
	public LootTable getLoot(int APILevel) {
		return new LootTable(config, "quests." + name + ".rewards", APILevel);
	}
}
