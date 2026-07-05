package org.executable.tasknodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.function.Predicate;

@SuppressWarnings("unused")
public class SkipNode implements TaskNode {
    final int skip;
    final Predicate<Object> predicate;
    public SkipNode(int skip) {
        this.skip = skip;
        this.predicate = null;
    }
    public SkipNode(Predicate<Object> predicate, int skip) {
        this.skip = skip;
        this.predicate = predicate;
    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        if (predicate == null) {
            queue.currentTask += skip;
        }
        else if (predicate.test(current)) queue.currentTask += skip;
        return current;
    }

    @Override
    public Class<SkipNode> identity() {
        return SkipNode.class;
    }
}
