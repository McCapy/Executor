package org.executable.tasknodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;
import org.executable.annotations.SafeUsage;
import org.executable.annotations.ValueTask;

import java.util.function.Consumer;

@SuppressWarnings("unused")

@SafeUsage("This node does not throw any errors by default.")
@ValueTask("This node consumes the cache, setting it to null.")
public class EmptyNode implements TaskNode {
    final Consumer<Object> consumer;
    public EmptyNode(Runnable runnable) {
        this.consumer = (item) -> runnable.run();
    }
    public EmptyNode(Consumer<Object> consumer) {
        this.consumer = consumer;
    }
    public EmptyNode() { this.consumer = null; }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        try {
            consumer.accept(current);
        }
        catch (RuntimeException throwable) {
            queue.setError(throwable);
            return null;
        }
        return null;
    }

    @Override
    public Class<EmptyNode> identity() {
        return EmptyNode.class;
    }
}
