package org.executable.annotations;

@SuppressWarnings("unused")
public @interface VolatileUsage {
    String value() default "No reason specified.";
    String info() default
            "Shows that a Method/Class/Node is 'volatile' or unsafe," +
            " ie it could throw an exception, you should use the" +
            " Executor.catchError(Consumer/Runnable) to catch errors.";
}
