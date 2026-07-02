package org.executable.nodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;
import org.executable.annotations.SafeUsage;

@SuppressWarnings("unused")

@SafeUsage("Does not throw any errors by default.")
public class ExecuteNode implements TaskNode {
    final Runnable runnable;
    public ExecuteNode(Runnable runnable) {
        this.runnable = runnable;
    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        try {
            runnable.run();
        }
        catch (RuntimeException throwable) {
            queue.setError(throwable);
            return null;
        }
        return current;
    }

    @Override
    public Class<ExecuteNode> identity() {
        return ExecuteNode.class;
    }
}
