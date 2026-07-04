package org.executable.tasknodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.function.Supplier;

@SuppressWarnings("unused")

public class InitialNode implements TaskNode {
    final Supplier<?> item;
    public InitialNode(Object item) {
        this.item = () -> item;
    }
    public InitialNode(Supplier<?> supplier) {
        this.item = supplier;
    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        try {
            return item.get();
        } catch (RuntimeException throwable) {
            queue.setError(throwable);
            return null;
        }

    }
    @Override
    public Class<InitialNode> identity() {
        return InitialNode.class;
    }
}
