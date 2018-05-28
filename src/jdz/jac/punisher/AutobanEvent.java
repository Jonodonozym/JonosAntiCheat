
package jdz.jac.punisher;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import jdz.bukkitUtils.events.Cancellable;
import jdz.bukkitUtils.events.Event;
import jdz.jac.detection.HackType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = true)
public class AutobanEvent extends Event implements Cancellable{
	@Getter private final Player player;
	@Getter private final HackType type;
	
	@Getter private final int unbanTier;
	@Getter private final int days;
	@Getter private final boolean isPerm;
	
	public static HandlerList getHandlerList() {
		return getHandlers(AutobanEvent.class);
	}
}
