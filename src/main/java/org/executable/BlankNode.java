package org.executable;

import org.executable.annotations.SafeUsage;

@SuppressWarnings("unused")

@SafeUsage(
        "Does not throw any errors by default. Be weary when using."
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
