package org.executable.nodes;

import org.executable.Executor;
import org.executable.TaskNode;
import org.executable.TaskQueue;

@SuppressWarnings("unused")


public class MergeNode implements TaskNode {

    final Executor<?>[] executors;
    public MergeNode(Executor<?>... executors) {
        this.executors = executors;

    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        return null;
    }

    @Override
    public Class<MergeNode> identity() {
        return MergeNode.class;
    }
}
