package tsp.tasker.api.promise;

import tsp.tasker.api.scheduler.Scheduler;
import tsp.tasker.api.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.*;

public interface Promise<T> {

    static <T> Promise<T> start() {
        return new PromiseImpl<>(new CompletableFuture<>(), Schedulers.async());
    }

    static <T> Promise<T> startSync() {
        return new PromiseImpl<>(new CompletableFuture<>(), Schedulers.sync());
    }

    static <T> Promise<T> start(T value) {
        return new PromiseImpl<>(CompletableFuture.completedFuture(value), Schedulers.async());
    }

    static <T> Promise<T> startSync(T value) {
        return new PromiseImpl<>(CompletableFuture.completedFuture(value), Schedulers.sync());
    }

    static <T> Promise<T> await(Callable<T> callable) {
        return new PromiseImpl<>(Schedulers.async().run(callable), Schedulers.async());
    }

    static Promise<Void> await(Runnable runnable) {
        return new PromiseImpl<>(Schedulers.async().run(runnable), Schedulers.async());
    }

    static <T> Promise<T> awaitSync(Callable<T> callable) {
        return new PromiseImpl<>(Schedulers.sync().run(callable), Schedulers.sync());
    }

    static Promise<Void> awaitSync(Runnable runnable) {
        return new PromiseImpl<>(Schedulers.sync().run(runnable), Schedulers.sync());
    }

    // Schedulers

    Promise<T> sync(Scheduler syncScheduler);

    Promise<T> async(Scheduler asyncScheduler);

    default Promise<T> sync() {
        return sync(Schedulers.sync());
    }

    default Promise<T> async() {
        return async(Schedulers.async());
    }

    // Executors

    Promise<Void> thenRun(Runnable runnable);

    default Promise<Void> then(Runnable runnable) {
        return thenRun(runnable);
    }

    Promise<Void> thenAccept(Consumer<T> consumer);

    default Promise<Void> then(Consumer<T> consumer) {
        return thenAccept(consumer);
    }

    <U> Promise<U> thenApply(Function<T, U> function);

    default <U> Promise<U> then(Function<T, U> function) {
        return thenApply(function);
    }

    <U, R> Promise<R> thenCombine(Promise<U> other, BiFunction<T, U, R> combiner);

    default <U, R> Promise<R> then(Promise<U> other, BiFunction<T, U, R> combiner) {
        return thenCombine(other, combiner);
    }

    <U> Promise<U> compose(Function<T, CompletionStage<U>> function);

    default Promise<Void> thenAfter(Duration delay, Runnable runnable) {
        return thenAfter(delay, ignored -> {runnable.run();});
    }

    Promise<Void> thenAfter(Duration delay, Consumer<T> consumer);

    Promise<T> thenAfter(Duration delay, Callable<T> callable);

    <U> Promise<U> thenAfter(Duration delay, Function<T, U> function);

    Promise<Void> thenAfter(Duration delay, BiConsumer<T, Object> consumer);

    <U> Promise<U> thenAfter(Duration delay, BiFunction<T, Object, U> function);

    Promise<Void> thenRepeat(Duration delay, Duration interval, Runnable runnable);

    default Promise<Void> thenRepeat(Duration interval, Runnable runnable) {
        return thenRepeat(Duration.ZERO, interval, runnable);
    }

    Promise<T> thenRepeat(Duration delay, Duration interval, Callable<T> callable);

    default Promise<T> thenRepeat(Duration interval, Callable<T> callable) {
        return thenRepeat(Duration.ZERO, interval, callable);
    }

    <U> Promise<U> thenRepeat(Duration delay, Duration interval, Function<T, U> function);

    default <U> Promise<U> thenRepeat(Duration interval, Function<T, U> function) {
        return thenRepeat(Duration.ZERO, interval, function);
    }

    Promise<Void> thenRepeat(Duration delay, Duration interval, BiConsumer<T, Object> consumer);

    default Promise<Void> thenRepeat(Duration interval, BiConsumer<T, Object> consumer) {
        return thenRepeat(Duration.ZERO, interval, consumer);
    }

    <U> Promise<U> thenRepeat(Duration delay, Duration interval, BiFunction<T, Object, U> function);

    default <U> Promise<U> thenRepeat(Duration interval, BiFunction<T, Object, U> function) {
        return thenRepeat(Duration.ZERO, interval, function);
    }

    Promise<T> exceptionally(Function<Throwable, T> handler);

    Promise<T> assertThat(Predicate<T> predicate, Throwable throwable);

    default Promise<T> assertThat(Predicate<T> predicate) {
        return assertThat(predicate, new RuntimeException("Predicate test failed!"));
    }

    Promise<T> abortIf(Predicate<T> predicate, boolean interrupt);

    default Promise<T> abortIf(Predicate<T> predicate) {
        return abortIf(predicate, true);
    }

    // Converters

    T join();

    CompletableFuture<T> future();

    Scheduler scheduler();

}