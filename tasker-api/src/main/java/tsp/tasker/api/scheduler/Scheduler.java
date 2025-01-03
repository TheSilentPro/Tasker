package tsp.tasker.api.scheduler;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a scheduler for scheduling tasks.
 *
 * @see Schedulers
 * @see Schedulers#setAsyncScheduler(Scheduler)
 * @see Schedulers#setAsyncScheduler(Scheduler)
 * @author TheSilentPro (Silent)
 */
public interface Scheduler extends Executor {

    CompletableFuture<Void> run(Runnable runnable);

    <T> CompletableFuture<T> run(Callable<T> callable);

    <T> void delayed(T value, Duration delay, Consumer<T> command);

    <T> CompletableFuture<T> delayed(Duration delay, Callable<T> callable);

    <T,U> CompletableFuture<U> delayed(T value, Duration delay, Function<T,U> function);

    <T> CompletableFuture<Void> delayed(T value, Duration delay, BiConsumer<T, Object> consumer);

    <T,U> CompletableFuture<U> delayed(T value, Duration delay, BiFunction<T,Object,U> function);

    void repeating(Duration initialDelay, Duration period, Runnable command);

    <T> CompletableFuture<T> repeating(Duration initialDelay, Duration period, Callable<T> callable);

    <T,U> CompletableFuture<U> repeating(T value, Duration initialDelay, Duration period, Function<T,U> function);

    <T> CompletableFuture<Void> repeating(T value, Duration initialDelay, Duration interval, BiConsumer<T, Object> consumer);

    <T,U> CompletableFuture<U> repeating(T value, Duration initialDelay, Duration interval, BiFunction<T,Object,U> function);

}
