package org.executable.tasknodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "ClassCanBeRecord"})
public class WhileNode implements TaskNode {
    final Predicate<Object> predicate;
    final int futureNodes;
    // dev note, the .while() nodes index (i) is the reference point,
    // we go from i+1 -> futureNodes and will continue doing so while the predicate is true.
    // the predicate will take in the current object
    public WhileNode(Predicate<Object> predicate, int futureNodes) {
        this.predicate = predicate;
        this.futureNodes = futureNodes;
    }

    @Override
    public Object execute(Object current, TaskQueue queue) {
        try {
            List<TaskNode> tasks = new ArrayList<>(queue.getTasks());
            TaskNode[] nodes = new TaskNode[futureNodes];

            int ind = 0;
            for (int i = queue.currentTask + 1; i < queue.currentTask + futureNodes + 1; i++) {
                nodes[ind] = tasks.get(i);
                ind++;
            }
            boolean stopped = false;
            while (true) {
                for (TaskNode node : nodes) {
                    current = node.execute(current, queue);
                    stopped = predicate.test(current);
                }
                if (stopped) break;
            }
            queue.currentTask += futureNodes;
            return current;
        }
        catch (RuntimeException e) {
            queue.setError(e);
            return null;
        }
    }


    @Override
    public Class<WhileNode> identity() {
        return WhileNode.class;
    }
}
