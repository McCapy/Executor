package org.executable.tasknodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")
public class LoopNode implements TaskNode {
    final Function<Object,Object> function;
    final int iterations;
    final int ahead;
    public LoopNode(Function<Object,Object> function, int loop) {
        this.function = function;
        this.iterations = loop;
        this.ahead = 0;
    }
    public LoopNode(int loop,int ahead) {
        this.function = null;
        this.iterations = loop;
        this.ahead = ahead;
    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        if (function != null) {
            try {
                for (long i = 0; i < iterations; i++) {
                    try {
                        current = function.apply(current);
                    } catch (RuntimeException e) {
                        queue.setError(e);
                        current = null;
                        break;
                    }
                }
            } catch (RuntimeException e) {
                queue.setError(e);
                return null;
            }
            return current;
        }
        else {
            List<TaskNode> tasks = new ArrayList<>(queue.getTasks());
            TaskNode[] nodes = new TaskNode[ahead];

            int ind = 0;
            for (int i = queue.currentTask + 1; i < queue.currentTask + ahead + 1; i++) {
                nodes[ind] = tasks.get(i);
                ind++;
            }
            try {
                for (int i = 0; i < iterations; i++) {
                    for (TaskNode node : nodes) {
                        current = node.execute(current, queue);
                    }
                }
                queue.currentTask += ahead;
                return current;
            }
            catch (RuntimeException e) {
                queue.setError(e);
                return null;
            }

        }
    }

    @Override
    public Class<LoopNode> identity() {
        return LoopNode.class;
    }
}
