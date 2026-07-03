package org.executable;

import java.util.function.Function;

@SuppressWarnings("unused")
public class ForkIndependent implements TaskNode {
    final Function<Object, ? extends Executor<?>> function;

    public ForkIndependent(Executor<?> executor) {
        this.function = (item) -> executor;
    }

    public ForkIndependent(Function<Object, ? extends Executor<?>> function) {
        this.function = function;
    }

    @Override
    public Object execute(Object current, TaskQueue queue) {
        try {
            function.apply(current).start();
        } catch (RuntimeException e) {
            queue.setError(e);
            return null;
        }
        return current;
    }

    @Override
    public Class<ForkIndependent> identity() {
        return ForkIndependent.class;
    }
}
