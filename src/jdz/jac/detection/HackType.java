package jdz.jac.detection;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.EventPriority;

import lombok.Data;
import lombok.Getter;

@Data
public class HackType {
	@Getter private static final Set<HackType> allTypes = new HashSet<HackType>();

	private final String name;
	private final String actionDescription;
	private final Severity severity;
	private final int persistanceTicks;
	private final EventPriority priority;

	public HackType(String name, String actionDescription, Severity severity, int persistanceTicks) {
		this(name, actionDescription, severity, persistanceTicks, EventPriority.MONITOR);
	}

	public HackType(String name, String actionDescription, Severity severity, int persistanceTicks,
			EventPriority priority) {
		this.name = name;
		this.actionDescription = actionDescription;
		this.severity = severity;
		this.persistanceTicks = persistanceTicks;
		this.priority = priority;
		allTypes.add(this);
	}

	public boolean isEnabled() {
		return TypeEnabledConfig.isEnabled(this);
	}
}
