package de.geolykt.bake.util;

import org.bstats.bukkit.Metrics;
import org.bukkit.scheduler.BukkitRunnable;

import de.geolykt.bake.Bake;

/**
 * Used for metrics and update checking by the Bake plugin.
 * @since 1.6.0, last revision: 1.9.1
 * @author Geolykt
 *
 */
public class MeticsClass extends BukkitRunnable {
	
	public Bake plugin;
	
	@Override
	public void run() {
		Metrics metrics = new Metrics(plugin, 7279);
		metrics.addCustomChart(new Metrics.SimplePie("amount_of_wheat_contributed", plugin.metricsWheatAmount()));
		metrics.addCustomChart(new Metrics.SimplePie("bakedata_implementations",() -> plugin.DataHandle.getImplementationName()));
	}
}
