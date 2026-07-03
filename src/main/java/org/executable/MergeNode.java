package org.executable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "rawtypes", "BooleanMethodIsAlwaysInverted"})


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
