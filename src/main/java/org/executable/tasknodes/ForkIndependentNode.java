package org.executable.tasknodes;

import org.executable.Executor;
import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.function.Function;

@SuppressWarnings("unused")
public class ForkIndependentNode implements TaskNode {
    final Function<Object, ? extends Executor<?>> function;

    public ForkIndependentNode(Executor<?> executor) {
        this.function = (item) -> executor;
    }

    public ForkIndependentNode(Function<Object, ? extends Executor<?>> function) {
        this.function = function;
    }

    @Override
    public Object execute(Object current, TaskQueue queue) {
        try {
            function.apply(current).start();
        } catch (RuntimeException e) {
            queue.setError(e);
            return null;
        }
        return current;
    }

    @Override
    public Class<ForkIndependentNode> identity() {
        return ForkIndependentNode.class;
    }
}
