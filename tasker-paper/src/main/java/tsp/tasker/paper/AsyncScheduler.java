package tsp.tasker.paper;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import tsp.tasker.api.scheduler.Scheduler;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author TheSilentPro (Silent)
 */
public class AsyncScheduler implements Scheduler {

    private final JavaPlugin plugin;
    private final BukkitScheduler scheduler;

    public AsyncScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
    }

    @Override
    public void execute(@NotNull Runnable command) {
        scheduler.runTaskAsynchronously(plugin, command);
    }

    @Override
    public CompletableFuture<Void> run(Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        scheduler.runTaskAsynchronously(plugin, () -> {
            try {
                runnable.run();
                future.complete(null);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });
        return future;
    }

    @Override
    public <T> CompletableFuture<T> run(Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        scheduler.runTaskAsynchronously(plugin, () -> {
            try {
                future.complete(callable.call());
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });
        return future;
    }

    @Override
    public <T> void delayed(T value, Duration delay, Consumer<T> command) {
        scheduler.runTaskLaterAsynchronously(plugin, () -> command.accept(value), delay.toMillis() / 50);
    }

    @Override
    public <T> CompletableFuture<T> delayed(Duration delay, Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        scheduler.runTaskLaterAsynchronously(plugin, () -> {
            try {
                future.complete(callable.call());
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        }, delay.toMillis() / 50);
        return future;
    }


    @Override
    public <T, U> CompletableFuture<U> delayed(T value, Duration delay, Function<T, U> function) {
        CompletableFuture<U> future = new CompletableFuture<>();
        scheduler.runTaskLaterAsynchronously(plugin, () -> {
            try {
                future.complete(function.apply(value));
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        }, delay.toMillis() / 50);
        return future;
    }

    @Override
    public <T> CompletableFuture<Void> delayed(T value, Duration delay, BiConsumer<T, Object> consumer) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        scheduler.runTaskLaterAsynchronously(plugin, task -> {
            try {
                consumer.accept(value, task);
                future.complete(null);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        }, delay.toMillis() / 50);
        return future;
    }

    @Override
    public <T, U> CompletableFuture<U> delayed(T value, Duration delay, BiFunction<T, Object, U> function) {
        CompletableFuture<U> future = new CompletableFuture<>();
        scheduler.runTaskLaterAsynchronously(plugin, task -> {
            try {
                future.complete(function.apply(value, task));
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        }, delay.toMillis() / 50);
        return future;
    }

    @Override
    public void repeating(Duration initialDelay, Duration period, Runnable command) {
        scheduler.runTaskTimerAsynchronously(plugin, command, initialDelay.toMillis() / 50, period.toMillis() / 50);
    }

    @Override
    public <T> CompletableFuture<T> repeating(Duration initialDelay, Duration period, Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        scheduler.runTaskTimerAsynchronously(plugin, () -> {
            try {
                future.complete(callable.call());
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        }, initialDelay.toMillis() / 50, period.toMillis() / 50);
        return future;
    }


    @Override
    public <T, U> CompletableFuture<U> repeating(T value, Duration initialDelay, Duration period, Function<T, U> function) {
        CompletableFuture<U> future = new CompletableFuture<>();
        scheduler.runTaskTimerAsynchronously(plugin, () -> {
            try {
                future.complete(function.apply(value));
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        }, initialDelay.toMillis() / 50, period.toMillis() / 50);
        return future;
    }

    @Override
    public <T> CompletableFuture<Void> repeating(T value, Duration initialDelay, Duration interval, BiConsumer<T, Object> consumer) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        scheduler.runTaskTimerAsynchronously(plugin, task -> {
            try {
                consumer.accept(value, task);
                future.complete(null);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        }, initialDelay.toMillis() / 50, interval.toMillis() / 50);
        return future;
    }

    @Override
    public <T, U> CompletableFuture<U> repeating(T value, Duration initialDelay, Duration interval, BiFunction<T, Object, U> function) {
        CompletableFuture<U> future = new CompletableFuture<>();
        scheduler.runTaskTimerAsynchronously(plugin, task -> {
            try {
                future.complete(function.apply(value, task));
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        }, initialDelay.toMillis() / 50, interval.toMillis() / 50);
        return future;
    }

}