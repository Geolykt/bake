package de.geolykt.bake.util.BakeData;

import org.bukkit.entity.Player;

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

	public GlobalBake(Bake plugin) {
		super(plugin);
	}
	
	//TBA 1.6.0 Update (TODO)

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
