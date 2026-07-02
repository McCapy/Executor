package org.executable;

public interface TaskNode {
    Object execute(Object current, TaskQueue queue);
    default Class<?> identity() {
        return TaskNode.class;
    }
}
