package jdz.jac.detection;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import jdz.bukkitUtils.events.Cancellable;
import jdz.bukkitUtils.events.Event;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HackEvent extends Event implements Cancellable{
	@Getter private final Player player;
	@Getter private final HackType type;
	@Getter private String extraData = "";
	@Getter @Setter private String loggerData = "";
	@Getter private double persistanceTicksModifier = 1;
	@Getter private double severityModifier = 1;
	
	public HackEvent(Player player, HackType type, String extraData) {
		this(player, type);
		this.loggerData = extraData;
	}
	
	@Override
	public Event call() {
		if (!type.isEnabled())
			return this;
		return super.call();
	}
	
	public static HandlerList getHandlerList() {
		return getHandlers(HackEvent.class);
	}
}
