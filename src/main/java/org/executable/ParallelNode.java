package org.executable;

@SuppressWarnings("unused")
public class ParallelNode implements TaskNode {
    final Executor<?>[] executors;
    public ParallelNode(Executor<?>... executors) {
        this.executors = executors;
    }
    /*
    new Executor(input)
        .parallel(executors) // all executors are supplied with the given 'input' value
        .map(items -> {
            items.forEach(
            item -> {
                IO.println(item.toString());
            })
            return 5;
        });

     */
    @Override
    public Object execute(Object current, TaskQueue queue) {
        return
            new Executor<>(current)
                .map((item) -> {
                    Object[] results = new Object[executors.length];
                    for (Executor<?> executor : executors) {
                        executor.queue().setCurrent(item);
                        executor.start();
                    }
                    for (int i = 0, executorsLength = executors.length; i < executorsLength; i++) {
                        results[i] = executors[i].join();
                    }
                    return results;
                });
    }

    @Override
    public Class<ParallelNode> identity() {
        return ParallelNode.class;
    }
}
