package tsp.tasker.api.promise;

import tsp.tasker.api.scheduler.Scheduler;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.function.*;

public class PromiseImpl<T> implements Promise<T> {

    private final CompletableFuture<T> future;
    private final Scheduler scheduler;

    public PromiseImpl(CompletableFuture<T> future, Scheduler scheduler) {
        this.future = future;
        this.scheduler = scheduler;
    }

    @Override
    public Promise<T> sync(Scheduler syncScheduler) {
        return new PromiseImpl<>(this.future, syncScheduler);
    }

    @Override
    public Promise<T> async(Scheduler asyncScheduler) {
        return new PromiseImpl<>(this.future, asyncScheduler);
    }

    @Override
    public Promise<Void> thenRun(Runnable runnable) {
        CompletableFuture<Void> runFuture = this.future.thenRunAsync(runnable, scheduler);
        return new PromiseImpl<>(runFuture, scheduler);
    }

    @Override
    public Promise<Void> thenAccept(Consumer<T> consumer) {
        CompletableFuture<Void> acceptFuture = this.future.thenAcceptAsync(consumer, scheduler);
        return new PromiseImpl<>(acceptFuture, scheduler);
    }

    @Override
    public <U> Promise<U> thenApply(Function<T, U> function) {
        CompletableFuture<U> applyFuture = this.future.thenApplyAsync(function, scheduler);
        return new PromiseImpl<>(applyFuture, scheduler);
    }

    @Override
    public <U, R> Promise<R> thenCombine(Promise<U> other, BiFunction<T, U, R> combiner) {
        CompletableFuture<R> combinedFuture = this.future.thenCombineAsync(other.future(), combiner, scheduler);
        return new PromiseImpl<>(combinedFuture, scheduler);
    }

    @Override
    public <U> Promise<U> compose(Function<T, CompletionStage<U>> function) {
        CompletableFuture<U> composedFuture = this.future.thenComposeAsync(function, scheduler);
        return new PromiseImpl<>(composedFuture, scheduler);
    }

    @Override
    public Promise<Void> thenAfter(Duration delay, Consumer<T> consumer) {
        return new PromiseImpl<>(this.future.thenAcceptAsync(value -> scheduler.delayed(value, delay, consumer), scheduler), scheduler);
    }

    @Override
    public Promise<T> thenAfter(Duration delay, Callable<T> callable) {
        CompletableFuture<T> delayedFuture = this.future.thenComposeAsync(result -> {
            try {
                return scheduler.delayed(delay, callable);
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
        }, scheduler);
        return new PromiseImpl<>(delayedFuture, scheduler);
    }

    @Override
    public <U> Promise<U> thenAfter(Duration delay, Function<T, U> function) {
        CompletableFuture<U> delayedFuture = this.future.thenComposeAsync(result -> {
            try {
                return scheduler.delayed(result, delay, function);
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
        }, scheduler);
        return new PromiseImpl<>(delayedFuture, scheduler);
    }

    @Override
    public Promise<Void> thenAfter(Duration delay, BiConsumer<T, Object> consumer) {
        return new PromiseImpl<>(this.future.thenAcceptAsync(value -> scheduler.delayed(value, delay, consumer), scheduler), scheduler);
    }

    @Override
    public <U> Promise<U> thenAfter(Duration delay, BiFunction<T, Object, U> function) {
        CompletableFuture<U> delayedFuture = this.future.thenComposeAsync(result -> {
            try {
                return scheduler.delayed(result, delay, function);
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
        }, scheduler);
        return new PromiseImpl<>(delayedFuture, scheduler);
    }

    @Override
    public Promise<Void> thenRepeat(Duration delay, Duration interval, Runnable runnable) {
        CompletableFuture<Void> repeatedFuture = this.future.thenRunAsync(() -> scheduler.repeating(delay, interval, runnable), scheduler);
        return new PromiseImpl<>(repeatedFuture, scheduler);
    }

    @Override
    public Promise<T> thenRepeat(Duration delay, Duration interval, Callable<T> callable) {
        CompletableFuture<T> repeatedFuture = this.future.thenComposeAsync(result -> scheduler.repeating(delay, interval, callable), scheduler);
        return new PromiseImpl<>(repeatedFuture, scheduler);
    }

    @Override
    public <U> Promise<U> thenRepeat(Duration delay, Duration interval, Function<T,U> function) {
        CompletableFuture<U> repeatedFuture = this.future.thenComposeAsync(result -> scheduler.repeating(result, delay, interval, function), scheduler);
        return new PromiseImpl<>(repeatedFuture, scheduler);
    }

    @Override
    public Promise<Void> thenRepeat(Duration delay, Duration interval, BiConsumer<T, Object> consumer) {
        CompletableFuture<Void> repeatedFuture = this.future.thenComposeAsync(result -> scheduler.repeating(result, delay, interval, consumer), scheduler);
        return new PromiseImpl<>(repeatedFuture, scheduler);
    }

    @Override
    public <U> Promise<U> thenRepeat(Duration delay, Duration interval, BiFunction<T, Object, U> function) {
        CompletableFuture<U> repeatedFuture = this.future.thenComposeAsync(result -> scheduler.repeating(result, delay, interval, function), scheduler);
        return new PromiseImpl<>(repeatedFuture, scheduler);
    }

    @Override
    public Promise<T> exceptionally(Function<Throwable, T> handler) {
        CompletableFuture<T> handledFuture = this.future.exceptionallyAsync(handler, scheduler);
        return new PromiseImpl<>(handledFuture, scheduler);
    }

    @Override
    public Promise<T> assertThat(Predicate<T> predicate, Throwable throwable) {
        CompletableFuture<T> assertedFuture = this.future.thenApplyAsync(value -> {
            if (predicate.test(value)) {
                return value;
            } else {
                throw new CompletionException(throwable);
            }
        }, scheduler);
        return new PromiseImpl<>(assertedFuture, scheduler);
    }

    @Override
    public Promise<T> abortIf(Predicate<T> predicate, boolean interrupt) {
        CompletableFuture<T> abortedFuture = scheduler.run(() -> {
            if (predicate.test(this.future.get())) {
                future.cancel(interrupt);
                return null;
            } else {
                return this.future.get();
            }
        });
        return new PromiseImpl<>(abortedFuture, scheduler);
    }

    @Override
    public T join() {
        return this.future.join();
    }

    @Override
    public CompletableFuture<T> future() {
        return future;
    }

    @Override
    public Scheduler scheduler() {
        return scheduler;
    }

}