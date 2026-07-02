package org.executable;

import org.executable.nodes.ForkNode;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unused", "unchecked", "rawtypes"})
public final class TaskQueue {

    volatile Object result;
    volatile RuntimeException error = null;
    volatile boolean cancelled = false;
    volatile Runnable cancelEvent;
    final ArrayList<TaskNode> tasks = new ArrayList<>(2);
    final ArrayList<ForkNode> sideTasks = new ArrayList<>(2);

    final CountDownLatch started = new CountDownLatch(1);
    final CountDownLatch completed = new CountDownLatch(1);

    volatile Thread worker;



    public void resetError() {
        this.error = null;
    }

    public void addSideTask(ForkNode executor) {
        sideTasks.add(executor);
    }

    public void addSideTask(ForkNode... executors) {
        sideTasks.addAll(Arrays.asList(executors));
    }

    public void clearSideTasks() {
        sideTasks.clear();
    }

    public Collection<ForkNode> getSideTasks() {
        return Collections.unmodifiableCollection(sideTasks);
    }

    public Collection<TaskNode> getTasks() {
        return Collections.unmodifiableCollection(tasks);
    }

    public RuntimeException getError() {
        return error;
    }

    public <X> X getResult() {
        return cast(result);
    }

    public void setError(RuntimeException error) {
        if (this.error == null) {
            this.error = error;
        }
    }

    public void setCancelEvent(Runnable runnable) {
        this.cancelEvent = runnable;
    }

    public void addTask(TaskNode... nodes) {
        tasks.addAll(List.of(nodes));
    }

    public void addTask(long amt, TaskNode node) {
        for (long i = 0; i < amt; i++) {
            tasks.add(node);
        }
    }

    public void addTask(TaskNode node) {
        tasks.add(node);
    }

    public boolean isCompleted() {
        return completed.getCount() == 0;
    }

    public boolean isStarted() {
        return started.getCount() == 0;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public <X> X join() {
        if (!isStarted()) {
            start();
        }
        try {
            completed.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        return cancelled ? null : cast(result);
    }

    public <X> X join(long timeoutMs) {
        if (!isStarted()) {
            start();
        }
        try {
            if (!completed.await(timeoutMs, TimeUnit.MILLISECONDS)) {
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        return cancelled ? null : cast(result);
    }

    public void start() {
        if (isStarted()) {
            return;
        }
        worker = Thread.ofVirtual().start(this::run);
        started.countDown();
    }

    public void cancel() {
        if (!isStarted() || isCancelled() || isCompleted()) {
            return;
        }
        cancelled = true;
        if (worker != null) {
            worker.interrupt();
        }
        if (cancelEvent != null) {
            cancelEvent.run();
        }
        if (completed.getCount() > 0) {
            completed.countDown();
        }
        result = null;
        tasks.clear();
        sideTasks.clear();
    }

    void run() {
        Object current = null;
        for (TaskNode node : tasks) {
            if (cancelled) {
                if (cancelEvent != null) cancelEvent.run();
                break;
            }
            result = current = node.execute(current, this);
        }
        if (completed.getCount() > 0) {
            completed.countDown();
        }
    }

    <X> X cast(Object object) {
        return (X) object;
    }
}
