package jdz.jac.detection;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import jdz.bukkitUtils.components.events.Cancellable;
import jdz.bukkitUtils.components.events.Event;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;

@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HackEvent extends Event implements Cancellable {
	@Getter private final Player player;
	@Getter private final HackType type;
	@Getter private final String extraData;
	@Getter private final String loggerData;
	@Wither @Getter private double persistanceTicksModifier = 1;
	@Wither @Getter private double severityModifier = 1;

	public HackEvent(Player player, HackType type) {
		this(player, type, "", "");
	}

	public HackEvent(Player player, HackType type, String loggerData, String... extraData) {
		this(player, type, String.join("\n", extraData), loggerData);
	}

	public String getActionDescription() {
		if (type.getActionDescription().contains("%player%"))
			return type.getActionDescription().replaceAll("%player%", player.getName());
		return player.getName() + " " + type.getActionDescription();
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
