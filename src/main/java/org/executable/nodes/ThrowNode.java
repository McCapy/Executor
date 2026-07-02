package org.executable.nodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.function.Predicate;

@SuppressWarnings("unused")
public class ThrowNode implements TaskNode {
    final RuntimeException runtimeException;
    final Predicate<Object> predicate;
    public ThrowNode(RuntimeException runtimeException) {
        this.runtimeException = runtimeException;
        this.predicate = null;
    }
    public ThrowNode(Predicate<Object> predicate, RuntimeException runtimeException) {
        this.runtimeException = runtimeException;
        this.predicate = predicate;
    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        if (predicate != null) {
            if (predicate.test(current)) queue.setError(runtimeException);
        }
        else {
            queue.setError(runtimeException);
        }
        return current;
    }

    @Override
    public Class<ThrowNode> identity() {
        return ThrowNode.class;
    }
}
