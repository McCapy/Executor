package org.executable.nodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.function.Function;

@SuppressWarnings("unused")
public class LoopNode implements TaskNode {
    final Function<Object,Object> function;
    final long iterations;
    public LoopNode(Function<Object,Object> function, long loop) {
        this.function = function;
        this.iterations = loop;
    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        try {
            for (long i = 0; i < iterations; i++) {
                try {
                    current = function.apply(current);
                }
                catch (RuntimeException e) {
                    queue.setError(e);
                    current = null;
                    break;
                }
            }
        }
        catch (RuntimeException e) {
            queue.setError(e);
            return null;
        }
        return current;
    }

    @Override
    public Class<LoopNode> identity() {
        return LoopNode.class;
    }
}
