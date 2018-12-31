
package jdz.jac.notifier;

import static org.bukkit.ChatColor.*;

import org.bukkit.command.CommandSender;

public class Messager {
	public static void plainMessage(CommandSender sender, String message) {
		sender.sendMessage(message);
	}

	public static void message(CommandSender sender, String message) {
		sender.sendMessage(format(message));
	}

	public static void error(CommandSender sender, String message) {
		message(sender, RED + message);
	}

	private static String[] format(String message) {
		String[] lines = message.split("\n");
		lines[0] = RED + "" + BOLD + "[" + GOLD + "JAC" + RED + "]" + RESET + GOLD + " " + lines[0];
		for (int i = 1; i < lines.length; i++)
			lines[i] = GOLD + lines[i];
		return lines;
	}
}
