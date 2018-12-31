
package jdz.jac.logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import jdz.bukkitUtils.events.Listener;
import jdz.jac.detection.HackEvent;
import jdz.jac.punisher.AutobanEvent;

public class LoggerListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR)
	public void onHackEvent(HackEvent event) {
		Logger.getGeneral()
				.log(event.getActionDescription() + "\n\t" + event.getExtraData() + "\n\t" + event.getLoggerData());
		if (event.isCancelled())
			Logger.getGeneral().log("The hack event was cancelled by a plugin");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAutoban(AutobanEvent event) {
		String timeString = event.isPerm() ? "permanently " : "for " + event.getDays() + " days";
		Logger.getGeneral().log(
				event.getPlayer().getName() + " was autobanned " + timeString + "for " + event.getType().getName());
		if (!event.getExtraData().equals(""))
			Logger.getGeneral().log("Extra data:\t" + event.getExtraData());
		if (event.isCancelled())
			Logger.getGeneral().log("The autoban event was cancelled by a plugin");
	}
}
