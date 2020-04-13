package de.geolykt.bake.util.BakeData;

import org.bukkit.entity.Player;

import de.geolykt.bake.Bake;

/**
 * 
 * @since 1.6.0
 * @author Geolykt
 * The classic version of Bake. Now in the 1.6.0 BakeData format.
 */
public class LocalBake extends BakeData {

	public LocalBake(Bake plugin) {
		super(plugin);
	}

	@Override
	public void onContribution(int amount, Player player) {
		// TODO Auto-generated method stub
		
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

}
