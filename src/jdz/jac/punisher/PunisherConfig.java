package jdz.jac.punisher;

import org.bukkit.event.EventHandler;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.events.custom.ConfigReloadEvent;
import jdz.jac.JAC;

public class PunisherConfig implements Listener{
	
	@EventHandler
	public void onConfigReload(ConfigReloadEvent event) {
		if (event.getPlugin() != JAC.getInstance())
			return;
	}

}
