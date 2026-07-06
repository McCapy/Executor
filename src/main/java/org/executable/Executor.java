package org.executable;

import org.executable.tasknodes.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked", "unused"})

public record Executor<T>(TaskQueue queue) {

    public Executor() {
        this(new TaskQueue());
        queue.addTask(new InitialNode(() -> null));
    }

    public Executor(T initial) {
        this(new TaskQueue());
        queue.addTask(new InitialNode(initial));
    }

    public Executor(Supplier<T> supplier) {
        this(new TaskQueue());
        queue.addTask(new InitialNode(supplier));
    }

    public boolean isStarted() {
        return queue.isStarted();
    }

    public boolean isCancelled() {
        return queue.isCancelled();
    }

    public boolean isCompleted() {
        return queue.isCompleted();
    }

    public Executor<T> start() {
        queue.start();
        return this;
    }

    public T join() {
        return cast(queue.join());
    }

    public T join(long ms) {
        return cast(queue.join(ms));
    }

    public void cancel() {
        queue.cancel();
    }

    public <R> Executor<R> loop(int iterations, Function<T, R> function) {
        queue.addTask(new LoopNode((Function<Object, Object>) function, iterations));
        return new Executor<>(queue);
    }

    public <R> Executor<R> loop(Function<T, R> function, int iterations) {
        queue.addTask(new LoopNode((Function<Object, Object>) function, iterations));
        return new Executor<>(queue);
    }

    public Executor<T> loop(int loops, int tasksAhead) {
        queue.addTask(new LoopNode(loops,tasksAhead));
        return new Executor<>(queue);
    }

    public <R> Executor<R> map(Function<T, R> function) {
        queue.addTask(new MapNode((Function<Object, Object>) function));
        return new Executor<>(queue);
    }

    public <R> Executor<R> offer(Supplier<R> supplier) {
        queue.addTask(new InitialNode(supplier));
        return new Executor<>(queue);
    }

    public <R> Executor<R> offer(R initial) {
        queue.addTask(new InitialNode(initial));
        return new Executor<>(queue);
    }

    public Executor<T> peek(Consumer<T> consumer) {
        queue.addTask(new PeekNode((Consumer<Object>) consumer));
        return this;
    }

    public <R> Executor<T> fork(Function<T, Executor<R>> function) {
        ForkNode<R> node = new ForkNode<>((Function<Object, ? extends Executor<?>>) function);
        queue.addSideTask(node);
        queue.addTask(node);
        return this;
    }

    public <R> Executor<T> fork(Executor<R> executor) {
        ForkNode<R> node = new ForkNode<>(executor);
        queue.addSideTask(node);
        queue.addTask(node);
        return this;
    }

    public Executor<T> gather(Consumer<Object[]> consumer) {
        queue.addTask(new GatherNode(consumer));
        return this;
    }

    public Executor<T> gather() {
        queue.addTask(new GatherNode());
        return this;
    }

    public <R> Executor<R> gather(Function<Object[], R> function) {
        queue.addTask(new GatherNode((Function<Object[], Object>) function));
        return new Executor<>(queue);
    }

    public <R> Executor<R> filter(Predicate<T> predicate, R def) {
        queue.addTask(new FilterNode((Predicate<Object>) predicate, def));
        return new Executor<>(queue);
    }

    public <R> Executor<R> filter(Predicate<T> predicate, Supplier<R> def) {
        queue.addTask(new FilterNode((Predicate<Object>) predicate, def));
        return new Executor<>(queue);
    }

    public Executor<Void> empty(Consumer<T> consumer) {
        queue.addTask(new EmptyNode((Consumer<Object>) consumer));
        return new Executor<>(queue);
    }

    public Executor<Void> empty(Runnable runnable) {
        queue.addTask(new EmptyNode(runnable));
        return new Executor<>(queue);
    }

    public Executor<Void> empty() {
        queue.addTask(new EmptyNode());
        return new Executor<>(queue);
    }

    public Executor<T> execute(Runnable runnable) {
        queue.addTask(new ExecuteNode(runnable));
        return this;
    }

    public Executor<T> catchError(Consumer<RuntimeException> consumer) {
        queue.addTask(new ErrorCatchNode(consumer));
        return this;
    }

    public Executor<T> catchError(Runnable runnable) {
        queue.addTask(new ErrorCatchNode(runnable));
        return this;
    }

    public Executor<T> catchError() {
        queue.addTask(new ErrorCatchNode());
        return this;
    }

    public <R> Executor<R> catchError(Consumer<RuntimeException> consumer, R def) {
        queue.addTask(new ErrorCatchNode(consumer,def));
        return new Executor<>(queue);
    }

    public <R> Executor<R> catchError(Runnable runnable, R def) {
        queue.addTask(new ErrorCatchNode(runnable,def));
        return new Executor<>(queue);
    }

    public <R> Executor<R> catchError(R def) {
        queue.addTask(new ErrorCatchNode(() -> {}, def));
        return new Executor<>(queue);
    }

    public <R> Executor<R> catchError(Supplier<R> def) {
        queue.addTask(new ErrorCatchNode(() -> {}, def));
        return new Executor<>(queue);
    }

    public <R> Executor<R> catchError(Consumer<RuntimeException> consumer, Supplier<R> def) {
        queue.addTask(new ErrorCatchNode(consumer,def));
        return new Executor<>(queue);
    }

    public <R> Executor<R> catchError(Runnable runnable, Supplier<R> def) {
        queue.addTask(new ErrorCatchNode(runnable,def));
        return new Executor<>(queue);
    }

    public Executor<T> throwing(RuntimeException exception) {
        queue.addTask(new ThrowNode((item) -> true, exception));
        return this;
    }

    public Executor<T> throwIf(Predicate<T> predicate, RuntimeException exception) {
        queue.addTask(new ThrowNode((Predicate<Object>) predicate, exception));
        return this;
    }

    public Executor<T> cancelIf(Predicate<T> predicate) {
        queue.addTask(new CancelTaskNode((Predicate<Object>) predicate));
        return this;
    }

    public <R> Executor<R> mapIf(Predicate<T> predicate, Function<T, R> function) {
        queue.addTask(new ExecuteIfNode((Predicate<Object>) predicate, (Function<Object, Object>) function));
        return new Executor<>(queue);
    }

    public Executor<T> peekIf(Predicate<T> predicate, Consumer<T> consumer) {
        queue.addTask(new ExecuteIfNode((Predicate<Object>) predicate, (Consumer<Object>) consumer));
        return this;
    }

    public Executor<T> executeIf(Predicate<T> predicate, Runnable runnable) {
        queue.addTask(new ExecuteIfNode((Predicate<Object>) predicate, runnable));
        return this;
    }

    public <R> Executor<R> emptyIf(Predicate<T> predicate, Consumer<T> consumer) {
        queue.addTask(new EmptyIfNode((Predicate<Object>) predicate, (Consumer<Object>) consumer));
        return new Executor<>(queue);
    }

    public <R> Executor<R> emptyIf(Predicate<T> predicate, Runnable runnable) {
        queue.addTask(new EmptyIfNode((Predicate<Object>) predicate,runnable));
        return new Executor<>(queue);

    }

    public <R> Executor<R> emptyIf(Predicate<T> predicate) {
        queue.addTask(new EmptyIfNode((Predicate<Object>) predicate));
        return new Executor<>(queue);
    }

    public <R> Executor<T> independentFork(Function<T, Executor<R>> executorFunction) {
        queue.addTask(new ForkIndependentNode((Function<Object, ? extends Executor<?>>) executorFunction));
        return this;
    }

    public <R> Executor<T> independentFork(Executor<R> executor) {
        queue.addTask(new ForkIndependentNode(executor));
        return this;
    }

    public Executor<T> delay(long ms) {
        queue.addTask(new DelayNode(ms));
        return this;
    }

    public Executor<T> delayIf(Predicate<T> predicate, long ms) {
        queue.addTask(new DelayNode((Predicate<Object>) predicate, ms));
        return this;
    }

    public Executor<T> delayIf(long ms, Predicate<T> predicate) {
        queue.addTask(new DelayNode((Predicate<Object>)  predicate,ms));
        return this;
    }

    public Executor<T> onCancel(Runnable runnabl) {
        queue.setCancelEvent(runnabl);
        return this;
    }

    //public Executor<?> race(Executor<?>... executors) {
    //    List<Executor<?>> executorList = Arrays.asList(executors);
    //    executorList.addFirst(this);
    //    queue.addTask(new RaceNode(executorList.toArray(Executor<?>[]::new)));
    //    return new Executor<>(queue);
    //}

    //public Executor<?> race(long delay, Executor<?>... executors) {
    //    List<Executor<?>> executorList = Arrays.asList(executors);
    //    executorList.addFirst(this);
    //    queue.addTask(new RaceNode(delay,executorList.toArray(Executor<?>[]::new)));
    //    return new Executor<>(queue);
    //}

    public <R> Executor<R> addNode(TaskNode node) {
        queue.addTask(node);
        return new Executor<>(queue);
    }

    public <R> Executor<T> addSideNode(ForkNode<R> node) {
        queue.addSideTask(node);
        return this;
    }

    public Executor<T> skipIf(Predicate<T> predicate, int skips) {
        queue.addTask(new SkipNode((Predicate<Object>) predicate,skips));
        return this;
    }

    public Executor<T> skip(int skips) {
        queue.addTask(new SkipNode(skips));
        return this;
    }

    public Executor<T> doWhile(Predicate<T> predicate, int tasksAhead) {
        queue.addTask(new WhileNode((Predicate<Object>) predicate,tasksAhead));
        return new Executor<>(queue);
    }

    public Executor<T> catchRetry(int max) {
        queue.addTask(new RetryNode(max));
        return this;
    }

    public <R> Executor<R> catchRetry(int max, R def) {
        queue.addTask(new RetryNode((item) -> {},max,def));
        return new Executor<>(queue);
    }

    public Executor<T> catchRetry(Consumer<RuntimeException> consumer,int max) {
        queue.addTask(new RetryNode(consumer,max));
        return this;
    }

    public <R> Executor<R> catchRetry(Consumer<RuntimeException> consumer, int max, R def) {
        queue.addTask(new RetryNode(consumer,max,def));
        return new Executor<>(queue);
    }

    <X> X cast(X obj) {
        return obj;
    }
}
