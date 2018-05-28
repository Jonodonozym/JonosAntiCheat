
package jdz.jac.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import jdz.bukkitUtils.misc.ReflectionUtils;

public class PlayerPing {
	public static int getPing(Player player) {
		try {
			Class<?> craftPlayer = ReflectionUtils.getClass("entity.CraftPlayer");
			Method getHandle = craftPlayer.getMethod("getHandle");
			Object entityPlayer = getHandle.invoke(craftPlayer.cast(player));
			Field pingField = entityPlayer.getClass().getField("ping");
			return pingField.getInt(entityPlayer);
		}
		catch (ReflectiveOperationException e) {
			return 0;
		}
	}
}
