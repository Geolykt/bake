package de.geolykt.bake.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import de.geolykt.bake.Bake;
import de.geolykt.bake.Bake_Auxillary;

/**
 * The Leaderboard implementation of bake.
 * @author Geolykt
 * @since 1.6.0-pre3
 */
public class Leaderboard {

	public HashMap<UUID, Integer> lbMap;
	public LinkedHashMap<UUID, Integer> sortedMap;
	private Bake backlink;
	private ArrayList<UUID> informedPlayers;
	
	public Leaderboard (Bake plugin) {
		this.backlink = plugin;
		lbMap = new HashMap<UUID, Integer>();
		informedPlayers = new ArrayList<UUID>();
	}
	
	/**
	 * Called to load the database from the files. Should be called on startup.
	 */
	public void load () {
		try {
			new File(backlink.getDataFolder().getAbsolutePath() + "\\leaderboard.bake").createNewFile();
			BufferedReader br = new BufferedReader(new FileReader(new File(backlink.getDataFolder().getAbsolutePath() + "\\leaderboard.bake")));
			String s = br.readLine();
			if (s == null) {
				br.close();
				return;
			}
			if (!s.equals("0")) {
				this.backlink.getLogger().warning("The leaderboard database has an invalid version.");
				backlink.useLeaderboard = false;
				backlink.lbHandle = null;
				br.close();
				return;
			}
			s = br.readLine();
			
			while (s != null) {
				lbMap.put(UUID.fromString(s.split("_")[0]), Integer.decode(s.split("_")[1]));
				s = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		resort();
		updateSB();
		System.gc();
	}
	

	/**
	 * Called to write the database to the files and merge them. Should be called on shutdown.
	 */
	public void save () {
		try {
			BufferedWriter bw =new BufferedWriter( new FileWriter(new File(backlink.getDataFolder().getAbsolutePath() + "\\leaderboard.bake")));
			
			bw.write("0");
			bw.newLine();

			for (UUID uuid : lbMap.keySet()) {
				bw.write(uuid.toString()+"_"+lbMap.getOrDefault(uuid,0));
				bw.newLine();
			}
			bw.close();
			System.gc();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param player The UUID of whom should the update occur
	 * @param amount The amount that should be added/removed
	 */
	public void update (UUID player, int amount) {
		amount += lbMap.getOrDefault(player, 0);
		lbMap.put(player, amount);
		resort();
		updateSB();
	}
	
	public void resort() {
		sortedMap = (LinkedHashMap<UUID, Integer>) Bake_Auxillary.sortByValue(lbMap);
	}

	private void updateSB () {
		for (UUID id: informedPlayers) {
			OfflinePlayer p = Bukkit.getOfflinePlayer(id);
			if (!p.isOnline()) {
				continue;
			}
			Objective o = ((Player) p).getScoreboard().getObjective("baketop");
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			int s = lbMap.size()-20;
			for (int i = lbMap.size()-1; i > s && (i > -1); i--){
				UUID uuid = (UUID) sortedMap.keySet().toArray()[i];
				try {
					o.getScore(Bukkit.getPlayer(uuid).getName()).setScore(lbMap.get(uuid));
				} catch (NullPointerException expected) {
					o.getScore("NPE"+i).setScore(lbMap.get(uuid));
				}
			}
		}
	}
	
	private void updatePSB (Player p) {
		Objective o = (p).getScoreboard().getObjective("baketop");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		int s = lbMap.size()-20;
		for (int i = lbMap.size()-1; i > s && (i > -1); i--){
			UUID uuid = (UUID) sortedMap.keySet().toArray()[i];
			try {
				o.getScore(Bukkit.getPlayer(uuid).getName()).setScore(lbMap.get(uuid));
			} catch (NullPointerException expected) {
				o.getScore("NPE"+i).setScore(lbMap.get(uuid));
			}
		}
	}
	
	public void informPlayer(Player p) {
		if (informedPlayers.remove(p.getUniqueId())) {
			p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			p.sendMessage("you are no longer subscribed to the bake leaderboards.");
		} else {
			final Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
			sb.registerNewObjective("baketop", "dummy", "Bake - top contributors");
			p.setScoreboard(sb);
			updatePSB(p);
			informedPlayers.add(p.getUniqueId());
			p.sendMessage("you are now subscribed to the bake leaderboards. Perform the command again to unsubscribe.");
		}
	}
}
