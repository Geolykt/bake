package de.geolykt.bake.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import de.geolykt.bake.Bake_Auxillary;

/**
 * Used for metics and update checking by the Bake plugin.
 * @since 1.6.0
 * @author Geolykt
 *
 */
public class MeticsClass extends BukkitRunnable{

	/**
	 * 0x00 = All fine (proceed as normal)<br>
	 * 0x01 = First run (Warn user of the metrics)<br>
	 * 0x02 = Opt out (Don't start)<br>
	 */
	public byte State;
	
	/**
	 * @throws IllegalStateException in case MeticsClass.State isn't 0x00, 0x01 or 0x02.
	 */
	@Override
	public void run() {
		
		Bukkit.getLogger().info("[Bake] Sending metrics data and checking for new updates...");
		
		switch (State) {
		case 0x00:
			BufferedInputStream in = null;
			try {
				URI metricsServerURI = new URI("https://geolykt.de/src/bake/bakeMetrics.php?version=" + Bake_Auxillary.PLUGIN_VERSION_ID);
				URLConnection metricsServer = metricsServerURI.toURL().openConnection();
				metricsServer.connect();
				in = new BufferedInputStream(metricsServer.getInputStream());
			} catch (URISyntaxException | IOException e) {
				Bukkit.getLogger().info("An error occured while trying to send data to the metrics server. Ignoring."); // Would be strange, but don't panic
			}
			String s = "";
			for (int i = 1000; i > 0; i--) {
				try {
					int n = in.read();
					if (n == -1) {
						break;
					}
					s += (char) n;
				} catch (Exception e) {
					//Exception.
					break;
				}
			}
			try {
				if (s.split("\\$")[1].contentEquals(Bake_Auxillary.PLUGIN_VERSION)) {
					Bukkit.getLogger().info("A new (nightly) bake version is availabale.");
				} else {
					Bukkit.getLogger().info("No new updates");
				}
			} catch (Exception e) {
				Bukkit.getLogger().warning("Please check the internet connection, if it is there the update servers might be down for some time.");
			}
			try {
				in.close();
			} catch (Exception e) {
				//Exception. normal?
			}
			return;
		case 0x01:
			Bukkit.getLogger().info("Bake uses it's own metrics system. To honor privacy, it will not contact it on the first run or if \"bake.metrics.opt-out\" is set to true.");
			return;
		case 0x02:
			return;
		default:
			throw new IllegalStateException();
		}
	}
}
