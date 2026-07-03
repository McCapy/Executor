package org.executable.tasknodes;

import org.executable.Executor;
import org.executable.TaskNode;
import org.executable.TaskQueue;
import org.executable.annotations.SafeUsage;
import org.executable.annotations.ValueTask;
import org.executable.annotations.VolatileUsage;

import java.util.function.Function;

@SuppressWarnings({"unused", "unchecked", "rawtypes"})
@ValueTask("It depends")
@VolatileUsage("It depends")
@SafeUsage("It depends")
public final class ForkNode<R> implements TaskNode {

    final Function<Object, Executor<?>> function;
    Executor<R> executor;

    public Executor<R> getTask() {
        return executor;
    }

    public ForkNode(Function<Object, ? extends Executor<?>> executorFunction) {
        this.function = (Function<Object, Executor<?>>) executorFunction;
        this.executor = null;
    }

    public ForkNode(Executor<?> executor) {
        this.function = (item) -> executor;
        this.executor = null;
    }

    @Override
    public Object execute(Object current, TaskQueue queue) {
        try {
            this.executor = (Executor<R>) function.apply(current).start();
        } catch (RuntimeException e) {
            queue.setError(e);
            return null;
        }
        return current;
    }

    @Override
    public Class<ForkNode> identity() {
        return ForkNode.class;
    }
}
