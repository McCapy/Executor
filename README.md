# ExecutorAPI
> The ExecutorAPI is paired with another API called the TaskNodeAPI, both of these API's respectively enable the creation of unique TaskNode's to fit any situation where you might need them, in the scenario where an operation with no accompanying method or TaskNode does not exist this will help greatly.

## Understanding
To begin we must understand what the language inside the API means, we will go over EVERY method in the ExecutorAPI and the TaskNodeAPI respectively. Some methods will be considered `volatile` or `valuetasks` this mainly applies to TaskNodes which is where this language is most common, although to users that are NOT using the TaskNodeAPI this will just be simplified to "does this return something new?" and "does this throw an error that MUST be caught?"

Although there are some exceptions to this, some methods can be marked volatile (ie their use is unstable and **CAN** throw errors, if you happen to do something in a TaskNode that has the potential to throw an error, that automatically makes it a `volatile` task, meaning you should implicitly either, retry the task 
(explained later) **OR** do an errorCatch() and add a default value or just outright cancel the task IF there is an error (explained later as well)

Now for the actual documentation. We will start with the ExecutorAPI and move onto the TaskNodeAPI and its internals afterwards as it's quite hard to understand without the existence of the other TaskNodes and what they do to avoid pointless creation of a tasknode.

### ExecutorAPI
We will start with the creation of an Executor object. This is how you pass in `TaskNodes` among other operations.

- `new Executor()` **NON-VOLATILE, VALUETASK**
  > This is a blank executor which has an initial value of `null` meaning if you try to pass this value into an operation that is not a supplying task, then it        will likely throw.
- `new Executor(T initial)` & `new Executor(Supplier<T> initial)` **NON-VOLATILE, VALUETASK**
  > Both of these respectively initialize the Executor object with a given value at the start, so if you try to do an operation on this executor, then it will take
  > in the result of either the `supplier`, or the value `initial`

And that is all for initializations. Now we will move on to the status of an Executor.
*or if you're using the TaskNodeAPI the status of the `TaskQueue` ;)*

- `Executor#isStarted()` Checks if the executor has started.
- `Executor#isCompleted()` Checks if the executor has completed all tasks. This does not include forked tasks (unless gathered).
- `Executor#isCancelled()` Checks if the executor has been cancelled.

We will also cover how to transform these status's.

- `Executor#cancel()` **VOLATILE**
  > Cancels the executor. this halts the worker, as well as stops execution entirely. Any results which attempt to take from the cancelled executor will return       `null`

And that is all for status's. Now we will move on to operations. This is where we get the ability to change data, and execute tasks.

- `Executor#loop(Function<T,R> function,int iterations)` **VALUETASK**
  > This is a very basic operation. a simple loop, it loops a specific amount of times and executes the supplied `function` taking in the previous value it          returns a new value which is passed into the next function, if there is no next function (ie all iterations are complete) it will simply return the value to be    used in the next TaskNode
- `Executor#loop(int loops, int tasksAhead)` **VALUETASK**
  > This is very different from the previous loop. You will notice it does not take in a function, rather it takes in a `tasksAhead` integer, this loops from the    start which is defined as the .loop()'s location in the queue, and goes ahead and repeats all tasknodes in that bound `loops` times. Or until it reaches an        error.
- `Executor#doWhile(Predicate<T> predicate, int tasksAhead)` **VALUETASK**
  > This is quite a deal different, a `do while` is a type of loop that executes first, and then checks if the condition is true. So if the condition is false       during execution, it will still execute only once. It functions very similarly to the previous terminal operation due to the fact it operates based on the        `tasksAhead` integer.

Now before we discuss future operations we MUST discuss how to handle errors properly, and safely. You are given a set of methods ranging from catching errors and supplying values to replace the nulled value, as well as things to outright retry tasks which have failed.

**NOTE** I wont cover every method from this, just because there are like 8 of them. If you want look at the src code! They all behave self explanatorily.
The ones that i explain should be enough for your needs alone though. Just keep that in mind.

- `Executor#catchError()`
  > This just checks if an error is present, if so? Cancel the task. It should be appended directly after a *'risky'* task is executed. Such as one that throws an   error.
- `Executor#catchError(Runnable runnable)`
  > Whenever an error is caught, this runnable will run. Note this does have a variant with either a Supplier or an Object which can be passed in for the value      returned whenever an error is caught to replace the null value.
- `Executor#catchError(Consumer<RuntimeException> consumer)`
  > Whenever an error is caught, the consumer will consume the error. Note this does have a variant with either a Supplier or an Object which can be passed in for   the value returned whenever an error is caught to replace the null value.
- `Executor#catchError(Supplier<T> def)`
  > Replaces the null value from the error with the result of this supplier, can also be replaced with a `T def` instead.

Now, we're doing task retrying. This set of methods does not have variants with Supplier<R>, nor Runnable's, although they will be added eventually.

- `Executor#catchRetry(int max)`
  > This defines how many times a task can be retried before it fails, and returns null.
- `Executor#catchRetry(int max, R def)`
  > This defines how many times a task can be retried, when the task fails it will return `def` instead of null.
- `Executor#catchRetry(Consumer<RuntimeException> consumer, int max)`
  > This consumes the error (if any) and retries it `max` times.
- `Executor#catchRetry(Consumer<RuntimeException> consumer, int max, R def)`
  > This consumes the error, runs `max` times, after exceding `max` it will return `def` instead of null.

Now we've finally completed error handling, we can now move on to the actual operations of an Executor.
*wip*
