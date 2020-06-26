package de.geolykt.bake;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.geolykt.bake.util.quest.Quest;

public class BakeEventListener implements Listener {

	private Bake instance;
	private String msg;
	
    public BakeEventListener(Bake bake, String welcomeback_message) {
    	this.instance = bake;
    	this.msg = welcomeback_message;
	}

	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
		if (instance.DataHandle.notRewarded.containsKey(event.getPlayer().getUniqueId())) {
			Quest temp = new Quest(instance.DataHandle.QuestCfg, instance.DataHandle.notRewarded.get(event.getPlayer().getUniqueId()).getKey());
			int contrib = instance.DataHandle.notRewarded.getOrDefault(event.getPlayer().getUniqueId(), new java.util.AbstractMap.SimpleEntry<String,Integer>(null, 0)).getValue();
			Bake_Auxillary.rewardPlayer(event.getPlayer(), temp.getLoot(instance.API_LEVEL), temp.getThreshold(), contrib);
			
        	instance.DataHandle.notRewarded.remove(event.getPlayer().getUniqueId());
        	instance.givePlayerMoney(event.getPlayer(), temp.getEcoRewardAmount(contrib));
        	event.getPlayer().sendMessage(msg);
		}
    }
}
