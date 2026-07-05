package org.executable.tasknodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class EmptyIfNode implements TaskNode {
    final Predicate<Object> predicate;
    final Consumer<Object> consumer;
    final Runnable runnable;
    public EmptyIfNode(Predicate<Object> predicate, Consumer<Object> consumer) {
        this.predicate = predicate;
        this.consumer = consumer;
        this.runnable = null;
    }
    public EmptyIfNode(Predicate<Object> predicate, Runnable runnable) {
        this.predicate = predicate;
        this.runnable = runnable;
        this.consumer = null;
    }
    public EmptyIfNode(Predicate<Object> predicate) {
        this.predicate = predicate;
        this.consumer = null;
        this.runnable = null;
    }

    @Override
    public Object execute(Object current, TaskQueue queue) {
        if (predicate.test(current)) {
            if (consumer != null) {
                try {
                    consumer.accept(current);
                }
                catch (RuntimeException e) {
                    queue.setError(e);
                }
            }
            else if (runnable != null) {
                try {
                    runnable.run();
                }
                catch (RuntimeException e) {
                    queue.setError(e);
                }
            }
            return null;
        }
        return current;
    }

    @Override
    public Class<EmptyIfNode> identity() {
        return EmptyIfNode.class;
    }
}
