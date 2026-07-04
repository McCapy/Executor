package org.executable.tasknodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.function.Function;

@SuppressWarnings("unused")

public class MapNode implements TaskNode {
    final Function<Object, Object> function;
    public MapNode(Function<Object, Object> fn) {
        this.function = fn;
    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        try {
            return function.apply(current);
        }
        catch (RuntimeException throwable) {
            queue.setError(throwable);
            return null;
        }
    }
    @Override
    public Class<MapNode> identity() {
        return MapNode.class;
    }
}
