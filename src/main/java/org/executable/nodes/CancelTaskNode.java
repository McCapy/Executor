package org.executable.nodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.function.Predicate;

public class CancelTaskNode implements TaskNode {
    final Predicate<Object> predicate;
    public CancelTaskNode(Predicate<Object> predicate) {
        this.predicate = predicate;
    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        try {
            if (predicate.test(current)) queue.cancel();
            return null;
        }
        catch (RuntimeException e) {
            queue.setError(e);
            return null;
        }
    }

    @Override
    public Class<CancelTaskNode> identity() {
        return CancelTaskNode.class;
    }
}
