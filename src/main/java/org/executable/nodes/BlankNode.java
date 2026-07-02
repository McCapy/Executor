package org.executable.nodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;
import org.executable.annotations.SafeUsage;

@SuppressWarnings("unused")

@SafeUsage(
        "Does not throw any errors by default."
)
public class BlankNode implements TaskNode {
    @Override
    public Object execute(Object current, TaskQueue queue) {
        return current;
    }
    @Override
    public Class<BlankNode> identity() {
        return BlankNode.class;
    }
}
