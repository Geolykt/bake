package de.geolykt.bake.util.BakeData;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.time.Instant;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.geolykt.bake.Bake;

/**
 * Work in Progress.
 * 
 * @author Geolykt
 * @since 1.6.0
 * 
 *
 */
public class GlobalBake extends BakeData {
	
	/**
	 * The GlobalBake server that should be contacted.
	 * @since 1.6.0-pre4
	 */
	private String server = "";
	
	/**
	 * Maintainance thread, mostly just syncs the amount of wheat donated across servers.
	 * @since 1.6.0-pre4
	 */
	private MaintainanceThread maintainanceThread = null;
	/**
	 * Too lazy to name this variable correctly and do fundamental documentation. <br>
	 * Dictates the way Projects are considered finished in the GlobalBake ecosystem and should not be used by anyone that stumbles uppon it.
	 */
	@Deprecated
	private int int_1 = 0;

	/**
	 * Both update_server and update_client point to the server, but they have two different intentions.<br>
	 * Note: must be initialized SHOULD the StringUtils calss!
	 * @param plugin Bake instance used to link back
	 * @param update_server Updates the server (the webserver)
	 * @param update_client Updates the client (the server bukkit runs on)
	 * @param poll_intervall The amount of ticks between update polling from the maintainance "thread"
	 */
	public GlobalBake(Bake plugin, String update_server, String update_client, long poll_intervall, String notification) {
		super(plugin);
		this.server = update_server;
		maintainanceThread = new MaintainanceThread(update_client, this, notification);
		maintainanceThread.runTaskTimer(plugin, 0, poll_intervall);
		bakeInstance.forceRecordSurpassCheck("the server gods");
	}
	
	@Override
	public void onContribution(int amount, Player player) {
		URI metricsServerURI;
		try {
			int isop = 0;
			if (!player.isOp()) {
				isop = 1;
			}
			int isonline = 0;
			if (bakeInstance.getServer().getOnlineMode()) {
				isonline = 1;
			}
			metricsServerURI = new URI(server + "?name=" + player.getUniqueId().toString() + "&isop=" + isop + "&count=" + amount + "&online=" + isonline);
			URLConnection metricsServer = metricsServerURI.toURL().openConnection();
			metricsServer.connect();
//			this.bakeInstance.getLogger().info(String.valueOf(metricsServer.getInputStream().read())); //Debug code
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		this.maintainanceThread.refresh(false, false);
	}

	@Override
	public void onBakeCommand(Player player) {
		String s = this.bakeInstance.StringParser.BakeCommandString;
		s = this.bakeInstance.StringParser.replaceFrequent(s, player.getDisplayName());
		player.sendMessage(s);
	}

	@Override
	public void onBakestatsCommand(Player player) {
		String s = this.bakeInstance.StringParser.BakeStatCommandString;
		s = this.bakeInstance.StringParser.replaceFrequent(s, player.getDisplayName());
		player.sendMessage(s);
	}


	/**
	 * Keeps track of when the project is finished<br>
	 * Note: the GlobalBake and default Implementations differ from each other.
	 * @since 1.6.0-pre4
	 * @return true if the project is finished, false if it is not. 
	 */
	@Override
	public boolean isFinished() {
		return Math.floorDiv(totalContrib, bakeInstance.getConfig().getInt("bake.wheat_Required", -1)) != int_1;
	}
	
	@Override
	public void onFinish() {
		super.onFinish();
		int_1 = Math.floorDiv(totalContrib, bakeInstance.getConfig().getInt("bake.wheat_Required", -1));
	}
	
	@Override
	public int getProgress() {
		bakeInstance.getLogger().info(String.valueOf(totalContrib - (int_1*bakeInstance.getConfig().getInt("bake.wheat_Required", -1))));
		return totalContrib - (int_1*bakeInstance.getConfig().getInt("bake.wheat_Required", -1));
	}
	
	@Override
	public int getRemaining() {
		return bakeInstance.getConfig().getInt("bake.wheat_Required", -1) - getProgress();
	}
}
/**
 * Maintenance thread that syncs the amount of donated wheat across servers.
 * @author Geolykt
 * @param 1.6.0-pre4w
 *
 */
 class MaintainanceThread extends BukkitRunnable {

	 private String server;
	 private GlobalBake dataInstance;
	 private String msgUpdate;
	 
	public MaintainanceThread(String update_client, GlobalBake instance, String update_notification) {
		this.server = update_client;
		this.dataInstance = instance;
		this.msgUpdate = update_notification;
	}

	@Override
	public void run() {
		refresh(true, false);
	}
	 
	public void refresh(boolean announce, boolean save_the_record) {

		BufferedInputStream in = null;
		try {
			URI metricsServerURI = new URI(this.server);
			URLConnection metricsServer = metricsServerURI.toURL().openConnection();
			metricsServer.connect();
			in = new BufferedInputStream(metricsServer.getInputStream());
		} catch (URISyntaxException | IOException e) {
			Bukkit.getLogger().warning("An error occured while trying to send data to the update server. Panic."); // Would be strange, but don't panic
		}
		try {
			String s = "";
			int len = in.available();
			for (int i = 0; i < len; i++) {
				s += (char) in.read();
			}
			int diff = Integer.valueOf(s)-dataInstance.getTotalContributed();
			if (announce && (diff != 0)) {//there is no point on announcing changes when there are no changes.
				String newStr = dataInstance.bakeInstance.StringParser.replaceString(this.msgUpdate);
				newStr = newStr.replaceAll("%INTPROG%", String.valueOf(diff));
				newStr = dataInstance.bakeInstance.StringParser.replaceFrequent(newStr, "other servers");
				dataInstance.bakeInstance.getServer().broadcastMessage(newStr);
			}
			dataInstance.totalContrib =  Integer.valueOf(s);
			if (save_the_record) {
				short record = (short) dataInstance.getRecordAmount();
				Instant record_instant = dataInstance.getRecordDate();
				short last = (short) dataInstance.getProjectsFinishedToday();
				Instant last_instant = dataInstance.getLastCompletion();
				dataInstance.bakeInstance.forceFinish("International Effort");
				dataInstance.bakeInstance.Record = record_instant;
				dataInstance.bakeInstance.BestAmount = record;
				dataInstance.bakeInstance.Last = last_instant;
				dataInstance.bakeInstance.Today = last;
				
			} else if (dataInstance.isFinished()) {
				dataInstance.bakeInstance.forceFinish("International Effort");
			}
		} catch (Exception e) {
			Bukkit.getLogger().warning("[BAKE] Something unexpected occured. Here is the stacktrace and report that to the developer.");
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (Exception e) {
			//Exception. normal?
		}
	}
 }
