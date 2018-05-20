
package jdz.jac.punisher;

import org.bukkit.entity.Player;

import jdz.bukkitUtils.events.Cancellable;
import jdz.bukkitUtils.events.Event;
import jdz.jac.detection.HackType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AutobanEvent extends Event implements Cancellable{
	@Getter private final Player player;
	@Getter private final HackType type;
	
	@Getter private int unbanTier = 1;
	@Getter private int days = 30;
	@Getter private boolean isPerm = false;
}
