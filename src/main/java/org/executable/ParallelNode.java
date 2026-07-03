package org.executable;

import org.executable.annotations.ValueTask;
import org.executable.annotations.VolatileUsage;

@SuppressWarnings("unused")

@VolatileUsage(
        "Has the potential to throw unchecked " +
        "(or unreported) errors due to the " +
        "joining of foreign executors. These " +
        "errors will not be shown and should " +
        "be checked in the given executor which " +
        "has been passed in to prevent undefined " +
        "Behavior from arising. see .catchError()"
)
@ValueTask(
    "Returns a value by default which is in the form of an Object[], " +
    "this Object[] is a collection of all the results of the " +
    "executors (in the order of insertion) which were passed into the constructor."
)
public class ParallelNode implements TaskNode {
    final Executor<?>[] executors;

    public ParallelNode(Executor<?>... executors) {
        this.executors = executors;
    }

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
