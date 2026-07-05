package org.executable.tasknodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class RetryNode implements TaskNode {
    final int retryCount;
    final Consumer<RuntimeException> consumer;
    final Object def;
    public RetryNode(int retryCount) {
        this.retryCount = retryCount;
        this.consumer = null;
        this.def = null;
    }
    public RetryNode(Consumer<RuntimeException> consumer, int retryCount) {
        this.consumer = consumer;
        this.retryCount = retryCount;
        this.def = null;
    }
    public RetryNode(Consumer<RuntimeException> consumer, int retryCount,Object def) {
        this.consumer = consumer;
        this.retryCount = retryCount;
        this.def = def;
    }
    public RetryNode(int retryCount, Object def) {
        this.retryCount = retryCount;
        this.def = def;
        this.consumer = null;
    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        if (queue.getError() != null) {
            queue.retryCount++;
            if (queue.retryCount >= retryCount) {
                queue.resetError();
                return def;
            }
            if (consumer != null) consumer.accept(queue.getError());
            queue.resetError();
            Object item = queue.errorItem;
            queue.errorItem = null;
            queue.currentTask -= 2;
            return item;
        }
        queue.retryCount = 0;
        return current;
    }

    @Override
    public Class<RetryNode> identity() {
        return RetryNode.class;
    }
}
