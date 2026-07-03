package org.executable;

import org.executable.annotations.SafeUsage;
import org.executable.annotations.ValueTask;

import java.util.function.Supplier;

@SuppressWarnings("unused")

@ValueTask(
        "Acts as the initializer for the cache, " +
        "can also be null. It can also be used " +
        "to offer to, or set, the result"
)
@SafeUsage("Does not throw any errors by default.")
public class InitialNode implements TaskNode {
    final Supplier<?> item;
    public InitialNode(Object item) {
        this.item = () -> item;
    }
    public InitialNode(Supplier<?> supplier) {
        this.item = supplier;
    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        try {
            return item.get();
        } catch (RuntimeException throwable) {
            queue.setError(throwable);
            return null;
        }

    }
    @Override
    public Class<InitialNode> identity() {
        return InitialNode.class;
    }
}
