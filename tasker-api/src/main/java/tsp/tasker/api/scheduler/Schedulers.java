package tsp.tasker.api.scheduler;

/**
 * @author TheSilentPro (Silent)
 */
public class Schedulers {

    private static Scheduler SYNC_SCHEDULER = null;
    private static Scheduler ASYNC_SCHEDULER = null;

    public static void setAsyncScheduler(Scheduler asyncScheduler) {
        ASYNC_SCHEDULER = asyncScheduler;
    }

    public static void setSyncScheduler(Scheduler syncScheduler) {
        SYNC_SCHEDULER = syncScheduler;
    }

    public static Scheduler async() {
        if (ASYNC_SCHEDULER == null) {
            throw new IllegalStateException("Async scheduler has not been set! Schedulers#setAsyncScheduler");
        }
        return ASYNC_SCHEDULER;
    }

    public static Scheduler sync() {
        if (SYNC_SCHEDULER == null) {
            throw new IllegalStateException("Sync scheduler has not been set! Schedulers#setSyncScheduler");
        }
        return SYNC_SCHEDULER;
    }

}
