package org.executable;

import org.executable.annotations.SafeUsage;
import org.executable.annotations.ValueTask;

import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("unused")

@SafeUsage("This node does not throw any errors by default.")
@ValueTask(
        "Has the potential of changing the cache, " +
        "although it can also preserve the caches value. " +
        "This entirely depends on the Predicate"
)
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
