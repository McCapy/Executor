package org.executable.tasknodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")

public class ExecuteIfNode implements TaskNode {
    final Consumer<Object> consumer;
    final Function<Object,Object> function;
    final Runnable runnable;
    final Predicate<Object> predicate;
    public ExecuteIfNode(Predicate<Object> predicate, Runnable runnable) {
        this.predicate = predicate;
        this.runnable = runnable;
        this.consumer = null;
        this.function = null;
    }
    public ExecuteIfNode(Predicate<Object> predicate, Function<Object,Object> function) {
        this.predicate = predicate;
        this.function = function;
        this.consumer = null;
        this.runnable = null;
    }
    public ExecuteIfNode(Predicate<Object> predicate,Consumer<Object> consumer) {
        this.predicate = predicate;
        this.consumer = consumer;
        this.runnable = null;
        this.function = null;
    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        if (!predicate.test(current)) {
            return current;
        }
        if (consumer != null) {
            try {
                consumer.accept(current);
            }
            catch (RuntimeException runtimeException) {
                queue.setError(runtimeException);
                return null;
            }
        }
        else if (function != null) {
            try {
                return function.apply(current);
            }
            catch (RuntimeException exception) {
                queue.setError(exception);
                return null;
            }
        }
        else if (runnable != null) {
            try {
                runnable.run();
            }
            catch (RuntimeException exception) {
                queue.setError(exception);
                return null;
            }
        }
        return current;
    }

    @Override
    public Class<ExecuteIfNode> identity() {
        return ExecuteIfNode.class;
    }
}
