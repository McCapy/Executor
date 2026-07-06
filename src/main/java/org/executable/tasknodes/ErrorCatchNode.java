package org.executable.tasknodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "ClassCanBeRecord"})
public class ErrorCatchNode implements TaskNode {
    final Consumer<RuntimeException> consumer;
    final Supplier<Object> supplier;
    public ErrorCatchNode() {
        consumer = null;
        this.supplier = () -> null;
    }
    public ErrorCatchNode(Runnable runnable, Object def) {
        this.consumer = (item) -> runnable.run();
        this.supplier = () -> def;
    }
    public ErrorCatchNode(Consumer<RuntimeException> consumer,Object def) {
        this.consumer = consumer;
        this.supplier = () -> def;
    }
    public ErrorCatchNode(Runnable runnable, Supplier<Object> def) {
        this.consumer = (item) -> runnable.run();
        this.supplier = () -> def;
    }
    public ErrorCatchNode(Consumer<RuntimeException> consumer,Supplier<Object> def) {
        this.consumer = consumer;
        this.supplier = () -> def;
    }
    public ErrorCatchNode(Runnable runnable) {
        this.consumer = (item) -> runnable.run();
        this.supplier = () -> null;
    }
    public ErrorCatchNode(Consumer<RuntimeException> consumer) {
        this.consumer = consumer;
        this.supplier = () -> null;
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
                IO.println("An error came from the consumer/runnable of an error catch node.");
                return null;
            }
            queue.resetError();
        }
        return supplier.get();
    }

    @Override
    public Class<ErrorCatchNode> identity() {
        return ErrorCatchNode.class;
    }
}
