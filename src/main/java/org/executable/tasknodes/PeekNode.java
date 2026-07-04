package org.executable.tasknodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.function.Consumer;

@SuppressWarnings("unused")

public class PeekNode implements TaskNode {
    Consumer<Object> consumer;
    public PeekNode(Consumer<Object> objectConsumer) {
        this.consumer = objectConsumer;
    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        try {
            consumer.accept(current);
        } catch (RuntimeException throwable) {
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
