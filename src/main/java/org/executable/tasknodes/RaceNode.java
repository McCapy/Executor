package org.executable.tasknodes;

import org.executable.Executor;
import org.executable.TaskNode;
import org.executable.TaskQueue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class RaceNode implements TaskNode {
    final Executor<?>[] executors;
    final long delay;
    public RaceNode(Executor<?>... executors) {
        this.executors = executors;
        this.delay = 0;
    }
    public RaceNode(long ms,Executor<?>... executors) {
        this.executors = executors;
        this.delay = ms;
    }
    @Override
    public Object execute(Object current, TaskQueue queue) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger index = new AtomicInteger(0);
        for (int i = 0, executorsLength = executors.length; i < executorsLength; i++) {
            Executor<?> executor = executors[i];
            final int finalI = i;
            executor.execute(() -> {
                latch.countDown();
                index.set(finalI);
            }).start();
        }
        boolean success;
        try {
            success = latch.await(delay, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            queue.setError(new RuntimeException("Thread interrupted",e));
            return null;
        }
        if (!success) return null;
        int ind = index.get();
        Executor<?> completer = executors[ind];
        executors[ind] = null;
        for (Executor<?> executor : executors) {
            if (executor != null) {
                executor.cancel();
            }
        }
        return completer.join();
    }

    @Override
    public Class<RaceNode> identity() {
        return RaceNode.class;
    }
}
