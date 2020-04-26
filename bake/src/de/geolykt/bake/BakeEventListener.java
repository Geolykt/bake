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
        if (this.instance.DataHandle.notRewarded.contains(event.getPlayer().getUniqueId())) {
        	this.instance.DataHandle.notRewarded.remove(event.getPlayer().getUniqueId());
        	instance.rewardPlayer(event.getPlayer());
        	event.getPlayer().sendMessage(msg);
        }
    }
}
