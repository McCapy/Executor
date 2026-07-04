package org.executable.tasknodes;

import org.executable.TaskNode;
import org.executable.TaskQueue;

@SuppressWarnings("unused")

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
