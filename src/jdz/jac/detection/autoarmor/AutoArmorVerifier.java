
package jdz.jac.detection.autoarmor;

import org.bukkit.entity.Player;

import jdz.jac.detection.HackType;
import jdz.jac.detection.Severity;

public class AutoArmorVerifier {
	private static HackType HACKTYPE_AUTO_ARMOR = new HackType("auto-armor", "equiped items too fast! (Auto-armor)", Severity.NORMAL, 400);
	
	public static void verifyGive(Player player) {
		// TODO
	}
	
	public static void verifyStrip(Player player) {
		// TODO
	}
}

