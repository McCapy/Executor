package org.executable;

import org.executable.annotations.VolatileUsage;

import java.util.function.Predicate;

@SuppressWarnings("unused")
@VolatileUsage(
        "Possible InterruptedException " +
        "thrown when attempting to sleep."
)
public class DelayNode implements TaskNode {
    final long delay;
    final Predicate<Object> predicate;
    public DelayNode(long ms) {
        this.delay = ms;
        this.predicate = null;
    }
    public DelayNode(Predicate<Object> predicate, long ms) {
        this.predicate = predicate;
        this.delay = ms;
    }

    @Override
    public Object execute(Object current, TaskQueue queue) {
        if (predicate != null) {
            try {
                if (predicate.test(current)) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        queue.setError(new RuntimeException("Thread was Interrupted during sleep. " + e));
                        return null;
                    }
                }
            }
            catch (RuntimeException e) {
                queue.setError(e);
                return null;
            }
        }
        else {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException throwable) {
                Thread.currentThread().interrupt();
                queue.setError(new RuntimeException("Thread interrupted", throwable));
                return null;
            }
        }
        return current;
    }

    @Override
    public Class<DelayNode> identity() {
        return DelayNode.class;
    }
}
