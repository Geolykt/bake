package de.geolykt.bake.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import de.geolykt.bake.Bake;
import de.geolykt.bake.Bake_Auxillary;

/**
 * The Leaderboard implementation of bake.
 * @author Geolykt
 * @since 1.6.0-pre3
 */
public class Leaderboard {

	public HashMap<UUID, Integer> lbMap;
	public LinkedHashMap<UUID, Integer> SortedMap;
	private Bake backlink;
	
	public Leaderboard (Bake plugin) {
		this.backlink = plugin;
		lbMap = new HashMap<UUID, Integer>();
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
			System.gc();
		} catch (IOException e) {
			e.printStackTrace();
		}
		resort();
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
	 * @param player The uuid of whom should the update occur
	 * @param amount The amount that should be added/removed
	 */
	public void update (UUID player, int amount) {
		amount += lbMap.getOrDefault(player, 0);
		lbMap.put(player, amount);
		resort();
	}
	
	public void resort() {
		SortedMap = (LinkedHashMap<UUID, Integer>) Bake_Auxillary.sortByValue(lbMap);
	}
}
