package org.executable.tasknodes;

import org.executable.Executor;
import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"unused", "rawtypes", "BooleanMethodIsAlwaysInverted"})

public class GatherNode implements TaskNode {

    final Function<Object[], Object> function;
    final Consumer<Object[]> consumer;

    public GatherNode() {
        this.function = null;
        this.consumer = null;
    }

    public GatherNode(Consumer<Object[]> consumer) {
        this.consumer = consumer;
        this.function = null;
    }

    public GatherNode(Function<Object[], Object> function) {
        this.function = function;
        this.consumer = null;
    }

    @Override
    public Object execute(Object current, TaskQueue queue) {
        List<Object> res = collectSideTasks(queue);
        if (res == null) {
            return current;
        }
        res.addFirst(current);
        Object[] gathered = res.toArray();

        if (consumer != null) {
            try {
                consumer.accept(gathered);
            } catch (RuntimeException e) {
                queue.setError(e);
                return null;
            }
            return current;
        }

        else if (function != null) {
            try {
                return function.apply(gathered);
            } catch (RuntimeException e) {
                queue.setError(e);
                return null;
            }
        }

        return current;
    }

    @Override
    public Class<GatherNode> identity() {
        return GatherNode.class;
    }

    private List<Object> collectSideTasks(TaskQueue rootQueue) {
        List<Object> results = new ArrayList<>();

        for (ForkNode fork : rootQueue.getSideTasks()) {
            if (!collectSideTasks(rootQueue, fork, results)) {
                return null;
            }
        }
        rootQueue.clearSideTasks();
        return results;
    }

    private boolean collectSideTasks(TaskQueue rootQueue, ForkNode fork, List<Object> results) {

        Executor<?> exec = fork.getTask();
        if (exec == null) {
            return true;
        }

        TaskQueue subQueue = exec.queue();

        for (ForkNode nested : subQueue.getSideTasks()) {
            if (!collectSideTasks(rootQueue, nested, results)) {
                return false;
            }
        }

        subQueue.clearSideTasks();

        Object res;
        try {
            res = exec.join();
        } catch (RuntimeException e) {
            rootQueue.setError(e);
            return false;
        }

        results.add(res);
        return true;
    }
}
