package org.executable.annotations;

@SuppressWarnings("unused")
public @interface SafeUsage {
    String value() default "No Reason Given.";
    String info() default
            "Indicates that a given Method/Class/Node is safe to use," +
            " i.e. It does not throw an error by default, although " +
            "this behavior can be changed depending on the code put into it.";
}
