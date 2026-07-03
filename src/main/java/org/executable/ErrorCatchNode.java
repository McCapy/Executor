package org.executable;

import org.executable.annotations.SafeUsage;
import org.executable.annotations.ValueTask;

import java.util.function.Consumer;

@SuppressWarnings("unused")
@ValueTask(
        "Empties the cache of the Executor, " +
        "which removes its value and executes " +
        "task. This task can take in a value, " +
        "or it can just execute a runnable."
)
@SafeUsage(
        "Does not throw any errors by default."
)
public class ErrorCatchNode implements TaskNode {
    final Consumer<Throwable> consumer;
    public ErrorCatchNode() {
        consumer = null;
    }
    public ErrorCatchNode(Runnable runnable) {
        this.consumer = (item) -> runnable.run();
    }
    public ErrorCatchNode(Consumer<Throwable> consumer) {
        this.consumer = consumer;
    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        if (queue.getError() != null) {
            if (consumer == null) {
                queue.cancel();
                return current;
            }
            try {
                consumer.accept(queue.getError());
            }
            catch (RuntimeException e) {
                IO.println(new RuntimeException("An error came from the consumer of an error catch node.").getMessage());
                return null;
            }
            queue.resetError();
        }
        return current;
    }

    @Override
    public Class<ErrorCatchNode> identity() {
        return ErrorCatchNode.class;
    }
}
