package tsp.tasker.paper;

import org.bukkit.plugin.java.JavaPlugin;
import tsp.tasker.api.scheduler.Schedulers;

/**
 * @author TheSilentPro (Silent)
 */
public class Tasker {

    public static void init(JavaPlugin plugin) {
        Schedulers.setAsyncScheduler(new AsyncScheduler(plugin));
        Schedulers.setSyncScheduler(new SyncScheduler(plugin));
    }

}