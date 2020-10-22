package de.geolykt.bake.util.quest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Represents a command reward that is executed when the quest is finished by the console.
 * @since 1.9.0
 */
public class CommandRewardNode {

	private final String cmd;
	private final Collection<String> cmdArgs;
	private final CommandRewardRecieverModifier type;
	
	/**
	 * Constructs a command reward node via the command and it's arguments,
	 *  the arguments may only contain the %PLAYER% placeholder.
	 * @param command The command name to be executed
	 * @param args The argument
	 * @param type How often the command is invoked
	 * @since 1.9.0
	 */
	public CommandRewardNode(String command,
			String[] args, CommandRewardRecieverModifier modifier) {
		cmd = command;
		cmdArgs = Arrays.asList(args);
		type = modifier;
	}
	
	private String collectAll() {
		StringBuilder b = new StringBuilder();
		cmdArgs.forEach(b::append);
		return b.toString();
	}

	private String collectAll(String playerName) {
		StringBuilder b = new StringBuilder();
		cmdArgs.forEach((s) -> 
			b.append(s.toLowerCase(Locale.ROOT).equals("%player%") ? playerName : s));
		return b.toString();
	}
	
	public void rewardAll() {
		if (type == CommandRewardRecieverModifier.ONCE) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd + collectAll());
		}
	}
	
	public void rewardPlayer(Player p) {
		if (type == CommandRewardRecieverModifier.FOREACH) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd + collectAll(p.getName()));
		}
	}
	
	public CommandRewardRecieverModifier getTargets() {
		return type;
	}
}
