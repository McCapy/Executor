package org.executable.nodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;
import org.executable.annotations.SafeUsage;

import java.util.function.Consumer;

@SuppressWarnings("unused")

@SafeUsage("Does not throw any errors by default.")
public class PeekNode implements TaskNode {
    final Consumer<Object> consumer;
    public PeekNode(Consumer<Object> consumer) {
        this.consumer = consumer;
    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        try {
            consumer.accept(current);
        }
        catch (RuntimeException throwable) {
            queue.setError(throwable);
            return null;
        }
        return current;
    }
    @Override
    public Class<PeekNode> identity() {
        return PeekNode.class;
    }
}
