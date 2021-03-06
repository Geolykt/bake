package de.geolykt.bake.util.BakeData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.geolykt.bake.Bake;
import de.geolykt.bake.Bake_Auxillary;
import de.geolykt.bake.util.quest.Quest;

/**
 * 
 * @since 1.6.0
 * @author Geolykt
 * The classic version of Bake. Now in the 1.6.0 BakeData format.
 */
public class LocalBake extends BakeData {

	public LocalBake(Bake plugin) {
		super(plugin);
		totalContrib = plugin.getConfig().getInt("bake.save.all", -1);
		new QuestCleanerTask(this).runTask(plugin);
	}

	@Override
	public void onContribution(int amount, Player player) {
		totalContrib+=amount;
		
		amount += projectReminderList.getOrDefault(player.getUniqueId(), 0);
		projectReminderList.put(player.getUniqueId(), amount);
	}

	@Override
	public void onBakeCommand(Player player) {
		String s = bakeInstance.StringParser.BakeCommandString;
		s = s.replaceAll("%TOOLTIP%", bakeInstance.StringParser.getFormattedTooltip(activeQuest.getRawTooltip(), player.getDisplayName()));
		s = bakeInstance.StringParser.replaceFrequent(s, player.getDisplayName());
		player.sendMessage(s);
	}

	@Override
	public void onBakestatsCommand(Player player) {
		String s = this.bakeInstance.StringParser.BakeStatCommandString;
		s = this.bakeInstance.StringParser.replaceFrequent(s, player.getDisplayName());
		player.sendMessage(s);
	}

	@Override
	public void adminCP(String[] args, CommandSender sender) {
		if (args.length == 1) {
			adminPanelHelp(sender);
		} else {
			if (args[1].equalsIgnoreCase("help") || args[1].equalsIgnoreCase("?")) {
				adminPanelHelp(sender);
			} else if (args[1].equalsIgnoreCase("add")) {
				if (args.length == 2) {
					sender.sendMessage(ChatColor.AQUA + "Format: /bake admin [add <amount>]");
				} else {
					try {
						activeQuest.addEffort(Integer.parseInt(args[2]));;
						sender.sendMessage(ChatColor.DARK_GREEN + "Nudged " + args[2] + " towards the project.");
					} catch(Exception e) {
						sender.sendMessage(ChatColor.AQUA + "Format: /bake admin [add <amount>]");
						return;
					}
					if (isFinished()) {
						bakeInstance.forceFinish("nudge");;
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Unknown subcommand.");
			}
		}
	}
	
	/**
	 * Class library function. Just displays the help menu.
	 * @param sender The CommandSender that will receive the information
	 */
	private void adminPanelHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Bake admin panel help:");
		sender.sendMessage(ChatColor.AQUA + "/bake admin: displays this help tab");
		sender.sendMessage(ChatColor.AQUA + "/bake admin [help|?]: displays this help tab");
		sender.sendMessage(ChatColor.AQUA + "/bake admin [add <amount>]: adds progress to the project");
	}

	@Override
	public String getImplementationName() {
		return "default_local";
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("admin help");
		list.add("admin ?");
		list.add("admin add");
		return list;
	}
	
	@Override
	public void onFinish() {
		for (Entry<UUID, Entry<String, Integer>> entry : notRewarded.entrySet()) {
			Player p = Bukkit.getPlayer(entry.getKey());
			if (p.isOnline()) {
				bakeInstance.givePlayerMoney(p,activeQuest.getEcoRewardAmount(entry.getValue().getValue()));
			}
		}
		notRewarded.putAll(Bake_Auxillary.rewardPlayers(projectReminderList, activeQuest.getLoot(bakeInstance.API_LEVEL), activeQuest.getThreshold(), activeQuest.getName()));
		newQuest();
	}
	
	@Override
	public int getRemaining() {
		return activeQuest.getRequirement_left();
	}

	/**
	 * Method to handle the timeout of quests
	 * 
	 * @since 1.9.0
	 */
	protected void taskCleanup() {
		long diff = System.currentTimeMillis()-activeQuest.getQuestBeginningInstant().toEpochMilli();
		if (diff > QuestCfg.getLong("questConfig.timeOutQuestsAfter", 86400000)) {
			newStartQuest();
		}
		new QuestCleanerTask(this).runTaskLater(bakeInstance, (-diff+QuestCfg.getLong("questConfig.timeOutQuestsAfter", 86400000))/50);
	}
	
	/**
	 * Starts a new quest, independent from it's successor
	 * 
	 * @since 1.9.0
	 */
	private void newStartQuest() {
		List <String> quests = QuestCfg.getStringList("quests.names");
		bakeInstance.getLogger().info("[BAKE] Choosing new quest. Quests available: " + quests.toString());
		int questID = (int) Math.round(Math.random()*(quests.size()-1));
		activeQuest = new Quest(QuestCfg, quests.get(questID));
	}
} 
/**
 * BukkitRunnable to handle the timeout of quests
 * 
 * @since 1.9.0
 */
class QuestCleanerTask extends BukkitRunnable {

	private final LocalBake qmgr;
	
	public QuestCleanerTask(LocalBake questManager) {
		qmgr = questManager;
	}
	
	@Override
	public void run() {
		qmgr.taskCleanup();
	}
	
}
