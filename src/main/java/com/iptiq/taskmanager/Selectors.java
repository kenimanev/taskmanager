package com.iptiq.taskmanager;

import java.util.function.Predicate;

/**
 * A collection of predicates, that can be used when calling {@link TaskManager#kill(Predicate)}.
 */
public class Selectors {

    /**
     * @return a predicate that matches all processes
     */
    public static Predicate<Process> all() {
        return process -> true;
    }

    /**
     * @return a predicate that matches a process with specific pid
     */
    public static Predicate<Process> byPID(final int pid) {
        return process -> process.getPid() == pid;
    }

    /**
     * @return a predicate that matches all processes with specific priority
     */
    public static Predicate<Process> byPriority(final int priority) {
        return process -> process.getPriority() == priority;
    }

}
