package com.muhammaddaffa.mdlib.task;

import com.muhammaddaffa.mdlib.task.handleTask.HandleTask;

import java.util.ArrayList;
import java.util.List;

public class TaskChain {

    private record Step(boolean async, long delayTicks, Runnable task) {
    }

    private final List<Step> steps = new ArrayList<>();
    private long cursor = 0;

    public static TaskChain create() {
        return new TaskChain();
    }

    public TaskChain sync(Runnable task) {
        steps.add(new Step(false, cursor, task));
        return this;
    }

    public TaskChain async(Runnable task) {
        steps.add(new Step(true, cursor, task));
        return this;
    }

    public TaskChain delay(long ticks) {
        cursor += ticks;
        return this;
    }

    public TaskChain syncLater(long ticks, Runnable task) {
        cursor += ticks;
        steps.add(new Step(false, cursor, task));
        return this;
    }

    public TaskChain asyncLater(long ticks, Runnable task) {
        cursor += ticks;
        steps.add(new Step(true, cursor, task));
        return this;
    }

    public List<HandleTask> execute() {
        List<HandleTask> handles = new ArrayList<>();
        ExecutorProvider provider = ExecutorManager.getProvider();
        for (Step step : steps) {
            HandleTask handle;
            if (step.delayTicks() == 0) {
                handle = step.async() ? provider.async(step.task()) : provider.sync(step.task());
            } else {
                handle = step.async()
                        ? provider.asyncLater(step.delayTicks(), step.task())
                        : provider.syncLater(step.delayTicks(), step.task());
            }
            handles.add(handle);
        }
        return handles;
    }
}