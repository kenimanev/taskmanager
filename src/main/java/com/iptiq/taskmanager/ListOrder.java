package com.iptiq.taskmanager;

import java.util.Comparator;

import static java.util.Comparator.comparing;

/**
 * An enum helping callers to specify their sorting preferences in
 * {@link TaskManager#listProcesses(ListOrder)}.
 */
public enum ListOrder {
    BY_PID(comparing(Process::getPid)),
    BY_PRIORITY(comparing(Process::getPriority)),
    BY_TIME_STARTED(comparing(Process::getTimeStarted));

    private final Comparator<Process> comparator;

    ListOrder(Comparator<Process> comparator) {
        this.comparator = comparator;
    }

    /**
     * @return the corresponding {@link Comparator} object.
     */
    Comparator<Process> getComparator() {
        return comparator;
    }
}
