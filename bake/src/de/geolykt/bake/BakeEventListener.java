package de.geolykt.bake;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
			Bake_Auxillary.rewardPlayer(event.getPlayer(), instance.DataHandle.activeQuest.getLoot(instance.API_LEVEL), instance.DataHandle.activeQuest.getThreshold(), instance.DataHandle.notRewarded.getOrDefault(event.getPlayer().getUniqueId(), 0));
			
        	instance.DataHandle.notRewarded.remove(event.getPlayer().getUniqueId());
        	instance.rewardPlayer(event.getPlayer());
        	event.getPlayer().sendMessage(msg);
		}
    }
}
