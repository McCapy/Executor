package org.executable.annotations;

@SuppressWarnings("unused")
public @interface ValueTask {
    String value() default "No Specified Message.";
    String info() default
            "Shows a given Class/Node/Method returns a" +
            " value/values that is not the original value.";
}
