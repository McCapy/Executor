package org.executable.nodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;
import org.executable.annotations.SafeUsage;
import org.executable.annotations.ValueTask;

import java.util.function.Function;

@SuppressWarnings("unused")

@SafeUsage("Does not throw any errors by default.")
@ValueTask(
        "Transforms the original value into " +
        "a new value, which can be a different " +
        "type of class or the same type of class."
)
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
