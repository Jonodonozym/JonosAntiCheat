
package jdz.antifreecam;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiFreecamPlugin extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		AntiFreecamConfig.reload(this);
		Bukkit.getPluginManager().registerEvents(new AntiFreecamListener(this), this);
	}
}