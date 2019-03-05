
package jdz.jac.punisher;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import jdz.bukkitUtils.components.events.Cancellable;
import jdz.bukkitUtils.components.events.Event;
import jdz.jac.detection.HackType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AutobanEvent extends Event implements Cancellable {
	private final Player player;
	private final HackType type;

	private final int unbanTier;
	private final int days;
	private final boolean isPerm;

	private String extraData;

	public static HandlerList getHandlerList() {
		return getHandlers(AutobanEvent.class);
	}
}
