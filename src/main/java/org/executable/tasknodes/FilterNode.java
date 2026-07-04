package org.executable.tasknodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("unused")

public class FilterNode implements TaskNode {
    final Supplier<Object> def;
    final Predicate<Object> predicate;
    public FilterNode(Predicate<Object> predicate) {
        this.def = null;
        this.predicate = predicate;
    }
    public FilterNode(Predicate<Object> predicate, Object def) {
        this.def = () -> def;
        this.predicate = predicate;
    }
    public FilterNode(Predicate<Object> predicate, Supplier<Object> supplier) {
        this.def = supplier;
        this.predicate = predicate;
    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        try {
            return predicate.test(current) ? current : def;
        }
        catch (RuntimeException throwable) {
            queue.setError(throwable);
            return null;
        }
    }

    @Override
    public Class<FilterNode> identity() {
        return FilterNode.class;
    }
}
